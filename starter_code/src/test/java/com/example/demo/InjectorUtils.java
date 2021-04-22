package com.example.demo;

import java.lang.reflect.Field;
import java.lang.reflect.AccessibleObject;

public class InjectorUtils {

    public static void injectObjects(Object target, String fieldName, Object toInject) {
        try {
            Field field = (Field) target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, toInject);
            field.setAccessible(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
