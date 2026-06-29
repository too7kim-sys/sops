-- =====================================================================
-- 초기 데이터 (운영 표준운영절차 데모 데이터)
-- ※ OPS_USER.PASSWORD 는 평문으로 입력되며, 애플리케이션 기동 시
--    DataBootstrap 이 BCrypt 로 일괄 암호화한다.
-- =====================================================================

-- 사용자(운영자) -------------------------------------------------------
INSERT INTO OPS_USER (USER_ID, USER_NM, PASSWORD, ROLE, DEPT_CD, EMAIL, TELNO, USE_AT) VALUES
 ('admin',  '운영관리자', 'admin123!', 'ADMIN',    'D100', 'admin@egov.go.kr',  '02-100-0001', 'Y'),
 ('oper01', '김운영',     'oper123!',  'OPERATOR', 'D110', 'oper01@egov.go.kr', '02-100-0002', 'Y'),
 ('oper02', '이담당',     'oper123!',  'OPERATOR', 'D120', 'oper02@egov.go.kr', '02-100-0003', 'Y'),
 ('user01', '박사용',     'user123!',  'USER',     'D130', 'user01@egov.go.kr', '02-100-0004', 'Y');

-- 응용시스템 마스터 ----------------------------------------------------
INSERT INTO OPS_SYSTEM (SYS_ID, SYS_NM, SYS_DESC, MNGR_NM, MNGR_DEPT, GRAD, USE_AT) VALUES
 ('SYS001', '대국민 민원포털',     '국민 대상 민원신청/처리 포털 시스템',   '김운영', '시스템운영부', '1', 'Y'),
 ('SYS002', '내부행정 업무시스템', '공무원 내부 행정업무 처리 시스템',       '이담당', '응용운영부',   '2', 'Y'),
 ('SYS003', '통합 인증시스템',     'SSO 기반 통합 인증/권한 관리 시스템',    '김운영', '시스템운영부', '1', 'Y'),
 ('SYS004', '정보공개 포털',       '행정정보 공개 청구/제공 포털',           '이담당', '응용운영부',   '3', 'Y');

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
 ('CHECK_RESULT', 'ABNORMAL', '이상', 2, 'Y');

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
 ('CSR_TYPE', 'GENERAL',  '일반 요청', 1, 'Y'),
 ('CSR_TYPE', 'INCIDENT', '장애',      2, 'Y'),
 ('CSR_TYPE', 'CHANGE',   '변경',      3, 'Y'),
 ('CSR_TYPE', 'BACKUP',   '백업',      4, 'Y'),
 ('CSR_TYPE', 'CONFIG',   '구성',      5, 'Y'),
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
 ('PROBLEM_STATUS', 'CLOSED',     '종료',     6, 'Y');

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
 ('RELEASE_ITEM_RESULT', 'SKIP',    '제외', 3, 'Y');

-- 보강 샘플 데이터 ------------------------------------------------------
-- 장애 목표복구일시(SLA) 및 문제 연계 (샘플)
UPDATE OPS_INCIDENT SET TARGET_RESOLVE_DT = TIMESTAMP '2026-06-20 11:25:00' WHERE INC_ID = 1;
UPDATE OPS_INCIDENT SET TARGET_RESOLVE_DT = TIMESTAMP '2026-06-23 16:10:00', REF_PRB_ID = 1 WHERE INC_ID = 2;
UPDATE OPS_INCIDENT SET TARGET_RESOLVE_DT = TIMESTAMP '2026-06-25 08:35:00' WHERE INC_ID = 3;
INSERT INTO OPS_INCIDENT_ESCAL (INC_ID, ESCAL_LEVEL, ESCAL_TO, REASON, ESCAL_BY) VALUES
 (2, 'L2', '인증기술지원팀', '세션 동기화 전문 분석 필요', 'oper01'),
 (2, 'MANAGER', '운영관리책임자', '1등급 장애 SLA 임박 보고', 'oper01');
-- 변경 CAB 심의 이력 (샘플)
INSERT INTO OPS_CHANGE_CAB (CHG_ID, DECISION, OPINION, REVIEWER) VALUES
 (1, 'APPROVED', '야간 적용 조건부 승인', '변경자문위원회'),
 (2, 'HOLD', '영향도 추가 분석 후 재심의', '변경자문위원회');
