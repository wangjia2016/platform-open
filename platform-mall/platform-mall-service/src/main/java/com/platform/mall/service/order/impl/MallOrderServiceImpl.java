package com.platform.mall.service.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.enums.OrderStatusEnum;
import com.platform.common.exception.BusinessException;
import com.platform.common.exception.InsufficientStockException;
import com.platform.common.result.Result;
import com.platform.common.util.DateTimeUtil;
import com.platform.common.util.JsonUtil;
import com.platform.common.util.SnowFlakeIdGenerator;
import com.platform.common.util.UUIDUtils;
import com.platform.mall.dao.basic.MallOrderStatusConstant;
import com.platform.mall.dao.basic.NotifyManagerDto;
import com.platform.mall.dao.goods.mapper.MallGoodsMapper;
import com.platform.mall.dao.goods.model.detail.MallGoodsDetailDto;
import com.platform.mall.dao.order.convert.MallOrderConverter;
import com.platform.mall.dao.order.convert.MallOrderDeliveryConverter;
import com.platform.mall.dao.order.convert.MallOrderItemConverter;
import com.platform.mall.dao.order.entity.MallOrder;
import com.platform.mall.dao.order.entity.MallOrderDelivery;
import com.platform.mall.dao.order.entity.MallOrderItem;
import com.platform.mall.dao.order.mapper.MallOrderDeliveryMapper;
import com.platform.mall.dao.order.mapper.MallOrderItemMapper;
import com.platform.mall.dao.order.mapper.MallOrderMapper;
import com.platform.mall.dao.order.model.detail.MallOrderDeliveryDetailDto;
import com.platform.mall.dao.order.model.detail.MallOrderDetailDto;
import com.platform.mall.dao.order.model.list.MallOrderItemListDto;
import com.platform.mall.dao.order.model.query.MallCartRequestQuery;
import com.platform.mall.dao.order.model.query.MallOrderDeliveryQuery;
import com.platform.mall.dao.order.model.query.MallOrderItemQuery;
import com.platform.mall.dao.order.model.query.MallOrderQuery;
import com.platform.mall.dao.order.model.query.MallOrderRequestQuery;
import com.platform.mall.manager.cache.GoodsStockCache;
import com.platform.mall.manager.cache.MallGoodsCacheManager;
import com.platform.mall.service.flash.MallFlashSessionsService;
import com.platform.mall.service.order.MallOrderDeliveryService;
import com.platform.mall.service.order.MallOrderItemService;
import com.platform.mall.service.order.MallOrderMq;
import com.platform.mall.service.order.MallOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author Administrator
 */
