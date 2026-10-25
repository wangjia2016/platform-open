# Quick start

Minimal path to bring up local infra and run `platform-mall-web` against it.

## 1. Prerequisites

- JDK **8**, Maven 3.6+
- Docker Desktop (or Docker Engine + Compose v2)
- Optional: copy `.env.example` → `.env` and set `MYSQL_PASSWORD` (default `change-me`)

## 2. Start infrastructure

```bash
docker compose up -d
```

This starts:

| Service | Port | Notes |
| --- | --- | --- |
| MySQL 8 | 3306 | DB `mall-open`, root password from `MYSQL_PASSWORD` / `change-me` |
| Redis 6 | 6379 | no password by default (matches empty `REDIS_PASSWORD`) |
| Nacos | 8848 | standalone, auth off |
| RocketMQ NameServer | 9876 | |
| RocketMQ Broker | 10909 / 10911 | |

MySQL first-boot runs scripts under `docker/mysql-init/` (cart/goods/order schema → flash schema → thread pool seed → flash demo seed).

Re-init DB only after wiping the volume:

```bash
docker compose down -v
docker compose up -d
```

## 3. Build and run the app

```bash
export MYSQL_PASSWORD=change-me
# optional: JWT_SECRET, NACOS_ADDR=localhost:8848, ROCKETMQ_NAME_SERVER=localhost:9876

mvn -pl platform-mall/platform-mall-web -am -DskipTests package
java -jar platform-mall/platform-mall-web/target/platform-mall-web-*.jar
# or run the main class from your IDE with env vars / application-local.yml
```

App default: `http://localhost:8080/mall`

## 4. Flash demo data

- Tables: see `platform-mall/.../sql/` and README §13
- Seed session is **relative to import time** (`mall_flash_seed.sql`) and binds `goods_id = 1` by default (schema creates `mall_goods`; insert a real goods row before placing orders)

## 5. Stop

```bash
docker compose down
```

## Troubleshooting

- **RocketMQ from host**: if producers cannot reach the broker, set `brokerIP1` to your host LAN IP in a custom broker conf mounted into the broker container (Docker Desktop networking quirk).
- **Nacos slow on first start**: wait until `curl -f http://127.0.0.1:8848/nacos/` succeeds before starting the app.
- **MySQL auth**: app uses `mysql_native_password` compatible URL; password must match compose `MYSQL_ROOT_PASSWORD`.
- **Gateway / WeChat / Elasticsearch**: not in this repo; see README honesty notes (plan item 10).
