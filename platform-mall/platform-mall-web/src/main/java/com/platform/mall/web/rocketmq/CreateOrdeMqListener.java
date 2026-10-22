package com.platform.mall.web.rocketmq;

import com.platform.mall.service.order.MallOrderMq;
import com.platform.mall.service.order.impl.CreateOrderMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消费秒杀下单消息。主题与 {@code MallOrderServiceImpl#preReduceStock} 发送的目的地一致。
 */
@Service
@RocketMQMessageListener(consumerGroup = MallOrderMq.CREATE_FLASH_GROUP, topic = MallOrderMq.CREATE_FLASH_TOPIC)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class CreateOrdeMqListener implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    private final CreateOrderMessageHandler createOrderMessageHandler;

    @Override
    public void onMessage(String message) {
        createOrderMessageHandler.onMessage(message);
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setPullInterval(0);
        consumer.setPullBatchSize(32);
    }
}
