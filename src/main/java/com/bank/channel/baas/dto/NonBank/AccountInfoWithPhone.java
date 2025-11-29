package com.bank.channel.baas.dto.NonBank;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountInfoWithPhone extends BasicAccountInfo {
    private final String phone;
}