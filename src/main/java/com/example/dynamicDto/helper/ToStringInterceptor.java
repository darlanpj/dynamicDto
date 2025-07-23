package com.example.dynamicDto.helper;

import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Field;
import java.util.StringJoiner;

public class ToStringInterceptor {

    @RuntimeType
    public static String intercept(@This Object obj) {
        Class<?> clazz = obj.getClass();
        StringJoiner joiner = new StringJoiner(", ", clazz.getSimpleName() + "[", "]");

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                joiner.add(field.getName() + "=" + value);
            } catch (IllegalAccessException e) {
                joiner.add(field.getName() + "=<inacessível>");
            }
        }

        return joiner.toString();
    }
}
