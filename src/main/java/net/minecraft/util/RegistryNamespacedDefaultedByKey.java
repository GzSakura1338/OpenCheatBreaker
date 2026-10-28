package net.minecraft.util;

public class RegistryNamespacedDefaultedByKey extends RegistryNamespaced {
    public String text;
    public Object defaultValue;

    public RegistryNamespacedDefaultedByKey(String str) {
        this.text = str;
    }

    @Override
    public void addObject(int n, String str, Object obj) {
        if (this.text.equals(str)) {
            this.defaultValue = obj;
        }

        super.addObject(n, str, obj);
    }

    @Override
    public Object getObject(String str) {
        Object object = super.getObject(str);
        return object == null ? this.defaultValue : object;
    }

    @Override
    public Object getObjectById(int n) {
        Object objectById = super.getObjectById(n);
        return objectById == null ? this.defaultValue : objectById;
    }

    @Override
    public Object getObject(Object obj) {
        return this.getObject((String)obj);
    }
}
