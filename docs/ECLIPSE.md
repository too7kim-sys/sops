# 이클립스(Eclipse / 전자정부 표준프레임워크 IDE)에서 사용하기

본 프로젝트는 **Maven 프로젝트**이므로 이클립스에서 그대로 가져와(Import) 실행할 수 있습니다.
전자정부 표준프레임워크 개발환경(eGovFrame Eclipse IDE)에서도 동일하게 적용됩니다.

> ⚠️ 시작 전 반드시 확인 — 본 프로젝트는 **Spring Boot 3 / Java 21** 기반입니다.
> 전자정부 표준프레임워크 IDE에 기본 탑재된 JDK(8/11)로는 빌드되지 않으므로 **JDK 21**을 별도 등록해야 하며,
> `@Data` 등 Lombok 애너테이션 인식을 위해 **Lombok을 이클립스에 설치**해야 합니다. (아래 1·3단계)

---

## 1. 사전 준비 (JDK 21 등록)

1. JDK 21 설치 (Temurin/Oracle/OpenJDK 등)
2. 이클립스 메뉴 **Window → Preferences → Java → Installed JREs → Add** 에서 JDK 21 추가 후 기본값으로 체크
3. **Window → Preferences → Java → Compiler** 의 *Compiler compliance level* 을 **21** 로 설정

---

## 2. 프로젝트 가져오기 (Import)

소스를 받은 뒤(`git clone` 또는 압축 해제) 다음 순서로 가져옵니다.

1. 메뉴 **File → Import...**
2. **Maven → Existing Maven Projects** 선택 → *Next*
3. *Root Directory* 에 프로젝트 폴더(`pom.xml` 이 있는 위치) 지정 → `pom.xml` 체크 → *Finish*
4. 최초 가져오기 시 m2e(Maven Integration)가 의존성을 자동 내려받습니다. (네트워크 필요, 수 분 소요)

> 의존성이 보이지 않으면 프로젝트 우클릭 → **Maven → Update Project...** (Alt+F5) → *Force Update* 체크 후 OK.

---

## 3. Lombok 설치 (필수)

본 프로젝트의 VO 는 Lombok `@Data`(getter/setter 자동생성)를 사용합니다.
**Lombok 미설치 시 이클립스 편집기에서 getter/setter 를 찾지 못해 컴파일 오류**가 표시됩니다.
(Maven 빌드 자체는 정상이지만 IDE 편집기 인식을 위해 설치가 필요합니다.)

1. Lombok jar 다운로드 — <https://projectlombok.org/download> 또는 로컬 Maven 저장소의
   `org.projectlombok:lombok` jar 사용
2. 설치 실행:
   ```bash
   java -jar lombok.jar
   ```
3. 설치 관리자에서 사용 중인 이클립스(또는 eGovFrame IDE) 실행파일 경로를 지정 → **Install/Update**
4. 이클립스 재시작

> 설치가 완료되면 이클립스의 `eclipse.ini` 끝에 `-javaagent:lombok.jar` 항목이 추가됩니다.

---

## 4. 인코딩 설정 (한글 깨짐 방지)

소스/주석/메시지에 한글이 포함되어 있으므로 워크스페이스 인코딩을 **UTF-8** 로 맞춥니다.

- **Window → Preferences → General → Workspace → Text file encoding → Other: UTF-8**

---

## 5. 실행 (개발/데모 · H2)

별도 DB 설치 없이 즉시 실행됩니다.

- **방법 A (자바 애플리케이션):**
  `src/main/java/egovframework/EgovOpsApplication.java` 우클릭 → **Run As → Java Application**
- **방법 B (Maven):**
  프로젝트 우클릭 → **Run As → Maven build...** → *Goals* 에 `spring-boot:run` 입력 → *Run*

기동 후 브라우저에서 접속합니다.

- 주소: <http://localhost:8080>
- 데모 계정: `admin` / `admin123!` (운영자 `oper01` / `oper123!`)

---

## 6. 실행 (운영 · PostgreSQL)

운영 프로파일(`prod`)로 실행하려면 **Run → Run Configurations...** 에서 위 *방법 A* 실행항목을 선택하고:

- **Arguments 탭 → Program arguments:**
  ```
  --spring.profiles.active=prod
  ```
- **Environment 탭 → New** 로 접속정보 등록:
  | 변수 | 예시 |
  |------|------|
  | `DB_URL` | `jdbc:postgresql://localhost:5432/egovops` |
  | `DB_USERNAME` | `egovops` |
  | `DB_PASSWORD` | `********` |

PostgreSQL 사전 준비(최초 1회):
```sql
CREATE ROLE egovops LOGIN PASSWORD '********';
CREATE DATABASE egovops OWNER egovops;
```
스키마/기준데이터는 기동 시 자동(멱등) 적용됩니다. (상세: `README.md` 4.2 운영 절)

---

## 7. 자주 묻는 문제 (Troubleshooting)

| 증상 | 원인 / 해결 |
|------|-------------|
| getter/setter 를 못 찾는다는 컴파일 오류 | Lombok 미설치 → **3단계** 수행 후 이클립스 재시작 |
| `Unsupported class file major version` / 빌드 실패 | JDK 21 미적용 → **1단계** 및 프로젝트 우클릭 → Properties → Java Build Path/Compiler 21 확인 |
| 의존성(빨간 X) 미해결 | **Maven → Update Project (Force Update)**, 네트워크/프록시 확인 |
| 한글 주석·화면 깨짐 | 워크스페이스 인코딩 UTF-8 (**4단계**) |
| 포트 8080 사용 중 | `src/main/resources/application.yml` 의 `server.port` 변경 |
| 운영 기동 시 DB 접속 오류 | `DB_URL/DB_USERNAME/DB_PASSWORD` 환경변수 및 PostgreSQL 기동 여부 확인 |

---

## 8. 참고

- 빌드 산출물: `mvn clean package` → `target/egov-ops.jar` (실행형 Jar, `java -jar` 로 단독 구동 가능)
- 이클립스는 내장 Maven(m2e)을 사용하므로 별도 Maven 설치 없이도 빌드/실행됩니다.
- JSP 가 아닌 Thymeleaf 뷰를 사용하므로 WAS(톰캣) 별도 설치 없이 내장 톰캣으로 실행됩니다.
