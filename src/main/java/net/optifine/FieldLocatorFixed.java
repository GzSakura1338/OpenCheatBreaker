package net.optifine;

import java.lang.reflect.Field;

public class FieldLocatorFixed implements IFieldLocator {
    public Field field;

    public FieldLocatorFixed(Field field) {
        this.field = field;
    }

    @Override
    public Field getField() {
        return this.field;
    }
}
