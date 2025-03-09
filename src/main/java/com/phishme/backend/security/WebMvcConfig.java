package com.phishme.backend.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOrigins("http://211.228.145.212:8081")
                .allowedMethods("GET", "PATCH", "PUT", "POST");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /* '/img/**'로 호출하는 자원은 '/static/img/' 폴더 아래에서 찾는다. */
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/")
                .setCachePeriod(60 * 60 * 24 * 365);
    }
}
