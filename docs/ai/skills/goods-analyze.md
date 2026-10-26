---
name: goods-analyze
description: 对单品或批量商品做卖点/风险/文案/定价备注分析。
repo_hooks:
  - entity: com.platform.mall.dao.goods.entity.MallGoods
  - future_api: POST /ai/v1/goods/analyze
inputs: [goods | goods[]]
outputs: [summary, sellingPoints, risks, copyDrafts, pricingNotes]
---

# Skill: 商品分析

## 步骤
1. 读取 price/stock/stockWarn/onUnSale/auditStatus 等
2. 库存 ≤ stockWarn 时写入 risks
3. 给出可发表的 title/subtitle 草稿，不虚构评价数据
4. 契约见 `prompts/goods-analyze.system.md`
