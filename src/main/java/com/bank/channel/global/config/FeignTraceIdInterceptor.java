package com.bank.channel.global.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * Feign Client Interceptor
 * 
 * 계정계 API 호출 시 traceId를 HTTP 헤더에 자동으로 추가합니다.
 * 
 * [동작 원리]
 * 1. Feign Client가 HTTP 요청을 보내기 전에 이 Interceptor 실행
 * 2. MDC에서 현재 스레드의 traceId 가져오기
 * 3. HTTP 헤더에 X-Trace-Id 추가
 * 4. 계정계로 전송
 * 
 * [RestTemplate Interceptor와의 차이]
 * - RestTemplate: ClientHttpRequestInterceptor 구현
 * - Feign: RequestInterceptor 구현 (더 간단!)
 */
@Slf4j
public class FeignTraceIdInterceptor implements RequestInterceptor {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String MDC_TRACE_ID_KEY = "traceId";

    @Override
    public void apply(RequestTemplate template) {
        // MDC에서 현재 스레드의 traceId 가져오기
        String traceId = MDC.get(MDC_TRACE_ID_KEY);
        
        if (org.springframework.util.StringUtils.hasText(traceId)) {
            // HTTP 헤더에 traceId 추가
            template.header(TRACE_ID_HEADER, traceId);
            
            log.debug("Feign Request - URL: {}, TraceId: {}", 
                    template.url(), 
                    traceId);
        } else {
            log.warn("Feign Request - URL: {}, TraceId가 MDC에 없습니다!", 
                    template.url());
        }
    }
}
