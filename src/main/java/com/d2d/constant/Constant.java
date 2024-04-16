package com.d2d.constant;

public class Constant {

    /**
     * JWT INFO
     */
    public static final String HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String NOT_FOUND = "Record Not Found..!";
    public static final String TOKEN_ERROR = "Unable to get JWT Token..!";
    public static final String EXPIRED = "JWT Token has expired";
    public static final String PREFIX_MISSED = "JWT Token does not begin with Bearer String";
    /**
     * CONTROLLER INFO
     */
    public static final String USER = "/user";
    public static final String SM = "/sm";
    public static final String QUESTION_TRACKER = "/question/tracker";
    public static final String MASTER_DATA = "/master/data";
    /**
     * URL INFO
     */
    public static final String ID = "/{id}";
    public static final String END = "/";
    public static final String DATE = "/date";
    public static final String ALL = "/all";
    public static final String STATUS = "/status";
    public static final String ROLE = "/role";
    public static final String BY_VSO = "/vso";
    public static final String BY_SM = "/sm";
    public static final String SUBMIT = "/submit";
    public static final String LOGIN = "/auth/login";
    public static final String REFRESH = "/auth/refresh";

    public static final String USER_SIGNUP = "/user/userSignup";

    public static final String TEST_1 = "/user/test";


