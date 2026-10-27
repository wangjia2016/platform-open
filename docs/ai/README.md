# AI 辅助（商城场景）

本目录是 **platform-open 商城业务的 AI 资产与设计**：Agent 角色、Skills、提示词，以及第二期要落地的技术/架构方案。

## 诚实现状

| 阶段 | 状态 |
| --- | --- |
| **第一期** | Agent / Skills / 提示词文档资产 |
| **第二期（已落地）** | 独立模块 `platform-ai` + mall Feign；默认规则降级，配置 `AI_ENABLED`+`AI_API_KEY` 后走 OpenAI 兼容接口 |

不要把本目录理解成「已经上线智能推荐引擎」。


## 怎么用（第二期）

见根目录 [QUICKSTART.md](../../QUICKSTART.md) §5：起 `platform-ai`（8088），再调 `/ai/v1/*` 或经 mall `/mall/api/ai/v1/*`。默认无密钥走规则降级。

## 怎么用（第一期）

1. 读 [ARCHITECTURE.md](./ARCHITECTURE.md) 了解为何做成独立服务、模块边界与第二期接口契约  
2. 按场景打开 `skills/` 或 `prompts/`，在 Cursor / 其它 Agent 中加载  
3. 用 `examples/` 下样例演练：`sample-goods.json`、`search-plan-sample.json`、`recommend-request-response.json`、`analyze-request-response.json`  
4. 角色约束见 `agents/`

## 目录

```text
docs/ai/
  README.md
  ARCHITECTURE.md          # 技术设计、架构、独立服务决策、第二期接口
  agents/                  # 运营 / 研发 Agent
  skills/                  # 可复用 Skill
  prompts/                 # system/user 提示词模板
  examples/                # 样例商品 JSON
```

## 与本仓业务的对齐

- 商品字段：`MallGoods`（`name/title/price/stock/stockWarn/onUnSale/...`）  
- 秒杀：`MallFlashPortalController`（`/mall/api/flash/...`）、`GoodsStockCache`  
- 订单/支付：状态机与支付回调幂等（分析/风控 Skill 只读复盘，第一期不写库）

学习路径选修入口见 [../LEARNING_PATH.md](../LEARNING_PATH.md)。

## Phase 3（更晚）

方案与硬边界见 [PHASE3.md](./PHASE3.md)（ES/行为/写回；默认不进支付与库存主链路）。
