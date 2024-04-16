package com.d2d.config;

import com.d2d.constant.Constant;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        License mitLicense = new License( ).name("Apache 2.0").url("http://springdoc.org");
        Info info = new Info( ).title("D2D").version("1.0")
                .description("D2D Backend service End points")
                .termsOfService("http://swagger.io/terms/")
                .license(mitLicense);
        return new OpenAPI( ).info(info)
                .addSecurityItem(new SecurityRequirement( )
                        .addList(Constant.D2D_AUTHENTICATION)).components(new Components( ).addSecuritySchemes(Constant.D2D_AUTHENTICATION, new SecurityScheme( )
                        .name(Constant.D2D_AUTHENTICATION)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer").bearerFormat("JWT")));
    }

}

