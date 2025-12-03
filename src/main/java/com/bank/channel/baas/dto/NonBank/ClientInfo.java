package com.bank.channel.baas.dto.NonBank;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

/**
 * 의뢰인(클라이언트) 정보 DTO
 * 
 * 계약 메타데이터에서 의뢰인 정보를 담는 객체
 */
@Getter
@Builder
public class ClientInfo {

    @NotBlank(message = "의뢰인 이름은 필수입니다")
    private final String name; // 의뢰인 이름

    @NotBlank(message = "의뢰인 휴대폰 번호는 필수입니다")
    private final String phone; // 의뢰인 휴대폰 번호
}
