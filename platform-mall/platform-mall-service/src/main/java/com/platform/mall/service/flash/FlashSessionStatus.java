package com.platform.mall.service.flash;

import java.util.Date;

/**
 * 秒杀场次时间状态：0未开始 1进行中 2已结束。
 * 纯计算逻辑，便于单测；门户列表用它填充 status / countdownSeconds。
 */
public final class FlashSessionStatus {

    public static final int NOT_STARTED = 0;
    public static final int ONGOING = 1;
    public static final int ENDED = 2;

    private FlashSessionStatus() {
    }

    public static int resolve(Date startTime, Date endTime, Date now) {
        if (startTime == null || endTime == null || now == null) {
            return ENDED;
        }
        long n = now.getTime();
        if (n < startTime.getTime()) {
            return NOT_STARTED;
        }
        if (n <= endTime.getTime()) {
            return ONGOING;
        }
        return ENDED;
    }

    public static long countdownSeconds(Date startTime, Date endTime, Date now) {
        if (startTime == null || endTime == null || now == null) {
            return 0L;
        }
        int status = resolve(startTime, endTime, now);
        if (status == NOT_STARTED) {
            return Math.max(0L, (startTime.getTime() - now.getTime()) / 1000L);
        }
        if (status == ONGOING) {
            return Math.max(0L, (endTime.getTime() - now.getTime()) / 1000L);
        }
        return 0L;
    }
}
