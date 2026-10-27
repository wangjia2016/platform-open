# 技术栈与版本钉扎

| 层 | 选择 | 说明 |
|----|------|------|
| 运行时 | Java 8 + Spring Boot **2.3.4** | 与生产同源代际，不追逐最新 |
| 微服务 | Spring Cloud **Hoxton.SR12** + Alibaba **2.2.9** | 与 Boot 2.3 对齐 |
| 库存 | Redis + Lua（`GoodsStockCache`） | 秒杀预扣 |
| 消息 | RocketMQ | 异步落单 / 延时关单 |
| 锁 | Redisson | 统一 `safeUnlock` |
| 限流 | Sentinel | 下单资源 |
| 可选 AI | `platform-ai` OpenAI 兼容 HTTP | 默认规则降级 |

升级评估（不在本仓顺手升级）：[UPGRADE_BOOT.md](../UPGRADE_BOOT.md)。Dependabot 已忽略 Boot/Cloud **major**，见 `.github/dependabot.yml`。
