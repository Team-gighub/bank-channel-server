package com.bank.channel.baas.controller;

import com.bank.channel.baas.dto.Bank.BankPaymentAuthorizeResponse;
import com.bank.channel.baas.dto.NonBank.*;
import com.bank.channel.baas.service.PaymentService;
import com.bank.channel.global.exception.CustomException;
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

    private final PaymentService paymentService = null;

    /**
     * POST /payment/authorize
     * [결제 요청] API 엔드포인트: 외부 시스템의 요청을 처리합니다.
     */
    @PostMapping("/authorize")
    public String authorizePayment(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
            @RequestBody PaymentAuthorizeRequest request
    ) {

        String successUrl = request.getSuccessUrl();
        String failUrl = request.getFailUrl();

        try {
            // 1. 서비스 호출: 결제 검증을 수행
            BankPaymentAuthorizeResponse response = paymentService.authorizePayment(request);

            // 2. 성공 시: successUrl로 escrowId, confirmToken을 쿼리 파라미터로 담아 리다이렉트
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(successUrl)
                    .queryParam("escrowId", response.getEscrowId())
                    .queryParam("confirmToken", response.getConfirmToken());

            log.info("[Payment Controller] 결제 인증 성공. Success URL로 리다이렉트: {}", builder.toUriString());
            return "redirect:" + builder.toUriString();

        } catch (CustomException e) {
            // 3. 실패 시 (CustomException 발생): failUrl로 에러 코드와 메시지를 담아 리다이렉트

            ErrorCode errorCode = e.getErrorCode();
            log.error("[Payment Authorize Failed] Code: {}, Message: {}", errorCode.getCode(), errorCode.getMessage());

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(failUrl)
                    .queryParam("code", errorCode.getCode())
                    .queryParam("message", errorCode.getMessage());
            return "redirect:" + builder.toUriString();

        } catch (Exception e) {
            // 4. 예기치 않은 서버 오류: failUrl로 INTERNAL_SERVER_ERROR를 담아 리다이렉트

            ErrorCode internalError = ErrorCode.INTERNAL_SERVER_ERROR;
            log.error("[Unexpected Server Error]", e);

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(failUrl)
                    .queryParam("code", internalError.getCode())
                    .queryParam("message", internalError.getMessage());
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
