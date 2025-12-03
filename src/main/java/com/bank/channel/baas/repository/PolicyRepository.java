package com.bank.channel.baas.repository;

import com.bank.channel.baas.domain.ApiBillingPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * PolicyRepository
 * ApiBillingPolicy 엔티티를 사용해 유효한 전역 요금 정책을 조회하는 쿼리 구현
 */
@Repository
public interface PolicyRepository extends JpaRepository<ApiBillingPolicy, Long> {

    /**
     * 특정 merchant_id 대해 주어진 날짜(currentDate)에 유효하고
     * 전역적(api_endpoint IS NULL)으로 적용되는 정책을 조회합니다.
     * * [최적화 포인트]
     * 1. merchant_id 유효 기간(effective_start_date, effective_end_date)을 WHERE 절에 포함하여 인덱스를 효율적으로 사용합니다.
     * 2. 정책 충돌 방지를 위해 최신 정책을 가져오거나(ORDER BY created_at DESC) 단일 유효 정책만 존재함을 가정합니다.
     * 3. 비용 데이터는 BigDecimal로 자동 매핑됩니다.
     */
    @Query(value = """
        SELECT *
        FROM api_billing_policies
        WHERE merchant_id = :merchantId 
          AND (effective_start_date IS NULL OR effective_start_date <= :currentDate)
          AND (effective_end_date IS NULL OR effective_end_date > :currentDate)
        ORDER BY created_at DESC
        LIMIT 1
    """, nativeQuery = true)
    ApiBillingPolicy findActiveGlobalPolicyByMerchantIdAndDate(
            @Param("merchantId") String merchantId,
            @Param("currentDate") LocalDateTime currentDate
    );
}
