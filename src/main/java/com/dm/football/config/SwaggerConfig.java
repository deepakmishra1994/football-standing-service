package com.dm.football.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(
            @Value("${application.description:Football Standing Service}") String appDescription,
            @Value("${application.version:1.0.0}") String appVersion) {

        return new OpenAPI()
                .info(new Info()
                        .title("Football Standing Microservice API")
                        .version(appVersion)
                        .description(appDescription + " - A production-ready microservice for football standings data")
                        .termsOfService("http://swagger.io/terms/")
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                        .contact(new Contact()
                                .name("Mishra's Team")
                                .email("deepakmisra1994@gmail.com")
                                .url("https://github.com/deepakmishra1994")));
    }
}
