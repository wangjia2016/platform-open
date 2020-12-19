package com.platform.mall.web.rocketmq;

import com.platform.common.util.JsonUtil;
import com.platform.mall.dao.order.model.detail.MallOrderDetailDto;
import com.platform.mall.dao.order.model.query.MallOrderQuery;
import com.platform.mall.dao.order.model.query.MallOrderRequestQuery;
import com.platform.mall.service.order.MallOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description 创建普通订单Listener
 * @ClassName CancelOrdeMqListener
 * @Author wangjia
 * @date 2020.11.06 19:03
 */
@Service
@RocketMQMessageListener(consumerGroup = "create-normal-mall-order-group",topic = "create-normal-mall-order")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class CreateOrdeMqListener implements RocketMQListener<String> , RocketMQPushConsumerLifecycleListener {

    private final MallOrderService mallOrderService;

    @Override
    public void onMessage(String message) {

        MallOrderRequestQuery mallOrderRequestQuery = JsonUtil.json2Object(message, MallOrderRequestQuery.class);
        MallOrderDetailDto mallOrder = mallOrderService.getOne(MallOrderQuery.builder()
                .orderNo(mallOrderRequestQuery.getOrderNo()).build());

        //要实现消息幂等性处理
        if (mallOrder != null) {
            log.info("message:{},处理次数",message);
            log.error("订单号{},已创建，忽略处理",mallOrderRequestQuery.getOrderNo());
        }else{
            final Boolean order = mallOrderService.createOrder(mallOrderRequestQuery);
            if(!order){
                log.error("订单创建失败,{}",JsonUtil.object2Json(mallOrderRequestQuery));
            }
        }
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {

        //每次拉取间隔，单位毫秒
        consumer.setPullInterval(2000);
        //每次从队列中拉取的消息数
        consumer.setPullBatchSize(100);
    }


}
