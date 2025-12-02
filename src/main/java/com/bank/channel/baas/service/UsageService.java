package com.bank.channel.baas.service;

import com.bank.channel.baas.domain.ApiBillingPolicy;
import com.bank.channel.baas.dto.NonBank.UsageRequest;
import com.bank.channel.baas.dto.NonBank.UsageResponse;
import com.bank.channel.baas.repository.PolicyRepository;
import com.bank.channel.baas.repository.UsageAggregationResult;
import com.bank.channel.baas.repository.UsageRepository;
import com.bank.channel.global.exception.CustomException;
import com.bank.channel.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsageService {

    // 기본 정책
    private final Long DEFAULT_SUCCESS_PRICE = 10L;
    private final Long DEFAULT_FAIL_PRICE = 5L;

    private final UsageRepository usageRepository; // 집계 Repository 추가
    private final PolicyRepository policyRepository; // 정책 Repository 추가

    public UsageResponse getUsages(UsageRequest request) {
        // Service 시작 전, 날짜 유효성 검증 수행
        validateUsageRequest(request);

        // 1. startDate ~ endDate 집계 결과 results
        List<UsageAggregationResult> results = usageRepository.aggregateUsagesByMerchantIdAndPeriod(
                request.getMerchantId(), request.getStartDate(), request.getEndDate()
        );

        // 2. Group별 count를 매핑한 결과 countMap
        Map<String, Long> countMap = results.stream()
                .collect(Collectors.toMap(
                        UsageAggregationResult::getStatusGroup,
                        UsageAggregationResult::getCount
                ));

        // 3. 건수 집계
        Long totalSuccessCount = countMap.getOrDefault("2XX", 0L);
        Long totalClientErrorCount = countMap.getOrDefault("4XX", 0L);
        Long totalServerErrorCount = countMap.getOrDefault("5XX", 0L);
        Long totalFailureCount = totalClientErrorCount + totalServerErrorCount; // 실패 건수 합산
        Long totalCount = totalSuccessCount + totalFailureCount;

        // 4. 유효한 비용 정책 조회
        // 현재 날짜 기준 비용 정책 불러오기
        ApiBillingPolicy policy = policyRepository.findActiveGlobalPolicyByMerchantIdAndDate(
                request.getMerchantId(), LocalDate.now().toString()
        );

        // 5. 동적 비용 산정 로직 호출
        BigDecimal estimatedTotalCost = calculateCost(
                totalSuccessCount,
                totalClientErrorCount,
                totalServerErrorCount,
                policy // 조회된 정책 전달
        );

        // 6. 최종 응답 DTO 생성 및 반환
        return new UsageResponse(
                totalCount,
                totalSuccessCount,
                totalClientErrorCount,
                totalServerErrorCount,
                estimatedTotalCost
        );
    }

    /**
     * 성공/실패 건수와 비용 정책을 기반으로 예상 총 비용을 산정합니다.
     */
    private BigDecimal calculateCost(
            Long successCount,
            Long clientFailureConut,
            Long serverFailureCount,
            ApiBillingPolicy policy
    ) {
        // 1. 적용할 단위 비용 결정
        Long successPrice;
        Long clientErrorPrice;
        Long serverErrorPrice;

        if (policy != null && policy.getUnitSuccessPrice() != null && policy.getUnitClientErrorPrice() != null && policy.getUnitServcerErrorPrice() != null) {
            successPrice = policy.getUnitSuccessPrice();
            clientErrorPrice = policy.getUnitClientErrorPrice();
            serverErrorPrice = policy.getUnitServcerErrorPrice();
        } else {
            // 정책이 없을 경우 기본(Fallback) 가격 적용
            successPrice = DEFAULT_SUCCESS_PRICE;
            clientErrorPrice = DEFAULT_FAIL_PRICE;
            serverErrorPrice = DEFAULT_FAIL_PRICE;
            log.info("[BILLING POLICY] The basic rate policy, not the customer-specific rate, was applied.");
        }

        // 2. 비용 계산
        // 성공 비용 = 성공 건수 * 단위 성공 가격
        BigDecimal costSuccess = new BigDecimal(successCount).multiply(new BigDecimal(successPrice));

        // 실패 비용 = 실패 건수 * 단위 실패 가격
        BigDecimal costFailure =
                new BigDecimal(clientFailureConut).multiply(new BigDecimal(clientErrorPrice))
                        .add(new BigDecimal(serverFailureCount).multiply(new BigDecimal(serverErrorPrice)));

        // 3. 총 비용 계산 및 소수점 처리
        return costSuccess.add(costFailure)
                .setScale(4, RoundingMode.HALF_UP); // 소수점 4자리까지 처리 예시
    }

    private void validateUsageRequest(UsageRequest request) {
        try {
            LocalDate startDate = LocalDate.parse(request.getStartDate());
            LocalDate endDate = LocalDate.parse(request.getEndDate());

            if (startDate.isAfter(endDate)) {
                throw new CustomException(ErrorCode.INVALID_DATE_RANGE);
            }
        } catch (DateTimeParseException e) {
            throw new CustomException(ErrorCode.INVALID_DATE_FORMAT);
        }
    }
}