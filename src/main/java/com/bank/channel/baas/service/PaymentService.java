package com.bank.channel.baas.service;

import com.bank.channel.baas.dto.Bank.BankPaymentApprovalResponse;
import com.bank.channel.baas.dto.Bank.BankPaymentAuthorizeRequest;
import com.bank.channel.baas.dto.Bank.BankPaymentAuthorizeResponse;
import com.bank.channel.baas.dto.Bank.BankPaymentConfirmResponse;
import com.bank.channel.baas.dto.NonBank.*;
import com.bank.channel.global.exception.CustomException;
import com.bank.channel.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 결제 관련 비즈니스 로직 처리 (계정계 Feign Client 호출 및 채널계 로깅/예외 처리 담당)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final AccountSystemClient accountSystemClient;
    private final EndUserService endUserService;
    private final MerchantService merchantService;
    private final ObjectMapper objectMapper;

    /**
     * 결제 인증 로직
     * 응답: orderNo, confirmToken, escrowId
     */
    @Transactional
    public PaymentAuthorizeResponse authorizePayment(PaymentAuthorizeRequest request) {
        log.info("[PAYMENT_AUTHORIZE] Start processing request. OrderNo: {}", request.getOrderNo());

        // 0. BaaS End User 및 계좌 정보 저장/업데이트 (트랜잭션 내에서 처리됨)
        //merchantService.saveOrUpdate(request); // merchant table은 mock 데이터 사용
        endUserService.saveOrUpdateUserAndAccount(request);

        // 1. 외부 요청 DTO를 계정계 전용 DTO로 변환/가공
        BankPaymentAuthorizeRequest accountRequest = convertToAccountSystemRequest(request);

        try {
            // 2. 계정계 Feign Client 호출 (가공된 DTO 사용)
            BankPaymentAuthorizeResponse response = accountSystemClient.authorizePayment(accountRequest).getData();
            log.info("[PAYMENT_AUTHORIZE] Successfully received response from core system. ConfirmToken: {}, EscrowId: {}", response.getConfirmToken(), response.getEscrowId());
            return PaymentAuthorizeResponse.success(response);
        } catch (FeignException e) {
            // 3. Feign 통신 오류 및 계정계 응답 오류 처리
            log.error("[PAYMENT_AUTHORIZE] Feign error occurred during core system call. Status: {}, Message: {}", e.status(), e.contentUTF8(), e);
            ErrorCode mappedError = mapFeignExceptionToErrorCode(e);
            return PaymentAuthorizeResponse.fail(mappedError);

        } catch (Exception e) {
            // 4. 그 외 예상치 못한 예외 처리
            log.error("[PAYMENT_AUTHORIZE] Unexpected error during request processing.", e);
            return PaymentAuthorizeResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 결제 승인 로직: 외부 요청을 받아 DTO를 계정계로 전달
     * 요청: escrowId, confirmToken
     * 응답: escrowId
     */
    public PaymentApprovalResponse approvePayment(PaymentApprovalRequest request) {
        log.info("[PAYMENT_APPROVAL] Start processing request. EscrowId: {}", request.getEscrowId());

        try {
            // 1. 계정계 Feign Client 호출
            BankPaymentApprovalResponse response = accountSystemClient.approvePayment(request).getData();
            log.info("[PAYMENT_APPROVAL] Successfully received response from core system. EscrowId: {}", response.getEscrowId());
            // 2. 계정계 응답 DTO를 외부 응답 DTO로 변환하여 반환
            return PaymentApprovalResponse.builder()
                    .escrowId(response.getEscrowId())
                    .holdAmount(response.getHoldAmount())
                    .holdStartDatetime(response.getHoldStartDatetime())
                    .holdStatus(response.getHoldStatus())
                    .platformFee(response.getPlatformFee())
                    .payerBankCode(response.getPayerBankCode())
                    .payerAccount(response.getPayerAccount())
                    .build();

        } catch (FeignException e) {
            log.error("[PAYMENT_APPROVAL] Feign error occurred. Status: {}, Message: {}", e.status(), e.contentUTF8(), e);
            // 계정계 응답을 분석하여 커스텀 예외로 변환
            ErrorCode mappedError = mapFeignExceptionToErrorCode(e);
            throw new CustomException(mappedError);
        } catch (Exception e) {
            log.error("[PAYMENT_APPROVAL] Unexpected error during request processing.", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 지급 확정 로직: 외부 요청을 받아 DTO를 계정계로 전달
     * 요청: marchantId, reqYmd, escrowId, changerId
     * 응답: paymentId
     */
    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest request) {
        log.info("[PAYMENT_CONFIRM] Start processing request. EscrowId: {}", request.getEscrowId());

        try {
            // 1. 계정계 Feign Client 호출
            BankPaymentConfirmResponse response = accountSystemClient.confirmPayment(request).getData();
            log.info("[PAYMENT_CONFIRM] Successfully received response from core system. PaymentId: {}", response.getPaymentId());
            // 2. 계정계 응답 DTO를 외부 응답 DTO로 변환하여 반환
            return PaymentConfirmResponse.builder()
                    .paymentId(response.getPaymentId())
                    .build();

        } catch (FeignException e) {
            log.error("[PAYMENT_CONFIRM] Feign error occurred. Status: {}, Message: {}", e.status(), e.contentUTF8(), e);
            // 계정계 응답을 분석하여 커스텀 예외로 변환
            ErrorCode mappedError = mapFeignExceptionToErrorCode(e);
            throw new CustomException(mappedError);
        } catch (Exception e) {
            log.error("[PAYMENT_CONFIRM] Unexpected error during request processing.", e);
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


    /**
     * 에러 매핑 로직
     * : FeignException으로부터 계정계 오류 코드를 추출하여 Channel계 ErrorCode로 매핑합니다.
     * @param e FeignException
     * @return 매핑된 ErrorCode (매핑 실패 시 INTERNAL_SERVER_ERROR 반환)
     */
    private ErrorCode mapFeignExceptionToErrorCode(FeignException e) {
        // 1. 은행 서버 에러 (당/타행) 처리
        if (e.status() >= 500) {
            log.error("[Account System Error] Downstream system returned status. Status: {}", e.status());
            return ErrorCode.INTERNAL_SERVER_ERROR;
        }

        try {
            // Feign 응답 본문을 JSON으로 파싱하여 계정계 오류 코드를 추출합니다.
            String content = e.contentUTF8();
            if (content == null || content.isEmpty()) {
                log.warn("[Feign Mapping] Empty content from Feign exception. Default to INTERNAL_SERVER_ERROR.");
                return ErrorCode.INTERNAL_SERVER_ERROR;
            }

            // ObjectMapper를 사용해 JSON 파싱
            JsonNode root = objectMapper.readTree(content);
            JsonNode errorNode = root.path("error");
            String accountErrorCode = errorNode.path("code").asText();

            log.info("[Feign Mapping] Received core error code: {}", accountErrorCode);

            // 2. 계정계 비즈니스 오류 코드 매핑

            ErrorCode mappedErrorCode = ErrorCode.fromCode(accountErrorCode);

            if (mappedErrorCode != null) {
                log.warn("[Feign Mapping] Mapped core error {} to {}.", accountErrorCode, mappedErrorCode.name(),e);
                return mappedErrorCode;
            }

            // 계정계에서 정의되지 않은 비즈니스 코드 (4xx)가 넘어온 경우
            log.warn("[Feign Mapping] Received unknown core error code: {}. Defaulting to INTERNAL_SERVER_ERROR.", accountErrorCode);
            return ErrorCode.INTERNAL_SERVER_ERROR;
        } catch (Exception parseException) {
            log.error("[Feign Mapping Error] Failed to parse Feign response body for error mapping. Status: {}", e.status(), parseException);
        }

        // 특정 오류로 매핑되지 않거나 JSON 파싱에 실패한 경우
        return ErrorCode.INTERNAL_SERVER_ERROR;
    }

}
