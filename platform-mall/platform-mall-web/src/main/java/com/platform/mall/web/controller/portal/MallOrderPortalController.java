package com.platform.mall.web.controller.portal;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.platform.common.annotation.CheckIdempotent;
import com.platform.common.auth.CheckAuthentication;
import com.platform.common.idempotent.IdempotentKeys;
import com.platform.common.result.Result;
import com.platform.common.util.UUIDUtils;
import com.platform.mall.dao.order.model.detail.MallOrderDetailDto;
import com.platform.mall.dao.order.model.query.MallOrderRequestQuery;
import com.platform.mall.service.order.MallOrderService;
import com.platform.mall.web.config.OrderSentinelConfiguration;
import com.platform.mall.web.support.CurrentMember;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

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

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * @Description 提交订单 幂等性校验
     **/
    @PostMapping("/createOrder")
    @ApiOperation("提交订单")
    @CheckAuthentication
    @CheckIdempotent(true)
    @SentinelResource(value = OrderSentinelConfiguration.CREATE_ORDER_RESOURCE, blockHandler = "createOrderBlocked")
    public Result<MallOrderDetailDto> createOrder(@Valid @RequestBody MallOrderRequestQuery mallOrderRequest) {
        mallOrderRequest.setMemberId(CurrentMember.requireId());
        final Boolean result = mallOrderService.preCreateOrder(mallOrderRequest);
        if (result) {
            return Result.success("您的订单处理中，请稍后支付");
        }
        return Result.fail("下单异常");
    }

    public Result<MallOrderDetailDto> createOrderBlocked(MallOrderRequestQuery mallOrderRequest, BlockException ex) {
        log.warn("下单被限流,{}", ex.getMessage());
        return Result.fail("当前下单人数过多，请稍后再试");
    }

    @ApiOperation("订单状态")
    @GetMapping("/orderStatus")
    public Result<MallOrderDetailDto> orderStatus(@RequestParam(value = "id") Long id) {
        MallOrderDetailDto detail = mallOrderService.orderDetail(id, CurrentMember.requireId());
        if (detail == null) {
            return Result.fail("订单不存在");
        }
        return Result.success(detail);
    }

    @ApiOperation("订单详情")
    @GetMapping("/orderDetail")
    public Result<MallOrderDetailDto> orderDetail(@RequestParam(value = "id") Long id) {
        MallOrderDetailDto mallOrder = mallOrderService.orderDetail(id, CurrentMember.requireId());
        if (mallOrder == null) {
            return Result.fail("订单不存在");
        }
        return Result.success(mallOrder);
    }

    @ApiOperation("取消订单")
    @PostMapping("/cancelOrder")
    public Result cancelOrder(@RequestParam(value = "id") Long id,
                              @RequestParam(value = "reason") String reason) {
        return mallOrderService.cancelOrder(id, CurrentMember.requireId(), reason);
    }

    /**
     * 获取幂等性 token，5 分钟内有效，并且只属于当前会员。
     */
    @GetMapping("/getIdempotent")
    public Result getIdempotent() {
        Long memberId = CurrentMember.requireId();
        String token = UUIDUtils.getUUID32();
        stringRedisTemplate.opsForValue().set(
                IdempotentKeys.key(memberId, token),
                "1",
                IdempotentKeys.TTL_MINUTES,
                TimeUnit.MINUTES);
        return Result.success(token);
    }

}
