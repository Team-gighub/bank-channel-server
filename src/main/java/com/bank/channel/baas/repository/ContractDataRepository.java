package com.bank.channel.baas.repository;

import com.bank.channel.baas.domain.ContractData;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 채널계 내부 DB의 계약 데이터를 관리하는 Repository
 * ContractData 엔티티와 Primary Key 타입(VARCHAR(50) -> String)을 사용합니다.
 */
public interface ContractDataRepository extends JpaRepository<ContractData, String> {
}
