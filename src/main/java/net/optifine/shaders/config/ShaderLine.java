package net.optifine.shaders.config;

import net.optifine.Config;
import net.optifine.util.StrUtils;
import org.lwjgl.util.vector.Vector4f;

public class ShaderLine {
   public int type;
   public String name;
   public String value;
   public String line;
   public static final int TYPE_UNIFORM = 1;
   public static final int TYPE_ATTRIBUTE = 2;
   public static final int TYPE_CONST_INT = 3;
   public static final int TYPE_CONST_FLOAT = 4;
   public static final int TYPE_CONST_BOOL = 5;
   public static final int TYPE_PROPERTY = 6;
   public static final int TYPE_EXTENSION = 7;
   public static final int TYPE_CONST_VEC4 = 8;

   public ShaderLine(int n, String str, String str2, String str3) {
      this.type = n;
      this.name = str;
      this.value = str2;
      this.line = str3;
   }

   public int getType() {
      return this.type;
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   public boolean isUniform() {
      return this.type == 1;
   }

   public boolean isUniform(String str) {
      return this.isUniform() && str.equals(this.name);
   }

   public boolean isAttribute() {
      return this.type == 2;
   }

   public boolean isAttribute(String str) {
      return this.isAttribute() && str.equals(this.name);
   }

   public boolean isProperty() {
      return this.type == 6;
   }

   public boolean isConstInt() {
      return this.type == 3;
   }

   public boolean isConstFloat() {
      return this.type == 4;
   }

   public boolean isConstBool() {
      return this.type == 5;
   }

   public boolean isExtension() {
      return this.type == 7;
   }

   public boolean isConstVec4() {
      return this.type == 8;
   }

   public boolean isProperty(String str) {
      return this.isProperty() && str.equals(this.name);
   }

   public boolean isProperty(String str, String str2) {
      return this.isProperty(str) && str2.equals(this.value);
   }

   public boolean isConstInt(String str) {
      return this.isConstInt() && str.equals(this.name);
   }

   public boolean isConstIntSuffix(String str) {
      return this.isConstInt() && this.name.endsWith(str);
   }

   public boolean isConstFloat(String str) {
      return this.isConstFloat() && str.equals(this.name);
   }

   public boolean isConstBool(String str) {
      return this.isConstBool() && str.equals(this.name);
   }

   public boolean isExtension(String str) {
      return this.isExtension() && str.equals(this.name);
   }

   public boolean isConstBoolSuffix(String str) {
      return this.isConstBool() && this.name.endsWith(str);
   }

   public boolean isConstBoolSuffix(String str, boolean flag) {
      return this.isConstBoolSuffix(str) && this.getValueBool() == flag;
   }

   public boolean isConstBool(String str, String str2) {
      return this.isConstBool(str) || this.isConstBool(str2);
   }

   public boolean isConstBool(String str, String str2, String str3) {
      return this.isConstBool(str) || this.isConstBool(str2) || this.isConstBool(str3);
   }

   public boolean isConstBool(String str, boolean flag) {
      return this.isConstBool(str) && this.getValueBool() == flag;
   }

   public boolean isConstBool(String str, String str2, boolean flag) {
      return this.isConstBool(str, str2) && this.getValueBool() == flag;
   }

   public boolean isConstBool(String str, String str2, String str3, boolean flag) {
      return this.isConstBool(str, str2, str3) && this.getValueBool() == flag;
   }

   public boolean isConstVec4Suffix(String str) {
      return this.isConstVec4() && this.name.endsWith(str);
   }

   public int getValueInt() {
      try {
         return Integer.parseInt(this.value);
      } catch (NumberFormatException e) {
         throw new NumberFormatException("Invalid integer: " + this.value + ", line: " + this.line);
      }
   }

   public float getValueFloat() {
      try {
         return Float.parseFloat(this.value);
      } catch (NumberFormatException e) {
         throw new NumberFormatException("Invalid float: " + this.value + ", line: " + this.line);
      }
   }

   public Vector4f getValueVec4() {
      if (this.value == null) {
         return null;
      }

      String trim = this.value.trim();
      trim = StrUtils.removePrefix(trim, "vec4");
      trim = StrUtils.trim(trim, " ()");
      String[] tokenize = Config.tokenize(trim, ", ");
      if (tokenize.length != 4) {
         return null;
      }

      float[] floats = new float[4];

      for (int i = 0; i < tokenize.length; i++) {
         String str = tokenize[i];
         str = StrUtils.removeSuffix(str, new String[]{"F", "f"});
         float parseFloat = Config.parseFloat(str, Float.MAX_VALUE);
         if (parseFloat == Float.MAX_VALUE) {
            return null;
         }

         floats[i] = parseFloat;
      }

      return new Vector4f(floats[0], floats[1], floats[2], floats[3]);
   }

   public boolean getValueBool() {
      String toLowerCase = this.value.toLowerCase();
      if (!toLowerCase.equals("true") && !toLowerCase.equals("false")) {
         throw new RuntimeException("Invalid boolean: " + this.value + ", line: " + this.line);
      } else {
         return Boolean.valueOf(this.value);
      }
   }
}
