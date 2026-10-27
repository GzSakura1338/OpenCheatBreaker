package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class RenderRabbit extends RenderLiving<EntityRabbit> {
   public static ResourceLocation BROWN = new ResourceLocation("textures/entity/rabbit/brown.png");
   public static ResourceLocation WHITE = new ResourceLocation("textures/entity/rabbit/white.png");
   public static ResourceLocation BLACK = new ResourceLocation("textures/entity/rabbit/black.png");
   public static ResourceLocation goldPng = new ResourceLocation("textures/entity/rabbit/gold.png");
   public static ResourceLocation saltPng = new ResourceLocation("textures/entity/rabbit/salt.png");
   public static ResourceLocation whiteSplotchedPng = new ResourceLocation("textures/entity/rabbit/white_splotched.png");
   public static ResourceLocation TOAST = new ResourceLocation("textures/entity/rabbit/toast.png");
   public static ResourceLocation CAERBANNOG = new ResourceLocation("textures/entity/rabbit/caerbannog.png");

   public RenderRabbit(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
      super(renderManagerIn, modelBaseIn, shadowSizeIn);
   }

   public ResourceLocation getEntityTexture(EntityRabbit entity) {
      String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(entity.getName());
      if (textWithoutFormattingCodes != null && textWithoutFormattingCodes.equals("Toast")) {
         return TOAST;
      }

      switch (entity.getRabbitType()) {
         case 0:
         default:
            return BROWN;
         case 1:
            return WHITE;
         case 2:
            return BLACK;
         case 3:
            return whiteSplotchedPng;
         case 4:
            return goldPng;
         case 5:
            return saltPng;
         case 99:
            return CAERBANNOG;
      }
   }
}
