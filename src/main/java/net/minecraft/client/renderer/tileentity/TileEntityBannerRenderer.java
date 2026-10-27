package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBanner;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.LayeredColorMaskTexture;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBanner.EnumBannerPattern;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TileEntityBannerRenderer extends TileEntitySpecialRenderer<TileEntityBanner> {
   public static Map<String, TileEntityBannerRenderer.TimedBannerTexture> DESIGNS = Maps.newHashMap();
   public static ResourceLocation BANNERTEXTURES = new ResourceLocation("textures/entity/banner_base.png");
   public ModelBanner bannerModel = new ModelBanner();

   public void renderTileEntityAt(TileEntityBanner te, double x, double y, double z, float partialTicks, int destroyStage) {
      boolean flag = te.getWorld() != null;
      boolean flag2 = !flag || te.getBlockType() == Blocks.standing_banner;
      int n = flag ? te.getBlockMetadata() : 0;
      long l = flag ? te.getWorld().getTotalWorldTime() : 0L;
      GlStateManager.pushMatrix();
      float f = 0.6666667F;
      if (flag2) {
         GlStateManager.translate((float)x + 0.5F, (float)y + 0.75F * f, (float)z + 0.5F);
         float f2 = n * 360 / 16.0F;
         GlStateManager.rotate(-f2, 0.0F, 1.0F, 0.0F);
         this.bannerModel.bannerStand.showModel = true;
      } else {
         float f2 = 0.0F;
         if (n == 2) {
            f2 = 180.0F;
         }

         if (n == 4) {
            f2 = 90.0F;
         }

         if (n == 5) {
            f2 = -90.0F;
         }

         GlStateManager.translate((float)x + 0.5F, (float)y - 0.25F * f, (float)z + 0.5F);
         GlStateManager.rotate(-f2, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
         this.bannerModel.bannerStand.showModel = false;
      }

      BlockPos pos = te.getPos();
      float f2 = pos.getX() * 7 + pos.getY() * 9 + pos.getZ() * 13 + (float)l + partialTicks;
      this.bannerModel.bannerSlate.rotateAngleX = (-0.0125F + 0.01F * MathHelper.cos(f2 * (float) Math.PI * 0.02F)) * (float) Math.PI;
      GlStateManager.enableRescaleNormal();
      ResourceLocation texture = this.getTexture(te);
      if (texture != null) {
         this.bindTexture(texture);
         GlStateManager.pushMatrix();
         GlStateManager.scale(f, -f, -f);
         this.bannerModel.renderBanner();
         GlStateManager.popMatrix();
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   public ResourceLocation getTexture(TileEntityBanner bannerObj) {
      String patternResourceLocation = bannerObj.getPatternResourceLocation();
      if (patternResourceLocation.isEmpty()) {
         return null;
      }

      TileEntityBannerRenderer.TimedBannerTexture timedBannerTexture = DESIGNS.get(patternResourceLocation);
      if (timedBannerTexture == null) {
         if (DESIGNS.size() >= 256) {
            long currentTimeMillis = System.currentTimeMillis();
            Iterator iterator = DESIGNS.keySet().iterator();

            while (iterator.hasNext()) {
               String str = (String)iterator.next();
               TileEntityBannerRenderer.TimedBannerTexture timedBannerTexture2 = DESIGNS.get(str);
               if (currentTimeMillis - timedBannerTexture2.systemTime > 60000L) {
                  Minecraft.getMinecraft().getTextureManager().deleteTexture(timedBannerTexture2.bannerTexture);
                  iterator.remove();
               }
            }

            if (DESIGNS.size() >= 256) {
               return null;
            }
         }

         List patternList = bannerObj.getPatternList();
         List colorList = bannerObj.getColorList();
         ArrayList newArrayList = Lists.newArrayList();

         for (EnumBannerPattern enumBannerPattern : (Iterable<EnumBannerPattern>)patternList) {
            newArrayList.add("textures/entity/banner/" + enumBannerPattern.getPatternName() + ".png");
         }

         timedBannerTexture = new TileEntityBannerRenderer.TimedBannerTexture();
         timedBannerTexture.bannerTexture = new ResourceLocation(patternResourceLocation);
         Minecraft.getMinecraft().getTextureManager().loadTexture(timedBannerTexture.bannerTexture, new LayeredColorMaskTexture(BANNERTEXTURES, newArrayList, colorList));
         DESIGNS.put(patternResourceLocation, timedBannerTexture);
      }

      timedBannerTexture.systemTime = System.currentTimeMillis();
      return timedBannerTexture.bannerTexture;
   }

   static class TimedBannerTexture {
      public long systemTime;
      public ResourceLocation bannerTexture;

      public TimedBannerTexture() {
      }
   }
}
