package com.bank.channel.baas.domain;

import com.bank.channel.baas.domain.enums.MerchantStatus;
import com.bank.channel.baas.domain.enums.MerchantType;
import com.bank.channel.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "merchants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Merchant extends BaseEntity {

    @Id
    @Column(name = "merchant_id", length = 50)
    private String merchantId;

    @Column(name = "merchant_name", nullable = false, length = 100)
    private String merchantName;

    @Column(name = "business_reg_number", nullable = false, length = 20)
    private String businessRegNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "merchant_type", nullable = false)
    private MerchantType merchantType = MerchantType.PLATFORM;

    @Column(name = "api_key", nullable = false, length = 200)
    private String apiKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MerchantStatus status = MerchantStatus.ACTIVE;

    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL)
    private List<BaasEndUser> endUsers = new ArrayList<>();

}
