package net.minecraft.util;

public class RegistryDefaulted extends RegistrySimple {
    public Object defaultObject;

    public RegistryDefaulted(Object obj) {
        this.defaultObject = obj;
    }

    @Override
    public Object getObject(Object obj) {
        Object object = super.getObject(obj);
        return object == null ? this.defaultObject : object;
    }
}
