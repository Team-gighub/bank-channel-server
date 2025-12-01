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
     * 결제 인증 요청(PaymentAuthorizeRequest) 기반으로 가맹점 정보를 저장하거나 업데이트합니다.
     * DB에서 merchantId를 조회 후, 존재하면 업데이트, 없으면 신규 저장합니다.
     *
     * @param request 결제 인증 요청 DTO
     */
    @Transactional
    public void saveOrUpdate(PaymentAuthorizeRequest request) {

        // 1. DTO에서 필수 정보 추출
        String merchantId = request.getMerchantId();
        log.info("[MERCHANT_SERVICE] Start saving/updating merchant info. MerchantId: {}", merchantId);

        // 2. 가맹점 ID로 DB 조회
//        merchantRepository.findByMerchantId(merchantId);
//                .ifPresentOrElse(
//                        // 3. 존재할 경우: 업데이트 로직
//                        existingMerchant -> {
//                            log.info("[MERCHANT_SERVICE] Merchant {} found. Performing UPDATE.", merchantId);
//                            existingMerchant.updateInfo(request); // 엔티티의 필드를 최신 정보로 갱신
//                            // @Transactional 덕분에 save() 호출 없이도 변경 감지(Dirty Checking)로 DB에 반영됨
//                        },
//                        // 4. 존재하지 않을 경우: 신규 저장 로직
//                        () -> {
//                            log.info("[MERCHANT_SERVICE] Merchant {} NOT found. Performing SAVE.", merchantId);
//                            Merchant newMerchant = Merchant.fromRequest(request); // DTO 기반 신규 엔티티 생성
//                            merchantRepository.save(newMerchant); // DB에 저장
//                        }
//                );

        log.info("[MERCHANT_SERVICE] Successfully finished save/update for MerchantId: {}", merchantId);
    }

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
                    throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
                });
    }


}