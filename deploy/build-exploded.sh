#!/usr/bin/env sh
# =====================================================================
# 풀어서(Exploded) 배포 산출물 생성 스크립트
#  - 빌드 후 jar 를 풀어 deploy/app/ 디렉터리(BOOT-INF...)를 만들고 실행 스크립트를 복사한다.
#  - 산출물(deploy/app)을 운영 서버로 복사하여 그대로 실행한다.
#
# 사용법: sh deploy/build-exploded.sh
# 실행:   cd deploy/app && sh run.sh            (또는 운영: sh run.sh --spring.profiles.active=prod)
# =====================================================================
set -e
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

echo "[1/3] 빌드 (mvn package)"
mvn -q -DskipTests clean package

echo "[2/3] jar 압축 해제 -> deploy/app"
rm -rf deploy/app
mkdir -p deploy/app
( cd deploy/app && jar -xf "$ROOT/target/egov-ops.jar" )

echo "[3/3] 실행 스크립트 복사"
cp deploy/run.sh deploy/run.bat deploy/app/
chmod +x deploy/app/run.sh 2>/dev/null || true

echo ""
echo "완료: deploy/app  (BOOT-INF / META-INF / org)"
echo "  - 개발/데모 실행 :  cd deploy/app && sh run.sh"
echo "  - 운영(PostgreSQL):  cd deploy/app && DB_URL=... DB_USERNAME=... DB_PASSWORD=... sh run.sh --spring.profiles.active=prod"
echo "  - 접속: http://localhost:8085"
