package com.bank.channel.baas.service;

import com.bank.channel.baas.dto.AccountSystemEscrowRequest;
import com.bank.channel.baas.dto.EscrowRequest;
import com.bank.channel.baas.dto.EscrowRequestResponse;
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
    public EscrowRequestResponse requestEscrow(String authorizationHeader, EscrowRequest request) {
        log.info("[ESCROW_REQUEST] Start processing request. OrderNo: {}", request.getOrderNo());

        // 1. 외부 요청 DTO를 계정계 전용 DTO로 변환/가공
        AccountSystemEscrowRequest accountRequest = convertToAccountSystemRequest(request);

        try {
            // 2. 계정계 Feign Client 호출 (가공된 DTO 사용)
            EscrowRequestResponse response = accountSystemClient.requestEscrow(
                    authorizationHeader,
                    accountRequest
            );

            log.info("[ESCROW_REQUEST] Successfully received response from core system. ConfirmToken: {}",
                    response.getConfirmToken());
            return response;

        } catch (FeignException e) {
            // 3. Feign 통신 오류 및 계정계 응답 오류 처리
            log.error("[ESCROW_REQUEST] Feign error occurred during core system call. Status: {}, Message: {}",
                    e.status(), e.contentUTF8(), e);

            // TODO: 계정계 오류 코드를 외부 시스템에 적합한 형태로 변환하여 던지는 로직 필요
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);

        } catch (Exception e) {
            // 4. 그 외 예상치 못한 예외 처리
            log.error("[ESCROW_REQUEST] Unexpected error during request processing.", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 전용 DTO 변환 로직 (가공)
     * - EscrowRequest -> AccountSystemEscrowRequest로 변환하며 불필요한 필드(예: phone)를 제거합니다.
     */
    private AccountSystemEscrowRequest convertToAccountSystemRequest(EscrowRequest channelRequest) {

        // 1. PayerInfo 변환
        AccountSystemEscrowRequest.PayerInfo payerInfo = AccountSystemEscrowRequest.PayerInfo.builder()
                .accountNo(channelRequest.getPayerInfo().getAccountNo())
                .bankCode(channelRequest.getPayerInfo().getBankCode())
                .name(channelRequest.getPayerInfo().getName())
                .build();

        // 2. PayeeInfo 변환
        AccountSystemEscrowRequest.PayeeInfo payeeInfo = AccountSystemEscrowRequest.PayeeInfo.builder()
                .accountNo(channelRequest.getPayeeInfo().getAccountNo())
                .bankCode(channelRequest.getPayeeInfo().getBankCode())
                .name(channelRequest.getPayeeInfo().getName())
                .build();

        // 3. 메인 DTO 빌드 및 반환
        return AccountSystemEscrowRequest.builder()
                .amount(channelRequest.getAmount())
                .orderNo(channelRequest.getOrderNo())
                .payerInfo(payerInfo)
                .payeeInfo(payeeInfo)
                .build();
    }
}