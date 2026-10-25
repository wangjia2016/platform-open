package com.platform.mall.service.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.result.Result;
import com.platform.common.util.DateTimeUtil;
import com.platform.mall.dao.basic.MallOrderStatusConstant;

import com.platform.mall.dao.basic.PayConsts;
import com.platform.mall.dao.order.entity.MallOrder;
import com.platform.mall.dao.order.mapper.MallOrderItemMapper;
import com.platform.mall.dao.order.mapper.MallOrderMapper;
import com.platform.mall.dao.order.model.list.WxPayOrderNotifyResult;
import com.platform.mall.dao.order.model.query.MallOrderPayPortalRequest;
import com.platform.mall.service.feignClient.WechatFeignClient;
import com.platform.mall.service.order.MallOrderPayService;
import com.platform.mall.service.order.MallOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @Description
 * @ClassName MallOrderPayServiceImpl
 * @Author wangjia
 * @date 2020.11.08 20:39
 */
@Slf4j
@Service("mallOrderPayService")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MallOrderPayServiceImpl implements MallOrderPayService {

    private final MallOrderMapper mallOrderMapper;

    private final WechatFeignClient wechatFeignClient;

    private final MallOrderItemMapper mallOrderItemMapper;

    private final MallOrderService mallOrderService;

    @Override
    public Result orderPay(MallOrderPayPortalRequest mallOrderPayPortalRequest, Long memberId) {

        final MallOrder mallOrder = mallOrderMapper.selectById(mallOrderPayPortalRequest.getOrderId());

        if (mallOrder == null || memberId == null || !memberId.equals(mallOrder.getMemberId())) {
            log.error("订单不存在或已删除,请检查");
            return Result.fail("订单不存在或已删除,请检查");
        }

        if(!MallOrderStatusConstant.TO_PAY.getTypeValue().equals(mallOrder.getOrderStatus())){
            log.error("订单状态非法,{}",mallOrder.getOrderStatus());
            return Result.fail("订单状态非法");
        }

        Result result = validatorOrderPrice(mallOrder,mallOrderPayPortalRequest);
        if(result!=null&&result.getCode()!=1){
            //校验失败
            return result;
        }
        //支付中
        mallOrder.setPayStatus(2);
        mallOrderMapper.updateById(mallOrder);
        if(PayConsts.PAY_WECHAT.equals(mallOrderPayPortalRequest.getPayType())){
            //微信支付
            mallOrderPayPortalRequest.setOrderNo(mallOrder.getOrderNo());
            return wechatFeignClient.wxPay(mallOrderPayPortalRequest);
        }else{
            return Result.fail("不支持的支付方式");
        }
    }

    @Override
    public Boolean notifyOrderProcess(WxPayOrderNotifyResult wxPayOrderNotifyResult) {
        Long orderNo;
        try {
            orderNo = Long.valueOf(wxPayOrderNotifyResult.getOutTradeNo());
        } catch (NumberFormatException ex) {
            log.error("支付回调订单号非法,outTradeNo={}", wxPayOrderNotifyResult.getOutTradeNo());
            return false;
        }
        final MallOrder mallOrder = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>().lambda()
                .eq(MallOrder::getOrderNo, orderNo));
        if (mallOrder == null) {
            log.error("支付回调订单不存在,outTradeNo={}", wxPayOrderNotifyResult.getOutTradeNo());
            return false;
        }
        if (!MallOrderStatusConstant.TO_PAY.getTypeValue().equals(mallOrder.getOrderStatus())) {
            log.info("支付回调重复或状态已变化,orderNo={},status={}", mallOrder.getOrderNo(), mallOrder.getOrderStatus());
            return true;
        }
        mallOrder.setOrderStatus(MallOrderStatusConstant.TO_DELIVERY.getTypeValue());
        mallOrder.setTransactionId(wxPayOrderNotifyResult.getTransactionId());
        mallOrder.setPayTime(DateTimeUtil.parseDate(wxPayOrderNotifyResult.getTimeEnd(), "yyyyMMddHHmmss"));
        mallOrder.setPayStatus(0);
        int rows = mallOrderMapper.update(null, new UpdateWrapper<MallOrder>()
                .eq("id", mallOrder.getId())
                .eq("order_status", MallOrderStatusConstant.TO_PAY.getTypeValue())
                .set("order_status", mallOrder.getOrderStatus())
                .set("transaction_id", mallOrder.getTransactionId())
                .set("pay_time", mallOrder.getPayTime())
                .set("pay_status", mallOrder.getPayStatus()));
        if (rows == 0) {
            log.info("支付回调并发重复,orderNo={}", mallOrder.getOrderNo());
            return true;
        }
        mallOrderService.asyncMsgProcess(mallOrder);
        return true;
    }

    /**
     * 服务端校验订单价格
     * */
    private Result validatorOrderPrice(MallOrder mallOrder,MallOrderPayPortalRequest mallOrderPayPortalRequest) {

        if(mallOrder.getActualAmount().compareTo(mallOrderPayPortalRequest.getTotalAmount())!=0){
            //比较是否相等-实际支付价格
            return  Result.fail("订单价格非法,请检查");
        }
        if(mallOrder.getActualAmount().compareTo(new BigDecimal("0.01"))==-1){
            return  Result.fail("订单价格不能小于0.01,请检查");
        }

        if(mallOrder.getTotalAmount().compareTo(BigDecimal.ZERO)==0){
            return  Result.fail("订单价格不能等于0,请检查");
        }
        return Result.success("校验通过");
    }
}
