package com.bank.channel.baas.dto.NonBank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BasicAccountInfo {
    private final String accountNo;
    private final String bankCode;
    private final String name;
}