package com.d2d.util;

import com.d2d.constant.Constant;
import com.d2d.dto.DbDesignTables;
import org.apache.poi.ss.formula.eval.ConcatEval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Sekhar
 */
@Component
public class KeyCloakCreatorUtil {

    @Autowired
    CommonUtil commonUtil;

    public String SecurityConfigKeyCloak(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder( );
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.KEYCLOAK_CONFIG);
        stringAdd.append("import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;\n");
        stringAdd.append("import org.keycloak.adapters.springsecurity.KeycloakConfiguration;\n");
        stringAdd.append("import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;\n");
        stringAdd.append("import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;\n");
        stringAdd.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        stringAdd.append("import org.springframework.context.annotation.Bean;\n");
        stringAdd.append("import org.springframework.context.annotation.Import;\n");
        stringAdd.append("import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;\n");
        stringAdd.append("import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;\n");
        stringAdd.append("import org.springframework.security.config.annotation.web.builders.HttpSecurity;\n");
        stringAdd.append("import org.springframework.security.config.http.SessionCreationPolicy;\n");
        stringAdd.append("import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;\n");
        stringAdd.append("import org.springframework.security.core.session.SessionRegistry;\n");
        stringAdd.append("import org.springframework.security.core.session.SessionRegistryImpl;\n");
        stringAdd.append("import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;\n");
        stringAdd.append("import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;\n");
        stringAdd.append("\n");
        stringAdd.append("@KeycloakConfiguration\n");
        stringAdd.append("@Import(KeycloakSpringBootConfigResolver.class)\n");
        stringAdd.append("@EnableGlobalMethodSecurity(jsr250Enabled = true)\n");
        stringAdd.append("public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {\n");
        stringAdd.append("    /**\n");
        stringAdd.append("     * Registers the KeycloakAuthenticationProvider with the authentication manager.\n");
        stringAdd.append("     */\n");
        stringAdd.append("    @Autowired\n");
        stringAdd.append("    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {\n");
        stringAdd.append("        KeycloakAuthenticationProvider keycloakAuthenticationProvider = new KeycloakAuthenticationProvider( );\n");
        stringAdd.append("        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper( ));\n");
        stringAdd.append("        auth.authenticationProvider(keycloakAuthenticationProvider);\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("    /**\n");
        stringAdd.append("     * Defines the session authentication strategy.\n");
        stringAdd.append("     */\n");
        stringAdd.append("\n");
        stringAdd.append(Constant.BEAN);
        stringAdd.append(Constant.OVERRIDE);
        stringAdd.append("    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {\n");
        stringAdd.append("        return new RegisterSessionAuthenticationStrategy(buildSessionRegistry( ));\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append(Constant.BEAN);
        stringAdd.append("    protected SessionRegistry buildSessionRegistry() {\n");
        stringAdd.append("        return new SessionRegistryImpl( );\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append(Constant.OVERRIDE);
        stringAdd.append("    protected void configure(HttpSecurity http) throws Exception {\n");
        stringAdd.append("        super.configure(http);\n");
        stringAdd.append("        http.csrf( )\n");
        stringAdd.append("                .disable( )\n");
        stringAdd.append("                .authorizeRequests( )\n");
        stringAdd.append("                .antMatchers(\"/error\").permitAll( )\n");
        stringAdd.append("                .anyRequest( )\n");
        stringAdd.append("                .authenticated( )\n");
        stringAdd.append("                .and( )\n");
        stringAdd.append("                .sessionManagement( )\n");
        stringAdd.append("                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("}");
        return stringAdd.toString( );
    }

    public String SecurityConfigKeyCloakVersionThree(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder( );
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.KEYCLOAK_CONFIG);
        stringAdd.append("import lombok.RequiredArgsConstructor;\n");
        stringAdd.append("import org.springframework.context.annotation.Bean;\n");
        stringAdd.append("import org.springframework.context.annotation.Configuration;\n");
        stringAdd.append("import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;\n");
        stringAdd.append("import org.springframework.security.config.annotation.web.builders.HttpSecurity;\n");
        stringAdd.append("import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;\n");
        stringAdd.append("import org.springframework.security.web.SecurityFilterChain;\n");
        stringAdd.append("import org.springframework.security.config.http.SessionCreationPolicy;\n");
        stringAdd.append("\n");
        stringAdd.append("@Configuration\n");
        stringAdd.append("@EnableWebSecurity\n");
        stringAdd.append("@EnableMethodSecurity\n");
        stringAdd.append("@RequiredArgsConstructor\n");
        stringAdd.append("public class SecurityConfig {\n");
        stringAdd.append("\n");
        stringAdd.append("    private final JwtAuthConverter jwtAuthConverter;\n");
        stringAdd.append("\n");
        stringAdd.append(Constant.BEAN);
        stringAdd.append("    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {\n");
        stringAdd.append("        http.csrf(csrf -> csrf.disable( ));\n");
        stringAdd.append("\n");
        stringAdd.append("        http.authorizeHttpRequests(accessManagement -> accessManagement\n");
        stringAdd.append("                .requestMatchers(\"/error\").permitAll( )\n");
        stringAdd.append("                .anyRequest( ).authenticated( )\n");
        stringAdd.append("        );\n");
        stringAdd.append("        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)));\n");
        stringAdd.append("\n");
        stringAdd.append("        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));\n");
        stringAdd.append("\n");
        stringAdd.append("\n");
        stringAdd.append("        return http.build( );\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("}");
        return stringAdd.toString( );
    }

    public String JwtAuthConverterVersionThree(DbDesignTables tables) {
        StringBuilder stringAdd = new StringBuilder( );
        stringAdd.append(Constant.PACKAGE_SPACE).append(tables.getSpringBootBasicDTO( ).getPackageName( )).append(Constant.KEYCLOAK_CONFIG);
        stringAdd.append("import org.springframework.beans.factory.annotation.Value;\n");
        stringAdd.append("import org.springframework.core.convert.converter.Converter;\n");
        stringAdd.append("import org.springframework.lang.NonNull;\n");
        stringAdd.append("import org.springframework.security.authentication.AbstractAuthenticationToken;\n");
        stringAdd.append("import org.springframework.security.core.GrantedAuthority;\n");
        stringAdd.append("import org.springframework.security.core.authority.SimpleGrantedAuthority;\n");
        stringAdd.append("import org.springframework.security.oauth2.jwt.Jwt;\n");
        stringAdd.append("import org.springframework.security.oauth2.jwt.JwtClaimNames;\n");
        stringAdd.append("import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;\n");
        stringAdd.append("import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;\n");
        stringAdd.append("import org.springframework.stereotype.Component;\n");
        stringAdd.append("\n");
        stringAdd.append("import java.util.Collection;\n");
        stringAdd.append("import java.util.Map;\n");
        stringAdd.append("import java.util.Set;\n");
        stringAdd.append("import java.util.stream.Collectors;\n");
        stringAdd.append("import java.util.stream.Stream;\n");
        stringAdd.append("\n");
        stringAdd.append("@Component\n");
        stringAdd.append("public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {\n");
        stringAdd.append("\n");
        stringAdd.append("    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =\n");
        stringAdd.append("            new JwtGrantedAuthoritiesConverter( );\n");
        stringAdd.append("    @Value(\"${jwt.auth.converter.principle-attribute}\")\n");
        stringAdd.append("    private String principleAttribute;\n");
        stringAdd.append("    @Value(\"${jwt.auth.converter.resource-id}\")\n");
        stringAdd.append("    private String resourceId;\n");
        stringAdd.append("\n");
        stringAdd.append(Constant.OVERRIDE);
        stringAdd.append("    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {\n");
        stringAdd.append("        Collection<GrantedAuthority> authorities = Stream.concat(\n");
        stringAdd.append("                jwtGrantedAuthoritiesConverter.convert(jwt).stream( ),\n");
        stringAdd.append("                extractResourceRoles(jwt).stream( )\n");
        stringAdd.append("        ).collect(Collectors.toSet( ));\n");
        stringAdd.append("\n");
        stringAdd.append("        return new JwtAuthenticationToken(\n");
        stringAdd.append("                jwt,\n");
        stringAdd.append("                authorities,\n");
        stringAdd.append("                getPrincipleClaimName(jwt)\n");
        stringAdd.append("        );\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("    private String getPrincipleClaimName(Jwt jwt) {\n");
        stringAdd.append("        String claimName = JwtClaimNames.SUB;\n");
        stringAdd.append("        if (principleAttribute != null) {\n");
        stringAdd.append("            claimName = principleAttribute;\n");
        stringAdd.append(Constant.NEW_LINE);
        stringAdd.append("        return jwt.getClaim(claimName);\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {\n");
        stringAdd.append("        Map<String, Object> resourceAccess;\n");
        stringAdd.append("        Map<String, Object> resource;\n");
        stringAdd.append("        Collection<String> resourceRoles;\n");
        stringAdd.append("        if (jwt.getClaim(\"resource_access\") == null) {\n");
        stringAdd.append("            return Set.of( );\n");
        stringAdd.append(Constant.NEW_LINE);
        stringAdd.append("        resourceAccess = jwt.getClaim(\"resource_access\");\n");
        stringAdd.append("\n");
        stringAdd.append("        if (resourceAccess.get(resourceId) == null) {\n");
        stringAdd.append("            return Set.of( );\n");
        stringAdd.append(Constant.NEW_LINE);
        stringAdd.append("        resource = (Map<String, Object>) resourceAccess.get(resourceId);\n");
        stringAdd.append("\n");
        stringAdd.append("        resourceRoles = (Collection<String>) resource.get(\"roles\");\n");
        stringAdd.append("        return resourceRoles\n");
        stringAdd.append("                .stream( )\n");
        stringAdd.append("                .map(role -> new SimpleGrantedAuthority(\"ROLE_\" + role))\n");
        stringAdd.append("                .collect(Collectors.toSet( ));\n");
        stringAdd.append(Constant.SPACE_CURLY_NEWLINE);
        stringAdd.append("\n");
        stringAdd.append("}");


        return stringAdd.toString( );
    }
}
