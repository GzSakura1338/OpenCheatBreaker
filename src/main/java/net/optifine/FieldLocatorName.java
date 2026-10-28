package net.optifine;

import java.lang.reflect.Field;

public class FieldLocatorName implements IFieldLocator {
    public ReflectorClass reflectorClass = null;
    public String targetFieldName = null;

    public FieldLocatorName(ReflectorClass reflectorClass, String str) {
        this.reflectorClass = reflectorClass;
        this.targetFieldName = str;
    }

    @Override
    public Field getField() {
        Class targetClass = this.reflectorClass.getTargetClass();
        if (targetClass == null) {
            return null;
        }

        try {
            Field declaredField = this.getDeclaredField(targetClass, this.targetFieldName);
            declaredField.setAccessible(true);
            return declaredField;
        } catch (NoSuchFieldException e) {
            return null;
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public Field getDeclaredField(Class clazz, String str) throws NoSuchFieldException {
        Field[] declaredFields = clazz.getDeclaredFields();

        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
            if (field.getName().equals(str)) {
                return field;
            }
        }

        if (clazz == Object.class) {
            throw new NoSuchFieldException(str);
        } else {
            return this.getDeclaredField(clazz.getSuperclass(), str);
        }
    }
}
