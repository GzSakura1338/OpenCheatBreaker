package net.optifine.shaders;

import java.nio.IntBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.reflect.Reflector;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class ShadersRender {
   public static ResourceLocation END_PORTAL_TEXTURE = new ResourceLocation("textures/entity/end_portal.png");

   public static void setFrustrumPosition(ICamera camera, double d, double d2, double d3) {
      camera.setPosition(d, d2, d3);
   }

   public static void setupTerrain(RenderGlobal renderGlobal, Entity entity, double d, ICamera camera, int n, boolean flag) {
      renderGlobal.setupTerrain(entity, d, camera, n, flag);
   }

   public static void beginTerrainSolid() {
      if (Shaders.isRenderingWorld) {
         Shaders.fogEnabled = true;
         Shaders.useProgram(Shaders.ProgramTerrain);
      }
   }

   public static void beginTerrainCutoutMipped() {
      if (Shaders.isRenderingWorld) {
         Shaders.useProgram(Shaders.ProgramTerrain);
      }
   }

   public static void beginTerrainCutout() {
      if (Shaders.isRenderingWorld) {
         Shaders.useProgram(Shaders.ProgramTerrain);
      }
   }

   public static void endTerrain() {
      if (Shaders.isRenderingWorld) {
         Shaders.useProgram(Shaders.ProgramTexturedLit);
      }
   }

   public static void renderHand0(EntityRenderer entityRenderer, float f, int n) {
      if (!Shaders.isShadowPass) {
         boolean isItemToRenderMainTranslucent = Shaders.isItemToRenderMainTranslucent();
         boolean isItemToRenderOffTranslucent = Shaders.isItemToRenderOffTranslucent();
         if (!isItemToRenderMainTranslucent || !isItemToRenderOffTranslucent) {
            Shaders.readCenterDepth();
            Shaders.beginHand(false);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Shaders.setSkipRenderHands(isItemToRenderMainTranslucent, isItemToRenderOffTranslucent);
            entityRenderer.renderHand(f, n, true, false, false);
            Shaders.endHand();
            Shaders.setHandsRendered(!isItemToRenderMainTranslucent, !isItemToRenderOffTranslucent);
            Shaders.setSkipRenderHands(false, false);
         }
      }
   }

   public static void renderHand1(EntityRenderer entityRenderer, float f, int n) {
      if (!Shaders.isShadowPass && !Shaders.isBothHandsRendered()) {
         Shaders.readCenterDepth();
         GlStateManager.enableBlend();
         Shaders.beginHand(true);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         Shaders.setSkipRenderHands(Shaders.isHandRenderedMain(), Shaders.isHandRenderedOff());
         entityRenderer.renderHand(f, n, true, false, true);
         Shaders.endHand();
         Shaders.setHandsRendered(true, true);
         Shaders.setSkipRenderHands(false, false);
      }
   }

   public static void renderItemFP(ItemRenderer itemRenderer, float f, boolean flag) {
      Shaders.setRenderingFirstPersonHand(true);
      GlStateManager.depthMask(true);
      if (flag) {
         GlStateManager.depthFunc(519);
         GL11.glPushMatrix();
         IntBuffer activeDrawBuffers = Shaders.activeDrawBuffers;
         Shaders.setDrawBuffers(Shaders.drawBuffersNone);
         Shaders.renderItemKeepDepthMask = true;
         itemRenderer.renderItemInFirstPerson(f);
         Shaders.renderItemKeepDepthMask = false;
         Shaders.setDrawBuffers(activeDrawBuffers);
         GL11.glPopMatrix();
      }

      GlStateManager.depthFunc(515);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      itemRenderer.renderItemInFirstPerson(f);
      Shaders.setRenderingFirstPersonHand(false);
   }

   public static void renderFPOverlay(EntityRenderer entityRenderer, float f, int n) {
      if (!Shaders.isShadowPass) {
         Shaders.beginFPOverlay();
         entityRenderer.renderHand(f, n, false, true, false);
         Shaders.endFPOverlay();
      }
   }

   public static void beginBlockDamage() {
      if (Shaders.isRenderingWorld) {
         Shaders.useProgram(Shaders.ProgramDamagedBlock);
         if (Shaders.ProgramDamagedBlock.getId() == Shaders.ProgramTerrain.getId()) {
            Shaders.setDrawBuffers(Shaders.drawBuffersColorAtt0);
            GlStateManager.depthMask(false);
         }
      }
   }

   public static void endBlockDamage() {
      if (Shaders.isRenderingWorld) {
         GlStateManager.depthMask(true);
         Shaders.useProgram(Shaders.ProgramTexturedLit);
      }
   }

   public static void renderShadowMap(EntityRenderer entityRenderer, int n, float f, long l) {
      if (Shaders.usedShadowDepthBuffers > 0 && --Shaders.shadowPassCounter <= 0) {
         Minecraft minecraft = Minecraft.getMinecraft();
         minecraft.mcProfiler.endStartSection("shadow pass");
         RenderGlobal renderGlobal = minecraft.renderGlobal;
         Shaders.isShadowPass = true;
         Shaders.shadowPassCounter = Shaders.shadowPassInterval;
         Shaders.preShadowPassThirdPersonView = minecraft.gameSettings.thirdPersonView;
         minecraft.gameSettings.thirdPersonView = 1;
         Shaders.checkGLError("pre shadow");
         GL11.glMatrixMode(5889);
         GL11.glPushMatrix();
         GL11.glMatrixMode(5888);
         GL11.glPushMatrix();
         minecraft.mcProfiler.endStartSection("shadow clear");
         EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.sfb);
         Shaders.checkGLError("shadow bind sfb");
         minecraft.mcProfiler.endStartSection("shadow camera");
         entityRenderer.setupCameraTransform(f, 2);
         Shaders.setCameraShadow(f);
         Shaders.checkGLError("shadow camera");
         Shaders.useProgram(Shaders.ProgramShadow);
         GL20.glDrawBuffers(Shaders.sfbDrawBuffers);
         Shaders.checkGLError("shadow drawbuffers");
         GL11.glReadBuffer(0);
         Shaders.checkGLError("shadow readbuffer");
         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, Shaders.sfbDepthTextures.get(0), 0);
         if (Shaders.usedShadowColorBuffers != 0) {
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, Shaders.sfbColorTextures.get(0), 0);
         }

         Shaders.checkFramebufferStatus("shadow fb");
         GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glClear(Shaders.usedShadowColorBuffers != 0 ? 16640 : 256);
         Shaders.checkGLError("shadow clear");
         minecraft.mcProfiler.endStartSection("shadow frustum");
         ClippingHelper instance = ClippingHelperShadow.getInstance();
         minecraft.mcProfiler.endStartSection("shadow culling");
         Frustum frustum = new Frustum(instance);
         Entity renderViewEntity = minecraft.getRenderViewEntity();
         double d = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * f;
         double d2 = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * f;
         double d3 = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * f;
         frustum.setPosition(d, d2, d3);
         GlStateManager.shadeModel(7425);
         GlStateManager.enableDepth();
         GlStateManager.depthFunc(515);
         GlStateManager.depthMask(true);
         GlStateManager.colorMask(true, true, true, true);
         GlStateManager.disableCull();
         minecraft.mcProfiler.endStartSection("shadow prepareterrain");
         minecraft.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
         minecraft.mcProfiler.endStartSection("shadow setupterrain");
         int n2 = 0;
         n2 = entityRenderer.frameCount++;
         renderGlobal.setupTerrain(renderViewEntity, f, frustum, n2, minecraft.thePlayer.isSpectator());
         minecraft.mcProfiler.endStartSection("shadow updatechunks");
         minecraft.mcProfiler.endStartSection("shadow terrain");
         GlStateManager.matrixMode(5888);
         GlStateManager.pushMatrix();
         GlStateManager.disableAlpha();
         renderGlobal.renderBlockLayer(EnumWorldBlockLayer.SOLID, f, 2, renderViewEntity);
         Shaders.checkGLError("shadow terrain solid");
         GlStateManager.enableAlpha();
         renderGlobal.renderBlockLayer(EnumWorldBlockLayer.CUTOUT_MIPPED, f, 2, renderViewEntity);
         Shaders.checkGLError("shadow terrain cutoutmipped");
         minecraft.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
         renderGlobal.renderBlockLayer(EnumWorldBlockLayer.CUTOUT, f, 2, renderViewEntity);
         Shaders.checkGLError("shadow terrain cutout");
         minecraft.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
         GlStateManager.shadeModel(7424);
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         minecraft.mcProfiler.endStartSection("shadow entities");
         if (Reflector.ForgeHooksClient_setRenderPass.exists()) {
            Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, 0);
         }

         renderGlobal.renderEntities(renderViewEntity, frustum, f);
         Shaders.checkGLError("shadow entities");
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
         GlStateManager.disableBlend();
         GlStateManager.enableCull();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.alphaFunc(516, 0.1F);
         if (Shaders.usedShadowDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33989);
            Shaders.checkGLError("pre copy shadow depth");
            GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, Shaders.shadowMapWidth, Shaders.shadowMapHeight);
            Shaders.checkGLError("copy shadow depth");
            GlStateManager.setActiveTexture(33984);
         }

         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
         minecraft.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
         GlStateManager.shadeModel(7425);
         Shaders.checkGLError("shadow pre-translucent");
         GL20.glDrawBuffers(Shaders.sfbDrawBuffers);
         Shaders.checkGLError("shadow drawbuffers pre-translucent");
         Shaders.checkFramebufferStatus("shadow pre-translucent");
         if (Shaders.isRenderShadowTranslucent()) {
            minecraft.mcProfiler.endStartSection("shadow translucent");
            renderGlobal.renderBlockLayer(EnumWorldBlockLayer.TRANSLUCENT, f, 2, renderViewEntity);
            Shaders.checkGLError("shadow translucent");
         }

         if (Reflector.ForgeHooksClient_setRenderPass.exists()) {
            RenderHelper.enableStandardItemLighting();
            Reflector.call(Reflector.ForgeHooksClient_setRenderPass, 1);
            renderGlobal.renderEntities(renderViewEntity, frustum, f);
            Reflector.call(Reflector.ForgeHooksClient_setRenderPass, -1);
            RenderHelper.disableStandardItemLighting();
            Shaders.checkGLError("shadow entities 1");
         }

         GlStateManager.shadeModel(7424);
         GlStateManager.depthMask(true);
         GlStateManager.enableCull();
         GlStateManager.disableBlend();
         GL11.glFlush();
         Shaders.checkGLError("shadow flush");
         Shaders.isShadowPass = false;
         minecraft.gameSettings.thirdPersonView = Shaders.preShadowPassThirdPersonView;
         minecraft.mcProfiler.endStartSection("shadow postprocess");
         if (Shaders.hasGlGenMipmap) {
            if (Shaders.usedShadowDepthBuffers >= 1) {
               if (Shaders.shadowMipmapEnabled[0]) {
                  GlStateManager.setActiveTexture(33988);
                  GlStateManager.bindTexture(Shaders.sfbDepthTextures.get(0));
                  GL30.glGenerateMipmap(3553);
                  GL11.glTexParameteri(3553, 10241, Shaders.shadowFilterNearest[0] ? 9984 : 9987);
               }

               if (Shaders.usedShadowDepthBuffers >= 2 && Shaders.shadowMipmapEnabled[1]) {
                  GlStateManager.setActiveTexture(33989);
                  GlStateManager.bindTexture(Shaders.sfbDepthTextures.get(1));
                  GL30.glGenerateMipmap(3553);
                  GL11.glTexParameteri(3553, 10241, Shaders.shadowFilterNearest[1] ? 9984 : 9987);
               }

               GlStateManager.setActiveTexture(33984);
            }

            if (Shaders.usedShadowColorBuffers >= 1) {
               if (Shaders.shadowColorMipmapEnabled[0]) {
                  GlStateManager.setActiveTexture(33997);
                  GlStateManager.bindTexture(Shaders.sfbColorTextures.get(0));
                  GL30.glGenerateMipmap(3553);
                  GL11.glTexParameteri(3553, 10241, Shaders.shadowColorFilterNearest[0] ? 9984 : 9987);
               }

               if (Shaders.usedShadowColorBuffers >= 2 && Shaders.shadowColorMipmapEnabled[1]) {
                  GlStateManager.setActiveTexture(33998);
                  GlStateManager.bindTexture(Shaders.sfbColorTextures.get(1));
                  GL30.glGenerateMipmap(3553);
                  GL11.glTexParameteri(3553, 10241, Shaders.shadowColorFilterNearest[1] ? 9984 : 9987);
               }

               GlStateManager.setActiveTexture(33984);
            }
         }

         Shaders.checkGLError("shadow postprocess");
         EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.dfb);
         GL11.glViewport(0, 0, Shaders.renderWidth, Shaders.renderHeight);
         Shaders.activeDrawBuffers = null;
         minecraft.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
         Shaders.useProgram(Shaders.ProgramTerrain);
         GL11.glMatrixMode(5888);
         GL11.glPopMatrix();
         GL11.glMatrixMode(5889);
         GL11.glPopMatrix();
         GL11.glMatrixMode(5888);
         Shaders.checkGLError("shadow end");
      }
   }

   public static void preRenderChunkLayer(EnumWorldBlockLayer enumWorldBlockLayer) {
      if (Shaders.isRenderBackFace(enumWorldBlockLayer)) {
         GlStateManager.disableCull();
      }

      if (OpenGlHelper.useVbo()) {
         GL11.glEnableClientState(32885);
         GL20.glEnableVertexAttribArray(Shaders.midTexCoordAttrib);
         GL20.glEnableVertexAttribArray(Shaders.tangentAttrib);
         GL20.glEnableVertexAttribArray(Shaders.entityAttrib);
      }
   }

   public static void postRenderChunkLayer(EnumWorldBlockLayer enumWorldBlockLayer) {
      if (OpenGlHelper.useVbo()) {
         GL11.glDisableClientState(32885);
         GL20.glDisableVertexAttribArray(Shaders.midTexCoordAttrib);
         GL20.glDisableVertexAttribArray(Shaders.tangentAttrib);
         GL20.glDisableVertexAttribArray(Shaders.entityAttrib);
      }

      if (Shaders.isRenderBackFace(enumWorldBlockLayer)) {
         GlStateManager.enableCull();
      }
   }

   public static void setupArrayPointersVbo() {
      byte b = 14;
      GL11.glVertexPointer(3, 5126, 56, 0L);
      GL11.glColorPointer(4, 5121, 56, 12L);
      GL11.glTexCoordPointer(2, 5126, 56, 16L);
      OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
      GL11.glTexCoordPointer(2, 5122, 56, 24L);
      OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
      GL11.glNormalPointer(5120, 56, 28L);
      GL20.glVertexAttribPointer(Shaders.midTexCoordAttrib, 2, 5126, false, 56, 32L);
      GL20.glVertexAttribPointer(Shaders.tangentAttrib, 4, 5122, false, 56, 40L);
      GL20.glVertexAttribPointer(Shaders.entityAttrib, 3, 5122, false, 56, 48L);
   }

   public static void renderEnchantedGlintBegin() {
      Shaders.useProgram(Shaders.ProgramArmorGlint);
   }

   public static void renderEnchantedGlintEnd() {
      if (Shaders.isRenderingWorld) {
         if (Shaders.isRenderingFirstPersonHand() && Shaders.isRenderBothHands()) {
            Shaders.useProgram(Shaders.ProgramHand);
         } else {
            Shaders.useProgram(Shaders.ProgramEntities);
         }
      } else {
         Shaders.useProgram(Shaders.ProgramNone);
      }
   }

   public static boolean renderEndPortal(TileEntityEndPortal tileEntityEndPortal, double d, double d2, double d3, float f, int n, float f2) {
      if (!Shaders.isShadowPass && Shaders.activeProgram.getId() == 0) {
         return false;
      }

      GlStateManager.disableLighting();
      Config.getTextureManager().bindTexture(END_PORTAL_TEXTURE);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.BLOCK);
      float f3 = 0.5F;
      float f4 = f3 * 0.15F;
      float f5 = f3 * 0.3F;
      float f6 = f3 * 0.4F;
      float f7 = 0.0F;
      float f8 = 0.2F;
      float f9 = (float)(System.currentTimeMillis() % 100000L) / 100000.0F;
      short s = 240;
      worldRenderer.pos(d, d2 + f2, d3 + 1.0).color(f4, f5, f6, 1.0F).tex(f7 + f9, f7 + f9).lightmap(s, s).endVertex();
      worldRenderer.pos(d + 1.0, d2 + f2, d3 + 1.0).color(f4, f5, f6, 1.0F).tex(f7 + f9, f8 + f9).lightmap(s, s).endVertex();
      worldRenderer.pos(d + 1.0, d2 + f2, d3).color(f4, f5, f6, 1.0F).tex(f8 + f9, f8 + f9).lightmap(s, s).endVertex();
      worldRenderer.pos(d, d2 + f2, d3).color(f4, f5, f6, 1.0F).tex(f8 + f9, f7 + f9).lightmap(s, s).endVertex();
      instance.draw();
      GlStateManager.enableLighting();
      return true;
   }

   public static void noop() {
   }

   public static void useProgram() {
      Shaders.useProgram(Shaders.ProgramBeaconBeam);
   }

   public static void noop2() {
   }

   public static void useProgram2() {
      if (Shaders.isRenderingWorld) {
         Shaders.useProgram(Shaders.ProgramTexturedLit);
      }
   }

   public static void disableBlend() {
      GlStateManager.disableBlend();
   }

   public static void noop3() {
   }

   public static void run() {
      if (Shaders.isRenderingWorld) {
         if (Shaders.usedDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33995);
            Shaders.checkGLError("pre copy depth");
            GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, Shaders.renderWidth, Shaders.renderHeight);
            Shaders.checkGLError("copy depth");
            GlStateManager.setActiveTexture(33984);
         }

         Shaders.useProgram(Shaders.ProgramWater);
      }
   }
}
