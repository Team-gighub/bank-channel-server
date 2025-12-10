package com.bank.channel.baas.service;

import com.bank.channel.baas.domain.BaasEndUser;
import com.bank.channel.baas.domain.BaasUserAccount;
import com.bank.channel.baas.domain.Merchant;
import com.bank.channel.baas.domain.enums.Bank;
import com.bank.channel.baas.dto.NonBank.AccountInfoWithPhone;
import com.bank.channel.baas.dto.NonBank.BasicAccountInfo;
import com.bank.channel.baas.dto.NonBank.PaymentAuthorizeRequest;
import com.bank.channel.baas.repository.BaasEndUserRepository;
import com.bank.channel.baas.repository.BaasUserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;


/**
 * BaaS End User 및 계좌 관리 서비스
 * PaymentAuthorizeRequest에 merchantUserId가 없으므로, Payer의 계좌 정보를 기준으로 사용자 및 계좌를 식별하고 저장합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EndUserService {

    private final BaasEndUserRepository endUserRepository;
    private final BaasUserAccountRepository accountRepository;
    private final MerchantService merchantService;

    /**
     * 결제 요청 정보를 바탕으로 End User 및 계좌 정보를 저장하거나 업데이트합니다.
     * 이 메서드는 PaymentService에서 결제 인증 전에 호출됩니다.
     */
    public void saveOrUpdateUserAndAccount(PaymentAuthorizeRequest request) {
        log.info("[EndUser/Account Upsert] Start processing for Merchant ID: {}", request.getMerchantId());

        // 1. Merchant 조회
        Merchant merchant = merchantService.getMerchantById(request.getMerchantId());

        // 2. BaaS End User 처리 (조회 또는 신규 생성)
        BaasEndUser endUser = findOrCreateEndUser(request, merchant);

        // 3. Payer 계좌 정보 처리 (조회 또는 신규 생성)
        saveOrUpdateAccount(endUser, request.getPayerInfo());

        log.info("[EndUser/Account Upsert] Success for EndUserId: {}", endUser.getEndUserId());
    }

    /**
     * 계좌 정보를 기준으로 BaaS End User를 조회하거나 새로 생성합니다.
     * merchantUserId가 없으므로, 계좌번호와 은행코드로 기존 BaasUserAccount를 먼저 조회합니다.
     */
    private BaasEndUser findOrCreateEndUser(PaymentAuthorizeRequest request, Merchant merchant) {
        AccountInfoWithPhone payerInfo = request.getPayerInfo();

        // 1. Payer의 계좌번호/은행코드로 기존 BaasUserAccount를 조회
        return accountRepository
                .findByAccountNumberAndBankCode(
                        payerInfo.getAccountNo(),
                        payerInfo.getBankCode())
                .map(BaasUserAccount::getEndUser) // 기존 계좌가 있으면 연결된 EndUser 반환 (재활용)
                .orElseGet(() -> {
                    // 2. 계좌 정보가 없으면, 신규 BaaS End User 생성
                    // BaaS 내부 PK (endUserId) 생성: UUID 사용
                    String generatedEndUserId = generateUniqueId();

                    // 3. BaaS End User 엔티티 생성 및 저장
                    BaasEndUser newEndUser = BaasEndUser.builder()
                            .endUserId(generatedEndUserId)
                            .merchant(merchant)
                            .userName(request.getUserName())
                            .phoneNumber(payerInfo.getPhone())
                            .build();

                    return endUserRepository.save(newEndUser);
                });
    }

    /**
     * EndUser에 연결된 Payer 계좌 정보를 저장하거나 업데이트합니다.
     */
    private void saveOrUpdateAccount(BaasEndUser endUser, BasicAccountInfo payerInfo) {
        // BaasEndUser와 계좌번호, 은행코드를 기준으로 계좌 조회
        accountRepository.findByEndUserAndAccountNumberAndBankCode(
                        endUser,
                        payerInfo.getAccountNo(),
                        payerInfo.getBankCode())
                .orElseGet(() -> {
                    // 존재하지 않으면 신규 생성
                    BaasUserAccount newAccount = BaasUserAccount.builder()
                            .endUser(endUser)
                            .bankCode(payerInfo.getBankCode())
                            .bankName(getBankName(payerInfo.getBankCode()))
                            .accountNumber(payerInfo.getAccountNo())
                            .accountHolderName(payerInfo.getName())
                            .isVerified(true)
                            .build();
                    return accountRepository.save(newAccount);
                });
    }

    /**
     * BaaS 내부 PK인 endUserId를 UUID 기반으로 생성합니다.
     */
    private String generateUniqueId() { return UUID.randomUUID().toString(); }

    /**
     * 은행 코드를 은행명으로 변환
     */
    private String getBankName(String bankCode) {
        return Bank.getNameByCode(bankCode);
    }
}
