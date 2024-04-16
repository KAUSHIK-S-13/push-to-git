package com.d2d.util;

import com.d2d.constant.Constant;
import com.d2d.dto.DbDesignTables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Sekhar
 */
@Component
public class JwtCreatorVTwoUtil {

    @Autowired
    CommonUtil commonUtil;

    public String createControllerClassSecondVersions(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder( );
        String fieldOne = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO( ).getFieldOne( ));
        String fieldTwo = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO( ).getFieldTwo( ));
        String tableName = commonUtil.toCamelCase(tables.getEnableSecurityDTO( ).getAuthTableName( ));
        String capitalizeTableName = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO( ).getAuthTableName( ));
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(".controller;\n");
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(".securityconfig.JWTUtils;\n");
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.AUTH_RESPONSE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.LOGIN_RESPONSE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(".Response.LoginRequest;\n");
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.AUTH_SERVICE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.MODEL).append(capitalizeTableName).append(";\n");

        stringAdd.append(Constant.IMPORT_AUTOWIRED);
        stringAdd.append("import org.springframework.security.authentication.AuthenticationManager;\n");
        stringAdd.append("import org.springframework.security.authentication.BadCredentialsException;\n");
        stringAdd.append("import org.springframework.security.authentication.DisabledException;\n");
        stringAdd.append(Constant.IMPORT_AUTHENTICATION_TOKEN);
        stringAdd.append("import org.springframework.security.core.Authentication;\n");
        stringAdd.append("import org.springframework.security.core.context.SecurityContextHolder;\n");
        stringAdd.append("import org.springframework.web.bind.annotation.PostMapping;\n");
        stringAdd.append("import org.springframework.web.bind.annotation.RequestBody;\n");
        stringAdd.append("import org.springframework.web.bind.annotation.RequestParam;\n");
        stringAdd.append("import org.springframework.web.bind.annotation.RestController;\n");
        stringAdd.append("\n");
        stringAdd.append("import java.util.HashMap;\n");
        stringAdd.append("import java.util.Map;\n\n");
        stringAdd.append("@RestController\n");
        stringAdd.append("public class AuthController {\n\n");
        stringAdd.append(Constant.AUTOWIRED_NEWLINE);
        stringAdd.append("\tprivate AuthService authService;\n");
        stringAdd.append("\n");
        stringAdd.append(Constant.AUTOWIRED_NEWLINE);
        stringAdd.append("\tprivate AuthenticationManager authenticationManager;\n");
        stringAdd.append("\n");
        stringAdd.append(Constant.AUTOWIRED_NEWLINE);
        stringAdd.append("\tprivate JWTUtils jwtUtils;\n\n");

        stringAdd.append("\t@PostMapping(\"/login\")\n");
        stringAdd.append("\tpublic LoginResponse login(@RequestBody LoginRequest loginRequest) throws Exception {\n");
        stringAdd.append("\t\tif (loginRequest == null) {\n");
        stringAdd.append(Constant.THROW_BAD_REQUEST);
        stringAdd.append(Constant.TWO_TAB_NEWLINE);
        stringAdd.append("\t\tAuthResponse response = authenticate(loginRequest.get").append(fieldOne).append("(), loginRequest.get").append(fieldTwo).append("());\n");
        stringAdd.append("\t\treturn authService.login(response);\n");
        stringAdd.append(Constant.SINGLE_TAB_NEWLINE);
        stringAdd.append("\n");

        stringAdd.append("\t@PostMapping(\"/refresh\")\n");
        stringAdd.append("\tpublic AuthResponse refreshToken(@RequestParam String token) throws Exception {\n");
        stringAdd.append("\t\tString userName = jwtUtils.getUsernameFromToken(token);\n");
        stringAdd.append("\t\tif (userName==null){\n");
        stringAdd.append(Constant.THROW_BAD_REQUEST);
        stringAdd.append(Constant.TWO_TAB_NEWLINE);
        stringAdd.append("\t\t").append(capitalizeTableName).append(" ").append(tableName).append("= authService.getUserInfo(userName);\n");
        stringAdd.append("\t\tif (").append(tableName).append("==null){\n");
        stringAdd.append(Constant.THROW_BAD_REQUEST);
        stringAdd.append(Constant.TWO_TAB_NEWLINE);
        stringAdd.append("\t\treturn  getRefreshInfo(userName);\n");
        stringAdd.append(Constant.SINGLE_TAB_NEWLINE);
        stringAdd.append("\t\n");
        stringAdd.append("\n");
        stringAdd.append("\t\n");

        stringAdd.append("\tpublic AuthResponse authenticate(String username, String password) throws Exception {\n");
        stringAdd.append("\t\tString token = null;\n");
        stringAdd.append("\t\tString refreshToken = null;\n");
        stringAdd.append("\n");
        stringAdd.append("\t\ttry {\n");
        stringAdd.append("\t\t\tfinal Authentication authentication = authenticationManager\n");
        stringAdd.append("\t\t\t\t\t.authenticate(new UsernamePasswordAuthenticationToken(username, password));\n");
        stringAdd.append("\t\t\tSecurityContextHolder.getContext().setAuthentication(authentication);\n");
        stringAdd.append("\n");
        stringAdd.append("\t\t\ttoken = jwtUtils.generateToken(authentication);\n");
        stringAdd.append("\t\t\trefreshToken = jwtUtils.refreshToken(token);\n");
        stringAdd.append("\t\t} catch (DisabledException e) {\n");
        stringAdd.append("\t\t\tthrow new Exception(\"USER_DISABLED\", e);\n");
        stringAdd.append("\t\t} catch (BadCredentialsException e) {\n");
        stringAdd.append("\t\t\tthrow new Exception(\"INVALID_CREDENTIALS\", e);\n");
        stringAdd.append(Constant.TWO_TAB_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("\t\treturn new AuthResponse(token, refreshToken, jwtUtils.getUsernameFromToken(token));\n");
        stringAdd.append(Constant.SINGLE_TAB_NEWLINE);
        stringAdd.append("\n");

        stringAdd.append("\tpublic AuthResponse getRefreshInfo(String username) throws Exception {\n");
        stringAdd.append("\t\tString token = null;\n");
        stringAdd.append("\t\tString refreshToken = null;\n");
        stringAdd.append("\n");
        stringAdd.append("\t\ttry {\n");
        stringAdd.append("\t\t\tMap<String,Object> claims=new HashMap<>();\n");
        stringAdd.append("\t\t\ttoken = jwtUtils.getAccessToken(claims, username);\n");
        stringAdd.append("\t\t\trefreshToken = jwtUtils.refreshToken(token);\n");
        stringAdd.append("\t\t} catch (DisabledException e) {\n");
        stringAdd.append("\t\t\tthrow new Exception(\"USER_DISABLED\", e);\n");
        stringAdd.append("\t\t} catch (BadCredentialsException e) {\n");
        stringAdd.append("\t\t\tthrow new Exception(\"INVALID_CREDENTIALS\", e);\n");
        stringAdd.append(Constant.TWO_TAB_NEWLINE);
        stringAdd.append("\t\treturn new AuthResponse(token, refreshToken, jwtUtils.getUsernameFromToken(token));\n");
        stringAdd.append(Constant.SINGLE_TAB_NEWLINE);
        stringAdd.append("}");

        return stringAdd.toString( );
    }

    public String createServiceClassSecondVersions(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder( );
        String tableName = commonUtil.toCamelCase(tables.getEnableSecurityDTO( ).getAuthTableName( ));
        String capitalizeTableName = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO( ).getAuthTableName( ));
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(".service;\n\n");
        stringAdd.append(Constant.IMPORT_SERVICE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.AUTH_RESPONSE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.LOGIN_RESPONSE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.MODEL).append(capitalizeTableName).append(Constant.SINGLE_SLASH);


        stringAdd.append(Constant.SERVICE).append("public interface AuthService {\n");
        stringAdd.append("\tLoginResponse login(AuthResponse response) throws Exception;\n");
        stringAdd.append("\n");
        stringAdd.append("    ").append(capitalizeTableName).append(" getUserInfo(String userName) throws Exception;\n");
        stringAdd.append("\n");
        stringAdd.append("\t").append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.MODEL).append(capitalizeTableName).append(" getUser(String username) throws Exception;\n");
        stringAdd.append("}\n");
        return stringAdd.toString( );
    }

    public String jwtUtilAddSecondVersions(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder( );
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(".securityconfig;\n");
        stringAdd.append("import io.jsonwebtoken.Claims;\n");
        stringAdd.append("import io.jsonwebtoken.Jws;\n");
        stringAdd.append("import io.jsonwebtoken.JwtParser;\n");
        stringAdd.append("import io.jsonwebtoken.Jwts;\n");
        stringAdd.append("import io.jsonwebtoken.SignatureAlgorithm;\n");
        stringAdd.append("import org.springframework.beans.factory.annotation.Value;\n");
        stringAdd.append(Constant.IMPORT_AUTHENTICATION_TOKEN);
        stringAdd.append("import org.springframework.security.core.Authentication;\n");
        stringAdd.append("import org.springframework.security.core.GrantedAuthority;\n");
        stringAdd.append("import org.springframework.security.core.authority.SimpleGrantedAuthority;\n");
        stringAdd.append(Constant.IMPORT_USER_DETAILS);
        stringAdd.append(Constant.IMPORT_COMPONENT);
        stringAdd.append("\n");
        stringAdd.append("import java.util.Arrays;\nimport java.util.ArrayList;\n");
        stringAdd.append("import java.util.Date;\n");
        stringAdd.append("import java.util.HashMap;\n");
        stringAdd.append("import java.util.List;\n");
        stringAdd.append("import java.util.Map;\n");
        stringAdd.append("import java.util.function.Function;\n");
        stringAdd.append("import java.util.stream.Collectors;\n\n");
        stringAdd.append(Constant.IMPORT_SERIALIZABLE);
        stringAdd.append(Constant.COMPONENT).append("public class JWTUtils implements Serializable {");
        stringAdd.append("\n");
        stringAdd.append("    private static final long serialVersionUID = -2550185165626007488L;\n");
        stringAdd.append("\n");
        stringAdd.append("    public static final long JWT_TOKEN_VALIDITY = 2 * 60 * 60;\n");
        stringAdd.append("\n");
        stringAdd.append("    public static final long JWT_REFRESH_TOKEN_VALIDITY = 5 * 60 * 60;\n");
        stringAdd.append("\n");
        stringAdd.append("    @Value(\"${jwt.secret}\")\n");
        stringAdd.append("    private String secret;\n");
        stringAdd.append("\n");
        stringAdd.append("    // retrieve username from jwt token\n");
        stringAdd.append("    public String getUsernameFromToken(String token) {\n");
        stringAdd.append("        return getClaimFromToken(token, Claims::getSubject);\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("    // retrieve expiration date from jwt token\n");
        stringAdd.append("    public Date getExpirationDateFromToken(String token) {\n");
        stringAdd.append("        return getClaimFromToken(token, Claims::getExpiration);\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {\n");
        stringAdd.append("        final Claims claims = extractAllClaims(token);\n");
        stringAdd.append("        return claimsResolver.apply(claims);\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("    // for retrieveing any information from token we will need the secret key\n");
        stringAdd.append("    private Claims getAllClaimsFromToken(String token) {\n");
        stringAdd.append("        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("private Claims extractAllClaims(String token) {\n");
        stringAdd.append("        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("    // check if the token has expired\n");
        stringAdd.append("    private Boolean isTokenExpired(String token) {\n");
        stringAdd.append("        final Date expiration = getExpirationDateFromToken(token);\n");
        stringAdd.append("        return expiration.before(new Date());\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("    // validate token\n");
        stringAdd.append("    public Boolean validateToken(String token, UserDetails userDetails) {\n");
        stringAdd.append("        final String username = getUsernameFromToken(token);\n");
        stringAdd.append("        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("    \n");
        stringAdd.append("    public String generateToken(Authentication authentication) {\n");
        stringAdd.append("        final String authorities = authentication.getAuthorities().stream()\n");
        stringAdd.append("                .map(GrantedAuthority::getAuthority)\n");
        stringAdd.append("                .collect(Collectors.joining(\",\"));\n");
        stringAdd.append("        Map<String,Object> claims=new HashMap<>();\n");
        stringAdd.append("        return getAccessToken(claims, authentication.getName());\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("    public String getAccessToken(Map<String, Object> claims, String userName) {\n");
        stringAdd.append("        return Jwts.builder()\n");
        stringAdd.append("                .setClaims(claims)\n");
        stringAdd.append("                .setSubject(userName)\n");
        stringAdd.append("                .setIssuedAt(new Date(System.currentTimeMillis()))\n");
        stringAdd.append("                .setExpiration(new Date(System.currentTimeMillis()+1000*60*30))\n");
        stringAdd.append("                .signWith(SignatureAlgorithm.HS256, secret).compact();\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("  \n");
        stringAdd.append("    public String refreshToken(String token) {\n");
        stringAdd.append("        final Claims claims = extractAllClaims(token);\n");
        stringAdd.append("        claims.setIssuedAt(new Date(System.currentTimeMillis()));\n");
        stringAdd.append("        claims.setExpiration(new Date(System.currentTimeMillis() + JWT_REFRESH_TOKEN_VALIDITY*1000));\n");
        stringAdd.append("\n");
        stringAdd.append("        return Jwts.builder()\n");
        stringAdd.append("                .setClaims(claims).\n");
        stringAdd.append("                 signWith(SignatureAlgorithm.HS512, secret).compact();\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("    public UsernamePasswordAuthenticationToken getAuthentication(final String token, final Authentication existingAuth, final UserDetails userDetails) {\n");
        stringAdd.append("\n");
        stringAdd.append("        final JwtParser jwtParser = Jwts.parser().setSigningKey(secret);\n");
        stringAdd.append("\n");
        stringAdd.append("        final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);\n");
        stringAdd.append("\n");
        stringAdd.append("        final Claims claims = (Claims) claimsJws.getBody();\n");
        stringAdd.append("\n");
        stringAdd.append("\nfinal List<SimpleGrantedAuthority> authorities =new ArrayList<SimpleGrantedAuthority>();");
        stringAdd.append("\n");
        stringAdd.append("        return new UsernamePasswordAuthenticationToken(userDetails, \"\", authorities);\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("}\n");
        return stringAdd.toString( );
    }

    public String loginResponseAddSecondVersions(String packageName, String authTableName) {
        StringBuilder stringAdd = new StringBuilder( );
        String tableName = commonUtil.toCamelCase(authTableName);
        String capitalizeTableName = commonUtil.CapitalizeClassName(authTableName);
        stringAdd.append(Constant.PACKAGE_SPACE).append(packageName).append(Constant.RESPONSE_NEWLINE);
        stringAdd.append(Constant.IMPORT).append(packageName).append(Constant.MODEL).append(capitalizeTableName).append(";\n");
        stringAdd.append(Constant.IMPORT_GETTER);
        stringAdd.append("\n");
        stringAdd.append(Constant.GETTER_NEWLINE);
        stringAdd.append("public class LoginResponse {\n");
        stringAdd.append("    private").append(" ").append(capitalizeTableName).append(" ").append(tableName).append(";\n");
        stringAdd.append("\n");
        stringAdd.append("    private String token;\n");
        stringAdd.append("\n");
        stringAdd.append("    private String refreshToken;\n");
        stringAdd.append("\n");
        stringAdd.append("    public LoginResponse(").append(capitalizeTableName).append(" ").append(tableName).append(", String token, String refreshToken) {\n");
        stringAdd.append("        this.").append(tableName).append(" = ").append(tableName).append(";\n");
        stringAdd.append("        this.token = token;\n");
        stringAdd.append("        this.refreshToken = refreshToken;\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("}\n");
        return stringAdd.toString( );
    }


    public String loginRequestAddSecondVersions(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder( );
        String fieldOne = commonUtil.toCamelCase(tables.getEnableSecurityDTO( ).getFieldOne( ));
        String fieldTwo = commonUtil.toCamelCase(tables.getEnableSecurityDTO( ).getFieldTwo( ));

        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.RESPONSE_NEWLINE);
        stringAdd.append(Constant.IMPORT_GETTER);
        stringAdd.append("import lombok.Setter;\n");
        stringAdd.append("\n");
        stringAdd.append(Constant.GETTER_NEWLINE);
        stringAdd.append("@Setter\n");
        stringAdd.append("public class LoginRequest {\n");
        stringAdd.append("    private String ").append(fieldOne).append(";\n");
        stringAdd.append("\n");
        stringAdd.append("    private String ").append(fieldTwo).append(";\n");
        stringAdd.append("}");

        return stringAdd.toString( );

    }

    public String authResponseAddSecondVersions(String packageName) {
        StringBuilder stringAdd = new StringBuilder( );
        stringAdd.append(Constant.PACKAGE_SPACE).append(packageName).append(Constant.RESPONSE_NEWLINE);
        stringAdd.append(Constant.IMPORT_SERIALIZABLE);
        stringAdd.append("\n");
        stringAdd.append(Constant.IMPORT_GETTER);
        stringAdd.append("import lombok.Setter;\n");
        stringAdd.append("\n");
        stringAdd.append(Constant.GETTER_NEWLINE);
        stringAdd.append("@Setter\n");
        stringAdd.append("public class AuthResponse implements Serializable {\n");
        stringAdd.append("    public AuthResponse(String token, String refreshToken, String user) {\n");
        stringAdd.append("        this.token = token;\n");
        stringAdd.append("        this.user = user;\n");
        stringAdd.append("        this.refreshToken = refreshToken;\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("    private static final long serialVersionUID = 8286210631647330695L;\n");
        stringAdd.append("\n");
        stringAdd.append("    private String user;\n");
        stringAdd.append("    \n");
        stringAdd.append("    private String token;\n");
        stringAdd.append("\n");
        stringAdd.append("    private String refreshToken;\n");
        stringAdd.append("}\n");
        return stringAdd.toString( );


    }

    public String createServiceImplClassSecondVersions(DbDesignTables tables) {
        String fieldOne = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO( ).getFieldOne( ));
        String fieldTwo = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO( ).getFieldTwo( ));
        String tableName = commonUtil.toCamelCase(tables.getEnableSecurityDTO( ).getAuthTableName( ));
        String capitalizeTableName = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO( ).getAuthTableName( ));
        StringBuilder stringAdd = new StringBuilder( );
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(".serviceimpl;\n");
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.AUTH_RESPONSE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.LOGIN_RESPONSE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.AUTH_SERVICE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.MODEL).append(capitalizeTableName).append(Constant.SINGLE_SLASH);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(".repository.").append(capitalizeTableName).append("Repository").append(Constant.SINGLE_SLASH);
        stringAdd.append("import org.modelmapper.ModelMapper;\n");
        stringAdd.append(Constant.IMPORT_AUTOWIRED);
        stringAdd.append(Constant.IMPORT_SERVICE);
        stringAdd.append("\n");
        stringAdd.append("import java.util.Optional;\n\n");
        stringAdd.append(Constant.SERVICE);
        stringAdd.append("public class AuthServiceImpl implements AuthService {\n\n");
        stringAdd.append(Constant.AUTOWIRED_NEWLINE);
        stringAdd.append("\tprivate ").append(capitalizeTableName).append("Repository ").append(tableName).append("Repository").append(";\n");
        stringAdd.append("\t\n");
        stringAdd.append(Constant.AUTOWIRED_NEWLINE);
        stringAdd.append("\tprivate ModelMapper modelMapper;\n\n");

        stringAdd.append(Constant.TAB_OVERRIDE);
        stringAdd.append("\tpublic LoginResponse login(AuthResponse response) throws Exception {\n");
        stringAdd.append("\t\tif (response != null) {\n");
        stringAdd.append("\t\t\tOptional<").append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.MODEL).append(capitalizeTableName).append("> ").append(tableName).append("= " + tableName + Constant.REPO_FIND).append(fieldOne).append("(response.getUser());\n");
        stringAdd.append("\t\t\tif (").append(tableName).append(Constant.IS_EMPTY);
        stringAdd.append("\t\t\t\tthrow new Exception(\"Not Found\");\n");
        stringAdd.append("\t\t\t}\n");
        stringAdd.append("\t\t\treturn new LoginResponse(modelMapper.map(").append(tableName).append(".get(), ").append(capitalizeTableName).append(".class), response.getToken(),\n");
        stringAdd.append("\t\t\t\t\tresponse.getRefreshToken());\n");
        stringAdd.append("\t\t} else\n");
        stringAdd.append("\t\t\tthrow new Exception(\"Login Failed.!\");\n");
        stringAdd.append(Constant.SINGLE_TAB_NEWLINE);
        stringAdd.append("\n");

        stringAdd.append(Constant.TAB_OVERRIDE);
        stringAdd.append("\tpublic ").append(capitalizeTableName).append(" getUserInfo(String email) throws Exception {\n");
        stringAdd.append("\t\tOptional<").append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.MODEL).append(capitalizeTableName).append("> ").append(tableName).append(" = ").append(tableName).append(Constant.REPO_FIND).append(fieldOne).append("(email);\n");
        stringAdd.append("\t\tif (").append(tableName).append(Constant.IS_EMPTY);
        stringAdd.append("\t\t\tthrow new Exception(\"User Not found\");\n");
        stringAdd.append(Constant.TWO_TAB_NEWLINE);
        stringAdd.append("\t\treturn modelMapper.map(").append(tableName).append(".get(), ").append(capitalizeTableName).append(".class);\n");
        stringAdd.append(Constant.SINGLE_TAB_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("\n");

        stringAdd.append(Constant.TAB_OVERRIDE);
        stringAdd.append("\tpublic " + tables.getSpringBootBasicDTO( ).getPackageName( ) + Constant.MODEL + capitalizeTableName + " getUser(String mobile) throws Exception {\n");
        stringAdd.append("\t\tOptional<" + tables.getSpringBootBasicDTO( ).getPackageName( ) + Constant.MODEL + capitalizeTableName + "> " + tableName + "= " + tableName + Constant.REPO_FIND + fieldOne + "(mobile);\n");
        stringAdd.append("\t\tif(" + tableName + Constant.IS_EMPTY);
        stringAdd.append("\t\t\tthrow new Exception(\"Not found\");\n");
        stringAdd.append(Constant.TWO_TAB_NEWLINE);
        stringAdd.append("\t\treturn " + tableName + ".get();\n");
        stringAdd.append(Constant.SINGLE_TAB_NEWLINE);

        stringAdd.append("}\n");
        return stringAdd.toString( );
    }

    public String WebSecurityConfigSecondVersions(DbDesignTables tables) {
        StringBuilder stringBuilder = new StringBuilder( );
        stringBuilder.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(".securityconfig;\n");
        stringBuilder.append(Constant.IMPORT_AUTOWIRED);
        stringBuilder.append("import org.springframework.context.annotation.Bean;\n");
        stringBuilder.append("import org.springframework.context.annotation.Configuration;\n");
        stringBuilder.append("import org.springframework.security.authentication.AuthenticationManager;\n");
        stringBuilder.append("import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;\n");
        stringBuilder.append("import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;\n");
        stringBuilder.append("import org.springframework.security.config.annotation.web.builders.HttpSecurity;\n");
        stringBuilder.append("import org.springframework.security.config.annotation.web.builders.WebSecurity;\n");
        stringBuilder.append("import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;\n");
        stringBuilder.append("import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;\n");
        stringBuilder.append("import org.springframework.security.config.http.SessionCreationPolicy;\n");
        stringBuilder.append("import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;\n");
        stringBuilder.append("import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;\n");
        stringBuilder.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(".constant").append(".Constant;\n");
        stringBuilder.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.SECURITYCONFIG).append(".JwtAuthenticationEntryPoint;\n");
        stringBuilder.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.SECURITYCONFIG).append(".JwtRequestFilter;\n");
        stringBuilder.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.SECURITYCONFIG).append(".JwtUserDetailsService;\n");
        stringBuilder.append("@Configuration\n");
        stringBuilder.append("@EnableWebSecurity\n");
        stringBuilder.append("@EnableGlobalMethodSecurity(prePostEnabled = true)\n");

        stringBuilder.append(" public class ").append("WebSecurityConfig").append(" extends ").append(" WebSecurityConfigurerAdapter").append("{\n\n");
        stringBuilder.append(Constant.AUTOWIRED);
        stringBuilder.append("    private JwtUserDetailsService jwtUserDetailsService;\n");
        stringBuilder.append(Constant.AUTOWIRED);
        stringBuilder.append("    private JwtRequestFilter jwtRequestFilter;\n");
        stringBuilder.append(Constant.AUTOWIRED);
        stringBuilder.append("    private JwtAuthenticationEntryPoint entryPoint;\n");
        stringBuilder.append("\n");
        stringBuilder.append(Constant.AUTOWIRED);
        stringBuilder.append("    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {\n");
        stringBuilder.append("\n");
        stringBuilder.append("        auth.userDetailsService(jwtUserDetailsService)\n");
        stringBuilder.append("                .passwordEncoder(new BCryptPasswordEncoder( ));\n");
        stringBuilder.append(Constant.SPACE_CURLY_NEWLINE);
        stringBuilder.append("\n");
        stringBuilder.append("    @Bean\n");
        stringBuilder.append(Constant.OVERRIDE);
        stringBuilder.append("    public AuthenticationManager authenticationManagerBean() throws Exception {\n");
        stringBuilder.append("        return super.authenticationManagerBean( );\n");
        stringBuilder.append(Constant.SPACE_CURLY_NEWLINE);
        stringBuilder.append("\n");
        stringBuilder.append(Constant.OVERRIDE);
        stringBuilder.append("    protected void configure(HttpSecurity httpSecurity) throws Exception {\n");
        stringBuilder.append("        httpSecurity.csrf( ).disable( )\n");
        stringBuilder.append(Constant.SPACE_NEWLINE_TWO);
        stringBuilder.append("                 * don't authenticate this particular request\n");
        stringBuilder.append(Constant.SPACE_NEWLINE_THREE);
        stringBuilder.append("                .authorizeRequests( ).antMatchers(Constant.LOGIN,\n");
        stringBuilder.append("                        Constant.REFRESH,\n");
        stringBuilder.append("                        \"/swagger-resources/**\",\n");
        stringBuilder.append("                        \"/swagger-ui/**\",\n");
        stringBuilder.append("                        \"/v2/api-docs\",\n");
        stringBuilder.append("                        \"/v3/api-docs/**\",\n");
        stringBuilder.append("                        \"/webjars/**\")\n");
        stringBuilder.append("                .permitAll( ).\n");
        stringBuilder.append(Constant.SPACE_NEWLINE_TWO);
        stringBuilder.append("                 * all other requests need to be authenticated\n");
        stringBuilder.append(Constant.SPACE_NEWLINE_THREE);
        stringBuilder.append("                        anyRequest( ).authenticated( ).and( ).\n");
        stringBuilder.append(Constant.SPACE_NEWLINE_TWO);
        stringBuilder.append("                 * make sure we use state less session; session won't be used to store user's\n");
        stringBuilder.append("                 * state\n");
        stringBuilder.append(Constant.SPACE_NEWLINE_THREE);
        stringBuilder.append("                        exceptionHandling( ).authenticationEntryPoint(entryPoint).and( ).sessionManagement( )\n");
        stringBuilder.append("                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);\n");
        stringBuilder.append("\n");
        stringBuilder.append("        // Add a filter to validate the tokens with every request\n");
        stringBuilder.append("        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);\n");
        stringBuilder.append("\n");
        stringBuilder.append("        httpSecurity.cors( );\n");
        stringBuilder.append("\n");
        stringBuilder.append(Constant.SPACE_CURLY_NEWLINE);
        stringBuilder.append("\n");
        stringBuilder.append("\n");
        stringBuilder.append(Constant.OVERRIDE);
        stringBuilder.append("    public void configure(WebSecurity web) throws Exception {\n");
        stringBuilder.append("        web.ignoring( ).antMatchers(\"/swagger*/**\", \"/configuration/**\", \"/v2/api-docs\");\n");
        stringBuilder.append(Constant.SPACE_CURLY_NEWLINE);
        stringBuilder.append("}");
        return stringBuilder.toString( );
    }

    public String ConstantSecondVersions(String packageName) {
        StringBuilder stringBuilder = new StringBuilder( );
        stringBuilder.append(" package ").append(packageName).append(".constant;\n\n");
        stringBuilder.append("public class Constant").append("{\n\n");
        stringBuilder.append("\t\t/** JWT INFO */\n");
        stringBuilder.append("\t\tpublic static final String HEADER = \"Authorization\";\n");
        stringBuilder.append("\t\t public static final String TOKEN_PREFIX = \"Bearer \";\n");
        stringBuilder.append("\t\tpublic final static String NOT_FOUND = \"Record Not Found..!\";\n");
        stringBuilder.append("\t\tpublic final static String TOKEN_ERROR = \"Unable to get JWT Token..!\";\n");
        stringBuilder.append("\t\tpublic final static String EXPIRED = \"JWT Token has expired\";\n");
        stringBuilder.append("\t\tpublic final static String PREFIX_MISSED = \"JWT Token does not begin with Bearer String\";\n");
        stringBuilder.append("\t\t/** CONTROLLER INFO */\n");
        stringBuilder.append("\t\tpublic static final String USER = \"/user\";\n");
        stringBuilder.append("\t\tpublic static final String SM = \"/sm\";\n");
        stringBuilder.append("\t\tpublic static final String QUESTION_TRACKER = \"/question/tracker\";\n");
        stringBuilder.append("\t\tpublic static final String MASTER_DATA = \"/master/data\";\n");
        stringBuilder.append("\t\t/** URL INFO */\n");
        stringBuilder.append("\t\tpublic static final String ID = \"/{id}\";\n");
        stringBuilder.append("\t\tpublic static final String END = \"/\";\n");
        stringBuilder.append("\t\tpublic static final String DATE = \"/date\";\n");
        stringBuilder.append("\t\tpublic static final String ALL = \"/all\";\n");
        stringBuilder.append("\t\tpublic static final String STATUS = \"/status\";\n");
        stringBuilder.append("\t\tpublic static final String ROLE = \"/role\";\n");
        stringBuilder.append("\t\tpublic static final String BY_VSO = \"/vso\";\n");
        stringBuilder.append("\t\tpublic static final String BY_SM = \"/sm\";\n");
        stringBuilder.append("\t\tpublic static final String SUBMIT = \"/submit\";\n");
        stringBuilder.append("\t\tpublic static final String LOGIN = \"/login\";\n");
        stringBuilder.append("\t\tpublic static final String REFRESH = \"/refresh\";\n");

        stringBuilder.append("}\n\n");
        return stringBuilder.toString( );

    }

    public String jwtUserDetailServiceAddSecondVersions(DbDesignTables tables) {
        String fieldOne = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO( ).getFieldOne( ));
        String fieldTwo = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO( ).getFieldTwo( ));
        String tableName = commonUtil.toCamelCase(tables.getEnableSecurityDTO( ).getAuthTableName( ));
        String capitalizeTableName = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO( ).getAuthTableName( ));
        StringBuilder stringAdd = new StringBuilder( );
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.SECURITY_CONFIG_NEWLINE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.AUTH_SERVICE);
        stringAdd.append(Constant.IMPORT_AUTOWIRED);
        stringAdd.append("import org.springframework.security.core.GrantedAuthority;\n");
        stringAdd.append("import org.springframework.security.core.userdetails.User;\n");
        stringAdd.append(Constant.IMPORT_USER_DETAILS);
        stringAdd.append("import org.springframework.security.core.userdetails.UserDetailsService;\n");
        stringAdd.append("import org.springframework.security.core.userdetails.UsernameNotFoundException;\n");
        stringAdd.append(Constant.IMPORT_SERVICE);
        stringAdd.append("\n");
        stringAdd.append("import java.util.ArrayList;\n");
        stringAdd.append("import java.util.List;\n");
        stringAdd.append("\n");
        stringAdd.append(Constant.SERVICE);
        stringAdd.append("public class JwtUserDetailsService implements UserDetailsService {\n");
        stringAdd.append(Constant.AUTOWIRED);
        stringAdd.append("    private AuthService authService;\n");
        stringAdd.append("\n");
        stringAdd.append("\n");
        stringAdd.append(Constant.OVERRIDE);
        stringAdd.append("    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {\n");
        stringAdd.append("        " + tables.getSpringBootBasicDTO( ).getPackageName( ) + Constant.MODEL + capitalizeTableName + " " + tableName + " = null;\n");
        stringAdd.append("        \n");
        stringAdd.append("        try {\n");
        stringAdd.append("            " + tableName + " = authService.getUser(username);\n");
        stringAdd.append("        } catch (Exception e) {\n");
        stringAdd.append("            throw new RuntimeException(e);\n");
        stringAdd.append(Constant.NEW_LINE);
        stringAdd.append("        List<GrantedAuthority> listRole = new ArrayList<GrantedAuthority>();\n");
        stringAdd.append("\n");
        stringAdd.append("        return new User(" + tableName + ".get" + fieldOne + "(), " + tableName + ".get" + fieldTwo + "(), listRole);\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("}\n");
        return stringAdd.toString( );
    }

    public String jwtRequestFilterServiceAddSecondVersions(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder( );
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.SECURITY_CONFIG_NEWLINE);
        stringAdd.append("import io.jsonwebtoken.ExpiredJwtException;\n");
        stringAdd.append("import javax.servlet.FilterChain;\n");
        stringAdd.append("import javax.servlet.ServletException;\n");
        stringAdd.append("import javax.servlet.http.HttpServletRequest;\n");
        stringAdd.append("import javax.servlet.http.HttpServletResponse;\n");
        stringAdd.append(Constant.IMPORT_AUTOWIRED);
        stringAdd.append(Constant.IMPORT_AUTHENTICATION_TOKEN);
        stringAdd.append("import org.springframework.security.core.context.SecurityContextHolder;\n");
        stringAdd.append(Constant.IMPORT_USER_DETAILS);
        stringAdd.append("import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;\n");
        stringAdd.append(Constant.IMPORT_COMPONENT);
        stringAdd.append("import org.springframework.web.filter.OncePerRequestFilter;\n");
        stringAdd.append("\n");
        stringAdd.append("import java.io.IOException;\n");
        stringAdd.append("\n");
        stringAdd.append(Constant.COMPONENT);
        stringAdd.append("public class JwtRequestFilter extends OncePerRequestFilter {\n");
        stringAdd.append(Constant.AUTOWIRED);
        stringAdd.append("    private JwtUserDetailsService jwtUserDetailsService;\n");
        stringAdd.append("\n");
        stringAdd.append(Constant.AUTOWIRED);
        stringAdd.append("    private JWTUtils jwtTokenUtil;\n");
        stringAdd.append("\n");
        stringAdd.append(Constant.OVERRIDE);
        stringAdd.append("    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)\n");
        stringAdd.append("            throws ServletException, IOException {\n");
        stringAdd.append("        final String requestTokenHeader = request.getHeader(\"Authorization\");\n");
        stringAdd.append("\n");
        stringAdd.append("        String username = null;\n");
        stringAdd.append("        String jwtToken = null;\n");
        stringAdd.append("\n");
        stringAdd.append("\n");
        stringAdd.append("        /*\n");
        stringAdd.append("         * JWT Token is in the form \"Bearer token\". Remove Bearer word and get only the\n");
        stringAdd.append("         * Token\n");
        stringAdd.append("         */\n");
        stringAdd.append("        if (requestTokenHeader != null && requestTokenHeader.startsWith(\"Bearer \")) {\n");
        stringAdd.append("            jwtToken = requestTokenHeader.replace(\"Bearer \", \"\");\n");
        stringAdd.append("            try {\n");
        stringAdd.append("                username = jwtTokenUtil.getUsernameFromToken(jwtToken);\n");
        stringAdd.append("            } catch (IllegalArgumentException e) {\n");
        stringAdd.append("                logger.warn(\"Unable to get JWT Token..!\");\n");
        stringAdd.append("            } catch (ExpiredJwtException e) {\n");
        stringAdd.append("                logger.warn(\"JWT Token has expired\");\n");
        stringAdd.append("            }\n");
        stringAdd.append("        } else {\n");
        stringAdd.append("            logger.warn(\"JWT Token does not begin with Bearer String\");\n");
        stringAdd.append(Constant.NEW_LINE);
        stringAdd.append("\n");
        stringAdd.append("        /* Once we get the token validate it */\n");
        stringAdd.append("        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {\n");
        stringAdd.append("\n");
        stringAdd.append("            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);\n");
        stringAdd.append("\n");
        stringAdd.append("            /*\n");
        stringAdd.append("             * if token is valid configure Spring Security to manually set authentication\n");
        stringAdd.append("             */\n");
        stringAdd.append("            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {\n");
        stringAdd.append("\n");
        stringAdd.append("            \tUsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = jwtTokenUtil\n");
        stringAdd.append("\t\t\t\t\t\t.getAuthentication(jwtToken, SecurityContextHolder.getContext().getAuthentication(),\n");
        stringAdd.append("\t\t\t\t\t\t\t\tuserDetails);\n");
        stringAdd.append("                usernamePasswordAuthenticationToken\n");
        stringAdd.append("                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));\n");
        stringAdd.append(Constant.SPACE_NEWLINE_TWO);
        stringAdd.append("                 * After setting the Authentication in the context, we specify that the current\n");
        stringAdd.append("                 * user is authenticated. So it passes the Spring Security Configurations\n");
        stringAdd.append("                 * successfully.\n");
        stringAdd.append(Constant.SPACE_NEWLINE_THREE);
        stringAdd.append("                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);\n");
        stringAdd.append("            }\n");
        stringAdd.append(Constant.NEW_LINE);
        stringAdd.append("        filterChain.doFilter(request, response);\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("\n");
        stringAdd.append("}\n");
        return stringAdd.toString( );
    }

    public String jwtAuthenticationEntryPointAddSecondVersions(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder( );
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.SECURITY_CONFIG_NEWLINE);
        stringAdd.append("import org.springframework.security.core.AuthenticationException;\n");
        stringAdd.append("import org.springframework.security.web.AuthenticationEntryPoint;\n");
        stringAdd.append(Constant.IMPORT_COMPONENT);
        stringAdd.append("\n");
        stringAdd.append("import javax.servlet.ServletException;\n");
        stringAdd.append("import javax.servlet.http.HttpServletRequest;\n");
        stringAdd.append("import javax.servlet.http.HttpServletResponse;\n");
        stringAdd.append("import java.io.IOException;\n");
        stringAdd.append(Constant.IMPORT_SERIALIZABLE);
        stringAdd.append("\n");
        stringAdd.append(Constant.COMPONENT);
        stringAdd.append("public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {\n");
        stringAdd.append("    private static final long serialVersionUID = -521401304750789166L;\n");
        stringAdd.append("\n");
        stringAdd.append(Constant.OVERRIDE);
        stringAdd.append("    public void commence(HttpServletRequest request, HttpServletResponse response,\n");
        stringAdd.append("                         AuthenticationException authException) throws IOException, ServletException {\n");
        stringAdd.append("        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, \"Unauthorized\");\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("}\n");
        return stringAdd.toString( );
    }

}
