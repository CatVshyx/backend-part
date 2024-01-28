package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
public class WebConfigurer implements WebMvcConfigurer {
//    @Value("${spring.resources.static-locations}")
//    private String resources;
//    @Value("${spring.resources.library-location}")
//    private String libraries;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET","POST","PUT","DELETE","PATCH")
                .allowedHeaders("Authorization","Origin","X-Requested-With","Content-Type","Accept")
                .exposedHeaders("Content-Disposition");
    }

//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/notFound").setViewName("forward:/index.html");
//    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("/**").addResourceLocations("classpath:/resources/static");

//        registry.addResourceHandler("/**");

//                .addResourceLocations("file:"+resources).addResourceLocations("file:"+libraries);
    }


}