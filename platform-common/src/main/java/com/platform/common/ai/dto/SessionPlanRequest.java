package com.platform.common.ai.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class SessionPlanRequest {
    /** e.g. { "start": "10:00", "end": "22:00" } */
    private Map<String, String> window;
    private List<GoodsSnapshot> goodsPool = new ArrayList<GoodsSnapshot>();
}
