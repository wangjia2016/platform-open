package com.platform.mall.service.order.impl;

import com.platform.common.enums.MallCacheKey;
import com.platform.common.enums.OrderStatusEnum;
import com.platform.common.exception.BusinessException;
import com.platform.common.result.Result;
import com.platform.common.util.DateTimeUtil;
import com.platform.common.util.JsonUtil;
import com.platform.common.util.SnowFlakeIdGenerator;
import com.platform.common.util.UUIDUtils;
import com.platform.mall.dao.basic.MallOrderStatusConstant;
import com.platform.mall.dao.basic.NotifyManagerDto;
import com.platform.mall.dao.goods.convert.MallGoodsConverter;
import com.platform.mall.dao.goods.mapper.MallGoodsMapper;
import com.platform.mall.dao.goods.model.detail.MallGoodsDetailDto;
import com.platform.mall.dao.order.convert.MallOrderDeliveryConverter;
import com.platform.mall.dao.order.convert.MallOrderItemConverter;
import com.platform.mall.dao.order.entity.MallOrderDelivery;
import com.platform.mall.dao.order.entity.MallOrderItem;
import com.platform.mall.dao.order.mapper.MallOrderDeliveryMapper;
import com.platform.mall.dao.order.mapper.MallOrderItemMapper;
import com.platform.mall.dao.order.model.detail.MallOrderDeliveryDetailDto;
import com.platform.mall.dao.order.model.list.MallOrderItemListDto;
import com.platform.mall.dao.order.model.query.*;
import com.platform.mall.manager.cache.MallGoodsCacheManager;
import com.platform.mall.service.order.MallOrderDeliveryService;
import com.platform.mall.service.order.MallOrderItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.platform.mall.dao.order.mapper.MallOrderMapper;
import com.platform.mall.dao.order.entity.MallOrder;
import com.platform.mall.service.order.MallOrderService;
import com.platform.mall.dao.order.model.detail.MallOrderDetailDto;
import com.platform.mall.dao.order.convert.MallOrderConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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

    private final RedisTemplate redisTemplate;

    private final RedissonClient redissonClient;
    
    private final SnowFlakeIdGenerator snowFlakeIdGenerator;

    private final RocketMQTemplate rocketMQTemplate;

    private final MallGoodsMapper mallGoodsMapper;

    private final MallGoodsCacheManager mallGoodsCacheManager;

    private final ExecutorService consumerQueueThreadPool;

    @Override
    public MallOrderDetailDto getOne(MallOrderQuery mallOrderQuery) {
        MallOrder mallOrder = this.getOne(new QueryWrapper<MallOrder>().lambda()
                        .eq(MallOrder::getTenantId,mallOrderQuery.getTenantId()),
                true);
        return MallOrderConverter.INSTANCE.toDTO(mallOrder);
    }

    @Override
    public MallOrderDetailDto orderDetail(Long id) {
        MallOrder mallOrder = mallOrderMapper.selectById(id);
        //查询订单项
        List<MallOrderItem> mallOrderItems = mallOrderItemMapper.selectList(new QueryWrapper<MallOrderItem>().lambda()
                .in(MallOrderItem::getOrderId, id));
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

        //创建订单项对象
        final Long goodsId = mallOrderFlashRequestQuery.getGoodsId();
        final Integer number = mallOrderFlashRequestQuery.getNumber();
        final MallOrder mallOrder = MallOrderConverter.INSTANCE.fromMallOrderFlashRequestQuery(mallOrderFlashRequestQuery);

        MallOrderItem mallOrderItem =  MallOrderItem.builder()
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
        //更新库存
        mallGoodsMapper.updateGoodsStock(goodsId,number);
        mallOrder.setId(orderId);
        mallOrder.setOrderNo(orderNo);
        mallOrder.setOrderStatus(MallOrderStatusConstant.TO_PAY.getTypeValue());
        mallOrder.setSessionsId(mallOrderFlashRequestQuery.getSessionsId());
        if(mallOrderFlashRequestQuery.getDeliveryType()!=null){
            if(mallOrderFlashRequestQuery.getDeliveryType()==1){
                //实物订单-物流配送
                final MallOrderDelivery mallOrderDelivery = MallOrderDeliveryConverter.INSTANCE.fromMallOrderFlashRequestQuery(mallOrderFlashRequestQuery);
                mallOrderDelivery.setOrderId(orderId);
                mallOrderDelivery.setOrderNo(orderNo);
                mallOrderDeliveryMapper.insert(mallOrderDelivery);
            }
        }
        // 秒杀订单
        mallOrder.setBusinessType(2);
        final int insert = mallOrderMapper.insert(mallOrder);
        //发送延时消息到MQ
        //destination:tag1||tag2 延时等级 1s 5s 10s 15s 20s 25s 30s 1m 5m 10m 15m 20m 30m 45m 1h 2h 23h 1d 2d 7d
        // 秒杀订单 10分钟未支付 直接取消
        SendResult sendResult = rocketMQTemplate.syncSend("cancel-normal-mall-order:cancelNormalOrderTag",
                MessageBuilder.withPayload(orderId).setHeader(RocketMQHeaders.KEYS, orderId).build(),5000,10);
        log.info("cancel-normal-mall-order 延时消息发送:",sendResult.toString());
        //发送支付提醒消息到MQ 提前一个小时提醒
        SendResult payResult = rocketMQTemplate.syncSend("mall-order-to-pay-notify:orderToPayNotifyTag",
                MessageBuilder.withPayload(orderId).setHeader(RocketMQHeaders.KEYS, orderId).build(),5000,17);
        log.info("normal-mall-order-to-pay 待支付提醒消息发送:",payResult.toString());
        if(insert>0){
            return true;
        }
        return false;
    }

    /*
     * 检查秒杀参数
     */
    public void checkOrderParam(MallOrderRequestQuery mallOrderRequestQuery){
        // 入参校验
        if (mallOrderRequestQuery == null) {
            log.info("秒杀下单请求参数mallOrderFlashRequestQuery为空,返回下单失败");
            throw new BusinessException("秒杀下单请求参数mallOrderFlashRequestQuery为空,返回下单失败");
        }
        if (log.isDebugEnabled()) {
            log.info("秒杀下单请求参数mallOrderRequestQuery=[{}].", JsonUtil.object2Json(mallOrderRequestQuery));
        }

        if (mallOrderRequestQuery.getSessionsId()==null) {
            log.info("秒杀下单必要参数[sessionsId]不能为空,下单失败");
            throw new BusinessException("下单必要参数[sessionsId]不能为空,下单失败");
        }
        if(mallOrderRequestQuery.getMemberId()==null){
            log.info("秒杀下单必要参数[memberId]不能为空,下单失败");
            throw new BusinessException("下单必要参数[memberId]不能为空,下单失败");
        }
        if (StringUtils.isBlank(mallOrderRequestQuery.getAppId())) {
            log.info("秒杀下单必要参数[appId]为空,下单失败");
            throw new BusinessException("秒杀下单必要参数[appId]为空,下单失败");
        }
        if(mallOrderRequestQuery.getGoodsId() ==null){
            log.info("秒杀下单必要参数[goodsId]为空,下单失败");
            throw new BusinessException("秒杀下单必要参数[goodsId]为空,下单失败");
        }
        if(mallOrderRequestQuery.getNumber() ==null){
            log.info("秒杀下单必要参数[number]为空,下单失败");
            throw new BusinessException("下单必要参数[number]为空,下单失败");
        }
    }

    @Override
    public Boolean checkStock(MallOrderRequestQuery mallOrderRequestQuery){
        //购物车校验
        for(MallCartRequestQuery mallCartRequestQuery:mallOrderRequestQuery.getListMallGoods()) {
            final Long goodsId = mallCartRequestQuery.getGoodsId();
            final Integer number = mallCartRequestQuery.getNumber();
            MallGoodsDetailDto mallGoods = mallGoodsCacheManager.getMallGoods(goodsId);

            Integer stock = (Integer) redisTemplate.opsForValue().get(MallCacheKey.NORMAL_GOODS_SEMAPHORE_PREFIX.getTypeValue() + goodsId);
            //缓存里面数据为空 - 加载数据库？
            if(mallGoods == null){
                log.info("商品为空,下单失败");
                throw new BusinessException("商品为空,下单失败");
            }
            //库存校验
            if(stock<=0||number>stock){
                log.info("商品{},库存不足,下单失败",mallCartRequestQuery.getGoodsName());
                return false;
            }
            // 上下架校验
            // 售卖时间校验
            // 删除校验
        }
        return true;
    }

    @Override
    public Result cancelOrder(Long id, String reason) {
        MallOrder mallOrder = mallOrderMapper.selectById(id);

        if (!MallOrderStatusConstant.TO_PAY.getTypeValue().equals(mallOrder.getOrderStatus())) {
            //交易关闭
            log.info("取消失败，订单不是处于待付款状态,{}",mallOrder.getOrderStatus());
            return Result.fail("取消失败，订单不是处于待付款状态: "+mallOrder.getOrderStatus());
        }
        mallOrder.setOrderStatus(Integer.valueOf(MallOrderStatusConstant.TRADE_CLOSED.getTypeValue()));
        mallOrder.setCloseReason(reason);
        mallOrderMapper.updateById(mallOrder);
        mallGoodsMapper.updateGoodStockBack(mallOrder.getOrderNo());
        //返回库存
        return Result.success("取消成功");
    }

    @Override
    @Async("consumerQueueThreadPool")
    public void asyncMsgProcess(MallOrder mallOrder) {
        final List<MallOrderItemListDto> mallOrderItemListDtos = mallOrderItemService.listMallOrderItem(MallOrderItemQuery.builder()
                .orderNo(mallOrder.getOrderNo()).build());
        if(CollectionUtils.isEmpty(mallOrderItemListDtos)){
            throw new BusinessException("订单,{}不存在",mallOrder.getId());
        }

        final MallOrderDeliveryDetailDto mallOrderDeliveryDetailDto = mallOrderDeliveryService.getOne(MallOrderDeliveryQuery.builder()
                .orderNo(mallOrder.getOrderNo()).build());
        //发送相关通知-模板消息 异步消息 通知用户 通知管理员
        final NotifyManagerDto build = NotifyManagerDto.builder()
                .orderNo(mallOrder.getOrderNo())
                .appId(mallOrder.getAppId())
                .price(mallOrder.getActualAmount().toString())
                .goodsName(mallOrderItemListDtos.get(0).getGoodsName())
                .address(mallOrderDeliveryDetailDto.getProvince()+mallOrderDeliveryDetailDto.getCity()
                +mallOrderDeliveryDetailDto.getArea()+mallOrderDeliveryDetailDto.getAddress())
                .remark(DateTimeUtil.format(mallOrder.getPayTime(),"yyyy-MM-dd HH:mm:ss"))
                .openid("").build();
        SendResult sendResult = rocketMQTemplate.syncSend("order-pay-success-notify-mamager:orderPayManagerNotify",
                MessageBuilder.withPayload(build).setHeader(RocketMQHeaders.KEYS, UUIDUtils.getUUID32()).build(),5000);
        log.info("order-pay-success-notify-mamager 延时消息发送:",sendResult.getSendStatus());
    }

    @Override
    public Boolean preReduceStock(MallOrderRequestQuery mallOrderFlashRequestQuery) {
        mallOrderFlashRequestQuery.setTenantId(mallOrderFlashRequestQuery.getTenantId());
        Long goodsId = mallOrderFlashRequestQuery.getGoodsId();
        RLock rLock = null;
        try {
            rLock = redissonClient.getLock(MallCacheKey.FLASH_GOODS_LOCK_PREFIX.getTypeValue() + mallOrderFlashRequestQuery.getSessionsId()+"_"+goodsId);
            rLock.lock();
            Integer num = mallOrderFlashRequestQuery.getNumber();
            final MallGoodsDetailDto mallGoods = mallGoodsCacheManager.getMallGoods(goodsId);

            Integer stock = mallGoods.getStock();
            // 首先不能超出普通商品库存
            if (stock <= 0 || num > stock) {
                log.info("秒杀-当前商品库存不足,goodsId={},stock={}", goodsId, stock);
                throw new BusinessException("秒杀-预扣减库存-商品{},库存不足,下单失败", mallGoods.getName());
            }
            //更新缓存里面的库存数
            mallGoods.setStock(stock - num);
            CompletableFuture<Void> normalStockFuture = CompletableFuture.runAsync(()->{
                // 普通商品库存减 num
                redisTemplate.opsForValue().set(MallCacheKey.NORMAL_GOODS_PREFIX.getTypeValue() + goodsId, MallGoodsConverter.INSTANCE.fromDTO(mallGoods), 7200, TimeUnit.SECONDS);
            },consumerQueueThreadPool);

            CompletableFuture.allOf(normalStockFuture).get();
            Long orderId = snowFlakeIdGenerator.nextId();
            //发送消息到MQ 這裡購物車裡面應該是發送一條消息，不應該是多條消息
            mallOrderFlashRequestQuery.setOrderId(orderId);
            SendResult sendResult = rocketMQTemplate.syncSend("create-flash-mall-order:createFlashOrderTag",
                    MessageBuilder.withPayload(JsonUtil.object2Json(mallOrderFlashRequestQuery)).setHeader(RocketMQHeaders.KEYS, orderId).build());
            log.info(sendResult.toString());
            //发送成功
            if (sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
                return true;
            }
        }catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException(e.getMessage());
        }finally {
            rLock.unlock();
        }
        return false;
    }

    @Override
    public Boolean preCreateOrder(MallOrderRequestQuery mallOrderRequestQuery) {
        //幂等性处理 - 防重放攻击
        this.checkOrderParam(mallOrderRequestQuery);
        CompletableFuture<Void> checkFlashSessionFuture = CompletableFuture.runAsync(()->{
            // 秒杀场次校验
            //this.checkFlashSession(mallOrderRequestQuery);
        },consumerQueueThreadPool);
        AtomicReference<MallGoodsDetailDto> mallGoodsDetailDto = new AtomicReference<>();
        CompletableFuture<Void> checkGoodsValidatorFuture = CompletableFuture.runAsync(()->{
            // 商品合法性校验
            mallGoodsDetailDto.set(this.checkGoodsValidator(mallOrderRequestQuery));
        },consumerQueueThreadPool);
        try {
            CompletableFuture.allOf(checkFlashSessionFuture,checkGoodsValidatorFuture).get();
        } catch (Exception e) {
            log.error("preCreateOrder error:{}",e.getMessage());
            throw new BusinessException(e.getMessage());
        }
        // 预扣减库存
        return this.preReduceStock(mallOrderRequestQuery);
    }

    /**
     * 商品合法性校验
     */
    public MallGoodsDetailDto checkGoodsValidator(MallOrderRequestQuery mallOrderFlashRequestQuery){
        final Integer number = mallOrderFlashRequestQuery.getNumber();
        MallGoodsDetailDto mallGoods = mallGoodsCacheManager.getMallGoods(mallOrderFlashRequestQuery.getGoodsId());
        //缓存里面数据为空 - 加载数据库？
        if(Objects.isNull(mallGoods)){
            log.info("商品为空,下单失败");
            throw new BusinessException("商品为空,下单失败");
        }
        // 如果商品未上架
        if(mallGoods.getOnUnSale() == 1){
            log.info("商品已下架,下单失败");
            throw new BusinessException("商品已下架,下单失败");
        }
        // 如果商品已删除
        if(mallGoods.getIsDelete()==1){
            log.info("商品已删除,下单失败");
            throw new BusinessException("商品已删除,下单失败");
        }
        //库存校验
        if(mallGoods.getStock()<=0||number>mallGoods.getStock()){
            //商品名称取缓存的值？
            log.info("商品{},库存不足,下单失败",mallGoods.getName());
            throw new BusinessException("商品合法性校验-商品{},库存不足,下单失败",mallGoods.getName());
        }
        return mallGoods;
    }

    @Override
    public Boolean changOrderState(Long orderId, OrderStatusEnum beforeStatus, OrderStatusEnum afterStatus) {
        return mallOrderMapper.changOrderState(orderId,beforeStatus,afterStatus);
    }

}