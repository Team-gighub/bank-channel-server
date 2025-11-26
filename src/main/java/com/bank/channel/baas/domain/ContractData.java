package com.bank.channel.baas.domain;

import com.bank.channel.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "contract_data")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContractData extends BaseEntity {

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
}
