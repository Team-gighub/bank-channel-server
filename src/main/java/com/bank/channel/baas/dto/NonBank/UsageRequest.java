package com.bank.channel.baas.dto.NonBank;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UsageRequest {
    @NotBlank(message = "merchantId is required.")
    private final String merchantId;
    @NotBlank(message = "startDate is required.")
    private final String startDate;
    @NotBlank(message = "endDate is required.")
    private final String endDate;
}
