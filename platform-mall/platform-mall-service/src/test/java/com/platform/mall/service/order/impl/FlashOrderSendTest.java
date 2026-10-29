package com.platform.mall.service.order.impl;

import com.platform.common.exception.BusinessException;
import com.platform.common.util.SnowFlakeIdGenerator;
import com.platform.mall.dao.goods.mapper.MallGoodsMapper;
import com.platform.mall.dao.order.mapper.MallOrderDeliveryMapper;
import com.platform.mall.dao.order.mapper.MallOrderItemMapper;
import com.platform.mall.dao.order.mapper.MallOrderMapper;
import com.platform.mall.dao.order.model.query.MallOrderRequestQuery;
import com.platform.mall.manager.cache.GoodsStockCache;
import com.platform.mall.manager.cache.MallGoodsCacheManager;
import com.platform.mall.service.flash.MallFlashSessionsService;
import com.platform.mall.service.order.MallOrderDeliveryService;
import com.platform.mall.service.order.MallOrderItemService;
import com.platform.mall.service.order.MallOrderMq;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FlashOrderSendTest {

    private RocketMQTemplate rocket;
    private GoodsStockCache stock;
    private MallOrderServiceImpl service;

    @BeforeEach
    void setUp() {
        rocket = mock(RocketMQTemplate.class);
        stock = mock(GoodsStockCache.class);
        SnowFlakeIdGenerator ids = mock(SnowFlakeIdGenerator.class);
        when(ids.nextId()).thenReturn(100L);
        when(stock.tryDeduct(9L, 1)).thenReturn(4L);
        service = new MallOrderServiceImpl(
                mock(MallOrderItemMapper.class),
                mock(MallOrderItemService.class),
                mock(MallOrderMapper.class),
                mock(MallOrderDeliveryMapper.class),
                mock(MallOrderDeliveryService.class),
                ids,
                rocket,
                mock(MallGoodsMapper.class),
                mock(MallGoodsCacheManager.class),
                stock,
                mock(MallFlashSessionsService.class));
    }

    @Test
    void returnsAfterQueueingWithoutWaitingForBroker() {
        MallOrderRequestQuery request = request();

        assertTrue(service.preReduceStock(request));

        assertEquals(100L, request.getOrderId());
        verify(rocket).asyncSend(eq(MallOrderMq.CREATE_FLASH_DESTINATION), any(Message.class), any(SendCallback.class), eq(3000L));
        verify(stock, never()).restore(eq(9L), eq(1));
    }

    @Test
    void brokerRejectionRestoresStockAndFailsTheRequest() {
        doAnswer(invocation -> {
            SendCallback callback = invocation.getArgument(2);
            callback.onException(new RuntimeException("broker down"));
            return null;
        }).when(rocket).asyncSend(eq(MallOrderMq.CREATE_FLASH_DESTINATION), any(Message.class), any(SendCallback.class), eq(3000L));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.preReduceStock(request()));

        assertEquals("下单消息发送失败", ex.getMessage());
        verify(stock).restore(9L, 1);
    }

    @Test
    void submitFailureRestoresStockOnce() {
        doAnswer(invocation -> {
            SendCallback callback = invocation.getArgument(2);
            callback.onException(new RuntimeException("broker down"));
            throw new RuntimeException("submit failed");
        }).when(rocket).asyncSend(eq(MallOrderMq.CREATE_FLASH_DESTINATION), any(Message.class), any(SendCallback.class), eq(3000L));

        assertThrows(BusinessException.class, () -> service.preReduceStock(request()));

        verify(stock).restore(9L, 1);
    }

    @Test
    void nonOkStatusRestoresStock() {
        doAnswer(invocation -> {
            SendCallback callback = invocation.getArgument(2);
            SendResult result = new SendResult();
            result.setSendStatus(SendStatus.FLUSH_DISK_TIMEOUT);
            callback.onSuccess(result);
            return null;
        }).when(rocket).asyncSend(eq(MallOrderMq.CREATE_FLASH_DESTINATION), any(Message.class), any(SendCallback.class), eq(3000L));

        assertThrows(BusinessException.class, () -> service.preReduceStock(request()));
        verify(stock).restore(9L, 1);
    }

    @Test
    void clientThrowRestoresStock() {
        doThrow(new RuntimeException("client down")).when(rocket)
                .asyncSend(eq(MallOrderMq.CREATE_FLASH_DESTINATION), any(Message.class), any(SendCallback.class), eq(3000L));

        assertThrows(BusinessException.class, () -> service.preReduceStock(request()));
        verify(stock).restore(9L, 1);
    }

    private static MallOrderRequestQuery request() {
        MallOrderRequestQuery request = new MallOrderRequestQuery();
        request.setGoodsId(9L);
        request.setNumber(1);
        request.setMemberId(3L);
        request.setSessionsId(4L);
        request.setAppId("app");
        return request;
    }
}
