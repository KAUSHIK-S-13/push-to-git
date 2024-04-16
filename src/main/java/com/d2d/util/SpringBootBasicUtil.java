package com.d2d.util;

import com.d2d.constant.Constant;
import com.d2d.dto.Column;
import com.d2d.dto.DbDesignTables;
import com.d2d.dto.Table;
import com.d2d.exception.CustomValidationException;
import com.d2d.exception.ErrorCode;
import com.d2d.generator.DbDesignExcelGenerator;
import com.d2d.repository.DataBaseEngineRepository;
import com.d2d.repository.DataTypeRepository;
import com.d2d.repository.MappingRepository;
import com.d2d.serviceImpl.D2DServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Component
public class SpringBootBasicUtil {

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Autowired
    DataTypeRepository dataTypeRepository;

    @Autowired
    DataBaseEngineRepository dataBaseEngineRepository;

    @Autowired
    CommonUtil commonUtil;

    @Autowired
    EmailUtil emailUtil;

    @Autowired
    ClassCreatorUtil classCreatorUtil;

    @Autowired
    CrudUtil crudUtil;

    @Autowired
    MappingRepository mappingRepository;

    @Autowired
    JwtCreatorVThreeUtil jwtCreatorVThreeUtil;

    @Autowired
    JwtCreatorVTwoUtil jwtCreatorVTwoUtil;
    @Autowired
    OauthCreatorUtil oauthCreatorUtil;
    @Autowired
    KeyCloakCreatorUtil keyCloakCreatorUtil;
    @Autowired
    UnitTestCreatorUtil unitTestCreatorUtil;

    @Autowired
    ProjectUtil projectUtil;

    private static final byte[] BUFFER = new byte[4096 * 1024];
    private static final Logger logger = LoggerFactory.getLogger(D2DServiceImpl.class);
    private final RestTemplate restTemplate;

