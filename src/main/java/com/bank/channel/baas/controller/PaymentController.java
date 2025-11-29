package com.bank.channel.baas.controller;

import com.bank.channel.baas.dto.NonBank.PaymentApprovalResponse;
import com.bank.channel.baas.dto.NonBank.PaymentAuthorizeRequest;
import com.bank.channel.baas.dto.NonBank.PaymentConfirmRequest;
import com.bank.channel.baas.dto.NonBank.PaymentConfirmResponse;
import com.bank.channel.baas.service.PaymentService;
import com.bank.channel.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * BaaS API 진입점 (결제 요청, 승인, 지급 확정)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService = null;

    /**
     * POST /payment/authorize
     * [결제 요청] API 엔드포인트: 외부 시스템의 요청을 처리합니다.
     */
    @PostMapping("/request")
    public void authorizePayment(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
            @RequestBody PaymentAuthorizeRequest request
    ) {
        paymentService.authorizePayment(request);
    }

    /**
     * POST /payment/approval
     * [결제 요청] API 엔드포인트: 외부 시스템의 요청을 처리합니다.
     */
    @PostMapping("/request")
    public ApiResponse<PaymentApprovalResponse> approvePayment(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
            @RequestBody PaymentAuthorizeRequest request
    ) {
        PaymentApprovalResponse response = paymentService.approvePayment(request);
        return ApiResponse.success(response);
    }

    /**
     * POST /payment/confirm
     * [결제 요청] API 엔드포인트: 외부 시스템의 요청을 처리합니다.
     */
    @PostMapping("/request")
    public ApiResponse<PaymentConfirmResponse> requestEscrow(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
            @RequestBody PaymentConfirmRequest request
    ) {
        PaymentConfirmResponse response = paymentService.confirmPayment(request);
        return ApiResponse.success(response);
    }

}
