package com.bank.channel.global.filter;

import com.bank.channel.baas.service.ApiCallLogService;
import com.bank.channel.baas.service.ApiKeyAuthService;
import com.bank.channel.baas.service.ApiKeyAuthService.MerchantAuthResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * TraceId 발급 및 관리 Filter
 * 
 * 기능:
 * 1. 각 API 호출마다 고유한 traceId 발급 (UUID)
 * 2. MDC에 저장하여 로그에 자동 포함
 * 3. 응답 헤더에 traceId 추가
 * 4. API Key 기반 보안 인증 + Request Body 교차 검증
 * 5. 보안 위협 감지 시 즉시 차단 (403 응답)
 * 6. API 호출 로그 저장 (사용량 측정)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TraceIdFilter extends OncePerRequestFilter {

    private final ApiCallLogService apiCallLogService;
    private final ApiKeyAuthService apiKeyAuthService;
    private final ObjectMapper objectMapper;

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String MDC_TRACE_ID_KEY = "traceId";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Request Body를 여러 번 읽을 수 있도록 래핑
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);

        // 2. 요청 헤더에서 traceId 확인 (계정계에서 전달받은 경우)
        String traceId = cachedRequest.getHeader(TRACE_ID_HEADER);
        
        // 3. 없으면 새로 생성 (최초 요청인 경우)
        if (!org.springframework.util.StringUtils.hasText(traceId)) {
            traceId = generateTraceId();
        }

        // 4. MDC에 저장 (로그에 자동 포함됨)
        MDC.put(MDC_TRACE_ID_KEY, traceId);

        // 5. 응답 헤더에 추가 (클라이언트가 확인 가능)
        response.addHeader(TRACE_ID_HEADER, traceId);

        // 6. 요청 시각 기록
        LocalDateTime requestAt = LocalDateTime.now();
        
        // 7. 요청 로깅
        log.info("API Request - Method: {}, URI: {}, TraceId: {}", 
                cachedRequest.getMethod(), 
                cachedRequest.getRequestURI(), 
                traceId);

        // 8. merchantId 추출 및 보안 검증 (FilterChain 이전에 수행!)
        String merchantId = extractAndValidateMerchantId(cachedRequest);
        
        // 9. 보안 위협 감지 시 즉시 차단!
        if ("SECURITY_THREAT".equals(merchantId) || "INVALID_API_KEY".equals(merchantId)) {
            handleSecurityThreat(cachedRequest, response, traceId, merchantId, requestAt);
            return; // ⚠️ 여기서 종료! FilterChain 진행 안 함
        }

        try {
            // 10. 검증 통과한 요청만 Filter Chain 진행
            filterChain.doFilter(cachedRequest, response);
        } finally {
            // 11. 응답 시각 기록
            LocalDateTime responseAt = LocalDateTime.now();
            
            // 12. 응답 로깅
            log.info("API Response - Status: {}, TraceId: {}", 
                    response.getStatus(), 
                    traceId);
            
            // 13. API 호출 로그 저장 (정상 처리된 요청)
            try {
                apiCallLogService.saveApiCallLog(
                    traceId,
                    merchantId,
                    cachedRequest.getRequestURI(),
                    cachedRequest.getMethod(),
                    requestAt,
                    responseAt,
                    response.getStatus()
                );
            } catch (Exception e) {
                log.error("API 로그 저장 실패 - TraceId: {}", traceId, e);
            }
            
            // 14. MDC 정리 (메모리 누수 방지)
            MDC.remove(MDC_TRACE_ID_KEY);
        }
    }

    /**
     * 보안 위협 처리 (403 응답 + 로그 저장)
     * 
     * @param request 요청
     * @param response 응답
     * @param traceId TraceId
     * @param merchantId merchantId (SECURITY_THREAT 또는 INVALID_API_KEY)
     * @param requestAt 요청 시각
     */
    private void handleSecurityThreat(
            CachedBodyHttpServletRequest request,
            HttpServletResponse response,
            String traceId,
            String merchantId,
            LocalDateTime requestAt
    ) throws IOException {
        LocalDateTime responseAt = LocalDateTime.now();
        
        // 1. 403 Forbidden 응답
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json; charset=UTF-8");
        
        String errorMessage = "SECURITY_THREAT".equals(merchantId) 
                ? "인증 실패: Merchant ID 불일치" 
                : "인증 실패: 유효하지 않은 API Key";
        
        response.getWriter().write(String.format(
                "{\"success\":false,\"error\":{\"code\":\"AUTH_001\",\"message\":\"%s\"}}",
                errorMessage
        ));
        
        // 2. 보안 위협 로그 기록
        log.error("[BLOCKED] {} - URI: {}, TraceId: {}", 
                merchantId, request.getRequestURI(), traceId);
        
        // 3. API 호출 로그 저장 (보안 모니터링용)
        try {
            apiCallLogService.saveApiCallLog(
                traceId,
                merchantId,
                request.getRequestURI(),
                request.getMethod(),
                requestAt,
                responseAt,
                403
            );
        } catch (Exception e) {
            log.error("보안 위협 로그 저장 실패 - TraceId: {}", traceId, e);
        }
        
        // 4. MDC 정리
        MDC.remove(MDC_TRACE_ID_KEY);
    }

    /**
     * TraceId 생성
     * UUID에서 하이픈(-) 제거하여 32자 문자열로 생성
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * merchantId 추출 및 보안 검증
     * 
     * 보안 정책:
     * 1. API Key로 merchantId 식별 (신뢰의 기준)
     * 2. Request Body와 교차 검증
     * 3. 불일치 시 보안 위협으로 간주 → "SECURITY_THREAT" 반환
     * 4. API Key 무효 시 → "INVALID_API_KEY" 반환
     * 
     * @param request CachedBodyHttpServletRequest
     * @return merchantId (검증 통과) 또는 "UNKNOWN" / "SECURITY_THREAT" / "INVALID_API_KEY"
     */
    private String extractAndValidateMerchantId(CachedBodyHttpServletRequest request) {
        try {
            // 1. Authorization 헤더에서 API Key 추출
            String apiKey = request.getHeader(AUTHORIZATION_HEADER);
            
            if (apiKey == null || apiKey.isBlank()) {
                log.debug("[MERCHANT_AUTH] No API Key provided - merchantId: UNKNOWN");
                return "UNKNOWN";
            }

            // 2. Request Body에서 merchantId 추출 (있다면)
            String requestMerchantId = extractMerchantIdFromBody(request);

            // 3. API Key 기반 인증 + 교차 검증
            MerchantAuthResult authResult = apiKeyAuthService.validateMerchantWithCrossCheck(
                    apiKey, 
                    requestMerchantId
            );

            // 4. 검증 결과에 따른 처리
            if (authResult.isValid()) {
                return authResult.getMerchantId();
            }

            // 5. 검증 실패 - 보안 위협 여부 판단
            if (authResult.isMismatch()) {
                // 심각한 보안 위협: API Key와 Request Body의 merchantId 불일치
                log.error("[SECURITY_ALERT] {}", authResult.getErrorMessage());
                return "SECURITY_THREAT";
            }

            // 6. 기타 검증 실패 (잘못된 API Key 등)
            log.warn("[MERCHANT_AUTH] {}", authResult.getErrorMessage());
            return "INVALID_API_KEY";

        } catch (Exception e) {
            log.error("[MERCHANT_AUTH] Error extracting merchantId", e);
            return "ERROR";
        }
    }

    /**
     * Request Body에서 merchantId 추출
     * 
     * JSON 파싱하여 "merchantId" 필드 추출
     * 
     * @param request CachedBodyHttpServletRequest
     * @return merchantId (Optional)
     */
    private String extractMerchantIdFromBody(CachedBodyHttpServletRequest request) {
        try {
            String body = request.getCachedBody();
            
            if (body == null || body.isBlank()) {
                return null;
            }

            // JSON 파싱
            JsonNode jsonNode = objectMapper.readTree(body);
            JsonNode merchantIdNode = jsonNode.get("merchantId");

            if (merchantIdNode != null && !merchantIdNode.isNull()) {
                return merchantIdNode.asText();
            }

            return null;
        } catch (Exception e) {
            // JSON 파싱 실패는 에러가 아님 (GET 요청 등 Body가 없을 수 있음)
            log.debug("[MERCHANT_AUTH] Failed to parse merchantId from request body", e);
            return null;
        }
    }
}
