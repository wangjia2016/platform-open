package com.platform.mall.dao.order.convert;

import com.platform.mall.dao.order.entity.MallOrderDelivery;
import com.platform.mall.dao.order.model.list.MallOrderDeliveryListDto;
import com.platform.mall.dao.order.model.detail.MallOrderDeliveryDetailDto;
import com.platform.mall.dao.order.model.query.MallOrderDeliveryQuery;
import com.platform.mall.dao.order.model.query.MallOrderRequestQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
* @Description MallOrderDelivery转换器
* @ClassName MallOrderDeliveryConverter
* @Author wangjia
* @date 2020-10-21 14:43:02
*/
@Mapper
public interface MallOrderDeliveryConverter {

    MallOrderDeliveryConverter INSTANCE = Mappers.getMapper(MallOrderDeliveryConverter.class);

    MallOrderDeliveryDetailDto toDTO(MallOrderDelivery entity);

    List<MallOrderDeliveryListDto> toListDTO(List<MallOrderDelivery> lst);

    MallOrderDelivery fromDTO(MallOrderDeliveryDetailDto dto);

    MallOrderDelivery fromQuery(MallOrderDeliveryQuery query);

    MallOrderDelivery fromMallOrderRequestQuery(MallOrderRequestQuery query);

    MallOrderDelivery fromMallOrderFlashRequestQuery(MallOrderRequestQuery mallOrderFlashRequestQuery);

}