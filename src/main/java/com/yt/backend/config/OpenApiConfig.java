package com.yt.backend.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
        @Bean
        public OpenAPI usersMicroserviceOpenAPI() {
                return new OpenAPI()
                        .info(new Info().title("Komita API Documentation")
                                .description("komita project api description")
                                .version("1.0"));
        }
}
