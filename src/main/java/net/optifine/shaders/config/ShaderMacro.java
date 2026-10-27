package net.optifine.shaders.config;

public class ShaderMacro {
   public String name;
   public String value;

   public ShaderMacro(String str, String str2) {
      this.name = str;
      this.value = str2;
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   public String getSourceLine() {
      return "#define " + this.name + " " + this.value;
   }

   @Override
   public String toString() {
      return this.getSourceLine();
   }
}
