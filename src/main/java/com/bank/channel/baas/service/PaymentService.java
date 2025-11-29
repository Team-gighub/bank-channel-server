package com.bank.channel.baas.service;

import com.bank.channel.baas.dto.Bank.BankPaymentApprovalResponse;
import com.bank.channel.baas.dto.Bank.BankPaymentAuthorizeRequest;
import com.bank.channel.baas.dto.Bank.BankPaymentAuthorizeResponse;
import com.bank.channel.baas.dto.Bank.BankPaymentConfirmResponse;
import com.bank.channel.baas.dto.NonBank.*;
import com.bank.channel.global.exception.CustomException;
import com.bank.channel.global.exception.ErrorCode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

/**
 * 결제 관련 비즈니스 로직 처리 (계정계 Feign Client 호출 및 채널계 로깅/예외 처리 담당)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final AccountSystemClient accountSystemClient;

    /**
     * 결제 인증 로직
     * 응답: HTTP Body 없음. 성공 시 successUrl로 리다이렉션 (쿼리 파라미터로 confirmToken, escrowId 전달)
     */
    public void authorizePayment(PaymentAuthorizeRequest request) {
        String traceId = MDC.get("traceId");
        log.info("[PAYMENT_AUTHORIZE] Start processing request. TraceId: {}, OrderNo: {}", traceId, request.getOrderNo());

        // 1. 외부 요청 DTO를 계정계 전용 DTO로 변환/가공
        BankPaymentAuthorizeRequest accountRequest = convertToAccountSystemRequest(request);

        try {
            // 2. 계정계 Feign Client 호출 (가공된 DTO 사용)
            BankPaymentAuthorizeResponse response = accountSystemClient.authorizePayment(accountRequest);
            log.info("[PAYMENT_AUTHORIZE] Successfully received response from core system. TraceId: {}, ConfirmToken: {}, EscrowId: {}", traceId, response.getConfirmToken(), response.getEscrowId());
        } catch (FeignException e) {
            // 3. Feign 통신 오류 및 계정계 응답 오류 처리
            log.error("[PAYMENT_AUTHORIZE] Feign error occurred during core system call. TraceId: {}, Status: {}, Message: {}", traceId, e.status(), e.contentUTF8(), e);

            // TODO: 계정계 오류 코드를 외부 시스템에 적합한 형태로 변환하여 던지는 로직 필요
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);

        } catch (Exception e) {
            // 4. 그 외 예상치 못한 예외 처리
            log.error("[PAYMENT_AUTHORIZE] Unexpected error during request processing. TraceId: {}", traceId, e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 결제 승인 로직: 외부 요청을 받아 DTO를 계정계로 전달
     * 요청: escrowId, confirmToken
     * 응답: escrowId
     */
    public PaymentApprovalResponse approvePayment(PaymentApprovalRequest request) {
        String traceId = MDC.get("traceId");
        log.info("[PAYMENT_APPROVAL] Start processing request. TraceId: {}, EscrowId: {}", traceId, request.getEscrowId());

        try {
            // 1. 계정계 Feign Client 호출
            BankPaymentApprovalResponse response = accountSystemClient.approvePayment(request);
            log.info("[PAYMENT_APPROVAL] Successfully received response from core system. TraceId: {}, EscrowId: {}", traceId, response.getEscrowId());
            // 2. 계정계 응답 DTO를 외부 응답 DTO로 변환하여 반환
            return PaymentApprovalResponse.builder()
                    .escrowId(response.getEscrowId())
                    .build();

        } catch (FeignException e) {
            log.error("[PAYMENT_APPROVAL] Feign error occurred. TraceId: {}, Status: {}, Message: {}", traceId, e.status(), e.contentUTF8(), e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR); // TODO: 적절한 예외 반환
        } catch (Exception e) {
            log.error("[PAYMENT_APPROVAL] Unexpected error during request processing. TraceId: {}", traceId, e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * TODO: 구체화되면 수정 필요
     * 지급 확정 로직: 외부 요청을 받아 DTO를 계정계로 전달
     * 요청: marchantId, reqYmd, escrowId, changerId
     * 응답: paymentId
     */
    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest request) {
        String traceId = MDC.get("traceId");
        log.info("[PAYMENT_CONFIRM] Start processing request. TraceId: {}, EscrowId: {}", traceId, request.getEscrowId());

        try {
            // 1. 계정계 Feign Client 호출
            BankPaymentConfirmResponse response = accountSystemClient.confirmPayment(request);
            log.info("[PAYMENT_CONFIRM] Successfully received response from core system. TraceId: {}, PaymentId: {}", traceId, response.getPaymentId());
            // 2. 계정계 응답 DTO를 외부 응답 DTO로 변환하여 반환
            return PaymentConfirmResponse.builder()
                    .paymentId(response.getPaymentId())
                    .build();

        } catch (FeignException e) {
            log.error("[PAYMENT_CONFIRM] Feign error occurred. TraceId: {}, Status: {}, Message: {}", traceId, e.status(), e.contentUTF8(), e);
             throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR); // TODO: 적절한 예외 반환
        } catch (Exception e) {
            log.error("[PAYMENT_CONFIRM] Unexpected error during request processing. TraceId: {}", traceId, e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }



    /**
     * 결제 인증 요청 전용 DTO 변환
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

}
