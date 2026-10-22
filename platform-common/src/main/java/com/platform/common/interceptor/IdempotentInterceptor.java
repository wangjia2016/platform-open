package com.platform.common.interceptor;

import com.platform.common.annotation.CheckIdempotent;
import com.platform.common.idempotent.IdempotentKeys;
import com.platform.common.util.JwtOperator;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 幂等 token 必须属于当前登录会员，并且只能使用一次。
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IdempotentInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    private final JwtOperator jwtOperator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        Method method = ((HandlerMethod) handler).getMethod();
        boolean hasAnnotation = method.isAnnotationPresent(CheckIdempotent.class);
        if (hasAnnotation && method.getAnnotation(CheckIdempotent.class).value() && !checkToken(request)) {
            throw new SecurityException("不允许重复提交");
        }
        return true;
    }

    private boolean checkToken(HttpServletRequest request) {
        String token = request.getHeader("I-token");
        if (StringUtils.isEmpty(token)) {
            throw new SecurityException("不允许重复提交 || I-token 不存在");
        }
        String auth = request.getHeader("X-Token");
        if (StringUtils.isBlank(auth)) {
            throw new SecurityException("Token不能为空！");
        }
        Claims claims = jwtOperator.getClaimsFromToken(auth);
        Object memberId = claims.get("id");
        if (memberId == null) {
            throw new SecurityException("Token不合法！");
        }
        return Boolean.TRUE.equals(stringRedisTemplate.delete(IdempotentKeys.key(memberId, token)));
    }
}
