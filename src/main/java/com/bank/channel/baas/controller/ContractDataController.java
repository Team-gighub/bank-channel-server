package com.bank.channel.baas.controller;

import com.bank.channel.baas.dto.NonBank.ContractRegisterRequest;
import com.bank.channel.baas.service.ContractDataService;
import com.bank.channel.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contracts")
public class ContractDataController {

    private final ContractDataService contractDataService;

    /**
     * POST /contracts/register : 계약 데이터 등록 API
     */
    @PostMapping("/register")
    public ApiResponse<Void> registerContract(@RequestBody ContractRegisterRequest request) {

        String contractId = contractDataService.registerContract(request);
        return ApiResponse.success();
    }
}
