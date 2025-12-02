package com.bank.channel.baas.controller;

import com.bank.channel.baas.dto.NonBank.ContractRegisterRequest;
import com.bank.channel.baas.service.ContractDataService;
import com.bank.channel.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/contracts")
public class ContractDataController {

    private final ContractDataService contractDataService;

    /**
     * POST /contracts/register : 계약 데이터 등록 API
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> registerContract(@Valid @RequestBody ContractRegisterRequest request) {
        contractDataService.registerContract(request);
        return ApiResponse.success();
    }
}
