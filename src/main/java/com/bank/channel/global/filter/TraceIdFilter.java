package com.bank.channel.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 간단한 TraceId 발급 및 관리 Filter
 * 
 * 기능:
 * 1. 각 API 호출마다 고유한 traceId 발급 (UUID)
 * 2. MDC에 저장하여 로그에 자동 포함
 * 3. 응답 헤더에 traceId 추가
 */
@Slf4j
@Component
public class TraceIdFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String MDC_TRACE_ID_KEY = "traceId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. 요청 헤더에서 traceId 확인 (계정계에서 전달받은 경우)
        String traceId = request.getHeader(TRACE_ID_HEADER);
        
        // 2. 없으면 새로 생성 (최초 요청인 경우)
        if (!org.springframework.util.StringUtils.hasText(traceId)) {
            traceId = generateTraceId();
        }

        // 3. MDC에 저장 (로그에 자동 포함됨)
        MDC.put(MDC_TRACE_ID_KEY, traceId);

        // 4. 응답 헤더에 추가 (클라이언트가 확인 가능)
        response.addHeader(TRACE_ID_HEADER, traceId);

        // 5. 요청 로깅
        log.info("API Request - Method: {}, URI: {}, TraceId: {}", 
                request.getMethod(), 
                request.getRequestURI(), 
                traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 6. 응답 로깅
            log.info("API Response - Status: {}, TraceId: {}", 
                    response.getStatus(), 
                    traceId);
            
            // 7. MDC 정리 (메모리 누수 방지)
            MDC.clear();
        }
    }

    /**
     * TraceId 생성
     * UUID에서 하이픈(-) 제거하여 32자 문자열로 생성
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
