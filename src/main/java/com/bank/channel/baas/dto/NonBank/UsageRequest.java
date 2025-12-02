package com.bank.channel.baas.dto.NonBank;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsageRequest {
    @NotNull
    private final String merchantId;
    @NotNull
    private final String startDate;
    @NotNull
    private final String endDate;
}
