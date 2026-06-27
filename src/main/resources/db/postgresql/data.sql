-- =====================================================================
-- 운영(PostgreSQL) 초기 기준데이터 (멱등 : ON CONFLICT DO NOTHING)
--   - 운영 환경에는 샘플 거래데이터(장애/변경/배포/점검)를 적재하지 않는다.
--   - 사용자/응용시스템/공통코드 등 기준정보만 시딩한다.
--   - OPS_USER.PASSWORD 평문은 기동 시 DataBootstrap 이 BCrypt 로 일괄 암호화한다.
--   - 최초 운영 적용 후 관리자 비밀번호는 반드시 변경할 것.
-- =====================================================================

-- 사용자(운영자) -------------------------------------------------------
INSERT INTO OPS_USER (USER_ID, USER_NM, PASSWORD, ROLE, DEPT_NM, EMAIL, TELNO, USE_AT) VALUES
 ('admin',  '운영관리자', 'admin123!', 'ADMIN',    '정보화운영팀', 'admin@egov.go.kr',  '02-100-0001', 'Y')
ON CONFLICT (USER_ID) DO NOTHING;

-- 공통코드 ------------------------------------------------------------
INSERT INTO OPS_CODE (CODE_GRP, CODE_ID, CODE_NM, SORT_ORDR, USE_AT) VALUES
 ('INCIDENT_STATUS', 'RECEIVED',  '접수',     1, 'Y'),
 ('INCIDENT_STATUS', 'ANALYZING', '원인분석', 2, 'Y'),
 ('INCIDENT_STATUS', 'ACTING',    '조치중',   3, 'Y'),
 ('INCIDENT_STATUS', 'RESOLVED',  '조치완료', 4, 'Y'),
 ('INCIDENT_STATUS', 'CLOSED',    '종결',     5, 'Y'),
 ('INCIDENT_SEVERITY', '1', '1등급(긴급)',  1, 'Y'),
 ('INCIDENT_SEVERITY', '2', '2등급(높음)',  2, 'Y'),
 ('INCIDENT_SEVERITY', '3', '3등급(보통)',  3, 'Y'),
 ('INCIDENT_SEVERITY', '4', '4등급(낮음)',  4, 'Y'),
 ('CHANGE_STATUS', 'REQUESTED', '요청',     1, 'Y'),
 ('CHANGE_STATUS', 'REVIEWING', '검토',     2, 'Y'),
 ('CHANGE_STATUS', 'APPROVED',  '승인',     3, 'Y'),
 ('CHANGE_STATUS', 'REJECTED',  '반려',     4, 'Y'),
 ('CHANGE_STATUS', 'APPLIED',   '적용',     5, 'Y'),
 ('CHANGE_STATUS', 'COMPLETED', '완료',     6, 'Y'),
 ('CHANGE_TYPE', 'PROGRAM',   '프로그램변경', 1, 'Y'),
 ('CHANGE_TYPE', 'DATABASE',  'DB변경',       2, 'Y'),
 ('CHANGE_TYPE', 'CONFIG',    '설정변경',     3, 'Y'),
 ('CHANGE_TYPE', 'EMERGENCY', '긴급변경',     4, 'Y'),
 ('RELEASE_STATUS', 'PLANNED',   '배포계획', 1, 'Y'),
 ('RELEASE_STATUS', 'APPROVED',  '배포승인', 2, 'Y'),
 ('RELEASE_STATUS', 'DEPLOYING', '배포중',   3, 'Y'),
 ('RELEASE_STATUS', 'DEPLOYED',  '배포완료', 4, 'Y'),
 ('RELEASE_STATUS', 'ROLLBACK',  '롤백',     5, 'Y'),
 ('CHECK_TYPE', 'DAILY',   '일일점검', 1, 'Y'),
 ('CHECK_TYPE', 'WEEKLY',  '주간점검', 2, 'Y'),
 ('CHECK_TYPE', 'MONTHLY', '월간점검', 3, 'Y'),
 ('CHECK_RESULT', 'NORMAL',   '정상', 1, 'Y'),
 ('CHECK_RESULT', 'ABNORMAL', '이상', 2, 'Y')
ON CONFLICT (CODE_GRP, CODE_ID) DO NOTHING;

