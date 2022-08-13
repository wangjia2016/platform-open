package com.platform.mall.service.order.stateFlow;

import com.platform.common.enums.OrderStatusEnum;
import com.platform.common.result.Result;

/**
 * 订单状态处理器接口
 * @author wangjia
 * @date 2022.02.26 18:55
 */
public interface IStateHandler {

    /**
     * 待支付
     * @param orderId 订单id
     * @param currentStatus 订单状态枚举
     * @return Result
     * @author wangjia
     * @date 2022.02.26 19:15
     */
    public  Result toPay(Long orderId, OrderStatusEnum currentStatus);

    /**
     * 待发货
     * @param  orderId 订单id
     * @param  currentStatus 订单状态枚举
     * @return Result
     * @author wangjia
     * @date 2022.02.26 19:15
     */
    public  Result toDelivery(Long orderId,OrderStatusEnum currentStatus);

    /**
     * 待收货
     * @param  orderId 订单id
     * @param  currentStatus 订单状态枚举
     * @return Result
     * @author wangjia
     * @date 2022.02.26 19:30
     **/
    public  Result toReceive(Long orderId,OrderStatusEnum currentStatus);

    /**
     * 待评价
     * @param  orderId 订单id
     * @param  currentStatus 订单状态枚举
     * @return Result
     * @author wangjia
     * @date 2022.02.26 19:32
     **/
    public  Result toComment(Long orderId,OrderStatusEnum currentStatus);

    /**
     * 交易关闭
     * @param  orderId 订单id
     * @param  currentStatus 订单状态枚举
     * @return Result
     * @author wangjia
     * @date 2022.02.26 19:32
     **/
    public  Result tradeClosed(Long orderId,OrderStatusEnum currentStatus);

    /**
     * 待评价
     * @param  orderId 订单id
     * @param  currentStatus 订单状态枚举
     * @return Result
     * @author wangjia
     * @date 2022.02.26 19:32
     **/
    public  Result tradeFinished(Long orderId,OrderStatusEnum currentStatus);

}
