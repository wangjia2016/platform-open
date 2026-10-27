# 秒杀下单演示验收清单

不引入完整前端：用 HTTP 把「场次 → 商品 → 鉴权 → 幂等 → 预扣下单」跑通。脚本：[`scripts/flash-order-demo.sh`](../scripts/flash-order-demo.sh)。

## 前置

- [ ] `docker compose`（或等价）已起 MySQL / Redis / RocketMQ（按 [QUICKSTART.md](../QUICKSTART.md)）
- [ ] mall 应用已起，context-path `/mall`，默认 `http://127.0.0.1:8080/mall`
- [ ] 已执行 `mall_flash.sql` + `mall_flash_seed.sql`（或 docker `mysql-init`）
- [ ] `mall_goods` 至少有 `id=1`（种子默认绑 goods_id=1）
- [ ] `JWT_SECRET` 已配置（与签发测试 token 一致）

## 冒烟步骤（脚本自动做）

| # | 步骤 | 接口 | 期望 |
|---|------|------|------|
| 1 | 场次列表 | `GET /api/flash/sessions/list` | `code=1`，至少 1 条启用场次 |
| 2 | 场次商品 | `GET /api/flash/sessions/goods?sessionsId=` | `code=1`，含 `goodsId` |
| 3 | 测试 JWT | `GET /api/basic/getTestToken` | `data` 为 JWT 字符串 |
| 4 | 幂等 token | `GET /api/order/getIdempotent` + Header `X-Token` | `data` 为幂等串 |
| 5 | 秒杀下单 | `POST /api/order/createOrder` + `X-Token` + `I-token` | `code=1`，提示处理中（异步落单） |

成功仅表示 **Lua 预扣 + MQ 投递** 被接受；订单行由消费者异步写入，可用订单查询或库表再验。

## 手工运行

```bash
# 默认 BASE_URL
./scripts/flash-order-demo.sh

# 指定场次/商品
SESSIONS_ID=900002 GOODS_ID=1 BASE_URL=http://127.0.0.1:8080/mall ./scripts/flash-order-demo.sh
```

## 常见失败对照

| 现象 | 优先核对 |
|------|----------|
| sessions 空 | 种子 SQL、场次 `is_enable`、时间窗是否覆盖当前 |
| getTestToken / 鉴权失败 | `JWT_SECRET`、时钟 |
| I-token 报重复提交 | 每次下单重新 `getIdempotent`；Header 名是 `I-token` |
| 库存不足 | Redis 预热库存、`GoodsStockCache`、商品上架 |
| 场次校验失败 | `validateSessionForOrder`：场次启用、商品已绑定、在时间窗内 |
| 下单成功但无订单 | RocketMQ 消费组、`create-flash-mall-order` topic |

更细失败分支见 [ARCHITECTURE.md §秒杀主链路失败路径](./ARCHITECTURE.md#秒杀主链路失败路径)。
