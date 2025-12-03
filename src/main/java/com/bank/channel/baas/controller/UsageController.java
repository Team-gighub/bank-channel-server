package com.bank.channel.baas.controller;

import com.bank.channel.baas.dto.NonBank.UsageResponse;
import com.bank.channel.baas.service.UsageService;
import com.bank.channel.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/usages")
public class UsageController {

    private final UsageService usageService;

    @GetMapping("/{merchantId}")
    public ApiResponse<UsageResponse> getUsages(
            @PathVariable String merchantId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        UsageResponse response = usageService.getUsages(merchantId, startDate, endDate);
        return ApiResponse.success(response);
    }

}
