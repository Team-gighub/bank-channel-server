package com.bank.channel.baas.dto.NonBank;

import com.bank.channel.baas.dto.Bank.BankPaymentAuthorizeResponse;
import com.bank.channel.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class PaymentAuthorizeResponse {

    private final boolean success;
    private final BankPaymentAuthorizeResponse response; // 성공 시 데이터
    private final ErrorCode errorCode;                  // 실패 시 에러 코드

    // 성공
    public static PaymentAuthorizeResponse success(BankPaymentAuthorizeResponse response) {
        return new PaymentAuthorizeResponse(true, response, null);
    }

    // 실패
    public static PaymentAuthorizeResponse fail(ErrorCode errorCode) {
        return new PaymentAuthorizeResponse(false, null, errorCode);
    }

    private PaymentAuthorizeResponse(boolean success, BankPaymentAuthorizeResponse response, ErrorCode errorCode) {
        this.success = success;
        this.response = response;
        this.errorCode = errorCode;
    }
}