-- 표준운영절차 확장 모듈 공통코드 ----------------------------------------
INSERT INTO OPS_CODE (CODE_GRP, CODE_ID, CODE_NM, SORT_ORDR, USE_AT) VALUES
 ('PRIORITY', 'HIGH', '높음', 1, 'Y'),
 ('PRIORITY', 'MID',  '보통', 2, 'Y'),
 ('PRIORITY', 'LOW',  '낮음', 3, 'Y'),
 ('CSR_STATUS', 'REQUESTED',   '요청',     1, 'Y'),
 ('CSR_STATUS', 'RECEIVED',    '접수',     2, 'Y'),
 ('CSR_STATUS', 'CLASSIFIED',  '분류완료', 3, 'Y'),
 ('CSR_STATUS', 'IN_PROGRESS', '처리중',   4, 'Y'),
 ('CSR_STATUS', 'PROCESSED',   '처리완료', 5, 'Y'),
 ('CSR_STATUS', 'CLOSED',      '종료',     6, 'Y'),
 ('CSR_STATUS', 'REJECTED',    '반려',     7, 'Y'),
 ('CSR_TYPE', 'INQUIRY', '단순문의', 1, 'Y'),
 ('CSR_TYPE', 'CHANGE',  '변경요청', 2, 'Y'),
 ('CSR_TYPE', 'IMPROVE', '개선요청', 3, 'Y'),
 ('CSR_TYPE', 'WORK',    '작업요청', 4, 'Y'),
 ('TEST_STATUS', 'PLANNED',  '계획',     1, 'Y'),
 ('TEST_STATUS', 'TESTING',  '수행중',   2, 'Y'),
 ('TEST_STATUS', 'ANALYZED', '분석완료', 3, 'Y'),
 ('TEST_STATUS', 'CLOSED',   '종료',     4, 'Y'),
 ('TEST_STATUS', 'FAILED',   '실패',     5, 'Y'),
 ('TEST_TYPE', 'UNIT',        '단위테스트', 1, 'Y'),
 ('TEST_TYPE', 'INTEGRATION', '통합테스트', 2, 'Y'),
 ('TEST_TYPE', 'PERFORMANCE', '성능테스트', 3, 'Y'),
 ('TEST_TYPE', 'ACCEPTANCE',  '인수테스트', 4, 'Y'),
 ('TEST_ENV', 'DEV', '개발환경', 1, 'Y'),
 ('TEST_ENV', 'OPS', '운영환경', 2, 'Y'),
 ('TEST_CASE_RESULT', 'PASS', '성공',     1, 'Y'),
 ('TEST_CASE_RESULT', 'FAIL', '실패',     2, 'Y'),
 ('TEST_CASE_RESULT', 'NA',   '해당없음', 3, 'Y'),
 ('INTF_STATUS', 'REQUESTED', '요청',     1, 'Y'),
 ('INTF_STATUS', 'REVIEWING', '검토',     2, 'Y'),
 ('INTF_STATUS', 'PLANNING',  '계획수립', 3, 'Y'),
 ('INTF_STATUS', 'WORKING',   '작업중',   4, 'Y'),
 ('INTF_STATUS', 'TESTING',   '연계테스트', 5, 'Y'),
 ('INTF_STATUS', 'COMPLETED', '완료',     6, 'Y'),
 ('INTF_STATUS', 'REJECTED',  '반려',     7, 'Y'),
 ('INTF_TYPE', 'SYNC',  '동기',   1, 'Y'),
 ('INTF_TYPE', 'ASYNC', '비동기', 2, 'Y'),
 ('INTF_TYPE', 'BATCH', '배치',   3, 'Y'),
 ('INTF_TYPE', 'API',   'API',    4, 'Y'),
 ('CI_STATUS', 'IDENTIFIED',  '식별',       1, 'Y'),
 ('CI_STATUS', 'BASELINED',   '베이스라인', 2, 'Y'),
 ('CI_STATUS', 'CHECKED_OUT', '체크아웃',   3, 'Y'),
 ('CI_STATUS', 'CHECKED_IN',  '체크인',     4, 'Y'),
 ('CI_TYPE', 'SOURCE',   '소스코드',     1, 'Y'),
 ('CI_TYPE', 'LIBRARY',  '라이브러리',   2, 'Y'),
 ('CI_TYPE', 'DOCUMENT', '문서',         3, 'Y'),
 ('CI_TYPE', 'CONFIG',   '설정파일',     4, 'Y'),
 ('CI_TYPE', 'DB',       'DB객체',       5, 'Y'),
 ('CI_CHG_TYPE', 'IDENTIFY', '식별',         1, 'Y'),
 ('CI_CHG_TYPE', 'CHECKOUT', '체크아웃',     2, 'Y'),
 ('CI_CHG_TYPE', 'CHECKIN',  '체크인',       3, 'Y'),
 ('CI_CHG_TYPE', 'BASELINE', '베이스라인설정', 4, 'Y'),
 ('CI_CHG_TYPE', 'AUDIT',    '형상감사',     5, 'Y'),
 ('EVENT_STATUS', 'DETECTED',  '감지',     1, 'Y'),
 ('EVENT_STATUS', 'ANALYZING', '분석중',   2, 'Y'),
 ('EVENT_STATUS', 'ACTING',    '조치중',   3, 'Y'),
 ('EVENT_STATUS', 'HANDLED',   '조치완료', 4, 'Y'),
 ('EVENT_STATUS', 'ESCALATED', '장애전환', 5, 'Y'),
 ('EVENT_STATUS', 'CLOSED',    '종료',     6, 'Y'),
 ('EVENT_TYPE', 'CPU',     'CPU',          1, 'Y'),
 ('EVENT_TYPE', 'MEMORY',  '메모리',       2, 'Y'),
 ('EVENT_TYPE', 'DISK',    '디스크',       3, 'Y'),
 ('EVENT_TYPE', 'PROCESS', '프로세스',     4, 'Y'),
 ('EVENT_TYPE', 'NETWORK', '네트워크',     5, 'Y'),
 ('EVENT_TYPE', 'APP',     '애플리케이션', 6, 'Y'),
 ('EVENT_SEVERITY', 'INFO',     '정보', 1, 'Y'),
 ('EVENT_SEVERITY', 'WARN',     '경고', 2, 'Y'),
 ('EVENT_SEVERITY', 'CRITICAL', '심각', 3, 'Y'),
 ('PROBLEM_STATUS', 'REGISTERED', '등록',     1, 'Y'),
 ('PROBLEM_STATUS', 'ANALYZING',  '분석중',   2, 'Y'),
 ('PROBLEM_STATUS', 'IDENTIFIED', '원인규명', 3, 'Y'),
 ('PROBLEM_STATUS', 'RESOLVING',  '해결중',   4, 'Y'),
 ('PROBLEM_STATUS', 'RESOLVED',   '해결완료', 5, 'Y'),
 ('PROBLEM_STATUS', 'CLOSED',     '종료',     6, 'Y')
