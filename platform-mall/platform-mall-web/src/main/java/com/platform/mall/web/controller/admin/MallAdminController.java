package com.platform.mall.web.controller.admin;

import com.platform.common.enums.ThreadPoolNameEnum;
import com.platform.common.result.Result;
import com.platform.mall.service.basic.BasicMallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description
 * @ClassName MallAdminController
 * @Author wangjia
 * @date 2022.05.08 11:48
 */
@RestController
@RequestMapping("admin/basic")
@Api(tags = "管理后台-商城模块配置api")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MallAdminController {

    private final BasicMallService basicMallService;

    @ApiOperation("调整线程池大小")
    @PostMapping("/changeThreadPoolConfig")
    public Result changeThreadPoolConfig(@RequestParam(value="corePoolSize") Integer corePoolSize,
                                         @RequestParam(value="maximumPoolSize") Integer maximumPoolSize,
                                         @RequestParam(value="poolName") String poolName){
        final Boolean aBoolean = basicMallService.updateThreadPoolConfig(corePoolSize, maximumPoolSize, ThreadPoolNameEnum.valueOf(poolName));
        if (aBoolean) {
            Result.success("更新成功");
        }
        return Result.fail("更新失败");
    }

}
