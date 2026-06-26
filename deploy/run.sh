#!/usr/bin/env sh
# =====================================================================
# 풀어서(Exploded) 배포 실행 스크립트 (Linux/macOS)
#  - 이 스크립트가 위치한 디렉터리(BOOT-INF 가 있는 압축 해제 위치)에서 앱을 기동한다.
#  - jar/war 단일 산출물이 아니라 "압축을 푼 디렉터리" 그대로 실행한다.
#
# 준비:   jar -xf egov-ops.jar      (BOOT-INF / META-INF / org 가 생성됨)
# 사용법: sh run.sh                                  # 기본(application.yml: 8085)
#         sh run.sh --server.port=8090               # 포트 지정
#         sh run.sh --spring.profiles.active=prod    # 운영(PostgreSQL)
# 환경변수: JAVA_OPTS(힙 등), 운영 DB(DB_URL/DB_USERNAME/DB_PASSWORD), SERVER_PORT
# =====================================================================
DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR" || exit 1

if [ ! -d "BOOT-INF" ]; then
  echo "[오류] '$DIR' 에 BOOT-INF 가 없습니다."
  echo "       먼저 jar 를 풀어주세요:  jar -xf egov-ops.jar"
  exit 1
fi

exec java ${JAVA_OPTS:--Xms256m -Xmx512m} \
     org.springframework.boot.loader.launch.JarLauncher "$@"
