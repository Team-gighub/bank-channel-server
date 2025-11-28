package com.bank.channel.baas.dto.NonBank;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

/**
 * [결제 요청] API (/payment/request) 요청 DTO
 */
@Builder
@Getter
public class PaymentRequest {

    // 필수 항목 (Required)
    private String merchantId; // 고객사 아이디
    private String userId; // 고객사 회원 ID (의뢰인 PK)
    private String userName; // 고객사 회원명
    private String productName; // 상품명 (워켓의 경우 계약명)
    private BigDecimal amount; // 결제 금액
    private String orderNo; // 고객사 주문번호 (워켓의 경우 거래 PK)
    private String successUrl; // 결제 성공 후 리다이렉트 URL
    private String failUrl; // 결제 실패 후 리다이렉트 URL
    private AccountInfoWithPhone payerInfo; // 구매자 (워켓의 경우 의뢰인) 정보
    private AccountInfoWithPhone payeeInfo; // 판매자 (워켓의 경우 프리랜서) 정보

}