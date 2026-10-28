package net.optifine;

public class ReflectorFields {
    public ReflectorClass reflectorClass;
    public Class fieldType;
    public int fieldCount;
    public ReflectorField[] reflectorFields;

    public ReflectorFields(ReflectorClass reflectorClass, Class clazz, int n) {
        this.reflectorClass = reflectorClass;
        this.fieldType = clazz;
        if (reflectorClass.exists() && clazz != null) {
            this.reflectorFields = new ReflectorField[n];

            for (int i = 0; i < this.reflectorFields.length; i++) {
                this.reflectorFields[i] = new ReflectorField(reflectorClass, clazz, i);
            }
        }
    }

    public ReflectorClass getReflectorClass() {
        return this.reflectorClass;
    }

    public Class getFieldType() {
        return this.fieldType;
    }

    public int getFieldCount() {
        return this.fieldCount;
    }

    public ReflectorField getReflectorField(int n) {
        return n >= 0 && n < this.reflectorFields.length ? this.reflectorFields[n] : null;
    }
}
