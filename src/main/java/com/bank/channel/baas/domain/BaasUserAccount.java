package com.bank.channel.baas.domain;

import com.bank.channel.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "baas_user_accounts")
@Getter
@Builder(toBuilder = true) // Builder 패턴 추가
@AllArgsConstructor(access = AccessLevel.PROTECTED) // Builder를 위한 AllArgsConstructor 추가
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaasUserAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "end_user_id", nullable = false)
    private BaasEndUser endUser;

    @Column(name = "bank_code", nullable = false, length = 10)
    private String bankCode;

    @Column(name = "bank_name", nullable = false, length = 50)
    private String bankName;

    @Column(name = "account_number", nullable = false, length = 50)
    private String accountNumber;

    @Column(name = "account_holder_name", nullable = false, length = 100)
    private String accountHolderName;

    @Column(name = "is_verified")
    private Boolean isVerified = true;

}
