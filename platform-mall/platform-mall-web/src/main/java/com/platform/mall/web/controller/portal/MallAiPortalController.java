package com.platform.mall.web.controller.portal;

import com.platform.common.ai.dto.*;
import com.platform.common.result.Result;
import com.platform.mall.dao.goods.model.detail.MallGoodsDetailDto;
import com.platform.mall.dao.goods.model.list.MallGoodsListDto;
import com.platform.mall.dao.goods.model.query.MallGoodsQuery;
import com.platform.mall.service.feignClient.PlatformAiClient;
import com.platform.mall.service.goods.MallGoodsService;
import com.platform.mall.web.support.AiGoodsSnapshotFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Mall facade for platform-ai. Does not sit on flash stock / pay paths.
 */
@Api(tags = "前端AI辅助api")
@RestController
@RequestMapping("api/ai/v1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MallAiPortalController {

    private final PlatformAiClient platformAiClient;
    private final MallGoodsService mallGoodsService;

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
        return forward(() -> platformAiClient.searchPlan(request));
    }

    @ApiOperation("商品推荐")
    @PostMapping("/goods/recommend")
    public Result recommend(@RequestBody RecommendRequest request) {
        return forward(() -> platformAiClient.recommend(request));
    }

    @ApiOperation("商品分析")
    @PostMapping("/goods/analyze")
    public Result analyze(@RequestBody AnalyzeRequest request) {
        return forward(() -> platformAiClient.analyze(request));
    }

    @ApiOperation("按商品 ID 分析（从库组装快照，只读）")
    @GetMapping("/goods/analyze-by-id")
    public Result analyzeById(@RequestParam("goodsId") Long goodsId) {
        return forward(() -> {
            MallGoodsDetailDto detail = mallGoodsService.queryGoodsDetail(goodsId);
            if (detail == null || detail.getId() == null) {
                return Result.fail("商品不存在");
            }
            AnalyzeRequest req = new AnalyzeRequest();
            req.setGoods(AiGoodsSnapshotFactory.fromDetail(detail));
            return platformAiClient.analyze(req);
        });
    }

    @ApiOperation("按锚点商品推荐（候选从在售商品列表抽样，只读）")
    @GetMapping("/goods/recommend-by-anchor")
    public Result recommendByAnchor(@RequestParam("goodsId") Long goodsId,
                                    @RequestParam(value = "limit", defaultValue = "5") Integer limit) {
        return forward(() -> {
            MallGoodsDetailDto anchor = mallGoodsService.queryGoodsDetail(goodsId);
            if (anchor == null || anchor.getId() == null) {
                return Result.fail("商品不存在");
            }
            List<MallGoodsListDto> listed = mallGoodsService.listMallGoods(MallGoodsQuery.builder()
                    .onUnSale(0)
                    .build());
            RecommendRequest req = new RecommendRequest();
            req.setAnchorGoodsId(goodsId);
            req.setLimit(limit == null ? 5 : limit);
            List<GoodsSnapshot> candidates = new ArrayList<GoodsSnapshot>();
            candidates.add(AiGoodsSnapshotFactory.fromDetail(anchor));
            if (listed != null) {
                for (MallGoodsListDto g : listed) {
                    if (g.getId() == null || g.getId().equals(goodsId)) {
                        continue;
                    }
                    candidates.add(AiGoodsSnapshotFactory.fromList(g));
                    if (candidates.size() >= 30) {
                        break;
                    }
                }
            }
            req.setCandidates(candidates);
            return platformAiClient.recommend(req);
        });
    }

    @ApiOperation("秒杀场次策划")
    @PostMapping("/flash/session-plan")
    public Result sessionPlan(@RequestBody SessionPlanRequest request) {
        return forward(() -> platformAiClient.sessionPlan(request));
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
