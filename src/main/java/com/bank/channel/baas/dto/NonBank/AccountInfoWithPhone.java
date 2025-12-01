package com.bank.channel.baas.dto.NonBank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AccountInfoWithPhone extends BasicAccountInfo {
    private String phone;
}
