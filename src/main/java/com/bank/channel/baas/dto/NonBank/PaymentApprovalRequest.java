package com.bank.channel.baas.dto.NonBank;

import lombok.Builder;
import lombok.Getter;

/**
 * [결제 승인] API (/payment/approval) 요청 DTO
 */
@Getter
@Builder
public class PaymentApprovalRequest {
    private final String escrowId;
    private final String confirmToken;
}
