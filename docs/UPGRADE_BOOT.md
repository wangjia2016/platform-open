# Boot 2.7 / 3.x 升级评估（不做顺手升级）

**结论（当前）：保持 Boot 2.3.4 + Hoxton.SR12 + Alibaba 2.2.9。** 升级单独立项，不在日常 PR 混入。

## 动机 vs 成本

| 目标版本 | 收益 | 主要成本 |
|----------|------|----------|
| 2.7.x | 尚在 2.x，javax 仍可用；依赖漏洞修复面更大 | Cloud/Alibaba BOM 重选；部分 API 弃用清理；回归秒杀/支付 |
| 3.x | 长期支持、Jakarta、可跟新生态 | **javax→jakarta** 全仓；Spring Cloud 202x + 新 Alibaba；Sentinel/RocketMQ/MyBatis-Plus 版本矩阵；Java 17+ |

## 建议里程碑（若启动）

1. 分支 `spike/boot-2.7`：只升 BOM + 编译，不改业务行为。  
2. 全量单测 + `flash-order-demo.sh` + 支付回调幂等用例。  
3. 再评估 3.x；未过 2.7 回归前不开 3.x PR。  

## 明确不做

- 在 master 上「顺手」升 parent。  
- Dependabot 自动开 Boot/Cloud **major** PR（已 ignore）。
