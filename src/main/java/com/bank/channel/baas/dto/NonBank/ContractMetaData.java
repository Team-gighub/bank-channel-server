package com.bank.channel.baas.dto.NonBank;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContractMetaData {

    private final ContractInfo contractInfo;
    private final ClientInfo clientInfo;
    private final FreelancerInfo freelancerInfo;

    @Getter
    @Builder
    public static class ClientInfo {

        private final String name;
        private final String phone;
    }

    @Getter
    @Builder
    public static class FreelancerInfo {

        private final String name;
        private final String phone;
        private final String account;
        private final String bank;
    }
}
