# 이클립스(Eclipse / 전자정부 표준프레임워크 IDE)에서 사용하기

본 프로젝트는 **전자정부 표준프레임워크 표준 웹 프로젝트 구조**입니다.
- Spring Framework 5.3 (MVC) + Spring Security 5 (XML 설정)
- JSP + JSTL 뷰, MyBatis(XML Mapper)
- **WAR 패키징 / Apache Tomcat 9.x 구동** (Servlet `javax.*` — Tomcat 10+ 아님)
- Java 17, Maven

이클립스에서 `src/main/java`(소스)와 `src/main/webapp`(JSP·WEB-INF)이 표준 구조로 보이며,
**Run on Server(Tomcat 9)** 로 바로 확인할 수 있습니다.

---

## 1. 사전 준비

1. **JDK 17** 설치 → **Window → Preferences → Java → Installed JREs** 에 등록, *Compiler compliance level* 17
2. **Apache Tomcat 9.0** 다운로드(압축 해제) → **Preferences → Server → Runtime Environments → Add → Apache Tomcat v9.0** 에서 설치 경로 지정
3. **Lombok 설치(필수)** — VO 가 `@Data` 를 사용하므로 미설치 시 편집기에서 getter/setter 오류가 표시됨
   - `java -jar lombok.jar` 실행 → 이클립스 지정 → Install → 재시작
4. **Window → Preferences → General → Workspace → Text file encoding → UTF-8**

---

## 2. 프로젝트 가져오기

1. **File → Import… → Maven → Existing Maven Projects**
2. *Root Directory* 에 `pom.xml` 이 있는 폴더 지정 → `pom.xml` 체크 → **Finish**
3. 최초 가져오기 시 의존성 자동 다운로드(네트워크 필요)
4. 안 보이면 프로젝트 우클릭 → **Maven → Update Project (Alt+F5)** → *Force Update*

> 소스는 **Project Explorer 의 `Java Resources > src/main/java`** 아래에 표시됩니다.
> JSP/설정은 `src/main/webapp`( `WEB-INF/jsp`, `WEB-INF/web.xml`, `WEB-INF/config/.../dispatcher-servlet.xml` )에 있습니다.

---

## 3. Tomcat 으로 실행 (Run on Server)

1. 프로젝트 우클릭 → **Run As → Run on Server**
2. 등록한 **Tomcat v9.0** 선택 → Finish
3. 브라우저: <http://localhost:8080/> (톰캣 기본 포트). 로그인 화면으로 이동
   - 포트 변경: Servers 뷰의 서버 더블클릭 → **Ports** 의 HTTP 포트 수정
   - 루트(`/`)가 아닌 컨텍스트로 뜨면(예: `/egov-sop`) 해당 경로로 접속하거나, 서버의 *Modules* 에서 Path 를 `/` 로 변경

**데모 계정**

| 아이디 | 비밀번호 | 권한 |
|--------|----------|------|
| `admin` | `admin123!` | 운영관리자(ADMIN) |
| `oper01` | `oper123!` | 운영자(OPERATOR) |
| `user01` | `user123!` | 일반사용자(USER) |

> 개발/데모는 H2 In-memory + 샘플 데이터가 기동 시 자동 적재됩니다(`dev` 기본 프로파일).

---

## 4. 명령행 실행 (검증용, 톰캣 설치 없이)

```bash
mvn clean package      # target/egov-sop.war
mvn cargo:run          # Tomcat 9.0 자동 내려받아 8085 포트로 구동 → http://localhost:8085/
```

---

## 5. 운영(PostgreSQL) 배포

WAR(`target/egov-sop.war`)를 톰캣 9 `webapps/` 에 배포하고, 톰캣 기동 시 프로파일/접속정보를 주입합니다.

```bash
# $CATALINA_BASE/bin/setenv.sh (예)
export JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=prod \
  -DDB_URL=jdbc:postgresql://DB:5432/egovsop -DDB_USERNAME=egovsop -DDB_PASSWORD=********"
```
- 스키마/기준데이터는 비파괴적·멱등 적용. 자동 적재 끄기: `-DSQL_INIT_ENABLED=false`
- 이클립스에서 운영 프로파일로 띄우려면 서버 실행설정(VM arguments)에 `-Dspring.profiles.active=prod` 와 DB 시스템속성 추가

---

## 6. 구조 / 설정 파일

```
src/main/java/egovframework/                 # Controller / Service / ServiceImpl / Mapper / VO
src/main/resources/
  egovframework/spring/context-*.xml         # 루트 컨텍스트(공통/DataSource/MyBatis/Tx/Security)
  egovframework/mapper/**/*.xml              # MyBatis 매퍼
  egovframework/globals.properties           # 전역 프로퍼티
  db/ , db/postgresql/                       # 스키마/초기데이터
  logback.xml
src/main/webapp/
  WEB-INF/web.xml                            # ContextLoaderListener / DispatcherServlet / Security 필터
  WEB-INF/config/egovframework/springmvc/dispatcher-servlet.xml
  WEB-INF/jsp/**/*.jsp                        # 화면(JSP)
  css/
```

---

## 7. 자주 묻는 문제

| 증상 | 해결 |
|------|------|
| getter/setter 컴파일 오류 | Lombok 미설치 → 1-3 단계 후 재시작 |
| `src/main/java` 안 보임 | **Maven → Update Project (Force)**, `Java Resources` 노드 확인 |
| 톰캣 배포 후 404/오류 | **Tomcat 9.x** 인지 확인(10+ 는 `javax`→`jakarta` 불일치로 미동작) |
| JSP 에 `HttpServletRequest cannot be resolved to a type` | 프로젝트에 톰캣 런타임 미연결 → **Properties → Targeted Runtimes** 에서 **Apache Tomcat v9.0** 체크(서블릿 API 가 JSP 검증 클래스패스에 추가됨). 우회: **Properties → Validation** 에서 JSP Validator 끄기 |
| 한글 깨짐 | 워크스페이스 인코딩 UTF-8 |
| 포트 충돌 | Servers 뷰 → 서버 → Ports 변경, 또는 cargo `cargo.servlet.port` |
