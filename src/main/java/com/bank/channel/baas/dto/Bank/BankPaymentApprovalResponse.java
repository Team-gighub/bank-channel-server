package com.bank.channel.baas.dto.Bank;

import com.bank.channel.baas.domain.enums.HoldStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * [계정계 전용]
 * [결제 승인] API (/payment/approval) 응답 DTO
 */
@Builder
@Getter
public class BankPaymentApprovalResponse {
    private final String escrowId;
    private final BigDecimal holdAmount;
    private final HoldStatus holdStatus;
    private final BigDecimal platformFee;
    private final LocalDateTime holdStartDatetime;
}
