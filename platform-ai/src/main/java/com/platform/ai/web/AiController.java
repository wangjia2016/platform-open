package com.platform.ai.web;

import com.platform.ai.application.AiFacadeService;
import com.platform.common.ai.dto.*;
import com.platform.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/v1")
public class AiController {
    private final AiFacadeService facade;

    public AiController(AiFacadeService facade) {
        this.facade = facade;
    }

    @GetMapping("/health")
    public Result<AiHealthResponse> health() {
        return Result.success(facade.health());
    }

    @PostMapping("/goods/search-plan")
    public Result<SearchPlanResponse> searchPlan(@RequestBody SearchPlanRequest request) {
        return Result.success(facade.searchPlan(request));
    }

    @PostMapping("/goods/recommend")
    public Result<RecommendResponse> recommend(@RequestBody RecommendRequest request) {
        return Result.success(facade.recommend(request));
    }

    @PostMapping("/goods/analyze")
    public Result<AnalyzeResponse> analyze(@RequestBody AnalyzeRequest request) {
        return Result.success(facade.analyze(request));
    }

    @PostMapping("/flash/session-plan")
    public Result<SessionPlanResponse> sessionPlan(@RequestBody SessionPlanRequest request) {
        return Result.success(facade.sessionPlan(request));
    }
}
