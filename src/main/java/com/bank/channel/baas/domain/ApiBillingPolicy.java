package com.bank.channel.baas.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_billing_policies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiBillingPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Long policyId;

    @Column(name = "merchant_id", nullable = false, length = 50)
    private String merchantId;

    @Column(name = "api_endpoint", nullable = false, length = 64)
    private String apiEndpoint;

    @Column(name = "unit_success_price", nullable = false)
    private Long unitSuccessPrice;

    @Column(name = "unit_client_error_price", nullable = false)
    private Long unitClientErrorPrice;

    @Column(name = "unit_server_error_price", nullable = false)
    private Long unitServcerErrorPrice;

    @Column(name = "effective_start_date")
    private LocalDateTime effectiveStartDate;

    @Column(name = "effective_end_date")
    private LocalDateTime effectiveEndDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
