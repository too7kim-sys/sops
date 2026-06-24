-- =====================================================================
-- 초기 데이터 (운영 표준운영절차 데모 데이터)
-- ※ OPS_USER.PASSWORD 는 평문으로 입력되며, 애플리케이션 기동 시
--    DataBootstrap 이 BCrypt 로 일괄 암호화한다.
-- =====================================================================

-- 사용자(운영자) -------------------------------------------------------
INSERT INTO OPS_USER (USER_ID, USER_NM, PASSWORD, ROLE, DEPT_NM, EMAIL, TELNO, USE_AT) VALUES
 ('admin',  '운영관리자', 'admin123!', 'ADMIN',    '정보화운영팀', 'admin@egov.go.kr',  '02-100-0001', 'Y'),
 ('oper01', '김운영',     'oper123!',  'OPERATOR', '시스템운영부', 'oper01@egov.go.kr', '02-100-0002', 'Y'),
 ('oper02', '이담당',     'oper123!',  'OPERATOR', '응용운영부',   'oper02@egov.go.kr', '02-100-0003', 'Y'),
 ('user01', '박사용',     'user123!',  'USER',     '민원지원과',   'user01@egov.go.kr', '02-100-0004', 'Y');

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
