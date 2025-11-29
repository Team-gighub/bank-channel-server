package com.bank.channel.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * TraceId 동작 확인용 테스트 Controller
 * 
 * 사용법:
 * GET http://localhost:8080/test/trace-id
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TraceTestController {

    @GetMapping("/trace-id")
    public Map<String, String> getTraceId() {
        
        // MDC에서 traceId 가져오기
        String traceId = MDC.get("traceId");
        
        // 콘솔에 출력
        log.info("===========================================");
        log.info("🔍 TraceId 확인:");
        log.info("   TraceId: {}", traceId);
        log.info("===========================================");

        // 응답으로도 반환
        Map<String, String> response = new HashMap<>();
        response.put("traceId", traceId != null ? traceId : "없음");
        response.put("message", "TraceId가 자동으로 발급되었습니다!");
        response.put("설명", "UUID 기반으로 생성된 32자리 고유 ID입니다.");
        
        return response;
    }
}
