package com.bank.channel.baas.service;

import com.bank.channel.baas.domain.ContractData;
import com.bank.channel.baas.dto.NonBank.ContractRegisterRequest;
import com.bank.channel.baas.repository.ContractDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 계약 데이터 관리 Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractDataRepository contractDataRepository;

    /**
     * 계약 데이터 등록
     * 
     * @param request 계약 등록 요청 DTO
     * @return 저장된 계약 ID
     */
    @Transactional
    public String registerContract(ContractRegisterRequest request) {
        String traceId = MDC.get("traceId");
        
        log.info("[계약등록] 계약 데이터 저장 시작 - TraceId: {}, PaymentId: {}", 
                traceId, request.getPaymentId());

        // DTO → Entity 변환
        ContractData contractData = convertToEntity(request);
        
        // DB 저장
        ContractData saved = contractDataRepository.save(contractData);
        
        log.info("[계약등록] 계약 데이터 저장 완료 - TraceId: {}, ContractId: {}", 
                traceId, saved.getContractId());

        return saved.getContractId();
    }

    /**
     * DTO → Entity 변환
     * 
     * String → Long, String → BigDecimal 타입 변환 처리
     */
    private ContractData convertToEntity(ContractRegisterRequest request) {
        // contractId 생성 (UUID)
        String contractId = UUID.randomUUID().toString().replace("-", "");

        // String → Long 변환
        Long businessSectorYears = null;
        try {
            businessSectorYears = Long.parseLong(request.getBusinessSectorYears());
        } catch (NumberFormatException e) {
            log.warn("업력 변환 실패: {}", request.getBusinessSectorYears());
        }

        // String → BigDecimal 변환
        BigDecimal annualTotalIncome = null;
        try {
            annualTotalIncome = new BigDecimal(request.getAnnualIncomeTotal());
        } catch (NumberFormatException e) {
            log.warn("연간누적소득액 변환 실패: {}", request.getAnnualIncomeTotal());
        }

        return ContractData.builder()
                .contractId(contractId)
                .paymentTid(request.getPaymentId())
                .contractTitle(request.getMetadata().getContractInfo().getTitle())
                .contractDescription(null) // TODO: DTO에 없음, 필요시 추가
                .name(request.getMetadata().getClientInfo().getName())
                .phone(request.getMetadata().getClientInfo().getPhone())
                .businessSector(request.getBusinessSector())
                .businessSectorYears(businessSectorYears)
                .annualTotalIncome(annualTotalIncome)
                .contractUrl(request.getContractUrl())
                .build();
    }
}
