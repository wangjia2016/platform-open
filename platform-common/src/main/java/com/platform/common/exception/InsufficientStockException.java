package com.platform.common.exception;

/**
 * 数据库或缓存库存不足。调用方应回补已预扣的缓存，且不必让消息无限重试。
 */
public class InsufficientStockException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public InsufficientStockException(String message) {
        super(message);
    }
}
