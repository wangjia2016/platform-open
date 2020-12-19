package com.platform.mall.service.feignClient;

import com.platform.common.result.Result;
import com.platform.mall.dao.order.model.query.MallOrderPayPortalRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "platform-wechat")
public interface WechatFeignClient {

    /**
     * 微信支付
     * */
    @PostMapping("/wechat/api/wxPay/wxPay")
    Result wxPay(@RequestBody MallOrderPayPortalRequest mallOrderPayPortalRequest);

}
