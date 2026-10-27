package net.optifine.shaders.uniform;

import java.util.ArrayList;
import java.util.List;

public class ShaderUniforms {
   public List<ShaderUniformBase> listUniforms = new ArrayList<>();

   public void setProgram(int n) {
      for (int i = 0; i < this.listUniforms.size(); i++) {
         ShaderUniformBase shaderUniformBase = this.listUniforms.get(i);
         shaderUniformBase.setProgram(n);
      }
   }

   public void reset() {
      for (int i = 0; i < this.listUniforms.size(); i++) {
         ShaderUniformBase shaderUniformBase = this.listUniforms.get(i);
         shaderUniformBase.reset();
      }
   }

   public ShaderUniform1i make1i(String str) {
      ShaderUniform1i shaderUniform1i = new ShaderUniform1i(str);
      this.listUniforms.add(shaderUniform1i);
      return shaderUniform1i;
   }

   public ShaderUniform2i make2i(String str) {
      ShaderUniform2i shaderUniform2i = new ShaderUniform2i(str);
      this.listUniforms.add(shaderUniform2i);
      return shaderUniform2i;
   }

   public ShaderUniform4i make4i(String str) {
      ShaderUniform4i shaderUniform4i = new ShaderUniform4i(str);
      this.listUniforms.add(shaderUniform4i);
      return shaderUniform4i;
   }

   public ShaderUniform1f make1f(String str) {
      ShaderUniform1f shaderUniform1f = new ShaderUniform1f(str);
      this.listUniforms.add(shaderUniform1f);
      return shaderUniform1f;
   }

   public ShaderUniform3f make3f(String str) {
      ShaderUniform3f shaderUniform3f = new ShaderUniform3f(str);
      this.listUniforms.add(shaderUniform3f);
      return shaderUniform3f;
   }

   public ShaderUniform4f make4f(String str) {
      ShaderUniform4f shaderUniform4f = new ShaderUniform4f(str);
      this.listUniforms.add(shaderUniform4f);
      return shaderUniform4f;
   }

   public ShaderUniformM4 makeM4(String str) {
      ShaderUniformM4 shaderUniformM4 = new ShaderUniformM4(str);
      this.listUniforms.add(shaderUniformM4);
      return shaderUniformM4;
   }
}
