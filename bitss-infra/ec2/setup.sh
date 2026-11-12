#!/bin/bash
set -euo pipefail

: "${MYSQL_ROOT_PASSWORD:?Define MYSQL_ROOT_PASSWORD}"
: "${BITSS_JWT_SECRET:?Define BITSS_JWT_SECRET}"

dnf install -y docker
systemctl enable --now docker

docker network create bitss-net || true

mkdir -p /opt/bitss
cat > /opt/bitss/.env <<EOF
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=${MYSQL_ROOT_PASSWORD}
BITSS_JWT_SECRET=${BITSS_JWT_SECRET}
USUARIO_SERVICE_URL=http://usuario:8081
CATALOGO_SERVICE_URL=http://catalogo:8082
CARRO_SERVICE_URL=http://carro:8083
GESTION_SERVICE_URL=http://gestion:8084
PAGOS_SERVICE_URL=http://pagos:8085
COLECCION_SERVICE_URL=http://coleccion:8086
EOF
chmod 600 /opt/bitss/.env

docker volume create bitss-mysql-data
docker rm -f mysql || true
docker run -d \
  --name mysql \
  --restart unless-stopped \
  --memory 768m \
  --network bitss-net \
  -e MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD}" \
  -v bitss-mysql-data:/var/lib/mysql \
  mysql:8.4
