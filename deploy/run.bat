@echo off
REM =====================================================================
REM  풀어서(Exploded) 배포 실행 스크립트 (Windows)
REM   - 이 스크립트가 위치한 디렉터리(BOOT-INF 가 있는 압축 해제 위치)에서 앱을 기동한다.
REM
REM  준비:   jar -xf egov-ops.jar
REM  사용법: run.bat                                   (기본 8085)
REM          run.bat --server.port=8090
REM          run.bat --spring.profiles.active=prod     (운영 PostgreSQL)
REM =====================================================================
cd /d "%~dp0"

if not exist "BOOT-INF" (
  echo [오류] 현재 폴더에 BOOT-INF 가 없습니다.
  echo        먼저 jar 를 풀어주세요:  jar -xf egov-ops.jar
  exit /b 1
)

if "%JAVA_OPTS%"=="" set JAVA_OPTS=-Xms256m -Xmx512m
java %JAVA_OPTS% org.springframework.boot.loader.launch.JarLauncher %*
