package com.shopkart.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI shopKartOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("ShopKart Product Management API")
                                .description(
                                        "REST API for managing products and categories"
                                )
                                .version("1.0")
                );
    }
}