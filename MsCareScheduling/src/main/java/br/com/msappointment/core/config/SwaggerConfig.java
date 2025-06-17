package br.com.msappointment.core.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI careSchedulingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Care Scheduling Project")
                        .description("Care Scheduling Project - Grazielle Ferreira")
                        .version("v0.0.1")
                        .license(new License()
                                .name("GFT")
                                .url("https://www.gft.com/"))
                        .contact(new Contact()
                                .name("Grazielle Ferreira")
                                .url("https://git.gft.com/glir")
                                .email("grazielle.ferreira@gft.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("GitLab")
                        .url("https://git.gft.com/glir"));
    }

    @Bean
    public OpenApiCustomizer customerGlobalHeaderOpenApiCustomiser() {
        return openApi -> openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
            ApiResponses apiResponses = operation.getResponses();
            apiResponses.addApiResponse("200", createApiResponse("Success!"));
            apiResponses.addApiResponse("201", createApiResponse("Object Persisted!"));
            apiResponses.addApiResponse("204", createApiResponse("Object Deleted!"));
            apiResponses.addApiResponse("400", createApiResponse("Bad Request!"));
            apiResponses.addApiResponse("401", createApiResponse("Unauthorized Access!"));
            apiResponses.addApiResponse("403", createApiResponse("Forbidden Access!"));
            apiResponses.addApiResponse("404", createApiResponse("Object Not Found!"));
            apiResponses.addApiResponse("500", createApiResponse("Application Error!"));
        }));
    }

    private ApiResponse createApiResponse(String message) {
        return new ApiResponse().description(message);
    }
}