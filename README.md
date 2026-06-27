# 범정부 응용프로그램 운영관리시스템 (eGov-Ops)

전자정부 표준프레임워크(eGovFrame)의 계층형 아키텍처와 **범정부 정보시스템 운영관리 매뉴얼의
"응용프로그램 표준운영절차(SOP)"** 를 구현한 운영관리 웹 애플리케이션입니다.

장애·변경·배포·점검 등 응용프로그램 운영의 표준 처리절차를 단일 시스템에서 통합 관리합니다.

---

## 1. 주요 기능 (표준운영절차)

| 구분 | 모듈 | 표준 처리절차 |
|------|------|----------------|
| 운영현황 | **대시보드** | 진행중 장애/승인대기 변경/금일 배포·점검이상 통합 모니터링 |
| 협업/결재 | **결재선·공유** | 검토/승인/처리자 라인 관리, **병렬 검토·승인**, 공유(공람)·공유함 |
| 표준운영절차 | **장애관리** | 접수 → 원인분석 → 조치 → 조치완료 → 종결 (등급 1~4, 처리이력 추적) |
| 표준운영절차 | **변경관리** | 요청 → 검토 → 승인/반려 → 적용 → 완료 (심의의견·승인이력) |
| 표준운영절차 | **배포관리** | 배포계획 → 승인 → 배포중 → 배포완료/롤백 (버전·연계변경 관리) |
| 표준운영절차 | **운영점검** | 일일/주간/월간 점검 체크리스트(헤더+항목), 종합결과 자동산정 |
| 기준정보 | **응용시스템** | 운영대상 시스템 마스터(담당자/중요도등급) 관리 |
| 기준정보 | **사용자관리** | 운영자 계정·권한(ADMIN/OPERATOR/USER) 관리 |
| 기준정보 | **공통코드** | 상태/유형 등 코드 관리 |

---

## 2. 기술 스택

전자정부 표준프레임워크 **표준 웹 프로젝트 구조**(Spring 5 + XML 설정 + JSP + WAR)로 구성하였습니다.

| 계층 | 기술 |
|------|------|
| Presentation | Spring MVC 5.3 (`@Controller`), **JSP + JSTL**, 무료 웹에디터(**Summernote Lite**, MIT·로컬호스팅) |
| Business Logic | Service / ServiceImpl (`EgovAbstractServiceImpl` 상속) |
| Persistence | MyBatis (XML Mapper) + mybatis-spring |
| Security | Spring Security 5 (XML 설정, 폼 로그인, 권한기반 접근통제, BCrypt) |
| DB | **운영: PostgreSQL** / 개발·데모: H2 (In-memory) |
| 설정 | XML (web.xml, `context-*.xml`, `dispatcher-servlet.xml`) + `globals.properties` |
| 패키징 / 실행 | **WAR** / **Apache Tomcat 9+** (이클립스 *Run on Server* / `mvn cargo:run`) / Java 17 |

> 웹에디터: 요청내용·처리내용·장애원인/조치·변경내용 등 본문 입력란에 무료 WYSIWYG
> 에디터(Summernote Lite)를 적용했다. 자산(JS/CSS/폰트)은 외부 CDN 없이 `webapp/js/editor`,
> `webapp/css/editor` 에 **로컬 호스팅**하여 망분리 환경에서도 동작한다. 본문은 HTML 로 저장되며
> 해당 컬럼은 `VARCHAR(8000)`(본문)/`VARCHAR(4000)`(부가)로 확장하였다.
> 입력 주체는 인증된 운영자이지만, 외부 입력을 다룰 경우 저장 전 HTML 정제(예: jsoup 화이트리스트)를
> 적용할 것을 권장한다.

> 프로파일 분리: 기본(`dev`) 프로파일은 H2 In-memory 로 즉시 구동되며, 운영(`prod`) 프로파일은
> PostgreSQL 에 연결된다(`-Dspring.profiles.active=prod`). 매퍼 SQL 은 양쪽 모두 호환되도록
> 표준 함수(`COALESCE`, `TO_CHAR`, `TO_TIMESTAMP`, `OFFSET … FETCH NEXT …`)로 작성되어 있다.

> 표준프레임워크 실행환경(egovframework-rte) 의존성을 외부 저장소에서 받지 않고도 빌드/실행할 수 있도록,
> `egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl` 등 핵심 기반 클래스를 동등하게 자체 포함하였습니다.
> (Servlet `javax.*` 기반이므로 외부 톰캣은 **Tomcat 9.x** 를 사용한다 — Tomcat 10+ 아님.)

---

## 3. 패키지 구조 (표준프레임워크 네이밍 준수)

