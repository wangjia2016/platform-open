package com.platform.mall.service.goods.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.platform.mall.dao.goods.mapper.MallGoodsMapper;
import com.platform.mall.dao.goods.entity.MallGoods;
import com.platform.mall.service.goods.MallGoodsService;
import com.platform.mall.dao.goods.model.list.MallGoodsListDto;
import com.platform.mall.dao.goods.model.detail.MallGoodsDetailDto;
import com.platform.mall.dao.goods.model.query.MallGoodsQuery;
import com.platform.mall.dao.goods.convert.MallGoodsConverter;

/**
 * @author Administrator
 */
@Service("mallGoodsService")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MallGoodsServiceImpl extends ServiceImpl<MallGoodsMapper, MallGoods> implements MallGoodsService {

    private final MallGoodsMapper mallGoodsMapper;

    @Override
    public List<MallGoodsListDto> listMallGoods(MallGoodsQuery mallGoodsQuery) {
        List<MallGoods> mallGoodsList;
        MallGoods mallGoods = MallGoodsConverter.INSTANCE.fromQuery(mallGoodsQuery);
        mallGoodsList = this.mallGoodsMapper.selectList(new QueryWrapper<MallGoods>(mallGoods));

        List<MallGoodsListDto> mallGoodsListDtoList = MallGoodsConverter.INSTANCE.toListDTO(mallGoodsList);
        return mallGoodsListDtoList;
    }

    @Override
    public MallGoodsDetailDto getOne(MallGoodsQuery mallGoodsQuery) {
        MallGoods mallGoods = this.getOne(new QueryWrapper<MallGoods>().lambda()
                        .eq(MallGoods::getTenantId,mallGoodsQuery.getTenantId()),
                true);
        return MallGoodsConverter.INSTANCE.toDTO(mallGoods);
    }

    @Override
    public MallGoodsDetailDto queryGoodsDetail(Long id) {
        return mallGoodsMapper.queryGoodsDetail(id);
    }

}