package com.bank.channel.baas.dto.NonBank;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BasicAccountInfo {
    private final String accountNo;
    private final String bankCode;
    private final String name;
}