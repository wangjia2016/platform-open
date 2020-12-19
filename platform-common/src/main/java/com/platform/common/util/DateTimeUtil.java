package com.platform.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Description
 * @ClassName DateTimeUtil
 * @Author wangjia
 * @date 2020.11.11 10:52
 */
public class DateTimeUtil {

    private static final ConcurrentMap<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();

    private static final int PATTERN_CACHE_SIZE = 500;
    /**
     * Date转换为格式化时间
     * @param date date
     * @param pattern 格式
     * @return
     */
    public static String format(Date date, String pattern){
        return format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()), pattern);
    }

    /**
     * localDateTime转换为格式化时间
     * @param localDateTime localDateTime
     * @param pattern 格式
     * @return
     */
    public static String format(LocalDateTime localDateTime, String pattern){
        DateTimeFormatter formatter = createCacheFormatter(pattern);
        return localDateTime.format(formatter);
    }

    /**
     * 格式化字符串转为Date
     * @param time 格式化时间
     * @param pattern 格式
     * @return
     */
    public static Date parseDate(String time, String pattern){
        return Date.from(parseLocalDateTime(time, pattern).atZone(ZoneId.systemDefault()).toInstant());

    }

    /**
     * 格式化字符串转为LocalDateTime
     * @param time 格式化时间
     * @param pattern 格式
     * @return
     */
    public static LocalDateTime parseLocalDateTime(String time, String pattern){
        DateTimeFormatter formatter = createCacheFormatter(pattern);
        return LocalDateTime.parse(time, formatter);
    }

    /**
     * 在缓存中创建DateTimeFormatter
     * @param pattern 格式
     * @return
     */
    private static DateTimeFormatter createCacheFormatter(String pattern){
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException("Invalid pattern specification");
        }
        DateTimeFormatter formatter = FORMATTER_CACHE.get(pattern);
        if(formatter == null){
            if(FORMATTER_CACHE.size() < PATTERN_CACHE_SIZE){
                formatter = DateTimeFormatter.ofPattern(pattern);
                DateTimeFormatter oldFormatter = FORMATTER_CACHE.putIfAbsent(pattern, formatter);
                if(oldFormatter != null){
                    formatter = oldFormatter;
                }
            }
        }

        return formatter;
    }
}
