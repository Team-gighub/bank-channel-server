package com.bank.channel.global.filter;

import com.bank.channel.baas.service.ApiCallLogService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
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
@RequiredArgsConstructor
public class TraceIdFilter extends OncePerRequestFilter {

    private final ApiCallLogService apiCallLogService;

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

        // 5. 요청 시각 기록
        LocalDateTime requestAt = LocalDateTime.now();
        
        // 6. 요청 로깅
        log.info("API Request - Method: {}, URI: {}, TraceId: {}", 
                request.getMethod(), 
                request.getRequestURI(), 
                traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 7. 응답 시각 기록
            LocalDateTime responseAt = LocalDateTime.now();
            
            // 8. 응답 로깅
            log.info("API Response - Status: {}, TraceId: {}", 
                    response.getStatus(), 
                    traceId);
            
            // 9. API 호출 로그 저장 (비동기로 저장 - 실패해도 API에 영향 없음)
            try {
                // TODO: merchantId 추출 로직 필요 (Authorization 헤더 또는 RequestBody)
                String merchantId = extractMerchantId(request);
                
                apiCallLogService.saveApiCallLog(
                    traceId,
                    merchantId,
                    request.getRequestURI(),
                    request.getMethod(),
                    requestAt,
                    responseAt,
                    response.getStatus()
                );
            } catch (Exception e) {
                log.error("API 로그 저장 실패 - TraceId: {}", traceId, e);
            }
            
            // 10. MDC 정리 (메모리 누수 방지)
            MDC.remove(MDC_TRACE_ID_KEY);
        }
    }

    /**
     * TraceId 생성
     * UUID에서 하이픈(-) 제거하여 32자 문자열로 생성
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * merchantId 추출
     * 
     * TODO: 실제 구현 필요
     * - Authorization 헤더 파싱
     * - JWT 토큰에서 추출
     * - 또는 요청 Body에서 추출
     */
    private String extractMerchantId(HttpServletRequest request) {
        // 임시: Authorization 헤더가 있으면 사용, 없으면 "UNKNOWN"
        String authorization = request.getHeader("Authorization");
        return authorization != null ? "EXTRACTED_FROM_TOKEN" : "UNKNOWN";
    }
}
