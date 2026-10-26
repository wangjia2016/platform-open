# 秒杀商城 + AI 运营：Lua 防超卖、MQ 削峰，AI 负责选品和策划

> 仓库：[platform-open](https://github.com/wangjia2016/platform-open)  
> 技术栈：Spring Boot 2.3.4 / Spring Cloud Hoxton.SR12 / Spring Cloud Alibaba 2.2.9  
> 关键词：秒杀、Redis Lua、RocketMQ、Nacos、Feign、支付幂等、订单状态机、OpenAI 兼容 AI

## 写在前面

做微服务 demo 不难，难的是：**主链路说得清、失败路径说得清、本地真能跑起来**。

`platform-open` 把我在商城/秒杀场景里用过的一套实现整理开源了出来：库存用 Redis Lua 原子预扣，下单走 RocketMQ 异步削峰，支付回调做幂等，订单用状态机推进。最近把商城侧的 AI 也补上了（独立模块 `platform-ai`）：搜索计划、推荐重排、商品分析、场次策划都能走通；没配密钥时用规则兜底，配了 OpenAI 兼容密钥就调模型。库存和支付这条热路径仍然不经过 AI。

版本钉在 Boot 2.3 / Hoxton，不为追新。Gateway 在独立仓，本仓聚焦 mall + 公共能力 + 可选 AI。

---

## 1. 整体长什么样

本地用 `docker compose` 拉起 MySQL / Redis / Nacos / RocketMQ，再启 `platform-mall-web`。可选再启 `platform-ai`（默认 8088）。

```text
Client
  └─ platform-mall-web
        ├─ 秒杀主链路：GoodsStockCache(Lua) → RocketMQ → MySQL/状态机
        ├─ 领域：service / manager / dao
        └─ Feign ─(可选)─→ platform-ai  (/mall/api/ai/v1/*)
基础设施：Nacos · Redis/Redisson · RocketMQ · MySQL
```

更完整的图见仓库 README 里的 `docs/screenshots/architecture.svg`。

---

## 2. 秒杀为什么是「Lua 预扣 + MQ」

秒杀的核心矛盾就三句话：

1. **瞬时写压力**不能全砸在 MySQL 上  
2. **库存绝不能超卖**  
3. **下单落库可以异步**，但预扣必须快且原子

当前实现（V3）的取舍是：

| 方案 | 做法 | 取舍 |
| --- | --- | --- |
| V1 单机锁 / DB 锁 | 悲观锁、乐观锁、`synchronized` | 简单，扛不住分布式峰值 |
| V2 分布式锁扣库存 | Redisson 锁住再改库存 | 租约过期后误 unlock 有坑，本仓已弃用该路径 |
| **V3（当前）** | **Redis Lua 原子预扣可售库存 + RocketMQ 异步落单** | 热点写留在 Redis；DB 只吃削峰后的消息 |

关键点在 `GoodsStockCache`：预扣成功才发 MQ；库存不足立刻失败返回。Redisson 仍保留作锁基建，解锁请走 `RedissonLocks.safeUnlock`，避免租约过期后的误操作。

支付侧强调**回调幂等**；订单侧用状态机推进（仓库里有相关单测可对照）。这些不是概念图，是可以顺着代码读的落地。

---

## 3. AI 怎么接，又不污染主链路

商城里 AI 最容易犯的错，是一上来就绑进下单/扣库存。我们的结论是：

> **AI 做成仓内独立服务 `platform-ai`，mall 用 Feign 调；秒杀库存与支付路径永远不经过 AI。**

能力上目前覆盖四类辅助接口：

1. **商品搜索计划**：自然语言 → 结构化过滤/排序意图（本仓暂无完整 ES，先出「查询计划」）  
2. **商品推荐**：锚点 + 候选集 → Top-N + 理由  
3. **商品分析**：卖点、定价/库存风险、文案草稿  
4. **秒杀场次策划**：活动窗口 + 商品池 → 场次建议  

运行策略也很务实：

- 默认 `AI_ENABLED=false`，**规则引擎降级**（无密钥也能演示）  
- 配置 `AI_ENABLED` + `AI_API_KEY` 后，走 OpenAI 兼容 `/chat/completions`  
- **没有引入 Spring AI / LangChain**，先用 `RestTemplate` 把边界跑通，依赖面可控  

mall 侧门面：`/mall/api/ai/v1/*`；AI 服务挂了返回明确不可用，不影响秒杀。

---

## 4. 本地怎么 5 分钟摸到毛

```bash
git clone https://github.com/wangjia2016/platform-open.git
cd platform-open
docker compose up -d
# 再按 QUICKSTART 启动 platform-mall-web

# 秒杀冒烟（context-path=/mall）
curl -sS "http://127.0.0.1:8080/mall/api/flash/sessions/list"

# 可选：起 AI（无密钥=规则降级）
mvn -pl platform-ai -am -DskipTests package
java -jar platform-ai/target/platform-ai-*.jar
curl -sS http://127.0.0.1:8088/ai/v1/health
curl -sS -X POST http://127.0.0.1:8088/ai/v1/goods/search-plan \
  -H 'Content-Type: application/json' \
  -d '{"query":"300元内降噪耳机"}'
```

更细的环境变量与排错见仓库根目录 `QUICKSTART.md`。

---

## 5. 诚实边界（避免期望错位）

适合：

- 对照真实秒杀链路做研读或二次开发  
- 看清 Lua 库存、MQ 削峰、支付幂等、状态机怎么落代码  
- 体验「可选 AI + 强制降级」的服务边界  

本仓刻意不做 / 不在本仓：

- 完整 C 端 App、独立 Gateway 工程  
- 多机生产部署与一键压测环境  
- Boot 3 / 最新 Alibaba 版本追随  
- AI 自动写库改价、完整行为推荐中台（Roadmap 更晚）

---

## 6. 写给想贡献 / 想 star 的人

如果你在看秒杀或微服务落地，欢迎直接跑一遍 QUICKSTART，给个 Star，或开 Issue 聊失败路径、AI 场景。仓库里有 `good-first-issue`（例如失败路径文档）可以认领。

- 仓库：https://github.com/wangjia2016/platform-open  
- 架构：`docs/ARCHITECTURE.md`  
- AI 设计：`docs/ai/ARCHITECTURE.md`  

评论区可以聊聊：你们线上秒杀是锁库存、Lua，还是别的方案？AI 有没有敢挂进交易链路？

---

*文中实现以仓库当前 `master` 为准；若你 clone 后与文中略有出入，以代码和 Changelog 为准。*
