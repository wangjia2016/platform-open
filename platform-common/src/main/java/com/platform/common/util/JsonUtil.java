package com.platform.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @desc JSON操作工具类
 * 
 * @author wangjia
 */
public class JsonUtil {

	private static ObjectMapper objectMapper = new ObjectMapper();

	// 日起格式化
	private static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

	static {
		//objectMapper.setSerializationInclusion(Include.NON_NULL);
		//对象的所有字段全部列入
		objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
		//取消默认转换timestamps形式
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false);
		//忽略空Bean转json的错误
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
		//所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
		objectMapper.setDateFormat(new SimpleDateFormat(STANDARD_FORMAT));
		//忽略 在json字符串中存在，但是在java对象中不存在对应属性的情况。防止错误
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
	}

	public static String object2Json(Object o) {
		if (o == null) {
			return null;
		}

		String s = null;

		try {
			s = objectMapper.writeValueAsString(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static <T> List<String> listObject2ListJson(List<T> objects) {
		if (objects == null) {
			return null;
		}

		List<String> lists = new ArrayList<String>();
		for (T t : objects) {
			lists.add(JsonUtil.object2Json(t));
		}

		return lists;
	}

	public static <T> List<T> listJson2ListObject(List<String> jsons, Class<T> c) {
		if (jsons == null) {
			return null;
		}

		List<T> ts = new ArrayList<T>();
		for (String j : jsons) {
			ts.add(JsonUtil.json2Object(j, c));
		}

		return ts;
	}

	public static <T> T json2Object(String json, Class<T> c) {
		if (StringUtils.hasLength(json) == false) {
			return null;
		}

		T t = null;
		try {
			t = objectMapper.readValue(json, c);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	@SuppressWarnings("unchecked")
	public static <T> T json2Object(String json, TypeReference<T> tr) {
		if (StringUtils.hasLength(json) == false) {
			return null;
		}

		T t = null;
		try {
			t = (T) objectMapper.readValue(json, tr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (T) t;
	}
}
