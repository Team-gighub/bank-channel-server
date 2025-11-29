package com.bank.channel.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * RestTemplate 설정
 * 
 * TraceId를 계정계로 전파하기 위한 Interceptor를 추가합니다.
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // TraceId 전파 Interceptor 추가
        restTemplate.setInterceptors(
            List.of(new TraceIdPropagationInterceptor())
        );
        
        return restTemplate;
    }
}
