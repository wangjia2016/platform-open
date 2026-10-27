# 学习路径

目标：用 **一条可运行的商城秒杀链路**，把微服务常见组件串起来，而不是只堆依赖。

建议总时长：有 Java / Spring 基础约 **1–2 周**（含本地跑通 + 精读主链路）。


秒杀主链路的失败分支（Lua 库存不足、MQ 发送/消费回补、支付回调幂等）见 [ARCHITECTURE.md §失败路径](./ARCHITECTURE.md#秒杀主链路失败路径)。

## 0. 先跑通（半天）

1. 读根目录 [QUICKSTART.md](../QUICKSTART.md)
2. `docker compose up -d`，等 Nacos / MySQL 就绪
3. 启动 `platform-mall-web`，确认 `http://localhost:8080/mall` 可访问
4. 对照 README §13 与 `platform-mall-dao/.../sql/` 理解种子数据

验收：依赖起来、应用起来、能看到秒杀相关接口/管理端入口。

## 1. 看清架构（半天）

- [ARCHITECTURE.md](./ARCHITECTURE.md) 模块图 + 秒杀时序
- README「秒杀架构演变」V1→V3：为什么从 DB 锁 / Redisson 锁走到 **Lua 预扣 + MQ 削峰**

## 2. 精读秒杀主链路（2–3 天）

按调用顺序下断点或加日志：

| 步骤 | 看什么 | 为什么重要 |
| --- | --- | --- |
| 入口 | `MallFlashPortalController` | 请求如何进场次/商品 |
| 库存 | `GoodsStockCache` | 超卖防护的核心 |
| 异步 | RocketMQ 生产/消费落单 | 削峰与最终一致性 |
| 订单 | 订单状态机 + 单测 | 业务正确性 |
| 支付 | 支付回调幂等 | 重复通知不重复记账 |
| 锁 | `RedissonLocks.safeUnlock` | 分布式锁租约过期陷阱 |

## 3. 横向扫组件（3–5 天）

按需深入，不必一次读完：

- Nacos 配置 / 发现（本仓如何接）
- Feign 调用约定
- Redis 幂等
- 统一异常与 JWT（`jwt.secret` 强制配置）
- 线程池种子（`thread_pool_config.sql`）

## 4. 动手题（巩固）

优先从带 `good first issue` 标签的 Issue 选；也可以自选：

1. 给秒杀主链路补一张「失败路径」说明（库存不足 / MQ 失败 / 重复支付）
2. 给 `QUICKSTART` 增加一条「用 curl 验库存预扣」的最小命令
3. 修正文档与代码不一致之处（开 PR，附对比）

## 5. 升级与边界（选修）

Boot / Cloud 大版本升级 **不在** 默认学习路径内（成本高、易踩坑）。若要做，请单独开 Issue 讨论范围，避免 Dependabot 大跨度 PR 直接合入。

## 6. AI 辅助（选修）

1. 读 [ai/README.md](./ai/README.md) 与 [ai/ARCHITECTURE.md](./ai/ARCHITECTURE.md)（为何独立 `platform-ai`）
2. 用 `ai/examples/sample-goods.json` + `ai/skills/goods-analyze.md` 做一次无密钥演练
3. 第二期实现时按架构文 §8 清单，勿把 AI 接到秒杀扣库存路径

## 代码地图

按类名速查见 [CODE_MAP.md](./CODE_MAP.md)；秒杀 HTTP 验收见 [FLASH_DEMO_CHECKLIST.md](./FLASH_DEMO_CHECKLIST.md)。

概念与部署稳定篇：[wiki/README.md](./wiki/README.md)。Boot 升级评估与 Gateway 决策见同目录 `UPGRADE_BOOT.md` / `GATEWAY.md`。
