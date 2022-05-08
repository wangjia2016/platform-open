package com.platform.common.enums;

public enum ThreadPoolNameEnum {

    GOODS_POOL("商品线程池","goods_pool"),

    ORDER_POOL("订单线程池","order_pool");


    /**
     * 常量名称
     */
    private String type;

    /**
     * 常量值
     */
    private String typeValue;

    private ThreadPoolNameEnum(String type, String typeValue) {
        this.type = type;
        this.typeValue = typeValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }
}
