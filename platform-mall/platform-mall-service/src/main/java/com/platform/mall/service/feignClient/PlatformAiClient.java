package com.platform.mall.service.feignClient;

import com.platform.common.ai.dto.*;
import com.platform.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Optional AI service. Default URL points at local platform-ai; override with platform.ai.base-url or Nacos name.
 */
@FeignClient(name = "platform-ai", url = "${platform.ai.base-url:http://127.0.0.1:8088}")
public interface PlatformAiClient {

    @GetMapping("/ai/v1/health")
    Result<AiHealthResponse> health();

    @PostMapping("/ai/v1/goods/search-plan")
    Result<SearchPlanResponse> searchPlan(@RequestBody SearchPlanRequest request);

    @PostMapping("/ai/v1/goods/recommend")
    Result<RecommendResponse> recommend(@RequestBody RecommendRequest request);

    @PostMapping("/ai/v1/goods/analyze")
    Result<AnalyzeResponse> analyze(@RequestBody AnalyzeRequest request);

    @PostMapping("/ai/v1/flash/session-plan")
    Result<SessionPlanResponse> sessionPlan(@RequestBody SessionPlanRequest request);
}
