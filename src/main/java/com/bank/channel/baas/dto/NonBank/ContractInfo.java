package com.bank.channel.baas.dto.NonBank;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ContractInfo {

    private final String title;
    private final BigDecimal amount;
    private final String startDate;
    private final String endDate;
    private final String description;
}
