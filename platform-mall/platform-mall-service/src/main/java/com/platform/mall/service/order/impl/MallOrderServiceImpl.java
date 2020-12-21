package com.platform.mall.service.order.impl;

import com.platform.common.enums.MallCacheKey;
import com.platform.common.exception.BusinessException;
import com.platform.common.result.Result;
import com.platform.common.util.DateTimeUtil;
import com.platform.common.util.JsonUtil;
import com.platform.common.util.SnowFlakeIdGenerator;
import com.platform.common.util.UUIDUtils;
import com.platform.mall.dao.basic.MallGoodsConstant;
import com.platform.mall.dao.basic.MallOrderStatusConstant;
import com.platform.mall.dao.basic.NotifyManagerDto;
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
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.platform.mall.dao.order.mapper.MallOrderMapper;
import com.platform.mall.dao.order.entity.MallOrder;
import com.platform.mall.service.order.MallOrderService;
import com.platform.mall.dao.order.model.detail.MallOrderDetailDto;
import com.platform.mall.dao.order.convert.MallOrderConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
    public Boolean createOrder(MallOrderRequestQuery mallOrderRequestQuery) {

        List<MallOrderItem> mallOrderItemList = new ArrayList<>();

        // todo 线程不安全的操作 AtomicReference<BigDecimal>
        final BigDecimal[] totalAmount = {new BigDecimal(0)};
        Long orderId = snowFlakeIdGenerator.nextId();
        //创建订单项对象
        Long orderNo = mallOrderRequestQuery.getOrderNo();
        mallOrderRequestQuery.getListMallGoods().forEach(mallCartRequestQuery -> {
            MallGoodsDetailDto mallGoods = (MallGoodsDetailDto) redisTemplate.opsForValue().get(MallCacheKey.NORMAL_GOODS_PREFIX.getTypeValue()+mallCartRequestQuery.getGoodsId());
            final Long goodsId = mallCartRequestQuery.getGoodsId();
            final Integer number = mallCartRequestQuery.getNumber();
            mallOrderItemList.add(MallOrderItem.builder()
                    .appId(mallOrderRequestQuery.getAppId())
                    .openid(mallOrderRequestQuery.getOpenid())
                    .memberId(mallOrderRequestQuery.getMemberId())
                    .orderId(orderId)
                    .orderNo(orderNo)
                    .goodsId(goodsId)
                    .number(number)
                    .distributorId(mallOrderRequestQuery.getDistributorId())
                    .price(mallGoods.getPrice())
                    .goodsName(mallCartRequestQuery.getGoodsName())
                    .goodsLogo(mallCartRequestQuery.getLogoUrl())
                    .tenantId(mallOrderRequestQuery.getTenantId()).build());
            totalAmount[0] = totalAmount[0].add(mallGoods.getPrice().multiply(new BigDecimal(number)));
            //更新库存
            mallGoodsMapper.updateGoodsStock(goodsId,number);
        });

        final MallOrder mallOrder = MallOrderConverter.INSTANCE.fromMallOrderRequestQuery(mallOrderRequestQuery);
        mallOrder.setId(orderId);
        mallOrder.setOrderStatus(1);
        mallOrder.setTotalAmount(totalAmount[0]);
        mallOrder.setGoodsPrice(totalAmount[0]);
        mallOrder.setActualAmount(totalAmount[0]);

        if(mallOrderRequestQuery.getDeliveryType()!=null){
            mallOrder.setGoodsType(MallGoodsConstant.MATERIAL.getTypeValue());
            if(mallOrderRequestQuery.getDeliveryType()==1){
                //实物订单-物流配送
                final MallOrderDelivery mallOrderDelivery = MallOrderDeliveryConverter.INSTANCE.fromMallOrderRequestQuery(mallOrderRequestQuery);
                mallOrderDelivery.setOrderId(orderId);
                mallOrderDeliveryMapper.insert(mallOrderDelivery);
            }
        }else{
            //虚拟商品
            mallOrder.setGoodsType(MallGoodsConstant.VIRTUAL.getTypeValue());
        }
        mallOrder.setBusinessType(1);
        final int insert = mallOrderMapper.insert(mallOrder);
        Boolean batchResult = mallOrderItemService.saveBatch(mallOrderItemList);

        //发送延时消息到MQ
        //设置重试次数 rocketMQTemplate.getProducer().setRetryTimesWhenSendFailed();
        //destination:tag1||tag2 延时等级 1s 5s 10s 15s 20s 25s 30s 1m 5m 10m 15m 20m 30m 45m 1h 2h 23h 1d 2d 7d
        SendResult sendResult = rocketMQTemplate.syncSend("cancel-normal-mall-order:cancelNormalOrderTag",
                MessageBuilder.withPayload(orderId).setHeader(RocketMQHeaders.KEYS, orderId).build(),5000,18);
        log.info("cancel-normal-mall-order 延时消息发送:",sendResult.toString());

        if(batchResult&&insert>0){
            return true;
        }
        return false;
    }

    @Override
    public Boolean checkOrderParam(MallOrderRequestQuery mallOrderRequestQuery){
        // 入参校验
        if (mallOrderRequestQuery == null) {
            log.info("下单请求参数mallOrderRequestQuery为空,返回下单失败");
            return false;
        }
        log.info("下单请求参数mallOrderRequestQuery=[{}].", JsonUtil.object2Json(mallOrderRequestQuery));

        if(mallOrderRequestQuery.getMemberId()==null&&StringUtils.isBlank(mallOrderRequestQuery.getOpenid())){
            log.info("下单必要参数[memberId、openid]不能同时为空,下单失败");
            return false;
        }
        if (StringUtils.isBlank(mallOrderRequestQuery.getAppId())) {
            log.info("下单必要参数[appId]为空,下单失败");
            return false;
        }
        //购物车校验
        for(MallCartRequestQuery mallCartRequestQuery:mallOrderRequestQuery.getListMallGoods()) {
            final Long goodsId = mallCartRequestQuery.getGoodsId();
            if(goodsId ==null){
                log.info("下单必要参数[goodsId]为空,下单失败");
                return false;
            }
            final Integer number = mallCartRequestQuery.getNumber();
            if(number ==null){
                log.info("下单必要参数[number]为空,下单失败");
                return false;
            }
        }
        return true;
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
    public Boolean preReduceStock(MallOrderRequestQuery mallOrderRequestQuery) {

        mallOrderRequestQuery.setTenantId(mallOrderRequestQuery.getTenantId());
        for(MallCartRequestQuery mallCartRequestQuery:mallOrderRequestQuery.getListMallGoods()) {
            MallGoodsDetailDto mallGoods = mallGoodsCacheManager.getMallGoods(mallCartRequestQuery.getGoodsId());

            Long goodsId = mallGoods.getId();
            Integer num =  mallCartRequestQuery.getNumber();
            final Integer stock = mallGoods.getStock();
            if (stock <= 0) {
                log.info("当前商品库存不足,goodsId={},stock={}", goodsId, stock);
                return false;
            }
            //RedissonMultiLock  處理？
            //设置信号量 - 库存数
            RSemaphore semaphore = redissonClient.getSemaphore(MallCacheKey.NORMAL_GOODS_SEMAPHORE_PREFIX.getTypeValue()+goodsId);
            log.info("availablePermits,{}",semaphore.availablePermits());
            if(semaphore.availablePermits()==0){
                log.info("goodsId={} 预减库存失败，信号量为0", goodsId);
                return false;
            }
            boolean b = semaphore.tryAcquire(num);
            //尝试获取 如果能获取到信号量的值 则返回true
            //获取不到，则尝试重新获取 如果信号量已经是0 则返回false
            if(b){

            }else{
                log.info("goodsId={} 预减库存失败!", goodsId);
                //semaphore.release(num);
                return false;
            }
        }
        log.info("秒杀成功!");
        //生成订单号
        Long orderNo = snowFlakeIdGenerator.nextId();
        //创建订单对象
        //发送消息到MQ 這裡購物車裡面應該是發送一條消息，不應該是多條消息
        mallOrderRequestQuery.setOrderNo(orderNo);
        SendResult sendResult = rocketMQTemplate.syncSend("create-normal-mall-order:createNormalOrderTag",
                MessageBuilder.withPayload(JsonUtil.object2Json(mallOrderRequestQuery)).setHeader(RocketMQHeaders.KEYS, orderNo).build());

        log.info(sendResult.toString());
        //发送成功
        if(sendResult.getSendStatus().equals(SendStatus.SEND_OK)){
            return true;
        }else{
            //发送失败，需要做业务补偿，重新发送
            return false;
        }
    }
}