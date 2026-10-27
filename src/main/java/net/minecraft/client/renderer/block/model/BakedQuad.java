package net.minecraft.client.renderer.block.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.IVertexProducer;
import net.optifine.Config;
import net.optifine.model.QuadBounds;
import net.optifine.reflect.Reflector;

public class BakedQuad implements IVertexProducer {
   public int[] vertexDataSingle = null;
   public int[] vertexData;
   public int tintIndex;
   public EnumFacing face;
   public TextureAtlasSprite sprite;
   public QuadBounds quadBounds;
   public boolean quadEmissiveChecked;
   public BakedQuad quadEmissive;

   public BakedQuad(int[] ints, int n, EnumFacing enumFacing, TextureAtlasSprite textureAtlasSprite) {
      this.vertexData = ints;
      this.tintIndex = n;
      this.face = enumFacing;
      this.sprite = textureAtlasSprite;
      this.fixVertexData();
   }

   public BakedQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn) {
      this.vertexData = vertexDataIn;
      this.tintIndex = tintIndexIn;
      this.face = faceIn;
      this.fixVertexData();
   }

   public TextureAtlasSprite getSprite() {
      if (this.sprite == null) {
         this.sprite = getSpriteByUv(this.getVertexData());
      }

      return this.sprite;
   }

   public int[] getVertexData() {
      this.fixVertexData();
      return this.vertexData;
   }

   public boolean hasTintIndex() {
      return this.tintIndex != -1;
   }

   public int getTintIndex() {
      return this.tintIndex;
   }

   public EnumFacing getFace() {
      if (this.face == null) {
         this.face = FaceBakery.getFacingFromVertexData(this.getVertexData());
      }

      return this.face;
   }

   public int[] getVertexDataSingle() {
      if (this.vertexDataSingle == null) {
         this.vertexDataSingle = makeVertexDataSingle(this.getVertexData(), this.getSprite());
      }

      return this.vertexDataSingle;
   }

   public static int[] makeVertexDataSingle(int[] ints, TextureAtlasSprite textureAtlasSprite) {
      int[] ints2 = (int[])ints.clone();
      int n = ints2.length / 4;

      for (int i = 0; i < 4; i++) {
         int n2 = i * n;
         float intBitsToFloat = Float.intBitsToFloat(ints2[n2 + 4]);
         float intBitsToFloat2 = Float.intBitsToFloat(ints2[n2 + 4 + 1]);
         float toSingleU = textureAtlasSprite.toSingleU(intBitsToFloat);
         float toSingleV = textureAtlasSprite.toSingleV(intBitsToFloat2);
         ints2[n2 + 4] = Float.floatToRawIntBits(toSingleU);
         ints2[n2 + 4 + 1] = Float.floatToRawIntBits(toSingleV);
      }

      return ints2;
   }

   @Override
   public void pipe(IVertexConsumer vertexConsumer) {
      Reflector.callVoid(Reflector.LightUtil_putBakedQuad, vertexConsumer, this);
   }

   public static TextureAtlasSprite getSpriteByUv(int[] ints) {
      float f = 1.0F;
      float f2 = 1.0F;
      float f3 = 0.0F;
      float f4 = 0.0F;
      int n = ints.length / 4;

      for (int i = 0; i < 4; i++) {
         int n2 = i * n;
         float intBitsToFloat = Float.intBitsToFloat(ints[n2 + 4]);
         float intBitsToFloat2 = Float.intBitsToFloat(ints[n2 + 4 + 1]);
         f = Math.min(f, intBitsToFloat);
         f2 = Math.min(f2, intBitsToFloat2);
         f3 = Math.max(f3, intBitsToFloat);
         f4 = Math.max(f4, intBitsToFloat2);
      }

      float f5 = (f + f3) / 2.0F;
      float f6 = (f2 + f4) / 2.0F;
      return Minecraft.getMinecraft().getTextureMapBlocks().getIconByUV(f5, f6);
   }

   public void fixVertexData() {
      if (Config.isShaders()) {
         if (this.vertexData.length == 28) {
            this.vertexData = expandVertexData(this.vertexData);
         }
      } else if (this.vertexData.length == 56) {
         this.vertexData = compactVertexData(this.vertexData);
      }
   }

   public static int[] expandVertexData(int[] ints) {
      int n = ints.length / 4;
      int n2 = n * 2;
      int[] ints2 = new int[n2 * 4];

      for (int i = 0; i < 4; i++) {
         System.arraycopy(ints, i * n, ints2, i * n2, n);
      }

      return ints2;
   }

   public static int[] compactVertexData(int[] ints) {
      int n = ints.length / 4;
      int n2 = n / 2;
      int[] ints2 = new int[n2 * 4];

      for (int i = 0; i < 4; i++) {
         System.arraycopy(ints, i * n, ints2, i * n2, n2);
      }

      return ints2;
   }

   public QuadBounds getQuadBounds() {
      if (this.quadBounds == null) {
         this.quadBounds = new QuadBounds(this.getVertexData());
      }

      return this.quadBounds;
   }

   public float getMidX() {
      QuadBounds quadBounds = this.getQuadBounds();
      return (quadBounds.getMaxX() + quadBounds.getMinX()) / 2.0F;
   }

   public double getMidY() {
      QuadBounds quadBounds = this.getQuadBounds();
      return (quadBounds.getMaxY() + quadBounds.getMinY()) / 2.0F;
   }

   public double getMidZ() {
      QuadBounds quadBounds = this.getQuadBounds();
      return (quadBounds.getMaxZ() + quadBounds.getMinZ()) / 2.0F;
   }

   public boolean isFaceQuad() {
      QuadBounds quadBounds = this.getQuadBounds();
      return quadBounds.isFaceQuad(this.face);
   }

   public boolean isFullQuad() {
      QuadBounds quadBounds = this.getQuadBounds();
      return quadBounds.isFullQuad(this.face);
   }

   public boolean isFullFaceQuad() {
      return this.isFullQuad() && this.isFaceQuad();
   }

   public BakedQuad getQuadEmissive() {
      if (this.quadEmissiveChecked) {
         return this.quadEmissive;
      }

      if (this.quadEmissive == null && this.sprite != null && this.sprite.spriteEmissive != null) {
         this.quadEmissive = new BreakingFour(this, this.sprite.spriteEmissive);
      }

      this.quadEmissiveChecked = true;
      return this.quadEmissive;
   }

   @Override
   public String toString() {
      return "vertex: " + this.vertexData.length / 7 + ", tint: " + this.tintIndex + ", facing: " + this.face + ", sprite: " + this.sprite;
   }
}
