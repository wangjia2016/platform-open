package com.platform.common.idempotent;

/**
 * 下单幂等 token。键里带会员编号，避免拿别人的 token 代下单。
 */
public final class IdempotentKeys {

    public static final long TTL_MINUTES = 5L;

    private IdempotentKeys() {
    }

    public static String key(Object memberId, String token) {
        return "idempotent:" + memberId + ":" + token;
    }
}
