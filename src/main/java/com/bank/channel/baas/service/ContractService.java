package com.bank.channel.baas.service;

import com.bank.channel.baas.domain.ContractData;
import com.bank.channel.baas.domain.repository.ContractDataRepository;
import com.bank.channel.baas.dto.NonBank.ContractRegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * Entity의 정적 팩토리 메서드 from()을 사용
     */
    private ContractData convertToEntity(ContractRegisterRequest request) {
        // contractId 생성 (UUID)
        String contractId = UUID.randomUUID().toString().replace("-", "");

        // Entity의 정적 팩토리 메서드 사용
        return ContractData.from(request, contractId);
    }
}
