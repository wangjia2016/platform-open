---
name: flash-session-plan
description: 策划秒杀场次结构与商品绑定建议，强调 Lua 预扣与超卖边界。
repo_hooks:
  - GoodsStockCache
  - MallFlashPortalController
  - future_api: POST /ai/v1/flash/session-plan
inputs: [window, goodsPool]
outputs: [sessions, bindSuggestions, riskNotes]
---

# Skill: 秒杀场次策划

## 步骤
1. 按时间窗切分「抢购中 / 即将开始」等 Tab
2. 为每场建议商品与 flashStock，提醒预扣与售罄文案
3. riskNotes 必须提到：AI 不参与扣库存；以 `GoodsStockCache` 为准
