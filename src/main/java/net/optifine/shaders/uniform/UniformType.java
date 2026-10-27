package net.optifine.shaders.uniform;

import net.optifine.expr.ExpressionType;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionBool;
import net.optifine.expr.IExpressionFloat;
import net.optifine.expr.IExpressionFloatArray;

public enum UniformType {
   BOOL,
   INT,
   FLOAT,
   VEC2,
   VEC3,
   VEC4;

   public ShaderUniformBase makeShaderUniform(String str) {
      switch (this) {
         case BOOL:
            return new ShaderUniform1i(str);
         case INT:
            return new ShaderUniform1i(str);
         case FLOAT:
            return new ShaderUniform1f(str);
         case VEC2:
            return new ShaderUniform2f(str);
         case VEC3:
            return new ShaderUniform3f(str);
         case VEC4:
            return new ShaderUniform4f(str);
         default:
            throw new RuntimeException("Unknown uniform type: " + this);
      }
   }

   public void updateUniform(IExpression expression, ShaderUniformBase shaderUniformBase) {
      switch (this) {
         case BOOL:
            this.updateUniformBool((IExpressionBool)expression, (ShaderUniform1i)shaderUniformBase);
            return;
         case INT:
            this.updateUniformInt((IExpressionFloat)expression, (ShaderUniform1i)shaderUniformBase);
            return;
         case FLOAT:
            this.updateUniformFloat((IExpressionFloat)expression, (ShaderUniform1f)shaderUniformBase);
            return;
         case VEC2:
            this.updateUniformFloat2((IExpressionFloatArray)expression, (ShaderUniform2f)shaderUniformBase);
            return;
         case VEC3:
            this.updateUniformFloat3((IExpressionFloatArray)expression, (ShaderUniform3f)shaderUniformBase);
            return;
         case VEC4:
            this.updateUniformFloat4((IExpressionFloatArray)expression, (ShaderUniform4f)shaderUniformBase);
            return;
         default:
            throw new RuntimeException("Unknown uniform type: " + this);
      }
   }

   public void updateUniformBool(IExpressionBool expressionBool, ShaderUniform1i shaderUniform1i) {
      boolean eval = expressionBool.eval();
      int n = eval ? 1 : 0;
      shaderUniform1i.setValue(n);
   }

   public void updateUniformInt(IExpressionFloat expressionFloat, ShaderUniform1i shaderUniform1i) {
      int eval = (int)expressionFloat.eval();
      shaderUniform1i.setValue(eval);
   }

   public void updateUniformFloat(IExpressionFloat expressionFloat, ShaderUniform1f shaderUniform1f) {
      float eval = expressionFloat.eval();
      shaderUniform1f.setValue(eval);
   }

   public void updateUniformFloat2(IExpressionFloatArray expressionFloatArray, ShaderUniform2f shaderUniform2f) {
      float[] eval = expressionFloatArray.eval();
      if (eval.length != 2) {
         throw new RuntimeException("Value length is not 2, length: " + eval.length);
      }

      shaderUniform2f.setValue(eval[0], eval[1]);
   }

   public void updateUniformFloat3(IExpressionFloatArray expressionFloatArray, ShaderUniform3f shaderUniform3f) {
      float[] eval = expressionFloatArray.eval();
      if (eval.length != 3) {
         throw new RuntimeException("Value length is not 3, length: " + eval.length);
      }

      shaderUniform3f.setValue(eval[0], eval[1], eval[2]);
   }

   public void updateUniformFloat4(IExpressionFloatArray expressionFloatArray, ShaderUniform4f shaderUniform4f) {
      float[] eval = expressionFloatArray.eval();
      if (eval.length != 4) {
         throw new RuntimeException("Value length is not 4, length: " + eval.length);
      }

      shaderUniform4f.setValue(eval[0], eval[1], eval[2], eval[3]);
   }

   public boolean matchesExpressionType(ExpressionType expressionType) {
      switch (this) {
         case BOOL:
            return expressionType == ExpressionType.BOOL;
         case INT:
            return expressionType == ExpressionType.FLOAT;
         case FLOAT:
            return expressionType == ExpressionType.FLOAT;
         case VEC2:
         case VEC3:
         case VEC4:
            return expressionType == ExpressionType.FLOAT_ARRAY;
         default:
            throw new RuntimeException("Unknown uniform type: " + this);
      }
   }

   public static UniformType parse(String str) {
      UniformType[] values2 = values();

      for (int i = 0; i < values2.length; i++) {
         UniformType uniformType = values2[i];
         if (uniformType.name().toLowerCase().equals(str)) {
            return uniformType;
         }
      }

      return null;
   }
}
