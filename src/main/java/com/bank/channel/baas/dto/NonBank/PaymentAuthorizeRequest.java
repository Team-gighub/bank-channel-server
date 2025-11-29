package com.bank.channel.baas.dto.NonBank;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

/**
 * [결제 인증] API (/payment/authorize) 요청 DTO
 */
@Getter
@Builder
public class PaymentAuthorizeRequest {

    private final String merchantId; // 고객사 ID
    private final String userId; // 고객사 회원 ID ( 의뢰인 PK )
    private final String userName; // 고객사 회원명
    private final String productName; // 상품명 ( 워켓의 경우 계약명 )
    private final String orderNo; // 고객사 주문번호 ( 워켓의 경우 거래 PK )
    private final BigDecimal amount; // 결제 금액
    private final AccountInfoWithPhone payerInfo; // 구매자 ( 워켓의 경우 의뢰인 ) 정보
    private final AccountInfoWithPhone payeeInfo; // 판매자 ( 워켓의 경우 프리랜서 ) 정보
    private final String successUrl; // 성공 시, redirect url
    private final String failUrl; // 실패 시, redirect url

}