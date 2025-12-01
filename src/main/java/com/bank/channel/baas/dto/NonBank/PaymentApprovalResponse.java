package com.bank.channel.baas.dto.NonBank;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * [결제 승인] API (/payment/approval) 응답 DTO
 */
@Getter
@Builder
public class PaymentApprovalResponse {
    private final String escrowId; // 에스크로 결제 승인 TID
    private final BigDecimal amount; //대금
    private final String status; //대금예치 상태
    private final String createdAt; //결제일
    private final String releaseDate; //지급확정일(예정일)
}
