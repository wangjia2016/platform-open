# 优惠券与权限 — 代码里实际有什么

诚实范围：本仓 **不是** 完整优惠券中台 / RBAC 产品；下列为现有落点，便于二次开发对照。

## 优惠券（字段级，非独立券服务）

| 落点 | 说明 |
|------|------|
| `MallOrder.couponAmount` / `couponAttr` | 订单上的券金额与属性串 |
| 商品虚拟类型 `coupon` | `MallGoodsDetailDto` 虚拟物类型枚举含义之一 |
| 微信支付通知 | `coupon_fee` / `coupon_count` 等回调字段 |

**没有** 独立的领券、核销状态机、券模板 CRUD 模块。若要做完整券，建议新模块 + 下单计价扩展点，且 **秒杀预扣路径保持简单**（券逻辑勿塞进 Lua）。

## 权限 / 鉴权

| 落点 | 说明 |
|------|------|
| `@CheckAuthentication` + `AuthAspect` | 校验 Header `X-Token` JWT |
| `JwtOperator` | 签发/解析；演示用 `GET /api/basic/getTestToken` |
| `CurrentMember` | 从上下文取会员 id |
| Portal vs Admin Controller | 包路径分离（`portal` / `admin`），**非** 细粒度 RBAC |
| `@CheckIdempotent` | 防重复提交，不是角色权限 |

管理端接口默认仍走应用内鉴权约定；细粒度菜单/角色表 **未** 作为完整示例开源。扩展时建议独立权限模块，避免与秒杀热路径耦合。
