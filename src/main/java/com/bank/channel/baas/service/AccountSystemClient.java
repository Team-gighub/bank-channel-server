package com.bank.channel.baas.service;

import com.bank.channel.baas.dto.Bank.BankPaymentApprovalRequest;
import com.bank.channel.baas.dto.Bank.BankPaymentAuthorizeRequest;
import com.bank.channel.baas.dto.NonBank.PaymentApprovalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * [계정계] 시스템 (Core Server)과의 통신을 위한 Feign Client
 * - 채널계 요청을 계정계로 전달하는 역할을 합니다.
 */
@FeignClient(name = "account-system", url = "${external.api.account-system.url}")
public interface AccountSystemClient {

    /**
     * POST /payment/authorize
     * 계졍계 결제 요청 API 호출
     *
     * @param request 계정계 전용 요청 DTO (가공된 데이터)
     * @return 결제 요청 응답 DTO (orderNo, confirmToken)
     */
    @PostMapping("/payment/authorize")
    PaymentRequestResponse requestEscrow(
            // 가공된 DTO 사용
            @RequestBody BankPaymentAuthorizeRequest request
    );

    /**
     * POST /payment/approval
     * 계정계 결제 승인 API 호출
     *
     * @param request
     * @return 결제 승인 응답 DTO (escrowId)
     */
    @PostMapping("/payment/approval")
    PaymentApprovalResponse approvePayment(
            @RequestBody BankPaymentApprovalRequest request
    );
}
