package com.platform.common.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * Created by WangJia on 2016/11/9.
 * 自定义缓存数据 key 生成策略
 */
public class DefaultKeyGenerator implements KeyGenerator {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static String[] types = { "java.lang.Integer", "java.lang.Double",
            "java.lang.Float", "java.lang.Long", "java.lang.Short","java.util.Date",
            "java.lang.Byte", "java.lang.Boolean", "java.lang.Char",
            "java.lang.String", "int", "double", "long", "short", "byte",
            "boolean", "char", "float" };

    @Override
    public Object generate(Object target, Method method, Object... params) {
        return getKey(target,method,params);
        //return new DefaultKey(target, method, params);
    }

    /**
     * 拼接key
     * @param params
     * @return String
     */
    private String getKey(Object target, Method method,Object... params){
        Object[] args = params;
        StringBuilder sb = new StringBuilder();

        sb.append(target.getClass().getName()+"_");
        sb.append(method.getName()+"_");

        boolean clazzFlag = true;
        for(int k=0; k<args.length; k++){
            Object arg = args[k];
            if(null==arg) {
                continue;
            }
            // 获取对象类型
            String typeName = arg.getClass().getName();
            for (String t : types) {
                if (t.equals(typeName)) {
                    //基本类型
                    sb.append(arg+"_");
                    clazzFlag = false;
                    break;
                }
            }

            if (clazzFlag) {
                //非基本类型
                sb.append(getFieldsValue(arg));
            }
        }
        return sb.toString();
    }

    /**
     * 得到参数的值
     * @param obj
     */
    public static String getFieldsValue(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();

        StringBuilder sb = new StringBuilder();

        if(!(obj.getClass().getSuperclass().getName().equals("java.lang.Object"))){
            Field[] fieldsSuper = obj.getClass().getSuperclass().getDeclaredFields();
            int length = fields.length;
            int length1 = fieldsSuper.length;
            fields = Arrays.copyOf(fields, length+length1);//数组扩容
            System.arraycopy(fieldsSuper, 0, fields, length, length1);
        }
        for (Field f : fields) {
            try {
                f.setAccessible(true);
                if(null!=f.get(obj)){
                    for (String str : types) {
                        if (f.getType().getName().equals(str)){
                            if(str.equals("java.util.Date")){
                                sb.append(f.getName() + "=" +new SimpleDateFormat("yyyyMMddHHmmss").format(f.get(obj))+"_");
                            }else{
                                sb.append(f.getName() + "=" + f.get(obj)+"_");
                            }
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        System.out.println(sb.toString());
        return sb.toString();
    }
}