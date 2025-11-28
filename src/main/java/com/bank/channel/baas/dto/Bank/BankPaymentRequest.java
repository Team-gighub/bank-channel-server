package com.bank.channel.baas.dto.Bank;

import com.bank.channel.baas.dto.NonBank.BasicAccountInfo;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

/**
 * [계정계 전용] 결제 요청 DTO
 * - 채널계가 외부에서 받은 EscrowRequest를 가공하여 계정계로 보낼 때 사용
 */
@Builder
@Getter
public class BankPaymentRequest {

    private BigDecimal amount; // 결제 금액
    private BasicAccountInfo payerInfo; // 구매자 (워켓의 경우 의뢰인) 정보
    private BasicAccountInfo payeeInfo; // 판매자 (워켓의 경우 프리랜서) 정보
}