-- 배포 항목 (샘플)
INSERT INTO OPS_RELEASE_ITEM (REL_ID, ITEM_NM, ITEM_DESC, ITEM_RESULT) VALUES
 (1, 'minwon.war', '민원포털 애플리케이션', 'SUCCESS'),
 (1, 'idx_minwon.sql', '조회 인덱스 DDL', 'SUCCESS');

-- 장애관리 데이터 -----------------------------------------------------
INSERT INTO OPS_INCIDENT (SYS_ID, TITLE, CONTENT, SEVERITY, STATUS, OCCR_DT, RCPT_DT, RESOLVE_DT, CAUSE, ACTION, CHARGER_ID, REG_ID) VALUES
 ('SYS001', '민원신청 화면 응답지연', '민원신청 페이지 로딩이 30초 이상 지연됨', '2', 'CLOSED',
   TIMESTAMP '2026-06-20 09:15:00', TIMESTAMP '2026-06-20 09:25:00', TIMESTAMP '2026-06-20 11:40:00',
   'DB 커넥션풀 고갈', '커넥션풀 최대치 상향 및 슬로우쿼리 인덱스 추가', 'oper01', 'oper01'),
 ('SYS003', '통합인증 간헐적 로그인 실패', 'SSO 토큰 검증 간헐 실패로 로그인 불가', '1', 'ACTING',
   TIMESTAMP '2026-06-23 14:05:00', TIMESTAMP '2026-06-23 14:10:00', NULL,
   '인증서버 세션 동기화 지연 추정', '세션 클러스터 점검 진행 중', 'oper01', 'oper01'),
 ('SYS002', '결재 첨부파일 업로드 오류', '10MB 이상 첨부파일 업로드 시 오류 발생', '3', 'RECEIVED',
   TIMESTAMP '2026-06-24 08:30:00', TIMESTAMP '2026-06-24 08:35:00', NULL,
   NULL, NULL, NULL, 'user01');

INSERT INTO OPS_INCIDENT_HIS (INC_ID, STATUS, CONTENT, PROC_ID) VALUES
 (1, 'RECEIVED',  '장애 접수 - 민원신청 응답지연 신고', 'oper01'),
 (1, 'ANALYZING', '원인분석 - DB 커넥션 모니터링 결과 커넥션풀 고갈 확인', 'oper01'),
 (1, 'RESOLVED',  '커넥션풀 상향 및 인덱스 추가 후 정상화', 'oper01'),
 (1, 'CLOSED',    '재발방지 대책 수립 후 종결', 'admin'),
 (2, 'RECEIVED',  '장애 접수 - 통합인증 로그인 실패', 'oper01'),
 (2, 'ANALYZING', '인증서버 로그 분석 - 세션 동기화 지연 추정', 'oper01'),
 (2, 'ACTING',    '세션 클러스터 점검 및 노드 재기동 진행', 'oper01');

-- 변경관리 데이터 -----------------------------------------------------
INSERT INTO OPS_CHANGE (SYS_ID, TITLE, CHG_TYPE, REASON, CONTENT, STATUS, REQ_ID, REQ_DT, APPR_ID, APPR_DT, APPR_OPINION, PLAN_DT, APPLY_DT) VALUES
 ('SYS001', '민원신청 슬로우쿼리 인덱스 추가', 'DATABASE', '장애 재발방지', '민원신청 조회 SQL 대상 인덱스 2건 생성', 'COMPLETED',
   'oper01', TIMESTAMP '2026-06-20 13:00:00', 'admin', TIMESTAMP '2026-06-20 14:00:00', '승인. 야간 적용 권고', DATE '2026-06-20', TIMESTAMP '2026-06-20 22:30:00'),
 ('SYS002', '첨부파일 업로드 용량 제한 상향', 'CONFIG', '대용량 첨부 요구 증가', '업로드 제한 10MB→50MB 상향', 'REVIEWING',
   'oper02', TIMESTAMP '2026-06-24 09:00:00', NULL, NULL, NULL, DATE '2026-06-26', NULL),
 ('SYS004', '정보공개 목록 페이징 개선', 'PROGRAM', '대량 데이터 성능개선', '서버사이드 페이징 적용', 'REQUESTED',
   'oper02', TIMESTAMP '2026-06-24 10:30:00', NULL, NULL, NULL, DATE '2026-06-30', NULL);

