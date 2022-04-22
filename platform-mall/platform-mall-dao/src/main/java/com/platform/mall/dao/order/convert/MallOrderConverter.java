package com.platform.mall.dao.order.convert;

import com.platform.mall.dao.order.entity.MallOrder;
import com.platform.mall.dao.order.model.list.MallOrderListDto;
import com.platform.mall.dao.order.model.detail.MallOrderDetailDto;
import com.platform.mall.dao.order.model.query.MallOrderPortalQuery;
import com.platform.mall.dao.order.model.query.MallOrderQuery;
import com.platform.mall.dao.order.model.query.MallOrderRequestQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
* @Description MallOrder转换器
* @ClassName MallOrderConverter
* @Author wangjia
* @date 2020-10-02 17:06:55
*/
@Mapper
public interface MallOrderConverter {

    MallOrderConverter INSTANCE = Mappers.getMapper(MallOrderConverter.class);

    MallOrderDetailDto toDTO(MallOrder entity);

    List<MallOrderListDto> toListDTO(List<MallOrder> lst);

    MallOrder fromDTO(MallOrderDetailDto dto);

    MallOrder fromQuery(MallOrderQuery query);

    MallOrder fromMallOrderFlashRequestQuery(MallOrderRequestQuery mallOrderFlashRequestQuery);

    MallOrder fromMallOrderPortalQuery(MallOrderPortalQuery query);

    MallOrder fromMallOrderRequestQuery(MallOrderRequestQuery query);

    /**
     *
     * 转成订单列表封装-含商品信息
     */
    MallOrderDetailDto toDetailDTO(MallOrderListDto entity);

    /**
     * 下单请求参数转换成 mq协议参数
     * */
   // MallOrderMsgProtocol toMallOrderMsgProtocol(MallOrderRequestQuery mallOrderRequestQuery);
}