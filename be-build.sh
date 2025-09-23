#!/bin/bash

JAR_NAME="/Users/dorosee/Documents/dev_jdh/PlanBoard-BE/build/libs/project_manager_be-0.0.1-SNAPSHOT.jar"  # 전송할 JAR 파일
REMOTE_USER="azureuser"                     # 원격 서버 계정
REMOTE_HOST="dh-lab.koreacentral.cloudapp.azure.com"  # 원격 서버 호스트
REMOTE_DIR="/home/azureuser/server"           # 원격 서버 배포 디렉토리
SSH_KEY="$HOME/.ssh/id_rsa"                # SSH 키 (없으면 -i 옵션 제거)


echo "🌐 원격 서버 접속 및 디렉토리 확인..."
ssh -i "$SSH_KEY" "$REMOTE_USER@$REMOTE_HOST" "mkdir -p $REMOTE_DIR"

echo "🚀 JAR 파일 전송..."
scp -i "$SSH_KEY" "$JAR_NAME" "$REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR/" || { echo "❌ 파일 전송 실패"; exit 1; }

echo "✅ JAR 파일 전송 완료: $REMOTE_HOST:$REMOTE_DIR/$(basename $JAR_NAME)"
