use bank_channel;
-- MerchantStatus: ACTIVE, INACTIVE
-- MerchantType: PLATFORM, GENERAL

INSERT INTO merchants (
    merchant_id, 
    merchant_name, 
    business_reg_number, 
    merchant_type, 
    api_key, 
    status,
    created_at, -- BaseEntity 필드 (현재 시간으로 가정)
    updated_at  -- BaseEntity 필드 (현재 시간으로 가정)
) VALUES 
(
    'WK', 
    '워켓코리아', 
    '123-45-67890', 
    'PLATFORM', 
    'apiKey_woket_d8d3f820241201', 
    'ACTIVE',
    NOW(),
    NOW()
);

-- 1. 성공 데이터 (2XX): 100건
INSERT INTO api_call_logs (
    trace_id, merchant_id, api_endpoint, http_method, 
    request_at, response_at, latency_ms, status_code
)
SELECT 
    CONCAT('trace-200-', n.rn),
    'WK', 
    '/api/v1/payment/authorize', 
    'POST', 
    DATE_ADD('2025-11-25 10:00:00', INTERVAL n.rn - 1 MINUTE), 
    -- 응답 시각: Latency(200ms)를 MICROSECOND로 변환 (200 * 1000 = 200,000 MICROSECOND)
    DATE_ADD(DATE_ADD('2025-11-25 10:00:00', INTERVAL n.rn - 1 MINUTE), INTERVAL 200000 MICROSECOND), 
    200, -- Latency (200ms)
    200
FROM (
    SELECT ROW_NUMBER() OVER () as rn
    FROM information_schema.tables LIMIT 100
) n;

-- 2. 클라이언트 오류 데이터 (4XX): 50건 (350ms -> 350,000 MICROSECOND)
INSERT INTO api_call_logs (
    trace_id, merchant_id, api_endpoint, http_method, 
    request_at, response_at, latency_ms, status_code
)
SELECT 
    CONCAT('trace-200-', n.rn),
    'WK', 
    '/api/v1/payment/authorize', 
    'POST', 
    DATE_ADD('2025-11-26 10:00:00', INTERVAL n.rn - 1 MINUTE), 
    DATE_ADD(DATE_ADD('2025-11-26 10:00:00', INTERVAL n.rn - 1 MINUTE), INTERVAL 350000 MICROSECOND), -- 수정
    350, 
    400
FROM (
    SELECT ROW_NUMBER() OVER () as rn
    FROM information_schema.tables LIMIT 50
) n;

-- 3. 서버 오류 데이터 (5XX): 20건 (500ms -> 500,000 MICROSECOND)
INSERT INTO api_call_logs (
  trace_id, merchant_id, api_endpoint, http_method, 
    request_at, response_at, latency_ms, status_code
)
SELECT 
    CONCAT('trace-200-', n.rn),
    'WK', 
    '/api/v1/payment/authorize', 
    'POST', 
    DATE_ADD('2025-11-27 10:00:00', INTERVAL n.rn - 1 MINUTE), 
    DATE_ADD(DATE_ADD('2025-11-27 10:00:00', INTERVAL n.rn - 1 MINUTE), INTERVAL 500000 MICROSECOND), -- 수정
    500, 
    503
FROM (
    SELECT ROW_NUMBER() OVER () as rn
    FROM information_schema.tables LIMIT 20
) n;

-- 현재 시간 설정 (created_at, updated_at에 사용)
SET @CURRENT_TIME = NOW();
-- 테스트 기준 시간 설정 (2025년 11월)
SET @START_OF_TEST_PERIOD = '2025-01-01 00:00:00';
SET @END_OF_LAST_MONTH = '2025-10-31 23:59:59';

-- 1. WOKET_KR 고객사를 위한 활성 전역 정책
--    (가장 최근에 생성된 유효 정책이므로 PolicyRepository에 의해 조회됨)
INSERT INTO api_billing_policies (
    merchant_id, api_endpoint, unit_success_price, 
    unit_client_error_price, unit_server_error_price, 
    effective_start_date, effective_end_date, created_at, updated_at
) VALUES 
(
    'WK', 
    '/api/v1/usages', 
    10,        -- 성공 10원
    10,         -- 클라이언트 오류 10원
    5,         -- 서버 오류 5원
    @START_OF_TEST_PERIOD, 
    NULL,      -- 종료일 없음 (활성)
    @CURRENT_TIME, 
    @CURRENT_TIME
);

INSERT INTO api_billing_policies (
    merchant_id, api_endpoint, unit_success_price, 
    unit_client_error_price, unit_server_error_price, 
    effective_start_date, effective_end_date, created_at, updated_at
) VALUES 
(
    'WK', 
    '/api/v1/payment/authorize', 
    10,        -- 성공 10원
    10,         -- 클라이언트 오류 10원
    5,         -- 서버 오류 5원
    @START_OF_TEST_PERIOD, 
    NULL,      -- 종료일 없음 (활성)
    @CURRENT_TIME, 
    @CURRENT_TIME
);

INSERT INTO api_billing_policies (
    merchant_id, api_endpoint, unit_success_price, 
    unit_client_error_price, unit_server_error_price, 
    effective_start_date, effective_end_date, created_at, updated_at
) VALUES 
(
    'WK', 
    '/api/v1/payment/confirm', 
    10,        -- 성공 10원
    10,         -- 클라이언트 오류 10원
    5,         -- 서버 오류 5원
    @START_OF_TEST_PERIOD, 
    NULL,      -- 종료일 없음 (활성)
    @CURRENT_TIME, 
    @CURRENT_TIME
);

INSERT INTO api_billing_policies (
    merchant_id, api_endpoint, unit_success_price, 
    unit_client_error_price, unit_server_error_price, 
    effective_start_date, effective_end_date, created_at, updated_at
) VALUES 
(
    'WK', 
    '/api/v1/payment/approval', 
    10,        -- 성공 10원
    10,         -- 클라이언트 오류 10원
    5,         -- 서버 오류 5원
    @START_OF_TEST_PERIOD, 
    NULL,      -- 종료일 없음 (활성)
    @CURRENT_TIME, 
    @CURRENT_TIME
);

INSERT INTO api_billing_policies (
    merchant_id, api_endpoint, unit_success_price, 
    unit_client_error_price, unit_server_error_price, 
    effective_start_date, effective_end_date, created_at, updated_at
) VALUES 
(
    'WK', 
    '/api/v1/contracts/register', 
    10,        -- 성공 10원
    10,         -- 클라이언트 오류 10원
    5,         -- 서버 오류 5원
    @START_OF_TEST_PERIOD, 
    NULL,      -- 종료일 없음 (활성)
    @CURRENT_TIME, 
    @CURRENT_TIME
);