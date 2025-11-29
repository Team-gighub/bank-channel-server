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

/**
 * BaaS API 진입점 (결제 요청, 승인, 지급 확정)
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * POST /payment/authorize
     * [결제 요청] API 엔드포인트: 외부 시스템의 요청을 처리합니다.
     */
    @PostMapping("/authorize")
    public String authorizePayment(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
            @RequestBody PaymentAuthorizeRequest request
    ) {
        // 1. 서비스 호출: 결제 인증을 수행
        PaymentAuthorizeResponse result = paymentService.authorizePayment(request);

        // 2. 성공/실패에 따라 로직 분기
        if (result.isSuccess()) {
            // 2-A. 성공 로직
            BankPaymentAuthorizeResponse response = result.getResponse();
            String successUrl = request.getSuccessUrl();

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(successUrl)
                    .queryParam("escrowId", response.getEscrowId())
                    .queryParam("confirmToken", response.getConfirmToken());

            log.info("[Payment Authorize Failed] 결제 검증 성공. Success URL로 리다이렉트: {}", builder.toUriString());
            return "redirect:" + builder.toUriString();

        } else {
            // 2-B. 실패 로직 (CustomException/Exception 처리 로직이 단일화됨)
            ErrorCode errorCode = result.getErrorCode();
            String failUrl = request.getFailUrl();

            log.error("[Payment Authorize Failed] 결제 검증 실패. Fail URL로 리다이렉트 - Code: {}, Message: {}",
                    errorCode.getCode(), errorCode.getMessage());

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(failUrl)
                    .queryParam("code", errorCode.getCode())
                    .queryParam("message", errorCode.getMessage());

            return "redirect:" + builder.toUriString();
        }
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
