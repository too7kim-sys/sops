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
