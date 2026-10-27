# Gateway 是否独立仓开源 — 决策

**决策：Gateway 继续独立工程，不并入 `platform-open`。**

## 理由

- 本仓聚焦 **商城/秒杀主链路** 可读可跑；Gateway 另有路由、鉴权插件、限流与运维面，合并会抬高 clone/启动成本。  
- README 架构图已标明 `Gateway（不在本仓）`；学习者用 mall 直连即可演示秒杀。  
- 若开源 Gateway，应 **另仓** 发布，本仓仅文档链接，避免版本与 Issue 混杂。

## 本仓替代

- 本地：浏览器/脚本直打 `http://host:8080/mall/...`。  
- 鉴权：`X-Token` JWT（`AuthAspect`），不依赖网关发 token。
