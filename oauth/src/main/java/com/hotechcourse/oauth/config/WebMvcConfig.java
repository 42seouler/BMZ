package com.hotechcourse.oauth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        long MAX_AGE_SECS = 3600;
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000",
                    "http://sparta42fe.s3-website.ap-northeast-2.amazonaws.com/",
                    "http://3.36.249.156:4000/",
                    "http://3.36.249.156:4001/",
                    "http://3.36.249.156:4002/",
                    "http://3.36.249.156:4003/",
                    "http://3.36.249.156:4004/",
                    "http://3.36.249.156:4005/",
                    "http://0.0.0.0"
            )
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(MAX_AGE_SECS);
    }
}
