package com.platform.mall.service.order.impl;

import org.springframework.stereotype.Service;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.platform.mall.dao.order.mapper.MallOrderItemMapper;
import com.platform.mall.dao.order.entity.MallOrderItem;
import com.platform.mall.service.order.MallOrderItemService;
import com.platform.mall.dao.order.model.list.MallOrderItemListDto;
import com.platform.mall.dao.order.model.query.MallOrderItemQuery;
import com.platform.mall.dao.order.convert.MallOrderItemConverter;


@Service("mallOrderItemService")
public class MallOrderItemServiceImpl extends ServiceImpl<MallOrderItemMapper, MallOrderItem> implements MallOrderItemService {

    @Override
    public List<MallOrderItemListDto> listMallOrderItem(MallOrderItemQuery mallOrderItemQuery) {
        List<MallOrderItem> mallOrderItemList = this.baseMapper.selectList(new QueryWrapper<>(MallOrderItemConverter.INSTANCE.fromQuery(mallOrderItemQuery)));

        List<MallOrderItemListDto> MallOrderItemListDtoList = MallOrderItemConverter.INSTANCE.toListDTO(mallOrderItemList);
        return MallOrderItemListDtoList;
    }
}