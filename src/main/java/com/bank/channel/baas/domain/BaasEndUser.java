package com.bank.channel.baas.domain;

import com.bank.channel.baas.domain.enums.UserStatus;
import com.bank.channel.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "baas_end_users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaasEndUser extends BaseEntity {

    @Id
    @Column(name = "end_user_id", length = 255)
    private String endUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(name = "merchant_user_id", nullable = false, length = 100)
    private String merchantUserId;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "passcode", length = 200)
    private String passcode;

    @OneToMany(mappedBy = "endUser", cascade = CascadeType.ALL)
    private List<BaasUserAccount> userAccounts = new ArrayList<>();

}
