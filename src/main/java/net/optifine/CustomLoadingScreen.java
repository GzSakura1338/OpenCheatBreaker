package net.optifine;

import java.util.Properties;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class CustomLoadingScreen {
   public int scaleMode = 0;
   public int scale = 2;
   public ResourceLocation locationTexture;
   public boolean center;
   public static final int SCALE_MODE_FIXED = 0;
   public static final int SCALE_MODE_FULL = 1;
   public static final int CONSTANT_2 = 2;
   public static final int CONSTANT_2_2 = 2;

   public CustomLoadingScreen(ResourceLocation resourceLocation, int n, int n2, boolean flag) {
      this.locationTexture = resourceLocation;
      this.scaleMode = n;
      this.scale = n2;
      this.center = flag;
   }

   public static CustomLoadingScreen parseScreen(String str, int n, Properties properties) {
      ResourceLocation resourceLocation = new ResourceLocation(str);
      int parseScaleMode2 = parseScaleMode(getProperty("scaleMode", n, properties));
      int n2 = parseScaleMode2 == 0 ? 2 : 1;
      int parseScale2 = parseScale(getProperty("scale", n, properties), n2);
      boolean parseBoolean = Config.parseBoolean(getProperty("center", n, properties), false);
      return new CustomLoadingScreen(resourceLocation, parseScaleMode2, parseScale2, parseBoolean);
   }

   public static String getProperty(String str, int n, Properties properties) {
      if (properties == null) {
         return null;
      }

      String property = properties.getProperty("dim" + n + "." + str);
      return property != null ? property : properties.getProperty(str);
   }

   public static int parseScaleMode(String str) {
      if (str == null) {
         return 0;
      }

      str = str.toLowerCase().trim();
      if (str.equals("fixed")) {
         return 0;
      }

      if (str.equals("full")) {
         return 1;
      }

      if (str.equals("stretch")) {
         return 2;
      }

      CustomLoadingScreens.warn("Invalid scale mode: " + str);
      return 0;
   }

   public static int parseScale(String str, int n) {
      if (str == null) {
         return n;
      } else {
         str = str.trim();
         int parseInt = Config.parseInt(str, -1);
         if (parseInt < 1) {
            CustomLoadingScreens.warn("Invalid scale: " + str);
            return n;
         } else {
            return parseInt;
         }
      }
   }

   public void drawBackground(int n, int n2) {
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      Config.getTextureManager().bindTexture(this.locationTexture);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      double d = 16 * this.scale;
      double d2 = n / d;
      double d3 = n2 / d;
      double d4 = 0.0;
      double d5 = 0.0;
      if (this.center) {
         d4 = (d - n) / (d * 2.0);
         d5 = (d - n2) / (d * 2.0);
      }

      switch (this.scaleMode) {
         case 1:
            d = Math.max(n, n2);
            d2 = this.scale * n / d;
            d3 = this.scale * n2 / d;
            if (this.center) {
               d4 = this.scale * (d - n) / (d * 2.0);
               d5 = this.scale * (d - n2) / (d * 2.0);
            }
            break;
         case 2:
            d2 = this.scale;
            d3 = this.scale;
            d4 = 0.0;
            d5 = 0.0;
      }

      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldRenderer.pos(0.0, n2, 0.0).tex(d4, d5 + d3).color(255, 255, 255, 255).endVertex();
      worldRenderer.pos(n, n2, 0.0).tex(d4 + d2, d5 + d3).color(255, 255, 255, 255).endVertex();
      worldRenderer.pos(n, 0.0, 0.0).tex(d4 + d2, d5).color(255, 255, 255, 255).endVertex();
      worldRenderer.pos(0.0, 0.0, 0.0).tex(d4, d5).color(255, 255, 255, 255).endVertex();
      instance.draw();
   }
}
