package net.optifine.entity.model;

import net.minecraft.util.ResourceLocation;

public class CustomEntityRenderer {
   public String name = null;
   public String basePath = null;
   public ResourceLocation textureLocation = null;
   public CustomModelRenderer[] customModelRenderers = null;
   public float shadowSize = 0.0F;

   public CustomEntityRenderer(String str, String str2, ResourceLocation resourceLocation, CustomModelRenderer[] customModelRenderers, float f) {
      this.name = str;
      this.basePath = str2;
      this.textureLocation = resourceLocation;
      this.customModelRenderers = customModelRenderers;
      this.shadowSize = f;
   }

   public String getName() {
      return this.name;
   }

   public String getBasePath() {
      return this.basePath;
   }

   public ResourceLocation getTextureLocation() {
      return this.textureLocation;
   }

   public CustomModelRenderer[] getCustomModelRenderers() {
      return this.customModelRenderers;
   }

   public float getShadowSize() {
      return this.shadowSize;
   }
}
