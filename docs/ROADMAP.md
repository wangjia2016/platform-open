# Roadmap

公开路线图，方便学习者与贡献者知道「仓库在往哪走」。完成项会迁到 [CHANGELOG.md](../CHANGELOG.md)。

## Now（近期）

- [x] OSS 卫生：密钥占位、CI、CONTRIBUTING / SECURITY、docker-compose + QUICKSTART
- [x] 秒杀主链路加固：Lua 库存、支付回调幂等、Redisson 安全解锁、订单状态机单测
- [x] C 端示意截图（`docs/screenshots`）与诚实 README
- [ ] 学习向文档：架构图（本地 Mermaid）、学习路径、模块地图（本迭代）
- [x] 社区信号：good-first-issue、清理无效 Dependabot；GitHub topics/About 描述需在网页或 `gh` 侧再改（本机无 gh）
- [x] Dependabot：忽略 Boot / Cloud major 与不存在的 Alibaba 小版本

## Next

- [ ] 最小可演示的「秒杀下单」HTTP 脚本 / 验收清单（不引入完整前端工程）
- [ ] 设计模式与业务点的「代码地图」索引（类名 + 一句话）
- [ ] Wiki 或 docs 内迁移原「WIKI」承诺中的稳定篇章
- [ ] Dependabot 策略收紧：忽略 Boot 大跨度主版本

## Later

- [ ] Boot 2.7 / 3.x 升级评估（单独里程碑，不做顺手升级）
- [ ] Gateway 示例是否独立仓开源
- [ ] 更完整的优惠券 / 权限示例文档化

反馈请开 Issue；安全问题走 [SECURITY.md](../SECURITY.md)。
