package com.bank.channel.baas.dto.NonBank;

import com.bank.channel.baas.domain.enums.HoldStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * [결제 승인] API (/payment/approval) 응답 DTO
 */
@Getter
@Builder
public class PaymentApprovalResponse {
    private String escrowId; // 에스크로 결제 승인 TID
    private BigDecimal holdAmount;
    private HoldStatus holdStatus;
    private BigDecimal platformFee;
    private LocalDateTime holdStartDatetime;
    private String payerBankCode;
    private String payerAccount;
}
