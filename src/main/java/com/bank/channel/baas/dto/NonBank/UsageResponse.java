package com.bank.channel.baas.dto.NonBank;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class UsageResponse {
    private final Long totalCount;
    private final Long totalSuccessCount;
    private final Long totalClientErrorCount;
    private final Long totalServerErrorCount;
    private final BigDecimal estimatedTotalCost;
}
