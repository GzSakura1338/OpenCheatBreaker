package net.optifine;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class ReflectorMethod {
    public ReflectorClass reflectorClass = null;
    public String targetMethodName = null;
    public Class[] targetMethodParameterTypes = null;
    public boolean checked = false;
    public Method targetMethod = null;

    public ReflectorMethod(ReflectorClass reflectorClass, String str) {
        this(reflectorClass, str, (Class[])null, false);
    }

    public ReflectorMethod(ReflectorClass reflectorClass, String str, Class[] clazzs) {
        this(reflectorClass, str, clazzs, false);
    }

    public ReflectorMethod(ReflectorClass reflectorClass, String str, Class[] clazzs, boolean flag) {
        this.reflectorClass = reflectorClass;
        this.targetMethodName = str;
        this.targetMethodParameterTypes = clazzs;
        if (!flag) {
            Method targetMethod = this.getTargetMethod();
        }
    }

    public Method getTargetMethod() {
        if (this.checked) {
            return this.targetMethod;
        }

        this.checked = true;
        Class targetClass = this.reflectorClass.getTargetClass();
        if (targetClass == null) {
            return null;
        }

        try {
            if (this.targetMethodParameterTypes == null) {
                Method[] methods = getMethods(targetClass, this.targetMethodName);
                if (methods.length <= 0) {
                    return null;
                }

                if (methods.length > 1) {
                    Config.warn("(Reflector) More than one method found: " + targetClass.getName() + "." + this.targetMethodName);

                    for (int i = 0; i < methods.length; i++) {
                        Method method = methods[i];
                        Config.warn("(Reflector)  - " + method);
                    }

                    return null;
                }

                this.targetMethod = methods[0];
            } else {
                this.targetMethod = getMethod(targetClass, this.targetMethodName, this.targetMethodParameterTypes);
            }

            if (this.targetMethod == null) {
                return null;
            }

            this.targetMethod.setAccessible(true);
            return this.targetMethod;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean exists() {
        return this.checked ? this.targetMethod != null : this.getTargetMethod() != null;
    }

    public Class getReturnType() {
        Method targetMethod = this.getTargetMethod();
        return targetMethod == null ? null : targetMethod.getReturnType();
    }

    public void deactivate() {
        this.checked = true;
        this.targetMethod = null;
    }

    public static Method getMethod(Class clazz, String str, Class[] clazzs) {
        Method[] declaredMethods = clazz.getDeclaredMethods();

        for (int i = 0; i < declaredMethods.length; i++) {
            Method method = declaredMethods[i];
            if (method.getName().equals(str)) {
                Class[] parameterTypes = method.getParameterTypes();
                if (Reflector.matchesTypes(clazzs, parameterTypes)) {
                    return method;
                }
            }
        }

        return null;
    }

    public static Method[] getMethods(Class clazz, String str) {
        ArrayList list = new ArrayList();
        Method[] declaredMethods = clazz.getDeclaredMethods();

        for (int i = 0; i < declaredMethods.length; i++) {
            Method method = declaredMethods[i];
            if (method.getName().equals(str)) {
                list.add(method);
            }
        }

        return (Method[])list.toArray(new Method[list.size()]);
    }
}
