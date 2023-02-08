/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.api;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author Wei.Cheng
 */
//@Configuration
//@EnableWebMvc
//@ComponentScan(basePackages = {"org.springdoc"})
//@Import({org.springdoc.core.SpringDocConfiguration.class,
//         org.springdoc.webmvc.core.SpringDocWebMvcConfiguration.class,
//         org.springdoc.webmvc.ui.SwaggerConfig.class,
//         org.springdoc.core.SwaggerUiConfigProperties.class,
//         org.springdoc.core.SwaggerUiOAuthProperties.class,
//         org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.class})
public class OpenApiConfig implements WebMvcConfigurer {

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        /**
//         * swagger配置
//         */
//        registry.addResourceHandler("/swagger-ui/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/4.15.5/");
//    }
    
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/swagger-ui/")
//                .setViewName("forward:/swagger-ui/index.html");
//    }
    
//    @Bean
//    public GroupedOpenApi publicApi() {
//        return GroupedOpenApi.builder()
//                .group("springshop-public")
//                .pathsToMatch("/public/**")
//                .build();
//    }
//
//    @Bean
//    public GroupedOpenApi adminApi() {
//        return GroupedOpenApi.builder()
//                .group("springshop-admin")
//                .pathsToMatch("/admin/**")
//                //                .addOpenApiMethodFilter(method -> method.isAnnotationPresent(Admin.class))
//                .build();
//    }
//
//    @Bean
//    public OpenAPI springShopOpenAPI() {
//        return new OpenAPI()
//                .info(new Info().title("SpringShop API")
//                        .description("Spring shop sample application")
//                        .version("v0.0.1")
//                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
//                .externalDocs(new ExternalDocumentation()
//                        .description("SpringShop Wiki Documentation")
//                        .url("https://springshop.wiki.github.org/docs"));
//    }

}