ON CONFLICT (CODE_GRP, CODE_ID) DO NOTHING;

-- 현행 모듈 보강 공통코드 ----------------------------------------------
INSERT INTO OPS_CODE (CODE_GRP, CODE_ID, CODE_NM, SORT_ORDR, USE_AT) VALUES
 ('RELEASE_STATUS', 'VERIFYING',  '배포검증', 6, 'Y'),
 ('RELEASE_STATUS', 'STABILIZING','안정화',   7, 'Y'),
 ('CAB_DECISION', 'APPROVED', '승인', 1, 'Y'),
 ('CAB_DECISION', 'REJECTED', '반려', 2, 'Y'),
 ('CAB_DECISION', 'HOLD',     '보류', 3, 'Y'),
 ('ESCAL_LEVEL', 'L1',      '1차(운영자)',   1, 'Y'),
 ('ESCAL_LEVEL', 'L2',      '2차(전문기술)', 2, 'Y'),
 ('ESCAL_LEVEL', 'MANAGER', '관리자',        3, 'Y'),
 ('ESCAL_LEVEL', 'VENDOR',  '제조사/벤더',   4, 'Y'),
 ('RELEASE_ITEM_RESULT', 'SUCCESS', '성공', 1, 'Y'),
 ('RELEASE_ITEM_RESULT', 'FAIL',    '실패', 2, 'Y'),
 ('RELEASE_ITEM_RESULT', 'SKIP',    '제외', 3, 'Y')
ON CONFLICT (CODE_GRP, CODE_ID) DO NOTHING;

-- =====================================================================
-- 결재선/공유 공통코드 (운영: 기준코드만 시딩, 거래데이터 미적재)
-- =====================================================================
INSERT INTO OPS_CODE (CODE_GRP, CODE_ID, CODE_NM, SORT_ORDR, USE_AT) VALUES
 ('LINE_TYPE', 'REVIEW',  '검토', 1, 'Y'),
 ('LINE_TYPE', 'APPROVE', '승인', 2, 'Y'),
 ('LINE_TYPE', 'HANDLE',  '처리', 3, 'Y'),
 ('APPR_STATUS', 'PENDING',  '대기',     1, 'Y'),
 ('APPR_STATUS', 'REVIEWED', '검토완료', 2, 'Y'),
 ('APPR_STATUS', 'APPROVED', '승인',     3, 'Y'),
 ('APPR_STATUS', 'REJECTED', '반려',     4, 'Y'),
 ('APPR_STATUS', 'DONE',     '처리완료', 5, 'Y')
ON CONFLICT (CODE_GRP, CODE_ID) DO NOTHING;
