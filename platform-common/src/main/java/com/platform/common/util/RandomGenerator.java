package com.platform.common.util;

import org.apache.commons.text.RandomStringGenerator;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

/**
 * @description:
 * @author: 王佳  10:04 2018/9/7
 * @version: 1.0
 * @modify: MODIFIER'S NAME YYYY/MM/DD 修改内容简述
 * @Copyright: 版权信息
 */
public class RandomGenerator {

    /**
     * 随机生成纯字符 a-z
     * */
    public static String randomStr(Integer len){
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('a', 'z').build();

        return generator.generate(len);
    }

    /**
     * 随机生成纯数字 0-9
     * */
    public static String randomNumeric(Integer len){
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', '9').build();
        return generator.generate(len);
    }

    /**
     * 随机生成数字&字母组合
     * */
    public static String randomStrAndNumeric(Integer len){
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(LETTERS, DIGITS)//过滤
                .build();
        return generator.generate(len);
    }


}
