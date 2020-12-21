package com.platform.mall.web.rocketmq;

import com.platform.mall.dao.basic.MallOrderStatusConstant;
import com.platform.mall.dao.order.entity.MallOrder;
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
 * @Description 取消订单Listener
 * @ClassName CancelOrdeMqListener
 * @Author wangjia
 * @date 2020.11.06 19:03
 */
@Service
@RocketMQMessageListener(consumerGroup = "cancel-normal-mall-order-group",topic = "cancel-normal-mall-order")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class CancelOrdeMqListener implements RocketMQListener<String> , RocketMQPushConsumerLifecycleListener {

    private final MallOrderService mallOrderService;

    @Override
    public void onMessage(String message) {

        MallOrder mallOrder = mallOrderService.getById(Long.parseLong(message));
        //要实现消息幂等性处理
        if (mallOrder != null) {
            log.info("message:{},处理次数",message);
            if (MallOrderStatusConstant.TO_PAY.getTypeValue().equals(mallOrder.getOrderStatus())){
                //待支付 -取消
                mallOrderService.cancelOrder(Long.parseLong(message),"超时取消");
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
