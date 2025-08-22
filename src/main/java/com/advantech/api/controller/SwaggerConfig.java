/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.api.controller;

import java.util.Collections;
import java.util.function.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 *
 * @author Justin.Yeh
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
//        Predicate<RequestHandler> selector = RequestHandlerSelectors.basePackage("com.advantech.api.controller")
//                .or(RequestHandlerSelectors.basePackage("com.advantech.api.controller.auth"));

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("0.API public")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.advantech.api.controller"))
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    @Bean
    public Docket apiAuth() {

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("1.API auth")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.advantech.api.controller.auth"))
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(Collections.singletonList(new BasicAuth("basicAuth")))// use for test account ,however still cannot lock page.
                .securityContexts(Collections.singletonList(SecurityContext.builder()
                        .securityReferences(Collections.singletonList(
                                new SecurityReference("basicAuth", new springfox.documentation.service.AuthorizationScope[0])
                        ))
                        .build()
                ))
                .apiInfo(apiInfoAuth());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Requisition API")
                .description("APIs for Requisition")
                .version("1.0")
                .build();
    }

    private ApiInfo apiInfoAuth() {
        return new ApiInfoBuilder()
                .title("Auth API")
                .description("APIs for Auth")
                .version("1.0")
                .build();
    }
}
