package com.bank.channel.baas.dto.Bank;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

/**
 * [계정계 전용]
 * [결제 인증] API (/payment/authorize) 응답 DTO
 */
@Getter
@Builder
public class BankPaymentAuthorizeResponse {
    private final String orderNo;
    private final String confirmToken;
    private final String escrowId;
}
