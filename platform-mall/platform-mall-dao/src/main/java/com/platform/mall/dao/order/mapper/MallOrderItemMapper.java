package com.platform.mall.dao.order.mapper;

import com.platform.mall.dao.order.entity.MallOrderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项，订单包含的商品、订单详情
 * 
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-21 14:43:02
 */
@Mapper
public interface MallOrderItemMapper extends BaseMapper<MallOrderItem> {
	
}
