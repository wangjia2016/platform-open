package com.platform.mall.web.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 下单入口的默认 QPS 上限。未接 Sentinel 控制台时仍然生效，可用 mall.order.create-qps 覆盖。
 */
@Configuration
public class OrderSentinelConfiguration implements InitializingBean {

    public static final String CREATE_ORDER_RESOURCE = "createOrder";

    @Value("${mall.order.create-qps:2000}")
    private double createQps;

    @Override
    public void afterPropertiesSet() {
        List<FlowRule> rules = new ArrayList<>(FlowRuleManager.getRules());
        rules.removeIf(rule -> CREATE_ORDER_RESOURCE.equals(rule.getResource()));
        FlowRule rule = new FlowRule(CREATE_ORDER_RESOURCE);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(createQps);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
}
