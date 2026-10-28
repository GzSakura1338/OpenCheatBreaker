package net.minecraft.util;

public class TupleIntJsonSerializable {
    public int integerValue;
    public IJsonSerializable jsonSerializableValue;

    public int getIntegerValue() {
        return this.integerValue;
    }

    public void setIntegerValue(int n) {
        this.integerValue = n;
    }

    public IJsonSerializable getJsonSerializableValue() {
        return this.jsonSerializableValue;
    }

    public void setJsonSerializableValue(IJsonSerializable jsonSerializable) {
        this.jsonSerializableValue = jsonSerializable;
    }
}
