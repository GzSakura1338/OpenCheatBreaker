package net.optifine.render;

public class GlAlphaState {
   public boolean enabled;
   public int func;
   public float ref;

   public GlAlphaState() {
      this(false, 519, 0.0F);
   }

   public GlAlphaState(boolean flag) {
      this(flag, 519, 0.0F);
   }

   public GlAlphaState(boolean flag, int n, float f) {
      this.enabled = flag;
      this.func = n;
      this.ref = f;
   }

   public void setState(boolean flag, int n, float f) {
      this.enabled = flag;
      this.func = n;
      this.ref = f;
   }

   public void setState(GlAlphaState glAlphaState) {
      this.enabled = glAlphaState.enabled;
      this.func = glAlphaState.func;
      this.ref = glAlphaState.ref;
   }

   public void setFuncRef(int n, float f) {
      this.func = n;
      this.ref = f;
   }

   public void setEnabled(boolean flag) {
      this.enabled = flag;
   }

   public void setEnabled() {
      this.enabled = true;
   }

   public void setDisabled() {
      this.enabled = false;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public int getFunc() {
      return this.func;
   }

   public float getRef() {
      return this.ref;
   }

   @Override
   public String toString() {
      return "enabled: " + this.enabled + ", func: " + this.func + ", ref: " + this.ref;
   }
}
