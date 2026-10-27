package com.cheatbreaker.client.emote;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class EmoteEntityRenderer extends RendererLivingEntity {
   public static EmoteEntityRenderer emoteEntityRenderer;

   public static boolean b_(EntityLivingBase entityLivingBase) {
      return emoteEntityRenderer.canRenderName(entityLivingBase);
   }

   public ResourceLocation nullValue(EntityLivingBase entityLivingBase) {
      return null;
   }

   @Override
   public int getColorMultiplier(EntityLivingBase entityLivingBase, float f, float f2) {
      return 0;
   }

   public static boolean checkEntityLivingBase(EntityLivingBase entityLivingBase, float f) {
      return emoteEntityRenderer.setBrightness(entityLivingBase, f, true);
   }

   public static void handleEntityLivingBase(EntityLivingBase entityLivingBase, String str, double d, double d2, double d3) {
      emoteEntityRenderer.renderLivingLabel(entityLivingBase, str, d, d2, d3, 64);
   }

   public static void run() {
      emoteEntityRenderer = new EmoteEntityRenderer(Minecraft.getMinecraft().getRenderManager(), null, 0.0F);
   }

   public static void run2() {
      emoteEntityRenderer.unsetBrightness();
   }

   @Override
   public ResourceLocation getEntityTexture(Entity entity) {
      return this.nullValue((EntityLivingBase)entity);
   }

   public EmoteEntityRenderer(RenderManager renderManager, ModelBase modelBase, float f) {
      super(renderManager, modelBase, f);
   }
}