```
egovframework
├─ EgovOpsApplication.java          # 부트 메인
├─ rte.fdl.cmmn                     # 표준프레임워크 공통 기반(ServiceImpl 추상클래스)
├─ com                              # 공통 컴포넌트
│  ├─ cmm                           # 공통 VO/페이징(ComDefaultVO, PaginationInfo, SessionVO)
│  ├─ config                        # 보안설정, 인증(UserDetailsService), 초기화
│  └─ web                           # 메인/로그인 컨트롤러
└─ ops                             # 운영 업무 컴포넌트
   ├─ cmm.dashboard                 # 운영현황 대시보드
   ├─ cmm.code                      # 공통코드
   ├─ system                       # 응용시스템 마스터
   ├─ sys.user                     # 사용자관리
   ├─ incident                     # 장애관리
   ├─ change                       # 변경관리
   ├─ release                      # 배포관리
   └─ check                        # 운영점검
```

각 업무 모듈은 표준프레임워크 계층구조를 따릅니다.

```
<module>
├─ web/        Egov___Controller      (Presentation)
├─ service/    Egov___Service, ___VO   (Business 인터페이스/VO)
└─ service/impl/ Egov___ServiceImpl, ___Mapper  (Business 구현/Persistence)
```

---

## 4. 실행 방법

WAR 로 패키징되어 **Apache Tomcat 9.x** 에서 구동된다. (Servlet `javax.*` 기반 — Tomcat 10+ 아님)

### 4.1 이클립스에서 Tomcat 으로 실행 (권장)

1. **File → Import → Maven → Existing Maven Projects** 로 `pom.xml` 가져오기
2. **Window → Preferences → Server → Runtime Environments** 에 **Apache Tomcat 9.0** 등록
3. 프로젝트 우클릭 → **Run As → Run on Server** → Tomcat 9 선택
4. 접속: <http://localhost:8080/> (또는 톰캣 포트). 컨텍스트 루트(`/`) 로 띄우면 로그인 화면으로 이동

> `src/main/java`(소스) 와 `src/main/webapp`(JSP·WEB-INF) 가 표준 eGovFrame 웹 프로젝트 구조로 보이며, 소스 수정 후 서버 재시작으로 바로 확인된다.

### 4.2 명령행에서 즉시 실행 (내장 Tomcat 9, 검증용)

```bash
mvn clean package          # target/egov-ops.war
mvn cargo:run              # Tomcat 9.0 자동 내려받아 8085 포트로 구동
```
- 접속: <http://localhost:8085/> · 데모 계정 `admin / admin123!`
- 개발/데모는 H2 In-memory + 샘플 데이터 자동 적재(`dev` 기본 프로파일)

### 4.3 운영 (PostgreSQL) — `prod` 프로파일

```sql
CREATE ROLE egovops LOGIN PASSWORD 'egovops';
CREATE DATABASE egovops OWNER egovops;
```
WAR 를 톰캣 9 `webapps/` 에 배포하고, 톰캣 기동 시 시스템속성/환경변수로 접속정보를 주입한다.
```bash
# 예: $CATALINA_BASE/bin/setenv.sh
export JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=prod \
  -DDB_URL=jdbc:postgresql://DB호스트:5432/egovops -DDB_USERNAME=egovops -DDB_PASSWORD=********"
```
- 스키마/기준데이터는 비파괴적(`CREATE TABLE IF NOT EXISTS`, `ON CONFLICT DO NOTHING`)으로 기동 시 멱등 적용.
  운영에는 샘플 거래데이터를 적재하지 않고 **기준정보(사용자/응용시스템/공통코드)** 만 시딩한다.
- 자동 적재를 끄려면 `-DSQL_INIT_ENABLED=false` (DDL/시드: `src/main/resources/db/postgresql/`)
- **최초 적용 후 `admin` 계정 비밀번호를 반드시 변경할 것.**

### 4.4 이클립스(Eclipse / 전자정부 표준프레임워크 IDE) 상세

Maven 프로젝트이므로 **File → Import → Maven → Existing Maven Projects** 로 가져와 실행합니다.
JDK 21 등록·Lombok 설치·UTF-8 인코딩 등 상세 절차는 **[docs/ECLIPSE.md](docs/ECLIPSE.md)** 참고.

### 데모 계정

| 아이디 | 비밀번호 | 권한 | 비고 |
|--------|----------|------|------|
| `admin` | `admin123!` | 운영관리자(ADMIN) | 사용자/공통코드 관리 포함 전체 |
| `oper01` | `oper123!` | 운영자(OPERATOR) | 장애/변경/배포/점검 처리 |
| `oper02` | `oper123!` | 운영자(OPERATOR) | |
| `user01` | `user123!` | 일반사용자(USER) | 조회/요청 |

> 초기 데이터의 평문 비밀번호는 기동 시 `DataBootstrap` 이 BCrypt 로 일괄 암호화합니다.

---

## 5. 데이터 모델

