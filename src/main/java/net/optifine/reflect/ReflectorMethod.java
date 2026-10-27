package net.optifine.reflect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import net.optifine.Log;

public class ReflectorMethod implements IResolvable {
   public ReflectorClass reflectorClass = null;
   public String targetMethodName = null;
   public Class[] targetMethodParameterTypes = null;
   public boolean checked = false;
   public Method targetMethod = null;

   public ReflectorMethod(ReflectorClass reflectorClass, String str) {
      this(reflectorClass, str, (Class[])null);
   }

   public ReflectorMethod(ReflectorClass reflectorClass, String str, Class[] clazzs) {
      this.reflectorClass = reflectorClass;
      this.targetMethodName = str;
      this.targetMethodParameterTypes = clazzs;
      ReflectorResolver.register(this);
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
               Log.warn("(Reflector) More than one method found: " + targetClass.getName() + "." + this.targetMethodName);

               for (int i = 0; i < methods.length; i++) {
                  Method method = methods[i];
                  Log.warn("(Reflector)  - " + method);
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

   public Object call(Object... objs) {
      return Reflector.call(this, objs);
   }

   public boolean callBoolean(Object... objs) {
      return Reflector.callBoolean(this, objs);
   }

   public int callInt(Object... objs) {
      return Reflector.callInt(this, objs);
   }

   public float callFloat(Object... objs) {
      return Reflector.callFloat(this, objs);
   }

   public double callDouble(Object... objs) {
      return Reflector.callDouble(this, objs);
   }

   public String callString(Object... objs) {
      return Reflector.callString(this, objs);
   }

   public Object call(Object obj) {
      return Reflector.call(this, obj);
   }

   public boolean callBoolean(Object obj) {
      return Reflector.callBoolean(this, obj);
   }

   public int callInt(Object obj) {
      return Reflector.callInt(this, obj);
   }

   public float callFloat(Object obj) {
      return Reflector.callFloat(this, obj);
   }

   public double callDouble(Object obj) {
      return Reflector.callDouble(this, obj);
   }

   public String callString1(Object obj) {
      return Reflector.callString(this, obj);
   }

   public void callVoid(Object... objs) {
      Reflector.callVoid(this, objs);
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

   @Override
   public void resolve() {
      Method targetMethod = this.getTargetMethod();
   }
}
