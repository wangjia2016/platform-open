package com.platform.mall.manager.cache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderStatusCacheTest {

    @Test
    void processingSnapshotHasNoStatus() {
        OrderStatusCache.Snapshot snapshot = OrderStatusCache.parse("7:P");
        assertEquals(7L, snapshot.getMemberId());
        assertTrue(snapshot.processing());
        assertNull(snapshot.getStatus());
    }

    @Test
    void statusSnapshotKeepsCode() {
        OrderStatusCache.Snapshot snapshot = OrderStatusCache.parse("7:1");
        assertEquals(7L, snapshot.getMemberId());
        assertEquals(1, snapshot.getStatus());
        assertNull(snapshot.getOrderNo());
    }

    @Test
    void statusSnapshotKeepsOrderNo() {
        OrderStatusCache.Snapshot snapshot = OrderStatusCache.parse("7:1:900");
        assertEquals(7L, snapshot.getMemberId());
        assertEquals(1, snapshot.getStatus());
        assertEquals(900L, snapshot.getOrderNo());
    }

    @Test
    void brokenValueIsIgnored() {
        assertNull(OrderStatusCache.parse("nope"));
        assertNull(OrderStatusCache.parse("7:x"));
        assertNull(OrderStatusCache.parse(null));
    }
}
