package com.platform.common.util;

import java.util.UUID;

/**
 * <p/>
 * <p>Author:wangjia
 * <p>Date:2016/11/05
 */
public class UUIDUtils {

	/**
	 * 生成没有-的uuid 32位
	 * @return
	 */
    public static String getUUID32() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 生成uuid，有-的uuid 36位
     * @return
     */
    public static String getUUID36() {
    	return UUID.randomUUID().toString();
    }

    
    public static void main(String[] args) {
        System.out.println(getUUID36());
    }
    
}
