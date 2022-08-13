package com.platform.mall.service.order.stateFlow.impl;

import com.platform.common.enums.OrderStatusEnum;
import com.platform.common.result.Result;
import com.platform.mall.service.order.stateFlow.IStateHandler;
import com.platform.mall.service.order.stateFlow.StateConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description 状态处理器实现类
 * @ClassName StateHandlerImpl
 * @Author wangjia
 * @date 2022.02.26 19:50
 */
@Service("stateHandler")
@Slf4j
public class StateHandlerImpl extends StateConfig implements IStateHandler {

    StateHandlerImpl(){
        log.info("loading...");
    }
    @Override
    public Result toPay(Long orderId, OrderStatusEnum currentStatus) {
        return stateGroup.get(currentStatus).toPay(orderId,currentStatus);
    }

    @Override
    public Result toDelivery(Long orderId, OrderStatusEnum currentStatus) {
        return stateGroup.get(currentStatus).toDelivery(orderId,currentStatus);
    }

    @Override
    public Result toReceive(Long orderId, OrderStatusEnum currentStatus) {
        return stateGroup.get(currentStatus).toReceive(orderId,currentStatus);
    }

    @Override
    public Result toComment(Long orderId, OrderStatusEnum currentStatus) {
        return stateGroup.get(currentStatus).toComment(orderId,currentStatus);
    }

    @Override
    public Result tradeClosed(Long orderId, OrderStatusEnum currentStatus) {
        return stateGroup.get(currentStatus).tradeClosed(orderId,currentStatus);
    }

    @Override
    public Result tradeFinished(Long orderId, OrderStatusEnum currentStatus) {
        return stateGroup.get(currentStatus).tradeFinished(orderId,currentStatus);
    }
}
