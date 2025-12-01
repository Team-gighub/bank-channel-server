package com.bank.channel.baas.service;

import com.bank.channel.baas.domain.Merchant;
import com.bank.channel.baas.dto.NonBank.PaymentAuthorizeRequest;
import com.bank.channel.baas.repository.MerchantRepository;
import com.bank.channel.global.exception.CustomException;
import com.bank.channel.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 가맹점(Merchant) 관련 비즈니스 로직 처리.
 * 결제 인증 요청(PaymentAuthorizeRequest)을 통해 가맹점 정보를 DB에 저장/업데이트합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MerchantService {

    private final MerchantRepository merchantRepository; // 실제 Repository 주입

    /**
     * 가맹점 ID로 Merchant 엔티티를 조회합니다.
     * @param merchantId 가맹점 식별자
     * @return 조회된 Merchant 엔티티
     * @throws CustomException 가맹점 정보가 존재하지 않을 경우 (NOT_FOUND)
     */
    public Merchant getMerchantById(String merchantId) {
        log.debug("[MERCHANT_SERVICE] Fetching Merchant by ID: {}", merchantId);

        return merchantRepository.findByMerchantId(merchantId)
                .orElseThrow(() -> {
                    log.error("[MERCHANT_SERVICE] Merchant not found for ID: {}", merchantId);
                    throw new CustomException(ErrorCode.MERCHANT_NOT_FOUND);
                });
    }


}
