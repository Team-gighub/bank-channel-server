package com.bank.channel.baas.repository;

/**
 * PolicyRepository, UsageRespository에서 집계된 결과를 Service 계층으로 전달
 */
public interface UsageAggregationResult {

    /**
     * SQL 쿼리에서 AS 'statusGroup'으로 정의된 HTTP 상태 코드 그룹을 반환합니다.
     * (예: "2XX", "4XX", "5XX", "OTHER")
     * * @return 상태 그룹 문자열
     */
    String getStatusGroup();

    /**
     * SQL 쿼리에서 AS 'count'로 정의된 해당 그룹의 총 호출 건수를 반환합니다.
     * * @return 해당 그룹의 건수 (Long 타입으로 대용량 데이터 처리 대비)
     */
    Long getCount();
}