    public static final String GOOGLE_SIGNUP = "/user/googleSignup";
    public static final Object USER_CREATED_SUCCESSFULLY = "USER CREATED SUCCESSFULLY";
    public static final String EMAIL = "email";
    public static final String D2D_AUTHENTICATION = "D2D Authentication";
    public static final String BAD_REQUEST = "Bad Request";
    public static final String CREATE_TABLE_FORMAT = "CREATE TABLE %s (\n";
    public static final String CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS %s (\n";
    public static final String COLUMN_FORMAT = "  %s %s";
    public static final String DEFAULT_FORMAT = " DEFAULT %s ";
    public static final String PRIMARY_KEY = " PRIMARY KEY";
    public static final String AUTO_INCREMENT = "AUTO_INCREMENT";
    public static final String CONSTRAINT_FK_FORMAT = "  CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s (%s)";
    public static final String COMMA_NEWLINE = ",\n";
    public static final String DEFAULT = " DEFAULT '%s'";
    public static final String END_OF_STATEMENT = "\n);\n\n";
    public static final String URL = "?useSSL=true&requireSSL=false&serverTimezone=UTC";
    public static final String MOBILE = "mobile";
    public static final String MAIN_APPLICATION="Spring boot application started";

    public static final String SAMPLE="sample";
    public static final String NOTNULL="not null";
    public static final String VARCHAR="VARCHAR2(30)";
    public static final String FALSE="false";
    public static final String TRUE="true";

    public static final String APPLICATION_JAVA="Application.java";
    public static final String SRC_TEST_JAVA="/src/test/java/";
    public static final String SRC_MAIN_JAVA="/src/main/java/";
    public static final String SRC_TESTS_JAVA="ApplicationTests.java";
    public static final String DOT_JAVA =".java";
    public static final String PUBLIC_CLASS="public class ";
    public static final String DELETED_FLAG="deleted_flag";

    public static final String APPLICATION_TESTS="ApplicationTests {";
    public static final String SPRING_BOOT_TEST ="@SpringBootTest";
    public static final String METADATA_CLIENT="https://start.spring.io/metadata/client";
    public static final String STARTER_ZIP="https://start.spring.io/starter.zip";
    public static final String SECURITY_CONFIG="SecurityConfig.java";

    public static final String SSL_TRUE="?useSSL=true&requireSSL=false&serverTimezone=UTC";

    public static final String PACKAGE="package ";

    public static final String IMPORT_LOMBOK_SETTER="import lombok.Setter;\n\n";
    public static final String IMPORT_LOMBOK_GETTER ="import lombok.Getter;\n";

    public static final String TIMESTAMP="TIMESTAMP";

    public static final String DATE_TIME="DATETIME";

    public static final String TIMETZ="timetz";

    public static final String TIMES_TAMPTZ="timestamptz";
    public static final String TIMES_STAMP="timestamp";
    public static final String DATE_TIMES="datetime";
    public static final String DATE_TIME2="datetime2";
    public static final String SMALL_DATE_TIME="smalldatetime";

    public static final String DATE_TIME_OFF_SET="datetimeoffset";
    public static final String GETTER="@Getter\n";
    public static final String SETTER="@Setter\n";
    public static final String CLOSE_PARENTHESIS="\")\n";
    public static final String OPEN_PARENTHESIS=" {\n\n";
    public static final String COLUMN_NAME ="  @Column(name = \"";
    public static final String SINGLE_SLASH=";\n\n";
    public static final String PRIVATE="  private ";
    public static final String IMPORT_JAVA_UTIL_LIST="import java.util.List;\n";
    public static final String AND=" AND ";
    public static final String CLOSE="}\n\n";
    public static final String SLASH_PRIVATE="\tprivate ";
    public static final String OPEN_DEPENDENCY="\t\t<dependency>\n";
    public static final String CLOSE_DEPENDENCY="\t\t</dependency>\n";
    public static final String STRING_FORMAT = "/* %s */%n%n";
    public static final String JASON_WEB_TOKEN = "\t\t\t<groupId>io.jsonwebtoken</groupId>\n";
    public static final String JACKSON_VERSION = "\t\t\t<version>0.11.5</version>\n";
    public static final String SPRING_FRAMEWORK = "\t\t\t<groupId>org.springframework.boot</groupId>\n";
    public static final String SPRING_STARTER_SECURITY = "\t\t\t<artifactId>spring-boot-starter-security</artifactId>\n";
    public static final String DEPENDENCY = "\t\t</dependency>\n";
    public static final String EXCEPTION = ".exception;\n\n";
    public static final String DOUBLE_NEWLINE = "{\n\n";
    public static final String TAB_NEWLINE = "\t}\n\n";
    public static final String PUBLICCLASS = " public class ";
    public static final String SPACE_CHARACTER = " }\n\n";
    public static final String INTEGER = "Integer";
    public static final String STRING = "String";
    public static final String TIMESTAMP_PASCAL = "Timestamp";
    public static final String BOOLEAN = "Boolean";
    public static final String FLOAT = "float";
    public static final String NUMERIC = "numeric";
    public static final String DOUBLE = "Double";
    public static final String SHORT = "Short";
    public static final String VARCHAR_SMALL = "varchar";
    public static final String TIMESTAMP_SMALL = "timestamp";
    public static final String PACKAGE_SPACE = "package ";
    public static final String IMPORT = "import ";
    public static final String SUCCESS_RESPONSE = ".response.SuccessResponse;\n";
    public static final String PAGE_RESPONSE = ".response.PageResponse;\n";
    public static final String DTO = ".dto.";
    public static final String REQUEST_DTO_NEWLINE = "RequestDTO;\n";
    public static final String GET_ALLREQUEST_DTO = "GetAllRequestDTO;\n";
    public static final String AUTOWIRED = "    @Autowired\n";
    public static final String PRIVATE_SPACE = "    private ";
    public static final String SEMI_SPACE = ";\n\n";
    public static final String REQUEST_DTO = "RequestDTO";
    public static final String RETURN = "        return ";
    public static final String SPACE_NEWLINE = "    }\n\n";
    public static final String GETALL_REQUEST_DTO = "GetAllRequestDTO ";
    public static final String ID_NEWLINE = " id) {\n";
    public static final String ROLES_ALLOWED = "    public String rolesAllowed() {\n";
    public static final String RETURN_NEWLINE = "        return \"\";\n";
    public static final String OVERRIDE = "    @Override\n";
    public static final String SUCCESSRESPONSE_CREATION = "        SuccessResponse<Object> successResponse = new SuccessResponse<>();\n";
    public static final String LIST = "        List<";
    public static final String SPACE = "            ";
    public static final String NEW_LINE = "        }\n";
    public static final String RETURN_SUCCESS = "        return successResponse;\n";
    public static final String SECOND_SPACE = "                ";
    public static final String OPTIONAL = "        Optional<";
    public static final String OPTIONAL_EQUAL = "Optional = ";
    public static final String IF = "        if (";
    public static final String OPTIONAL_PRESENT = "Optional.isPresent()) {\n";
    public static final String ELSE_NEWLINE = "            } else {\n";
    public static final String THROW_NEW = "                throw new ";
    public static final String RUNTIME_EXCEPTION = "RuntimeException";
    public static final String CUSTOM_VALIDATION_EXCEPTION = "CustomValidationException";
    public static final String ID_NOT_FOUND_EXCEPTION = "(\"Id not Found Exception\");\n";
    public static final String CURLY_NEWLINE = "            }\n";
    public static final String CURLY_ELSE_NEWLINE = "        } else {\n";
    public static final String THROW_NEW_TWO = "            throw new ";
    public static final String SPACE_TWO = "                    ";
    public static final String REPO_DELETEBYID = "Repository.deleteById(id);\n";
    public static final String AUTH_RESPONSE = ".response.AuthResponse;\n";
    public static final String LOGIN_RESPONSE = ".response.LoginResponse;\n";
    public static final String AUTH_SERVICE = ".service.AuthService;\n";
    public static final String MODEL = ".model.";
    public static final String IMPORT_AUTOWIRED = "import org.springframework.beans.factory.annotation.Autowired;\n";
    public static final String IMPORT_AUTHENTICATION_TOKEN = "import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;\n";
    public static final String AUTOWIRED_NEWLINE = "\t@Autowired\n";
    public static final String THROW_BAD_REQUEST = "\t\t\tthrow new Exception(\"Bad Request\");\n";
    public static final String TWO_TAB_NEWLINE = "\t\t}\n";
    public static final String SINGLE_TAB_NEWLINE = "\t}\n";
    public static final String SECURITY_CONFIG_NEWLINE = ".securityconfig;\n\n";
    public static final String IMPORT_USER_DETAILS = "import org.springframework.security.core.userdetails.UserDetails;\n";
    public static final String IMPORT_COMPONENT = "import org.springframework.stereotype.Component;\n";
    public static final String IMPORT_SERIALIZABLE = "import java.io.Serializable;\n";
    public static final String COMPONENT = "@Component\n";
    public static final String SPACE_CURLY_NEWLINE = "    }\n";
    public static final String RESPONSE_NEWLINE = ".response;\n\n";
    public static final String IMPORT_GETTER = "import lombok.Getter;\n";
    public static final String GETTER_NEWLINE = "@Getter\n";
    public static final String IMPORT_SERVICE = "import org.springframework.stereotype.Service;\n";
    public static final String SERVICE = "@Service\n";
    public static final String TAB_OVERRIDE = "\t@Override\n";
    public static final String REPO_FIND = "Repository.findBy";
    public static final String IS_EMPTY = ".isEmpty()){\n";
    public static final String BEAN = "    @Bean\n";
    public static final String SECURITYCONFIG = " .securityconfig";
    public static final String SPACE_NEWLINE_TWO = "                /*\n";
    public static final String SPACE_NEWLINE_THREE = "                 */\n";
    public static final String KEYCLOAK_CONFIG = ".keycloakconfig;\n\n";
    public static final String OAUTH_CONFIG = ".oauthconfig;\n\n";
    public static final String SERVICE_NAME = "Service";
    public static final String REPOSITORY_SMALL = "repository";
    public static final String SMALLINT = "SMALLINT";
    public static final String SHORT_SMALL = "short";
    public static final String TEST = "\t@Test\n";
    public static final String FUNCTION_NEWLINE = "(){\n";
    public static final String GET = "\tpublic void get";
    public static final String LIST_DATA = "ListOfData()));\n";
    public static final String WHEN = "when(";
    public static final String OPTIONAL_NULLABLE = ".findById(any( ))).thenReturn(Optional.ofNullable";
    public static final String ASSERT_NOTNULL = "assertNotNull(";
    public static final String ARRAY_LIST = "ArrayList<>();";
    public static final String T_LIST = "\t\tList<";
    public static final String RETURN_SMALL = "return ";
    public static final String REQUEST_DTO_SPACE = "RequestDTO ";

    public static final String ORIGINAL_FILE_NAME = "originalFileName";

    public static final String GENERATED_FILE_NAME = "generatedFileName";

    public static final String USER_PROFILE_DOWNLOAD = "/downloadProfilePicture";

    private Constant() {
    }
}

