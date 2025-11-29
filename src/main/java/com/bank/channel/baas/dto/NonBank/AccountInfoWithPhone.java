package com.bank.channel.baas.dto.NonBank;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AccountInfoWithPhone extends BasicAccountInfo {
    private final String phone;
}
