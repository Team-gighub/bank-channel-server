package com.bank.channel.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins}")
    private String origins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")             // 모든 경로에 적용
                .allowedOrigins(origins.split(",")) // 허용할 프론트 도메인
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")         // GET, POST, PUT, DELETE 등 모두 허용
                .allowedHeaders("*")         // 모든 헤더 허용
                .allowCredentials(true)      // 쿠키 허용
                .maxAge(3600);               // preflight 요청 캐시 시간 (초)
    }
}
