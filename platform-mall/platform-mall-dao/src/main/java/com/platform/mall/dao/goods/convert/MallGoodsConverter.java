package com.platform.mall.dao.goods.convert;

import com.platform.mall.dao.goods.entity.MallGoods;
import com.platform.mall.dao.goods.model.list.MallGoodsListDto;
import com.platform.mall.dao.goods.model.detail.MallGoodsDetailDto;
import com.platform.mall.dao.goods.model.query.MallGoodsQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
* @Description MallGoods转换器
* @ClassName MallGoodsConverter
* @Author wangjia
* @date 2020-10-02 17:02:42
*/
@Mapper
public interface MallGoodsConverter {

    MallGoodsConverter INSTANCE = Mappers.getMapper(MallGoodsConverter.class);

    MallGoodsDetailDto toDTO(MallGoods entity);

    MallGoodsDetailDto toDetail(MallGoodsQuery entity);

    List<MallGoodsListDto> toListDTO(List<MallGoods> lst);

    MallGoods fromDTO(MallGoodsDetailDto dto);

    MallGoods fromQuery(MallGoodsQuery query);

    MallGoodsDetailDto listDtoToDetail(MallGoodsListDto entity);



}