# 模块与阅读顺序

```
platform-open
├── platform-common     # Result、JWT、幂等、鉴权切面
├── platform-mall-*     # 商城/秒杀/订单（dao → service → web）
├── platform-ai         # 可选运营 AI
├── docs/               # 架构、Wiki、AI、验收
└── scripts/            # HTTP 演示脚本
```

建议顺序：`LEARNING_PATH.md` → `CODE_MAP.md` → 跟一次 `flash-order-demo.sh` → 读 `GoodsStockCache` 与 `MallOrderServiceImpl#preCreateOrder`。
