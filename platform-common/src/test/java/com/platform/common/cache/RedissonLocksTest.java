package com.platform.common.cache;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedissonLocksTest {

    @Mock
    private RLock lock;

    @Test
    void unlocksWhenHeldByCurrentThread() {
        when(lock.isHeldByCurrentThread()).thenReturn(true);
        RedissonLocks.safeUnlock(lock);
        verify(lock).unlock();
    }

    @Test
    void skipsWhenNotHeld() {
        when(lock.isHeldByCurrentThread()).thenReturn(false);
        RedissonLocks.safeUnlock(lock);
        verify(lock, never()).unlock();
    }

    @Test
    void nullSafe() {
        RedissonLocks.safeUnlock(null);
    }
}
