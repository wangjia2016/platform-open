package com.platform.ai.application;

import com.platform.common.ai.dto.GoodsSnapshot;
import com.platform.common.ai.dto.SessionPlanRequest;
import com.platform.common.ai.dto.SessionPlanResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Post-process LLM / rules session-plan: drop unknown goodsId, off-shelf / zero stock,
 * clamp suggestFlashStock, fix sessionsIndex bounds.
 */
public final class SessionPlanSanitizer {
    private SessionPlanSanitizer() {
    }

    public static SessionPlanResponse sanitize(SessionPlanRequest req, SessionPlanResponse raw) {
        if (raw == null) {
            return SessionPlanResponse.builder().degraded(true).strategy("empty").build();
        }
        List<Map<String, Object>> sessions = raw.getSessions() == null
                ? new ArrayList<Map<String, Object>>()
                : new ArrayList<Map<String, Object>>(raw.getSessions());
        if (sessions.isEmpty()) {
            Map<String, Object> s = new LinkedHashMap<String, Object>();
            s.put("name", "默认场次");
            s.put("start", "10:00");
            s.put("end", "22:00");
            sessions.add(s);
        }

        Map<Long, GoodsSnapshot> byId = new HashMap<Long, GoodsSnapshot>();
        if (req != null && req.getGoodsPool() != null) {
            for (GoodsSnapshot g : req.getGoodsPool()) {
                if (g != null && g.getId() != null) {
                    byId.put(g.getId(), g);
                }
            }
        }

        List<Map<String, Object>> binds = new ArrayList<Map<String, Object>>();
        Set<Long> seen = new HashSet<Long>();
        List<Map<String, Object>> in = raw.getBindSuggestions() == null
                ? new ArrayList<Map<String, Object>>()
                : raw.getBindSuggestions();
        for (Map<String, Object> b : in) {
            if (b == null) {
                continue;
            }
            Long goodsId = asLong(b.get("goodsId"));
            if (goodsId == null || !byId.containsKey(goodsId) || seen.contains(goodsId)) {
                continue;
            }
            GoodsSnapshot g = byId.get(goodsId);
            if (g.getOnUnSale() != null && g.getOnUnSale() == 1) {
                continue;
            }
            int stock = g.getStock() == null ? 0 : g.getStock();
            if (stock <= 0) {
                continue;
            }
            int suggest = asInt(b.get("suggestFlashStock"), Math.max(1, stock / 2));
            if (suggest < 1) {
                suggest = 1;
            }
            if (suggest > stock) {
                suggest = stock;
            }
            int idx = asInt(b.get("sessionsIndex"), 0);
            if (idx < 0 || idx >= sessions.size()) {
                idx = 0;
            }
            Map<String, Object> out = new LinkedHashMap<String, Object>();
            out.put("sessionsIndex", idx);
            out.put("goodsId", goodsId);
            out.put("suggestFlashStock", suggest);
            if (b.get("reason") != null) {
                out.put("reason", String.valueOf(b.get("reason")));
            }
            binds.add(out);
            seen.add(goodsId);
            if (binds.size() >= 20) {
                break;
            }
        }

        List<String> risks = raw.getRiskNotes() == null
                ? new ArrayList<String>()
                : new ArrayList<String>(raw.getRiskNotes());
        boolean hasStockNote = false;
        for (String r : risks) {
            if (r != null && r.contains("库存")) {
                hasStockNote = true;
                break;
            }
        }
        if (!hasStockNote) {
            risks.add("AI 不参与扣库存；以缓存 Lua 预扣为准");
        }

        return SessionPlanResponse.builder()
                .sessions(sessions)
                .bindSuggestions(binds)
                .riskNotes(risks)
                .degraded(raw.isDegraded())
                .strategy(raw.getStrategy())
                .build();
    }

    private static Long asLong(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Number) {
            return ((Number) o).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(o).trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static int asInt(Object o, int def) {
        if (o == null) {
            return def;
        }
        if (o instanceof Number) {
            return ((Number) o).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(o).trim());
        } catch (Exception e) {
            return def;
        }
    }
}
