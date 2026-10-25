package com.platform.mall.service.order;

import com.platform.mall.dao.basic.MallOrderStatusConstant;
import com.platform.mall.dao.order.entity.MallOrder;
import com.platform.mall.dao.order.mapper.MallOrderItemMapper;
import com.platform.mall.dao.order.mapper.MallOrderMapper;
import com.platform.mall.dao.order.model.list.WxPayOrderNotifyResult;
import com.platform.mall.service.feignClient.WechatFeignClient;
import com.platform.mall.service.order.impl.MallOrderPayServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 支付回调：乐观锁幂等 + 仅首次成功发 MQ（经 asyncMsgProcess）。
 */
@ExtendWith(MockitoExtension.class)
class MallOrderPayNotifyTest {

    @Mock
    private MallOrderMapper mallOrderMapper;
    @Mock
    private WechatFeignClient wechatFeignClient;
    @Mock
    private MallOrderItemMapper mallOrderItemMapper;
    @Mock
    private MallOrderService mallOrderService;

    private MallOrderPayServiceImpl payService;

    @BeforeEach
    void setUp() {
        payService = new MallOrderPayServiceImpl(mallOrderMapper, wechatFeignClient, mallOrderItemMapper, mallOrderService);
    }

    @Test
    void firstNotify_updatesToDeliveryAndSendsMq() {
        MallOrder order = baseOrder(MallOrderStatusConstant.TO_PAY.getTypeValue());
        when(mallOrderMapper.selectOne(any())).thenReturn(order);
        when(mallOrderMapper.update(isNull(), any())).thenReturn(1);

        assertTrue(payService.notifyOrderProcess(notify("9001", "tx-1", "20261025120000")));

        verify(mallOrderMapper).update(isNull(), any());
        assertEquals(MallOrderStatusConstant.TO_DELIVERY.getTypeValue(), order.getOrderStatus());
        assertEquals("tx-1", order.getTransactionId());
        verify(mallOrderService).asyncMsgProcess(order);
    }

    @Test
    void duplicateWhenAlreadyPaid_skipsUpdateAndMq() {
        MallOrder order = baseOrder(MallOrderStatusConstant.TO_DELIVERY.getTypeValue());
        when(mallOrderMapper.selectOne(any())).thenReturn(order);

        assertTrue(payService.notifyOrderProcess(notify("9001", "tx-1", "20261025120000")));

        verify(mallOrderMapper, never()).update(any(), any());
        verify(mallOrderService, never()).asyncMsgProcess(any());
    }

    @Test
    void concurrentLostUpdate_skipsMq() {
        MallOrder order = baseOrder(MallOrderStatusConstant.TO_PAY.getTypeValue());
        when(mallOrderMapper.selectOne(any())).thenReturn(order);
        when(mallOrderMapper.update(isNull(), any())).thenReturn(0);

        assertTrue(payService.notifyOrderProcess(notify("9001", "tx-2", "20261025120000")));
        verify(mallOrderService, never()).asyncMsgProcess(any());
    }

    @Test
    void badOutTradeNo_fails() {
        assertFalse(payService.notifyOrderProcess(notify("not-a-number", "tx", "20261025120000")));
        verify(mallOrderMapper, never()).selectOne(any());
    }

    @Test
    void missingOrder_fails() {
        when(mallOrderMapper.selectOne(any())).thenReturn(null);
        assertFalse(payService.notifyOrderProcess(notify("9001", "tx", "20261025120000")));
    }

    private static MallOrder baseOrder(Integer status) {
        MallOrder o = new MallOrder();
        o.setId(1L);
        o.setOrderNo(9001L);
        o.setOrderStatus(status);
        o.setActualAmount(new BigDecimal("19.90"));
        return o;
    }

    private static WxPayOrderNotifyResult notify(String outTradeNo, String tx, String timeEnd) {
        WxPayOrderNotifyResult r = new WxPayOrderNotifyResult();
        r.setOutTradeNo(outTradeNo);
        r.setTransactionId(tx);
        r.setTimeEnd(timeEnd);
        return r;
    }
}
