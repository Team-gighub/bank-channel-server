package com.bank.channel.baas.domain;

import com.bank.channel.baas.domain.enums.UserStatus;
import com.bank.channel.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "baas_end_users")
@Getter
@Builder(toBuilder = true) // Builder 패턴 추가
@AllArgsConstructor(access = AccessLevel.PROTECTED) // Builder를 위한 AllArgsConstructor 추가
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaasEndUser extends BaseEntity {

    @Id
    @Column(name = "end_user_id", length = 255)
    private String endUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "passcode", length = 200)
    private String passcode;

}
