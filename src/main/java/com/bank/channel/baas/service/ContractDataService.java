package com.bank.channel.baas.service;

import com.bank.channel.baas.domain.ContractData;
import com.bank.channel.baas.repository.ContractDataRepository;
import com.bank.channel.baas.dto.NonBank.ContractRegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ContractDataService {

    private final ContractDataRepository contractDataRepository;

    public void registerContract(ContractRegisterRequest request) {

        // 1. contract_id 생성 (UUID 사용 예시)
        String contractId = UUID.randomUUID().toString();
        // 2. DTO -> Entity 변환
        ContractData contractData = ContractData.from(request, contractId);
        // 3. DB 저장
        contractDataRepository.save(contractData);
    }
}
