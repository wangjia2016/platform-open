package com.platform.mall.service.flash;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlashSessionStatusTest {

    @Test
    void resolveNotStartedOngoingEnded() {
        Date start = new Date(1_000_000L);
        Date end = new Date(2_000_000L);
        assertEquals(FlashSessionStatus.NOT_STARTED, FlashSessionStatus.resolve(start, end, new Date(500_000L)));
        assertEquals(FlashSessionStatus.ONGOING, FlashSessionStatus.resolve(start, end, new Date(1_500_000L)));
        assertEquals(FlashSessionStatus.ONGOING, FlashSessionStatus.resolve(start, end, end));
        assertEquals(FlashSessionStatus.ENDED, FlashSessionStatus.resolve(start, end, new Date(2_500_000L)));
        assertEquals(FlashSessionStatus.ENDED, FlashSessionStatus.resolve(null, end, new Date()));
    }

    @Test
    void countdownSecondsByStatus() {
        Date start = new Date(10_000_000L);
        Date end = new Date(20_000_000L);
        assertEquals(5_000L, FlashSessionStatus.countdownSeconds(start, end, new Date(5_000_000L)));
        assertEquals(5_000L, FlashSessionStatus.countdownSeconds(start, end, new Date(15_000_000L)));
        assertEquals(0L, FlashSessionStatus.countdownSeconds(start, end, new Date(25_000_000L)));
    }
}
