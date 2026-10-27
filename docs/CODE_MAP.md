# 代码地图（类名 + 一句话）

读代码入口索引，不替代源码。路径相对仓库根。

## 秒杀主链路

| 类 | 一句话 |
|----|--------|
| `MallFlashPortalController` | C 端场次列表/详情/场次商品，免登录 |
| `MallFlashAdminController` | 管理端场次与绑品 |
| `MallFlashSessionsServiceImpl` | 场次查询、绑品、下单前场次校验 |
| `MallOrderPortalController` | 下单入口：鉴权 + 幂等 + Sentinel |
| `MallOrderServiceImpl` | 秒杀参数校验、商品校验、预扣库存、异步落单 |
| `GoodsStockCache` | Redis Lua 原子预扣/回补可售库存 |
| `MallOrderMq` | 秒杀下单 / 取消 / 催付等 MQ 目的地常量 |
| `RedissonLocks` | 统一 `safeUnlock`，避免误解锁 |

## 订单与支付

| 类 | 一句话 |
|----|--------|
| `MallOrderPayPortalController` | 支付相关门户接口 |
| 订单状态机相关常量 / `changOrderState` | 待付 → 已付 / 关单等受控迁移 |
| 支付回调处理（mall-service 内） | 回调幂等，避免重复入账 |

## 鉴权 / 幂等 / 限流

| 类 | 一句话 |
|----|--------|
| `AuthAspect` + `@CheckAuthentication` | Header `X-Token` JWT 校验 |
| `JwtOperator` / `MallBasicPortalController#getTestToken` | 签发与解析 JWT；本地演示发测试 token |
| `IdempotentInterceptor` + `@CheckIdempotent` | Header `I-token` 一次性消费 |
| `OrderSentinelConfiguration` | 下单资源名与 Sentinel 限流 |

## AI（旁路，不进秒杀扣库存）

| 类 | 一句话 |
|----|--------|
| `AiFacadeService` | 四类能力：LLM 或规则降级 |
| `RuleAiService` | 无密钥时的规则兜底 |
| `SessionPlanSanitizer` | 场次策划结果校验（goodsId/库存/下标） |
| `AiProperties` | `AI_ENABLED` + key + `features.*` 按能力开关 |
| `MallAiPortalController` | mall 门面：含 analyze-by-id / session-plan-from-onsale |
| `PlatformAiClient` | Feign 调 `platform-ai` |

## 常见设计点（对照读）

| 点 | 落点 |
|----|------|
| 缓存预扣 + 异步落单 | `preReduceStock` → MQ → 消费者建单 |
| 模板方法式下单校验 | `checkOrderParam` → `checkGoodsValidator` → `validateSessionForOrder` |
| 切面鉴权 / 幂等 | `AuthAspect`、`IdempotentInterceptor` |
| 策略/降级（AI） | `AiFacadeService` 中 LLM 失败回 `RuleAiService` |
| 安全解锁 | `RedissonLocks.safeUnlock` |

补充条目可开 PR 改本文件；大图见 [ARCHITECTURE.md](./ARCHITECTURE.md)。
