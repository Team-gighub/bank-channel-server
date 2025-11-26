package com.bank.channel.baas.domain;

import com.bank.channel.baas.domain.enums.CoreSystemType;
import com.bank.channel.baas.domain.enums.EventStatus;
import com.bank.channel.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "core_banking_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoreBankingEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "trace_id", length = 50)
    private String traceId;

    @Column(name = "channel_tx_id", nullable = false, length = 64)
    private String channelTxId;

    @Column(name = "api_name", nullable = false, length = 100)
    private String apiName;

    @Enumerated(EnumType.STRING)
    @Column(name = "core_system_type", nullable = false)
    private CoreSystemType coreSystemType;

    @Column(name = "core_endpoint", nullable = false, length = 255)
    private String coreEndpoint;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_payload", nullable = false, columnDefinition = "json")
    private String requestPayload;

    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_payload", columnDefinition = "json")
    private String responsePayload;

    @Column(name = "response_time")
    private LocalDateTime responseTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status;

}