| 테이블 | 설명 |
|--------|------|
| `OPS_USER` | 사용자(운영자) |
| `OPS_DEPT` | 부서 마스터(상위부서/부서장/정렬/사용여부) — 결재선/공유 대상 지정에 사용 |
| `OPS_SYSTEM` | 응용시스템 마스터 |
| `OPS_CODE` | 공통코드 |
| `OPS_INCIDENT` / `OPS_INCIDENT_HIS` | 장애 / 장애 처리이력 |
| `OPS_CHANGE` | 변경관리 |
| `OPS_RELEASE` | 배포관리 |
| `OPS_CHECK` / `OPS_CHECK_ITEM` | 운영점검 / 점검항목 |
| `OPS_APPR_LINE` | 결재선(검토/승인/처리자 라인) — `bizType`+`bizId` 로 업무 공통 연결 |
| `OPS_SHARE` | 공유(공람/참조) |
| `OPS_APPR_TEMPLATE` | 결재선/공유 관리별 기본 템플릿 |

스키마: `src/main/resources/db/schema.sql`, 초기데이터: `src/main/resources/db/data.sql`

---

## 6. 표준운영절차 상태 흐름

```
[장애]  RECEIVED → ANALYZING → ACTING → RESOLVED → CLOSED
[변경]  REQUESTED → REVIEWING → APPROVED/REJECTED → APPLIED → COMPLETED
[배포]  PLANNED → APPROVED → DEPLOYING → DEPLOYED/ROLLBACK
[점검]  항목 결과 집계 → 종합결과(NORMAL/ABNORMAL) 자동 산정
```

> **진행 단계 시각화**: 요청·변경·배포·장애·문제·테스트·연계·형상·운영상태 9개 업무의
> 목록에는 현재 단계(미니 스테퍼), 상세에는 전체 진행단계 스테퍼를 표시한다. 단계 정의는
> 공통 프래그먼트 `WEB-INF/jsp/include/stage.jsp` 한 곳에서 관리하며, 표준경로 외 상태
> (반려/롤백/실패/장애전환)는 단절 배지로 구분한다.

---

## 7. 결재선(검토/승인/처리자) · 병렬 처리 · 공유

업무 모듈에 독립적인 **공통 결재선/공유 컴포넌트**(`egovframework.ops.appr`)를 제공한다.
각 업무 상세화면에 `<c:import url="/appr/panel">` 로 포함되어 모듈 컨트롤러 수정 없이 동작한다.

- **라인 유형**: 검토(REVIEW) / 승인(APPROVE) / 처리(HANDLE)
- **병렬 처리**: 동일 **단계(STEP)** 번호에 여러 대상자를 지정하면 병렬로 검토·승인한다.
  전체 상태는 승인 라인 집계로 산정(전원 승인 → 승인완료, 1건이라도 반려 → 반려).
- **대상 지정(사용자/부서/요청자/전체)**: 결재선·공유 대상을 개별 사용자뿐 아니라 **부서**(부서원 전체),
  **요청자**(해당 건 요청자/담당자), **전체**(모든 활성 운영자)로 지정하면 적용 시 실제 사용자로 자동 전개된다.
- **관리별 기본설정(템플릿)**: 운영관리자는 `결재 기본설정`(`/appr/template`)에서 업무 구분별 기본 결재선/공유를
  정의한다. **신규 건 등록 시 해당 업무의 기본설정(검토/승인/처리자/공유)이 자동 전개**되어 적용된다.
  (템플릿이 없거나 결재선이 비어 있는 건은 상세의 **[기본 결재선/공유 적용]** 버튼으로 수동 전개 가능.)
- **권한**: 결재선/공유의 추가·삭제는 운영자(ADMIN/OPERATOR), 라인 처리는 **본인 라인**(또는 ADMIN),
  기본설정(템플릿) 관리는 **운영관리자(ADMIN)** 전용.
- **공유(공람)**: 대상자에게 업무를 공유하고, 좌측 **공유함**(`/appr/shared`)과 헤더 미열람 배지로 확인.
  대상자가 상세를 열람하면 자동으로 열람 처리된다.
- 단계(STEP)는 그룹/표시 순서이며, 처리는 동시(병렬) 가능하다.
- **모듈 상태 자동 연동**: 승인 라인 전원 승인/반려 시 해당 업무의 자체 상태를 자동 전이한다.
  승인 게이트가 정의된 모듈만 적용된다 — 변경(승인→`APPROVED`/반려→`REJECTED`),
  배포(승인→`APPROVED`), 요청(승인→`IN_PROGRESS`/반려→`REJECTED`).
  변경은 승인 확정 시 **승인자(처리자)·승인일시**(`APPR_ID`/`APPR_DT`)도 함께 기록한다.
  그 외 모듈은 상태를 강제 전이하지 않고 협업 레이어로만 동작한다.
  (전이 규칙의 테이블/컬럼은 서비스 내부 화이트리스트 값으로, 외부 입력과 무관하다.)
