package net.minecraft.client.renderer.tileentity;

import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBeacon.BeamSegment;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;

public class TileEntityBeaconRenderer extends TileEntitySpecialRenderer<TileEntityBeacon> {
   public static ResourceLocation beaconBeam = new ResourceLocation("textures/entity/beacon_beam.png");

   public void renderTileEntityAt(TileEntityBeacon te, double x, double y, double z, float partialTicks, int destroyStage) {
      float shouldBeamRender = te.shouldBeamRender();
      if (shouldBeamRender > 0.0) {
         if (Config.isShaders()) {
            Shaders.beginBeacon();
         }

         GlStateManager.alphaFunc(516, 0.1F);
         if (shouldBeamRender > 0.0F) {
            Tessellator instance = Tessellator.getInstance();
            WorldRenderer worldRenderer = instance.getWorldRenderer();
            GlStateManager.disableFog();
            List beamSegments = te.getBeamSegments();
            int n = 0;

            for (int i = 0; i < beamSegments.size(); i++) {
               BeamSegment beamSegment = (BeamSegment)beamSegments.get(i);
               int n2 = n + beamSegment.getHeight();
               this.bindTexture(beaconBeam);
               GL11.glTexParameterf(3553, 10242, 10497.0F);
               GL11.glTexParameterf(3553, 10243, 10497.0F);
               GlStateManager.disableLighting();
               GlStateManager.disableCull();
               GlStateManager.disableBlend();
               GlStateManager.depthMask(true);
               GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
               double d = (double)te.getWorld().getTotalWorldTime() + partialTicks;
               double fractionalPart = MathHelper.fractionalPart(-d * 0.2 - MathHelper.floor_double(-d * 0.1));
               float f = beamSegment.getColors()[0];
               float f2 = beamSegment.getColors()[1];
               float f3 = beamSegment.getColors()[2];
               double d2 = d * 0.025 * -1.5;
               double d3 = 0.2;
               double d4 = 0.5 + Math.cos(d2 + (Math.PI * 3.0 / 4.0)) * 0.2;
               double d5 = 0.5 + Math.sin(d2 + (Math.PI * 3.0 / 4.0)) * 0.2;
               double d6 = 0.5 + Math.cos(d2 + (Math.PI / 4)) * 0.2;
               double d7 = 0.5 + Math.sin(d2 + (Math.PI / 4)) * 0.2;
               double d8 = 0.5 + Math.cos(d2 + (Math.PI * 5.0 / 4.0)) * 0.2;
               double d9 = 0.5 + Math.sin(d2 + (Math.PI * 5.0 / 4.0)) * 0.2;
               double d10 = 0.5 + Math.cos(d2 + (Math.PI * 7.0 / 4.0)) * 0.2;
               double d11 = 0.5 + Math.sin(d2 + (Math.PI * 7.0 / 4.0)) * 0.2;
               double d12 = 0.0;
               double d13 = 1.0;
               double d14 = -1.0 + fractionalPart;
               double d15 = beamSegment.getHeight() * shouldBeamRender * 2.5 + d14;
               worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
               worldRenderer.pos(x + d4, y + n2, z + d5).tex(1.0, d15).color(f, f2, f3, 1.0F).endVertex();
               worldRenderer.pos(x + d4, y + n, z + d5).tex(1.0, d14).color(f, f2, f3, 1.0F).endVertex();
               worldRenderer.pos(x + d6, y + n, z + d7).tex(0.0, d14).color(f, f2, f3, 1.0F).endVertex();
               worldRenderer.pos(x + d6, y + n2, z + d7).tex(0.0, d15).color(f, f2, f3, 1.0F).endVertex();
               worldRenderer.pos(x + d10, y + n2, z + d11).tex(1.0, d15).color(f, f2, f3, 1.0F).endVertex();
               worldRenderer.pos(x + d10, y + n, z + d11).tex(1.0, d14).color(f, f2, f3, 1.0F).endVertex();
               worldRenderer.pos(x + d8, y + n, z + d9).tex(0.0, d14).color(f, f2, f3, 1.0F).endVertex();
               worldRenderer.pos(x + d8, y + n2, z + d9).tex(0.0, d15).color(f, f2, f3, 1.0F).endVertex();
               worldRenderer.pos(x + d6, y + n2, z + d7).tex(1.0, d15).color(f, f2, f3, 1.0F).endVertex();
               worldRenderer.pos(x + d6, y + n, z + d7).tex(1.0, d14).color(f, f2, f3, 1.0F).endVertex();
               worldRenderer.pos(x + d10, y + n, z + d11).tex(0.0, d14).color(f, f2, f3, 1.0F).endVertex();
               worldRenderer.pos(x + d10, y + n2, z + d11).tex(0.0, d15).color(f, f2, f3, 1.0F).endVertex();
               worldRenderer.pos(x + d8, y + n2, z + d9).tex(1.0, d15).color(f, f2, f3, 1.0F).endVertex();
               worldRenderer.pos(x + d8, y + n, z + d9).tex(1.0, d14).color(f, f2, f3, 1.0F).endVertex();
               worldRenderer.pos(x + d4, y + n, z + d5).tex(0.0, d14).color(f, f2, f3, 1.0F).endVertex();
               worldRenderer.pos(x + d4, y + n2, z + d5).tex(0.0, d15).color(f, f2, f3, 1.0F).endVertex();
               instance.draw();
               GlStateManager.enableBlend();
               GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
               GlStateManager.depthMask(false);
               d2 = 0.2;
               d3 = 0.2;
               d4 = 0.8;
               d5 = 0.2;
               d6 = 0.2;
               d7 = 0.8;
               d8 = 0.8;
               d9 = 0.8;
               d10 = 0.0;
               d11 = 1.0;
               d12 = -1.0 + fractionalPart;
               d13 = beamSegment.getHeight() * shouldBeamRender + d12;
               worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
               worldRenderer.pos(x + 0.2, y + n2, z + 0.2).tex(1.0, d13).color(f, f2, f3, 0.125F).endVertex();
               worldRenderer.pos(x + 0.2, y + n, z + 0.2).tex(1.0, d12).color(f, f2, f3, 0.125F).endVertex();
               worldRenderer.pos(x + 0.8, y + n, z + 0.2).tex(0.0, d12).color(f, f2, f3, 0.125F).endVertex();
               worldRenderer.pos(x + 0.8, y + n2, z + 0.2).tex(0.0, d13).color(f, f2, f3, 0.125F).endVertex();
               worldRenderer.pos(x + 0.8, y + n2, z + 0.8).tex(1.0, d13).color(f, f2, f3, 0.125F).endVertex();
               worldRenderer.pos(x + 0.8, y + n, z + 0.8).tex(1.0, d12).color(f, f2, f3, 0.125F).endVertex();
               worldRenderer.pos(x + 0.2, y + n, z + 0.8).tex(0.0, d12).color(f, f2, f3, 0.125F).endVertex();
               worldRenderer.pos(x + 0.2, y + n2, z + 0.8).tex(0.0, d13).color(f, f2, f3, 0.125F).endVertex();
               worldRenderer.pos(x + 0.8, y + n2, z + 0.2).tex(1.0, d13).color(f, f2, f3, 0.125F).endVertex();
               worldRenderer.pos(x + 0.8, y + n, z + 0.2).tex(1.0, d12).color(f, f2, f3, 0.125F).endVertex();
               worldRenderer.pos(x + 0.8, y + n, z + 0.8).tex(0.0, d12).color(f, f2, f3, 0.125F).endVertex();
               worldRenderer.pos(x + 0.8, y + n2, z + 0.8).tex(0.0, d13).color(f, f2, f3, 0.125F).endVertex();
               worldRenderer.pos(x + 0.2, y + n2, z + 0.8).tex(1.0, d13).color(f, f2, f3, 0.125F).endVertex();
               worldRenderer.pos(x + 0.2, y + n, z + 0.8).tex(1.0, d12).color(f, f2, f3, 0.125F).endVertex();
               worldRenderer.pos(x + 0.2, y + n, z + 0.2).tex(0.0, d12).color(f, f2, f3, 0.125F).endVertex();
               worldRenderer.pos(x + 0.2, y + n2, z + 0.2).tex(0.0, d13).color(f, f2, f3, 0.125F).endVertex();
               instance.draw();
               GlStateManager.enableLighting();
               GlStateManager.enableTexture2D();
               GlStateManager.depthMask(true);
               n = n2;
            }

            GlStateManager.enableFog();
         }

         if (Config.isShaders()) {
            Shaders.endBeacon();
         }
      }
   }

   @Override
   public boolean forceTileEntityRender() {
      return true;
   }
}
