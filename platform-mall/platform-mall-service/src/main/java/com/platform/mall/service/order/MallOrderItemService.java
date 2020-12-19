package com.platform.mall.service.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.platform.mall.dao.order.entity.MallOrderItem;
import com.platform.mall.dao.order.model.list.MallOrderItemListDto;
import com.platform.mall.dao.order.model.detail.MallOrderItemDetailDto;
import com.platform.mall.dao.order.model.query.MallOrderItemQuery;

import java.util.List;

/**
 * MallOrderItem业务处理
 *
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2020-10-02 17:06:55
 */
public interface MallOrderItemService extends IService<MallOrderItem> {

    /**
     * @Description 查询MallOrderItem列表不分页
     * @Param MallOrderItemDto
     * @return
     * @Author wangjia
     * @Date 2020-10-02 17:06:55
     **/
    List<MallOrderItemListDto> listMallOrderItem(MallOrderItemQuery mallOrderItemQuery);

}

