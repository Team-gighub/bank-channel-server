package com.bank.channel.baas.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * [결제 요청] API (/api/v1/payment/request) 요청 DTO
 */
@Builder
@Getter
public class EscrowRequest {

    // 필수 항목 (Required)
    private String marchantId; // 고객사 아이디
    private String userId; // 고객사 회원 ID (의뢰인 PK)
    private String userName; // 고객사 회원명
    private String productName; // 상품명 (워켓의 경우 계약명)
    private String amount; // 결제 금액
    private String orderNo; // 고객사 주문번호 (워켓의 경우 거래 PK)
    private String successUrl; // 결제 성공 후 리다이렉트 URL
    private String failUrl; // 결제 실패 후 리다이렉트 URL
    private PayerInfo payerInfo; // 구매자 (워켓의 경우 의뢰인) 정보
    private PayeeInfo payeeInfo; // 판매자 (워켓의 경우 프리랜서) 정보

    /**
     * 구매자(의뢰인) 정보 - Nested Object
     */
    @Getter
    @Builder
    public static class PayerInfo {
        private String accountNo; // 출금계좌번호
        private String bankCode; // 출금계좌 은행코드
        private String name; // 구매자명
        private String phone; // 구매자 휴대폰 번호
    }

    /**
     * 판매자(프리랜서) 정보 - Nested Object
     */
    @Getter
    @Builder
    public static class PayeeInfo {
        private String accountNo; // 입금계좌번호
        private String bankCode; // 입금계좌 은행코드
        private String name; // 판매자명
        private String phone; // 구매자 휴대폰 번 (※ API 명세에 '구매자 휴대폰 번'으로 되어 있으나, '판매자 휴대폰 번호'로 해석하여 사용해야 할 수 있음)
    }
}