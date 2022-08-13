package com.platform.mall.service.order.stateFlow.event;

import com.platform.common.enums.OrderStatusEnum;
import com.platform.common.result.Result;
import com.platform.mall.service.order.stateFlow.AbstractState;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @ClassName ToPayState
 * @Author wangjia
 * @date 2022.02.26 19:36
 */
@Component
public class ToPayState extends AbstractState {
    @Override
    public Result toPay(Long orderId, OrderStatusEnum currentStatus) {
        return Result.fail("待支付不可重复待支付");
    }

    @Override
    public Result toDelivery(Long orderId, OrderStatusEnum currentStatus) {
        final boolean update = mallOrderService.changOrderState(orderId,currentStatus,OrderStatusEnum.TRADE_CLOSED);
        return update ? Result.success("订单支付完成") : Result.fail("订单状态变更失败");
    }

    @Override
    public Result toReceive(Long orderId, OrderStatusEnum currentStatus) {
        return Result.fail("待支付状态不可发货");
    }

    @Override
    public Result toComment(Long orderId, OrderStatusEnum currentStatus) {
        return Result.fail("待支付状态不可确认收货");
    }

    @Override
    public Result tradeClosed(Long orderId, OrderStatusEnum currentStatus) {
        final boolean update = mallOrderService.changOrderState(orderId,currentStatus,OrderStatusEnum.TRADE_CLOSED);
        return update ? Result.success("交易关闭完成") : Result.fail("订单状态变更失败");
    }

    @Override
    public Result tradeFinished(Long orderId, OrderStatusEnum currentStatus) {
        return Result.fail("待支付状态不可交易结束");
    }
}
