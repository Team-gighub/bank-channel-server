package com.bank.channel.baas.dto.NonBank;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

/**
 * [계약 데이터 등록] API (/contracts/register) 요청 DTO
 * 
 * 에스크로 결제의 마지막 단계로, 지급확정 후 계약 정보를 저장하는 용도
 */
@Getter
@Builder
public class ContractRegisterRequest {

    @NotBlank(message = "지급확정 TID는 필수입니다")
    private final String paymentId; // 지급확정 TID

    @NotBlank(message = "거래 PK는 필수입니다")
    private final String orderNo; // 거래 PK (워켓의 거래 PK)

    @NotBlank(message = "워켓 유저 PK는 필수입니다")
    private final String userId; // 워켓 유저 PK

    @NotBlank(message = "업종은 필수입니다")
    private final String businessSector; // 업종

    @NotBlank(message = "업력은 필수입니다")
    private final String businessSectorYears; // 업력

    @NotBlank(message = "연간누적소득액은 필수입니다")
    private final String annualIncomeTotal; // 연간누적소득액

    @NotBlank(message = "계약서 원본 URL은 필수입니다")
    private final String contractUrl; // 계약서 원본 S3 URL

    @NotBlank(message = "해시값은 필수입니다")
    private final String hash; // 계약서 원본 + 메타데이터 hash값

    @NotNull(message = "계약 메타데이터는 필수입니다")
    @Valid
    private final ContractMetadata metadata; // 계약서 메타데이터 (S3에 올리는 메타데이터와 동일 구조)
}
