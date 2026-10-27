package net.optifine.shaders.config;

import java.util.Arrays;
import java.util.List;
import net.optifine.Config;
import net.optifine.shaders.Shaders;
import net.optifine.util.StrUtils;

public abstract class ShaderOption {
   public String name = null;
   public String description = null;
   public String value = null;
   public String[] values = null;
   public String valueDefault = null;
   public String[] paths = null;
   public boolean enabled = true;
   public boolean visible = true;
   public static final String COLOR_GREEN = "§a";
   public static final String COLOR_RED = "§c";
   public static final String COLOR_BLUE = "§9";

   public ShaderOption(String str, String str2, String str3, String[] strs, String str4, String str5) {
      this.name = str;
      this.description = str2;
      this.value = str3;
      this.values = strs;
      this.valueDefault = str4;
      if (str5 != null) {
         this.paths = new String[]{str5};
      }
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public String getDescriptionText() {
      String normalize = Config.normalize(this.description);
      normalize = StrUtils.removePrefix(normalize, "//");
      return Shaders.translate("option." + this.getName() + ".comment", normalize);
   }

   public void setDescription(String str) {
      this.description = str;
   }

   public String getValue() {
      return this.value;
   }

   public boolean setValue(String str) {
      int index = getIndex(str, this.values);
      if (index < 0) {
         return false;
      }

      this.value = str;
      return true;
   }

   public String getValueDefault() {
      return this.valueDefault;
   }

   public void resetValue() {
      this.value = this.valueDefault;
   }

   public void nextValue() {
      int index = getIndex(this.value, this.values);
      if (index >= 0) {
         index = (index + 1) % this.values.length;
         this.value = this.values[index];
      }
   }

   public void prevValue() {
      int index = getIndex(this.value, this.values);
      if (index >= 0) {
         index = (index - 1 + this.values.length) % this.values.length;
         this.value = this.values[index];
      }
   }

   public static int getIndex(String str, String[] strs) {
      for (int i = 0; i < strs.length; i++) {
         String str2 = strs[i];
         if (str2.equals(str)) {
            return i;
         }
      }

      return -1;
   }

   public String[] getPaths() {
      return this.paths;
   }

   public void addPaths(String[] strs) {
      List asList = Arrays.asList(this.paths);

      for (int i = 0; i < strs.length; i++) {
         String str = strs[i];
         if (!asList.contains(str)) {
            this.paths = (String[])Config.addObjectToArray(this.paths, str);
         }
      }
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean flag) {
      this.enabled = flag;
   }

   public boolean isChanged() {
      return !Config.equals(this.value, this.valueDefault);
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean flag) {
      this.visible = flag;
   }

   public boolean isValidValue(String str) {
      return getIndex(str, this.values) >= 0;
   }

   public String getNameText() {
      return Shaders.translate("option." + this.name, this.name);
   }

   public String getValueText(String str) {
      return Shaders.translate("value." + this.name + "." + str, str);
   }

   public String getValueColor(String str) {
      return "";
   }

   public boolean matchesLine(String str) {
      return false;
   }

   public boolean checkUsed() {
      return false;
   }

   public boolean isUsedInLine(String str) {
      return false;
   }

   public String getSourceLine() {
      return null;
   }

   public String[] getValues() {
      return (String[])this.values.clone();
   }

   public float getIndexNormalized() {
      if (this.values.length <= 1) {
         return 0.0F;
      }

      int index = getIndex(this.value, this.values);
      return index < 0 ? 0.0F : 1.0F * index / (this.values.length - 1.0F);
   }

   public void setIndexNormalized(float f) {
      if (this.values.length > 1) {
         f = Config.limit(f, 0.0F, 1.0F);
         int round = Math.round(f * (this.values.length - 1));
         this.value = this.values[round];
      }
   }

   @Override
   public String toString() {
      return "" + this.name + ", value: " + this.value + ", valueDefault: " + this.valueDefault + ", paths: " + Config.arrayToString(this.paths);
   }
}
