本项目持续完善中... 具体技术 详解&业务设计，参见WIKI
## 1.项目说明
- 本项目基于SpringCloud Hoxton.SR8 &SpringCloud Alibaba 2.2.3.RELEASE 最新版;
- 关于SpirngCloud、SpringCloudAlibaba、SpringBoot 三者的版本依赖关系 参见SpringCloud官方文档
- 本项目可以作为微服务架构，整合了nacos、rocketmq、sentinal、feign、redis、redisson、jjwt、mybatis等常见微服务组件
- 网关采用Spring Cloud Gateway 是另外一个单独的项目 
- 本项目以商城秒杀下单为例，使用Redission分布式锁控制库存扣减，RocketMq进行异步、削峰处理
- 全局统一异常处理
- 

## 2.秒杀介绍
- 秒杀的特点、难点如下
  瞬时流量
  防止库存超卖
  
## 3.秒杀架构演变
- 单机环境下早期采用数据库的悲观锁 for update进行控制
- 分布式环境下 采用分布式锁进行控制库存扣减
- 微服务高并发环境下，采用redis+mq 缓存、异步、削峰

## 4.秒杀解决方案  
- 业务尽可能简单，能取缓存就从缓存取，减轻mysql的压力
- 采用mq 异步处理，并且控制入库的数量
- 流量拦截，尽可能将请求拦截在上游，比如参数校验，
- 前端拦截
  开始之前灰选
  页面静态化
  防止重复点击
  隐藏url
- 网关拦截
  校验token 
  接口防刷
  幂等性校验

## 5. 项目架构图
![系统架构](https://xxx.png "在这里输入图片标题")

## 6. 技术栈
- SpringCloud Hoxton.SR8
- SpringCloud Alibaba 2.2.3.RELEASE
- nacos
- open feign
- Ribbon
- Redis
- Redisson
- RocketMq 4.7.1
- Sentital
- ElasticSearch
- Spring Cloud Gateway
- JWT
- Mybatis
- mybatis-plus 3.4.0
- jackson
- HikariCP
- kaptcha
- Swagger

## 7. 工程结构
  基于maven的多module模块，如果要加入其它微服务模块，直接添加即可
## 8. 秒杀业务时序图
   参数校验-->redis库存预扣减-->发送下单消息到MQ-->提示订单处理中
   订阅者-->拉取下单消息-->真实库存扣减…&下单入库

## 9. 业务功能模块
- 秒杀业务
  - 秒杀场次列表 - 待实现
  - 秒杀活动，一个活动包含秒杀起止时间、活动名称等 
  - 秒杀场次，一个场次包含多个秒杀商品 - 待实现
  - 秒杀商品，一个秒杀商品可以被多个秒杀场次关联
- 支付
- 支付回调

## 10.系统流程

## 11.开发者规约
- 参考 阿里巴巴java开发者规范 Java开发手册嵩山版

## 12.压测结果
    使用jemeter进行压测
## 13.部署说明
  先导入数据库脚本
  修改application.yml里面的mysql、redis、nacos、rocketmq链接地址
  启动相应的组件

## 14.许可协议

## 功能截图
