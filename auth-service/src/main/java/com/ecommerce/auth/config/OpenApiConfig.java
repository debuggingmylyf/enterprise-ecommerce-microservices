package com.ecommerce.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

public class OpenApiConfig {

    public OpenAPI authServiceApi(){
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Auth Service API")
                                .version("v1")
                                .description("Enterprise Ecommerce Auth Service")
                                .contact(new Contact()
                                        .name("Shubham")
                                .email("shubham.aj.kumar@capgemini.com"))
                );
    }
}
