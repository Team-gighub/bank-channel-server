package com.bank.channel.baas.dto.Bank;

import com.bank.channel.baas.dto.NonBank.BasicAccountInfo;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

/**
 * [계정계 전용]
 * [결제 인증] API (/payment/authorize) 요청 DTO
 */
@Builder
@Getter
public class BankPaymentAuthorizeRequest {
    private final String orderNo;
    private final BigDecimal amount;
    private final BasicAccountInfo payerInfo;
    private final BasicAccountInfo payeeInfo;
}
