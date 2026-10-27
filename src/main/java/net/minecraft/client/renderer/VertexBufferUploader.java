package net.minecraft.client.renderer;

import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.optifine.Config;

public class VertexBufferUploader extends WorldVertexBufferUploader {
   public VertexBuffer vertexBuffer = null;

   @Override
   public void draw(WorldRenderer worldRenderer) {
      if (worldRenderer.getDrawMode() == 7 && Config.isQuadsToTriangles()) {
         worldRenderer.quadsToTriangles();
         this.vertexBuffer.setDrawMode(worldRenderer.getDrawMode());
      }

      this.vertexBuffer.bufferData(worldRenderer.getByteBuffer());
      worldRenderer.reset();
   }

   public void setVertexBuffer(VertexBuffer vertexBufferIn) {
      this.vertexBuffer = vertexBufferIn;
   }
}
