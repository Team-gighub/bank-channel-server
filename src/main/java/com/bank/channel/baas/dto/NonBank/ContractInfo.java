package com.bank.channel.baas.dto.NonBank;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 계약 정보 DTO
 * 
 * 계약 메타데이터에서 계약 세부 정보를 담는 객체
 */
@Getter
@Builder
public class ContractInfo {

    @NotBlank(message = "계약 제목은 필수입니다")
    private final String title; // 계약 제목

    @NotNull(message = "계약 금액은 필수입니다")
    private final BigDecimal amount; // 계약 금액

    @NotBlank(message = "계약 시작일은 필수입니다")
    private final String startDate; // 계약 시작일 (YYYY-MM-DD 형식 권장)

    @NotBlank(message = "계약 종료일은 필수입니다")
    private final String endDate; // 계약 종료일 (YYYY-MM-DD 형식 권장)
}
