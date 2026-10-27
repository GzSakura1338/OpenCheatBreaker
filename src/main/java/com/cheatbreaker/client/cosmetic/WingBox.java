package com.cheatbreaker.client.cosmetic;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.WorldRenderer;

public class WingBox extends ModelBox {
   public TexturedQuad texturedQuad;
   public TexturedQuad texturedQuad2;

   public WingBox(ModelRenderer modelRenderer, int n, int n2, float f, float f2, float f3, int n3, int n4) {
      super(modelRenderer, n, n2, f, f2, f3, n3, 0, n4, 0.0F);
      float f4 = f + n3;
      float f5 = f3 + n4;
      this.texturedQuad = new TexturedQuad(
         new PositionTextureVertex[]{
            new PositionTextureVertex(f4, f2, f3, 0.0F, 0.0F),
            new PositionTextureVertex(f, f2, f3, 0.0F, 0.0F),
            new PositionTextureVertex(f, f2, f5, 0.0F, 0.0F),
            new PositionTextureVertex(f4, f2, f5, 0.0F, 0.0F)
         },
         n,
         n2 + n4,
         n + n3,
         n2,
         modelRenderer.textureWidth,
         modelRenderer.textureHeight
      );
      this.texturedQuad2 = new TexturedQuad((PositionTextureVertex[])this.texturedQuad.vertexPositions.clone());
      this.texturedQuad2.flipFace();
   }

   @Override
   public void render(WorldRenderer worldRenderer, float f) {
      this.texturedQuad.draw(worldRenderer, f);
      this.texturedQuad2.draw(worldRenderer, f);
   }
}
