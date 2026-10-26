package com.platform.ai.fallback;

import com.platform.common.ai.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RuleAiService {
    private static final Pattern PRICE_WITHIN = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*元(?:以内|内|以下)?");

    public SearchPlanResponse searchPlan(SearchPlanRequest req) {
        String query = req.getQuery() == null ? "" : req.getQuery().trim();
        List<Map<String, Object>> filters = new ArrayList<Map<String, Object>>();
        filters.add(filter("onUnSale", "eq", 0));
        BigDecimal maxPrice = null;
        if (req.getHints() != null && req.getHints().get("maxPrice") != null) {
            maxPrice = new BigDecimal(String.valueOf(req.getHints().get("maxPrice")));
        }
        Matcher m = PRICE_WITHIN.matcher(query);
        if (m.find()) {
            maxPrice = new BigDecimal(m.group(1));
        }
        if (maxPrice != null) {
            filters.add(filter("price", "lte", maxPrice));
        }
        List<String> keywords = new ArrayList<String>();
        for (String part : query.replaceAll("[0-9.]+\\s*元(?:以内|内|以下)?", " ").split("\\s+|的|和|与")) {
            if (StringUtils.hasText(part) && part.length() >= 2) {
                keywords.add(part.trim());
            }
        }
        List<Map<String, Object>> sort = new ArrayList<Map<String, Object>>();
        Map<String, Object> s = new LinkedHashMap<String, Object>();
        s.put("field", "price");
        s.put("order", "asc");
        sort.add(s);

        StringBuilder sql = new StringBuilder("SELECT * FROM mall_goods WHERE on_un_sale = 0");
        if (maxPrice != null) {
            sql.append(" AND price <= ").append(maxPrice);
        }
        for (String kw : keywords) {
            sql.append(" AND name LIKE '%").append(kw.replace("'", "")).append("%'");
        }

        List<String> notes = new ArrayList<String>();
        notes.add("规则降级结果：未调用大模型");
        notes.add("本仓未接 ES：请用 MyBatis 条件或后续 ES 实现");

        return SearchPlanResponse.builder()
                .intent(StringUtils.hasText(query) ? ("寻找：" + query) : "未提供查询词")
                .filters(filters)
                .sort(sort)
                .keywords(keywords)
                .pseudoSql(sql.toString())
                .notes(notes)
                .degraded(true)
                .strategy("rules")
                .build();
    }

    public RecommendResponse recommend(RecommendRequest req) {
        int limit = req.getLimit() == null || req.getLimit() <= 0 ? 5 : req.getLimit();
        List<GoodsSnapshot> candidates = req.getCandidates() == null
                ? Collections.<GoodsSnapshot>emptyList()
                : new ArrayList<GoodsSnapshot>(req.getCandidates());
        List<RecommendResponse.RecommendItem> items = new ArrayList<RecommendResponse.RecommendItem>();
        Collections.sort(candidates, new Comparator<GoodsSnapshot>() {
            @Override
            public int compare(GoodsSnapshot a, GoodsSnapshot b) {
                return Double.compare(score(b), score(a));
            }
        });
        for (GoodsSnapshot g : candidates) {
            if (g.getId() == null) {
                continue;
            }
            if (req.getAnchorGoodsId() != null && req.getAnchorGoodsId().equals(g.getId())) {
                continue;
            }
            if (g.getOnUnSale() != null && g.getOnUnSale() != 0) {
                continue;
            }
            if (g.getStock() != null && g.getStock() <= 0) {
                continue;
            }
            List<String> reasons = new ArrayList<String>();
            reasons.add("规则分:" + String.format(Locale.ROOT, "%.2f", score(g)));
            if (g.getStock() != null && g.getStock() > 0) {
                reasons.add("有库存");
            }
            items.add(RecommendResponse.RecommendItem.builder()
                    .goodsId(g.getId())
                    .score(score(g))
                    .reasons(reasons)
                    .build());
            if (items.size() >= limit) {
                break;
            }
        }
        List<String> notes = new ArrayList<String>();
        notes.add("规则重排：优先上架且有库存；无协同过滤模型");
        return RecommendResponse.builder()
                .items(items)
                .strategy("rules")
                .degraded(true)
                .notes(notes)
                .build();
    }

    public AnalyzeResponse analyze(AnalyzeRequest req) {
        GoodsSnapshot g = req.getGoods();
        if (g == null && req.getGoodsList() != null && !req.getGoodsList().isEmpty()) {
            g = req.getGoodsList().get(0);
        }
        if (g == null) {
            return AnalyzeResponse.builder()
                    .summary("未提供商品数据")
                    .risks(Collections.singletonList("数据不足"))
                    .degraded(true)
                    .strategy("rules")
                    .build();
        }
        List<String> points = new ArrayList<String>();
        if (StringUtils.hasText(g.getTitle())) {
            points.add(g.getTitle());
        }
        if (g.getPrice() != null && g.getProposedPrice() != null
                && g.getPrice().compareTo(g.getProposedPrice()) < 0) {
            points.add("相对参考价有优惠");
        }
        List<String> risks = new ArrayList<String>();
        if (g.getStock() != null && g.getStockWarn() != null && g.getStock() <= g.getStockWarn()) {
            risks.add("库存低于或等于预警值 stockWarn");
        }
        if (g.getOnUnSale() != null && g.getOnUnSale() != 0) {
            risks.add("商品已下架");
        }
        Map<String, String> copy = new HashMap<String, String>();
        copy.put("title", g.getName() == null ? "未命名商品" : g.getName());
        copy.put("subtitle", StringUtils.hasText(g.getTitle()) ? g.getTitle() : "精选好物");
        String pricing = g.getPrice() == null ? "缺少价格" : ("当前价 " + g.getPrice());
        return AnalyzeResponse.builder()
                .summary("规则分析：" + (g.getName() == null ? g.getId() : g.getName()))
                .sellingPoints(points)
                .risks(risks)
                .copyDrafts(copy)
                .pricingNotes(pricing)
                .degraded(true)
                .strategy("rules")
                .build();
    }

    public SessionPlanResponse sessionPlan(SessionPlanRequest req) {
        Map<String, String> window = req.getWindow() == null ? new HashMap<String, String>() : req.getWindow();
        List<GoodsSnapshot> pool = req.getGoodsPool() == null
                ? Collections.<GoodsSnapshot>emptyList()
                : new ArrayList<GoodsSnapshot>(req.getGoodsPool());
        Collections.sort(pool, new Comparator<GoodsSnapshot>() {
            @Override
            public int compare(GoodsSnapshot a, GoodsSnapshot b) {
                int sa = a.getStock() == null ? 0 : a.getStock();
                int sb = b.getStock() == null ? 0 : b.getStock();
                return Integer.compare(sb, sa);
            }
        });
        List<Map<String, Object>> sessions = new ArrayList<Map<String, Object>>();
        Map<String, Object> s1 = new LinkedHashMap<String, Object>();
        s1.put("name", "抢购中");
        s1.put("start", window.getOrDefault("start", "10:00"));
        s1.put("end", window.getOrDefault("mid", "16:00"));
        sessions.add(s1);
        Map<String, Object> s2 = new LinkedHashMap<String, Object>();
        s2.put("name", "即将开始");
        s2.put("start", window.getOrDefault("mid", "16:00"));
        s2.put("end", window.getOrDefault("end", "22:00"));
        sessions.add(s2);

        List<Map<String, Object>> binds = new ArrayList<Map<String, Object>>();
        int i = 0;
        for (GoodsSnapshot g : pool) {
            if (g.getId() == null) {
                continue;
            }
            Map<String, Object> b = new LinkedHashMap<String, Object>();
            b.put("sessionsIndex", i % 2);
            b.put("goodsId", g.getId());
            b.put("suggestFlashStock", g.getStock() == null ? 10 : Math.max(1, g.getStock() / 2));
            binds.add(b);
            i++;
            if (binds.size() >= 10) {
                break;
            }
        }
        List<String> risks = new ArrayList<String>();
        risks.add("AI 不参与扣库存；以 GoodsStockCache Lua 预扣为准");
        risks.add("规则降级场次方案，仅供运营参考");
        return SessionPlanResponse.builder()
                .sessions(sessions)
                .bindSuggestions(binds)
                .riskNotes(risks)
                .degraded(true)
                .strategy("rules")
                .build();
    }

    private static Map<String, Object> filter(String field, String op, Object value) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("field", field);
        m.put("op", op);
        m.put("value", value);
        return m;
    }

    private static double score(GoodsSnapshot g) {
        double s = 0.5;
        if (g.getStock() != null) {
            s += Math.min(0.3, g.getStock() / 1000.0);
        }
        if (g.getOnUnSale() != null && g.getOnUnSale() == 0) {
            s += 0.2;
        }
        if (g.getPrice() != null) {
            s += 0.05;
        }
        return Math.min(0.99, s);
    }
}
