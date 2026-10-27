# Roadmap

公开路线图，方便学习者与贡献者知道「仓库在往哪走」。完成项会迁到 [CHANGELOG.md](../CHANGELOG.md)。

## Now（近期）

- [x] OSS 卫生：密钥占位、CI、CONTRIBUTING / SECURITY、docker-compose + QUICKSTART
- [x] 秒杀主链路加固：Lua 库存、支付回调幂等、Redisson 安全解锁、订单状态机单测
- [x] C 端示意截图（`docs/screenshots`）与诚实 README
- [x] 学习向文档：架构图（本地 Mermaid）、学习路径、模块地图（本迭代）
- [x] 社区信号：good-first-issue、清理无效 Dependabot；GitHub topics/About 描述需在网页或 `gh` 侧再改（本机无 gh）
- [x] Dependabot：忽略 Boot / Cloud major 与不存在的 Alibaba 小版本

## Next

- [x] 最小可演示的「秒杀下单」HTTP 脚本 / 验收清单（不引入完整前端工程）
- [x] 设计模式与业务点的「代码地图」索引（类名 + 一句话）
- [x] Wiki 或 docs 内迁移原「WIKI」承诺中的稳定篇章（见 [docs/wiki](./wiki/README.md)）
- [x] Dependabot 策略收紧：忽略 Boot / Cloud / Spring / Alibaba **major**（`.github/dependabot.yml`）

## Later

- [x] Boot 2.7 / 3.x 升级评估（[UPGRADE_BOOT.md](./UPGRADE_BOOT.md)；不做顺手升级）
- [x] Gateway 示例是否独立仓开源（决策：[GATEWAY.md](./GATEWAY.md) — 继续另仓）
- [x] 更完整的优惠券 / 权限示例文档化（诚实范围：[COUPON_AND_AUTH.md](./COUPON_AND_AUTH.md)）


## AI（商城）

- [x] 第一期：`docs/ai` 架构定稿（独立 `platform-ai` + Feign）+ Agents/Skills/prompts
- [x] 第二期：按 [ai/ARCHITECTURE.md](./ai/ARCHITECTURE.md) §8 **全部落地**（search-plan / recommend / analyze / session-plan、降级、mall 门面、配置与 curl）
- [x] AI 深化：`session-plan` LLM 路径；mall `analyze-by-id` / `recommend-by-anchor`（只读）
- [x] 更晚方案文档化：ES/向量、行为推荐、AI 写回边界（[ai/PHASE3.md](./ai/PHASE3.md)；实现另立里程碑）

反馈请开 Issue；安全问题走 [SECURITY.md](../SECURITY.md)。
