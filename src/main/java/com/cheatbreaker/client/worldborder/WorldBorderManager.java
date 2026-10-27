package com.cheatbreaker.client.worldborder;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.BlockPosIBlockStateListEvent;
import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.FloatEvent6;
import com.sun.javafx.geom.Vec2d;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class WorldBorderManager {
   public Minecraft mc = Minecraft.getMinecraft();
   public CheatBreaker cheatBreaker = CheatBreaker.getInstance();
   public List<WorldBorder> worldBorderList = new ArrayList<>();
   public static ResourceLocation forcefieldPng = new ResourceLocation("textures/misc/forcefield.png");

   public void onBlockPosIBlockStateListEvent(BlockPosIBlockStateListEvent blockPosIBlockStateListEvent) {
      for (WorldBorder worldBorder : this.worldBorderList) {
         ;
      }
   }

   public void onFloatEvent6(FloatEvent6 floatEvent6) {
      if (!this.worldBorderList.isEmpty()) {
         EntityPlayerSP thePlayer = this.mc.thePlayer;
         float partialTicks = floatEvent6.getPartialTicks();
         this.worldBorderList
            .stream()
            .filter(WorldBorder::isOnCurrentServer)
            .forEach(
               worldBorder -> {
                  Tessellator instance = Tessellator.getInstance();
                  WorldRenderer worldRenderer = instance.getWorldRenderer();
                  double d = this.mc.gameSettings.renderDistanceChunks * 16;
                  if (thePlayer.posX >= worldBorder.getMaxX() - d
                     || thePlayer.posX <= worldBorder.getMinX() + d
                     || thePlayer.posZ >= worldBorder.getMaxZ() - d
                     || thePlayer.posZ <= worldBorder.getMinZ() + d) {
                     double d2 = 1.0 - worldBorder.getDistanceToBorder(thePlayer) / d;
                     d2 = Math.pow(d2, 4.0);
                     double d3 = thePlayer.lastTickPosX + (thePlayer.posX - thePlayer.lastTickPosX) * partialTicks;
                     double d4 = thePlayer.lastTickPosY + (thePlayer.posY - thePlayer.lastTickPosY) * partialTicks;
                     double d5 = thePlayer.lastTickPosZ + (thePlayer.posZ - thePlayer.lastTickPosZ) * partialTicks;
                     GlStateManager.enableBlend();
                     GL11.glBlendFunc(770, 1);
                     this.mc.renderEngine.bindTexture(forcefieldPng);
                     GL11.glDepthMask(false);
                     GlStateManager.pushMatrix();
                     boolean flag = true;
                     float f = (WorldBorder.getColor(worldBorder).getRed() & 0xFF) / 255.0F;
                     float f2 = (WorldBorder.getColor(worldBorder).getGreen() & 0xFF) / 255.0F;
                     float f3 = (WorldBorder.getColor(worldBorder).getBlue() & 0xFF) / 255.0F;
                     GL11.glPolygonOffset(-3.0F, -3.0F);
                     GL11.glEnable(32823);
                     GL11.glAlphaFunc(516, 0.1F);
                     GL11.glEnable(3008);
                     GL11.glDisable(2884);
                     float f4 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F;
                     worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                     GL11.glTranslated(-d3, -d4, -d5);
                     double max = Math.max(MathHelper.floor_double(d5 - d), worldBorder.getMinZ());
                     double min = Math.min(MathHelper.ceiling_double_int(d5 + d), worldBorder.getMaxZ());
                     if (d3 > worldBorder.getMaxX() - d) {
                        float f5 = 0.0F;

                        for (double d6 = max; d6 < min; f5 += 0.5F) {
                           double min2 = Math.min(1.0, min - d6);
                           float f6 = (float)min2 * 0.5F;
                           worldRenderer.pos(worldBorder.getMaxX(), 256.0, d6).tex(f4 + f5, f4 + 0.0F).color(f, f2, f3, 1.0F).endVertex();
                           worldRenderer.pos(worldBorder.getMaxX(), 256.0, d6 + min2).tex(f4 + f6 + f5, f4 + 0.0F).color(f, f2, f3, 1.0F).endVertex();
                           worldRenderer.pos(worldBorder.getMaxX(), 0.0, d6 + min2).tex(f4 + f6 + f5, f4 + 128.0F).color(f, f2, f3, 1.0F).endVertex();
                           worldRenderer.pos(worldBorder.getMaxX(), 0.0, d6).tex(f4 + f5, f4 + 128.0F).color(f, f2, f3, 1.0F).endVertex();
                           d6++;
                        }
                     }

                     if (d3 < worldBorder.getMinX() + d) {
                        float f5 = 0.0F;

                        for (double d6 = max; d6 < min; f5 += 0.5F) {
                           double min2 = Math.min(1.0, min - d6);
                           float f6 = (float)min2 * 0.5F;
                           worldRenderer.pos(worldBorder.getMinX(), 256.0, d6).tex(f4 + f5, f4 + 0.0F).color(f, f2, f3, 1.0F).endVertex();
                           worldRenderer.pos(worldBorder.getMinX(), 256.0, d6 + min2)
                              .tex(f4 + f6 + f5, f4 + 0.0F)
                              .color(f, f2, f3, 1.0F)
                              .endVertex();
                           worldRenderer.pos(worldBorder.getMinX(), 0.0, d6 + min2)
                              .tex(f4 + f6 + f5, f4 + 128.0F)
                              .color(f, f2, f3, 1.0F)
                              .endVertex();
                           worldRenderer.pos(worldBorder.getMinX(), 0.0, d6).tex(f4 + f5, f4 + 128.0F).color(f, f2, f3, 1.0F).endVertex();
                           d6++;
                        }
                     }

                     max = Math.max(MathHelper.floor_double(d3 - d), worldBorder.getMinX());
                     min = Math.min(MathHelper.ceiling_double_int(d3 + d), worldBorder.getMaxX());
                     if (d5 > worldBorder.getMaxZ() - d) {
                        float f5 = 0.0F;

                        for (double d6 = max; d6 < min; f5 += 0.5F) {
                           double min2 = Math.min(1.0, min - d6);
                           float f6 = (float)min2 * 0.5F;
                           worldRenderer.pos(d6, 256.0, worldBorder.getMaxZ()).tex(f4 + f5, f4 + 0.0F).color(f, f2, f3, 1.0F).endVertex();
                           worldRenderer.pos(d6 + min2, 256.0, worldBorder.getMaxZ())
                              .tex(f4 + f6 + f5, f4 + 0.0F)
                              .color(f, f2, f3, 1.0F)
                              .endVertex();
                           worldRenderer.pos(d6 + min2, 0.0, worldBorder.getMaxZ())
                              .tex(f4 + f6 + f5, f4 + 128.0F)
                              .color(f, f2, f3, 1.0F)
                              .endVertex();
                           worldRenderer.pos(d6, 0.0, worldBorder.getMaxZ()).tex(f4 + f5, f4 + 128.0F).color(f, f2, f3, 1.0F).endVertex();
                           d6++;
                        }
                     }

                     if (d5 < worldBorder.getMinZ() + d) {
                        float f5 = 0.0F;

                        for (double d6 = max; d6 < min; f5 += 0.5F) {
                           double min2 = Math.min(1.0, min - d6);
                           float f6 = (float)min2 * 0.5F;
                           worldRenderer.pos(d6, 256.0, worldBorder.getMinZ()).tex(f4 + f5, f4 + 0.0F).color(f, f2, f3, 1.0F).endVertex();
                           worldRenderer.pos(d6 + min2, 256.0, worldBorder.getMinZ())
                              .tex(f4 + f6 + f5, f4 + 0.0F)
                              .color(f, f2, f3, 1.0F)
                              .endVertex();
                           worldRenderer.pos(d6 + min2, 0.0, worldBorder.getMinZ())
                              .tex(f4 + f6 + f5, f4 + 128.0F)
                              .color(f, f2, f3, 1.0F)
                              .endVertex();
                           worldRenderer.pos(d6, 0.0, worldBorder.getMinZ()).tex(f4 + f5, f4 + 128.0F).color(f, f2, f3, 1.0F).endVertex();
                           d6++;
                        }
                     }

                     instance.draw();
                     GL11.glTranslated(0.0, 0.0, 0.0);
                     GL11.glEnable(2884);
                     GL11.glDisable(3008);
                     GL11.glPolygonOffset(0.0F, 0.0F);
                     GL11.glDisable(32823);
                     GL11.glEnable(3008);
                     GlStateManager.disableBlend();
                     GlStateManager.popMatrix();
                     GL11.glDepthMask(true);
                  }
               }
            );
      }
   }

   public void removeIf(String str) {
      this.worldBorderList.removeIf(worldBorder -> Objects.equals(WorldBorder.getId(worldBorder), str));
   }

   public List<WorldBorder> getWorldBorderList() {
      return this.worldBorderList;
   }

   public void handleString(String str, String str2, int n, double d, double d2, double d3, double d4, boolean flag, boolean flag2) {
      this.worldBorderList.add(new WorldBorder(this, str, str2, n, d, d2, d3, d4, flag, flag2));
   }

   public void clear() {
      this.worldBorderList.clear();
   }

   public WorldBorderManager() {
      CheatBreaker.getInstance().getEventBus().checkClass2(ClientTickEvent.class, clientTickEvent -> this.worldBorderList.forEach(WorldBorder::tick));
      CheatBreaker.getInstance().getEventBus().checkClass2(FloatEvent6.class, this::onFloatEvent6);
      CheatBreaker.getInstance().getEventBus().checkClass2(BlockPosIBlockStateListEvent.class, this::onBlockPosIBlockStateListEvent);
   }

   public void handleString(String str, double d, double d2, double d3, double d4, int n) {
      this.worldBorderList
         .stream()
         .filter(worldBorder -> Objects.equals(WorldBorder.getId(worldBorder), str) && WorldBorder.isMoving(worldBorder))
         .findFirst()
         .ifPresent(worldBorder -> {
            WorldBorder.setTargetMin(worldBorder, new Vec2d(d, d2));
            WorldBorder.setTargetMax(worldBorder, new Vec2d(d3, d4));
            WorldBorder.setTransitionProgress(worldBorder, 0);
            WorldBorder.setTransitionTicks(worldBorder, n);
         });
   }

   public static CheatBreaker findCheatBreaker(WorldBorderManager worldBorderManager) {
      return worldBorderManager.cheatBreaker;
   }
}
