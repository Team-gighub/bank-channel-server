package com.bank.channel.baas.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Bank {
    WOORI("020", "우리은행"),
    NH("011", "농협은행"),
    SHINHAN("088", "신한은행"),
    KAKAO("090","카카오뱅크"),
    IBK("003","기업은행"),
    HANA("081","하나은행"),
    TOSS("092","토스"),
    MG("045","새마을금고"),
    // ... 나머지 은행
    ETC("999", "기타은행");
    private final String code;
    private final String name;

    public static String getNameByCode(String code) {
        return Arrays.stream(Bank.values())
                .filter(bank -> bank.getCode().equals(code))
                .findFirst()
                .map(Bank::getName)
                .orElse(ETC.getName());
    }
}
