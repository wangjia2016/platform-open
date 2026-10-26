# Quick start

跑通后建议接着看 [docs/LEARNING_PATH.md](./docs/LEARNING_PATH.md) 与 [docs/ARCHITECTURE.md](./docs/ARCHITECTURE.md)。

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

## 5. Optional: platform-ai

```bash
# terminal A — AI service (port 8088). No API key => rule fallback (degraded=true)
mvn -pl platform-ai -am -DskipTests package
java -jar platform-ai/target/platform-ai-*.jar

# smoke (direct)
curl -sS http://127.0.0.1:8088/ai/v1/health
curl -sS -X POST http://127.0.0.1:8088/ai/v1/goods/search-plan \
  -H 'Content-Type: application/json' \
  -d '{"query":"300元内降噪耳机"}'

# via mall facade (app must be up): /mall/api/ai/v1/...
curl -sS http://127.0.0.1:8080/mall/api/ai/v1/health

# mall: analyze / recommend by real goodsId (read-only DB → snapshot → AI)
curl -sS "http://127.0.0.1:8080/mall/api/ai/v1/goods/analyze-by-id?goodsId=1"
curl -sS "http://127.0.0.1:8080/mall/api/ai/v1/goods/recommend-by-anchor?goodsId=1&limit=5"

# session-plan (LLM when AI_ENABLED+key; else rules). Direct:
curl -sS -X POST http://127.0.0.1:8088/ai/v1/flash/session-plan \
  -H 'Content-Type: application/json' \
  -d '{"window":{"start":"10:00","mid":"16:00","end":"22:00"},"goodsPool":[{"id":1,"name":"demo","stock":100,"onUnSale":0}]}'
```

Set `AI_ENABLED=true` and `AI_API_KEY` only when you want real OpenAI-compatible calls. Flash order / pay paths never call AI.

## 6. Stop

```bash
docker compose down
```


## 7. Smoke check (curl)

App context-path is `/mall`. Flash portal APIs **do not require login** (see `MallFlashPortalController`).

```bash
# 场次列表（种子导入后应返回业务 JSON；空列表也算通）
curl -sS "http://127.0.0.1:8080/mall/api/flash/sessions/list" | head -c 400; echo

# 场次详情 / 场次商品（把 1 换成实际上有的 sessionsId）
curl -sS "http://127.0.0.1:8080/mall/api/flash/sessions/detail?sessionsId=1" | head -c 400; echo
curl -sS "http://127.0.0.1:8080/mall/api/flash/sessions/goods?sessionsId=1" | head -c 400; echo

# 可选：测试 JWT（需已配置 JWT_SECRET）
curl -sS "http://127.0.0.1:8080/mall/api/basic/getTestToken" | head -c 400; echo
```

期望：HTTP 非 404；JSON 里常见 `code` / `data` 字段（具体以 `Result` 为准）。连不上先确认应用已起、Nacos/MySQL 就绪。

## Troubleshooting

- **RocketMQ from host**: if producers cannot reach the broker, set `brokerIP1` to your host LAN IP in a custom broker conf mounted into the broker container (Docker Desktop networking quirk).
- **Nacos slow on first start**: wait until `curl -f http://127.0.0.1:8848/nacos/` succeeds before starting the app.
- **MySQL auth**: app uses `mysql_native_password` compatible URL; password must match compose `MYSQL_ROOT_PASSWORD`.
- **Gateway / WeChat / Elasticsearch**: not in this repo; see README honesty notes (plan item 10).
