package com.d2d.util;

import com.d2d.constant.Constant;
import com.d2d.dto.DbDesignTables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author sekhar
 */
@Component
public class OauthCreatorUtil {

    @Autowired
    CommonUtil commonUtil;


    /*OAUTH*/
    public String OAuth2Config(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder( );
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.OAUTH_CONFIG);
        stringAdd.append(Constant.IMPORT_AUTOWIRED);
        stringAdd.append("import org.springframework.beans.factory.annotation.Qualifier;\n");
        stringAdd.append("import org.springframework.context.annotation.Bean;\n");
        stringAdd.append("import org.springframework.beans.factory.annotation.Value;\n");
        stringAdd.append("import org.springframework.context.annotation.Configuration;\n");
        stringAdd.append("import org.springframework.security.authentication.AuthenticationManager;\n");
        stringAdd.append("import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;\n");
        stringAdd.append("import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;\n");
        stringAdd.append("import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;\n");
        stringAdd.append("import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;\n");
        stringAdd.append("import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;\n");
        stringAdd.append("import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;\n");
        stringAdd.append("import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;\n");
        stringAdd.append("import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;\n");
        stringAdd.append("\n");
        stringAdd.append("@Configuration\n");
        stringAdd.append("@EnableAuthorizationServer\n");
        stringAdd.append("public class OAuth2Config extends AuthorizationServerConfigurerAdapter {\n");
        stringAdd.append("    @Value(\"${app.clientId}\")\n");
        stringAdd.append("    private String clientid;\n");
        stringAdd.append("    @Value(\"${app.clientSecret}\")\n");
        stringAdd.append("    private String clientSecret;\n");
        stringAdd.append("    private String privateKey = \"-----BEGIN RSA PRIVATE KEY-----\\n\" +\n");
        stringAdd.append("            \"MIIEpAIBAAKCAQEAzl+XrIs22JIWi8+q+Jbz7xO7O8Mclq7tY4gjgS1RSM0SBJit\\n\" +\n");
        stringAdd.append("            \"3iZi9gAd984kYzDY3dExMMXuK9er0G2TZf0pH8vqQrwmOJlMvJ6yIHjGdRZBrKjm\\n\" +\n");
        stringAdd.append("            \"m8ALs37mWoqra86iMgYNyHKR14KZhNVzSaakngAXHJ1FAUI4kWH4S6KOgLIKyUYc\\n\" +\n");
        stringAdd.append("            \"GVvdtjShd4Ph5Gyj2fouu9OREJ8qRa3ILB73EIPzuanFoHnO8r0dN/JLE5wXQ6tO\\n\" +\n");
        stringAdd.append("            \"A2MKEe6aKQgjSDSVgrREjQt8cCxRMaw3jjT/G0r9fGFWMhkF4vqQlKuZuNdnIDEF\\n\" +\n");
        stringAdd.append("            \"WFB2TTzcfxAsYKhM52kt1GZtQT6JAn9dT9xAKQIDAQABAoIBAQCqNSU/ZcJidHqr\\n\" +\n");
        stringAdd.append("            \"7ScxyC6ALGHsNEvL65JW2N2PmPTFen011UbxRe+wvP/6dcaFGLCEohhUBasd9c77\\n\" +\n");
        stringAdd.append("            \"JKTBJT9ZvjEDwXI0lBs6Jhj7tyDXSPf5k4Q/SPFdpXgPjPc8/zu+zkdn3zZx7ID5\\n\" +\n");
        stringAdd.append("            \"kaJ4SDzVI3XsX7meBCPrpEXI+9VURgaEs7Bfwu13fBv3KjTrJADmMtPcHEB248o+\\n\" +\n");
        stringAdd.append("            \"1r+AVvPNiQ4GmPLgWUWXjWkvFKLp4lumeAUIwInyPypHr2jmb6M4tWERLnMnxCei\\n\" +\n");
        stringAdd.append("            \"/XU6e25Vu2FbeEN9EryCQ6iVrson7CjB4xkdL7FAyyNOpxxcT4ZTGacaGBxNF6tM\\n\" +\n");
        stringAdd.append("            \"i94+W/xBAoGBAP6fRKd2KCxX92/CaAizFhTho0LhYUR6TCPSsBnOzLG4drY6iYKP\\n\" +\n");
        stringAdd.append("            \"ViD/cI/nO7K4KsUs12LwGfOqwtVhfKQ8xRk1749FPE1sxXVQlZyx5bWcFerEKQWR\\n\" +\n");
        stringAdd.append("            \"M9+0CickuijKoVYaeluCIBlT7Loq9siSonIpBKTd3Cz75on9vCua4LDtAoGBAM99\\n\" +\n");
        stringAdd.append("            \"fA+NE+qu9Hyn/R6jP5QOR2B7ZnmVwTij5sjpEKByYX1N1jM5F9RxMQC+PjYA0vRY\\n\" +\n");
        stringAdd.append("            \"xqPvK16Rty4spRZ64n24Fl7S2M5T8GaAdw0sXfJJ8hHpEhu/WQHv91AteBT/dXij\\n\" +\n");
        stringAdd.append("            \"5xJpAbWwkD4Xzj2nIUFHcF1yvs+yFilSNmJAcXCtAoGBAPYaGtGBcvLpU0/CJ6vA\\n\" +\n");
        stringAdd.append("            \"7Obh8FsEXG9DhbRaP7uyFkbwdE1N5vs6b5UVUEbGyPzE5RIPdV87ktnhgs+bmk1k\\n\" +\n");
        stringAdd.append("            \"L11etF1WHiL3ryLk8Laze8M9SjHPx5aJ8gmWivmqlgQ3VhlTHlC0RTI12+lU/ZJ/\\n\" +\n");
        stringAdd.append("            \"qGOPId34UodoMjvN88JxwBtBAoGAIjvlBTfUqmr1Yb/hznVG3ym/8xZJIRRpTewU\\n\" +\n");
        stringAdd.append("            \"/t8dZCubFC6MgeUQnneSiznYT9aw96nSFGg4sqC+JXsuPdhGmFGQN1L4fIpy5qaZ\\n\" +\n");
        stringAdd.append("            \"fe3tDjXPH89hsnxOtjgeiPcHkgdYHXCFho8WviCEnAoXeCqkHP4pdvGgs+oBKA4t\\n\" +\n");
        stringAdd.append("            \"dOaHEP0CgYAVhF64jupCOqvZCeDge/KH79bHrSVpZXl9l8xrireNBrPLI5YUcbMz\\n\" +\n");
        stringAdd.append("            \"p4sPt07LsY52omFaTRaipQ5KoInLcwzV29EEn4+pJ70xPwqOJfwGjN/PvbwIBPNP\\n\" +\n");
        stringAdd.append("            \"TouAheSiq/rF0v68LGwyDTlGSlJ+K4rYV97ujrylAaHoLfqG/MUAIw==\\n\" +\n");
        stringAdd.append("            \"-----END RSA PRIVATE KEY-----\";\n");
        stringAdd.append("    private String publicKey = \"-----BEGIN PUBLIC KEY-----\\n\" +\n");
        stringAdd.append("            \"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzl+XrIs22JIWi8+q+Jbz\\n\" +\n");
        stringAdd.append("            \"7xO7O8Mclq7tY4gjgS1RSM0SBJit3iZi9gAd984kYzDY3dExMMXuK9er0G2TZf0p\\n\" +\n");
        stringAdd.append("            \"H8vqQrwmOJlMvJ6yIHjGdRZBrKjmm8ALs37mWoqra86iMgYNyHKR14KZhNVzSaak\\n\" +\n");
        stringAdd.append("            \"ngAXHJ1FAUI4kWH4S6KOgLIKyUYcGVvdtjShd4Ph5Gyj2fouu9OREJ8qRa3ILB73\\n\" +\n");
        stringAdd.append("            \"EIPzuanFoHnO8r0dN/JLE5wXQ6tOA2MKEe6aKQgjSDSVgrREjQt8cCxRMaw3jjT/\\n\" +\n");
        stringAdd.append("            \"G0r9fGFWMhkF4vqQlKuZuNdnIDEFWFB2TTzcfxAsYKhM52kt1GZtQT6JAn9dT9xA\\n\" +\n");
        stringAdd.append("            \"KQIDAQAB\\n\" +\n");
        stringAdd.append("            \"-----END PUBLIC KEY-----\";\n");
        stringAdd.append(Constant.AUTOWIRED);
        stringAdd.append("    @Qualifier(\"authenticationManagerBean\")\n");
        stringAdd.append("    private AuthenticationManager authenticationManager;\n");
        stringAdd.append("\n");
        stringAdd.append(Constant.BEAN);
        stringAdd.append("    public JwtAccessTokenConverter tokenEnhancer() {\n");
        stringAdd.append("        JwtAccessTokenConverter converter = new JwtAccessTokenConverter( );\n");
        stringAdd.append("        converter.setSigningKey(privateKey);\n");
        stringAdd.append("        converter.setVerifierKey(publicKey);\n");
        stringAdd.append("        return converter;\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append(Constant.BEAN);
        stringAdd.append("    public JwtTokenStore tokenStore() {\n");
        stringAdd.append("        return new JwtTokenStore(tokenEnhancer( ));\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append(Constant.OVERRIDE);
        stringAdd.append("    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {\n");
        stringAdd.append("        endpoints.authenticationManager(authenticationManager).tokenStore(tokenStore( ))\n");
        stringAdd.append("                .accessTokenConverter(tokenEnhancer( ));\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append(Constant.OVERRIDE);
        stringAdd.append("    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {\n");
        stringAdd.append("        security.tokenKeyAccess(\"permitAll()\").checkTokenAccess(\"isAuthenticated()\");\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append(Constant.OVERRIDE);
        stringAdd.append("    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {\n");
        stringAdd.append("        clients.inMemory( ).withClient(clientid).secret(new BCryptPasswordEncoder( ).encode(clientSecret)).scopes(\"read\", \"write\")\n");
        stringAdd.append("                .authorizedGrantTypes(\"password\", \"refresh_token\").accessTokenValiditySeconds(20000)\n");
        stringAdd.append("                .refreshTokenValiditySeconds(20000);\n");
        stringAdd.append("\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("}");
        return stringAdd.toString( );
    }

    public String SecurityConfiguration(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder( );
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.OAUTH_CONFIG);
        stringAdd.append(Constant.IMPORT_AUTOWIRED);
        stringAdd.append("import org.springframework.context.annotation.Bean;\n");
        stringAdd.append("import org.springframework.context.annotation.Configuration;\n");
        stringAdd.append("import org.springframework.security.authentication.AuthenticationManager;\n");
        stringAdd.append("import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;\n");
        stringAdd.append("import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;\n");
        stringAdd.append("import org.springframework.security.config.annotation.web.builders.HttpSecurity;\n");
        stringAdd.append("import org.springframework.security.config.annotation.web.builders.WebSecurity;\n");
        stringAdd.append("import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;\n");
        stringAdd.append("import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;\n");
        stringAdd.append("import org.springframework.security.config.http.SessionCreationPolicy;\n");
        stringAdd.append("import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;\n");
        stringAdd.append("import org.springframework.security.crypto.password.PasswordEncoder;\n");
        stringAdd.append("\n");
        stringAdd.append("@Configuration\n");
        stringAdd.append("@EnableWebSecurity\n");
        stringAdd.append("@EnableGlobalMethodSecurity(prePostEnabled = true)\n");
        stringAdd.append("public class SecurityConfiguration extends WebSecurityConfigurerAdapter {\n");
        stringAdd.append(Constant.AUTOWIRED);
        stringAdd.append("    private CustomDetailsService customDetailsService;\n");
        stringAdd.append("\n");
        stringAdd.append(Constant.BEAN);
        stringAdd.append("    public PasswordEncoder encoder() {\n");
        stringAdd.append("        return new BCryptPasswordEncoder( );\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append(Constant.OVERRIDE);
        stringAdd.append(Constant.AUTOWIRED);
        stringAdd.append("    protected void configure(AuthenticationManagerBuilder auth) throws Exception {\n");
        stringAdd.append("        auth.userDetailsService(customDetailsService).passwordEncoder(encoder( ));\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append(Constant.OVERRIDE);
        stringAdd.append("    protected void configure(HttpSecurity http) throws Exception {\n");
        stringAdd.append("        http.authorizeRequests( ).anyRequest( ).authenticated( ).and( ).sessionManagement( )\n");
        stringAdd.append("                .sessionCreationPolicy(SessionCreationPolicy.NEVER);\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append(Constant.OVERRIDE);
        stringAdd.append("    public void configure(WebSecurity web) throws Exception {\n");
        stringAdd.append("        web.ignoring( );\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append(Constant.OVERRIDE);
        stringAdd.append(Constant.BEAN);
        stringAdd.append("    public AuthenticationManager authenticationManagerBean() throws Exception {\n");
        stringAdd.append("        return super.authenticationManagerBean( );\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("}");
        return stringAdd.toString( );
    }


    public String CustomUser(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder( );
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.OAUTH_CONFIG);
        stringAdd.append("import org.springframework.security.core.userdetails.User;\n");
        stringAdd.append("public class CustomUser extends User {\n");
        stringAdd.append("    private static final long serialVersionUID = 1L;\n");
        stringAdd.append("\n");
        stringAdd.append("    public CustomUser(UserEntity user) {\n");
        stringAdd.append("        super(user.getUsername( ), user.getPassword( ), user.getGrantedAuthoritiesList( ));\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("}");
        return stringAdd.toString( );
    }

    public String customDetailsService(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder( );
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.OAUTH_CONFIG);
        stringAdd.append(Constant.IMPORT_AUTOWIRED);
        stringAdd.append("import org.springframework.security.core.userdetails.UserDetailsService;\n");
        stringAdd.append("import org.springframework.security.core.userdetails.UsernameNotFoundException;\n");
        stringAdd.append("import org.springframework.stereotype.Service;\n");
        stringAdd.append("@Service\n");
        stringAdd.append("public class CustomDetailsService").append(" implements ").append("UserDetailsService{\n");
        stringAdd.append("\t@Autowired\n" +
                "    OAuthDao oauthDao;\n");
        stringAdd.append("\t@Override\n" +
                "    public CustomUser loadUserByUsername(final String username) throws UsernameNotFoundException {\n" +
                "        UserEntity userEntity = null;\n" +
                "        try {\n" +
                "            userEntity = oauthDao.getUserDetails(username);\n" +
                "            CustomUser customUser = new CustomUser(userEntity);\n" +
                "            return customUser;\n" +
                "        } catch (Exception e) {\n" +
                "            e.printStackTrace();\n" +
                "            throw new UsernameNotFoundException(\"User \" + username + \" was not found in the database\");\n" +
                "        }\n" +
                "    }\n\n");
        stringAdd.append("}\n");
        return stringAdd.toString( );
    }


    public String oAuthDao(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder( );
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.OAUTH_CONFIG);
        stringAdd.append(Constant.IMPORT_AUTOWIRED);
        stringAdd.append("import org.springframework.jdbc.core.JdbcTemplate;\n");
        stringAdd.append("import org.springframework.security.core.GrantedAuthority;\n");
        stringAdd.append("import org.springframework.security.core.authority.SimpleGrantedAuthority;\n");
        stringAdd.append("import org.springframework.stereotype.Repository;\n");
        stringAdd.append("\n");
        stringAdd.append("import java.sql.ResultSet;\n");
        stringAdd.append("import java.util.ArrayList;\n");
        stringAdd.append("import java.util.Collection;\n");
        stringAdd.append("import java.util.List;\n");
        stringAdd.append("\n");
        stringAdd.append("@Repository\n");
        stringAdd.append("public class OAuthDao {\n");
        stringAdd.append(Constant.AUTOWIRED);
        stringAdd.append("    private JdbcTemplate jdbcTemplate;\n");
        stringAdd.append("\n");
        stringAdd.append("    public UserEntity getUserDetails(String username) {\n");
        stringAdd.append("        Collection<GrantedAuthority> grantedAuthoritiesList = new ArrayList<>( );\n");
        stringAdd.append("        String userSQLQuery = \"SELECT * FROM users WHERE USERNAME=?\";\n");
        stringAdd.append("        List<UserEntity> list = jdbcTemplate.query(userSQLQuery, new String[]{username},\n");
        stringAdd.append("                (ResultSet rs, int rowNum) -> {\n");
        stringAdd.append("\n");
        stringAdd.append("                    UserEntity user = new UserEntity( );\n");
        stringAdd.append("                    user.setUsername(username);\n");
        stringAdd.append("                    user.setPassword(rs.getString(\"PASSWORD\"));\n");
        stringAdd.append("                    return user;\n");
        stringAdd.append("                });\n");
        stringAdd.append("        if (list.size( ) > 0) {\n");
        stringAdd.append("            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(\"ROLE_SYSTEMADMIN\");\n");
        stringAdd.append("            grantedAuthoritiesList.add(grantedAuthority);\n");
        stringAdd.append("            list.get(0).setGrantedAuthoritiesList(grantedAuthoritiesList);\n");
        stringAdd.append("            return list.get(0);\n");
        stringAdd.append("        }\n");
        stringAdd.append("        return null;\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("}");


        return stringAdd.toString( );
    }


    public String userEntity(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder( );
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.OAUTH_CONFIG);
        stringAdd.append("\n");
        stringAdd.append("import org.springframework.security.core.GrantedAuthority;\n");
        stringAdd.append("\n");
        stringAdd.append("import java.util.ArrayList;\n");
        stringAdd.append("import java.util.Collection;\n");
        stringAdd.append("\n");
        stringAdd.append("public class UserEntity {\n");
        stringAdd.append("    private String username;\n");
        stringAdd.append("    private String password;\n");
        stringAdd.append("    private Collection<GrantedAuthority> grantedAuthoritiesList = new ArrayList<>( );\n");
        stringAdd.append("\n");
        stringAdd.append("    public String getPassword() {\n");
        stringAdd.append("        return password;\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("    public void setPassword(String password) {\n");
        stringAdd.append("        this.password = password;\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("    public Collection<GrantedAuthority> getGrantedAuthoritiesList() {\n");
        stringAdd.append("        return grantedAuthoritiesList;\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("    public void setGrantedAuthoritiesList(Collection<GrantedAuthority> grantedAuthoritiesList) {\n");
        stringAdd.append("        this.grantedAuthoritiesList = grantedAuthoritiesList;\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("    public String getUsername() {\n");
        stringAdd.append("        return username;\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("    public void setUsername(String username) {\n");
        stringAdd.append("        this.username = username;\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("}");

        return stringAdd.toString( );
    }
}
