package com.cheatbreaker.client.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ReflectionUtils {
    public static Object findObject(Object obj, Class clazz, Class clazz2, int n) {
        Class clazz3 = obj.getClass();

        while (!clazz3.equals(clazz) && clazz3.getSuperclass() != null) {
            clazz3 = clazz3.getSuperclass();
        }

        int n2 = 0;
        Field[] declaredFields = clazz3.getDeclaredFields();

        for (int i = 0; i < declaredFields.length; i++) {
            if (clazz2.equals(declaredFields[i].getType())) {
                if (n2 == n) {
                    try {
                        declaredFields[i].setAccessible(true);
                        return declaredFields[i].get(obj);
                    } catch (IllegalAccessException e) {
                    }
                }

                n2++;
            }
        }

        return null;
    }

    public static Object findObject(Object obj, Class clazz, Class clazz2) {
        return findObject(obj, clazz, clazz2, 0);
    }

    public static ArrayList<Field> findFields(Object obj, Class clazz, Class clazz2) {
        ArrayList list = new ArrayList();

        for (Class clazz3 = obj.getClass(); !clazz3.equals(clazz) && clazz3.getSuperclass() != null; clazz3 = clazz3.getSuperclass()) {
            Field[] declaredFields = clazz3.getDeclaredFields();

            for (int i = 0; i < declaredFields.length; i++) {
                if (clazz2.equals(declaredFields[i].getType())) {
                    declaredFields[i].setAccessible(true);
                    list.add(declaredFields[i]);
                }
            }
        }

        return list;
    }

    public static Object findObject(Object obj, String str) {
        Field[] fields = obj.getClass().getFields();

        for (int i = 0; i < fields.length; i++) {
            if (str.equals(fields[i].getName())) {
                try {
                    fields[i].setAccessible(true);
                    return fields[i].get(obj);
                } catch (IllegalAccessException e) {
                }
            }
        }

        return null;
    }

    public static Field findField(Object obj, Class clazz, Class clazz2) {
        return findField(obj, clazz, clazz2, 0);
    }

    public static boolean classExists(String str) {
        try {
            Class.forName(str);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static Method findMethod(int n, Class clazz, Class clazz2, Class... clazzs) {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        int n2 = 0;

        for (int i = 0; i < declaredMethods.length; i++) {
            if (clazz2.equals(declaredMethods[i].getReturnType())) {
                Class[] parameterTypes = declaredMethods[i].getParameterTypes();
                if (clazzs.length == parameterTypes.length) {
                    boolean flag = true;

                    for (int j = 0; j < clazzs.length; j++) {
                        if (clazzs[j] != parameterTypes[j]) {
                            flag = false;
                        }
                    }

                    if (n2 == n) {
                        declaredMethods[i].setAccessible(true);
                        return declaredMethods[i];
                    }
                }

                n2++;
            }
        }

        return null;
    }

    public static Method findMethod(Class clazz, Class clazz2, Class... clazzs) {
        return findMethod(0, clazz, clazz2, clazzs);
    }

    public static Field findField(Object obj, Class clazz, Class clazz2, int n) {
        Class clazz3 = obj.getClass();

        while (!clazz3.equals(clazz) && clazz3.getSuperclass() != null) {
            clazz3 = clazz3.getSuperclass();
        }

        int n2 = 0;
        Field[] declaredFields = clazz3.getDeclaredFields();

        for (int i = 0; i < declaredFields.length; i++) {
            if (clazz2.equals(declaredFields[i].getType())) {
                if (n2 == n) {
                    declaredFields[i].setAccessible(true);
                    return declaredFields[i];
                }

                n2++;
            }
        }

        return null;
    }
}
