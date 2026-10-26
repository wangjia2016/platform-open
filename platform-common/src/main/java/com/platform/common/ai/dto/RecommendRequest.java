package com.platform.common.ai.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class RecommendRequest {
    private Long anchorGoodsId;
    private List<GoodsSnapshot> candidates = new ArrayList<GoodsSnapshot>();
    private Integer limit = 5;
    private Map<String, Object> context;
}
