package com.platform.mall.service.order;

import com.platform.common.result.Result;
import com.platform.mall.dao.order.model.list.WxPayOrderNotifyResult;
import com.platform.mall.dao.order.model.query.MallOrderPayPortalRequest;

/**
 * @Description 支付service
 * @Author User
 * @Date 2020.11.08 20:38
 **/
public interface MallOrderPayService {

    /**
     * 发起支付
     * */
    Result orderPay(MallOrderPayPortalRequest mallOrderPayPortalRequest);

    /**
     * 支付之后处理
     * @param wxPayOrderNotifyResult 支付回调参数
     * @return Boolean
     * */
    Boolean notifyOrderProcess(WxPayOrderNotifyResult wxPayOrderNotifyResult);
}
