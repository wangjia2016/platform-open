package com.platform.mall.service.order.stateFlow;

import com.platform.common.enums.OrderStatusEnum;
import com.platform.common.enums.ResultCode;
import com.platform.common.result.Result;
import com.platform.mall.service.order.MallOrderService;
import com.platform.mall.service.order.stateFlow.event.ToCommentState;
import com.platform.mall.service.order.stateFlow.event.ToDeliveryState;
import com.platform.mall.service.order.stateFlow.event.ToPayState;
import com.platform.mall.service.order.stateFlow.event.ToReceiveState;
import com.platform.mall.service.order.stateFlow.impl.StateHandlerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 订单状态机单测：合法流转 mock 落库成功；非法流转直接失败且不调用 changOrderState。
 */
@ExtendWith(MockitoExtension.class)
class OrderStateFlowTest {

    private static final Long ORDER_ID = 1001L;

    @Mock
    private MallOrderService mallOrderService;

    private IStateHandler stateHandler;

    @BeforeEach
    void setUp() {
        ToPayState toPayState = new ToPayState();
        ToDeliveryState toDeliveryState = new ToDeliveryState();
        ToReceiveState toReceiveState = new ToReceiveState();
        ToCommentState toCommentState = new ToCommentState();
        inject(toPayState);
        inject(toDeliveryState);
        inject(toReceiveState);
        inject(toCommentState);

        StateHandlerImpl handler = new StateHandlerImpl();
        ReflectionTestUtils.setField(handler, "toPayState", toPayState);
        ReflectionTestUtils.setField(handler, "toDeliveryState", toDeliveryState);
        ReflectionTestUtils.setField(handler, "toReceiveState", toReceiveState);
        ReflectionTestUtils.setField(handler, "toCommentState", toCommentState);
        handler.init();
        stateHandler = handler;
    }

    private void inject(AbstractState state) {
        ReflectionTestUtils.setField(state, "mallOrderService", mallOrderService);
    }

    @Test
    void happyPath_toPay_toDelivery_toReceive_toComment_tradeFinished() {
        when(mallOrderService.changOrderState(eq(ORDER_ID), eq(OrderStatusEnum.TO_PAY), eq(OrderStatusEnum.TO_DELIVERY)))
                .thenReturn(true);
        when(mallOrderService.changOrderState(eq(ORDER_ID), eq(OrderStatusEnum.TO_DELIVERY), eq(OrderStatusEnum.TO_RECEIVE)))
                .thenReturn(true);
        when(mallOrderService.changOrderState(eq(ORDER_ID), eq(OrderStatusEnum.TO_RECEIVE), eq(OrderStatusEnum.TO_COMMENT)))
                .thenReturn(true);
        when(mallOrderService.changOrderState(eq(ORDER_ID), eq(OrderStatusEnum.TO_COMMENT), eq(OrderStatusEnum.TRADE_FINISHED)))
                .thenReturn(true);

        assertSuccess(stateHandler.toDelivery(ORDER_ID, OrderStatusEnum.TO_PAY), "订单支付完成");
        assertSuccess(stateHandler.toReceive(ORDER_ID, OrderStatusEnum.TO_DELIVERY), "订单发货完成");
        assertSuccess(stateHandler.toComment(ORDER_ID, OrderStatusEnum.TO_RECEIVE), "订单收货完成");
        assertSuccess(stateHandler.tradeFinished(ORDER_ID, OrderStatusEnum.TO_COMMENT), "待评价完成");
    }

    @Test
    void toPay_canCloseTrade() {
        when(mallOrderService.changOrderState(eq(ORDER_ID), eq(OrderStatusEnum.TO_PAY), eq(OrderStatusEnum.TRADE_CLOSED)))
                .thenReturn(true);
        assertSuccess(stateHandler.tradeClosed(ORDER_ID, OrderStatusEnum.TO_PAY), "交易关闭完成");
    }

    @Test
    void illegalTransitions_failWithoutPersisting() {
        assertFail(stateHandler.toPay(ORDER_ID, OrderStatusEnum.TO_PAY));
        assertFail(stateHandler.toReceive(ORDER_ID, OrderStatusEnum.TO_PAY));
        assertFail(stateHandler.toComment(ORDER_ID, OrderStatusEnum.TO_PAY));
        assertFail(stateHandler.tradeFinished(ORDER_ID, OrderStatusEnum.TO_PAY));

        assertFail(stateHandler.toPay(ORDER_ID, OrderStatusEnum.TO_DELIVERY));
        assertFail(stateHandler.toDelivery(ORDER_ID, OrderStatusEnum.TO_DELIVERY));
        assertFail(stateHandler.toComment(ORDER_ID, OrderStatusEnum.TO_DELIVERY));

        assertFail(stateHandler.toPay(ORDER_ID, OrderStatusEnum.TO_RECEIVE));
        assertFail(stateHandler.toDelivery(ORDER_ID, OrderStatusEnum.TO_RECEIVE));
        assertFail(stateHandler.toReceive(ORDER_ID, OrderStatusEnum.TO_RECEIVE));
        assertFail(stateHandler.tradeClosed(ORDER_ID, OrderStatusEnum.TO_RECEIVE));
        assertFail(stateHandler.tradeFinished(ORDER_ID, OrderStatusEnum.TO_RECEIVE));

        assertFail(stateHandler.toPay(ORDER_ID, OrderStatusEnum.TO_COMMENT));
        assertFail(stateHandler.toDelivery(ORDER_ID, OrderStatusEnum.TO_COMMENT));
        assertFail(stateHandler.toReceive(ORDER_ID, OrderStatusEnum.TO_COMMENT));
        assertFail(stateHandler.toComment(ORDER_ID, OrderStatusEnum.TO_COMMENT));
        assertFail(stateHandler.tradeClosed(ORDER_ID, OrderStatusEnum.TO_COMMENT));

        verify(mallOrderService, never()).changOrderState(
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
    }

    @Test
    void persistFailure_surfacesAsFail() {
        when(mallOrderService.changOrderState(eq(ORDER_ID), eq(OrderStatusEnum.TO_PAY), eq(OrderStatusEnum.TO_DELIVERY)))
                .thenReturn(false);
        Result result = stateHandler.toDelivery(ORDER_ID, OrderStatusEnum.TO_PAY);
        assertFail(result);
        assertEquals("订单状态变更失败", result.getMsg());
    }

    private static void assertSuccess(Result result, String dataMsg) {
        // Result.success(String) 把文案放进 data，msg 仍是「成功」
        assertEquals(ResultCode.SUCCESS.code(), result.getCode());
        assertEquals(dataMsg, result.getData());
    }

    private static void assertFail(Result result) {
        assertNotEquals(ResultCode.SUCCESS.code(), result.getCode());
    }
}
