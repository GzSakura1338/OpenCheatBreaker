package net.optifine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.profiler.Profiler;
import net.optifine.util.MemoryMonitor;
import org.lwjgl.opengl.GL11;

public class Lagometer {
   public static Minecraft mc;
   public static GameSettings gameSettings;
   public static Profiler profiler;
   public static boolean active = false;
   public static Lagometer.TimerNano timerTick = new Lagometer.TimerNano();
   public static Lagometer.TimerNano timerScheduledExecutables = new Lagometer.TimerNano();
   public static Lagometer.TimerNano timerChunkUpload = new Lagometer.TimerNano();
   public static Lagometer.TimerNano timerChunkUpdate = new Lagometer.TimerNano();
   public static Lagometer.TimerNano timerVisibility = new Lagometer.TimerNano();
   public static Lagometer.TimerNano timerTerrain = new Lagometer.TimerNano();
   public static Lagometer.TimerNano timerServer = new Lagometer.TimerNano();
   public static long[] timesFrame = new long[512];
   public static long[] timesTick = new long[512];
   public static long[] timesScheduledExecutables = new long[512];
   public static long[] timesChunkUpload = new long[512];
   public static long[] timesChunkUpdate = new long[512];
   public static long[] timesVisibility = new long[512];
   public static long[] timesTerrain = new long[512];
   public static long[] timesServer = new long[512];
   public static boolean[] gcs = new boolean[512];
   public static int numRecordedFrameTimes = 0;
   public static long prevFrameTimeNano = -1L;
   public static long renderTimeNano = 0L;

   public static void updateLagometer() {
      if (mc == null) {
         mc = Minecraft.getMinecraft();
         gameSettings = mc.gameSettings;
         profiler = mc.mcProfiler;
      }

      if (gameSettings.showDebugInfo && (gameSettings.ofLagometer || gameSettings.showLagometer)) {
         active = true;
         long nanoTime = System.nanoTime();
         if (prevFrameTimeNano == -1L) {
            prevFrameTimeNano = nanoTime;
         } else {
            int n = numRecordedFrameTimes & timesFrame.length - 1;
            numRecordedFrameTimes++;
            boolean isGcEvent = MemoryMonitor.isGcEvent();
            timesFrame[n] = nanoTime - prevFrameTimeNano - renderTimeNano;
            timesTick[n] = timerTick.timeNano;
            timesScheduledExecutables[n] = timerScheduledExecutables.timeNano;
            timesChunkUpload[n] = timerChunkUpload.timeNano;
            timesChunkUpdate[n] = timerChunkUpdate.timeNano;
            timesVisibility[n] = timerVisibility.timeNano;
            timesTerrain[n] = timerTerrain.timeNano;
            timesServer[n] = timerServer.timeNano;
            gcs[n] = isGcEvent;
            timerTick.reset();
            timerScheduledExecutables.reset();
            timerVisibility.reset();
            timerChunkUpdate.reset();
            timerChunkUpload.reset();
            timerTerrain.reset();
            timerServer.reset();
            prevFrameTimeNano = System.nanoTime();
         }
      } else {
         active = false;
         prevFrameTimeNano = -1L;
      }
   }

