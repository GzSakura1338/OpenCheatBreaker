package net.optifine.shaders.uniform;

import net.optifine.expr.IExpression;
import net.optifine.shaders.SMCLog;

public class CustomUniform {
   public String name;
   public UniformType type;
   public IExpression expression;
   public ShaderUniformBase shaderUniform;

   public CustomUniform(String str, UniformType uniformType, IExpression expression) {
      this.name = str;
      this.type = uniformType;
      this.expression = expression;
      this.shaderUniform = uniformType.makeShaderUniform(str);
   }

   public void setProgram(int n) {
      this.shaderUniform.setProgram(n);
   }

   public void update() {
      if (this.shaderUniform.isDefined()) {
         try {
            this.type.updateUniform(this.expression, this.shaderUniform);
         } catch (RuntimeException e) {
            SMCLog.severe("Error updating custom uniform: " + this.shaderUniform.getName());
            SMCLog.severe(e.getClass().getName() + ": " + e.getMessage());
            this.shaderUniform.disable();
            SMCLog.severe("Custom uniform disabled: " + this.shaderUniform.getName());
         }
      }
   }

   public void reset() {
      this.shaderUniform.reset();
   }

   public String getName() {
      return this.name;
   }

   public UniformType getType() {
      return this.type;
   }

   public IExpression getExpression() {
      return this.expression;
   }

   public ShaderUniformBase getShaderUniform() {
      return this.shaderUniform;
   }

   @Override
   public String toString() {
      return this.type.name().toLowerCase() + " " + this.name;
   }
}