-- 배포관리 데이터 -----------------------------------------------------
INSERT INTO OPS_RELEASE (SYS_ID, CHG_ID, VER, TITLE, CONTENT, STATUS, PLAN_DT, DEPLOY_DT, CHARGER_ID, RESULT) VALUES
 ('SYS001', 1, 'v1.4.2', '민원포털 성능개선 배포', 'DB 인덱스 적용 및 커넥션풀 설정 반영', 'DEPLOYED',
   TIMESTAMP '2026-06-20 22:00:00', TIMESTAMP '2026-06-20 22:30:00', 'oper01', '배포 성공 / 정상 확인'),
 ('SYS003', NULL, 'v2.1.0', '통합인증 세션 동기화 패치', '세션 클러스터 동기화 로직 개선', 'PLANNED',
   TIMESTAMP '2026-06-25 23:00:00', NULL, 'oper01', NULL);

-- 운영점검 데이터 -----------------------------------------------------
INSERT INTO OPS_CHECK (SYS_ID, CHK_TYPE, CHK_DT, CHKR_ID, RESULT, REMARK) VALUES
 ('SYS001', 'DAILY', DATE '2026-06-24', 'oper01', 'NORMAL',   '전 항목 정상'),
 ('SYS003', 'DAILY', DATE '2026-06-24', 'oper01', 'ABNORMAL', '인증 응답시간 임계치 초과(장애 INC-2 연계)'),
 ('SYS002', 'DAILY', DATE '2026-06-24', 'oper02', 'NORMAL',   '전 항목 정상');

INSERT INTO OPS_CHECK_ITEM (CHK_ID, ITEM_NM, ITEM_RESULT, ITEM_REMARK) VALUES
 (1, 'WAS 프로세스 기동 상태', 'NORMAL', ''),
 (1, 'DB 접속 상태',          'NORMAL', ''),
 (1, '디스크 사용률(80% 이하)', 'NORMAL', '62%'),
 (1, '배치 정상 수행 여부',   'NORMAL', ''),
 (2, 'WAS 프로세스 기동 상태', 'NORMAL', ''),
 (2, '인증 응답시간(2초 이내)', 'ABNORMAL', '평균 4.3초'),
 (2, 'DB 접속 상태',          'NORMAL', ''),
 (3, 'WAS 프로세스 기동 상태', 'NORMAL', ''),
 (3, 'DB 접속 상태',          'NORMAL', ''),
 (3, '디스크 사용률(80% 이하)', 'NORMAL', '55%');

