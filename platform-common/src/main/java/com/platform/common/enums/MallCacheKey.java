package com.platform.common.enums;

/**
 * @description: 商城缓存key前缀
 * @author: 王佳  13:54 2020/11/07
 * @version: 1.0
 * @modify: MODIFIER'S NAME YYYY/MM/DD 修改内容简述
 * @Copyright: 版权信息
 */
public enum MallCacheKey {

    /**
     * 普通通订单缓存前缀
     * */
    NORMAL_GOODS_PREFIX("普通商品缓存前缀","NORMAL_GOODS_"),
    NORMAL_GOODS_SEMAPHORE_PREFIX("普通商品信号量前缀","NORMAL_GOODS_SEMAPHORE_"),
    FLASH_GOODS_LOCK_PREFIX("秒杀商品缓存LOCK前缀","FLASH_GOODS_LOCK_"),
    MALLGOODS_DISTRIBUTO_RRULE_PREFIX("普通商品缓存前缀","MALLGOODS_DISTRIBUTO_RRULE_");

    /**
     * 常量名称
     */
    private String type;

    /**
     * 常量值
     */
    private String typeValue;

    private MallCacheKey(String type, String typeValue) {
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
