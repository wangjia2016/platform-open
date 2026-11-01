package com.platform.mall.service.order.impl;

import com.platform.common.exception.InsufficientStockException;
import com.platform.common.util.SnowFlakeIdGenerator;
import com.platform.mall.dao.goods.mapper.MallGoodsMapper;
import com.platform.mall.dao.order.entity.MallOrder;
import com.platform.mall.dao.order.mapper.MallOrderDeliveryMapper;
import com.platform.mall.dao.order.mapper.MallOrderItemMapper;
import com.platform.mall.dao.order.mapper.MallOrderMapper;
import com.platform.mall.dao.order.model.query.MallOrderRequestQuery;
import com.platform.mall.manager.cache.GoodsStockCache;
import com.platform.mall.manager.cache.MallGoodsCacheManager;
import com.platform.mall.manager.cache.OrderStatusCache;
import com.platform.mall.service.flash.MallFlashSessionsService;
import com.platform.mall.service.order.MallOrderDeliveryService;
import com.platform.mall.service.order.MallOrderItemService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateOrderPersistTest {

    private MallOrderMapper orderMapper;
    private MallOrderItemMapper itemMapper;
    private MallGoodsMapper goodsMapper;
    private MallOrderServiceImpl service;

    @BeforeEach
    void setUp() {
        orderMapper = mock(MallOrderMapper.class);
        itemMapper = mock(MallOrderItemMapper.class);
        goodsMapper = mock(MallGoodsMapper.class);
        SnowFlakeIdGenerator ids = mock(SnowFlakeIdGenerator.class);
        when(ids.nextId()).thenReturn(500L);
        service = new MallOrderServiceImpl(
                itemMapper,
                mock(MallOrderItemService.class),
                orderMapper,
                mock(MallOrderDeliveryMapper.class),
                mock(MallOrderDeliveryService.class),
                ids,
                mock(RocketMQTemplate.class),
                goodsMapper,
                mock(MallGoodsCacheManager.class),
                mock(GoodsStockCache.class),
                mock(MallFlashSessionsService.class),
                mock(OrderStatusCache.class));
    }

    @Test
    void stockUpdateHappensAfterInserts() {
        when(orderMapper.insert(any(MallOrder.class))).thenReturn(1);
        when(itemMapper.insert(any())).thenReturn(1);
        when(goodsMapper.updateGoodsStock(9L, 1)).thenReturn(1);

        assertTrue(service.createOrder(request()));

        org.mockito.InOrder sequence = inOrder(orderMapper, itemMapper, goodsMapper);
        sequence.verify(orderMapper).insert(any(MallOrder.class));
        sequence.verify(itemMapper).insert(any());
        sequence.verify(goodsMapper).updateGoodsStock(9L, 1);
    }

    @Test
    void duplicateOrderSkipsStockUpdate() {
        when(orderMapper.insert(any(MallOrder.class))).thenThrow(new DuplicateKeyException("dup"));

        assertTrue(service.createOrder(request()));

        verify(itemMapper, never()).insert(any());
        verify(goodsMapper, never()).updateGoodsStock(any(), any());
    }

    @Test
    void insufficientStockFailsAfterOrderRowIsInserted() {
        when(orderMapper.insert(any(MallOrder.class))).thenReturn(1);
        when(itemMapper.insert(any())).thenReturn(1);
        when(goodsMapper.updateGoodsStock(9L, 1)).thenReturn(0);

        assertThrows(InsufficientStockException.class, () -> service.createOrder(request()));
        verify(orderMapper).insert(any(MallOrder.class));
    }

    private static MallOrderRequestQuery request() {
        MallOrderRequestQuery request = new MallOrderRequestQuery();
        request.setOrderId(11L);
        request.setGoodsId(9L);
        request.setNumber(1);
        request.setMemberId(3L);
        request.setAppId("app");
        return request;
    }
}
