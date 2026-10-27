# 部署要点（本地演示）

1. `docker compose up`（MySQL / Redis / MQ 等，以仓库 `docker-compose.yml` 为准）。  
2. 导入秒杀 schema + seed（`mall_flash.sql` / `mall_flash_seed.sql` 或 `docker/mysql-init`）。  
3. 配置 `JWT_SECRET`（强制），再起 mall（context-path `/mall`）。  
4. 冒烟：`QUICKSTART.md` curl；完整下单：`scripts/flash-order-demo.sh`。  
5. 可选：起 `platform-ai`（8088）；默认不调 LLM。

生产硬化（多机、监控、真实支付证书）不在本仓演示范围。
