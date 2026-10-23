本项目持续完善中... 具体技术详解与业务设计，参见 WIKI。

## 1.项目说明
- 本项目基于 Spring Boot 2.3.4、Spring Cloud Hoxton.SR8、Spring Cloud Alibaba 2.2.3.RELEASE（版本以根目录 `pom.xml` 为准，并非跟踪各组件最新发行版）
- 关于 Spring Cloud、Spring Cloud Alibaba、Spring Boot 三者的版本依赖关系，参见 Spring Cloud 官方文档
- 本项目可作为微服务学习/脚手架参考，整合了 Nacos、RocketMQ、Sentinel、Feign、Redis、Redisson、JJWT、MyBatis 等常见组件
- 网关采用 Spring Cloud Gateway，为另外一个单独的项目（不在本仓库）
- 以商城秒杀下单为例：使用 Redisson 分布式锁控制库存扣减，RocketMQ 做异步、削峰，以及事务消息等
- 基于 Redis 的幂等性方案
- 全局统一异常处理
- 业务：秒杀、优惠券、微信支付、商品配置、订单
- 设计模式：工厂模式、策略模式、模板方法、状态机模式等

### 主要版本（摘自根 `pom.xml` / 子模块）

| 组件 | 版本 |
| --- | --- |
| Spring Boot | 2.3.4.RELEASE |
| Spring Cloud | Hoxton.SR8 |
| Spring Cloud Alibaba | 2.2.3.RELEASE |
| Java | 1.8 |
| MyBatis-Plus | 3.4.0 |
| rocketmq-spring-boot-starter | 2.0.3 |
| Redisson | 3.8.2 |
| JJWT | 0.10.7 |
| Guava | 30.0-jre |
| springfox (Swagger) | 3.0.0 |
| MapStruct | 1.3.1.Final |
| Lombok | 1.18.8 |
| Jedis | 3.3.0 |

说明：README 技术栈中若仍写「RocketMQ 4.7.1」等，指常见 Broker/运维侧版本；本仓 Maven 依赖以表内为准。

## 2.秒杀介绍
- 秒杀的难点
  - 瞬时流量
  - 防止库存超卖
  - 吞吐量
- 流量预估
  - 如何预估秒杀峰值？
  - 每个订单对象大小估算？
  - 带宽估算？
- 优化
  - 秒杀下 JVM 如何优化
  - 如何快速定位线上问题
  - 如何压测？
  
## 3.秒杀架构演变
- V1.0 单机环境下早期采用数据库的悲观锁 for update、乐观锁等进行控制 or ReentrantLock synchronized
- V2.0 分布式环境下 采用分布式锁进行控制库存扣减
- V3.0 微服务高并发环境下，采用 redis+mq 多级缓存、异步、削峰

## 4.秒杀解决方案  
- 业务尽可能简单，能取缓存就从缓存取（可以是多级缓存），减轻 mysql 的压力
- 采用 mq 异步，削峰，控制入库的数量
- 流量拦截，尽可能将请求拦截在上游，比如参数校验，黑名单校验等业务校验
- 前端
  - 秒杀倒计时（甚至有部分电商，各用户看到的倒计时都不一样）
  - 页面静态化
  - 防止重复点击
  - 隐藏 url
- 网关拦截
  - 校验 token 
  - 接口防刷 （防黄牛 - 风控机制）
  - 幂等性校验

## 5. 项目架构图
![系统架构](https://cdn.fanguwan.com/github/%E5%BE%AE%E6%9C%8D%E5%8A%A1%E6%9E%B6%E6%9E%84%E5%9B%BE-%E7%A7%92%E6%9D%80.png "架构图")

## 6. 技术栈
- Spring Cloud Hoxton.SR8
- Spring Cloud Alibaba 2.2.3.RELEASE
- Nacos
- OpenFeign
- Ribbon
- Redis
- Redisson
- RocketMQ（spring-boot-starter 2.0.3）
- Sentinel
- ElasticSearch（规划/周边能力，本仓未必完整包含）
- Spring Cloud Gateway（独立仓库）
- JWT
- MyBatis
- MyBatis-Plus 3.4.0
- Jackson
- HikariCP
- Kaptcha
- Swagger (springfox 3.0.0)

## 7. 工程结构
  基于 Maven 的多 module 模块，如果要加入其它微服务模块，直接添加即可

## 8. 秒杀业务时序图
   参数校验 --> redis 库存预扣减 --> 发送下单消息到 MQ --> 提示订单处理中
   订阅者 --> 拉取下单消息 --> 真实库存扣减…&下单入库

## 9. 业务功能模块
- 秒杀业务
  - 秒杀场次列表（门户 `GET /api/flash/sessions/list`）
  - 秒杀活动，一个活动包含秒杀起止时间、活动名称等（表 `mall_flash_promotion`）
  - 秒杀场次，一个场次包含多个秒杀商品（门户 `GET /api/flash/sessions/goods`；管理端绑定 `POST /admin/flash/sessions/bindGoods`）
  - 秒杀商品，一个秒杀商品可以被多个秒杀场次关联（表 `mall_flash_sessions_goods`）
- 支付
- 通知
  - 待支付提醒
  - 订单取消提醒
  - 支付提醒
  - 发货提醒
- 支付回调
- 订单状态机 - 状态模式实现

## 10.系统流程

## 11.开发者规约
- 参考 阿里巴巴 Java 开发者规范《Java 开发手册》嵩山版

## 12.压测结果
    使用 JMeter 进行压测

## 13.部署说明
  先导入数据库脚本
  敏感配置已改为环境变量占位（见根目录 `.env.example`），请勿把真实密码提交进仓库
  - 常用变量：`MYSQL_PASSWORD`、`REDIS_PASSWORD`、`JWT_SECRET`（以及可选的 `MYSQL_URL`、`NACOS_ADDR`、`ROCKETMQ_NAME_SERVER`）
  - 本地也可复制 `application-local.yml.example` → `application-local.yml`（已 gitignore），用 `--spring.profiles.include=common,local` 覆盖
  - 参考：`platform-mall/.../application-example.yml`
  启动相应的组件（MySQL / Redis / Nacos / RocketMQ）

## 14.许可协议
本项目采用 [Apache License 2.0](./LICENSE) 开源协议。

Copyright 2020-2026 wangjia2016

你可以自由地使用、修改和分发本项目代码，但须保留版权与许可声明；详见仓库根目录 `LICENSE`。

## 15.功能截图

- 前端效果
![前端效果](https://cdn.fanguwan.com/github/%E7%A7%92%E6%9D%80%E5%89%8D%E7%AB%AF.png "前端效果-秒杀商品列表")
![前端效果](https://cdn.fanguwan.com/github/%E7%A7%92%E6%9D%80%E5%89%8D%E7%AB%AF2.png "秒杀详情页")
