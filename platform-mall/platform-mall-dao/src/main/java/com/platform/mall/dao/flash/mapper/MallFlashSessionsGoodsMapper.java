package com.platform.mall.dao.flash.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.mall.dao.flash.entity.MallFlashSessionsGoods;
import com.platform.mall.dao.flash.model.list.MallFlashSessionsGoodsListDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MallFlashSessionsGoodsMapper extends BaseMapper<MallFlashSessionsGoods> {

    /**
     * 场次关联商品列表（联表商品基础信息）
     */
    List<MallFlashSessionsGoodsListDto> listGoodsBySessionsId(@Param("sessionsId") Long sessionsId);
}
