package com.platform.mall.service.order.stateFlow;

import com.platform.common.enums.OrderStatusEnum;
import com.platform.common.result.Result;
import com.platform.mall.service.order.MallOrderService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 订单状态抽象类
 * @author wangjia
 * @date 2022.02.26 18:55
 */
public abstract class AbstractState {

    @Autowired
    protected MallOrderService mallOrderService;

    /**
     * 待支付
     * @param orderId 订单id
     * @param currentStatus 当前状态
     * @return Result
     * @author wangjia
     * @date 2022.02.26 19:15
     */
    public abstract Result toPay(Long orderId,OrderStatusEnum currentStatus);

    /**
     * 待发货
     * @param  orderId 订单id
     * @param  currentStatus 当前状态
     * @return Result
     * @author wangjia
     * @date 2022.02.26 19:15
     */
    public abstract Result toDelivery(Long orderId,OrderStatusEnum currentStatus);
    
    /**
     * 待收货
     * @param  orderId 订单id
     * @param  currentStatus 当前状态
     * @return Result
     * @author wangjia
     * @date 2022.02.26 19:30
     **/
    public abstract Result toReceive(Long orderId,OrderStatusEnum currentStatus);

    /**
     * 待评价
     * @param  orderId 订单id
     * @param  currentStatus 当前状态
     * @return Result
     * @author wangjia
     * @date 2022.02.26 19:32
     **/
    public abstract Result toComment(Long orderId,OrderStatusEnum currentStatus);

    /**
     * 交易关闭
     * @param  orderId 订单id
     * @param  currentStatus 当前状态
     * @return Result
     * @author wangjia
     * @date 2022.02.26 19:32
     **/
    public abstract Result tradeClosed(Long orderId,OrderStatusEnum currentStatus);

    /**
     * 交易结束
     * @param  orderId 订单id
     * @param  currentStatus 当前状态
     * @return Result
     * @author wangjia
     * @date 2022.02.26 19:32
     **/
    public abstract Result tradeFinished(Long orderId,OrderStatusEnum currentStatus);

}
