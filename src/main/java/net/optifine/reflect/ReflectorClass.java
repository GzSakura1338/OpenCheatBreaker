package net.optifine.reflect;

public class ReflectorClass implements IResolvable {
   public String targetClassName = null;
   public boolean checked = false;
   public Class targetClass = null;

   public ReflectorClass(String str) {
      this.targetClassName = str;
      ReflectorResolver.register(this);
   }

   public ReflectorClass(Class clazz) {
      this.targetClass = clazz;
      this.targetClassName = clazz.getName();
      this.checked = true;
   }

   public Class getTargetClass() {
      if (this.checked) {
         return this.targetClass;
      }

      this.checked = true;

      try {
         this.targetClass = Class.forName(this.targetClassName);
      } catch (ClassNotFoundException e) {
      } catch (Throwable e) {
         e.printStackTrace();
      }

      return this.targetClass;
   }

   public boolean exists() {
      return this.getTargetClass() != null;
   }

   public String getTargetClassName() {
      return this.targetClassName;
   }

   public boolean isInstance(Object obj) {
      return this.getTargetClass() == null ? false : this.getTargetClass().isInstance(obj);
   }

   public ReflectorField makeField(String str) {
      return new ReflectorField(this, str);
   }

   public ReflectorMethod makeMethod(String str) {
      return new ReflectorMethod(this, str);
   }

   public ReflectorMethod makeMethod(String str, Class[] clazzs) {
      return new ReflectorMethod(this, str, clazzs);
   }

   @Override
   public void resolve() {
      Class targetClass = this.getTargetClass();
   }
}