-- 요청 소분류(CSR_SUBTYPE) : UPPER_CODE = 대분류(CSR_TYPE) 코드 ----------
INSERT INTO OPS_CODE (CODE_GRP, CODE_ID, CODE_NM, SORT_ORDR, USE_AT, UPPER_CODE) VALUES
 ('CSR_SUBTYPE', 'GEN_INQUIRY',  '단순/사용법 문의',  1, 'Y', 'GENERAL'),
 ('CSR_SUBTYPE', 'GEN_DATA',     '단순 데이터 확인',  2, 'Y', 'GENERAL'),
 ('CSR_SUBTYPE', 'GEN_REVIEW',   '업무/작업 검토',    3, 'Y', 'GENERAL'),
 ('CSR_SUBTYPE', 'GEN_ETC',      '기타',              4, 'Y', 'GENERAL'),
 ('CSR_SUBTYPE', 'INC_FAIL',     '기능 불가',         1, 'Y', 'INCIDENT'),
 ('CSR_SUBTYPE', 'INC_DEFECT',   '단순 오류/결함',    2, 'Y', 'INCIDENT'),
 ('CSR_SUBTYPE', 'INC_ETC',      '기타',              3, 'Y', 'INCIDENT'),
 ('CSR_SUBTYPE', 'CHG_NEW',      '신규 개발',         1, 'Y', 'CHANGE'),
 ('CSR_SUBTYPE', 'CHG_MOD',      '기능 수정/개발',    2, 'Y', 'CHANGE'),
 ('CSR_SUBTYPE', 'CHG_UIUX',     'UI/UX 변경',        3, 'Y', 'CHANGE'),
 ('CSR_SUBTYPE', 'CHG_DATA',     '데이터 변경',       4, 'Y', 'CHANGE'),
 ('CSR_SUBTYPE', 'CHG_INFO',     '정보/자료 요청',    5, 'Y', 'CHANGE'),
 ('CSR_SUBTYPE', 'CHG_SERVER',   '서버 작업 요청',    6, 'Y', 'CHANGE'),
 ('CSR_SUBTYPE', 'CHG_NETWORK',  '네트워크 작업 요청', 7, 'Y', 'CHANGE'),
 ('CSR_SUBTYPE', 'CHG_SECURITY', '보안 작업 요청',    8, 'Y', 'CHANGE'),
 ('CSR_SUBTYPE', 'CHG_DB',       'DB 작업 요청',      9, 'Y', 'CHANGE'),
 ('CSR_SUBTYPE', 'CHG_ETC',      '기타',             10, 'Y', 'CHANGE'),
 ('CSR_SUBTYPE', 'BAK_RESTORE',  '백업/복구 요청',    1, 'Y', 'BACKUP'),
 ('CSR_SUBTYPE', 'CFG_MOD',      '구성정보 변경요청', 1, 'Y', 'CONFIG'),
 ('CSR_SUBTYPE', 'CFG_ADD',      '구성정보 추가요청(신규)', 2, 'Y', 'CONFIG');

-- 요청 소분류별 요청내용 템플릿 (예시) ---------------------------------
INSERT INTO OPS_CSR_TPL (SUB_TYPE, CONTENT, USE_AT) VALUES
 ('GEN_INQUIRY', '<p>■ 문의 내용 : </p><p>■ 확인 요청 사항 : </p><p>■ 희망 회신 기한 : </p>', 'Y'),
 ('INC_FAIL',    '<p>■ 장애 현상 : </p><p>■ 발생 시각 : </p><p>■ 재현 경로/조건 : </p><p>■ 영향 범위 : </p>', 'Y'),
 ('CHG_NEW',     '<p>■ 개발 배경/목적 : </p><p>■ 요구 기능 : </p><p>■ 대상 화면/업무 : </p><p>■ 기대 효과 : </p>', 'Y'),
 ('CHG_MOD',     '<p>■ 수정 대상(화면/기능) : </p><p>■ 변경 전/후 : </p><p>■ 변경 사유 : </p>', 'Y'),
 ('CHG_DB',      '<p>■ 작업 대상(테이블/스키마) : </p><p>■ 작업 내용 : </p><p>■ 영향도/백업 여부 : </p><p>■ 작업 희망 일시 : </p>', 'Y'),
 ('BAK_RESTORE', '<p>■ 대상 시스템/데이터 : </p><p>■ 백업/복구 시점 : </p><p>■ 요청 사유 : </p>', 'Y'),
 ('CFG_ADD',     '<p>■ 신규 구성 항목 : </p><p>■ 사양/수량 : </p><p>■ 설치 위치 : </p><p>■ 도입 사유 : </p>', 'Y');

-- 확장 모듈 샘플 데이터 (개발/데모) --------------------------------------
-- ① 요청관리
INSERT INTO OPS_CSR (SYS_ID, TITLE, CONTENT, CSR_TYPE, CSR_SUB_TYPE, PRIORITY, STATUS, REQ_ID, REQ_DT, CHARGER_ID, PROC_CONTENT, PROC_DT) VALUES
 ('SYS001', '민원조회 화면 항목 추가 요청', '신청일자 컬럼 추가 요청', 'CHANGE', 'CHG_UIUX', 'MID', 'PROCESSED',
   'user01', TIMESTAMP '2026-06-23 10:00:00', 'oper01', '화면 항목 추가 반영', TIMESTAMP '2026-06-23 15:00:00'),
 ('SYS002', '결재선 지정 오류 문의', '전결 규정 문의', 'GENERAL', 'GEN_INQUIRY', 'LOW', 'CLOSED',
   'user01', TIMESTAMP '2026-06-24 09:30:00', 'oper02', '업무 안내 완료', TIMESTAMP '2026-06-24 10:10:00'),
 ('SYS004', '대용량 다운로드 기능 개선 요청', '엑셀 다운로드 속도 개선', 'CHANGE', 'CHG_MOD', 'HIGH', 'CLASSIFIED',
   'user01', TIMESTAMP '2026-06-24 11:00:00', 'oper02', NULL, NULL);
