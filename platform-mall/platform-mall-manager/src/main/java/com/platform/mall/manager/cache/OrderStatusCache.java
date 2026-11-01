package com.platform.mall.manager.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀下单后的状态轮询。受理时先记「处理中」，落库后再写成真实状态。
 * 同一进程内 300 毫秒内的重复轮询不再打 Redis。
 */
@Service
public class OrderStatusCache {

    static final String PROCESSING = "P";
    static final long TTL_SECONDS = 1800L;
    static final long LOCAL_TTL_NANOS = TimeUnit.MILLISECONDS.toNanos(300);

    private final StringRedisTemplate stringRedisTemplate;
    private final ConcurrentHashMap<Long, LocalSnapshot> local = new ConcurrentHashMap<>();

    public OrderStatusCache(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void markProcessing(Long orderId, Long memberId) {
        write(orderId, memberId, PROCESSING, null);
    }

    public void markStatus(Long orderId, Long memberId, int status, Long orderNo) {
        write(orderId, memberId, String.valueOf(status), orderNo);
    }

    public void evict(Long orderId) {
        if (orderId == null) {
            return;
        }
        local.remove(orderId);
        stringRedisTemplate.delete(key(orderId));
    }

    public Snapshot get(Long orderId) {
        if (orderId == null) {
            return null;
        }
        long now = System.nanoTime();
        LocalSnapshot cached = local.get(orderId);
        if (cached != null && cached.expireAt > now) {
            return cached.snapshot;
        }
        String value = stringRedisTemplate.opsForValue().get(key(orderId));
        Snapshot snapshot = parse(value);
        if (value != null && snapshot == null) {
            stringRedisTemplate.delete(key(orderId));
            local.remove(orderId);
            return null;
        }
        if (snapshot != null) {
            local.put(orderId, new LocalSnapshot(snapshot, now + LOCAL_TTL_NANOS));
        }
        return snapshot;
    }

    static Snapshot parse(String value) {
        if (value == null) {
            return null;
        }
        String[] parts = value.split(":", 3);
        if (parts.length < 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            return null;
        }
        Long memberId;
        try {
            memberId = Long.valueOf(parts[0]);
        } catch (NumberFormatException ex) {
            return null;
        }
        Long orderNo = null;
        if (parts.length == 3 && !parts[2].isEmpty()) {
            try {
                orderNo = Long.valueOf(parts[2]);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        if (PROCESSING.equals(parts[1])) {
            return new Snapshot(memberId, null, orderNo);
        }
        try {
            return new Snapshot(memberId, Integer.valueOf(parts[1]), orderNo);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void write(Long orderId, Long memberId, String status, Long orderNo) {
        if (orderId == null || memberId == null || status == null) {
            return;
        }
        String value = orderNo == null
                ? memberId + ":" + status
                : memberId + ":" + status + ":" + orderNo;
        stringRedisTemplate.opsForValue().set(key(orderId), value, TTL_SECONDS, TimeUnit.SECONDS);
        Snapshot snapshot = parse(value);
        if (snapshot != null) {
            local.put(orderId, new LocalSnapshot(snapshot, System.nanoTime() + LOCAL_TTL_NANOS));
        }
    }

    private static String key(Long orderId) {
        return "mall:order:status:" + orderId;
    }

    public static final class Snapshot {
        private final Long memberId;
        private final Integer status;
        private final Long orderNo;

        Snapshot(Long memberId, Integer status, Long orderNo) {
            this.memberId = memberId;
            this.status = status;
            this.orderNo = orderNo;
        }

        public Long getMemberId() {
            return memberId;
        }

        public Integer getStatus() {
            return status;
        }

        public Long getOrderNo() {
            return orderNo;
        }

        public boolean processing() {
            return status == null;
        }
    }

    private static final class LocalSnapshot {
        private final Snapshot snapshot;
        private final long expireAt;

        private LocalSnapshot(Snapshot snapshot, long expireAt) {
            this.snapshot = snapshot;
            this.expireAt = expireAt;
        }
    }
}
