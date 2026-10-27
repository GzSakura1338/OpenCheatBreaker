package net.minecraft.client.shader;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import org.lwjgl.util.vector.Matrix4f;

public class Shader {
   public List<Object> listAuxFramebuffers = Lists.newArrayList();
   public List<String> listAuxNames = Lists.newArrayList();
   public List<Integer> listAuxWidths = Lists.newArrayList();
   public List<Integer> listAuxHeights = Lists.newArrayList();
   public ShaderManager manager;
   public Framebuffer framebufferIn;
   public Framebuffer framebufferOut;
   public Matrix4f projectionMatrix;

   public Shader(IResourceManager resourceManager, String str, Framebuffer framebuffer, Framebuffer framebuffer2) throws IOException, JsonException {
      this.manager = new ShaderManager(resourceManager, str);
      this.framebufferIn = framebuffer;
      this.framebufferOut = framebuffer2;
   }

   public void deleteShader() {
      this.manager.deleteShader();
   }

   public void addAuxFramebuffer(String str, Object obj, int n, int n2) {
      this.listAuxNames.add(this.listAuxNames.size(), str);
      this.listAuxFramebuffers.add(this.listAuxFramebuffers.size(), obj);
      this.listAuxWidths.add(this.listAuxWidths.size(), n);
      this.listAuxHeights.add(this.listAuxHeights.size(), n2);
   }

   public void preLoadShader() {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.disableDepth();
      GlStateManager.disableAlpha();
      GlStateManager.disableFog();
      GlStateManager.disableLighting();
      GlStateManager.disableColorMaterial();
      GlStateManager.enableTexture2D();
      GlStateManager.bindTexture(0);
   }

   public void setProjectionMatrix(Matrix4f matrix4f) {
      this.projectionMatrix = matrix4f;
   }

   public void loadShader(float f) {
      this.preLoadShader();
      this.framebufferIn.unbindFramebuffer();
      float framebufferTextureWidth = this.framebufferOut.framebufferTextureWidth;
      float framebufferTextureHeight = this.framebufferOut.framebufferTextureHeight;
      GlStateManager.viewport(0, 0, (int)framebufferTextureWidth, (int)framebufferTextureHeight);
      this.manager.addSamplerTexture("DiffuseSampler", this.framebufferIn);

      for (int i = 0; i < this.listAuxFramebuffers.size(); i++) {
         this.manager.addSamplerTexture(this.listAuxNames.get(i), this.listAuxFramebuffers.get(i));
         this.manager.getShaderUniformOrDefault("AuxSize" + i).set(this.listAuxWidths.get(i).intValue(), this.listAuxHeights.get(i).intValue());
      }

      this.manager.getShaderUniformOrDefault("ProjMat").set(this.projectionMatrix);
      this.manager.getShaderUniformOrDefault("InSize").set(this.framebufferIn.framebufferTextureWidth, this.framebufferIn.framebufferTextureHeight);
      this.manager.getShaderUniformOrDefault("OutSize").set(framebufferTextureWidth, framebufferTextureHeight);
      this.manager.getShaderUniformOrDefault("Time").set(f);
      Minecraft minecraft = Minecraft.getMinecraft();
      this.manager.getShaderUniformOrDefault("ScreenSize").set(minecraft.displayWidth, minecraft.displayHeight);
      this.manager.useShader();
      this.framebufferOut.framebufferClear();
      this.framebufferOut.bindFramebuffer(false);
      GlStateManager.depthMask(false);
      GlStateManager.colorMask(true, true, true, true);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(0.0, framebufferTextureHeight, 500.0).color(255, 255, 255, 255).endVertex();
      worldRenderer.pos(framebufferTextureWidth, framebufferTextureHeight, 500.0).color(255, 255, 255, 255).endVertex();
      worldRenderer.pos(framebufferTextureWidth, 0.0, 500.0).color(255, 255, 255, 255).endVertex();
      worldRenderer.pos(0.0, 0.0, 500.0).color(255, 255, 255, 255).endVertex();
      instance.draw();
      GlStateManager.depthMask(true);
      GlStateManager.colorMask(true, true, true, true);
      this.manager.endShader();
      this.framebufferOut.unbindFramebuffer();
      this.framebufferIn.unbindFramebufferTexture();

      for (Object obj : this.listAuxFramebuffers) {
         if (obj instanceof Framebuffer) {
            ((Framebuffer)obj).unbindFramebufferTexture();
         }
      }
   }

   public ShaderManager getShaderManager() {
      return this.manager;
   }
}
