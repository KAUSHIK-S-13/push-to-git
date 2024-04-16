package com.d2d.util;

import com.d2d.config.MinioFileUploader;
import com.d2d.constant.Constant;
import com.d2d.dto.ByteArrayMultipartFile;
import com.d2d.dto.DbDesignTables;
import com.d2d.entity.DataBaseEngine;
import com.d2d.entity.FileDetails;
import com.d2d.entity.MnemonicMaster;
import com.d2d.entity.Project;
import com.d2d.entity.ProjectFileDetails;
import com.d2d.entity.ProjectUsers;
import com.d2d.entity.Users;
import com.d2d.exception.CustomValidationException;
import com.d2d.exception.ErrorCode;
import com.d2d.repository.DataBaseEngineRepository;
import com.d2d.repository.FileDetailsRepository;
import com.d2d.repository.MnemonicMasterRepository;
import com.d2d.repository.ProjectFileDetailsRepository;
import com.d2d.repository.ProjectRepository;
import com.d2d.repository.ProjectUsersRepository;
import com.d2d.repository.UsersRepository;
import com.d2d.securityConfig.JwtRequestFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;

@Component
public class ProjectUtil {

    @Autowired
    MnemonicMasterRepository mnemonicMasterRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectUsersRepository projectUsersRepository;

    @Autowired
    ProjectFileDetailsRepository projectFileDetailsRepository;

    @Autowired
    DataBaseEngineRepository dataBaseEngineRepository;

    @Autowired
    MinioFileUploader minioFileUploader;

    @Autowired
    FileDetailsRepository fileDetailsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Value("${app.SECRET_KEY}")
    private String SECRET_KEY;
    @Value("${app.ALGORITHM}")
    private String ALGORITHM;
    @Value("${app.CIPHER_TRANSFORMATION}")
    private String CIPHER_TRANSFORMATION;



    @Transactional
    public void projectDetailsSave(DbDesignTables tables, ByteArrayResource zipResource) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException, CustomValidationException {
        Project project=new Project();
        project.setProjectName(tables.getProjectName());
        Optional<MnemonicMaster> mnemonicMaster=mnemonicMasterRepository.findById(tables.getProjectTypeId());
        mnemonicMaster.ifPresent(project::setProjectType);
        Optional<DataBaseEngine> dataBaseEngine = dataBaseEngineRepository.findById(tables.getDatabaseEngineId());
        dataBaseEngine.ifPresent(project::setDbEngine);
        project.setIsActive(true);
        project.setDeletedFlag(false);
        project.setCreatedBy(JwtRequestFilter.userId.get());
        project.setModifiedBy(JwtRequestFilter.userId.get());
        Project projectSave=projectRepository.save(project);


        InputStream inputStream = zipResource.getInputStream();
        byte[] byteArray = StreamUtils.copyToByteArray(inputStream);
        MultipartFile multipartFile = new ByteArrayMultipartFile("file",
                tables.getProjectName()+"_DBDesignDocuments.zip",
                "application/zip", byteArray);
        Map<String,String> files=minioFileUploader.uploadFile(multipartFile);


        FileDetails fileDetails=new FileDetails();
        fileDetails.setOriginalFileName(files.get(Constant.ORIGINAL_FILE_NAME));
        fileDetails.setGeneratedFileName(files.get(Constant.GENERATED_FILE_NAME));
        fileDetails.setIsActive(true);
        fileDetails.setDeletedFlag(false);
        fileDetails.setCreatedBy(JwtRequestFilter.userId.get());
        fileDetails.setModifiedBy(JwtRequestFilter.userId.get());
        FileDetails fileDetailsSave=fileDetailsRepository.save(fileDetails);

        ProjectFileDetails projectFileDetails=new ProjectFileDetails();
        projectFileDetails.setFileDetails(fileDetailsSave);
        projectFileDetails.setProject(projectSave);
        projectFileDetailsRepository.save(projectFileDetails);


        ProjectUsers projectUsers=new ProjectUsers();
        Optional<Users> users = usersRepository.findById(JwtRequestFilter.userId.get());
        users.ifPresent(projectUsers::setUser);
        projectUsers.setProject(projectSave);
        projectUsersRepository.save(projectUsers);
    }


    public  void encryptJson(DbDesignTables tables) throws CustomValidationException {

        try {

            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);

            Cipher aesCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);

