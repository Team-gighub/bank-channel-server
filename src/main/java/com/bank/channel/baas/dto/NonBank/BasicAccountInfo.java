package com.bank.channel.baas.dto.NonBank;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BasicAccountInfo {
    private String accountNo;
    private String bankCode;
    private String name;
}