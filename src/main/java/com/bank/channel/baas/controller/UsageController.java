package com.bank.channel.baas.controller;

import com.bank.channel.baas.dto.NonBank.UsageRequest;
import com.bank.channel.baas.dto.NonBank.UsageResponse;
import com.bank.channel.baas.service.UsageService;
import com.bank.channel.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/usages")
public class UsageController {

    private final UsageService usageService;

    @GetMapping("")
    public ApiResponse<UsageResponse> getUsages(
            @Valid @RequestBody UsageRequest request) {

        UsageResponse response = usageService.getUsages(request);
        return ApiResponse.success(response);
    }

}
