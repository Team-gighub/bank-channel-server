package com.bank.channel.baas.controller;

import com.bank.channel.baas.dto.NonBank.ContractRegisterRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 계약 관련 API Controller
 * 
 * 각 API 호출마다 개별 traceId가 자동으로 발급됩니다.
 */
@Slf4j
@RestController
@RequestMapping("/contracts")
public class ContractController {

    /**
     * 계약 데이터 등록 API
     * 
     * 에스크로 결제의 마지막 단계로, 새로운 traceId가 발급됩니다.
     * 이 API는 지급확정 후 계약 정보를 저장하는 용도입니다.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerContract(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody @Valid ContractRegisterRequest request
    ) {
        String traceId = MDC.get("traceId");

        log.info("[계약등록] TraceId: {}, PaymentId: {}, OrderNo: {}, UserId: {}", 
                traceId, 
                request.getPaymentId(),
                request.getOrderNo(),
                request.getUserId());

        // TODO: 실제 계약 데이터 등록 로직 구현
        // 1. paymentId 검증
        // 2. 계약 데이터 저장 (DB)
        // 3. 계정계에 계약 정보 전송

        return ResponseEntity.ok(Map.of());
    }
}
