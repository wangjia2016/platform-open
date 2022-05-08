package com.platform.mall.web.controller.portal;

import com.platform.common.result.Result;
import com.platform.mall.dao.order.model.list.WxPayOrderNotifyResult;
import com.platform.mall.dao.order.model.query.MallOrderPayPortalRequest;
import com.platform.mall.service.order.MallOrderPayService;
import com.platform.mall.service.order.MallOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description 订单-支付
 * @ClassName MallOrderPortalController
 * @Author wangjia
 * @date 2020.10.18 09:34
 */
@Slf4j
@Api(tags = "订单-支付api")
@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MallOrderPayPortalController {

    private final MallOrderPayService mallOrderPayService;

    /**
     * @Description 订单支付
     * @Param id
     * @return Result
     * @Author wangjia
     * @Date 2020-11-08 20:06:55
     **/
    @ApiOperation("订单支付")
    @PostMapping("/orderPay")
    public Result orderPay(@RequestBody MallOrderPayPortalRequest mallOrderPayPortalRequest) {
        return mallOrderPayService.orderPay(mallOrderPayPortalRequest);
    }

    /**
     * @Description: 支付结果通知-异步通知
     * @param
     * @return Result
     * @throws
     * @Author: 王佳
     * @date 2020年11月10日
     */
    @PostMapping("/notifyOrderProcess")
    public Result notifyOrderProcess(@RequestBody WxPayOrderNotifyResult wxPayOrderNotifyResult) {
        log.info("wxPayOrderNotifyResult,{}0",wxPayOrderNotifyResult.toString());
        final Boolean aBoolean = mallOrderPayService.notifyOrderProcess(wxPayOrderNotifyResult);
        if(aBoolean){
            return Result.success("订单处理成功");
        }
        return Result.fail("订单处理异常");
    }
}
