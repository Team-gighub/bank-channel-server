package com.bank.channel.baas.controller;

import com.bank.channel.baas.dto.SampleRequest;
import com.bank.channel.baas.dto.SampleResponse;
import com.bank.channel.baas.service.SampleService;
import com.bank.channel.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sample")
public class SampleController {

    private final SampleService sampleService;

    @PostMapping
    public ApiResponse<SampleResponse> create(@RequestBody SampleRequest request) {
        SampleResponse response = sampleService.process(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    public ApiResponse<SampleResponse> getOne(@PathVariable Long id) {
        SampleResponse response = sampleService.findOne(id);
        return ApiResponse.success(response);
    }
}
