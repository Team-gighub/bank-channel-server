package com.bank.channel.baas.dto.Bank;

import lombok.Builder;
import lombok.Getter;

/**
 * [계정계 전용]
 * [결제 승인] API (/payment/approval) 응답 DTO
 */
@Builder
@Getter
public class BankPaymentApprovalResponse {
    private final String escrowId;
}
