---
name: order-risk-review
description: 只读复盘订单状态机与支付回调幂等，辅助排障与文档。
repo_hooks:
  - order state machine tests
  - pay notify idempotency
inputs: [symptom, orderSnapshot?]
outputs: [likelyCauses, checkList, docLinks]
---

# Skill: 订单风险 / 幂等复盘

## 步骤
1. 对照订单状态机与支付回调幂等实现（以代码与单测为准）
2. 给出检查清单，不编造已支付/已发货
3. 链接 `docs/ARCHITECTURE.md` 与相关测试类名
