-- =====================================================================
-- 운영(PostgreSQL) 초기 기준데이터 (멱등 : ON CONFLICT DO NOTHING)
--   - 운영 환경에는 샘플 거래데이터(장애/변경/배포/점검)를 적재하지 않는다.
--   - 사용자/응용시스템/공통코드 등 기준정보만 시딩한다.
--   - OPS_USER.PASSWORD 평문은 기동 시 DataBootstrap 이 BCrypt 로 일괄 암호화한다.
--   - 최초 운영 적용 후 관리자 비밀번호는 반드시 변경할 것.
-- =====================================================================

-- 사용자(운영자) -------------------------------------------------------
INSERT INTO OPS_USER (USER_ID, USER_NM, PASSWORD, ROLE, DEPT_CD, EMAIL, TELNO, USE_AT) VALUES
 ('admin',  '운영관리자', 'admin123!', 'ADMIN',    'D100', 'admin@egov.go.kr',  '02-100-0001', 'Y')
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
 ('PROBLEM_STATUS', 'CLOSED',     '종료',     6, 'Y')
ON CONFLICT (CODE_GRP, CODE_ID) DO NOTHING;

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
 ('CSR_SUBTYPE', 'CFG_ADD',      '구성정보 추가요청(신규)', 2, 'Y', 'CONFIG')
ON CONFLICT (CODE_GRP, CODE_ID) DO NOTHING;

-- 요청 소분류별 요청내용 템플릿 (예시, 멱등) ---------------------------
INSERT INTO OPS_CSR_TPL (SUB_TYPE, CONTENT, USE_AT) VALUES
 ('GEN_INQUIRY', '<p>■ 문의 내용 : </p><p>■ 확인 요청 사항 : </p><p>■ 희망 회신 기한 : </p>', 'Y'),
 ('INC_FAIL',    '<p>■ 장애 현상 : </p><p>■ 발생 시각 : </p><p>■ 재현 경로/조건 : </p><p>■ 영향 범위 : </p>', 'Y'),
 ('CHG_NEW',     '<p>■ 개발 배경/목적 : </p><p>■ 요구 기능 : </p><p>■ 대상 화면/업무 : </p><p>■ 기대 효과 : </p>', 'Y'),
 ('CHG_MOD',     '<p>■ 수정 대상(화면/기능) : </p><p>■ 변경 전/후 : </p><p>■ 변경 사유 : </p>', 'Y'),
 ('CHG_DB',      '<p>■ 작업 대상(테이블/스키마) : </p><p>■ 작업 내용 : </p><p>■ 영향도/백업 여부 : </p><p>■ 작업 희망 일시 : </p>', 'Y'),
 ('BAK_RESTORE', '<p>■ 대상 시스템/데이터 : </p><p>■ 백업/복구 시점 : </p><p>■ 요청 사유 : </p>', 'Y'),
 ('CFG_ADD',     '<p>■ 신규 구성 항목 : </p><p>■ 사양/수량 : </p><p>■ 설치 위치 : </p><p>■ 도입 사유 : </p>', 'Y')
ON CONFLICT (SUB_TYPE) DO NOTHING;

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

-- 결재 대상유형 코드 (운영: 기준코드만)
INSERT INTO OPS_CODE (CODE_GRP, CODE_ID, CODE_NM, SORT_ORDR, USE_AT) VALUES
 ('TARGET_TYPE', 'USER',      '사용자', 1, 'Y'),
 ('TARGET_TYPE', 'DEPT',      '부서',   2, 'Y'),
 ('TARGET_TYPE', 'REQUESTER', '요청자', 3, 'Y'),
 ('TARGET_TYPE', 'ALL',       '전체',   4, 'Y')
ON CONFLICT (CODE_GRP, CODE_ID) DO NOTHING;

-- 부서 마스터 (운영 기준정보)
INSERT INTO OPS_DEPT (DEPT_CD, DEPT_NM, UPPER_ID, MNGR_ID, SORT_ORDR, DEPT_DESC, USE_AT) VALUES
 ('D100', '정보화운영팀', NULL, 'admin',  1, '정보화 운영 총괄', 'Y'),
 ('D110', '시스템운영부', 1,    'oper01', 2, '인프라/시스템 운영', 'Y'),
 ('D120', '응용운영부',   1,    'oper02', 3, '응용프로그램 운영', 'Y'),
 ('D130', '민원지원과',   1,    NULL,     4, '대민 민원 지원', 'Y')
ON CONFLICT (DEPT_CD) DO NOTHING;

-- 결재 기본설정(템플릿) — 변경관리 기본 결재선/공유 (TPL_ID 가 시리얼이라 ON CONFLICT 불가 → 존재여부로 가드)
-- 결재선/공유가 비어 있는 업무 패널 조회 시 이 템플릿이 자동 적용된다.
INSERT INTO OPS_APPR_TEMPLATE (BIZ_TYPE, KIND, LINE_TYPE, STEP_NO, SORT_NO, TARGET_TYPE, TARGET_VALUE, MEMO)
SELECT v.* FROM (VALUES
 ('CHANGE', 'LINE',  'REVIEW',  1, 1, 'DEPT',      'D120',  CAST(NULL AS VARCHAR)),
 ('CHANGE', 'LINE',  'APPROVE', 2, 1, 'USER',      'admin', NULL),
 ('CHANGE', 'LINE',  'HANDLE',  3, 1, 'REQUESTER', NULL,    NULL),
 ('CHANGE', 'SHARE', NULL,      1, 1, 'DEPT',      'D110',  '변경 공유')
) AS v(BIZ_TYPE, KIND, LINE_TYPE, STEP_NO, SORT_NO, TARGET_TYPE, TARGET_VALUE, MEMO)
WHERE NOT EXISTS (SELECT 1 FROM OPS_APPR_TEMPLATE WHERE BIZ_TYPE = 'CHANGE');
