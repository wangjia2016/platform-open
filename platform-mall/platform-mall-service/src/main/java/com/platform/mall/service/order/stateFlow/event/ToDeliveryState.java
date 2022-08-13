package com.platform.mall.service.order.stateFlow.event;

import com.platform.common.enums.OrderStatusEnum;
import com.platform.common.result.Result;
import com.platform.mall.service.order.stateFlow.AbstractState;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @ClassName ToDeliveryState
 * @Author wangjia
 * @date 2022.02.26 19:37
 */
@Component
public class ToDeliveryState extends AbstractState {
    @Override
    public Result toPay(Long orderId, OrderStatusEnum currentStatus) {
        return Result.fail("待发货状态不可逆转为待支付");
    }

    @Override
    public Result toDelivery(Long orderId, OrderStatusEnum currentStatus) {
        return Result.fail("发货不可重复发货");
    }

    @Override
    public Result toReceive(Long orderId, OrderStatusEnum currentStatus) {
        final boolean update = mallOrderService.changOrderState(orderId,currentStatus,OrderStatusEnum.TO_RECEIVE);
        return update ? Result.success("订单发货完成") : Result.fail("订单状态变更失败");
    }

    @Override
    public Result toComment(Long orderId, OrderStatusEnum currentStatus) {
        return Result.fail("待发货状态不可点评");
    }

    @Override
    public Result tradeClosed(Long orderId, OrderStatusEnum currentStatus) {
        return Result.fail("待发货状态不可交易结束");
    }

    @Override
    public Result tradeFinished(Long orderId, OrderStatusEnum currentStatus) {
        return Result.fail("待发货状态不可交易关闭");
    }
}
