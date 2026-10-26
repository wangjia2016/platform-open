package com.platform.mall.web.controller.portal;

import com.platform.common.ai.dto.*;
import com.platform.common.result.Result;
import com.platform.mall.service.feignClient.PlatformAiClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mall facade for optional platform-ai. Does not sit on flash stock / pay paths.
 */
@Api(tags = "前端AI辅助api")
@RestController
@RequestMapping("api/ai/v1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MallAiPortalController {

    private final PlatformAiClient platformAiClient;

    @Value("${platform.ai.gateway-enabled:true}")
    private boolean gatewayEnabled;

    @ApiOperation("AI 健康检查")
    @GetMapping("/health")
    public Result health() {
        if (!gatewayEnabled) {
            return Result.fail(503, "AI_GATEWAY_DISABLED");
        }
        try {
            return platformAiClient.health();
        } catch (Exception e) {
            return Result.fail(503, "AI service unavailable: " + e.getMessage());
        }
    }

    @ApiOperation("商品搜索计划")
    @PostMapping("/goods/search-plan")
    public Result searchPlan(@RequestBody SearchPlanRequest request) {
        return forward(new SupplierResult() {
            @Override
            public Result get() {
                return platformAiClient.searchPlan(request);
            }
        });
    }

    @ApiOperation("商品推荐")
    @PostMapping("/goods/recommend")
    public Result recommend(@RequestBody RecommendRequest request) {
        return forward(new SupplierResult() {
            @Override
            public Result get() {
                return platformAiClient.recommend(request);
            }
        });
    }

    @ApiOperation("商品分析")
    @PostMapping("/goods/analyze")
    public Result analyze(@RequestBody AnalyzeRequest request) {
        return forward(new SupplierResult() {
            @Override
            public Result get() {
                return platformAiClient.analyze(request);
            }
        });
    }

    @ApiOperation("秒杀场次策划")
    @PostMapping("/flash/session-plan")
    public Result sessionPlan(@RequestBody SessionPlanRequest request) {
        return forward(new SupplierResult() {
            @Override
            public Result get() {
                return platformAiClient.sessionPlan(request);
            }
        });
    }

    private Result forward(SupplierResult supplier) {
        if (!gatewayEnabled) {
            return Result.fail(503, "AI_GATEWAY_DISABLED");
        }
        try {
            return supplier.get();
        } catch (Exception e) {
            return Result.fail(503, "AI service unavailable: " + e.getMessage());
        }
    }

    private interface SupplierResult {
        Result get();
    }
}
