package com.platform.mall.service.order;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MallOrderMqTest {

    @Test
    void paySuccessDestinationKeepsLegacyTopicSpelling() {
        assertEquals("order-pay-success-notify-mamager:orderPayManagerNotify",
                MallOrderMq.PAY_SUCCESS_DESTINATION);
    }

    @Test
    void payReminderFiresBeforeUnpaidCancel() {
        assertTrue(MallOrderMq.delaySeconds(MallOrderMq.PAY_REMIND_DELAY_LEVEL)
                < MallOrderMq.delaySeconds(MallOrderMq.UNPAID_CANCEL_DELAY_LEVEL));
        assertEqualsMinutes(5, MallOrderMq.PAY_REMIND_DELAY_LEVEL);
        assertEqualsMinutes(10, MallOrderMq.UNPAID_CANCEL_DELAY_LEVEL);
    }

    private static void assertEqualsMinutes(int minutes, int level) {
        org.junit.jupiter.api.Assertions.assertEquals(minutes * 60, MallOrderMq.delaySeconds(level));
    }
}
