package net.minecraft.util;

import org.apache.commons.lang3.Validate;

public class ResourceLocation {
   public String resourceDomain;
   public String resourcePath;

   public ResourceLocation(int n, String... resourceName) {
      this.resourceDomain = org.apache.commons.lang3.StringUtils.isEmpty(resourceName[0]) ? "minecraft" : resourceName[0].toLowerCase();
      this.resourcePath = resourceName[1];
      Validate.notNull(this.resourcePath);
   }

   public ResourceLocation(String resourceName) {
      this(0, splitObjectName(resourceName));
   }

   public ResourceLocation(String resourceDomainIn, String resourcePathIn) {
      this(0, resourceDomainIn, resourcePathIn);
   }

   public static String[] splitObjectName(String toSplit) {
      String[] strs = new String[]{null, toSplit};
      int indexOf = toSplit.indexOf(58);
      if (indexOf >= 0) {
         strs[1] = toSplit.substring(indexOf + 1, toSplit.length());
         if (indexOf > 1) {
            strs[0] = toSplit.substring(0, indexOf);
         }
      }

      return strs;
   }

   public String getResourcePath() {
      return this.resourcePath;
   }

   public String getResourceDomain() {
      return this.resourceDomain;
   }

   @Override
   public String toString() {
      return this.resourceDomain + ':' + this.resourcePath;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof ResourceLocation)) {
         return false;
      }

      ResourceLocation resourceLocation = (ResourceLocation)obj;
      return this.resourceDomain.equals(resourceLocation.resourceDomain) && this.resourcePath.equals(resourceLocation.resourcePath);
   }

   @Override
   public int hashCode() {
      return 31 * this.resourceDomain.hashCode() + this.resourcePath.hashCode();
   }
}
