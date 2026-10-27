package net.minecraft.client.renderer.block.model;

import java.util.Arrays;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class BreakingFour extends BakedQuad {
   public TextureAtlasSprite texture;

   public BreakingFour(BakedQuad quad, TextureAtlasSprite textureIn) {
      super(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), quad.tintIndex, FaceBakery.getFacingFromVertexData(quad.getVertexData()));
      this.texture = textureIn;
      this.remapQuad();
      this.fixVertexData();
   }

   public void remapQuad() {
      for (int i = 0; i < 4; i++) {
         this.remapVert(i);
      }
   }

   public void remapVert(int vertex) {
      int n = this.vertexData.length / 4;
      int n2 = n * vertex;
      float intBitsToFloat = Float.intBitsToFloat(this.vertexData[n2]);
      float intBitsToFloat2 = Float.intBitsToFloat(this.vertexData[n2 + 1]);
      float intBitsToFloat3 = Float.intBitsToFloat(this.vertexData[n2 + 2]);
      float f = 0.0F;
      float f2 = 0.0F;
      switch (this.face) {
         case DOWN:
            f = intBitsToFloat * 16.0F;
            f2 = (1.0F - intBitsToFloat3) * 16.0F;
            break;
         case UP:
            f = intBitsToFloat * 16.0F;
            f2 = intBitsToFloat3 * 16.0F;
            break;
         case NORTH:
            f = (1.0F - intBitsToFloat) * 16.0F;
            f2 = (1.0F - intBitsToFloat2) * 16.0F;
            break;
         case SOUTH:
            f = intBitsToFloat * 16.0F;
            f2 = (1.0F - intBitsToFloat2) * 16.0F;
            break;
         case WEST:
            f = intBitsToFloat3 * 16.0F;
            f2 = (1.0F - intBitsToFloat2) * 16.0F;
            break;
         case EAST:
            f = (1.0F - intBitsToFloat3) * 16.0F;
            f2 = (1.0F - intBitsToFloat2) * 16.0F;
      }

      this.vertexData[n2 + 4] = Float.floatToRawIntBits(this.texture.getInterpolatedU(f));
      this.vertexData[n2 + 4 + 1] = Float.floatToRawIntBits(this.texture.getInterpolatedV(f2));
   }
}
