package com.bank.channel.baas.domain;

import com.bank.channel.baas.domain.enums.HttpMethod;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_call_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiCallLog {

    @Builder
    private ApiCallLog(
            String traceId,
            String merchantId,
            String apiEndpoint,
            HttpMethod httpMethod,
            LocalDateTime requestAt,
            LocalDateTime responseAt,
            Integer latencyMs,
            Integer statusCode
    ) {
        this.traceId = traceId;
        this.merchantId = merchantId;
        this.apiEndpoint = apiEndpoint;
        this.httpMethod = httpMethod;
        this.requestAt = requestAt;
        this.responseAt = responseAt;
        this.latencyMs = latencyMs;
        this.statusCode = statusCode;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "trace_id", nullable = false, length = 50)
    private String traceId;

    @Column(name = "merchant_id", nullable = false, length = 50)
    private String merchantId;

    @Column(name = "api_endpoint", nullable = false, length = 200)
    private String apiEndpoint;

    @Enumerated(EnumType.STRING)
    @Column(name = "http_method", nullable = false)
    private HttpMethod httpMethod;

    @Column(name = "request_at", nullable = false)
    private LocalDateTime requestAt;

    @Column(name = "response_at", nullable = false)
    private LocalDateTime responseAt;

    @Column(name = "latency_ms", nullable = false)
    private Integer latencyMs;

    @Column(name = "status_code", nullable = false)
    private Integer statusCode;
}