            ObjectMapper objectMapper = new ObjectMapper();
            if (tables.getDatabaseEngineId() != null && tables.getDatabaseEngineId() != 0) {
                Optional<DataBaseEngine> dataBaseEngine =
                        dataBaseEngineRepository.findByIdAndIsActive(tables.getDatabaseEngineId(), 1);
                tables.setDbName(dataBaseEngine.get().getDataBaseEngineName());
            }
            String jsonValue = objectMapper.writeValueAsString(tables);
            byte[] encryptedBytes = aesCipher.doFinal(jsonValue.getBytes());

            MultipartFile multipartFile=getFileDetails(tables, encryptedBytes);
            if (tables.getProjectId()==null || tables.getProjectId()==0){
            Project project=new Project();
            project.setProjectName(tables.getProjectName());
            Optional<MnemonicMaster> mnemonicMaster=mnemonicMasterRepository.findById(tables.getProjectTypeId());
            mnemonicMaster.ifPresent(project::setProjectType);
            Optional<DataBaseEngine> dataBaseEngine = dataBaseEngineRepository.findById(tables.getDatabaseEngineId());
            dataBaseEngine.ifPresent(project::setDbEngine);
            project.setIsActive(true);
            project.setDeletedFlag(false);
            project.setCreatedBy(JwtRequestFilter.userId.get());
            project.setModifiedBy(JwtRequestFilter.userId.get());
            Project projectSave=projectRepository.save(project);

            Map<String,String> files=minioFileUploader.uploadFile(multipartFile);
            FileDetails fileDetails=new FileDetails();
            fileDetails.setOriginalFileName(files.get(Constant.ORIGINAL_FILE_NAME));
            fileDetails.setGeneratedFileName(files.get(Constant.GENERATED_FILE_NAME));
            fileDetails.setIsActive(true);
            fileDetails.setDeletedFlag(false);
            fileDetails.setCreatedBy(JwtRequestFilter.userId.get());
            fileDetails.setModifiedBy(JwtRequestFilter.userId.get());
            FileDetails fileDetailsSave=fileDetailsRepository.save(fileDetails);

            ProjectFileDetails projectFileDetails=new ProjectFileDetails();
            projectFileDetails.setFileDetails(fileDetailsSave);
            projectFileDetails.setProject(projectSave);
            projectFileDetails.setDeletedFlag(false);
            projectFileDetailsRepository.save(projectFileDetails);


            ProjectUsers projectUsers=new ProjectUsers();
            Optional<Users> users = usersRepository.findById(JwtRequestFilter.userId.get());
            users.ifPresent(projectUsers::setUser);
            projectUsers.setProject(projectSave);
            projectUsers.setDeletedFlag(false);
            projectUsersRepository.save(projectUsers);
            }else {
                Optional<Project> project=projectRepository.findByIdAndDeletedFlag(tables.getProjectId(), false);
                if (project.isPresent()){

                    Optional<ProjectFileDetails> projectFileDetailsGet=projectFileDetailsRepository
                            .findByProjectIdAndDeletedFlag(tables.getProjectId(), false);
                    projectFileDetailsGet.ifPresent(projectFileDetail -> {
                        projectFileDetail.setDeletedFlag(true);
                        projectFileDetailsRepository.save(projectFileDetailsGet.get());
                    });

                    Optional<ProjectUsers> projectUsersGet=projectUsersRepository
                            .findByProjectIdAndDeletedFlag(tables.getProjectId(), false);
                    projectUsersGet.ifPresent(projectUser -> {
                        projectUser.setDeletedFlag(true);
                        projectUsersRepository.save(projectUsersGet.get());
                    });

                    Optional<FileDetails> fileDetailsGet=fileDetailsRepository
                            .findByIdAndDeletedFlag(projectFileDetailsGet.get().getFileDetails().getId(), false);
                    fileDetailsGet.ifPresent(fileDetail -> {
                        fileDetail.setDeletedFlag(true);
                        fileDetailsRepository.save(fileDetailsGet.get());
                    });

                    Map<String,String> files=minioFileUploader.uploadFile(multipartFile);
                    FileDetails fileDetails=new FileDetails();
                    fileDetails.setOriginalFileName(files.get(Constant.ORIGINAL_FILE_NAME));
                    fileDetails.setGeneratedFileName(files.get(Constant.GENERATED_FILE_NAME));
                    fileDetails.setIsActive(true);
                    fileDetails.setDeletedFlag(false);
                    fileDetails.setCreatedBy(JwtRequestFilter.userId.get());
                    fileDetails.setModifiedBy(JwtRequestFilter.userId.get());
                    FileDetails fileDetailsSave=fileDetailsRepository.save(fileDetails);

                    ProjectFileDetails projectFileDetails=new ProjectFileDetails();
                    projectFileDetails.setFileDetails(fileDetailsSave);
                    projectFileDetails.setProject(project.get());
                    projectFileDetails.setDeletedFlag(false);
                    projectFileDetailsRepository.save(projectFileDetails);


                    ProjectUsers projectUsers=new ProjectUsers();
                    Optional<Users> users = usersRepository.findById(JwtRequestFilter.userId.get());
                    users.ifPresent(projectUsers::setUser);
                    projectUsers.setProject(project.get());
                    projectUsers.setDeletedFlag(false);
                    projectUsersRepository.save(projectUsers);


                }

            }
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException | JsonProcessingException e) {
            throw new CustomValidationException(ErrorCode.valueOf("File Not Exist"));
        } catch (MinioException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public  DbDesignTables exportEncryptJson(DbDesignTables tables) throws CustomValidationException {

        try {

            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);

            Cipher aesCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);

            ObjectMapper objectMapper = new ObjectMapper();
            if (tables.getDatabaseEngineId() != null && tables.getDatabaseEngineId() != 0) {
                Optional<DataBaseEngine> dataBaseEngine =
                        dataBaseEngineRepository.findByIdAndIsActive(tables.getDatabaseEngineId(), 1);
                tables.setDbName(dataBaseEngine.get().getDataBaseEngineName());
            }
            String jsonValue = objectMapper.writeValueAsString(tables);
            byte[] encryptedBytes = aesCipher.doFinal(jsonValue.getBytes());

            MultipartFile multipartFile=getFileDetails(tables, encryptedBytes);
            if (tables.getProjectId()==null || tables.getProjectId()==0){
                Project project=new Project();
                project.setProjectName(tables.getProjectName());
                Optional<MnemonicMaster> mnemonicMaster=mnemonicMasterRepository.findById(tables.getProjectTypeId());
                mnemonicMaster.ifPresent(project::setProjectType);
                Optional<DataBaseEngine> dataBaseEngine = dataBaseEngineRepository.findById(tables.getDatabaseEngineId());
                dataBaseEngine.ifPresent(project::setDbEngine);
                project.setIsActive(true);
                project.setDeletedFlag(false);
                project.setCreatedBy(JwtRequestFilter.userId.get());
                project.setModifiedBy(JwtRequestFilter.userId.get());
                Project projectSave=projectRepository.save(project);

                Map<String,String> files=minioFileUploader.uploadFile(multipartFile);
                FileDetails fileDetails=new FileDetails();
                fileDetails.setOriginalFileName(files.get(Constant.ORIGINAL_FILE_NAME));
                fileDetails.setGeneratedFileName(files.get(Constant.GENERATED_FILE_NAME));
                fileDetails.setIsActive(true);
                fileDetails.setDeletedFlag(false);
                fileDetails.setCreatedBy(JwtRequestFilter.userId.get());
                fileDetails.setModifiedBy(JwtRequestFilter.userId.get());
                FileDetails fileDetailsSave=fileDetailsRepository.save(fileDetails);

                ProjectFileDetails projectFileDetails=new ProjectFileDetails();
                projectFileDetails.setFileDetails(fileDetailsSave);
                projectFileDetails.setProject(projectSave);
                projectFileDetails.setDeletedFlag(false);
                projectFileDetailsRepository.save(projectFileDetails);


                ProjectUsers projectUsers=new ProjectUsers();
                Optional<Users> users = usersRepository.findById(JwtRequestFilter.userId.get());
                users.ifPresent(projectUsers::setUser);
                projectUsers.setProject(projectSave);
                projectUsers.setDeletedFlag(false);
                projectUsersRepository.save(projectUsers);
                tables.setProjectId(projectSave.getId());
                return tables;
            }else {
                Optional<Project> project=projectRepository.findByIdAndDeletedFlag(tables.getProjectId(), false);
                if (project.isPresent()){

                    Optional<ProjectFileDetails> projectFileDetailsGet=projectFileDetailsRepository
                            .findByProjectIdAndDeletedFlag(tables.getProjectId(), false);
                    projectFileDetailsGet.ifPresent(projectFileDetail -> {
                        projectFileDetail.setDeletedFlag(true);
                        projectFileDetailsRepository.save(projectFileDetailsGet.get());
                    });

                    Optional<ProjectUsers> projectUsersGet=projectUsersRepository
                            .findByProjectIdAndDeletedFlag(tables.getProjectId(), false);
                    projectUsersGet.ifPresent(projectUser -> {
                        projectUser.setDeletedFlag(true);
                        projectUsersRepository.save(projectUsersGet.get());
                    });

                    Optional<FileDetails> fileDetailsGet=fileDetailsRepository
                            .findByIdAndDeletedFlag(projectFileDetailsGet.get().getFileDetails().getId(), false);
                    fileDetailsGet.ifPresent(fileDetail -> {
                        fileDetail.setDeletedFlag(true);
                        fileDetailsRepository.save(fileDetailsGet.get());
                    });

                    Map<String,String> files=minioFileUploader.uploadFile(multipartFile);
                    FileDetails fileDetails=new FileDetails();
                    fileDetails.setOriginalFileName(files.get(Constant.ORIGINAL_FILE_NAME));
                    fileDetails.setGeneratedFileName(files.get(Constant.GENERATED_FILE_NAME));
                    fileDetails.setIsActive(true);
                    fileDetails.setDeletedFlag(false);
                    fileDetails.setCreatedBy(JwtRequestFilter.userId.get());
                    fileDetails.setModifiedBy(JwtRequestFilter.userId.get());
                    FileDetails fileDetailsSave=fileDetailsRepository.save(fileDetails);

                    ProjectFileDetails projectFileDetails=new ProjectFileDetails();
                    projectFileDetails.setFileDetails(fileDetailsSave);
                    projectFileDetails.setProject(project.get());
                    projectFileDetails.setDeletedFlag(false);
                    projectFileDetailsRepository.save(projectFileDetails);


                    ProjectUsers projectUsers=new ProjectUsers();
                    Optional<Users> users = usersRepository.findById(JwtRequestFilter.userId.get());
                    users.ifPresent(projectUsers::setUser);
                    projectUsers.setProject(project.get());
                    projectUsers.setDeletedFlag(false);
                    projectUsersRepository.save(projectUsers);

                    tables.setProjectId(project.get().getId());
                }
                return tables;

            }
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException | JsonProcessingException e) {
            throw new CustomValidationException(ErrorCode.valueOf("File Not Exist"));
        } catch (MinioException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public MultipartFile getFile(byte[] encryptedBytes) {
        MultipartFile multipartFile = new MultipartFile() {
            @Override
            public String getName() {
                return "file";
            }

            @Override
            public String getOriginalFilename() {
                return "d2dFile.dtd";
            }

            @Override
            public String getContentType() {
                return "application/octet-stream";
            }

            @Override
            public boolean isEmpty() {
                return encryptedBytes.length == 0;
            }

            @Override
            public long getSize() {
                return encryptedBytes.length;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return encryptedBytes;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(encryptedBytes);
            }

            @Override
            public Resource getResource() {
                return MultipartFile.super.getResource();
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {

            }

            @Override
            public void transferTo(Path dest) throws IOException, IllegalStateException {
                MultipartFile.super.transferTo(dest);
            }
        };
        return multipartFile;
    }

    public MultipartFile getFileDetails(DbDesignTables tables,byte[] encryptedBytes) {
        MultipartFile multipartFile = new MultipartFile() {
            @Override
            public String getName() {
                return "file";
            }

            @Override
            public String getOriginalFilename() {
                return tables.getProjectName() + "_" + tables.getProjectTypeId() + "_d2dFile.dtd";
            }

            @Override
            public String getContentType() {
                return "application/octet-stream";
            }

            @Override
            public boolean isEmpty() {
                return encryptedBytes.length == 0;
            }

            @Override
            public long getSize() {
                return encryptedBytes.length;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return encryptedBytes;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(encryptedBytes);
            }

            @Override
            public Resource getResource() {
                return MultipartFile.super.getResource();
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {

            }

            @Override
            public void transferTo(Path dest) throws IOException, IllegalStateException {
                MultipartFile.super.transferTo(dest);
            }
        };
        return multipartFile;
    }


    }
