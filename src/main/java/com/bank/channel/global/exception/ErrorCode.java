package com.bank.channel.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // TODO: 관련 기능별 추가 및 정리
    // --- 💰 계좌/잔액 관련 오류 (BAL) ---
    // 403 Forbidden
    BALANCE_INSUFFICIENT(HttpStatus.FORBIDDEN, "BAL_3001", "잔액이 부족하여 거래를 진행할 수 없습니다."),

    // --- 🔑 인증/검증 관련 오류 (VAL) ---
    // 403 Forbidden
    TOKEN_EXPIRED_OR_INVALID(HttpStatus.FORBIDDEN, "VAL_3001", "검증 토큰이 만료되었거나 유효하지 않습니다."),

    // --- 🏦 계정계/조회 관련 오류 (ACCT) ---
    // 404 Not Found
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCT_4001", "해당 계좌 정보를 찾을 수 없습니다."),

    // 400 Bad Request
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_001", "입력 값이 올바르지 않습니다."),
    INVALID_JSON(HttpStatus.BAD_REQUEST, "COMMON_002", "JSON 형식이 잘못되었습니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_003", "인증이 필요합니다."),

    // 403 Forbidden
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_004", "접근 권한이 없습니다."),

    // 405 Method Not Allowed
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_005", "지원하지 않는 HTTP 메서드입니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_006", "서버 내부 오류가 발생했습니다."),

    // merchant 관련 에러
    MERCHANT_NOT_FOUND(HttpStatus.NOT_FOUND, "MRCH_4001", "해당 가맹점 정보를 찾을 수 없습니다."),

    // 날짜 유효성 관련 에러
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "DATE_4001", "날짜 형식이 올바르지 않습니다. (예: yyyy-MM-dd)"),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "DATE_4002", "조회 시작일이 종료일보다 늦거나 기간이 유효하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}