package com.platform.mall.service.order.stateFlow;

import com.platform.common.enums.OrderStatusEnum;
import com.platform.mall.service.order.stateFlow.event.ToCommentState;
import com.platform.mall.service.order.stateFlow.event.ToDeliveryState;
import com.platform.mall.service.order.stateFlow.event.ToPayState;
import com.platform.mall.service.order.stateFlow.event.ToReceiveState;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 状态流转配置
 * @ClassName StateConfig
 * @author wangjia
 * @date 2022.02.26 18:57
 */
public class StateConfig {

    @Resource
    private ToCommentState toCommentState;
    @Resource
    private ToDeliveryState toDeliveryState;
    @Resource
    private ToPayState toPayState;
    @Resource
    private ToReceiveState toReceiveState;

    protected Map<OrderStatusEnum, AbstractState> stateGroup = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        stateGroup.put(OrderStatusEnum.TO_PAY, toPayState);
        stateGroup.put(OrderStatusEnum.TO_DELIVERY, toDeliveryState);
        stateGroup.put(OrderStatusEnum.TO_RECEIVE, toReceiveState);
        stateGroup.put(OrderStatusEnum.TO_COMMENT, toCommentState);
    }
}
