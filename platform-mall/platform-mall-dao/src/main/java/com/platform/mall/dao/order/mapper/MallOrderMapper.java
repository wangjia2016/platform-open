package com.platform.mall.dao.order.mapper;

import com.platform.common.enums.OrderStatusEnum;
import com.platform.mall.dao.order.entity.MallOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-02 17:06:55
 */
@Mapper
public interface MallOrderMapper extends BaseMapper<MallOrder> {

    /**
     * 订单状态流转
     * @param orderId
     * @param beforeStatus 流转之前的状态
     * @param afterStatus  流转之后的状态
     * @return Boolean
     * @author wangjia
     * @date 2022.02.26 21:31
     **/
    Boolean changOrderState(Long orderId, OrderStatusEnum beforeStatus, OrderStatusEnum afterStatus);
}
