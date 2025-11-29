package com.bank.channel.baas.dto.NonBank;

import lombok.Builder;
import lombok.Getter;

/**
 * [지급 확정] API (/payment/confirm) 응답 DTO
 */
@Builder
@Getter
public class PaymentConfirmResponse {
    private final String paymentId; // 지급확정 TID
}
