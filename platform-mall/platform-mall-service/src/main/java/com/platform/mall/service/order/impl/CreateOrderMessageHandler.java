package com.platform.mall.service.order.impl;

import com.platform.common.exception.BusinessException;
import com.platform.common.exception.InsufficientStockException;
import com.platform.common.util.JsonUtil;
import com.platform.mall.dao.order.model.detail.MallOrderDetailDto;
import com.platform.mall.dao.order.model.query.MallOrderQuery;
import com.platform.mall.dao.order.model.query.MallOrderRequestQuery;
import com.platform.mall.manager.cache.GoodsStockCache;
import com.platform.mall.service.order.MallOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 创建订单消息的消费逻辑。库存不足时回补预扣并确认消息，其它失败交给 RocketMQ 重试。
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CreateOrderMessageHandler {

    private final MallOrderService mallOrderService;

    private final GoodsStockCache goodsStockCache;

    public void onMessage(String message) {
        MallOrderRequestQuery request = JsonUtil.json2Object(message, MallOrderRequestQuery.class);
        if (request == null || request.getOrderId() == null) {
            throw new BusinessException("创建订单消息缺少 orderId");
        }
        MallOrderDetailDto existing = mallOrderService.getOne(MallOrderQuery.builder()
                .id(request.getOrderId())
                .build());
        if (existing != null) {
            log.info("订单已创建,忽略重复消息,orderId={}", request.getOrderId());
            return;
        }
        try {
            Boolean created = mallOrderService.createOrder(request);
            if (!Boolean.TRUE.equals(created)) {
                throw new BusinessException("订单创建失败,orderId=" + request.getOrderId());
            }
        } catch (InsufficientStockException ex) {
            goodsStockCache.restore(request.getGoodsId(), request.getNumber() == null ? 0 : request.getNumber());
            log.error("落库时库存不足,已回补缓存,orderId={}", request.getOrderId(), ex);
        }
    }
}