    public SpringBootBasicUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<Resource> getResourceResponseEntity(DbDesignTables tables) throws Exception {
        if (tables.getEnableSecurityDTO().getIsSecurityRequired()){
            ByteArrayResource downloadExcelFile = DbDesignExcelGenerator.downloadDBDesign(tables.getTableList());
            ByteArrayResource downloadTextFile = null;
            if (tables.getDatabaseEngineId() == 1) {
                downloadTextFile = DbDesignExcelGenerator.queryGenerator(tables.getTableList(), tables);
            } else if (tables.getDatabaseEngineId() == 4) {
                downloadTextFile = DbDesignExcelGenerator.postGreSQLQueryGenerator(tables.getTableList(), tables);
            } else if (tables.getDatabaseEngineId() == 3) {
                downloadTextFile = DbDesignExcelGenerator.oracleQueryGenerator(tables.getTableList(), tables);
            } else if (tables.getDatabaseEngineId() == 5) {
                downloadTextFile = DbDesignExcelGenerator.msSqlQueryGenerator(tables.getTableList(), tables);
            }
            Path dbDesignAndScriptZip = Files.createTempFile("documents", ".zip");

            writeToZip(dbDesignAndScriptZip, tables, downloadTextFile, downloadExcelFile);


            Path projectBaseSetUpZip = Files.createTempFile("projectBaseSetUp", ".zip");
            ZipOutputStream updatedZipFile = new ZipOutputStream(new FileOutputStream(projectBaseSetUpZip.toFile()));
            zipFileCreation(tables, updatedZipFile);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);


            ZipEntry dbDesignAndScriptZipEntry = new ZipEntry("documents.zip");
            zipOutputStream.putNextEntry(dbDesignAndScriptZipEntry);
            zipOutputStream.write(Files.readAllBytes(dbDesignAndScriptZip.toAbsolutePath()));
            zipOutputStream.closeEntry();



            ZipEntry projectBaseSetUpZipEntry = new ZipEntry(tables.getSpringBootBasicDTO().getArtifactId() + ".zip");
            zipOutputStream.putNextEntry(projectBaseSetUpZipEntry);
            zipOutputStream.write(Files.readAllBytes(projectBaseSetUpZip.toAbsolutePath()));
            zipOutputStream.closeEntry();

            zipOutputStream.close();

            Files.deleteIfExists(projectBaseSetUpZip);
            Files.deleteIfExists(dbDesignAndScriptZip);

            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MMM_yyyy_HH.mm");
            String formattedDateTime = currentDateTime.format(formatter);
            String zipName = tables.getSpringBootBasicDTO().getArtifactId() + "_" + formattedDateTime + ".zip";

            projectUtil.encryptJson(tables);

            HttpHeaders headers = new HttpHeaders();
            ByteArrayResource zipResource = new ByteArrayResource(outputStream.toByteArray());
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipName);
            headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
            return new ResponseEntity<>(zipResource, headers, HttpStatus.OK);
        }
        else {
            Path projectBaseSetUpZip = Files.createTempFile("projectBaseSetUp", ".zip");
            ZipOutputStream updatedZipFile = new ZipOutputStream(new FileOutputStream(projectBaseSetUpZip.toFile()));
            zipFileCreation(tables, updatedZipFile);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);


            ZipEntry projectBaseSetUpZipEntry = new ZipEntry(tables.getSpringBootBasicDTO().getArtifactId() + ".zip");
            zipOutputStream.putNextEntry(projectBaseSetUpZipEntry);
            zipOutputStream.write(Files.readAllBytes(projectBaseSetUpZip.toAbsolutePath()));
            zipOutputStream.closeEntry();

            zipOutputStream.close();

            Files.deleteIfExists(projectBaseSetUpZip);

            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MMM_yyyy_HH.mm");
            String formattedDateTime = currentDateTime.format(formatter);
            String zipName = tables.getSpringBootBasicDTO().getArtifactId() + "_" + formattedDateTime + ".zip";
            projectUtil.encryptJson(tables);

            HttpHeaders headers = new HttpHeaders();
            ByteArrayResource zipResource = new ByteArrayResource(outputStream.toByteArray());
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipName);
            headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
            return new ResponseEntity<>(zipResource, headers, HttpStatus.OK);

        }
    }

    public void zipFileCreation(DbDesignTables tables, ZipOutputStream updatedZipFile) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonValueForDbDesignTables = objectMapper.writeValueAsString(tables);
        try {
            if (classCreatorUtil.isValidSpringDetails(tables)) {
                throw new CustomValidationException(ErrorCode.D2D_6);
            }
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = Constant.STARTER_ZIP;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("?type=");
            stringBuilder.append(tables.getSpringBootBasicDTO().getType());
            stringBuilder.append("&language=");
            stringBuilder.append(tables.getSpringBootBasicDTO().getLanguage());
            stringBuilder.append("&bootVersion=");
            stringBuilder.append(tables.getSpringBootBasicDTO().getBootVersion());
            stringBuilder.append("&baseDir=");
            stringBuilder.append(tables.getSpringBootBasicDTO().getBaseDir());
            stringBuilder.append("&groupId=");
            stringBuilder.append(tables.getSpringBootBasicDTO().getGroupId());
            stringBuilder.append("&artifactId=");
            stringBuilder.append(tables.getSpringBootBasicDTO().getArtifactId());
            stringBuilder.append("&name=");
            stringBuilder.append(tables.getSpringBootBasicDTO().getName());
            stringBuilder.append("&description=");
            stringBuilder.append(tables.getSpringBootBasicDTO().getDescription());
            stringBuilder.append("&packageName=");
            stringBuilder.append(tables.getSpringBootBasicDTO().getPackageName());
            stringBuilder.append("&packaging=");
            stringBuilder.append(tables.getSpringBootBasicDTO().getPackaging());
            stringBuilder.append("&javaVersion=");
            stringBuilder.append(tables.getSpringBootBasicDTO().getJavaVersion());
            stringBuilder.append("&dependencies=");
            stringBuilder.append(tables.getSpringBootBasicDTO().getDependencies());
            apiUrl = apiUrl + stringBuilder.toString();

            String artifactName = tables.getSpringBootBasicDTO().getArtifactId();
            String projectNameInCamelCase = tables.getSpringBootBasicDTO().getName().substring(0, 1).toUpperCase() +
                    tables.getSpringBootBasicDTO().getName().substring(1);
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<byte[]> restTemplateResponse = restTemplate.exchange(apiUrl, HttpMethod.GET, request, byte[].class);
            Path pomFilePath = null;
            Path mainApplicationFilePath = null;
            Path mainApplicationFilePath1 = null;
            if (restTemplateResponse.hasBody() && restTemplateResponse.getStatusCode().value() == 200) {
                byte[] responseBody = restTemplateResponse.getBody();
                Path tempZipFile = Files.createTempFile(tables.getSpringBootBasicDTO().getArtifactId(), ".zip");
                FileOutputStream fileOutputStream = new FileOutputStream(tempZipFile.toFile());
                fileOutputStream.write(responseBody);
                fileOutputStream.close();
                try (ZipFile oldZipFile = new ZipFile(tempZipFile.toFile().getAbsolutePath())) {
                    Enumeration<? extends ZipEntry> entries = oldZipFile.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry e = entries.nextElement();
                        if (!e.getName().equalsIgnoreCase(artifactName + "/src/main/resources/application.properties") &&
                                (!e.getName().equalsIgnoreCase(artifactName + "/pom.xml")) &&
                                (!e.getName().equalsIgnoreCase(artifactName + Constant.SRC_MAIN_JAVA + tables.getSpringBootBasicDTO().getPackageName().replaceAll("\\.", "/")
                                        + "/" + projectNameInCamelCase + Constant.APPLICATION_JAVA)) && (!e.getName().equalsIgnoreCase(artifactName + Constant.SRC_TEST_JAVA + tables.getSpringBootBasicDTO().getPackageName().replaceAll("\\.", "/") + "/" + projectNameInCamelCase + Constant.SRC_TESTS_JAVA))) {
                            updatedZipFile.putNextEntry(e);
                            if (!e.isDirectory()) {
                                copy(oldZipFile.getInputStream(e), updatedZipFile);
                            }
                            updatedZipFile.closeEntry();
                        } else {
                            if (e.getName().equalsIgnoreCase(artifactName + "/pom.xml")) {
                                InputStream inputStream = oldZipFile.getInputStream(e);
                                pomFilePath = Files.createTempFile("pom", ".xml");
                                logger.info("path {}", pomFilePath);
                                try(FileOutputStream fileOutputStreams = new FileOutputStream(pomFilePath.toFile())) {
                                    fileOutputStreams.write(inputStream.readAllBytes());
                                } catch(IOException i) {
                                    logger.info("==========zipFileCreation============");
                                }

                            }
                            if (e.getName().equalsIgnoreCase(artifactName + "/src/main/java/" + tables.getSpringBootBasicDTO().getPackageName().replaceAll("\\.", "/")
                                    + "/" + projectNameInCamelCase + Constant.APPLICATION_JAVA)) {
                                InputStream mainApplicationFileInputStream = oldZipFile.getInputStream(e);
                                mainApplicationFilePath = Files.createTempFile("MainApplicationFile", Constant.DOT_JAVA);
                                try (FileOutputStream fileOutputStream1 = new FileOutputStream(mainApplicationFilePath.toFile())){
                                    fileOutputStream1.write(mainApplicationFileInputStream.readAllBytes());
                                }catch (IOException i){
                                    logger.info("===============zipFileCreation===========",i);
                                }

                            }
                            /*if (e.getName().equalsIgnoreCase(artifactName + Constant.SRC_TEST_JAVA + tables.getSpringBootBasicDTO().getPackageName().replaceAll("\\.", "/") + "/" + projectNameInCamelCase + Constant.SRC_TESTS_JAVA)) {
                                InputStream mainApplicationFileInputStream1 = oldZipFile.getInputStream(e);
                                mainApplicationFilePath1 = Files.createTempFile("MainApplicationFiles", Constant.DOT_JAVA);
                                try (FileOutputStream fileOutputStream1 = new FileOutputStream(mainApplicationFilePath1.toFile())){
                                    fileOutputStream1.write(mainApplicationFileInputStream1.readAllBytes());
                                }catch (IOException i){
                                    logger.info("******************zipFileCreation*********");
                                }
                            }*/
                        }
                    }
                    oldZipFile.close();
                } catch (IOException e) {
                    logger.info("********zipFileCreation*******", e);
                }
                Files.deleteIfExists(tempZipFile.toAbsolutePath());
            }

            ZipEntry applicationPropertyFileEntry = new ZipEntry(artifactName + "\\src\\main\\resources\\application.properties");
            String applicationFileString = classCreatorUtil.applicationPropertiesString(tables);
            updatedZipFile.putNextEntry(applicationPropertyFileEntry);
            updatedZipFile.write(applicationFileString.getBytes(StandardCharsets.UTF_8));
            updatedZipFile.closeEntry();

            ZipEntry pomFileEntry1 = new ZipEntry(artifactName + "\\pom.xml");
            updatedZipFile.putNextEntry(pomFileEntry1);
            List<String> lines = Files.readAllLines(pomFilePath);
            String applicationFileString1 = classCreatorUtil.addDependencysString(tables);
            String keyCloakManagementString = classCreatorUtil.addDependencysKeyCloakManagementString(tables);
            int index = lines.indexOf("</dependency>") + 20;
            if (tables.getEnableSecurityDTO().getIsSecurityRequired() && tables.getEnableSecurityDTO().getSecurityMethod() == 3) {
                lines.add(44, keyCloakManagementString);
            }
            lines.add(index, applicationFileString1);
            updatedZipFile.write(lines.stream().collect(Collectors.joining(System.lineSeparator())).getBytes());
            updatedZipFile.closeEntry();

            ZipEntry mainApplicationFileZipEntry = new ZipEntry(artifactName + Constant.SRC_MAIN_JAVA + tables.getSpringBootBasicDTO().getPackageName().replaceAll("\\.", "/")
                    + "/" + projectNameInCamelCase + Constant.APPLICATION_JAVA);
            updatedZipFile.putNextEntry(mainApplicationFileZipEntry);
            List<String> mainFileInString = Files.readAllLines(mainApplicationFilePath);
            String modelMapperBeanClassCreation = classCreatorUtil.modelMapperBeanClassCreation();
            int getimportindex = mainFileInString.indexOf("@SpringBootApplication");
            mainFileInString.add(getimportindex, "import org.springframework.context.annotation.Bean;\nimport org.modelmapper.ModelMapper;\n");
            int getIndex = mainFileInString.indexOf(Constant.PUBLIC_CLASS + projectNameInCamelCase + "Application {") + 1;
            mainFileInString.add(getIndex, modelMapperBeanClassCreation);

            updatedZipFile.write(mainFileInString.stream().collect(Collectors.joining(System.lineSeparator())).getBytes(StandardCharsets.UTF_8));
            updatedZipFile.closeEntry();

            if (tables.getTableList()!=null && !tables.getTableList().isEmpty()){
            if (!Objects.equals(tables.getTableList().get(0).getName(), "user")){
            ZipEntry mainApplicationFileZipEntry1 = new ZipEntry(artifactName + Constant.SRC_TEST_JAVA + tables.getSpringBootBasicDTO().getPackageName().replaceAll("\\.", "/") + "/" + projectNameInCamelCase + Constant.SRC_TESTS_JAVA);
            updatedZipFile.putNextEntry(mainApplicationFileZipEntry1);

            String tableClassName1 = null;
            String tableClassName2 = null;

            String modelMapperBeanClassCreation1 = null;
            String modelMapperBeanClassCreation2 = null;
            String modelMapperBeanClassCreation3 = null;


                    List<String> mainFileInString1 = Files.readAllLines(mainApplicationFilePath1);
                    for (Table table : tables.getTableList()) {
                        if (table.getName() == null || table.getName().isEmpty()) {
                            throw new CustomValidationException(ErrorCode.D2D_7);
                        }
                        tableClassName1 = commonUtil.CapitalizeClassName(table.getName());
                        Boolean isDeletedPresent = false;
                        if (table.getColumns().stream().filter(column -> column.getName().equalsIgnoreCase(Constant.DELETED_FLAG)).collect(Collectors.toList()).size() > 0) {
                            isDeletedPresent = true;
                        }
                        modelMapperBeanClassCreation1 = unitTestCreatorUtil.mockitoClassCreation(tableClassName1);

                        int getIndex1 = mainFileInString1.indexOf(Constant.PUBLIC_CLASS + projectNameInCamelCase + Constant.APPLICATION_TESTS) + 8;
                        mainFileInString1.add(getIndex1, modelMapperBeanClassCreation1);
                    }


                    //CurdDetails
                    for (Table table : tables.getTableList()) {
                        if (table.getName() == null || table.getName().isEmpty()) {
                            throw new CustomValidationException(ErrorCode.D2D_7);
                        }
                        tableClassName1 = commonUtil.CapitalizeClassName(table.getName());
                        Boolean isDeletedPresent = false;
                        if (table.getColumns().stream().filter(column -> column.getName().equalsIgnoreCase(Constant.DELETED_FLAG)).collect(Collectors.toList()).isEmpty()) {
                            isDeletedPresent = true;
                        }
                        int getIndex1 = mainFileInString1.indexOf(Constant.PUBLIC_CLASS + projectNameInCamelCase + Constant.APPLICATION_TESTS) + 10;
                        modelMapperBeanClassCreation2 = unitTestCreatorUtil.curdUnitTestCaseCreation(table, tableClassName1, table.getColumns());
                        mainFileInString1.add(getIndex1, modelMapperBeanClassCreation2);

                    }

                    for (Table table : tables.getTableList()) {
                        if (table.getName() == null || table.getName().isEmpty()) {
                            throw new CustomValidationException(ErrorCode.D2D_7);
                        }
                        tableClassName1 = commonUtil.CapitalizeClassName(table.getName());
                        Boolean isDeletedPresent = false;
                        if (table.getColumns().stream().filter(column -> column.getName().equalsIgnoreCase(Constant.DELETED_FLAG)).collect(Collectors.toList()).size() > 0) {
                            isDeletedPresent = true;
                        }
                        int getIndex1 = mainFileInString1.lastIndexOf("}");
                        modelMapperBeanClassCreation2 = unitTestCreatorUtil.curdUnitTestCommonData(table, tableClassName1, table.getColumns());
                        mainFileInString1.add(getIndex1, modelMapperBeanClassCreation2);
                    }

                    int getIndex1 = mainFileInString1.indexOf(Constant.PUBLIC_CLASS + projectNameInCamelCase + Constant.APPLICATION_TESTS) + 8;

                    modelMapperBeanClassCreation3 = unitTestCreatorUtil.commanModel();
                    mainFileInString1.add(getIndex1, modelMapperBeanClassCreation3);
                    for (Table table : tables.getTableList()) {
                        if (table.getName() == null || table.getName().isEmpty()) {

                            throw new CustomValidationException(ErrorCode.D2D_7);

                        }
                        tableClassName2 = commonUtil.CapitalizeClassName(table.getName());

                        int getimportindex1 = mainFileInString1.indexOf(Constant.SPRING_BOOT_TEST);
                        mainFileInString1.add(getimportindex1, unitTestCreatorUtil.uniTestImportsCreation(tables.getSpringBootBasicDTO().getPackageName(), tableClassName2, table.getCrudDTO()));

                    }
                    int getimportindex2 = mainFileInString1.indexOf(Constant.SPRING_BOOT_TEST);
                    mainFileInString1.add(getimportindex2, unitTestCreatorUtil.commanImport());

                    int getimportindex1 = mainFileInString1.indexOf(Constant.SPRING_BOOT_TEST);
                    mainFileInString1.add(getimportindex1, "import org.mockito.InjectMocks;\nimport org.mockito.Mock;\n");


                    updatedZipFile.write(mainFileInString1.stream().collect(Collectors.joining(System.lineSeparator())).getBytes(StandardCharsets.UTF_8));
                }
                updatedZipFile.closeEntry();

            String commonPath = "\\src\\main\\java\\";
            String packageName = tables.getSpringBootBasicDTO().getPackageName();
            String[] packageNameArray = packageName.split("\\.");
            String modelPackageName = "model\\";
            String repositoryPackageName = "repository\\";
            String dTOPackageName = "dto\\";
            String exceptionPackageName = "exception\\";
            String swaggerConfig = "config\\";
            String controllerPackageName = "controller\\";
            String responsePackage = "response\\";
            String serviceInterfacePackageName = "service\\";
            String serviceImplPackageName = "serviceimpl\\";
            String securityPackageName = "securityconfig\\";
            String constantPackageName = "constant\\";
            String oauthPackageName = "oauthconfig\\";
            String keyCloakConfig = "keycloakconfig\\";

            packageName = "";

            for (int i = 0; i < packageNameArray.length; i++) {
                packageName = packageName + packageNameArray[i] + "\\";
            }

            String finalPath = artifactName + commonPath + packageName;
            Integer databaseEngineId = tables.getDatabaseEngineId();


            ZipEntry exceptions = new ZipEntry(finalPath + exceptionPackageName + "ApiError.java");
            updatedZipFile.putNextEntry(exceptions);
            String zipErrorClass = classCreatorUtil.apiErrorClass(tables.getSpringBootBasicDTO().getPackageName());
            updatedZipFile.write(zipErrorClass.getBytes());

            ZipEntry responseEntityBuilder = new ZipEntry(finalPath + exceptionPackageName + "ResponseEntityBuilder.java");
            updatedZipFile.putNextEntry(responseEntityBuilder);
            String ResponseEntityBuilderClass = classCreatorUtil.ResponseEntityBuilder(tables.getSpringBootBasicDTO().getPackageName());
            updatedZipFile.write(ResponseEntityBuilderClass.getBytes());

            ZipEntry customExceptionHandler = new ZipEntry(finalPath + exceptionPackageName + "CustomExceptionHandler.java");
            updatedZipFile.putNextEntry(customExceptionHandler);
            String customExceptionHandlerClass = classCreatorUtil.CustomExceptionHandler(tables.getSpringBootBasicDTO().getPackageName());
            updatedZipFile.write(customExceptionHandlerClass.getBytes());

            ZipEntry customValidationException = new ZipEntry(finalPath + exceptionPackageName + "CustomValidationException.java");
            updatedZipFile.putNextEntry(customValidationException);
            String customValidationExceptionHandlerClass = classCreatorUtil.customValidationException(tables.getSpringBootBasicDTO().getPackageName());
            updatedZipFile.write(customValidationExceptionHandlerClass.getBytes());


            if (tables.isSwagger() == true) {
                ZipEntry swaggerConfiguration = new ZipEntry(finalPath + swaggerConfig + "SwaggerConfig.java");
                updatedZipFile.putNextEntry(swaggerConfiguration);
                String swaggerConfigClass = classCreatorUtil.SwaggerConfig(tables.getSpringBootBasicDTO().getPackageName(), tables.getSpringBootBasicDTO().getArtifactId());
                updatedZipFile.write(swaggerConfigClass.getBytes());
            }

            String successResponseString = crudUtil.successResponseClassCreation(tables.getSpringBootBasicDTO().getPackageName());
            ZipEntry successResponseClass = new ZipEntry(finalPath + responsePackage + "SuccessResponse.java");
            updatedZipFile.putNextEntry(successResponseClass);
            updatedZipFile.write(successResponseString.getBytes(StandardCharsets.UTF_8));
            updatedZipFile.closeEntry();

            String pageResponseString = crudUtil.pageResponseClassCreation(tables.getSpringBootBasicDTO().getPackageName());
            ZipEntry pageResponseClass = new ZipEntry(finalPath + responsePackage + "PageResponse.java");
            updatedZipFile.putNextEntry(pageResponseClass);
            updatedZipFile.write(pageResponseString.getBytes(StandardCharsets.UTF_8));
            updatedZipFile.closeEntry();

            String swaggerResponseClass = crudUtil.swaggerConstant(tables.getSpringBootBasicDTO().getPackageName(),artifactName);
            ZipEntry swaggerResponse = new ZipEntry(finalPath + responsePackage + "SwaggerConstant.java");
            updatedZipFile.putNextEntry(swaggerResponse);
            updatedZipFile.write(swaggerResponseClass.getBytes(StandardCharsets.UTF_8));
            updatedZipFile.closeEntry();



            if (tables.getEnableSecurityDTO().getIsSecurityRequired() && tables.getEnableSecurityDTO().getSecurityMethod() == 1) {
                if (tables.getSpringBootBasicDTO().getBootVersion().startsWith("3")) {
                    String webSecurityConfig = jwtCreatorVThreeUtil.webSecurityConfigAdd(tables);
                    ZipEntry webSecurityConfigZip = new ZipEntry(finalPath + securityPackageName + "WebSecurityConfig.java");
                    updatedZipFile.putNextEntry(webSecurityConfigZip);
                    updatedZipFile.write(webSecurityConfig.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String jwtUserDetailService = jwtCreatorVThreeUtil.jwtUserDetailServiceAdd(tables);
                    ZipEntry jwtUserDetailServiceZip = new ZipEntry(finalPath + securityPackageName + "JwtUserDetailsService.java");
                    updatedZipFile.putNextEntry(jwtUserDetailServiceZip);
                    updatedZipFile.write(jwtUserDetailService.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String jwtRequestFilterService = jwtCreatorVThreeUtil.jwtRequestFilterServiceAdd(tables);
                    ZipEntry jwtRequestFilterServiceZip = new ZipEntry(finalPath + securityPackageName + "JwtRequestFilter.java");
                    updatedZipFile.putNextEntry(jwtRequestFilterServiceZip);
                    updatedZipFile.write(jwtRequestFilterService.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String jwtAuthenticationEntryPointService = jwtCreatorVThreeUtil.jwtAuthenticationEntryPointAdd(tables);
                    ZipEntry jwtAuthenticationEntryPointZip = new ZipEntry(finalPath + securityPackageName + "JwtAuthenticationEntryPoint.java");
                    updatedZipFile.putNextEntry(jwtAuthenticationEntryPointZip);
                    updatedZipFile.write(jwtAuthenticationEntryPointService.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String loginRequestAdds = jwtCreatorVThreeUtil.loginRequestAdd(tables);
                    ZipEntry loginRequestAddZip = new ZipEntry(finalPath + responsePackage + "LoginRequest.java");
                    updatedZipFile.putNextEntry(loginRequestAddZip);
                    updatedZipFile.write(loginRequestAdds.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String authResponseAdd = jwtCreatorVThreeUtil.authResponseAdd(tables.getSpringBootBasicDTO().getPackageName());
                    ZipEntry authResponseAddZip = new ZipEntry(finalPath + responsePackage + "AuthResponse.java");
                    updatedZipFile.putNextEntry(authResponseAddZip);
                    updatedZipFile.write(authResponseAdd.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String loginResponseAdd = jwtCreatorVThreeUtil.loginResponseAdd(tables.getSpringBootBasicDTO().getPackageName(), tables.getEnableSecurityDTO().getAuthTableName());
                    ZipEntry loginResponseAddZip = new ZipEntry(finalPath + responsePackage + "LoginResponse.java");
                    updatedZipFile.putNextEntry(loginResponseAddZip);
                    updatedZipFile.write(loginResponseAdd.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String jwtUtilAdd = jwtCreatorVThreeUtil.jwtUtilAdd(tables);
                    ZipEntry jwtUtilAddZip = new ZipEntry(finalPath + securityPackageName + "JWTUtils.java");
                    updatedZipFile.putNextEntry(jwtUtilAddZip);
                    updatedZipFile.write(jwtUtilAdd.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String authCreateControllerClass = jwtCreatorVThreeUtil.createControllerClass(tables);
                    ZipEntry authControllerClass = new ZipEntry(finalPath + controllerPackageName + "AuthController.java");
                    updatedZipFile.putNextEntry(authControllerClass);
                    updatedZipFile.write(authCreateControllerClass.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String authCreateServiceClass = jwtCreatorVThreeUtil.createServiceClass(tables);
                    ZipEntry authServiceClass = new ZipEntry(finalPath + serviceInterfacePackageName + "AuthService.java");
                    updatedZipFile.putNextEntry(authServiceClass);
                    updatedZipFile.write(authCreateServiceClass.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String authCreateServiceImplClass = jwtCreatorVThreeUtil.createServiceImplClass(tables);
                    ZipEntry authServiceImplClass = new ZipEntry(finalPath + serviceImplPackageName + "AuthServiceImpl.java");
                    updatedZipFile.putNextEntry(authServiceImplClass);
                    updatedZipFile.write(authCreateServiceImplClass.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();
                } else {
                    ZipEntry constant = new ZipEntry(finalPath + constantPackageName + "Constant.java");
                    updatedZipFile.putNextEntry(constant);
                    String constantClass = jwtCreatorVTwoUtil.ConstantSecondVersions(tables.getSpringBootBasicDTO().getPackageName());
                    updatedZipFile.write(constantClass.getBytes());

                    String authCreateControllerClass = jwtCreatorVTwoUtil.createControllerClassSecondVersions(tables);
                    ZipEntry authControllerClass = new ZipEntry(finalPath + controllerPackageName + "AuthController.java");
                    updatedZipFile.putNextEntry(authControllerClass);
                    updatedZipFile.write(authCreateControllerClass.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String authCreateServiceClass = jwtCreatorVTwoUtil.createServiceClassSecondVersions(tables);
                    ZipEntry authServiceClass = new ZipEntry(finalPath + serviceInterfacePackageName + "AuthService.java");
                    updatedZipFile.putNextEntry(authServiceClass);
                    updatedZipFile.write(authCreateServiceClass.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String jwtUtilAdd = jwtCreatorVTwoUtil.jwtUtilAddSecondVersions(tables);
                    ZipEntry jwtUtilAddZip = new ZipEntry(finalPath + securityPackageName + "JWTUtils.java");
                    updatedZipFile.putNextEntry(jwtUtilAddZip);
                    updatedZipFile.write(jwtUtilAdd.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String loginResponseAdd = jwtCreatorVTwoUtil.loginResponseAddSecondVersions(tables.getSpringBootBasicDTO().getPackageName(), tables.getEnableSecurityDTO().getAuthTableName());
                    ZipEntry loginResponseAddZip = new ZipEntry(finalPath + responsePackage + "LoginResponse.java");
                    updatedZipFile.putNextEntry(loginResponseAddZip);
                    updatedZipFile.write(loginResponseAdd.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String LoginRequestAdd = jwtCreatorVTwoUtil.loginRequestAddSecondVersions(tables);
                    ZipEntry loginRequestAddZip = new ZipEntry(finalPath + responsePackage + "LoginRequest.java");
                    updatedZipFile.putNextEntry(loginRequestAddZip);
                    updatedZipFile.write(LoginRequestAdd.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String authResponseAdd = jwtCreatorVTwoUtil.authResponseAddSecondVersions(tables.getSpringBootBasicDTO().getPackageName());
                    ZipEntry authResponseAddZip = new ZipEntry(finalPath + responsePackage + "AuthResponse.java");
                    updatedZipFile.putNextEntry(authResponseAddZip);
                    updatedZipFile.write(authResponseAdd.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String authCreateServiceImplClass = jwtCreatorVTwoUtil.createServiceImplClassSecondVersions(tables);
                    ZipEntry authServiceImplClass = new ZipEntry(finalPath + serviceImplPackageName + "AuthServiceImpl.java");
                    updatedZipFile.putNextEntry(authServiceImplClass);
                    updatedZipFile.write(authCreateServiceImplClass.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String webSecurityConfig = jwtCreatorVTwoUtil.WebSecurityConfigSecondVersions(tables);
                    ZipEntry webSecurityConfigZip = new ZipEntry(finalPath + securityPackageName + "WebSecurityConfig.java");
                    updatedZipFile.putNextEntry(webSecurityConfigZip);
                    updatedZipFile.write(webSecurityConfig.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String jwtUserDetailService = jwtCreatorVTwoUtil.jwtUserDetailServiceAddSecondVersions(tables);
                    ZipEntry jwtUserDetailServiceZip = new ZipEntry(finalPath + securityPackageName + "JwtUserDetailsService.java");
                    updatedZipFile.putNextEntry(jwtUserDetailServiceZip);
                    updatedZipFile.write(jwtUserDetailService.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String jwtRequestFilterService = jwtCreatorVTwoUtil.jwtRequestFilterServiceAddSecondVersions(tables);
                    ZipEntry jwtRequestFilterServiceZip = new ZipEntry(finalPath + securityPackageName + "JwtRequestFilter.java");
                    updatedZipFile.putNextEntry(jwtRequestFilterServiceZip);
                    updatedZipFile.write(jwtRequestFilterService.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String jwtAuthenticationEntryPointService = jwtCreatorVTwoUtil.jwtAuthenticationEntryPointAddSecondVersions(tables);
                    ZipEntry jwtAuthenticationEntryPointZip = new ZipEntry(finalPath + securityPackageName + "JwtAuthenticationEntryPoint.java");
                    updatedZipFile.putNextEntry(jwtAuthenticationEntryPointZip);
                    updatedZipFile.write(jwtAuthenticationEntryPointService.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();
                }
            }
            if (tables.getEnableSecurityDTO().getIsSecurityRequired() && tables.getEnableSecurityDTO().getSecurityMethod() == 2) {
                if (tables.getSpringBootBasicDTO().getBootVersion().startsWith("2")) {
                    /*OAuth Start*/
                    ZipEntry oauthConfiguration = new ZipEntry(finalPath + oauthPackageName + "OAuth2Config.java");
                    updatedZipFile.putNextEntry(oauthConfiguration);
                    String oauthConfigClass = oauthCreatorUtil.OAuth2Config(tables);
                    updatedZipFile.write(oauthConfigClass.getBytes());

                    ZipEntry securityConfiguration = new ZipEntry(finalPath + oauthPackageName + "SecurityConfiguration.java");
                    updatedZipFile.putNextEntry(securityConfiguration);
                    String securityConfigurationClass = oauthCreatorUtil.SecurityConfiguration(tables);
                    updatedZipFile.write(securityConfigurationClass.getBytes());

                    ZipEntry customUserConfiguration = new ZipEntry(finalPath + oauthPackageName + "CustomUser.java");
                    updatedZipFile.putNextEntry(customUserConfiguration);
                    String customUserConfigurationClass = oauthCreatorUtil.CustomUser(tables);
                    updatedZipFile.write(customUserConfigurationClass.getBytes());


                    ZipEntry customDetailsServiceConfiguration = new ZipEntry(finalPath + oauthPackageName + "CustomDetailsService.java");
                    updatedZipFile.putNextEntry(customDetailsServiceConfiguration);
                    String customDetailsServiceClass = oauthCreatorUtil.customDetailsService(tables);
                    updatedZipFile.write(customDetailsServiceClass.getBytes());

                    ZipEntry oAuthDaoConfiguration = new ZipEntry(finalPath + oauthPackageName + "OAuthDao.java");
                    updatedZipFile.putNextEntry(oAuthDaoConfiguration);
                    String oAuthDaoClass = oauthCreatorUtil.oAuthDao(tables);
                    updatedZipFile.write(oAuthDaoClass.getBytes());

                    ZipEntry userEntityConfiguration = new ZipEntry(finalPath + oauthPackageName + "UserEntity.java");
                    updatedZipFile.putNextEntry(userEntityConfiguration);
                    String userEntityConfigurationClass = oauthCreatorUtil.userEntity(tables);
                    updatedZipFile.write(userEntityConfigurationClass.getBytes());


                } else if (tables.getSpringBootBasicDTO().getBootVersion().startsWith("3")) {
                    ZipEntry securityConfigKeyCloakConfigurationThree = new ZipEntry(finalPath + keyCloakConfig + Constant.SECURITY_CONFIG);
                    updatedZipFile.putNextEntry(securityConfigKeyCloakConfigurationThree);
                    String securityConfigKeyCloakConfigurationConfigClassThree = keyCloakCreatorUtil.SecurityConfigKeyCloakVersionThree(tables);
                    updatedZipFile.write(securityConfigKeyCloakConfigurationConfigClassThree.getBytes());

                    ZipEntry jwtAuthConverterKeyCloakConfiguration = new ZipEntry(finalPath + keyCloakConfig + "JwtAuthConverter.java");
                    updatedZipFile.putNextEntry(jwtAuthConverterKeyCloakConfiguration);
                    String jwtAuthConverterKeyCloakConfigurationThree = keyCloakCreatorUtil.JwtAuthConverterVersionThree(tables);
                    updatedZipFile.write(jwtAuthConverterKeyCloakConfigurationThree.getBytes());
                }
                /*KeyCloakThree End*/
            }
            /*KeyCloak Start*/
            if (tables.getEnableSecurityDTO().getIsSecurityRequired() && tables.getEnableSecurityDTO().getSecurityMethod() == 3) {

                if (tables.getSpringBootBasicDTO().getBootVersion().startsWith("2")) {
                    ZipEntry securityConfigKeyCloakConfiguration = new ZipEntry(finalPath + keyCloakConfig + Constant.SECURITY_CONFIG);
                    updatedZipFile.putNextEntry(securityConfigKeyCloakConfiguration);
                    String securityConfigKeyCloakConfigurationConfigClass = keyCloakCreatorUtil.SecurityConfigKeyCloak(tables);
                    updatedZipFile.write(securityConfigKeyCloakConfigurationConfigClass.getBytes());

                } else if (tables.getSpringBootBasicDTO().getBootVersion().startsWith("3")) {
                    ZipEntry securityConfigKeyCloakConfigurationThree = new ZipEntry(finalPath + keyCloakConfig + Constant.SECURITY_CONFIG);
                    updatedZipFile.putNextEntry(securityConfigKeyCloakConfigurationThree);
                    String securityConfigKeyCloakConfigurationConfigClassThree = keyCloakCreatorUtil.SecurityConfigKeyCloakVersionThree(tables);
                    updatedZipFile.write(securityConfigKeyCloakConfigurationConfigClassThree.getBytes());

                    ZipEntry jwtAuthConverterKeyCloakConfiguration = new ZipEntry(finalPath + keyCloakConfig + "JwtAuthConverter.java");
                    updatedZipFile.putNextEntry(jwtAuthConverterKeyCloakConfiguration);
                    String jwtAuthConverterKeyCloakConfigurationThree = keyCloakCreatorUtil.JwtAuthConverterVersionThree(tables);
                    updatedZipFile.write(jwtAuthConverterKeyCloakConfigurationThree.getBytes());
                }

            }
            /*KeyCloak End*/

            for (Table table : tables.getTableList()) {
                if (table.getName() == null || table.getName().isEmpty()) {
                    throw new CustomValidationException(ErrorCode.D2D_7);
                }
                Boolean isDeletedPresent = false;
                if (table.getColumns().stream().filter(column -> column.getName()
                        .equalsIgnoreCase("deleted_flag")).collect(Collectors.toList()).size() > 0) {
                    isDeletedPresent = true;
                }
                // now append some extra content
                ZipEntry e = new ZipEntry(finalPath + modelPackageName + commonUtil.CapitalizeClassName(table.getName()) + ".java");
                updatedZipFile.putNextEntry(e);
                String completeString = classCreatorUtil.modelClassFileZipEntryString(databaseEngineId, table,
                        tables.getSpringBootBasicDTO().getPackageName(), tables.getSpringBootBasicDTO().getBootVersion(), isDeletedPresent);
                updatedZipFile.write(completeString.getBytes());
                ZipEntry ex = new ZipEntry(finalPath + repositoryPackageName + commonUtil.CapitalizeClassName(table.getName()) + "Repository.java");
                updatedZipFile.putNextEntry(ex);
                String repositoryClassString = classCreatorUtil.repositoryClassFileZipEntryString(tables.getEnableSecurityDTO(), databaseEngineId, table, tables.getSpringBootBasicDTO().getPackageName());
                updatedZipFile.write(repositoryClassString.getBytes());
                ZipEntry dto = new ZipEntry(finalPath + dTOPackageName + commonUtil.CapitalizeClassName(table.getName()) + "DTO.java");
                updatedZipFile.putNextEntry(dto);
                String dtoClassString = classCreatorUtil.dtoClassFileZipEntryString(databaseEngineId, table, tables.getSpringBootBasicDTO().getPackageName(), "DTO");
                updatedZipFile.write(dtoClassString.getBytes());
                if (table.getCrudDTO().getCreate()) {
                    ZipEntry requestDto = new ZipEntry(finalPath + dTOPackageName + commonUtil.CapitalizeClassName(table.getName()) + "RequestDTO.java");
                    updatedZipFile.putNextEntry(requestDto);
                    String requestDtoClassString = classCreatorUtil.dtoClassFileZipEntryString(databaseEngineId, table, tables.getSpringBootBasicDTO().getPackageName(), "RequestDTO");
                    updatedZipFile.write(requestDtoClassString.getBytes());
                }

                if (table.getCrudDTO().getMultipleRecord().getIsChecked() && (table.getCrudDTO().getMultipleRecord().getPagination()
                        || table.getCrudDTO().getMultipleRecord().getSort() || table.getCrudDTO().getMultipleRecord().getFilter() ||
                        table.getCrudDTO().getMultipleRecord().getIsSearch())) {
                    ZipEntry getAllRequestDTO = new ZipEntry(finalPath + dTOPackageName + commonUtil.CapitalizeClassName(table.getName()) + "GetAllRequestDTO.java");
                    updatedZipFile.putNextEntry(getAllRequestDTO);
                    String getAllRequestDtoClassString = classCreatorUtil.getAllDTOClassFileZipEntryString(databaseEngineId, table,
                            tables.getSpringBootBasicDTO().getPackageName());
                    updatedZipFile.write(getAllRequestDtoClassString.getBytes());
                }


                if (table.getCrudDTO().getMultipleRecord().getIsChecked() || table.getCrudDTO().getSingleRecord() ||
                        table.getCrudDTO().getDelete().getIsChecked() || table.getCrudDTO().getCreate()) {
                    String tableClassName = commonUtil.CapitalizeClassName(table.getName());

                    List<Column> filteredColumnList = table.getColumns().stream().filter(s -> s.getKeytype() != null && s.getKeytype().equalsIgnoreCase("PK")).collect(Collectors.toList());

                    if (filteredColumnList.size() != 1) {
                        throw new CustomValidationException(ErrorCode.D2D_10);
                    }

                    Map<Integer, Map<String, String>> map = commonUtil.getJavaType();
                    String primaryKeyJavaType = map.get(databaseEngineId).get(filteredColumnList.get(0).getDatatype().split("\\(")[0]);
                    String primaryKeyFieldName = filteredColumnList.get(0).getName();

                    String createControllerClass = crudUtil.createControllerClass(table, tableClassName, tables.getSpringBootBasicDTO().getPackageName(),
                            primaryKeyJavaType, tables, isDeletedPresent);
                    ZipEntry controllerClass = new ZipEntry(finalPath + controllerPackageName + tableClassName + "Controller.java");
                    updatedZipFile.putNextEntry(controllerClass);
                    updatedZipFile.write(createControllerClass.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String createServiceClass = crudUtil.createServiceClass(table, tableClassName, tables.getSpringBootBasicDTO().getPackageName(),
                            primaryKeyJavaType, isDeletedPresent);
                    ZipEntry serviceClass = new ZipEntry(finalPath + serviceInterfacePackageName + commonUtil.CapitalizeClassName(table.getName()) + "Service.java");
                    updatedZipFile.putNextEntry(serviceClass);
                    updatedZipFile.write(createServiceClass.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();

                    String createServiceImplClass = crudUtil.createServiceImplClass(table, tableClassName, tables.getSpringBootBasicDTO().getPackageName(),
                            primaryKeyJavaType, primaryKeyFieldName, isDeletedPresent);
                    ZipEntry serviceImplClass = new ZipEntry(finalPath + serviceImplPackageName + commonUtil.CapitalizeClassName(table.getName()) + "ServiceImpl.java");
                    updatedZipFile.putNextEntry(serviceImplClass);
                    updatedZipFile.write(createServiceImplClass.getBytes(StandardCharsets.UTF_8));
                    updatedZipFile.closeEntry();
                }
            }

                updatedZipFile.closeEntry();
            }
            updatedZipFile.close();
        } catch (CustomValidationException customValidationException) {
            throw new CustomValidationException(customValidationException.getErrorCode());
        }

    }


    public void writeToZip(Path dbDesignAndScriptZip,DbDesignTables tables,ByteArrayResource downloadTextFile,ByteArrayResource downloadExcelFile) throws IOException {
        try (ZipOutputStream dbDesignAndScript = new ZipOutputStream(new FileOutputStream(dbDesignAndScriptZip.toFile()))) {
            ZipEntry excelEntry = new ZipEntry(tables.getSpringBootBasicDTO().getArtifactId() + "_DbDesign.xlsx");
            dbDesignAndScript.putNextEntry(excelEntry);
            IOUtils.copy(downloadExcelFile.getInputStream(), dbDesignAndScript);
            dbDesignAndScript.closeEntry();
            ZipEntry textEntry = new ZipEntry(tables.getSpringBootBasicDTO().getArtifactId() + "_SqlScript.sql");
            dbDesignAndScript.putNextEntry(textEntry);
            if (downloadTextFile != null) {
                IOUtils.copy(downloadTextFile.getInputStream(), dbDesignAndScript);
            } else {
                throw new CustomValidationException(ErrorCode.D2D_11);
            }
            dbDesignAndScript.closeEntry();

        } catch (Exception e) {
            logger.info("********generateBasicSpringBootSetUp*****", e);
        }
    }

    public static void copy(InputStream input, OutputStream output) throws IOException {
        int bytesRead;
        while ((bytesRead = input.read(BUFFER)) != -1) {
            output.write(BUFFER, 0, bytesRead);
        }
    }
}