INSERT INTO OPS_CSR_HIS (CSR_ID, STATUS, CONTENT, PROC_ID) VALUES
 (1, 'REQUESTED', '요청 등록', 'user01'),
 (1, 'RECEIVED',  '요청 접수', 'oper01'),
 (1, 'PROCESSED', '화면 항목 추가 반영', 'oper01'),
 (2, 'REQUESTED', '요청 등록', 'user01'),
 (2, 'CLOSED',    '업무 안내 후 종료', 'oper02');

-- ④ 테스트관리
INSERT INTO OPS_TEST (SYS_ID, CHG_ID, TITLE, TEST_TYPE, TEST_ENV, STATUS, PLAN_DT, TESTER_ID, RESULT_SUMMARY) VALUES
 ('SYS001', 1, '민원포털 인덱스 적용 성능테스트', 'PERFORMANCE', 'OPS', 'ANALYZED', DATE '2026-06-20', 'oper01', '응답시간 30s→2s 개선 확인'),
 ('SYS002', NULL, '결재 첨부 업로드 단위테스트', 'UNIT', 'DEV', 'TESTING', DATE '2026-06-25', 'oper02', NULL);
INSERT INTO OPS_TEST_CASE (TEST_ID, CASE_NM, EXPECTED, CASE_RESULT, REMARK) VALUES
 (1, '민원목록 조회 응답시간', '2초 이내', 'PASS', '1.8초'),
 (1, '동시접속 100명 부하', '오류 없음', 'PASS', ''),
 (2, '10MB 파일 업로드', '정상 업로드', 'FAIL', '용량초과 오류');

-- ⑤ 연계관리
INSERT INTO OPS_INTERFACE (SYS_ID, TITLE, PARTNER_SYS, IF_TYPE, DATA_DESC, STATUS, REQ_ID, REQ_DT, CHARGER_ID, PLAN_DT) VALUES
 ('SYS001', '행정정보공동이용 주민정보 연계', '행정정보공동이용센터', 'API', '주민등록 기본정보 조회', 'COMPLETED',
   'oper01', TIMESTAMP '2026-06-18 09:00:00', 'oper01', DATE '2026-06-19'),
 ('SYS003', 'SSO 인증 토큰 연계', '통합인증센터', 'SYNC', 'SAML 토큰 교환', 'WORKING',
   'oper01', TIMESTAMP '2026-06-24 09:00:00', 'oper01', DATE '2026-06-26');
INSERT INTO OPS_INTERFACE_HIS (INTF_ID, STATUS, CONTENT, PROC_ID) VALUES
 (1, 'REQUESTED', '연계 요청 등록', 'oper01'),
 (1, 'COMPLETED', '연계 작업 및 테스트 완료', 'oper01'),
 (2, 'REQUESTED', '연계 요청 등록', 'oper01'),
 (2, 'WORKING',   '연계 작업 진행 중', 'oper01');

-- ⑥ 형상관리
INSERT INTO OPS_CI (SYS_ID, CI_NM, CI_TYPE, VER, CI_STATUS, LOCATION, OWNER_ID, CI_DESC) VALUES
 ('SYS001', '민원포털 소스코드', 'SOURCE', 'v1.4.2', 'BASELINED', 'git://repo/minwon', 'oper01', '민원포털 전체 소스'),
 ('SYS001', '민원포털 DB 스키마', 'DB', 'v1.4', 'CHECKED_IN', 'svn://db/minwon', 'oper01', 'DDL 스크립트'),
 ('SYS003', '통합인증 설정파일', 'CONFIG', 'v2.0', 'IDENTIFIED', '/etc/sso/config', 'oper01', 'SSO 환경설정');
