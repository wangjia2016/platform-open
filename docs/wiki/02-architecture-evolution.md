# 秒杀架构演变与本仓落点

常见演进（教材顺序，非本仓 git 历史）：

1. **单库扣库存** → 易锁表、超卖难控。  
2. **DB 乐观锁 / 扣减行** → 仍扛不住热点行。  
3. **Redis 预扣 + DB 异步** → 本仓主路径（Lua + MQ）。  
4. **多级限流 / 静态化 / CDN** → 本仓有 Sentinel 下单限流；完整 CDN/Gateway 不在本仓。  
5. **可选运营 AI** → `platform-ai` 旁路，**不**进入扣库存与支付。

图示：根目录 [README 架构图](../../README.md) 与 [ARCHITECTURE.md](../ARCHITECTURE.md)。Gateway 决策见 [GATEWAY.md](../GATEWAY.md)。
