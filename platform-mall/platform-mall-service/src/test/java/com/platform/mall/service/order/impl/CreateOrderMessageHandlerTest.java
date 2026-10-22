package com.platform.mall.service.order.impl;

import com.platform.common.exception.BusinessException;
import com.platform.common.exception.InsufficientStockException;
import com.platform.common.util.JsonUtil;
import com.platform.mall.dao.order.model.detail.MallOrderDetailDto;
import com.platform.mall.dao.order.model.query.MallOrderQuery;
import com.platform.mall.dao.order.model.query.MallOrderRequestQuery;
import com.platform.mall.manager.cache.GoodsStockCache;
import com.platform.mall.service.order.MallOrderService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateOrderMessageHandlerTest {

    @Test
    void duplicateMessageDoesNotCreateAgain() {
        MallOrderService orders = mock(MallOrderService.class);
        GoodsStockCache stock = mock(GoodsStockCache.class);
        when(orders.getOne(any(MallOrderQuery.class))).thenReturn(new MallOrderDetailDto());
        CreateOrderMessageHandler handler = new CreateOrderMessageHandler(orders, stock);

        handler.onMessage(payload(11L, 22L, 1));

        verify(orders, never()).createOrder(any());
        verify(stock, never()).restore(any(), anyInt());
    }

    @Test
    void insufficientStockRestoresReservationAndAcks() {
        MallOrderService orders = mock(MallOrderService.class);
        GoodsStockCache stock = mock(GoodsStockCache.class);
        when(orders.getOne(any(MallOrderQuery.class))).thenReturn(null);
        when(orders.createOrder(any())).thenThrow(new InsufficientStockException("库存不足"));
        CreateOrderMessageHandler handler = new CreateOrderMessageHandler(orders, stock);

        handler.onMessage(payload(11L, 22L, 2));

        verify(stock).restore(22L, 2);
    }

    @Test
    void otherFailuresAreRetried() {
        MallOrderService orders = mock(MallOrderService.class);
        GoodsStockCache stock = mock(GoodsStockCache.class);
        when(orders.getOne(any(MallOrderQuery.class))).thenReturn(null);
        when(orders.createOrder(any())).thenThrow(new BusinessException("数据库不可用"));
        CreateOrderMessageHandler handler = new CreateOrderMessageHandler(orders, stock);

        assertThrows(BusinessException.class, () -> handler.onMessage(payload(11L, 22L, 1)));
        verify(stock, never()).restore(any(), anyInt());
    }

    private static String payload(Long orderId, Long goodsId, Integer number) {
        MallOrderRequestQuery request = new MallOrderRequestQuery();
        request.setOrderId(orderId);
        request.setGoodsId(goodsId);
        request.setNumber(number);
        return JsonUtil.object2Json(request);
    }
}
