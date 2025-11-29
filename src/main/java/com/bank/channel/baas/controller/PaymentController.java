package com.bank.channel.baas.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 결제 관련 API Controller
 * 
 * 각 API 호출마다 개별 traceId가 자동으로 발급됩니다.
 * SimpleTraceIdFilter에서 UUID 기반으로 자동 생성/관리됩니다.
 */
@Slf4j
@RestController
@RequestMapping("/payment")
public class PaymentController {

    /**
     * 결제 요청 API
     * 
     * 에스크로 결제의 첫 단계로, 이 API 호출 시 새로운 traceId가 발급됩니다.
     * 발급된 traceId는 응답 헤더 X-Trace-Id로 반환됩니다.
     */
    @PostMapping("/request")
    public ResponseEntity<?> requestPayment(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Map<String, Object> request
    ) {
        // MDC에서 traceId 가져오기
        String traceId = MDC.get("traceId");

        log.info("[결제요청] TraceId: {}, OrderNo: {}", 
                traceId, 
                request.get("orderNo"));

        // TODO: 실제 결제 요청 로직 구현
        // 1. merchantId 검증
        // 2. 계정계 API 호출 (출금 요청)
        // 3. confirmToken 생성

        Map<String, String> response = Map.of(
                "orderNo", (String) request.get("orderNo"),
                "confirmToken", "TOKEN_" + (traceId != null ? traceId.substring(0, 8) : "unknown")
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 결제 승인 API
     * 
     * 새로운 traceId가 발급되어 계정계 예치 API 호출을 추적합니다.
     */
    @PostMapping("/approval")
    public ResponseEntity<?> approvePayment(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Map<String, Object> request
    ) {
        String traceId = MDC.get("traceId");

        log.info("[결제승인] TraceId: {}, OrderNo: {}", 
                traceId, 
                request.get("orderNo"));

        // TODO: 실제 결제 승인 로직 구현
        // 1. confirmToken 검증
        // 2. 계정계 API 호출 (예치)
        // 3. escrowId 생성

        Map<String, String> response = Map.of(
                "escrowId", "ESCROW_" + (traceId != null ? traceId.substring(0, 8) : "unknown")
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 지급 확정 API
     * 
     * 새로운 traceId가 발급되어 계정계 송금 API 호출을 추적합니다.
     */
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Map<String, Object> request
    ) {
        String traceId = MDC.get("traceId");

        log.info("[지급확정] TraceId: {}, EscrowId: {}", 
                traceId, 
                request.get("escrowId"));

        // TODO: 실제 지급 확정 로직 구현
        // 1. escrowId 검증
        // 2. 계정계 API 호출 (송금)
        // 3. paymentId 생성

        Map<String, String> response = Map.of(
                "paymentId", "PAYMENT_" + (traceId != null ? traceId.substring(0, 8) : "unknown")
        );

        return ResponseEntity.ok(response);
    }
}
