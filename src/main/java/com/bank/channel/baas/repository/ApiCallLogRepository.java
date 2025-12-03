package com.bank.channel.baas.repository;

import com.bank.channel.baas.domain.ApiCallLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * API 호출 로그 Repository
 */
public interface ApiCallLogRepository extends JpaRepository<ApiCallLog, Long> {
}
