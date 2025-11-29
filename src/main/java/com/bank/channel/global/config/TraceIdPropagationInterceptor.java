package com.bank.channel.global.config;

import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * RestTemplate Interceptor
 * 
 * 계정계 API 호출 시 traceId를 HTTP 헤더에 자동으로 추가합니다.
 */
public class TraceIdPropagationInterceptor implements ClientHttpRequestInterceptor {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String MDC_TRACE_ID_KEY = "traceId";

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, 
            byte[] body, 
            ClientHttpRequestExecution execution
    ) throws IOException {
        
        // MDC에서 현재 traceId 가져오기
        String traceId = MDC.get(MDC_TRACE_ID_KEY);
        
        // traceId가 있으면 헤더에 추가
        if (traceId != null && !traceId.isEmpty()) {
            request.getHeaders().add(TRACE_ID_HEADER, traceId);
        }
        
        // 실제 HTTP 요청 실행
        return execution.execute(request, body);
    }
}
