package net.minecraft.client.renderer;

import net.optifine.SmartAnimations;

public class Tessellator {
   public WorldVertexBufferUploader vboUploader = new WorldVertexBufferUploader();
   public WorldRenderer worldRenderer;
   public static Tessellator instance = new Tessellator(2097152);

   public static Tessellator getInstance() {
      return instance;
   }

   public Tessellator(int bufferSize) {
      this.worldRenderer = new WorldRenderer(bufferSize);
   }

   public void draw() {
      if (this.worldRenderer.animatedSprites != null) {
         SmartAnimations.spritesRendered(this.worldRenderer.animatedSprites);
      }

      this.worldRenderer.finishDrawing();
      this.vboUploader.draw(this.worldRenderer);
   }

   public WorldRenderer getWorldRenderer() {
      return this.worldRenderer;
   }
}
