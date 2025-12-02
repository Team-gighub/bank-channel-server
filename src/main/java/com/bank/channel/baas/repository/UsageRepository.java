package com.bank.channel.baas.repository;

import java.util.List;

import com.bank.channel.baas.domain.ApiCallLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * UsageRepository
 * ApiCallLog 엔티티를 사용해 기간별 API 호출 건수를 상태 코드 그룹별로 집계하는 쿼리 구현
 */
@Repository
public interface UsageRepository extends JpaRepository<ApiCallLog, Long> {

    /**
     * 특정 merchant_id 기간을 기준으로 API 호출 로그를 집계하여 상태 그룹별 건수를 반환합니다.
     * * [최적화 포인트]
     * 1. CASE 문과 GROUP BY를 사용하여 DB 레벨에서 한 번의 쿼리로 모든 집계를 완료합니다. (가장 효율적)
     * 2. merchant_id request_at(혹은 created_at) 필드에 인덱스가 있어야 최적의 성능을 냅니다.
     * 3. 집계 결과는 UsageAggregationResult DTO/Projection으로 자동 매핑됩니다.
     */
    @Query(value = """
        SELECT
            CASE
                WHEN status_code >= 200 AND status_code < 300 THEN '2XX' 
                WHEN status_code >= 400 AND status_code < 500 THEN '4XX' 
                WHEN status_code >= 500 AND status_code < 600 THEN '5XX'
                ELSE 'OTHER' 
            END AS statusGroup,
            COUNT(log_id) AS count
        FROM api_call_logs
        WHERE merchant_id = :merchantId
          AND request_at >= :startDate
          AND request_at <= :endDate
        GROUP BY statusGroup
    """, nativeQuery = true)
    List<UsageAggregationResult> aggregateUsagesByMerchantIdAndPeriod(
            @Param("merchantId") String merchantId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );
}
