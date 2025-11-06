package com.creditrefinancing.bff.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI creditRefinancingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Credit Refinancing BFF API")
                        .description("Backend for Frontend for Credit Refinancing System")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Credit Refinancing Team")
                                .email("team@creditrefinancing.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
