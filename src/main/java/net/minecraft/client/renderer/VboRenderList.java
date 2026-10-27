package net.minecraft.client.renderer;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.EnumWorldBlockLayer;
import net.optifine.Config;
import net.optifine.render.VboRegion;
import net.optifine.shaders.ShadersRender;
import org.lwjgl.opengl.GL11;

public class VboRenderList extends ChunkRenderContainer {
   public double viewEntityX;
   public double viewEntityY;
   public double viewEntityZ;

   @Override
   public void renderChunkLayer(EnumWorldBlockLayer layer) {
      if (this.initialized) {
         if (!Config.isRenderRegions()) {
            for (RenderChunk renderChunk : this.renderChunks) {
               VertexBuffer vertexBufferByLayer = renderChunk.getVertexBufferByLayer(layer.ordinal());
               GlStateManager.pushMatrix();
               this.preRenderChunk(renderChunk);
               renderChunk.multModelviewMatrix();
               vertexBufferByLayer.bindBuffer();
               this.setupArrayPointers();
               vertexBufferByLayer.drawArrays(7);
               GlStateManager.popMatrix();
            }
         } else {
            int n = Integer.MIN_VALUE;
            int n2 = Integer.MIN_VALUE;
            VboRegion vboRegion = null;

            for (RenderChunk renderChunk : this.renderChunks) {
               VertexBuffer vertexBufferByLayer = renderChunk.getVertexBufferByLayer(layer.ordinal());
               VboRegion vboRegion2 = vertexBufferByLayer.getVboRegion();
               if (vboRegion2 != vboRegion || n != renderChunk.regionX || n2 != renderChunk.regionZ) {
                  if (vboRegion != null) {
                     this.drawRegion(n, n2, vboRegion);
                  }

                  n = renderChunk.regionX;
                  n2 = renderChunk.regionZ;
                  vboRegion = vboRegion2;
               }

               vertexBufferByLayer.drawArrays(7);
            }

            if (vboRegion != null) {
               this.drawRegion(n, n2, vboRegion);
            }
         }

         OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
         GlStateManager.resetColor();
         this.renderChunks.clear();
      }
   }

   public void setupArrayPointers() {
      if (Config.isShaders()) {
         ShadersRender.setupArrayPointersVbo();
      } else {
         GL11.glVertexPointer(3, 5126, 28, 0L);
         GL11.glColorPointer(4, 5121, 28, 12L);
         GL11.glTexCoordPointer(2, 5126, 28, 16L);
         OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
         GL11.glTexCoordPointer(2, 5122, 28, 24L);
         OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
      }
   }

   @Override
   public void initialize(double d, double d2, double d3) {
      this.viewEntityX = d;
      this.viewEntityY = d2;
      this.viewEntityZ = d3;
      super.initialize(d, d2, d3);
   }

   public void drawRegion(int n, int n2, VboRegion vboRegion) {
      GlStateManager.pushMatrix();
      this.preRenderRegion(n, 0, n2);
      vboRegion.finishDraw(this);
      GlStateManager.popMatrix();
   }

   public void preRenderRegion(int n, int n2, int n3) {
      GlStateManager.translate((float)(n - this.viewEntityX), (float)(n2 - this.viewEntityY), (float)(n3 - this.viewEntityZ));
   }
}
