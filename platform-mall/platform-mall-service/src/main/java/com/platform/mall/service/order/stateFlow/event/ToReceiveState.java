package com.platform.mall.service.order.stateFlow.event;

import com.platform.common.enums.OrderStatusEnum;
import com.platform.common.result.Result;
import com.platform.mall.service.order.stateFlow.AbstractState;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @ClassName ToReceiveState
 * @Author wangjia
 * @date 2022.02.26 19:38
 */
@Component
public class ToReceiveState extends AbstractState {
    @Override
    public Result toPay(Long orderId, OrderStatusEnum currentStatus) {
        return Result.fail("待收货状态不可逆转为待支付");
    }

    @Override
    public Result toDelivery(Long orderId, OrderStatusEnum currentStatus) {
        return Result.fail("待收货状态不能待发货");
    }

    @Override
    public Result toReceive(Long orderId, OrderStatusEnum currentStatus) {
        return Result.fail("待收货状态不能重复待收货");
    }

    @Override
    public Result toComment(Long orderId, OrderStatusEnum currentStatus) {
        final boolean update = mallOrderService.changOrderState(orderId,currentStatus,OrderStatusEnum.TO_COMMENT);
        return update ? Result.success("订单收货完成") : Result.fail("订单状态变更失败");
    }

    @Override
    public Result tradeClosed(Long orderId, OrderStatusEnum currentStatus) {
        return Result.fail("待收货状态不能交易关闭");
    }

    @Override
    public Result tradeFinished(Long orderId, OrderStatusEnum currentStatus) {
        return Result.fail("待收货状态不能交易完成");
    }
}
