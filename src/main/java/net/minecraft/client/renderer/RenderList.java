package net.minecraft.client.renderer;

import java.nio.Buffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.EnumWorldBlockLayer;
import net.optifine.Config;
import org.lwjgl.opengl.GL11;

public class RenderList extends ChunkRenderContainer {
   public double viewEntityX;
   public double viewEntityY;
   public double viewEntityZ;
   public IntBuffer bufferLists = GLAllocation.createDirectIntBuffer(16);

   @Override
   public void renderChunkLayer(EnumWorldBlockLayer layer) {
      if (this.initialized) {
         if (!Config.isRenderRegions()) {
            for (RenderChunk renderChunk : this.renderChunks) {
               ListedRenderChunk listedRenderChunk = (ListedRenderChunk)renderChunk;
               GlStateManager.pushMatrix();
               this.preRenderChunk(renderChunk);
               GL11.glCallList(listedRenderChunk.getDisplayList(layer, listedRenderChunk.getCompiledChunk()));
               GlStateManager.popMatrix();
            }
         } else {
            int n = Integer.MIN_VALUE;
            int n2 = Integer.MIN_VALUE;

            for (RenderChunk renderChunk : this.renderChunks) {
               ListedRenderChunk listedRenderChunk = (ListedRenderChunk)renderChunk;
               if (n != renderChunk.regionX || n2 != renderChunk.regionZ) {
                  if (this.bufferLists.position() > 0) {
                     this.drawRegion(n, n2, this.bufferLists);
                  }

                  n = renderChunk.regionX;
                  n2 = renderChunk.regionZ;
               }

               if (this.bufferLists.position() >= this.bufferLists.capacity()) {
                  IntBuffer createDirectIntBuffer = GLAllocation.createDirectIntBuffer(this.bufferLists.capacity() * 2);
                  ((Buffer)this.bufferLists).flip();
                  createDirectIntBuffer.put(this.bufferLists);
                  this.bufferLists = createDirectIntBuffer;
               }

               this.bufferLists.put(listedRenderChunk.getDisplayList(layer, listedRenderChunk.getCompiledChunk()));
            }

            if (this.bufferLists.position() > 0) {
               this.drawRegion(n, n2, this.bufferLists);
            }
         }

         if (Config.isMultiTexture()) {
            GlStateManager.bindCurrentTexture();
         }

         GlStateManager.resetColor();
         this.renderChunks.clear();
      }
   }

   @Override
   public void initialize(double d, double d2, double d3) {
      this.viewEntityX = d;
      this.viewEntityY = d2;
      this.viewEntityZ = d3;
      super.initialize(d, d2, d3);
   }

   public void drawRegion(int n, int n2, IntBuffer intBuffer) {
      GlStateManager.pushMatrix();
      this.preRenderRegion(n, 0, n2);
      ((Buffer)intBuffer).flip();
      GlStateManager.callLists(intBuffer);
      ((Buffer)intBuffer).clear();
      GlStateManager.popMatrix();
   }

   public void preRenderRegion(int n, int n2, int n3) {
      GlStateManager.translate((float)(n - this.viewEntityX), (float)(n2 - this.viewEntityY), (float)(n3 - this.viewEntityZ));
   }
}
