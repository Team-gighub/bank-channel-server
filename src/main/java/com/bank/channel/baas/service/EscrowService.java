package com.bank.channel.baas.service;

import com.bank.channel.baas.dto.Bank.BankPaymentApprovalRequest;
import com.bank.channel.baas.dto.Bank.BankPaymentAuthorizeRequest;
import com.bank.channel.baas.dto.NonBank.*;
import com.bank.channel.global.exception.CustomException;
import com.bank.channel.global.exception.ErrorCode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 에스크로 관련 비즈니스 로직 처리 (계정계 Feign Client 호출 및 채널계 로깅/예외 처리 담당)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EscrowService {

    private final AccountSystemClient accountSystemClient;

    /**
     * 결제 요청 로직: 외부 요청을 받아 DTO를 가공하여 계정계로 전달
     */
    public PaymentRequestResponse requestEscrow(PaymentAuthorizeRequest request) {
        log.info("[PAYMENT_REQUEST] Start processing request. OrderNo: {}", request.getOrderNo());

        // 1. 외부 요청 DTO를 계정계 전용 DTO로 변환/가공
        BankPaymentAuthorizeRequest accountRequest = convertToAccountSystemRequest(request);

        try {
            // 2. 계정계 Feign Client 호출 (가공된 DTO 사용)
            PaymentRequestResponse response = accountSystemClient.requestEscrow(
                    accountRequest
            );

            log.info("[PAYMENT_REQUEST] Successfully received response from core system. ConfirmToken: {}",
                    response.getConfirmToken());
            return response;

        } catch (FeignException e) {
            // 3. Feign 통신 오류 및 계정계 응답 오류 처리
            log.error("[PAYMENT_REQUEST] Feign error occurred during core system call. Status: {}, Message: {}",
                    e.status(), e.contentUTF8(), e);

            // TODO: 계정계 오류 코드를 외부 시스템에 적합한 형태로 변환하여 던지는 로직 필요
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);

        } catch (Exception e) {
            // 4. 그 외 예상치 못한 예외 처리
            log.error("[PAYMENT_REQUEST] Unexpected error during request processing.", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 결제 승인 로직: 외부 요청을 받아 DTO를 가공하여 계정계로 전달
     */
    public PaymentApprovalResponse approvePayment(PaymentApprovalRequest request) {
        log.info("[PAYMENT_APPROVAL] Start processing request. OrderNo: {}", request.getOrderNo());

        // 1. 외부 요청 DTO를 계정계 전용 DTO로 변환/가공
        BankPaymentApprovalRequest accountRequest = convertToBankApprovalRequest(request);

        try {
            // 2. 계정계 Feign Client 호출
            PaymentApprovalResponse response = accountSystemClient.approvePayment(accountRequest);

            log.info("[PAYMENT_APPROVAL] Successfully received response from core system. EscrowId: {}",
                    response.getEscrowId());
            return response;

        } catch (FeignException e) {
            log.error("[PAYMENT_APPROVAL] Feign error occurred. Status: {}, Message: {}",
                    e.status(), e.contentUTF8(), e);
            //throw new CustomException(ErrorCode.BANK_API_ERROR, e.contentUTF8()); // 적절한 예외 변환
        } catch (Exception e) {
            log.error("[PAYMENT_APPROVAL] Unexpected error during request processing.", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 결제 요청 전용 DTO 변환
     */
    private BankPaymentAuthorizeRequest convertToAccountSystemRequest(PaymentAuthorizeRequest channelRequest) {

        // 1. PayerInfo 변환
        BasicAccountInfo payerInfo = BasicAccountInfo.builder()
                .accountNo(channelRequest.getPayerInfo().getAccountNo())
                .bankCode(channelRequest.getPayerInfo().getBankCode())
                .name(channelRequest.getPayerInfo().getName())
                .build();

        // 2. PayeeInfo 변환
        BasicAccountInfo payeeInfo = BasicAccountInfo.builder()
                .accountNo(channelRequest.getPayeeInfo().getAccountNo())
                .bankCode(channelRequest.getPayeeInfo().getBankCode())
                .name(channelRequest.getPayeeInfo().getName())
                .build();

        // 3. 메인 DTO 빌드 및 반환
        return BankPaymentAuthorizeRequest.builder()
                .amount(channelRequest.getAmount())
                .payerInfo(payerInfo)
                .payeeInfo(payeeInfo)
                .build();
    }

    /**
     * 결제 승인 전용 DTO 변환
     */
    private BankPaymentApprovalRequest convertToBankApprovalRequest(PaymentApprovalRequest channelRequest) {

        BasicAccountInfo bankPayerInfo = BasicAccountInfo.builder()
                .accountNo(channelRequest.getPayerInfo().getAccountNo())
                .bankCode(channelRequest.getPayerInfo().getBankCode())
                .name(channelRequest.getPayerInfo().getName())
                .build();

        BasicAccountInfo bankPayeeInfo = BasicAccountInfo.builder()
                .accountNo(channelRequest.getPayeeInfo().getAccountNo())
                .bankCode(channelRequest.getPayeeInfo().getBankCode())
                .name(channelRequest.getPayeeInfo().getName())
                .build();

        return BankPaymentApprovalRequest.builder()
                .orderNo(channelRequest.getOrderNo())
                .amount(channelRequest.getAmount())
                .payerInfo(bankPayerInfo)
                .payeeInfo(bankPayeeInfo)
                .build();
    }
}