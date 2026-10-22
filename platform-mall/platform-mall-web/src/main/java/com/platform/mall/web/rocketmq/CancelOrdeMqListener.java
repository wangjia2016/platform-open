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
        if (mallOrder != null && MallOrderStatusConstant.TO_PAY.getTypeValue().equals(mallOrder.getOrderStatus())) {
            mallOrderService.cancelOrder(mallOrder.getId(), null, "超时取消");
        }
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setPullInterval(0);
        consumer.setPullBatchSize(32);
    }
}
