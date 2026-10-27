package com.platform.ai.application;

import com.platform.common.ai.dto.GoodsSnapshot;
import com.platform.common.ai.dto.SessionPlanRequest;
import com.platform.common.ai.dto.SessionPlanResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SessionPlanSanitizerTest {

    @Test
    public void dropsUnknownAndOffShelfAndClampsStock() {
        SessionPlanRequest req = new SessionPlanRequest();
        List<GoodsSnapshot> pool = new ArrayList<GoodsSnapshot>();
        pool.add(GoodsSnapshot.builder().id(1L).stock(10).onUnSale(0).build());
        pool.add(GoodsSnapshot.builder().id(2L).stock(5).onUnSale(1).build());
        pool.add(GoodsSnapshot.builder().id(3L).stock(0).onUnSale(0).build());
        req.setGoodsPool(pool);

        List<Map<String, Object>> binds = new ArrayList<Map<String, Object>>();
        binds.add(bind(0, 1L, 99)); // clamp to 10
        binds.add(bind(0, 2L, 2));  // off shelf
        binds.add(bind(0, 3L, 1));  // zero stock
        binds.add(bind(0, 99L, 1)); // unknown
        binds.add(bind(5, 1L, 3));  // duplicate + bad index → first wins already seen

        Map<String, Object> session = new LinkedHashMap<String, Object>();
        session.put("name", "A");
        session.put("start", "10:00");
        session.put("end", "12:00");
        List<Map<String, Object>> sessions = new ArrayList<Map<String, Object>>();
        sessions.add(session);

        SessionPlanResponse raw = SessionPlanResponse.builder()
                .sessions(sessions)
                .bindSuggestions(binds)
                .riskNotes(new ArrayList<String>())
                .degraded(false)
                .strategy("llm")
                .build();

        SessionPlanResponse out = SessionPlanSanitizer.sanitize(req, raw);
        Assert.assertEquals(1, out.getBindSuggestions().size());
        Map<String, Object> b = out.getBindSuggestions().get(0);
        Assert.assertEquals(1L, ((Number) b.get("goodsId")).longValue());
        Assert.assertEquals(10, ((Number) b.get("suggestFlashStock")).intValue());
        Assert.assertEquals(0, ((Number) b.get("sessionsIndex")).intValue());
        Assert.assertFalse(out.getRiskNotes().isEmpty());
    }

    private static Map<String, Object> bind(int idx, long goodsId, int stock) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("sessionsIndex", idx);
        m.put("goodsId", goodsId);
        m.put("suggestFlashStock", stock);
        return m;
    }
}
