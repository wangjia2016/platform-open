---
name: goods-search
description: 把用户自然语言商品需求整理成检索计划（过滤/排序/伪 SQL），对齐 MallGoods 字段。
repo_hooks:
  - entity: com.platform.mall.dao.goods.entity.MallGoods
  - future_api: POST /ai/v1/goods/search-plan
inputs: [query, hints?]
outputs: [intent, filters, sort, keywords, pseudoSql, notes]
---

# Skill: 商品搜索计划

## 何时用
用户说「帮我找…」「搜索意图整理」且目标是本仓商品域。

## 步骤
1. 解析价带、品牌、类目、是否仅上架（`onUnSale=0`）等
2. 映射到 `MallGoods` 字段，未知字段放入 `notes`，勿发明列名
3. 按 `prompts/goods-search.system.md` 产出 JSON
4. 注明：本仓默认无 ES，计划供 MyBatis 或第二期检索实现使用
