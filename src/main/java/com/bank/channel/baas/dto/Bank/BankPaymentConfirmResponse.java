package com.bank.channel.baas.dto.Bank;

import lombok.Builder;
import lombok.Getter;

/**
 * [계정계 전용]
 * [지급 확정] API (/payment/confirm) 응답 DTO
 */
@Getter
@Builder
public class BankPaymentConfirmResponse {
    private final String paymentId; // 지급확정 TID
}
