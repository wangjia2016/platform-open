package com.platform.mall.manager.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 秒杀下单后的状态轮询。受理时先记「处理中」，落库后再写成真实状态。
 */
@Service
public class OrderStatusCache {

    static final String PROCESSING = "P";
    static final long TTL_SECONDS = 1800L;

    private final StringRedisTemplate stringRedisTemplate;

    public OrderStatusCache(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void markProcessing(Long orderId, Long memberId) {
        write(orderId, memberId, PROCESSING);
    }

    public void markStatus(Long orderId, Long memberId, int status) {
        write(orderId, memberId, String.valueOf(status));
    }

    public void evict(Long orderId) {
        if (orderId == null) {
            return;
        }
        stringRedisTemplate.delete(key(orderId));
    }

    public Snapshot get(Long orderId) {
        if (orderId == null) {
            return null;
        }
        String value = stringRedisTemplate.opsForValue().get(key(orderId));
        Snapshot snapshot = parse(value);
        if (value != null && snapshot == null) {
            stringRedisTemplate.delete(key(orderId));
        }
        return snapshot;
    }

    static Snapshot parse(String value) {
        if (value == null) {
            return null;
        }
        int split = value.indexOf(':');
        if (split <= 0 || split == value.length() - 1) {
            return null;
        }
        Long memberId;
        try {
            memberId = Long.valueOf(value.substring(0, split));
        } catch (NumberFormatException ex) {
            return null;
        }
        String statusText = value.substring(split + 1);
        if (PROCESSING.equals(statusText)) {
            return new Snapshot(memberId, null);
        }
        try {
            return new Snapshot(memberId, Integer.valueOf(statusText));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void write(Long orderId, Long memberId, String status) {
        if (orderId == null || memberId == null || status == null) {
            return;
        }
        stringRedisTemplate.opsForValue().set(key(orderId), memberId + ":" + status, TTL_SECONDS, TimeUnit.SECONDS);
    }

    private static String key(Long orderId) {
        return "mall:order:status:" + orderId;
    }

    public static final class Snapshot {
        private final Long memberId;
        private final Integer status;

        Snapshot(Long memberId, Integer status) {
            this.memberId = memberId;
            this.status = status;
        }

        public Long getMemberId() {
            return memberId;
        }

        public Integer getStatus() {
            return status;
        }

        public boolean processing() {
            return status == null;
        }
    }
}
