package com.bank.channel.baas.repository;

import com.bank.channel.baas.domain.ContractData;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 계약 데이터 Repository
 */
public interface ContractDataRepository extends JpaRepository<ContractData, String> {
}
