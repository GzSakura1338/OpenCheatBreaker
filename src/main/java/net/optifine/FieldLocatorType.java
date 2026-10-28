package net.optifine;

import java.lang.reflect.Field;

public class FieldLocatorType implements IFieldLocator {
    public ReflectorClass reflectorClass = null;
    public Class targetFieldType = null;
    public int targetFieldIndex;

    public FieldLocatorType(ReflectorClass reflectorClass, Class clazz) {
        this(reflectorClass, clazz, 0);
    }

    public FieldLocatorType(ReflectorClass reflectorClass, Class clazz, int n) {
        this.reflectorClass = reflectorClass;
        this.targetFieldType = clazz;
        this.targetFieldIndex = n;
    }

    @Override
    public Field getField() {
        Class targetClass = this.reflectorClass.getTargetClass();
        if (targetClass == null) {
            return null;
        }

        try {
            Field[] declaredFields = targetClass.getDeclaredFields();
            int n = 0;

            for (int i = 0; i < declaredFields.length; i++) {
                Field field = declaredFields[i];
                if (field.getType() == this.targetFieldType) {
                    if (n == this.targetFieldIndex) {
                        field.setAccessible(true);
                        return field;
                    }

                    n++;
                }
            }

            return null;
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