   public static void showLagometer(ScaledResolution scaledResolution) {
      if (gameSettings != null && (gameSettings.ofLagometer || gameSettings.showLagometer)) {
         long nanoTime = System.nanoTime();
         GlStateManager.clear(256);
         GlStateManager.matrixMode(5889);
         GlStateManager.pushMatrix();
         GlStateManager.enableColorMaterial();
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0, mc.displayWidth, mc.displayHeight, 0.0, 1000.0, 3000.0);
         GlStateManager.matrixMode(5888);
         GlStateManager.pushMatrix();
         GlStateManager.loadIdentity();
         GlStateManager.translate(0.0F, 0.0F, -2000.0F);
         GL11.glLineWidth(1.0F);
         GlStateManager.disableTexture2D();
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         worldRenderer.begin(1, DefaultVertexFormats.POSITION_COLOR);

         for (int i = 0; i < timesFrame.length; i++) {
            int n = (i - numRecordedFrameTimes & timesFrame.length - 1) * 100 / timesFrame.length;
            n += 155;
            float displayHeight = mc.displayHeight;
            long l = 0L;
            if (gcs[i]) {
               renderTime(i, timesFrame[i], n, n / 2, 0, displayHeight, worldRenderer);
            } else {
               renderTime(i, timesFrame[i], n, n, n, displayHeight, worldRenderer);
               displayHeight -= (float)renderTime(i, timesServer[i], n / 2, n / 2, n / 2, displayHeight, worldRenderer);
               displayHeight -= (float)renderTime(i, timesTerrain[i], 0, n, 0, displayHeight, worldRenderer);
               displayHeight -= (float)renderTime(i, timesVisibility[i], n, n, 0, displayHeight, worldRenderer);
               displayHeight -= (float)renderTime(i, timesChunkUpdate[i], n, 0, 0, displayHeight, worldRenderer);
               displayHeight -= (float)renderTime(i, timesChunkUpload[i], n, 0, n, displayHeight, worldRenderer);
               displayHeight -= (float)renderTime(i, timesScheduledExecutables[i], 0, 0, n, displayHeight, worldRenderer);
               float f = displayHeight - (float)renderTime(i, timesTick[i], 0, n, n, displayHeight, worldRenderer);
            }
         }

         renderTimeDivider(0, timesFrame.length, 33333333L, 196, 196, 196, mc.displayHeight, worldRenderer);
         renderTimeDivider(0, timesFrame.length, 16666666L, 196, 196, 196, mc.displayHeight, worldRenderer);
         instance.draw();
         GlStateManager.enableTexture2D();
         int n = mc.displayHeight - 80;
         int n2 = mc.displayHeight - 160;
         mc.fontRendererObj.drawString("30", 2.0F, n2 + 1, -8947849);
         mc.fontRendererObj.drawString("30", 1.0F, n2, -3881788);
         mc.fontRendererObj.drawString("60", 2.0F, n + 1, -8947849);
         mc.fontRendererObj.drawString("60", 1.0F, n, -3881788);
         GlStateManager.matrixMode(5889);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.enableTexture2D();
         float f = 1.0F - (float)((System.currentTimeMillis() - MemoryMonitor.getStartTimeMs()) / 1000.0);
         f = Config.limit(f, 0.0F, 1.0F);
         int n3 = (int)(170.0F + f * 85.0F);
         int n4 = (int)(100.0F + f * 55.0F);
         int n5 = (int)(10.0F + f * 10.0F);
         int n6 = n3 << 16 | n4 << 8 | n5;
         int n7 = 512 / scaledResolution.getScaleFactor() + 2;
         int n8 = mc.displayHeight / scaledResolution.getScaleFactor() - 8;
         GuiIngame ingameGUI = mc.ingameGUI;
         GuiIngame.drawRect(n7 - 1, n8 - 1, n7 + 50, n8 + 10, -1605349296);
         mc.fontRendererObj.drawString(" " + MemoryMonitor.getAllocationRateMb() + " MB/s", n7, n8, n6);
         renderTimeNano = System.nanoTime() - nanoTime;
      }
   }

   public static long renderTime(int n, long l, int n2, int n3, int n4, float f, WorldRenderer worldRenderer) {
      long l2 = l / 200000L;
      if (l2 < 3L) {
         return 0L;
      }

      worldRenderer.pos(n + 0.5F, f - (float)l2 + 0.5F, 0.0).color(n2, n3, n4, 255).endVertex();
      worldRenderer.pos(n + 0.5F, f + 0.5F, 0.0).color(n2, n3, n4, 255).endVertex();
      return l2;
   }

   public static long renderTimeDivider(int n, int n2, long l, int n3, int n4, int n5, float f, WorldRenderer worldRenderer) {
      long l2 = l / 200000L;
      if (l2 < 3L) {
         return 0L;
      }

      worldRenderer.pos(n + 0.5F, f - (float)l2 + 0.5F, 0.0).color(n3, n4, n5, 255).endVertex();
      worldRenderer.pos(n2 + 0.5F, f - (float)l2 + 0.5F, 0.0).color(n3, n4, n5, 255).endVertex();
      return l2;
   }

   public static boolean isActive() {
      return active;
   }

   public static class TimerNano {
      public long timeStartNano = 0L;
      public long timeNano = 0L;

      public void start() {
         if (Lagometer.active && this.timeStartNano == 0L) {
            this.timeStartNano = System.nanoTime();
         }
      }

      public void end() {
         if (Lagometer.active && this.timeStartNano != 0L) {
            this.timeNano = this.timeNano + (System.nanoTime() - this.timeStartNano);
            this.timeStartNano = 0L;
         }
      }

      public void reset() {
         this.timeNano = 0L;
         this.timeStartNano = 0L;
      }
   }
}
