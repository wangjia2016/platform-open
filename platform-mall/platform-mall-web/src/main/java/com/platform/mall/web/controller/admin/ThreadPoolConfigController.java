package com.platform.mall.web.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.platform.common.result.Result;
import com.platform.mall.dao.threadpool.convert.ThreadPoolConfigConverter;
import com.platform.mall.dao.threadpool.entity.ThreadPoolConfig;
import com.platform.mall.dao.threadpool.model.detail.ThreadPoolConfigDetailDto;
import com.platform.mall.dao.threadpool.model.list.ThreadPoolConfigListDto;
import com.platform.mall.dao.threadpool.model.query.ThreadPoolConfigQuery;
import com.platform.mall.service.threadpool.ThreadPoolConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * -Controller
 *
 * @author wangjia
 * @email vivianshine@126.com
 * @date 2022-06-11 22:10:55
 */
@Api(tags = "管理")
@RestController
@RequestMapping("admin/threadPoolConfig")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ThreadPoolConfigController {

    public final ThreadPoolConfigService threadPoolConfigService;

    /**
     * @Description ThreadPoolConfig列表-分页
     * @Param ThreadPoolConfigQuery
     * @return Result
     * @Author wangjia
     * @Date 2022-06-11 22:10:55
     **/
    @GetMapping("/listThreadPoolConfigPage")
    @ApiOperation("ThreadPoolConfig列表分页")
    public Result<ThreadPoolConfigListDto> listThreadPoolConfigPage(ThreadPoolConfigQuery threadPoolConfigQuery){
        IPage threadPoolConfigList = threadPoolConfigService.listThreadPoolConfigPage(threadPoolConfigQuery);
        return Result.success(threadPoolConfigList);
    }

    /**
     * @Description ThreadPoolConfig列表-不分页
     * @Param ThreadPoolConfigQuery
     * @return Result
     * @Author wangjia
     * @Date 2022-06-11 22:10:55
     **/
    @ApiOperation("ThreadPoolConfig列表不分页")
    @RequestMapping(method = {RequestMethod.GET,RequestMethod.POST},value = "/listThreadPoolConfig")
    public Result<ThreadPoolConfigListDto> listThreadPoolConfig(ThreadPoolConfigQuery threadPoolConfigQuery){
        List<ThreadPoolConfigListDto> threadPoolConfigList = threadPoolConfigService.listThreadPoolConfig(threadPoolConfigQuery);
        return Result.success(threadPoolConfigList);
    }

    /**
     * @Description 查询ThreadPoolConfig
     * @Param id
     * @return Result
     * @Author wangjia
     * @Date 2022-06-11 22:10:55
     **/
    @ApiOperation("ThreadPoolConfig详情")
    @GetMapping("/queryThreadPoolConfig")
    public Result<ThreadPoolConfigDetailDto> queryThreadPoolConfig(@RequestParam(value="id") Long id){
        ThreadPoolConfig threadPoolConfig = threadPoolConfigService.getById(id);
        return Result.success(ThreadPoolConfigConverter.INSTANCE.toDTO(threadPoolConfig));
    }

    /**
     * @Description 新增ThreadPoolConfig
     * @Param threadPoolConfigQuery
     * @return Result
     * @Author wangjia
     * @Date 2022-06-11 22:10:55
     **/
    @ApiOperation("ThreadPoolConfig添加")
    @PostMapping("/saveThreadPoolConfig")
    public Result<ThreadPoolConfig> saveThreadPoolConfig(@Valid @RequestBody ThreadPoolConfigQuery threadPoolConfigQuery){
        ThreadPoolConfig threadPoolConfig = ThreadPoolConfigConverter.INSTANCE.fromQuery(threadPoolConfigQuery);
        threadPoolConfigService.save(threadPoolConfig);
        return Result.success(ThreadPoolConfigConverter.INSTANCE.toDTO(threadPoolConfig));
    }

    /**
     * @Description 修改ThreadPoolConfig
     * @Param ThreadPoolConfigQuery
     * @return Result
     * @Author wangjia
     * @Date 2022-06-11 22:10:55
     **/
    @ApiOperation("ThreadPoolConfig修改")
    @PostMapping("/updateThreadPoolConfig")
    public Result<ThreadPoolConfig> updateThreadPoolConfig(@RequestBody ThreadPoolConfigQuery threadPoolConfigQuery){
        ThreadPoolConfig threadPoolConfig = ThreadPoolConfigConverter.INSTANCE.fromQuery(threadPoolConfigQuery);
        threadPoolConfigService.updateById(threadPoolConfig);
        return Result.success(ThreadPoolConfigConverter.INSTANCE.toDTO(threadPoolConfig));
    }

    /**
    * @Description 删除ThreadPoolConfig
    * @Param id
    * @return
    * @Author wangjia
    * @Date 2022-06-11 22:10:55
    **/
    @ApiOperation("ThreadPoolConfig删除")
    @DeleteMapping("/deleteThreadPoolConfig")
    public Result deleteThreadPoolConfig(@RequestParam(value="id")Long id){

        final boolean result = threadPoolConfigService.removeById(id);
        if(result){
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }
}
