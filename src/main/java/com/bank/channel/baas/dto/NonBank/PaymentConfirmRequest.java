package com.bank.channel.baas.dto.NonBank;

import lombok.Builder;
import lombok.Getter;

/**
 *  * [지급 확정] API (/payment/confirm) 요청 DTO
 */
@Getter
@Builder
public class PaymentConfirmRequest {
    private final String merchantId; // 고객사 아이디
    private final String escrowId; // 거래번호 ( 에스크로 결제 승인 TID )

}
