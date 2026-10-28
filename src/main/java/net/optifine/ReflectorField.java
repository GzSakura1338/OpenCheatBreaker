package net.optifine;

import java.lang.reflect.Field;

public class ReflectorField {
    public IFieldLocator fieldLocator = null;
    public boolean checked = false;
    public Field targetField = null;

    public ReflectorField(ReflectorClass reflectorClass, String str) {
        this(new FieldLocatorName(reflectorClass, str));
    }

    public ReflectorField(ReflectorClass reflectorClass, String str, boolean flag) {
        this(new FieldLocatorName(reflectorClass, str), flag);
    }

    public ReflectorField(ReflectorClass reflectorClass, Class clazz) {
        this(reflectorClass, clazz, 0);
    }

    public ReflectorField(ReflectorClass reflectorClass, Class clazz, int n) {
        this(new FieldLocatorType(reflectorClass, clazz, n));
    }

    public ReflectorField(Field field) {
        this(new FieldLocatorFixed(field));
    }

    public ReflectorField(IFieldLocator fieldLocator) {
        this(fieldLocator, false);
    }

    public ReflectorField(IFieldLocator fieldLocator, boolean flag) {
        this.fieldLocator = fieldLocator;
        if (!flag) {
            this.getTargetField();
        }
    }

    public Field getTargetField() {
        if (this.checked) {
            return this.targetField;
        }

        this.checked = true;
        this.targetField = this.fieldLocator.getField();
        if (this.targetField != null) {
            this.targetField.setAccessible(true);
        }

        return this.targetField;
    }

    public Object getValue() {
        return Reflector.getFieldValue(null, this);
    }

    public void setValue(Object obj) {
        Reflector.setFieldValue(null, this, obj);
    }

    public void setValue(Object obj, Object obj2) {
        Reflector.setFieldValue(obj, this, obj2);
    }

    public boolean exists() {
        return this.getTargetField() != null;
    }
}
