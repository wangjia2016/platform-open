package com.platform.mall.web.support;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 当前登录会员。编号来自认证切面写入的请求属性，不信任客户端传参。
 */
public final class CurrentMember {

    private CurrentMember() {
    }

    public static Long requireId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new SecurityException("未登录");
        }
        Object id = attributes.getRequest().getAttribute("id");
        if (id == null) {
            throw new SecurityException("未登录");
        }
        return Long.valueOf(String.valueOf(id));
    }
}
