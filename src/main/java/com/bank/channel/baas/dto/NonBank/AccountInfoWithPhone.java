package com.bank.channel.baas.dto.NonBank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AccountInfoWithPhone extends BasicAccountInfo {
    private final String phone;

    @JsonCreator
    public AccountInfoWithPhone(
            @JsonProperty("phone") String phone,
            @JsonProperty("accountNo") String accountNo,  // BasicAccountInfo의 필드들
            @JsonProperty("bankCode") String bankCode,
            @JsonProperty("name") String name
    ) {
        super(accountNo, bankCode, name);
        this.phone = phone;
    }
}
