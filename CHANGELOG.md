# Changelog

本文件记录对学习者/使用者可感知的变更。格式参考 [Keep a Changelog](https://keepachangelog.com/)。

## Unreleased

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
