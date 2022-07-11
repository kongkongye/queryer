package com.kongkongye.backend.queryer.common;

import org.springframework.lang.Nullable;

import java.lang.reflect.*;

public class ReflectionUtil {
    /**
     * 获取类上定义的指定名字的字段(不检测父类)
     *
     * @param cls       类
     * @param fieldName 字段名
     * @return 不存在返回null
     */
    public static Field getDeclaredField(Class cls, String fieldName) {
        try {
            return cls.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    /**
     * 获取Field的值(即使Field私有也可以)
     */
    public static Object getField(Field field, Object obj) {
        boolean access = field.isAccessible();
        field.setAccessible(true);
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            return null;
        } finally {
            field.setAccessible(access);
        }
    }

    /**
     * 设置Field的值(即使Field私有也可以)
     */
    public static void setField(Field field, Object obj, Object value) {
        boolean access = field.isAccessible();
        field.setAccessible(true);
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        field.setAccessible(access);
    }

    /**
     * 获取Method的值(即使Method私有也可以)
     */
    public static Object getMethod(Method method, @Nullable Object obj, Object... args) {
        boolean access = method.isAccessible();
        method.setAccessible(true);
        try {
            return invoke(obj, method, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            method.setAccessible(access);
        }
    }

    /**
     * 调用方法
     *
     * @param instance 可能是代理Proxy,调用静态方法时为null
     * @param method   方法
     * @param args     变量
     * @return 调用的结果
     */
    public static Object invoke(@Nullable Object instance, Method method, Object... args) {
        try {
            if (instance != null && Proxy.isProxyClass(instance.getClass())) {
                return Proxy.getInvocationHandler(instance).invoke(instance, method, args);
            } else {
                return method.invoke(instance, args);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    /**
     * 使用空构造器
     *
     * @see #newInstance(Class, Class[], Object[])
     */
    public static <T> T newInstance(Class<T> c) {
        return newInstance(c, new Class[0], new Object[0]);
    }

    /**
     * 新建实例(即使构造器私有也可以)
     *
     * @return 异常返回null
     */
    public static <T> T newInstance(Class<T> c, Class[] classParams, Object[] args) {
        try {
            Constructor<T> constructor = c.getDeclaredConstructor(classParams);
            boolean access = constructor.isAccessible();
            constructor.setAccessible(true);
            T result = constructor.newInstance(args);
            constructor.setAccessible(access);
            return result;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
