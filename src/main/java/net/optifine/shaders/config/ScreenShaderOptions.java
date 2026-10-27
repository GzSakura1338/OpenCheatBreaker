package net.optifine.shaders.config;

public class ScreenShaderOptions {
   public String name;
   public ShaderOption[] shaderOptions;
   public int columns;

   public ScreenShaderOptions(String str, ShaderOption[] shaderOptions, int n) {
      this.name = str;
      this.shaderOptions = shaderOptions;
      this.columns = n;
   }

   public String getName() {
      return this.name;
   }

   public ShaderOption[] getShaderOptions() {
      return this.shaderOptions;
   }

   public int getColumns() {
      return this.columns;
   }
}
