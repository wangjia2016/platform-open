package com.platform.mall.web.controller.portal;

import com.platform.common.auth.CheckAuthentication;
import com.platform.common.result.Result;
import com.platform.mall.dao.order.convert.MallOrderConverter;
import com.platform.mall.dao.order.entity.MallOrder;
import com.platform.mall.dao.order.model.detail.MallOrderDetailDto;
import com.platform.mall.dao.order.model.query.MallOrderRequestQuery;
import com.platform.mall.service.order.MallOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Description 订单-前端控制器
 * @ClassName MallOrderPortalController
 * @Author wangjia
 * @date 2020.10.18 09:34
 */
@Slf4j
@Api(tags = "前端订单api")
@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CheckAuthentication
public class MallOrderPortalController {

    private final MallOrderService mallOrderService;

    /**
     * @Description 提交订单
     * @Param mallCartQuery
     * @return Result
     * @Author wangjia
     * @Date 2020-10-14 17:06:55
     **/
    @PostMapping("/createOrder")
    @ApiOperation("提交订单")
    @CheckAuthentication
    public Result<MallOrderDetailDto> createOrder(@Valid @RequestBody MallOrderRequestQuery mallOrderRequest){

        if(!mallOrderService.checkOrderParam(mallOrderRequest)){
            return Result.fail("参数校验失败");
        }
        if(!mallOrderService.checkStock(mallOrderRequest)){
            return Result.fail("商品庫存不足");
        }

        if(!CollectionUtils.isEmpty(mallOrderRequest.getListMallGoods())){
            if(mallOrderService.preReduceStock(mallOrderRequest)){
                // TODO 返回false的情況需要處理
                return Result.success("您的订单处理中，请稍后支付");
            }
        }
        return Result.fail("下單異常");
    }


    /**
     * @Description 订单状态
     * @Param id
     * @return Result
     * @Author wangjia
     * @Date 2020-11-08 17:06:55
     **/
    @ApiOperation("订单状态")
    @GetMapping("/orderStatus")
    public Result<MallOrderDetailDto> orderStatus(@RequestParam(value = "id") Long id){

        return Result.success(MallOrderConverter.INSTANCE.toDTO(mallOrderService.getById(id)));
    }

    /**
     * @Description 查询MallOrder
     * @Param id
     * @return Result
     * @Author wangjia
     * @Date 2020-11-08 17:06:55
     **/
    @ApiOperation("订单详情")
    @GetMapping("/orderDetail")
    public Result<MallOrderDetailDto> orderDetail(@RequestParam(value="id") Long id){
        MallOrderDetailDto mallOrder = mallOrderService.orderDetail(id);
        return Result.success(mallOrder);
    }

    /**
     * @Description 取消订单
     * @Param id
     * @return Result
     * @Author wangjia
     * @Date 2020-11-08 17:06:55
     **/
    @ApiOperation("取消订单")
    @PostMapping("/cancelOrder")
    public Result cancelOrder(@RequestParam(value = "id") Long id,
                              @RequestParam(value = "reason") String reason) {
        return mallOrderService.cancelOrder(id,reason);
    }

}
