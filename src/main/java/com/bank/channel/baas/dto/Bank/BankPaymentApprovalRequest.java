package com.bank.channel.baas.dto.Bank;

import com.bank.channel.baas.dto.NonBank.BasicAccountInfo;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * [계정계 전용] 결제 승인 DTO
 */
@Builder
@Getter
public class BankPaymentApprovalRequest {
    private String orderNo; //고객사 주문번호
    private BigDecimal amount; // 결제 금액
    private BasicAccountInfo payerInfo; // 구매자 (워켓의 경우 의뢰인) 정보
    private BasicAccountInfo payeeInfo; // 판매자 (워켓의 경우 프리랜서) 정보

}
