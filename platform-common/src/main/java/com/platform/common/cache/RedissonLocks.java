package com.platform.common.cache;

import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Redisson 锁辅助：只解当前线程持有的锁，避免租约过期后误删别人的锁。
 */
public final class RedissonLocks {

    private static final Logger log = LoggerFactory.getLogger(RedissonLocks.class);

    private RedissonLocks() {
    }

    /**
     * 安全 unlock：未持锁或已过期时跳过，不抛 IllegalMonitorStateException。
     */
    public static void safeUnlock(RLock lock) {
        if (lock == null) {
            return;
        }
        try {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        } catch (IllegalMonitorStateException ex) {
            log.warn("skip unlock, lock not held by current thread: {}", lock.getName());
        }
    }
}
