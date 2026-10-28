package net.optifine;

import java.lang.reflect.Constructor;

public class ReflectorConstructor {
    public ReflectorClass reflectorClass = null;
    public Class[] parameterTypes = null;
    public boolean checked = false;
    public Constructor targetConstructor = null;

    public ReflectorConstructor(ReflectorClass reflectorClass, Class[] clazzs) {
        this.reflectorClass = reflectorClass;
        this.parameterTypes = clazzs;
        Constructor targetConstructor = this.getTargetConstructor();
    }

    public Constructor getTargetConstructor() {
        if (this.checked) {
            return this.targetConstructor;
        }

        this.checked = true;
        Class targetClass = this.reflectorClass.getTargetClass();
        if (targetClass == null) {
            return null;
        }

        try {
            this.targetConstructor = findConstructor(targetClass, this.parameterTypes);
            if (this.targetConstructor != null) {
                this.targetConstructor.setAccessible(true);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return this.targetConstructor;
    }

    public static Constructor findConstructor(Class clazz, Class[] clazzs) {
        Constructor[] declaredConstructors = clazz.getDeclaredConstructors();

        for (int i = 0; i < declaredConstructors.length; i++) {
            Constructor constructor = declaredConstructors[i];
            Class[] parameterTypes = constructor.getParameterTypes();
            if (Reflector.matchesTypes(clazzs, parameterTypes)) {
                return constructor;
            }
        }

        return null;
    }

    public boolean exists() {
        return this.checked ? this.targetConstructor != null : this.getTargetConstructor() != null;
    }

    public void deactivate() {
        this.checked = true;
        this.targetConstructor = null;
    }
}
