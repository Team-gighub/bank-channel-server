package com.bank.channel.baas.dto.Bank;

import lombok.Builder;
import lombok.Getter;

/**
 * [계정계 전용]
 * [결제 승인] API (/payment/approval) 요청 DTO
 */
@Builder
@Getter
public class BankPaymentApprovalRequest {
    private final String escrowId;
    private final String confirmToken;

}
