package net.minecraft.client.resources.model;

import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public class ModelResourceLocation extends ResourceLocation {
   public String variant;

   public ModelResourceLocation(int n, String... strs) {
      super(0, strs[0], strs[1]);
      this.variant = StringUtils.isEmpty(strs[2]) ? "normal" : strs[2].toLowerCase();
   }

   public ModelResourceLocation(String str) {
      this(0, parsePathString(str));
   }

   public ModelResourceLocation(ResourceLocation resourceLocation, String str) {
      this(resourceLocation.toString(), str);
   }

   public ModelResourceLocation(String str, String str2) {
      this(0, parsePathString(str + '#' + (str2 == null ? "normal" : str2)));
   }

   public static String[] parsePathString(String str) {
      String[] strs = new String[]{null, str, null};
      int indexOf = str.indexOf(35);
      String str2 = str;
      if (indexOf >= 0) {
         strs[2] = str.substring(indexOf + 1, str.length());
         if (indexOf > 1) {
            str2 = str.substring(0, indexOf);
         }
      }

      System.arraycopy(ResourceLocation.splitObjectName(str2), 0, strs, 0, 2);
      return strs;
   }

   public String getVariant() {
      return this.variant;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj instanceof ModelResourceLocation && super.equals(obj)) {
         ModelResourceLocation modelResourceLocation = (ModelResourceLocation)obj;
         return this.variant.equals(modelResourceLocation.variant);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return 31 * super.hashCode() + this.variant.hashCode();
   }

   @Override
   public String toString() {
      return super.toString() + '#' + this.variant;
   }
}
