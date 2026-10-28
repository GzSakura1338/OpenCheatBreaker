package net.optifine;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectorRaw {
    public static Field getField(Class clazz, Class clazz2) {
        try {
            Field[] declaredFields = clazz.getDeclaredFields();

            for (int i = 0; i < declaredFields.length; i++) {
                Field field = declaredFields[i];
                if (field.getType() == clazz2) {
                    field.setAccessible(true);
                    return field;
                }
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static Field[] getFields(Class clazz, Class clazz2) {
        try {
            Field[] declaredFields = clazz.getDeclaredFields();
            return getFields(declaredFields, clazz2);
        } catch (Exception e) {
            return null;
        }
    }

    public static Field[] getFields(Field[] fields, Class clazz) {
        try {
            ArrayList list = new ArrayList();

            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (field.getType() == clazz) {
                    field.setAccessible(true);
                    list.add(field);
                }
            }

            return (Field[])list.toArray(new Field[list.size()]);
        } catch (Exception e) {
            return null;
        }
    }

    public static Field[] getFieldsAfter(Class clazz, Field field, Class clazz2) {
        try {
            Field[] declaredFields = clazz.getDeclaredFields();
            List asList = Arrays.asList(declaredFields);
            int indexOf = asList.indexOf(field);
            if (indexOf < 0) {
                return new Field[0];
            }

            List subList = asList.subList(indexOf + 1, asList.size());
            Field[] toArray = (Field[])subList.toArray(new Field[subList.size()]);
            return getFields(toArray, clazz2);
        } catch (Exception e) {
            return null;
        }
    }

    public static Field[] getFields(Object obj, Field[] fields, Class clazz, Object obj2) {
        try {
            ArrayList list = new ArrayList();

            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (field.getType() == clazz) {
                    boolean isStatic = Modifier.isStatic(field.getModifiers());
                    if ((obj != null || isStatic) && (obj == null || !isStatic)) {
                        field.setAccessible(true);
                        Object obj3 = field.get(obj);
                        if (obj3 == obj2) {
                            list.add(field);
                        } else if (obj3 != null && obj2 != null && obj3.equals(obj2)) {
                            list.add(field);
                        }
                    }
                }
            }

            return (Field[])list.toArray(new Field[list.size()]);
        } catch (Exception e) {
            return null;
        }
    }

    public static Field getField(Class clazz, Class clazz2, int n) {
        Field[] fields = getFields(clazz, clazz2);
        return n >= 0 && n < fields.length ? fields[n] : null;
    }

    public static Field getFieldAfter(Class clazz, Field field, Class clazz2, int n) {
        Field[] fieldsAfter = getFieldsAfter(clazz, field, clazz2);
        return n >= 0 && n < fieldsAfter.length ? fieldsAfter[n] : null;
    }

    public static Object getFieldValue(Object obj, Class clazz, Class clazz2) {
        ReflectorField reflectorField = getReflectorField(clazz, clazz2);
        return reflectorField == null ? null : (!reflectorField.exists() ? null : Reflector.getFieldValue(obj, reflectorField));
    }

    public static Object getFieldValue(Object obj, Class clazz, Class clazz2, int n) {
        ReflectorField reflectorField = getReflectorField(clazz, clazz2, n);
        return reflectorField == null ? null : (!reflectorField.exists() ? null : Reflector.getFieldValue(obj, reflectorField));
    }

    public static boolean setFieldValue(Object obj, Class clazz, Class clazz2, Object obj2) {
        ReflectorField reflectorField = getReflectorField(clazz, clazz2);
        return reflectorField == null ? false : (!reflectorField.exists() ? false : Reflector.setFieldValue(obj, reflectorField, obj2));
    }

    public static boolean setFieldValue(Object obj, Class clazz, Class clazz2, int n, Object obj2) {
        ReflectorField reflectorField = getReflectorField(clazz, clazz2, n);
        return reflectorField == null ? false : (!reflectorField.exists() ? false : Reflector.setFieldValue(obj, reflectorField, obj2));
    }

    public static ReflectorField getReflectorField(Class clazz, Class clazz2) {
        Field field = getField(clazz, clazz2);
        if (field == null) {
            return null;
        }

        ReflectorClass reflectorClass = new ReflectorClass(clazz);
        return new ReflectorField(reflectorClass, field.getName());
    }

    public static ReflectorField getReflectorField(Class clazz, Class clazz2, int n) {
        Field field = getField(clazz, clazz2, n);
        if (field == null) {
            return null;
        }

        ReflectorClass reflectorClass = new ReflectorClass(clazz);
        return new ReflectorField(reflectorClass, field.getName());
    }
}
