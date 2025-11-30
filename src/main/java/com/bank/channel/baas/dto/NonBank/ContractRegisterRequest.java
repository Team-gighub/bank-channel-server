package com.bank.channel.baas.dto.NonBank;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ContractRegisterRequest {

    private final String paymentId; // 지급확정 TID
    private final String orderNo; // 거래 PK
    private final String userId; // 워켓 유저 PK
    private final String businessSector; // 업종
    private final Long businessSectorYears; // 업력
    private final BigDecimal annualIncomeTotal; // 연간누적소득액
    private final String contractUrl; // 계약서 원본 S3 URL
    private final String hash; // 계약서 원본 + 메타데이터 hash값
    private final ContractMetaData metadata; // 계약서 메타 데이터
}