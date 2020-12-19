package com.platform.mall.service.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.platform.mall.dao.order.entity.MallOrderDelivery;
import com.platform.mall.dao.order.model.list.MallOrderDeliveryListDto;
import com.platform.mall.dao.order.model.detail.MallOrderDeliveryDetailDto;
import com.platform.mall.dao.order.model.query.MallOrderDeliveryQuery;

import java.util.List;

/**
 * MallOrderDelivery业务处理
 *
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-02 17:06:55
 */
public interface MallOrderDeliveryService extends IService<MallOrderDelivery> {
    /**
     * @Description 根据MallOrderDeliveryDto查询一条MallOrderDelivery记录
     * @Param MallOrderDeliveryDto
     * @return
     * @Author wangjia
     * @Date 2020-10-02 17:06:55
     **/
    MallOrderDeliveryDetailDto getOne(MallOrderDeliveryQuery mallOrderDeliveryQuery);
}

