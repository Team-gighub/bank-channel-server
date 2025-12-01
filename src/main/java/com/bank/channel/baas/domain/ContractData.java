package com.bank.channel.baas.domain;

import com.bank.channel.baas.dto.NonBank.ContractMetaData;
import com.bank.channel.baas.dto.NonBank.ContractRegisterRequest;
import com.bank.channel.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "contract_data")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContractData extends BaseEntity {

    @Builder
    private ContractData(
            String contractId,
            String paymentTid,
            String contractTitle,
            String contractDescription,
            String name,
            String phone,
            String businessSector,
            Long businessSectorYears,
            BigDecimal annualTotalIncome,
            String contractUrl
    ) {
        this.contractId = contractId;
        this.paymentTid = paymentTid;
        this.contractTitle = contractTitle;
        this.contractDescription = contractDescription;
        this.name = name;
        this.phone = phone;
        this.businessSector = businessSector;
        this.businessSectorYears = businessSectorYears;
        this.annualTotalIncome = annualTotalIncome;
        this.contractUrl = contractUrl;
    }

    @Id
    @Column(name = "contract_id", length = 50)
    private String contractId;

    @Column(name = "payment_tid", nullable = false, length = 50)
    private String paymentTid;

    @Column(name = "contract_title", length = 200)
    private String contractTitle;

    @Lob
    @Column(name = "contract_description")
    private String contractDescription;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "business_sector", length = 100)
    private String businessSector;

    @Column(name = "business_sector_years")
    private Long businessSectorYears;

    @Column(name = "annual_total_income", length = 255)
    private BigDecimal annualTotalIncome;

    @Lob
    @Column(name = "contract_url")
    private String contractUrl;

    @Column(name="hash")
    private String hash;

    // @Builder 등을 사용하지 않고, 생성자를 통해 필드를 초기화
    private ContractData(
            String contractId, String paymentTid, String contractTitle,
            String contractDescription, String name, String phone,
            String businessSector, Long businessSectorYears,
            BigDecimal annualTotalIncome, String contractUrl, String hash) {

        this.contractId = contractId;
        this.paymentTid = paymentTid;
        this.contractTitle = contractTitle;
        this.contractDescription = contractDescription;
        this.name = name;
        this.phone = phone;
        this.businessSector = businessSector;
        this.businessSectorYears = businessSectorYears;
        this.annualTotalIncome = annualTotalIncome;
        this.contractUrl = contractUrl;
        this.hash = hash;
    }

    /**
     * DTO와 생성된 ID를 기반으로 ContractData Entity를 생성하는 정적 팩토리 메서드
     */
    public static ContractData from(ContractRegisterRequest request, String contractId) {
        // 메타데이터에서 필요한 정보를 추출합니다.
        ContractMetaData metadata = request.getMetadata();

        return new ContractData(
                contractId, // 1. Service에서 생성한 ID 사용
                request.getPaymentId(),
                metadata.getContractInfo().getTitle(), // 메타데이터 > 계약 정보에서 추출
                metadata.getContractInfo().getDescription(), // Description 필드는 DTO에 추가
                metadata.getClientInfo().getName(), // 메타데이터 > 의뢰인 정보에서 추출
                metadata.getClientInfo().getPhone(), // 메타데이터 > 의뢰인 정보에서 추출
                request.getBusinessSector(),
                request.getBusinessSectorYears(),
                request.getAnnualIncomeTotal(),
                request.getContractUrl(),
                request.getHash()
        );
    }
}
