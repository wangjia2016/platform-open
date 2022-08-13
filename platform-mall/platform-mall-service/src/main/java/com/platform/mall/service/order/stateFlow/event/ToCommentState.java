package com.platform.mall.service.order.stateFlow.event;

import com.platform.common.enums.OrderStatusEnum;
import com.platform.common.result.Result;
import com.platform.mall.service.order.stateFlow.AbstractState;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @ClassName ToCommentState
 * @Author wangjia
 * @date 2022.02.26 19:38
 */
@Component
public class ToCommentState extends AbstractState {
    @Override
    public Result toPay(Long orderId, OrderStatusEnum currentStatus) {
        return Result.fail("待评价不能待支付");
    }

    @Override
    public Result toDelivery(Long orderId, OrderStatusEnum currentStatus) {
        return Result.fail("待评价不能支付");
    }

    @Override
    public Result toReceive(Long orderId, OrderStatusEnum currentStatus) {
        return Result.fail("待评价不能发货");
    }

    @Override
    public Result toComment(Long orderId, OrderStatusEnum currentStatus) {
        return  Result.fail("待评价不能确认收货");
    }

    @Override
    public Result tradeClosed(Long orderId, OrderStatusEnum currentStatus) {
        return Result.fail("待评价不能交易关闭");
    }

    @Override
    public Result tradeFinished(Long orderId, OrderStatusEnum currentStatus) {
        final boolean update = mallOrderService.changOrderState(orderId,currentStatus,OrderStatusEnum.TRADE_FINISHED);
        return update ? Result.success("待评价完成") : Result.fail("待评价状态变更失败");
    }
}
