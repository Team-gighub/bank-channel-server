package com.bank.channel.baas.repository;

import com.bank.channel.baas.domain.BaasEndUser;
import com.bank.channel.baas.domain.BaasUserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BaasUserAccountRepository extends JpaRepository<BaasUserAccount, Long> {

    /**
     * 특정 BaasEndUser에 연결된 계좌번호와 은행코드를 사용하여 BaasUserAccount를 조회합니다.
     */
    Optional<BaasUserAccount> findByEndUserAndAccountNumberAndBankCode(
            BaasEndUser endUser,
            String accountNumber,
            String bankCode
    );

    /**
     * 계좌번호와 은행코드만 사용하여 BaasUserAccount를 조회합니다.
     * End User ID가 없을 때 Payer가 기존 사용자인지 확인하는 데 사용됩니다.
     */
    Optional<BaasUserAccount> findByAccountNumberAndBankCode(String accountNumber, String bankCode);
}
