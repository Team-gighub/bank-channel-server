package com.bank.channel.baas.controller;

import com.bank.channel.baas.dto.Bank.BankPaymentAuthorizeResponse;
import com.bank.channel.baas.dto.NonBank.*;
import com.bank.channel.baas.service.PaymentService;
import com.bank.channel.global.exception.ErrorCode;
import com.bank.channel.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * BaaS API 진입점 (결제 요청, 승인, 지급 확정)
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/authorize")
    public ApiResponse<Map<String, String>> authorizePayment(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
            @RequestBody PaymentAuthorizeRequest request
    ) {
        // 1. 서비스 호출: 결제 인증 수행
        PaymentAuthorizeResponse result = paymentService.authorizePayment(request);

        String redirectUrl;

        if (result.isSuccess()) {
            BankPaymentAuthorizeResponse response = result.getResponse();
            redirectUrl = UriComponentsBuilder.fromUriString(request.getSuccessUrl())
                    .queryParam("escrowId", response.getEscrowId())
                    .queryParam("confirmToken", response.getConfirmToken())
                    .toUriString();

            log.info("[Payment Authorize Success] Redirect URL: {}", redirectUrl);
        } else {
            ErrorCode errorCode = result.getErrorCode();
            redirectUrl = UriComponentsBuilder.fromUriString(request.getFailUrl())
                    .queryParam("code", errorCode.getCode())
                    .toUriString();

            log.error("[Payment Authorize Failed] Redirect URL: {}", redirectUrl);
        }

        // 2. JSON body로 redirect URL 반환
        Map<String, String> body = Map.of("redirectUrl", redirectUrl);
        return ApiResponse.success(body);
    }

    /**
     * POST /payment/approval
     * [결제 요청] API 엔드포인트: 외부 시스템의 요청을 처리합니다.
     */
    @PostMapping("/approval")
    public ApiResponse<PaymentApprovalResponse> approvePayment(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
            @RequestBody PaymentApprovalRequest request
    ) {
        PaymentApprovalResponse response = paymentService.approvePayment(request);
        return ApiResponse.success(response);
    }

    /**
     * POST /payment/confirm
     * [결제 요청] API 엔드포인트: 외부 시스템의 요청을 처리합니다.
     */
    @PostMapping("/confirm")
    public ApiResponse<PaymentConfirmResponse> requestEscrow(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
            @RequestBody PaymentConfirmRequest request
    ) {
        PaymentConfirmResponse response = paymentService.confirmPayment(request);
        return ApiResponse.success(response);
    }

}
