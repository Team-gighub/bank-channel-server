package com.bank.channel.baas.service;

import com.bank.channel.baas.dto.SampleRequest;
import com.bank.channel.baas.dto.SampleResponse;
import com.bank.channel.global.exception.CustomException;
import com.bank.channel.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SampleService {

    public SampleResponse process(SampleRequest request) {

        if (request.getAmount() <= 0) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return new SampleResponse("ok", request.getAmount());
    }

    public SampleResponse findOne(Long id) {

        if (id < 0) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // DB 조회 생략
        return new SampleResponse("found", 1000);
    }
}