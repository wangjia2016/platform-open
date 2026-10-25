package com.platform.mall.service.order;

/**
 * 订单消息目的地，以及 RocketMQ 默认延时等级对应的秒数。
 * 默认等级：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h。
 */
public final class MallOrderMq {

    public static final String CREATE_FLASH_TOPIC = "create-flash-mall-order";
    public static final String CREATE_FLASH_TAG = "createFlashOrderTag";
    public static final String CREATE_FLASH_DESTINATION = CREATE_FLASH_TOPIC + ":" + CREATE_FLASH_TAG;
    public static final String CREATE_FLASH_GROUP = "create-flash-mall-order-group";

    public static final String CANCEL_DESTINATION = "cancel-normal-mall-order:cancelNormalOrderTag";
    public static final String PAY_REMIND_DESTINATION = "mall-order-to-pay-notify:orderToPayNotifyTag";

    /**
     * 支付成功通知运营侧。topic/tag 历史拼写 mamager 保持不变，避免破坏已有订阅。
     */
    public static final String PAY_SUCCESS_TOPIC = "order-pay-success-notify-mamager";
    public static final String PAY_SUCCESS_TAG = "orderPayManagerNotify";
    public static final String PAY_SUCCESS_DESTINATION = PAY_SUCCESS_TOPIC + ":" + PAY_SUCCESS_TAG;

    /** 默认等级 9 = 5 分钟，早于关单。 */
    public static final int PAY_REMIND_DELAY_LEVEL = 9;
    /** 默认等级 14 = 10 分钟。 */
    public static final int UNPAID_CANCEL_DELAY_LEVEL = 14;

    private static final int[] DELAY_SECONDS = {
            0,
            1, 5, 10, 30,
            60, 120, 180, 240, 300,
            360, 420, 480, 540, 600,
            1200, 1800, 3600, 7200
    };

    private MallOrderMq() {
    }

    public static int delaySeconds(int level) {
        if (level < 0 || level >= DELAY_SECONDS.length) {
            throw new IllegalArgumentException("未知的 RocketMQ 延时等级: " + level);
        }
        return DELAY_SECONDS[level];
    }
}
