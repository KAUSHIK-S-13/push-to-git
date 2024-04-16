package com.d2d.util;

import com.d2d.constant.Constant;
import com.d2d.dto.DbDesignTables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtCreatorVThreeUtil {

    @Autowired
    CommonUtil commonUtil;

    public String createControllerClass(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder();
        String fieldOne = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO().getFieldOne());
        String fieldTwo = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO().getFieldTwo());
        String tableName = commonUtil.toCamelCase(tables.getEnableSecurityDTO().getAuthTableName());
        String capitalizeTableName = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO().getAuthTableName());
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO().getPackageName()).append(".controller;\n");
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO().getPackageName()).append(".securityconfig.JWTUtils;\n");
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.AUTH_RESPONSE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.LOGIN_RESPONSE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO().getPackageName()).append(".response.LoginRequest;\n");
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.AUTH_SERVICE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.MODEL).append(capitalizeTableName).append(";\n");

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

        stringAdd.append("@PostMapping(\"/login\")\n");
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

        return stringAdd.toString();
    }

    public String jwtUtilAdd(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder();
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.SECURITY_CONFIG_NEWLINE);
        stringAdd.append("import io.jsonwebtoken.Claims;\n");
        stringAdd.append("import io.jsonwebtoken.Jws;\n");
        stringAdd.append("import io.jsonwebtoken.JwtParser;\n");
        stringAdd.append("import io.jsonwebtoken.Jwts;\n");
        stringAdd.append("import io.jsonwebtoken.SignatureAlgorithm;\n");
        stringAdd.append("import io.jsonwebtoken.io.Decoders;\n");
        stringAdd.append("import io.jsonwebtoken.security.Keys;\n");
        stringAdd.append("import org.springframework.beans.factory.annotation.Value;\n");
        stringAdd.append(Constant.IMPORT_AUTHENTICATION_TOKEN);
        stringAdd.append("import org.springframework.security.core.Authentication;\n");
        stringAdd.append("import org.springframework.security.core.GrantedAuthority;\n");
        stringAdd.append("import org.springframework.security.core.authority.SimpleGrantedAuthority;\n");
        stringAdd.append(Constant.IMPORT_USER_DETAILS);
        stringAdd.append(Constant.IMPORT_COMPONENT);
        stringAdd.append("\n");
        stringAdd.append(Constant.IMPORT_SERIALIZABLE);
        stringAdd.append("import java.security.Key;\n");
        stringAdd.append("import java.util.Arrays;\nimport java.util.ArrayList;\n");
        stringAdd.append("import java.util.Date;\n");
        stringAdd.append("import java.util.HashMap;\n");
        stringAdd.append("import java.util.List;\n");
        stringAdd.append("import java.util.Map;\n");
        stringAdd.append("import java.util.function.Function;\n");
        stringAdd.append("import java.util.stream.Collectors;\n\n");
        stringAdd.append("@Component\n").append("public class JWTUtils implements Serializable {");
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
        stringAdd.append("        return Jwts.parser().setSigningKey(getSignKey()).parseClaimsJws(token).getBody();\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("    private Claims extractAllClaims(String token) {\n");
        stringAdd.append("        return Jwts\n");
        stringAdd.append("                .parserBuilder()\n");
        stringAdd.append("                .setSigningKey(getSignKey())\n");
        stringAdd.append("                .build()\n");
        stringAdd.append("                .parseClaimsJws(token)\n");
        stringAdd.append("                .getBody();\n");
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
        stringAdd.append("                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("    private Key getSignKey() {\n");
        stringAdd.append("        byte[] keyBytes= Decoders.BASE64.decode(secret);\n");
        stringAdd.append("        return Keys.hmacShaKeyFor(keyBytes);\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("  \n");
        stringAdd.append("    public String refreshToken(String token) {\n");
        stringAdd.append("        final Claims claims = extractAllClaims(token);\n");
        stringAdd.append("        claims.setIssuedAt(new Date(System.currentTimeMillis()));\n");
        stringAdd.append("        claims.setExpiration(new Date(System.currentTimeMillis() + JWT_REFRESH_TOKEN_VALIDITY*1000));\n");
        stringAdd.append("\n");
        stringAdd.append("        return Jwts.builder()\n");
        stringAdd.append("                .setClaims(claims)\n");
        stringAdd.append("                .signWith(getSignKey(), SignatureAlgorithm.HS512)\n");
        stringAdd.append("                .compact();\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("    public UsernamePasswordAuthenticationToken getAuthentication(final String token, final Authentication existingAuth, final UserDetails userDetails) {\n");
        stringAdd.append("\n");
        stringAdd.append("        final JwtParser jwtParser = Jwts.parser().setSigningKey(getSignKey());\n");
        stringAdd.append("\n");
        stringAdd.append("        final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);\n");
        stringAdd.append("\n");
        stringAdd.append("        final Claims claims = (Claims) claimsJws.getBody();\n");
        stringAdd.append("\nfinal List<SimpleGrantedAuthority> authorities =new ArrayList<SimpleGrantedAuthority>();");
        stringAdd.append("\n");
        stringAdd.append("        return new UsernamePasswordAuthenticationToken(userDetails, \"\", authorities);\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("}\n");
        return stringAdd.toString();
    }

    public String authResponseAdd(String packageName) {
        StringBuilder stringAdd = new StringBuilder();
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
        return stringAdd.toString();
    }

    public String loginResponseAdd(String packageName, String authTableName) {
        StringBuilder stringAdd = new StringBuilder();
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
        return stringAdd.toString();
    }


    public String loginRequestAdd(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder();
        String fieldOne = commonUtil.toCamelCase(tables.getEnableSecurityDTO().getFieldOne());
        String fieldTwo = commonUtil.toCamelCase(tables.getEnableSecurityDTO().getFieldTwo());

        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.RESPONSE_NEWLINE);
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

        return stringAdd.toString();

    }

    public String createServiceClass(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder();
        String tableName = commonUtil.toCamelCase(tables.getEnableSecurityDTO().getAuthTableName());
        String capitalizeTableName = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO().getAuthTableName());
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO().getPackageName()).append(".service;\n\n");
        stringAdd.append(Constant.IMPORT_SERVICE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.AUTH_RESPONSE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.LOGIN_RESPONSE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.MODEL).append(capitalizeTableName).append(Constant.SINGLE_SLASH);


        stringAdd.append(Constant.SERVICE).append("public interface AuthService {\n");
        stringAdd.append("\tLoginResponse login(AuthResponse response) throws Exception;\n");
        stringAdd.append("\n");
        stringAdd.append("    ").append(capitalizeTableName).append(" getUserInfo(String userName) throws Exception;\n");
        stringAdd.append("\n");
        stringAdd.append("\t").append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.MODEL).append(capitalizeTableName).append(" getUser(String username) throws Exception;\n");
        stringAdd.append("}\n");
        return stringAdd.toString();
    }

    public String createServiceImplClass(DbDesignTables tables) {
        String fieldOne = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO().getFieldOne());
        String fieldTwo = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO().getFieldTwo());
        String tableName = commonUtil.toCamelCase(tables.getEnableSecurityDTO().getAuthTableName());
        String capitalizeTableName = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO().getAuthTableName());
        StringBuilder stringAdd = new StringBuilder();
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO().getPackageName()).append(".serviceimpl;\n");
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.AUTH_RESPONSE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.LOGIN_RESPONSE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.AUTH_SERVICE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.MODEL).append(capitalizeTableName).append(Constant.SINGLE_SLASH);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO().getPackageName()).append(".repository.").append(capitalizeTableName).append("Repository").append(Constant.SINGLE_SLASH);
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
        stringAdd.append("\t\t\tOptional<").append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.MODEL).append(capitalizeTableName).append("> ").append(tableName).append("= " + tableName + Constant.REPO_FIND).append(fieldOne).append("(response.getUser());\n");
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
        stringAdd.append("\t\tOptional<").append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.MODEL).append(capitalizeTableName).append("> ").append(tableName).append(" = ").append(tableName).append(Constant.REPO_FIND).append(fieldOne).append("(email);\n");
        stringAdd.append("\t\tif (").append(tableName).append(Constant.IS_EMPTY);
        stringAdd.append("\t\t\tthrow new Exception(\"User Not found\");\n");
        stringAdd.append(Constant.TWO_TAB_NEWLINE);
        stringAdd.append("\t\treturn modelMapper.map(").append(tableName).append(".get(), ").append(capitalizeTableName).append(".class);\n");
        stringAdd.append(Constant.SINGLE_TAB_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("\n");

        stringAdd.append(Constant.TAB_OVERRIDE);
        stringAdd.append("\tpublic " + tables.getSpringBootBasicDTO().getPackageName() + Constant.MODEL + capitalizeTableName + " getUser(String mobile) throws Exception {\n");
        stringAdd.append("\t\tOptional<" + tables.getSpringBootBasicDTO().getPackageName() + Constant.MODEL + capitalizeTableName + "> " + tableName + "= " + tableName + Constant.REPO_FIND + fieldOne + "(mobile);\n");
        stringAdd.append("\t\tif(" + tableName + Constant.IS_EMPTY);
        stringAdd.append("\t\t\tthrow new Exception(\"Not found\");\n");
        stringAdd.append(Constant.TWO_TAB_NEWLINE);
        stringAdd.append("\t\treturn " + tableName + ".get();\n");
        stringAdd.append(Constant.SINGLE_TAB_NEWLINE);

        stringAdd.append("}\n");
        return stringAdd.toString();
    }

    public String webSecurityConfigAdd(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder();
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO().getPackageName()).append(".securityconfig;\n");
        stringAdd.append(Constant.IMPORT_AUTOWIRED);
        stringAdd.append("import org.springframework.context.annotation.Bean;\n");
        stringAdd.append("import org.springframework.context.annotation.Configuration;\n");
        stringAdd.append("import org.springframework.security.authentication.AuthenticationManager;\n");
        stringAdd.append("import org.springframework.security.authentication.AuthenticationProvider;\n");
        stringAdd.append("import org.springframework.security.authentication.dao.DaoAuthenticationProvider;\n");
        stringAdd.append("import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;\n");
        stringAdd.append("import org.springframework.security.config.annotation.web.builders.HttpSecurity;\n");
        stringAdd.append("import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;\n");
        stringAdd.append("import org.springframework.security.config.http.SessionCreationPolicy;\n");
        stringAdd.append("import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;\n");
        stringAdd.append("import org.springframework.security.crypto.password.PasswordEncoder;\n");
        stringAdd.append("import org.springframework.security.web.SecurityFilterChain;\nimport org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;\n");
        stringAdd.append("import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;\n");
        stringAdd.append("\n");
        stringAdd.append("@Configuration\n");
        stringAdd.append("@EnableWebSecurity\n");
        stringAdd.append("public class WebSecurityConfig {\n");
        stringAdd.append(Constant.AUTOWIRED);
        stringAdd.append("    private JwtUserDetailsService jwtUserDetailsService;\n");
        stringAdd.append("\n");
        stringAdd.append(Constant.AUTOWIRED);
        stringAdd.append("    private JwtRequestFilter jwtRequestFilter;\n");
        stringAdd.append("\n");
        stringAdd.append(Constant.AUTOWIRED);
        stringAdd.append("    private AuthenticationProvider authenticationProvider;\n\n");
        stringAdd.append(Constant.BEAN);
        stringAdd.append("    public PasswordEncoder passwordEncoder() {\n");
        stringAdd.append("        return new BCryptPasswordEncoder();\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append(Constant.BEAN);
        stringAdd.append("    public AuthenticationProvider authenticationProvider() {\n");
        stringAdd.append("        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();\n");
        stringAdd.append("        authProvider.setUserDetailsService(jwtUserDetailsService);\n");
        stringAdd.append("        authProvider.setPasswordEncoder(passwordEncoder());\n");
        stringAdd.append("        return authProvider;\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append(Constant.BEAN);
        stringAdd.append("    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {\n");
        stringAdd.append("\t\t  http.csrf(csrf -> csrf.disable())\n" +
                "                .authorizeHttpRequests(accessManagement -> accessManagement\n" +
                "                        .requestMatchers(\"/api/v1/auth/**\",\n" +
                "                                \"/login\",\n" +
                "                                \"/refresh\",\n" +
                "                                \"/v3/api-docs/swagger-config\",\n" +
                "                                \"/configuration\",\n" +
                "                                \"/swagger-ui/index.html\",\n" +
                "                                \"/swagger-ui/swagger-ui.css\",\n" +
                "                                \"/swagger-ui/index.css\",\n" +
                "                                \"/swagger-ui/swagger-ui-bundle.js\",\n" +
                "                                \"/swagger-ui/swagger-ui-standalone-preset.js\",\n" +
                "                                \"/swagger-ui/favicon-32x32.png\",\n" +
                "                                \"/swagger-ui/favicon-16x16.png\",\n" +
                "                                \"/swagger-resources/**\",\n" +
                "                                \"/swagger-ui/**\",\n" +
                "                                \"/v2/api-docs\",\n" +
                "                                \"/v3/api-docs/**\",\n" +
                "                                \"/webjars/**\").permitAll()\n" +
                "                        .anyRequest().authenticated()\n" +
                "                ).sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))\n" +
                "                .authenticationProvider(authenticationProvider)\n" +
                "                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);\n" +
                "\n" +
                "        return http.build();\n" +
                "    }\n\n ");
        stringAdd.append(Constant.BEAN);
        stringAdd.append("    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)\n");
        stringAdd.append("            throws Exception {\n");
        stringAdd.append("        return authenticationConfiguration.getAuthenticationManager();\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append(" @Autowired\n");
        stringAdd.append("    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {\n");
        stringAdd.append("        auth.userDetailsService(jwtUserDetailsService)\n");
        stringAdd.append("                .passwordEncoder(new BCryptPasswordEncoder());\n");
        stringAdd.append(" }");
        stringAdd.append("}");

        return stringAdd.toString();
    }

    public String jwtUserDetailServiceAdd(DbDesignTables tables) {
        String fieldOne = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO().getFieldOne());
        String fieldTwo = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO().getFieldTwo());
        String tableName = commonUtil.toCamelCase(tables.getEnableSecurityDTO().getAuthTableName());
        String capitalizeTableName = commonUtil.CapitalizeClassName(tables.getEnableSecurityDTO().getAuthTableName());
        StringBuilder stringAdd = new StringBuilder();
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.SECURITY_CONFIG_NEWLINE);
        stringAdd.append(Constant.IMPORT).append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.AUTH_SERVICE);
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
        stringAdd.append("        " + tables.getSpringBootBasicDTO().getPackageName() + Constant.MODEL + capitalizeTableName + " " + tableName + " = null;\n");
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
        return stringAdd.toString();
    }

    public String jwtRequestFilterServiceAdd(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder();
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.SECURITY_CONFIG_NEWLINE);
        stringAdd.append("import io.jsonwebtoken.ExpiredJwtException;\n");
        stringAdd.append("import jakarta.servlet.FilterChain;\n");
        stringAdd.append("import jakarta.servlet.ServletException;\n");
        stringAdd.append("import jakarta.servlet.http.HttpServletRequest;\n");
        stringAdd.append("import jakarta.servlet.http.HttpServletResponse;\n");
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
        stringAdd.append("@Component\n");
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
        stringAdd.append("                /*\n");
        stringAdd.append("                 * After setting the Authentication in the context, we specify that the current\n");
        stringAdd.append("                 * user is authenticated. So it passes the Spring Security Configurations\n");
        stringAdd.append("                 * successfully.\n");
        stringAdd.append("                 */\n");
        stringAdd.append("                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);\n");
        stringAdd.append("            }\n");
        stringAdd.append(Constant.NEW_LINE);
        stringAdd.append("        filterChain.doFilter(request, response);\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("\n");
        stringAdd.append("}\n");
        return stringAdd.toString();
    }

    public String jwtAuthenticationEntryPointAdd(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder();
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO().getPackageName()).append(Constant.SECURITY_CONFIG_NEWLINE);
        stringAdd.append("import org.springframework.security.core.AuthenticationException;\n");
        stringAdd.append("import org.springframework.security.web.AuthenticationEntryPoint;\n");
        stringAdd.append(Constant.IMPORT_COMPONENT);
        stringAdd.append("\n");
        stringAdd.append("import jakarta.servlet.ServletException;\n");
        stringAdd.append("import jakarta.servlet.http.HttpServletRequest;\n");
        stringAdd.append("import jakarta.servlet.http.HttpServletResponse;\n");
        stringAdd.append("import java.io.IOException;\n");
        stringAdd.append(Constant.IMPORT_SERIALIZABLE);
        stringAdd.append("\n");
        stringAdd.append(Constant.COMPONENT);
        stringAdd.append("public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {\n");
        stringAdd.append("    private static final long serialVersionUID = -521401304750789166L;\n");
        stringAdd.append("\n");
        stringAdd.append(Constant.OVERRIDE);
        stringAdd.append("    public void commence(HttpServletRequest request, HttpServletResponse response,\n");
        stringAdd.append("            AuthenticationException authException) throws IOException, ServletException {\n");
        stringAdd.append("        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, \"Unauthorized\");\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("}\n");
        return stringAdd.toString();
    }
}
