package com.platform.ai.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.ai.config.AiProperties;
import com.platform.ai.fallback.RuleAiService;
import com.platform.ai.llm.OpenAiCompatibleClient;
import com.platform.ai.llm.PromptLoader;
import com.platform.common.ai.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AiFacadeService {
    private static final Logger log = LoggerFactory.getLogger(AiFacadeService.class);

    private final AiProperties props;
    private final RuleAiService rules;
    private final OpenAiCompatibleClient llm;
    private final PromptLoader prompts;
    private final ObjectMapper objectMapper;

    public AiFacadeService(AiProperties props, RuleAiService rules, OpenAiCompatibleClient llm,
                           PromptLoader prompts, ObjectMapper objectMapper) {
        this.props = props;
        this.rules = rules;
        this.llm = llm;
        this.prompts = prompts;
        this.objectMapper = objectMapper;
    }

    public AiHealthResponse health() {
        return AiHealthResponse.builder()
                .status("UP")
                .aiEnabled(props.isEnabled())
                .llmConfigured(props.isLlmConfigured())
                .model(props.getModel())
                .build();
    }

    public SearchPlanResponse searchPlan(SearchPlanRequest req) {
        if (!props.isLlmConfigured()) {
            return rules.searchPlan(req);
        }
        try {
            String json = llm.chatJson(prompts.load("goods-search.system.md"), objectMapper.writeValueAsString(req));
            SearchPlanResponse resp = objectMapper.readValue(json, SearchPlanResponse.class);
            resp.setDegraded(false);
            resp.setStrategy("llm");
            return resp;
        } catch (Exception e) {
            log.warn("search-plan LLM failed, fallback: {}", e.toString());
            return rules.searchPlan(req);
        }
    }

    public RecommendResponse recommend(RecommendRequest req) {
        if (!props.isLlmConfigured()) {
            return rules.recommend(req);
        }
        try {
            String json = llm.chatJson(prompts.load("goods-recommend.system.md"), objectMapper.writeValueAsString(req));
            RecommendResponse resp = objectMapper.readValue(json, RecommendResponse.class);
            if (resp.getItems() == null) {
                throw new IllegalStateException("AI_BAD_OUTPUT");
            }
            resp.setDegraded(false);
            if (resp.getStrategy() == null) {
                resp.setStrategy("llm_rerank");
            }
            return resp;
        } catch (Exception e) {
            log.warn("recommend LLM failed, fallback: {}", e.toString());
            return rules.recommend(req);
        }
    }

    public AnalyzeResponse analyze(AnalyzeRequest req) {
        if (!props.isLlmConfigured()) {
            return rules.analyze(req);
        }
        try {
            String json = llm.chatJson(prompts.load("goods-analyze.system.md"), objectMapper.writeValueAsString(req));
            AnalyzeResponse resp = objectMapper.readValue(json, AnalyzeResponse.class);
            resp.setDegraded(false);
            resp.setStrategy("llm");
            return resp;
        } catch (Exception e) {
            log.warn("analyze LLM failed, fallback: {}", e.toString());
            return rules.analyze(req);
        }
    }

    public SessionPlanResponse sessionPlan(SessionPlanRequest req) {
        if (!props.isLlmConfigured()) {
            return rules.sessionPlan(req);
        }
        try {
            String json = llm.chatJson(prompts.load("flash-session-plan.system.md"), objectMapper.writeValueAsString(req));
            SessionPlanResponse resp = objectMapper.readValue(json, SessionPlanResponse.class);
            if (resp.getSessions() == null || resp.getSessions().isEmpty()) {
                throw new IllegalStateException("AI_BAD_OUTPUT");
            }
            resp.setDegraded(false);
            resp.setStrategy("llm");
            return resp;
        } catch (Exception e) {
            log.warn("session-plan LLM failed, fallback: {}", e.toString());
            return rules.sessionPlan(req);
        }
    }
}
