# 범정부 응용프로그램 운영관리시스템 (eGov-Ops)

전자정부 표준프레임워크(eGovFrame)의 계층형 아키텍처와 **범정부 정보시스템 운영관리 매뉴얼의
"응용프로그램 표준운영절차(SOP)"** 를 구현한 운영관리 웹 애플리케이션입니다.

장애·변경·배포·점검 등 응용프로그램 운영의 표준 처리절차를 단일 시스템에서 통합 관리합니다.

---

## 1. 주요 기능 (표준운영절차)

| 구분 | 모듈 | 표준 처리절차 |
|------|------|----------------|
| 운영현황 | **대시보드** | 진행중 장애/승인대기 변경/금일 배포·점검이상 통합 모니터링 |
| 표준운영절차 | **장애관리** | 접수 → 원인분석 → 조치 → 조치완료 → 종결 (등급 1~4, 처리이력 추적) |
| 표준운영절차 | **변경관리** | 요청 → 검토 → 승인/반려 → 적용 → 완료 (심의의견·승인이력) |
| 표준운영절차 | **배포관리** | 배포계획 → 승인 → 배포중 → 배포완료/롤백 (버전·연계변경 관리) |
| 표준운영절차 | **운영점검** | 일일/주간/월간 점검 체크리스트(헤더+항목), 종합결과 자동산정 |
| 기준정보 | **응용시스템** | 운영대상 시스템 마스터(담당자/중요도등급) 관리 |
| 기준정보 | **사용자관리** | 운영자 계정·권한(ADMIN/OPERATOR/USER) 관리 |
| 기준정보 | **공통코드** | 상태/유형 등 코드 관리 |

---

## 2. 기술 스택

전자정부 표준프레임워크 4.x 호환 계층구조를 Spring Boot 기반으로 구성하였습니다.

| 계층 | 기술 |
|------|------|
| Presentation | Spring MVC (`@Controller`), Thymeleaf |
| Business Logic | Service / ServiceImpl (`EgovAbstractServiceImpl` 상속) |
| Persistence | MyBatis (XML Mapper), `@Mapper` |
| Security | Spring Security (폼 로그인, 권한기반 접근통제, BCrypt) |
| DB | H2 (In-memory, Oracle 호환모드) |
| Build / Runtime | Maven, Java 21 |

> 표준프레임워크 실행환경(egovframework-rte) 의존성을 외부 저장소에서 받지 않고도 빌드/실행할 수 있도록,
> `egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl` 등 핵심 기반 클래스를 동등하게 자체 포함하였습니다.

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

```bash
# 빌드
mvn clean package

# 실행
java -jar target/egov-ops.jar
#  또는
mvn spring-boot:run
```

- 접속: <http://localhost:8080>
- H2 콘솔: <http://localhost:8080/h2-console> (JDBC URL `jdbc:h2:mem:egovops`, user `sa`)

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
| `OPS_SYSTEM` | 응용시스템 마스터 |
| `OPS_CODE` | 공통코드 |
| `OPS_INCIDENT` / `OPS_INCIDENT_HIS` | 장애 / 장애 처리이력 |
| `OPS_CHANGE` | 변경관리 |
| `OPS_RELEASE` | 배포관리 |
| `OPS_CHECK` / `OPS_CHECK_ITEM` | 운영점검 / 점검항목 |

스키마: `src/main/resources/db/schema.sql`, 초기데이터: `src/main/resources/db/data.sql`

---

## 6. 표준운영절차 상태 흐름

```
[장애]  RECEIVED → ANALYZING → ACTING → RESOLVED → CLOSED
[변경]  REQUESTED → REVIEWING → APPROVED/REJECTED → APPLIED → COMPLETED
[배포]  PLANNED → APPROVED → DEPLOYING → DEPLOYED/ROLLBACK
[점검]  항목 결과 집계 → 종합결과(NORMAL/ABNORMAL) 자동 산정
```
