package com.bank.channel.baas.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

/**
 * [계정계 전용] 결제 요청 DTO
 * - 채널계가 외부에서 받은 EscrowRequest를 가공하여 계정계로 보낼 때 사용
 */
@Builder
@Getter
public class BankEscrowRequest {

    private BigDecimal amount; // 결제 금액
    private PayerInfo payerInfo; // 구매자 (워켓의 경우 의뢰인) 정보
    private PayeeInfo payeeInfo; // 판매자 (워켓의 경우 프리랜서) 정보

    /**
     * 구매자(의뢰인) 정보 - Nested Object
     */
    @Builder
    @Getter
    public static class PayerInfo {
        private String accountNo; // 출금계좌번호
        private String bankCode; // 출금계좌 은행코드
        private String name; // 구매자명
    }

    /**
     * 판매자(프리랜서) 정보 - Nested Object
     */
    @Builder
    @Getter
    public static class PayeeInfo {
        private String accountNo; // 입금계좌번호
        private String bankCode; // 입금계좌 은행코드
        private String name; // 판매자명
    }
}
