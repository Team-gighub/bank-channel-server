package com.bank.channel.baas.service;

import com.bank.channel.baas.domain.ApiCallLog;
import com.bank.channel.baas.domain.enums.HttpMethod;
import com.bank.channel.baas.repository.ApiCallLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * API 호출 로그 저장 Service
 * 
 * TraceIdFilter에서 모든 API 요청/응답을 자동으로 기록합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiCallLogService {

    private final ApiCallLogRepository apiCallLogRepository;

    /**
     * API 호출 로그 저장
     * 
     * @param traceId TraceId
     * @param merchantId 고객사 ID (없으면 "UNKNOWN")
     * @param apiEndpoint API 경로
     * @param httpMethod HTTP 메서드
     * @param requestAt 요청 시각
     * @param responseAt 응답 시각
     * @param statusCode HTTP 응답 코드
     */
    @Transactional
    public void saveApiCallLog(
            String traceId,
            String merchantId,
            String apiEndpoint,
            String httpMethod,
            LocalDateTime requestAt,
            LocalDateTime responseAt,
            int statusCode
    ) {
        try {
            // latency 계산 (ms)
            long latencyMs = java.time.Duration.between(requestAt, responseAt).toMillis();

            ApiCallLog apiCallLog = ApiCallLog.builder()
                    .traceId(traceId)
                    .merchantId(merchantId != null ? merchantId : "UNKNOWN")
                    .apiEndpoint(apiEndpoint)
                    .httpMethod(HttpMethod.valueOf(httpMethod))
                    .requestAt(requestAt)
                    .responseAt(responseAt)
                    .latencyMs((int) latencyMs)
                    .statusCode(statusCode)
                    .build();

            apiCallLogRepository.save(apiCallLog);
            
            log.debug("[API_CALL_LOG] Saved - TraceId: {}, Endpoint: {}, Latency: {}ms", 
                     traceId, apiEndpoint, latencyMs);
        } catch (Exception e) {
            // 로그 저장 실패해도 API 요청/응답에는 영향 없도록
            log.error("[API_CALL_LOG] Failed to save log. TraceId: {}, Error: {}", 
                     traceId, e.getMessage(), e);
        }
    }
}