INSERT INTO OPS_CI_HIS (CI_ID, CHG_TYPE, VER, CONTENT, PROC_ID) VALUES
 (1, 'IDENTIFY', 'v1.4.1', '형상항목 식별', 'oper01'),
 (1, 'BASELINE', 'v1.4.2', '배포 v1.4.2 베이스라인 설정', 'oper01'),
 (2, 'CHECKIN',  'v1.4',   '인덱스 추가 DDL 체크인', 'oper01');

-- ⑦ 운영상태관리 (이벤트)
INSERT INTO OPS_EVENT (SYS_ID, TITLE, EVT_TYPE, SEVERITY, STATUS, OCCR_DT, CONTENT, ACTION, CHARGER_ID, LINKED_INC_ID) VALUES
 ('SYS001', 'CPU 사용률 90% 초과', 'CPU', 'WARN', 'HANDLED', TIMESTAMP '2026-06-24 13:00:00', 'WAS 노드 CPU 임계 초과', '스케일아웃 조치', 'oper01', NULL),
 ('SYS003', '인증 응답시간 임계 초과', 'APP', 'CRITICAL', 'ESCALATED', TIMESTAMP '2026-06-23 14:05:00', '인증 응답 4초 이상', '장애로 전환', 'oper01', 2),
 ('SYS002', '디스크 사용률 85%', 'DISK', 'WARN', 'DETECTED', TIMESTAMP '2026-06-25 02:00:00', '첨부 스토리지 임계 근접', NULL, NULL, NULL);
INSERT INTO OPS_EVENT_HIS (EVT_ID, STATUS, CONTENT, PROC_ID) VALUES
 (1, 'DETECTED', '모니터링 임계 초과 감지', 'oper01'),
 (1, 'HANDLED',  '스케일아웃 후 정상화', 'oper01'),
 (2, 'DETECTED', '인증 응답시간 임계 초과 감지', 'oper01'),
 (2, 'ESCALATED','장애관리로 전환(INC-2)', 'oper01');

-- ⑨ 문제관리
INSERT INTO OPS_PROBLEM (SYS_ID, TITLE, CONTENT, PRIORITY, STATUS, ROOT_CAUSE, SOLUTION, REG_ID, CHARGER_ID, RESOLVE_DT) VALUES
 ('SYS003', '통합인증 간헐적 로그인 실패 반복', '동일 유형 장애 반복 발생', 'HIGH', 'RESOLVING',
   '세션 클러스터 동기화 지연', '세션 동기화 로직 개선(변경요청 연계)', 'admin', 'oper01', NULL),
 ('SYS001', '민원포털 야간 배치 지연', '월말 배치 지연 반복', 'MID', 'CLOSED',
   '인덱스 부재로 인한 풀스캔', '대상 테이블 인덱스 추가', 'oper01', 'oper01', TIMESTAMP '2026-06-21 02:00:00');
INSERT INTO OPS_PROBLEM_HIS (PRB_ID, STATUS, CONTENT, PROC_ID) VALUES
 (1, 'REGISTERED', '반복 장애 기반 문제 등록', 'admin'),
 (1, 'ANALYZING',  '근본원인 분석 착수', 'oper01'),
 (2, 'REGISTERED', '문제 등록', 'oper01'),
 (2, 'RESOLVED',   '인덱스 추가로 해결', 'oper01'),
 (2, 'CLOSED',     '재발 없음 확인 후 종료', 'admin');
INSERT INTO OPS_PROBLEM_INC (PRB_ID, INC_ID) VALUES (1, 2);
INSERT INTO OPS_KEDB (PRB_ID, TITLE, SYMPTOM, CAUSE, WORKAROUND, SOLUTION) VALUES
 (2, '야간 배치 지연(풀스캔)', '월말 배치 수행시간 급증', '대상 테이블 인덱스 부재', '배치 시간대 분산', '인덱스 추가 및 실행계획 점검');

