package com.bank.channel.baas.dto.NonBank;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

/**
 * 프리랜서 정보 DTO
 * 
 * 계약 메타데이터에서 프리랜서 정보를 담는 객체
 */
@Getter
@Builder
public class FreelancerInfo {

    @NotBlank(message = "프리랜서 이름은 필수입니다")
    private final String name; // 프리랜서 이름

    @NotBlank(message = "프리랜서 휴대폰 번호는 필수입니다")
    private final String phone; // 프리랜서 휴대폰 번호

    @NotBlank(message = "프리랜서 계좌번호는 필수입니다")
    private final String account; // 프리랜서 계좌번호

    @NotBlank(message = "프리랜서 은행코드는 필수입니다")
    private final String bank; // 프리랜서 은행코드
}
