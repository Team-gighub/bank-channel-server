package com.bank.channel.baas.dto.NonBank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
public class BasicAccountInfo {
    private final String accountNo;
    private final String bankCode;
    private final String name;
}