-- =====================================================================
-- 결재선/공유 공통코드 및 데모 데이터
-- =====================================================================
INSERT INTO OPS_CODE (CODE_GRP, CODE_ID, CODE_NM, SORT_ORDR, USE_AT) VALUES
 ('LINE_TYPE', 'REVIEW',  '검토', 1, 'Y'),
 ('LINE_TYPE', 'APPROVE', '승인', 2, 'Y'),
 ('LINE_TYPE', 'HANDLE',  '처리', 3, 'Y'),
 ('APPR_STATUS', 'PENDING',  '대기',     1, 'Y'),
 ('APPR_STATUS', 'REVIEWED', '검토완료', 2, 'Y'),
 ('APPR_STATUS', 'APPROVED', '승인',     3, 'Y'),
 ('APPR_STATUS', 'REJECTED', '반려',     4, 'Y'),
 ('APPR_STATUS', 'DONE',     '처리완료', 5, 'Y');

-- 결재선 데모 (변경 CHG-1: 1단계 검토 병렬 2인 / 2단계 승인 1인 / 처리자 1인)
INSERT INTO OPS_APPR_LINE (BIZ_TYPE, BIZ_ID, LINE_TYPE, STEP_NO, SORT_NO, ASSIGNEE_ID, STATUS, OPINION, ACT_DT, REG_ID) VALUES
 ('CHANGE', 1, 'REVIEW',  1, 1, 'oper01', 'REVIEWED', '영향도 검토 완료, 진행 가능', CURRENT_TIMESTAMP, 'admin'),
 ('CHANGE', 1, 'REVIEW',  1, 2, 'oper02', 'PENDING',  NULL, NULL, 'admin'),
 ('CHANGE', 1, 'APPROVE', 2, 1, 'admin',  'PENDING',  NULL, NULL, 'admin'),
 ('CHANGE', 1, 'HANDLE',  3, 1, 'oper01', 'PENDING',  NULL, NULL, 'admin');

-- 공유 데모
INSERT INTO OPS_SHARE (BIZ_TYPE, BIZ_ID, USER_ID, SHARE_MEMO, READ_AT, SHARED_BY) VALUES
 ('CHANGE', 1, 'user01', '변경 일정 참고 바랍니다.', 'N', 'admin');

-- 결재 대상유형 코드 + 변경관리 기본 템플릿 데모
INSERT INTO OPS_CODE (CODE_GRP, CODE_ID, CODE_NM, SORT_ORDR, USE_AT) VALUES
 ('TARGET_TYPE', 'USER',      '사용자', 1, 'Y'),
 ('TARGET_TYPE', 'DEPT',      '부서',   2, 'Y'),
 ('TARGET_TYPE', 'REQUESTER', '요청자', 3, 'Y'),
 ('TARGET_TYPE', 'ALL',       '전체',   4, 'Y');

INSERT INTO OPS_APPR_TEMPLATE (BIZ_TYPE, KIND, LINE_TYPE, STEP_NO, SORT_NO, TARGET_TYPE, TARGET_VALUE, MEMO) VALUES
 ('CHANGE', 'LINE',  'REVIEW',  1, 1, 'DEPT',      'D120',  NULL),
 ('CHANGE', 'LINE',  'APPROVE', 2, 1, 'USER',      'admin', NULL),
 ('CHANGE', 'LINE',  'HANDLE',  3, 1, 'REQUESTER', NULL,    NULL),
 ('CHANGE', 'SHARE', NULL,      1, 1, 'DEPT',      'D110',  '변경 공유');

-- 부서 마스터 데모 (기존 사용자 DEPT_NM 과 동일 명칭)
INSERT INTO OPS_DEPT (DEPT_CD, DEPT_NM, UPPER_ID, MNGR_ID, SORT_ORDR, DEPT_DESC, USE_AT) VALUES
 ('D100', '정보화운영팀', NULL, 'admin',  1, '정보화 운영 총괄', 'Y'),
 ('D110', '시스템운영부', 1,    'oper01', 2, '인프라/시스템 운영', 'Y'),
 ('D120', '응용운영부',   1,    'oper02', 3, '응용프로그램 운영', 'Y'),
 ('D130', '민원지원과',   1,    NULL,     4, '대민 민원 지원', 'Y');
