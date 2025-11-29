package com.bank.channel.baas.dto.NonBank;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

/**
 * 계약 메타데이터 DTO
 * 
 * 계약서 원본 + 메타데이터 정보를 담는 객체
 * S3에 올리는 메타데이터와 동일한 구조
 */
@Getter
@Builder
public class ContractMetadata {

    @NotNull(message = "계약 정보는 필수입니다")
    @Valid
    private final ContractInfo contractInfo; // 계약 세부 정보

    @NotNull(message = "의뢰인 정보는 필수입니다")
    @Valid
    private final ClientInfo clientInfo; // 의뢰인 정보

    @NotNull(message = "프리랜서 정보는 필수입니다")
    @Valid
    private final FreelancerInfo freelancerInfo; // 프리랜서 정보
}
