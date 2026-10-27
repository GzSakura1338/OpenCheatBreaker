package net.optifine;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class NaturalProperties {
   public int rotation = 1;
   public boolean flip = false;
   public Map[] quadMaps = new Map[8];

   public NaturalProperties(String str) {
      if (str.equals("4")) {
         this.rotation = 4;
      } else if (str.equals("2")) {
         this.rotation = 2;
      } else if (str.equals("F")) {
         this.flip = true;
      } else if (str.equals("4F")) {
         this.rotation = 4;
         this.flip = true;
      } else if (str.equals("2F")) {
         this.rotation = 2;
         this.flip = true;
      } else {
         Config.warn("NaturalTextures: Unknown type: " + str);
      }
   }

   public boolean isValid() {
      return this.rotation != 2 && this.rotation != 4 ? this.flip : true;
   }

   public synchronized BakedQuad getQuad(BakedQuad bakedQuad, int n, boolean flag) {
      int n2 = n;
      if (flag) {
         n2 = n | 4;
      }

      if (n2 > 0 && n2 < this.quadMaps.length) {
         Map map = this.quadMaps[n2];
         if (map == null) {
            map = new IdentityHashMap(1);
            this.quadMaps[n2] = map;
         }

         BakedQuad bakedQuad2 = (BakedQuad)map.get(bakedQuad);
         if (bakedQuad2 == null) {
            bakedQuad2 = this.makeQuad(bakedQuad, n, flag);
            map.put(bakedQuad, bakedQuad2);
         }

         return bakedQuad2;
      } else {
         return bakedQuad;
      }
   }

   public BakedQuad makeQuad(BakedQuad bakedQuad, int n, boolean flag) {
      int[] vertexData = bakedQuad.getVertexData();
      int tintIndex = bakedQuad.getTintIndex();
      EnumFacing face = bakedQuad.getFace();
      TextureAtlasSprite sprite = bakedQuad.getSprite();
      if (!this.isFullSprite(bakedQuad)) {
         n = 0;
      }

      vertexData = this.transformVertexData(vertexData, n, flag);
      return new BakedQuad(vertexData, tintIndex, face, sprite);
   }

   public int[] transformVertexData(int[] ints, int n, boolean flag) {
      int[] ints2 = (int[])ints.clone();
      int n2 = 4 - n;
      if (flag) {
         n2 += 3;
      }

      n2 %= 4;
      int n3 = ints2.length / 4;

      for (int i = 0; i < 4; i++) {
         int n4 = i * n3;
         int n5 = n2 * n3;
         ints2[n5 + 4] = ints[n4 + 4];
         ints2[n5 + 4 + 1] = ints[n4 + 4 + 1];
         if (flag) {
            if (--n2 < 0) {
               n2 = 3;
            }
         } else if (++n2 > 3) {
            n2 = 0;
         }
      }

      return ints2;
   }

   public boolean isFullSprite(BakedQuad bakedQuad) {
      TextureAtlasSprite sprite = bakedQuad.getSprite();
      float minU = sprite.getMinU();
      float maxU = sprite.getMaxU();
      float f = maxU - minU;
      float f2 = f / 256.0F;
      float minV = sprite.getMinV();
      float maxV = sprite.getMaxV();
      float f3 = maxV - minV;
      float f4 = f3 / 256.0F;
      int[] vertexData = bakedQuad.getVertexData();
      int n = vertexData.length / 4;

      for (int i = 0; i < 4; i++) {
         int n2 = i * n;
         float intBitsToFloat = Float.intBitsToFloat(vertexData[n2 + 4]);
         float intBitsToFloat2 = Float.intBitsToFloat(vertexData[n2 + 4 + 1]);
         if (!this.equalsDelta(intBitsToFloat, minU, f2) && !this.equalsDelta(intBitsToFloat, maxU, f2)) {
            return false;
         }

         if (!this.equalsDelta(intBitsToFloat2, minV, f4) && !this.equalsDelta(intBitsToFloat2, maxV, f4)) {
            return false;
         }
      }

      return true;
   }

   public boolean equalsDelta(float f, float f2, float f3) {
      float abs = MathHelper.abs(f - f2);
      return abs < f3;
   }
}
