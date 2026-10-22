package com.platform.common.idempotent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class IdempotentKeysTest {

    @Test
    void keyIsBoundToMember() {
        assertEquals("idempotent:7:abc", IdempotentKeys.key(7L, "abc"));
        assertNotEquals(IdempotentKeys.key(7L, "abc"), IdempotentKeys.key(8L, "abc"));
    }
}
