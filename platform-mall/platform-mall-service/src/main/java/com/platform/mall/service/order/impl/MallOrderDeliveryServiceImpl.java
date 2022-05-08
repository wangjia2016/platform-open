package com.platform.mall.service.order.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.mall.dao.order.mapper.MallOrderDeliveryMapper;
import com.platform.mall.dao.order.entity.MallOrderDelivery;
import com.platform.mall.service.order.MallOrderDeliveryService;
import com.platform.mall.dao.order.model.detail.MallOrderDeliveryDetailDto;
import com.platform.mall.dao.order.model.query.MallOrderDeliveryQuery;
import com.platform.mall.dao.order.convert.MallOrderDeliveryConverter;


@Service("mallOrderDeliveryService")
public class MallOrderDeliveryServiceImpl extends ServiceImpl<MallOrderDeliveryMapper, MallOrderDelivery> implements MallOrderDeliveryService {

    @Override
    public MallOrderDeliveryDetailDto getOne(MallOrderDeliveryQuery mallOrderDeliveryQuery) {
        MallOrderDelivery mallOrderDelivery = this.getOne(new QueryWrapper<>(MallOrderDeliveryConverter.INSTANCE.fromQuery(mallOrderDeliveryQuery)));
        return MallOrderDeliveryConverter.INSTANCE.toDTO(mallOrderDelivery);
    }
}