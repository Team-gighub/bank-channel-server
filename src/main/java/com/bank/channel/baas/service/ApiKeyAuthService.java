package com.bank.channel.baas.service;

import com.bank.channel.baas.domain.Merchant;
import com.bank.channel.baas.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * API Key 기반 인증 및 교차 검증 서비스
 * 
 * 보안 원칙:
 * 1. API Key가 신뢰의 기준 (DB에서 조회)
 * 2. Request Body의 merchantId와 교차 검증
 * 3. 불일치 시 보안 위협으로 간주
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiKeyAuthService {

    private final MerchantRepository merchantRepository;

    /**
     * API Key에서 merchantId 추출 (신뢰할 수 있는 방법)
     * 
     * @param apiKey API Key (Authorization 헤더에서 추출)
     * @return merchantId (Optional)
     */
    public Optional<String> extractMerchantIdFromApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("[API_KEY_AUTH] API Key is null or blank");
            return Optional.empty();
        }

        // Bearer 토큰 형식인 경우 처리
        String extractedKey = extractBearerToken(apiKey);

        return merchantRepository.findByApiKey(extractedKey)
                .map(merchant -> {
                    log.debug("[API_KEY_AUTH] Merchant found by API Key: {}", merchant.getMerchantId());
                    return merchant.getMerchantId();
                });
    }

    /**
     * Request Body의 merchantId와 교차 검증
     * 
     * 보안 검증:
     * - API Key로 조회한 merchantId (신뢰의 기준)
     * - Request Body의 merchantId (검증 대상)
     * - 불일치 시 보안 위협
     * 
     * @param apiKey API Key
     * @param requestMerchantId Request Body의 merchantId
     * @return 검증 결과
     */
    public MerchantAuthResult validateMerchantWithCrossCheck(String apiKey, String requestMerchantId) {
        // 1. API Key로 실제 merchantId 조회 (신뢰의 기준)
        Optional<String> trustedMerchantId = extractMerchantIdFromApiKey(apiKey);

        if (trustedMerchantId.isEmpty()) {
            log.warn("[SECURITY_THREAT] Invalid API Key - no merchant found");
            return MerchantAuthResult.invalidApiKey();
        }

        String actualTrustedId = trustedMerchantId.get();

        // 2. Request Body에 merchantId가 없는 경우 (교차 검증 불가)
        if (requestMerchantId == null || requestMerchantId.isBlank()) {
            log.debug("[API_KEY_AUTH] No merchantId in request body - using API Key only");
            return MerchantAuthResult.success(actualTrustedId);
        }

        // 3. 교차 검증: API Key의 merchantId vs Request Body의 merchantId
        if (!actualTrustedId.equals(requestMerchantId)) {
            log.error("[SECURITY_THREAT] Merchant ID mismatch! " +
                            "API Key merchant: {}, Request merchant: {}",
                    actualTrustedId, requestMerchantId);
            return MerchantAuthResult.mismatch(actualTrustedId, requestMerchantId);
        }

        // 4. 검증 성공
        log.debug("[API_KEY_AUTH] Cross validation passed - merchantId: {}", actualTrustedId);
        return MerchantAuthResult.success(actualTrustedId);
    }

    /**
     * Bearer 토큰에서 실제 토큰 추출
     * 
     * @param authorizationHeader Authorization 헤더 값
     * @return API Key
     */
    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7).trim();
        }
        return authorizationHeader;
    }

    /**
     * 인증 결과를 담는 DTO
     */
    public static class MerchantAuthResult {
        private final boolean valid;
        private final String merchantId;
        private final String errorMessage;
        private final MerchantAuthStatus status;

        private MerchantAuthResult(boolean valid, String merchantId, String errorMessage, MerchantAuthStatus status) {
            this.valid = valid;
            this.merchantId = merchantId;
            this.errorMessage = errorMessage;
            this.status = status;
        }

        public static MerchantAuthResult success(String merchantId) {
            return new MerchantAuthResult(true, merchantId, null, MerchantAuthStatus.SUCCESS);
        }

        public static MerchantAuthResult invalidApiKey() {
            return new MerchantAuthResult(false, null, "Invalid API Key", MerchantAuthStatus.INVALID_API_KEY);
        }

        public static MerchantAuthResult mismatch(String apiKeyMerchantId, String requestMerchantId) {
            String error = String.format("Merchant ID mismatch - API Key: %s, Request: %s",
                    apiKeyMerchantId, requestMerchantId);
            return new MerchantAuthResult(false, null, error, MerchantAuthStatus.MISMATCH);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMerchantId() {
            return merchantId;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public MerchantAuthStatus getStatus() {
            return status;
        }

        public boolean isMismatch() {
            return status == MerchantAuthStatus.MISMATCH;
        }
    }

    /**
     * 인증 상태
     */
    public enum MerchantAuthStatus {
        SUCCESS,            // 인증 성공
        INVALID_API_KEY,    // 유효하지 않은 API Key
        MISMATCH            // merchantId 불일치 (보안 위협)
    }
}
