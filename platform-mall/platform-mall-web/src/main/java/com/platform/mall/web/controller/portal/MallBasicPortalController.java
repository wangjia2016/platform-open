package com.platform.mall.web.controller.portal;

import com.platform.common.result.Result;
import com.platform.common.util.JwtOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @ClassName MallBasicPortalController
 * @Author wangjia
 * @date 2020.10.18 09:37
 */
@RestController
@RequestMapping("api/basic")
@Api(tags = "前端模块配置api")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MallBasicPortalController {

    private final JwtOperator jwtOperator;

    @ApiOperation("获取测试token")
    @GetMapping("/getTestToken")
    public Result getTestToken(){
        // 颁发token
        Map<String, Object> userInfo = new HashMap<>(3);
        userInfo.put("id", 123);
        userInfo.put("nickname", "WJ");
        userInfo.put("openid","oyGl81esbPplX1kZwWocI0Dvb5Mc");

        String token = jwtOperator.generateToken(userInfo);
        return Result.success(token);
    }

}
