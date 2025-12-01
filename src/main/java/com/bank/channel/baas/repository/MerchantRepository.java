package com.bank.channel.baas.repository;

import com.bank.channel.baas.domain.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Merchant Repository
 * API Key 기반 인증을 위한 조회 메서드 제공
 */
@Repository
public interface MerchantRepository extends JpaRepository<Merchant, String> {

    /**
     * API Key로 Merchant 조회
     * 
     * @param apiKey API Key
     * @return Merchant (Optional)
     */
    Optional<Merchant> findByApiKey(String apiKey);

    /**
     * merchantId와 apiKey가 모두 일치하는지 확인
     * 교차 검증용
     * 
     * @param merchantId Merchant ID
     * @param apiKey API Key
     * @return 존재 여부
     */
    boolean existsByMerchantIdAndApiKey(String merchantId, String apiKey);
}
