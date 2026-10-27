package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.ResourceLocation;

public class RenderHorse extends RenderLiving<EntityHorse> {
   public static Map<String, ResourceLocation> MODEL_IDENTIFIERS = Maps.newHashMap();
   public static ResourceLocation whiteHorseTextures = new ResourceLocation("textures/entity/horse/horse_white.png");
   public static ResourceLocation mulePng = new ResourceLocation("textures/entity/horse/mule.png");
   public static ResourceLocation donkeyPng = new ResourceLocation("textures/entity/horse/donkey.png");
   public static ResourceLocation zombieHorseTextures = new ResourceLocation("textures/entity/horse/horse_zombie.png");
   public static ResourceLocation skeletonHorseTextures = new ResourceLocation("textures/entity/horse/horse_skeleton.png");

   public RenderHorse(RenderManager rendermanagerIn, ModelHorse model, float shadowSizeIn) {
      super(rendermanagerIn, model, shadowSizeIn);
   }

   public void preRenderCallback(EntityHorse entitylivingbaseIn, float partialTickTime) {
      float f = 1.0F;
      int horseType = entitylivingbaseIn.getHorseType();
      if (horseType == 1) {
         f *= 0.87F;
      } else if (horseType == 2) {
         f *= 0.92F;
      }

      GlStateManager.scale(f, f, f);
      super.preRenderCallback(entitylivingbaseIn, partialTickTime);
   }

   public ResourceLocation getEntityTexture(EntityHorse entity) {
      if (!entity.check()) {
         switch (entity.getHorseType()) {
            case 0:
            default:
               return whiteHorseTextures;
            case 1:
               return donkeyPng;
            case 2:
               return mulePng;
            case 3:
               return zombieHorseTextures;
            case 4:
               return skeletonHorseTextures;
         }
      } else {
         return this.findResourceLocation(entity);
      }
   }

   public ResourceLocation findResourceLocation(EntityHorse horse) {
      String horseTexture = horse.getHorseTexture();
      if (!horse.isFlag()) {
         return null;
      }

      ResourceLocation resourceLocation = MODEL_IDENTIFIERS.get(horseTexture);
      if (resourceLocation == null) {
         resourceLocation = new ResourceLocation(horseTexture);
         Minecraft.getMinecraft().getTextureManager().loadTexture(resourceLocation, new LayeredTexture(horse.getVariantTexturePaths()));
         MODEL_IDENTIFIERS.put(horseTexture, resourceLocation);
      }

      return resourceLocation;
   }
}
