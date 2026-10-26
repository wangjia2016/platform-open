package com.platform.ai.fallback;

import com.platform.common.ai.dto.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RuleAiServiceTest {
    private final RuleAiService rules = new RuleAiService();

    @Test
    void searchPlanExtractsMaxPrice() {
        SearchPlanRequest req = new SearchPlanRequest();
        req.setQuery("300元内降噪耳机");
        SearchPlanResponse resp = rules.searchPlan(req);
        assertTrue(resp.isDegraded());
        assertEquals("rules", resp.getStrategy());
        assertTrue(resp.getPseudoSql().contains("price <= 300"));
        assertFalse(resp.getKeywords().isEmpty());
    }

    @Test
    void recommendSkipsOffShelfAndAnchor() {
        RecommendRequest req = new RecommendRequest();
        req.setAnchorGoodsId(1L);
        req.setLimit(3);
        GoodsSnapshot a = GoodsSnapshot.builder().id(1L).name("a").stock(10).onUnSale(0).price(new BigDecimal("10")).build();
        GoodsSnapshot b = GoodsSnapshot.builder().id(2L).name("b").stock(5).onUnSale(0).price(new BigDecimal("12")).build();
        GoodsSnapshot c = GoodsSnapshot.builder().id(3L).name("c").stock(9).onUnSale(1).price(new BigDecimal("8")).build();
        req.setCandidates(Arrays.asList(a, b, c));
        RecommendResponse resp = rules.recommend(req);
        assertEquals(1, resp.getItems().size());
        assertEquals(2L, resp.getItems().get(0).getGoodsId());
    }

    @Test
    void analyzeFlagsStockWarn() {
        AnalyzeRequest req = new AnalyzeRequest();
        req.setGoods(GoodsSnapshot.builder().id(1L).name("demo").stock(3).stockWarn(10).onUnSale(0)
                .price(new BigDecimal("99")).proposedPrice(new BigDecimal("120")).title("副标题").build());
        AnalyzeResponse resp = rules.analyze(req);
        assertTrue(resp.getRisks().stream().anyMatch(r -> r.contains("stockWarn")));
        assertEquals("demo", resp.getCopyDrafts().get("title"));
    }

    @Test
    void sessionPlanAddsRiskNote() {
        SessionPlanRequest req = new SessionPlanRequest();
        Map<String, String> window = new HashMap<String, String>();
        window.put("start", "10:00");
        window.put("end", "22:00");
        req.setWindow(window);
        req.setGoodsPool(Collections.singletonList(
                GoodsSnapshot.builder().id(9L).stock(40).onUnSale(0).build()));
        SessionPlanResponse resp = rules.sessionPlan(req);
        assertEquals(2, resp.getSessions().size());
        assertFalse(resp.getBindSuggestions().isEmpty());
        assertTrue(resp.getRiskNotes().get(0).contains("GoodsStockCache"));
    }
}
