package net.minecraft.client.renderer.tileentity;

import com.cheatbreaker.client.CheatBreaker;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.TexGen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.shaders.ShadersRender;

public class TileEntityEndPortalRenderer extends TileEntitySpecialRenderer<TileEntityEndPortal> {
   public static ResourceLocation END_SKY_TEXTURE = new ResourceLocation("textures/environment/end_sky.png");
   public static ResourceLocation END_PORTAL_TEXTURE = new ResourceLocation("textures/entity/end_portal.png");
   public static Random RANDOM = new Random(31100L);
   public FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);

   public void renderTileEntityAt(TileEntityEndPortal te, double x, double y, double z, float partialTicks, int destroyStage) {
      if (!CheatBreaker.getInstance().getKeybindSettings().hideEndPortals.getBooleanValue()) {
         if (!Config.isShaders() || !ShadersRender.renderEndPortal(te, x, y, z, partialTicks, destroyStage, 0.75F)) {
            float entityX = (float)this.rendererDispatcher.entityX;
            float entityY = (float)this.rendererDispatcher.entityY;
            float entityZ = (float)this.rendererDispatcher.entityZ;
            GlStateManager.disableLighting();
            RANDOM.setSeed(31100L);
            float f = 0.75F;

            for (int i = 0; i < 16; i++) {
               GlStateManager.pushMatrix();
               float f2 = 16 - i;
               float f3 = 0.0625F;
               float f4 = 1.0F / (f2 + 1.0F);
               if (i == 0) {
                  this.bindTexture(END_SKY_TEXTURE);
                  f4 = 0.1F;
                  f2 = 65.0F;
                  f3 = 0.125F;
                  GlStateManager.enableBlend();
                  GlStateManager.blendFunc(770, 771);
               }

               if (i >= 1) {
                  this.bindTexture(END_PORTAL_TEXTURE);
               }

               if (i == 1) {
                  GlStateManager.enableBlend();
                  GlStateManager.blendFunc(1, 1);
                  f3 = 0.5F;
               }

               float f5 = (float)(-(y + f));
               float f6 = f5 + (float)ActiveRenderInfo.getPosition().yCoord;
               float f7 = f5 + f2 + (float)ActiveRenderInfo.getPosition().yCoord;
               float f8 = f6 / f7;
               f8 = (float)(y + f) + f8;
               GlStateManager.translate(entityX, f8, entityZ);
               GlStateManager.texGen(TexGen.S, 9217);
               GlStateManager.texGen(TexGen.T, 9217);
               GlStateManager.texGen(TexGen.R, 9217);
               GlStateManager.texGen(TexGen.Q, 9216);
               GlStateManager.texGen(TexGen.S, 9473, this.createBuffer(1.0F, 0.0F, 0.0F, 0.0F));
               GlStateManager.texGen(TexGen.T, 9473, this.createBuffer(0.0F, 0.0F, 1.0F, 0.0F));
               GlStateManager.texGen(TexGen.R, 9473, this.createBuffer(0.0F, 0.0F, 0.0F, 1.0F));
               GlStateManager.texGen(TexGen.Q, 9474, this.createBuffer(0.0F, 1.0F, 0.0F, 0.0F));
               GlStateManager.enableTexGenCoord(TexGen.S);
               GlStateManager.enableTexGenCoord(TexGen.T);
               GlStateManager.enableTexGenCoord(TexGen.R);
               GlStateManager.enableTexGenCoord(TexGen.Q);
               GlStateManager.popMatrix();
               GlStateManager.matrixMode(5890);
               GlStateManager.pushMatrix();
               GlStateManager.loadIdentity();
               GlStateManager.translate(0.0F, (float)(Minecraft.getSystemTime() % 700000L) / 700000.0F, 0.0F);
               GlStateManager.scale(f3, f3, f3);
               GlStateManager.translate(0.5F, 0.5F, 0.0F);
               GlStateManager.rotate((i * i * 4321 + i * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.translate(-0.5F, -0.5F, 0.0F);
               GlStateManager.translate(-entityX, -entityZ, -entityY);
               f6 = f5 + (float)ActiveRenderInfo.getPosition().yCoord;
               GlStateManager.translate(
                  (float)ActiveRenderInfo.getPosition().xCoord * f2 / f6, (float)ActiveRenderInfo.getPosition().zCoord * f2 / f6, -entityY
               );
               Tessellator instance = Tessellator.getInstance();
               WorldRenderer worldRenderer = instance.getWorldRenderer();
               worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
               float f9 = (RANDOM.nextFloat() * 0.5F + 0.1F) * f4;
               float f10 = (RANDOM.nextFloat() * 0.5F + 0.4F) * f4;
               float f11 = (RANDOM.nextFloat() * 0.5F + 0.5F) * f4;
               if (i == 0) {
                  f9 = f10 = f11 = 1.0F * f4;
               }

               worldRenderer.pos(x, y + f, z).color(f9, f10, f11, 1.0F).endVertex();
               worldRenderer.pos(x, y + f, z + 1.0).color(f9, f10, f11, 1.0F).endVertex();
               worldRenderer.pos(x + 1.0, y + f, z + 1.0).color(f9, f10, f11, 1.0F).endVertex();
               worldRenderer.pos(x + 1.0, y + f, z).color(f9, f10, f11, 1.0F).endVertex();
               instance.draw();
               GlStateManager.popMatrix();
               GlStateManager.matrixMode(5888);
               this.bindTexture(END_SKY_TEXTURE);
            }

            GlStateManager.disableBlend();
            GlStateManager.disableTexGenCoord(TexGen.S);
            GlStateManager.disableTexGenCoord(TexGen.T);
            GlStateManager.disableTexGenCoord(TexGen.R);
            GlStateManager.disableTexGenCoord(TexGen.Q);
            GlStateManager.enableLighting();
         }
      }
   }

   public FloatBuffer createBuffer(float f, float f2, float f3, float f4) {
      ((Buffer)this.buffer).clear();
      this.buffer.put(f).put(f2).put(f3).put(f4);
      ((Buffer)this.buffer).flip();
      return this.buffer;
   }
}
