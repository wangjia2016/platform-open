# 秒杀：难点与常见解法

## 难点（为何和普通下单不同）

- **瞬时流量**：库存读改写并发极高，数据库连接与锁易打满。
- **超卖**：预扣、回滚、异步落单任一环不一致都会卖超或少卖。
- **体验**：倒计时、排队、限流提示要诚实，不能 silently 丢单。
- **资金**：支付回调重复、超时关单与库存回补必须幂等。

## 本仓采用的解法（对照代码）

| 问题 | 做法 | 入口 |
|------|------|------|
| 超卖 | Redis Lua 原子预扣可售库存 | `GoodsStockCache` |
| 削峰 | 预扣成功后 MQ 异步建单 | `MallOrderServiceImpl` + `MallOrderMq` |
| 场次合法 | 下单前校验场次/绑品/时间窗 | `MallFlashSessionsServiceImpl#validateSessionForOrder` |
| 重复提交 | `I-token` 一次性幂等 | `IdempotentInterceptor` |
| 限流 | Sentinel 资源 `createOrder` | `OrderSentinelConfiguration` |
| 支付/关单 | 状态机 + 回调幂等 + 库存回补 | 订单/支付相关 service |

失败分支细表见 [ARCHITECTURE.md §失败路径](../ARCHITECTURE.md#秒杀主链路失败路径)。HTTP 演示见 [FLASH_DEMO_CHECKLIST.md](../FLASH_DEMO_CHECKLIST.md)。
