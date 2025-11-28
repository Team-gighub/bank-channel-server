package com.bank.channel.baas.controller;

import com.bank.channel.baas.dto.EscrowRequest;
import com.bank.channel.baas.dto.EscrowRequestResponse;
import com.bank.channel.baas.service.EscrowService;
import com.bank.channel.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * BaaS API 진입점 (결제 요청, 승인, 지급 확정)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/escrow")
public class EscrowController {

    private final EscrowService escrowService;

    /**
     * POST /api/v1/escrow/request
     * [결제 요청] API 엔드포인트: 외부 시스템의 요청을 처리합니다.
     */
    @PostMapping("/request")
    public ApiResponse<EscrowRequestResponse> requestEscrow(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
            @Valid @RequestBody EscrowRequest request
    ) {
        EscrowRequestResponse response = escrowService.requestEscrow(authorizationHeader, request);
        return ApiResponse.success(response);
    }

}
