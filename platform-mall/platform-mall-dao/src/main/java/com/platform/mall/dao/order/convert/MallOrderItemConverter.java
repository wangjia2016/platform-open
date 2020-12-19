package com.platform.mall.dao.order.convert;

import com.platform.mall.dao.order.entity.MallOrderItem;
import com.platform.mall.dao.order.model.list.MallOrderItemListDto;
import com.platform.mall.dao.order.model.detail.MallOrderItemDetailDto;
import com.platform.mall.dao.order.model.query.MallOrderItemQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
* @Description MallOrderItem转换器
* @ClassName MallOrderItemConverter
* @Author wangjia
* @date 2020-10-21 14:43:02
*/
@Mapper
public interface MallOrderItemConverter {

    MallOrderItemConverter INSTANCE = Mappers.getMapper(MallOrderItemConverter.class);

    MallOrderItemDetailDto toDTO(MallOrderItem entity);

    List<MallOrderItemListDto> toListDTO(List<MallOrderItem> lst);

    MallOrderItem fromDTO(MallOrderItemDetailDto dto);

    MallOrderItem fromQuery(MallOrderItemQuery query);

}