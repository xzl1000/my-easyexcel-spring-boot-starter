package com.xuzl.myeasyexcel.utils;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName ReflectUtils.java
 * @Description TODO
 * @createTime 2023-10-23 15:52
 */
public class ReflectUtils {
    /**
     * 获取属性（含父类）
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        return findField(clazz, fieldName, 0);
    }

    private static Field findField(Class<?> clazz, String fieldName,int deep) {
        if (deep > 5) {
            return null;
        }

        if (clazz == null) {
            return null;
        }
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return findField(clazz.getSuperclass(), fieldName, deep + 1);
        }
    }


    public static Class<?> getGenericClass(Class<?> cls) {
        return getGenericClass(cls, 0);
    }

    public static <T> Class<T> getGenericClass(Class<?> cls, int i) {
        try {
            Type superClass = cls.getGenericSuperclass();
            if (superClass instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) superClass;
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                Type genericClass = typeArguments[i];

                // handle nested generic type
                if (genericClass instanceof ParameterizedType) {
                    return (Class<T>) ((ParameterizedType) genericClass).getRawType();
                }

                // handle array generic type
                if (genericClass instanceof GenericArrayType) {
                    return (Class<T>) ((GenericArrayType) genericClass).getGenericComponentType();
                }

                // Requires JDK 7 or higher, Foo<int[]> is no longer GenericArrayType
                if (((Class) genericClass).isArray()) {
                    return (Class<T>) ((Class) genericClass).getComponentType();
                }
                return (Class<T>) genericClass;
            } else {
                throw new IllegalArgumentException(cls.getName() + " generic type undefined!");
            }
        } catch (Throwable e) {
            throw new IllegalArgumentException(cls.getName() + " generic type undefined!", e);
        }
    }
}
