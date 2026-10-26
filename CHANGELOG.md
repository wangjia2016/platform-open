# Changelog

本文件记录对学习者/使用者可感知的变更。格式参考 [Keep a Changelog](https://keepachangelog.com/)。

## Unreleased

- AI：`session-plan` 增加 prompt + LLM 路径（失败仍规则降级）；mall 门面新增 `GET /goods/analyze-by-id`、`GET /goods/recommend-by-anchor`（只读组装快照）
- 文档：秒杀失败路径（#9）、README 30 秒 curl 演示与对比表、AI examples 样例 JSON
- 文档：弱化「学习脚手架」表述，README/About 改为生产实践开源整理定位
- 文档：重绘 README 项目架构图（`docs/screenshots/architecture.svg` + Mermaid），补上 platform-ai
- AI：第二期落地 `platform-ai`（规则降级 + 可选 OpenAI 兼容）与 mall 门面 `/mall/api/ai/v1/*`；第一期 docs/ai 资产保留
- 文档：学习路径、架构模块图（Mermaid）、公开 Roadmap；README 改为脚手架/学习定位
- 文档：QUICKSTART 增加 curl 冒烟验收；Dependabot 忽略 Boot/Cloud major

## 2026-10-26

- 文档：补充 C 端功能示意截图（`docs/screenshots`），移除已失效 CDN 外链截图
- 依赖：Spring Cloud → Hoxton.SR12，Spring Cloud Alibaba → 2.2.9.RELEASE
- 修复：`printStackTrace` 改为 SLF4J；强制配置 `jwt.secret`

## 2026-10-25

- 安全/正确性：Redisson `safeUnlock`；支付回调幂等与 MQ destination；恢复订单状态机单测
- 体验：docker-compose + QUICKSTART；秒杀/线程池 SQL 种子
- 社区：CONTRIBUTING、SECURITY、Issue 模板、CI badge 与诚实 README
