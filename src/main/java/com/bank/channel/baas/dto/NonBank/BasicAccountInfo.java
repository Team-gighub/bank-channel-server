package com.bank.channel.baas.dto.NonBank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class BasicAccountInfo {
    private final String accountNo;
    private final String bankCode;
    private final String name;
}
