---
name: goods-recommend
description: 在给定候选商品列表上做重排推荐并给理由；无行为数据时规则降级。
repo_hooks:
  - api: GET /mall/api/flash/sessions/goods
  - future_api: POST /ai/v1/goods/recommend
inputs: [anchorGoodsId?, candidates, limit, context?]
outputs: [items, strategy, degraded]
---

# Skill: 商品推荐

## 何时用
详情页「猜你喜欢」、秒杀场次内关联推荐、运营要推荐理由。

## 步骤
1. 确认候选列表来自调用方（不要假装有全站协同过滤模型）
2. 优先：同价带、有库存、已上架、秒杀场景下结合场次
3. 输出 Top-N + `reasons[]`；数据不足时 `degraded=true` 并说明规则
4. JSON 契约见 `prompts/goods-recommend.system.md`