@Slf4j
@Service("mallOrderService")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MallOrderServiceImpl extends ServiceImpl<MallOrderMapper, MallOrder> implements MallOrderService {

    public final MallOrderItemMapper mallOrderItemMapper;

    public final MallOrderItemService mallOrderItemService;

    public final MallOrderMapper mallOrderMapper;

    private final MallOrderDeliveryMapper mallOrderDeliveryMapper;

    private final MallOrderDeliveryService mallOrderDeliveryService;

    private final SnowFlakeIdGenerator snowFlakeIdGenerator;

    private final RocketMQTemplate rocketMQTemplate;

    private final MallGoodsMapper mallGoodsMapper;

    private final MallGoodsCacheManager mallGoodsCacheManager;

    private final GoodsStockCache goodsStockCache;

    private final MallFlashSessionsService mallFlashSessionsService;

    @Override
    public MallOrderDetailDto getOne(MallOrderQuery mallOrderQuery) {
        if (mallOrderQuery == null || (mallOrderQuery.getId() == null && mallOrderQuery.getOrderNo() == null)) {
            throw new BusinessException("查询订单缺少 id 或 orderNo");
        }
        MallOrder mallOrder = this.getOne(new QueryWrapper<MallOrder>().lambda()
                .eq(mallOrderQuery.getId() != null, MallOrder::getId, mallOrderQuery.getId())
                .eq(mallOrderQuery.getOrderNo() != null, MallOrder::getOrderNo, mallOrderQuery.getOrderNo())
                .eq(mallOrderQuery.getMemberId() != null, MallOrder::getMemberId, mallOrderQuery.getMemberId())
                .eq(StringUtils.isNotBlank(mallOrderQuery.getTenantId()), MallOrder::getTenantId, mallOrderQuery.getTenantId()),
                false);
        return MallOrderConverter.INSTANCE.toDTO(mallOrder);
    }

    @Override
    public MallOrderDetailDto orderDetail(Long id, Long memberId) {
        MallOrder mallOrder = mallOrderMapper.selectById(id);
        if (mallOrder == null || memberId == null || !memberId.equals(mallOrder.getMemberId())) {
            return null;
        }
        List<MallOrderItem> mallOrderItems = mallOrderItemMapper.selectList(new QueryWrapper<MallOrderItem>().lambda()
                .eq(MallOrderItem::getOrderId, id));
        MallOrderDetailDto orderDetail = MallOrderConverter.INSTANCE.toDTO(mallOrder);
        List<MallOrderItemListDto> mallOrderItemListDtos = MallOrderItemConverter.INSTANCE.toListDTO(mallOrderItems);
        orderDetail.setListMallOrderItem(mallOrderItemListDtos);
        return orderDetail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean createOrder(MallOrderRequestQuery mallOrderFlashRequestQuery) {
        Long orderNo = snowFlakeIdGenerator.nextId();
        final Long orderId = mallOrderFlashRequestQuery.getOrderId();
        final Long goodsId = mallOrderFlashRequestQuery.getGoodsId();
        final Integer number = mallOrderFlashRequestQuery.getNumber();

        Integer updated = mallGoodsMapper.updateGoodsStock(goodsId, number);
        if (updated == null || updated == 0) {
            throw new InsufficientStockException("商品库存不足,goodsId=" + goodsId);
        }

        final MallOrder mallOrder = MallOrderConverter.INSTANCE.fromMallOrderFlashRequestQuery(mallOrderFlashRequestQuery);
        MallOrderItem mallOrderItem = MallOrderItem.builder()
                .appId(mallOrderFlashRequestQuery.getAppId())
                .openid(mallOrderFlashRequestQuery.getOpenid())
                .memberId(mallOrderFlashRequestQuery.getMemberId())
                .orderId(orderId)
                .orderNo(orderNo)
                .goodsId(goodsId)
                .number(number)
                .distributorId(mallOrderFlashRequestQuery.getDistributorId())
                .tenantId(mallOrderFlashRequestQuery.getTenantId()).build();
        mallOrderItemMapper.insert(mallOrderItem);
        mallOrder.setId(orderId);
        mallOrder.setOrderNo(orderNo);
        mallOrder.setOrderStatus(MallOrderStatusConstant.TO_PAY.getTypeValue());
        mallOrder.setSessionsId(mallOrderFlashRequestQuery.getSessionsId());
        if (mallOrderFlashRequestQuery.getDeliveryType() != null && mallOrderFlashRequestQuery.getDeliveryType() == 1) {
            final MallOrderDelivery mallOrderDelivery = MallOrderDeliveryConverter.INSTANCE.fromMallOrderFlashRequestQuery(mallOrderFlashRequestQuery);
            mallOrderDelivery.setOrderId(orderId);
            mallOrderDelivery.setOrderNo(orderNo);
            mallOrderDeliveryMapper.insert(mallOrderDelivery);
        }
        mallOrder.setBusinessType(2);
        int insert = mallOrderMapper.insert(mallOrder);
        scheduleAfterCommit(orderId);
        return insert > 0;
    }

    private void scheduleAfterCommit(Long orderId) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    sendOrderTimers(orderId);
                }
            });
            return;
        }
        sendOrderTimers(orderId);
    }

    private void sendOrderTimers(Long orderId) {
        sendDelay(MallOrderMq.CANCEL_DESTINATION, orderId, MallOrderMq.UNPAID_CANCEL_DELAY_LEVEL);
        sendDelay(MallOrderMq.PAY_REMIND_DESTINATION, orderId, MallOrderMq.PAY_REMIND_DELAY_LEVEL);
    }

    private void sendDelay(String destination, Long orderId, int delayLevel) {
        try {
            SendResult sendResult = rocketMQTemplate.syncSend(destination,
                    MessageBuilder.withPayload(orderId).setHeader(RocketMQHeaders.KEYS, orderId).build(),
                    5000, delayLevel);
            log.info("延时消息已发送 destination={} delayLevel={} result={}", destination, delayLevel, sendResult);
        } catch (Exception ex) {
            log.error("延时消息发送失败 destination={} orderId={}", destination, orderId, ex);
        }
    }

    public void checkOrderParam(MallOrderRequestQuery mallOrderRequestQuery) {
        if (mallOrderRequestQuery == null) {
            throw new BusinessException("秒杀下单请求参数为空,返回下单失败");
        }
        if (log.isDebugEnabled()) {
            log.info("秒杀下单请求参数 mallOrderRequestQuery={}", JsonUtil.object2Json(mallOrderRequestQuery));
        }
        if (mallOrderRequestQuery.getSessionsId() == null) {
            throw new BusinessException("下单必要参数[sessionsId]不能为空,下单失败");
        }
        if (mallOrderRequestQuery.getMemberId() == null) {
            throw new BusinessException("下单必要参数[memberId]不能为空,下单失败");
        }
        if (StringUtils.isBlank(mallOrderRequestQuery.getAppId())) {
            throw new BusinessException("秒杀下单必要参数[appId]为空,下单失败");
        }
        if (mallOrderRequestQuery.getGoodsId() == null) {
            throw new BusinessException("秒杀下单必要参数[goodsId]为空,下单失败");
        }
        if (mallOrderRequestQuery.getNumber() == null || mallOrderRequestQuery.getNumber() <= 0) {
            throw new BusinessException("下单必要参数[number]非法,下单失败");
        }
    }

    @Override
    public Boolean checkStock(MallOrderRequestQuery mallOrderRequestQuery) {
        if (mallOrderRequestQuery.getListMallGoods() == null) {
            return false;
        }
        for (MallCartRequestQuery mallCartRequestQuery : mallOrderRequestQuery.getListMallGoods()) {
            final Long goodsId = mallCartRequestQuery.getGoodsId();
            final Integer number = mallCartRequestQuery.getNumber();
            MallGoodsDetailDto mallGoods = mallGoodsCacheManager.getMallGoods(goodsId);
            if (mallGoods == null) {
                throw new BusinessException("商品为空,下单失败");
            }
            Integer stock = goodsStockCache.getStock(goodsId);
            if (stock == null || number == null || stock <= 0 || number > stock) {
                log.info("商品{},库存不足,下单失败", mallCartRequestQuery.getGoodsName());
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result cancelOrder(Long id, Long memberId, String reason) {
        MallOrder mallOrder = mallOrderMapper.selectById(id);
        if (mallOrder == null || (memberId != null && !memberId.equals(mallOrder.getMemberId()))) {
            return Result.fail("订单不存在");
        }
        if (!MallOrderStatusConstant.TO_PAY.getTypeValue().equals(mallOrder.getOrderStatus())) {
            log.info("取消失败，订单不是处于待付款状态,{}", mallOrder.getOrderStatus());
            return Result.fail("取消失败，订单不是处于待付款状态: " + mallOrder.getOrderStatus());
        }
        int rows = mallOrderMapper.update(null, new UpdateWrapper<MallOrder>()
                .eq("id", id)
                .eq("order_status", MallOrderStatusConstant.TO_PAY.getTypeValue())
                .set("order_status", MallOrderStatusConstant.TRADE_CLOSED.getTypeValue())
                .set("close_reason", reason));
        if (rows == 0) {
            return Result.fail("取消失败，订单不是处于待付款状态");
        }
        Integer restored = mallGoodsMapper.updateGoodStockBack(mallOrder.getOrderNo());
        if (restored == null || restored == 0) {
            log.warn("取消订单时数据库库存未回补,orderNo={}", mallOrder.getOrderNo());
        }
        List<MallOrderItem> items = mallOrderItemMapper.selectList(new QueryWrapper<MallOrderItem>().lambda()
                .eq(MallOrderItem::getOrderId, id));
        restoreCacheAfterCommit(items);
        return Result.success("取消成功");
    }

    private void restoreCacheAfterCommit(List<MallOrderItem> items) {
        Runnable restore = () -> {
            if (CollectionUtils.isEmpty(items)) {
                return;
            }
            for (MallOrderItem item : items) {
                goodsStockCache.restore(item.getGoodsId(), item.getNumber() == null ? 0 : item.getNumber());
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    restore.run();
                }
            });
            return;
        }
        restore.run();
    }

    @Override
    @Async("consumerQueueThreadPool")
    public void asyncMsgProcess(MallOrder mallOrder) {
        final List<MallOrderItemListDto> mallOrderItemListDtos = mallOrderItemService.listMallOrderItem(MallOrderItemQuery.builder()
                .orderNo(mallOrder.getOrderNo()).build());
        if (CollectionUtils.isEmpty(mallOrderItemListDtos)) {
            throw new BusinessException("订单不存在,id=" + mallOrder.getId());
        }

        final MallOrderDeliveryDetailDto mallOrderDeliveryDetailDto = mallOrderDeliveryService.getOne(MallOrderDeliveryQuery.builder()
                .orderNo(mallOrder.getOrderNo()).build());
        String address = "";
        if (mallOrderDeliveryDetailDto != null) {
            address = StringUtils.defaultString(mallOrderDeliveryDetailDto.getProvince())
                    + StringUtils.defaultString(mallOrderDeliveryDetailDto.getCity())
                    + StringUtils.defaultString(mallOrderDeliveryDetailDto.getArea())
                    + StringUtils.defaultString(mallOrderDeliveryDetailDto.getAddress());
        }
        final NotifyManagerDto build = NotifyManagerDto.builder()
                .orderNo(mallOrder.getOrderNo())
                .appId(mallOrder.getAppId())
                .price(mallOrder.getActualAmount().toString())
                .goodsName(mallOrderItemListDtos.get(0).getGoodsName())
                .address(address)
                .remark(DateTimeUtil.format(mallOrder.getPayTime(), "yyyy-MM-dd HH:mm:ss"))
                .openid("").build();
        SendResult sendResult = rocketMQTemplate.syncSend("order-pay-success-notify-mamager:orderPayManagerNotify",
                MessageBuilder.withPayload(build).setHeader(RocketMQHeaders.KEYS, UUIDUtils.getUUID32()).build(), 5000);
        log.info("order-pay-success-notify-mamager 消息发送:{}", sendResult.getSendStatus());
    }

    @Override
    public Boolean preReduceStock(MallOrderRequestQuery mallOrderFlashRequestQuery) {
        Long goodsId = mallOrderFlashRequestQuery.getGoodsId();
        Integer num = mallOrderFlashRequestQuery.getNumber();
        long left = deductOrSeed(goodsId, num);
        if (left == -1L) {
            throw new InsufficientStockException("秒杀-预扣减库存-商品库存不足,goodsId=" + goodsId);
        }
        if (left < 0L) {
            throw new BusinessException("秒杀-预扣减库存失败,goodsId=" + goodsId);
        }
        Long orderId = snowFlakeIdGenerator.nextId();
        mallOrderFlashRequestQuery.setOrderId(orderId);
        try {
            SendResult sendResult = rocketMQTemplate.syncSend(MallOrderMq.CREATE_FLASH_DESTINATION,
                    MessageBuilder.withPayload(JsonUtil.object2Json(mallOrderFlashRequestQuery))
                            .setHeader(RocketMQHeaders.KEYS, orderId).build());
            log.info("秒杀下单消息发送结果:{}", sendResult);
            if (sendResult != null && SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                return true;
            }
            goodsStockCache.restore(goodsId, num);
            return false;
        } catch (RuntimeException ex) {
            goodsStockCache.restore(goodsId, num);
            log.error("秒杀下单消息发送失败,已回补库存,goodsId={}", goodsId, ex);
            throw new BusinessException("下单消息发送失败");
        }
    }

    private long deductOrSeed(Long goodsId, Integer num) {
        long left = goodsStockCache.tryDeduct(goodsId, num);
        if (left != -2L) {
            return left;
        }
        mallGoodsCacheManager.getMallGoods(goodsId);
        return goodsStockCache.tryDeduct(goodsId, num);
    }

    @Override
    public Boolean preCreateOrder(MallOrderRequestQuery mallOrderRequestQuery) {
        this.checkOrderParam(mallOrderRequestQuery);
        this.checkGoodsValidator(mallOrderRequestQuery);
        mallFlashSessionsService.validateSessionForOrder(
                mallOrderRequestQuery.getSessionsId(),
                mallOrderRequestQuery.getGoodsId());
        return this.preReduceStock(mallOrderRequestQuery);
    }

    public MallGoodsDetailDto checkGoodsValidator(MallOrderRequestQuery mallOrderFlashRequestQuery) {
        final Integer number = mallOrderFlashRequestQuery.getNumber();
        MallGoodsDetailDto mallGoods = mallGoodsCacheManager.getMallGoods(mallOrderFlashRequestQuery.getGoodsId());
        if (mallGoods == null) {
            throw new BusinessException("商品为空,下单失败");
        }
        if (mallGoods.getOnUnSale() != null && mallGoods.getOnUnSale() == 1) {
            throw new BusinessException("商品已下架,下单失败");
        }
        if (mallGoods.getIsDelete() != null && mallGoods.getIsDelete() == 1) {
            throw new BusinessException("商品已删除,下单失败");
        }
        Integer stock = goodsStockCache.getStock(mallGoods.getId());
        if (stock == null || stock <= 0 || number > stock) {
            throw new InsufficientStockException("商品合法性校验-商品库存不足,goodsId=" + mallGoods.getId());
        }
        return mallGoods;
    }

    @Override
    public Boolean changOrderState(Long orderId, OrderStatusEnum beforeStatus, OrderStatusEnum afterStatus) {
        return mallOrderMapper.changOrderState(orderId, beforeStatus, afterStatus);
    }

}
