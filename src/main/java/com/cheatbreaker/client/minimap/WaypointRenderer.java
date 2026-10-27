package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.CheatBreaker;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class WaypointRenderer extends Render<EntityWaypointContainer> {
   public MinimapSettings minimapSettings = null;

   public WaypointRenderer(MinimapSettings minimapSettings) {
      super(RenderManager.renderManager);
      this.minimapSettings = minimapSettings;
   }

   public ResourceLocation getEntityTexture(EntityWaypointContainer entityWaypointContainer) {
      return new ResourceLocation("", "");
   }

   public void drawColoredQuad(WorldRenderer worldRenderer, Tessellator tessellator, float f, float f2, float f3, float f4, Waypoint waypoint, float f5) {
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(f, f2, 0.0).color(waypoint.red, waypoint.green, waypoint.blue, f5).endVertex();
      worldRenderer.pos(f, f4, 0.0).color(waypoint.red, waypoint.green, waypoint.blue, f5).endVertex();
      worldRenderer.pos(f3, f4, 0.0).color(waypoint.red, waypoint.green, waypoint.blue, f5).endVertex();
      worldRenderer.pos(f3, f2, 0.0).color(waypoint.red, waypoint.green, waypoint.blue, f5).endVertex();
      tessellator.draw();
   }

   public void renderLabel(Waypoint waypoint, String str, double d, double d2, double d3, int n, double d4) {
      GlStateManager.alphaFunc(516, 0.1F);
      if (d4 <= this.minimapSettings.waypointMaxDistance || this.minimapSettings.waypointMaxDistance < 0) {
         str = str + " [" + (int)d4 + "m]";
         double d5 = this.minimapSettings.mc.gameSettings.getOptionFloatValue(Options.RENDER_DISTANCE) * 16.0F * 0.75;
         double d6 = d4;
         if (d4 > d5) {
            d = d / d4 * d5;
            d2 = d2 / d4 * d5;
            d3 = d3 / d4 * d5;
            d6 = d5;
         }

         FontRenderer fontRendererFromRenderManager = this.getFontRendererFromRenderManager();
         float f = ((float)d6 * 0.1F + 1.0F) * 0.0266F;
         GlStateManager.pushMatrix();
         GlStateManager.translate((float)d + 0.5F, (float)d2 + 1.3F, (float)d3 + 0.5F);
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
         GlStateManager.scale(-f, -f, f);
         GlStateManager.disableLighting();
         GlStateManager.disableFog();
         GlStateManager.depthMask(false);
         GlStateManager.disableDepth();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         byte b = 0;
         GlStateManager.disableTexture2D();
         int n2 = fontRendererFromRenderManager.getStringWidth(str) / 2;
         int waypointBoxPadding = this.minimapSettings.waypointBoxPadding;
         GlStateManager.enableDepth();
         if (d4 < d5) {
            GlStateManager.depthMask(true);
         }

         float f2 = d4 < 30.0 ? (float)d4 / 30.0F : 1.0F;
         if (f2 < 0.2F) {
            f2 = 0.2F;
         }

         GlStateManager.enablePolygonOffset();
         GlStateManager.doPolygonOffset(1.0F, 3.0F);
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
         float f3 = 0.4F * f2;
         worldRenderer.pos(-n2 - waypointBoxPadding, -waypointBoxPadding + b, 0.0).color(0.0F, 0.0F, 0.0F, f3).endVertex();
         worldRenderer.pos(-n2 - waypointBoxPadding, 9 + waypointBoxPadding + b, 0.0).color(0.0F, 0.0F, 0.0F, f3).endVertex();
         worldRenderer.pos(n2 + waypointBoxPadding, 9 + waypointBoxPadding + b, 0.0).color(0.0F, 0.0F, 0.0F, f3).endVertex();
         worldRenderer.pos(n2 + waypointBoxPadding, -waypointBoxPadding + b, 0.0).color(0.0F, 0.0F, 0.0F, f3).endVertex();
         instance.draw();
         if (this.minimapSettings.waypointBorder) {
            GlStateManager.doPolygonOffset(1.0F, 2.0F);
            float f4 = 0.9F * f2;
            this.drawColoredQuad(worldRenderer, instance, -n2 - waypointBoxPadding, -waypointBoxPadding + b, n2 + waypointBoxPadding, -waypointBoxPadding + 1 + b, waypoint, f4);
            this.drawColoredQuad(worldRenderer, instance, -n2 - waypointBoxPadding, 8 + waypointBoxPadding + b, n2 + waypointBoxPadding, 9 + waypointBoxPadding + b, waypoint, f4);
            this.drawColoredQuad(worldRenderer, instance, -n2 - waypointBoxPadding, -waypointBoxPadding + b, -n2 - waypointBoxPadding + 1, 9 + waypointBoxPadding + b, waypoint, f4);
            this.drawColoredQuad(worldRenderer, instance, n2 + waypointBoxPadding - 1, -waypointBoxPadding + b, n2 + waypointBoxPadding, 9 + waypointBoxPadding + b, waypoint, f4);
         }

         GlStateManager.disablePolygonOffset();
         GlStateManager.enableTexture2D();
         int n3 = 16777215 | (int)(255.0F * f2) << 24;
         fontRendererFromRenderManager.drawString(str, -n2, b, n3, this.minimapSettings.waypointTextShadow);
         GlStateManager.enableDepth();
         GlStateManager.depthMask(true);
         GlStateManager.enableLighting();
         GlStateManager.enableFog();
         GlStateManager.disableBlend();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }
   }

   public void renderBeam(Waypoint waypoint, double d, double d2, double d3, float f, double d4) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      GlStateManager.disableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      GlStateManager.depthMask(false);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 1);
      short s = 256;
      float f2 = 0.06F;
      double d5 = 1.05;
      double d6 = 1.05;
      float red = waypoint.red;
      float blue = waypoint.blue;
      float green = waypoint.green;
      float f3 = d4 < 30.0 ? (float)d4 / 30.0F : 1.0F;
      if (f3 < 0.2F) {
         f3 = 0.2F;
      }

      for (int i = 0; i < 4; i++) {
         worldRenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);
         float f4 = red * f2;
         float f5 = green * f2;
         float f6 = blue * f2;
         float f7 = 0.8F * f3;
         double d7 = 0.1 + i * 0.2;
         d7 *= d5;
         double d8 = 0.1 + i * 0.2;
         d8 *= d6;

         for (int j = 0; j < 5; j++) {
            double d9 = d + 0.5 - d7;
            double d10 = d3 + 0.5 - d7;
            if (j == 1 || j == 2) {
               d9 += d7 * 2.0;
            }

            if (j == 2 || j == 3) {
               d10 += d7 * 2.0;
            }

            double d11 = d + 0.5 - d8;
            double d12 = d3 + 0.5 - d8;
            if (j == 1 || j == 2) {
               d11 += d8 * 2.0;
            }

            if (j == 2 || j == 3) {
               d12 += d8 * 2.0;
            }

            worldRenderer.pos(d11, d2 + 0.0, d12).color(f4, f5, f6, f7).endVertex();
            worldRenderer.pos(d9, d2 + s, d10).color(f4, f5, f6, f7).endVertex();
         }

         instance.draw();
      }

      GlStateManager.disableBlend();
      GlStateManager.enableFog();
      GlStateManager.enableLighting();
      GlStateManager.enableTexture2D();
      GlStateManager.depthMask(true);
   }

   public void handleEntityWaypointContainer(EntityWaypointContainer entityWaypointContainer, double d, double d2, double d3, float f, float f2) {
      if (CheatBreaker.getInstance().getModManager().zansMinimapMod.isEnabled()) {
         for (Waypoint waypoint : entityWaypointContainer.waypoints) {
            if (waypoint.isActive()) {
               int n = waypoint.getX();
               int n2 = waypoint.getZ();
               int n3 = waypoint.getY();
               double sqrt = Math.sqrt(waypoint.getDistanceSqToEntity(this.renderManager.livingPlayer));
               if (this.minimapSettings.waypointBeacons && entityWaypointContainer.worldObj.getChunkFromBlockCoords(new BlockPos(n, 0, n2)).isLoaded()) {
                  double d4 = 0.0 - TileEntityRendererDispatcher.staticPlayerY;
                  this.renderBeam(
                     waypoint, n - TileEntityRendererDispatcher.staticPlayerX, d4, n2 - TileEntityRendererDispatcher.staticPlayerZ, 64.0F, sqrt
                  );
               }

               if (this.minimapSettings.waypointSigns && !this.minimapSettings.mc.gameSettings.hideGUI) {
                  String name = waypoint.name;
                  this.renderLabel(
                     waypoint,
                     name,
                     n - TileEntityRendererDispatcher.staticPlayerX,
                     n3 + 1.0 - TileEntityRendererDispatcher.staticPlayerY,
                     n2 - TileEntityRendererDispatcher.staticPlayerZ,
                     64,
                     sqrt
                  );
               }
            }
         }
      }
   }

   public void doRender(EntityWaypointContainer entityWaypointContainer, double d, double d2, double d3, float f, float f2) {
      this.handleEntityWaypointContainer(entityWaypointContainer, d, d2, d3, f, f2);
   }
}
