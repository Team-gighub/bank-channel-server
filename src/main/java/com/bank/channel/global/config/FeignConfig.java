package com.bank.channel.global.config;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign Client 전역 설정
 * 
 * [설정 내용]
 * 1. TraceId 전파용 Interceptor 등록
 * 2. Feign 로깅 레벨 설정
 * 
 * [적용 범위]
 * - @FeignClient가 붙은 모든 클라이언트에 자동 적용
 * - AccountSystemClient에 자동으로 Interceptor 추가됨
 */
@Configuration
public class FeignConfig {

    /**
     * TraceId 전파용 Interceptor 등록
     * 
     * 모든 Feign Client 요청에 자동으로 X-Trace-Id 헤더 추가
     */
    @Bean
    public RequestInterceptor feignTraceIdInterceptor() {
        return new FeignTraceIdInterceptor();
    }

    /**
     * Feign 로깅 레벨 설정
     * 
     * NONE: 로깅 안 함 (프로덕션)
     * BASIC: 요청 메서드, URL, 응답 상태, 실행 시간만
     * HEADERS: BASIC + 요청/응답 헤더
     * FULL: 모든 것 (개발 환경)
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;  // 운영 환경에서는 NONE으로 변경
    }
}
