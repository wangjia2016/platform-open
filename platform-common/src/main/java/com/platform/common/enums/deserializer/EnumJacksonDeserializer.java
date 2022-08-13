package com.platform.common.enums.deserializer;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @Description 针对Enum类型的反序列化器,
 *  将code字符串或json对象格式的输入转成enum对象
 * @Author wangjia
 * @Date 2022/8/13 10:29
 * @Version 1.0
 **/
@Slf4j
@Setter
public class EnumJacksonDeserializer extends JsonDeserializer<Enum<?>> implements ContextualDeserializer {

    private Class<?> clazz;

    private final static String CODE = "typeValue";


    @Override
    public Enum<?> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Class<?> enumType = clazz;
        if (Objects.isNull(enumType) || !enumType.isEnum()) {
            return null;
        }
        JsonToken jsonToken =  jsonParser.getCurrentToken();
        String code = null;
        //json对象字符串
        if(jsonToken.equals(JsonToken.START_OBJECT)){
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if(node.get(CODE) != null){
                code = node.get(CODE).asText();
            }
        } //json文本
        else if(jsonToken.equals(JsonToken.VALUE_STRING)){
            code = jsonParser.getValueAsString();
        } //整数值
        else if(jsonToken.equals(JsonToken.VALUE_NUMBER_INT)){
            code = String.valueOf(jsonParser.getIntValue());
        }
        Method method = getMethod(clazz);
        Enum<?>[] enumConstants = (Enum<?>[]) enumType.getEnumConstants();
        // 将值与枚举对象对应并缓存
        for (Enum<?> e : enumConstants) {
            try {
                if (Objects.equals(String.valueOf(method.invoke(e)), code)) {
                    return e;
                }
            } catch (IllegalAccessException  | InvocationTargetException ex) {
                log.error("获取枚举值错误!!! ", ex);
            }
        }
        return null;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        Class<?> rawCls = ctxt.getContextualType().getRawClass();
        EnumJacksonDeserializer converter = new EnumJacksonDeserializer();
        converter.setClazz(rawCls);
        return converter;
    }

    private <T> Method getMethod(Class<T> enumType) {
        Field field = dealEnumType(enumType).orElseThrow(() -> new IllegalArgumentException(String.format(
                        "类:%s 找不到 EnumValue注解", enumType.getName())));
        String mn = String.format("get%s", StringUtils.capitalize(field.getName()));
        Method method = null;
        try {
            method = enumType.getMethod(mn);
        } catch (NoSuchMethodException e) {
            log.error("找不到指定get方法");
        }
        return method;
    }

    private Optional<Field> dealEnumType(Class<?> clazz) {
        return clazz.isEnum() ?
                Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(EnumValue.class)).findFirst() : Optional.empty();
    }
}
