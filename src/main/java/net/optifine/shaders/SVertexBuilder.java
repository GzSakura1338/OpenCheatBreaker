package net.optifine.shaders;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL20;

public class SVertexBuilder {
   public int vertexSize;
   public int offsetNormal;
   public int offsetUV;
   public int offsetUVCenter;
   public boolean hasNormal;
   public boolean hasTangent;
   public boolean hasUV;
   public boolean hasUVCenter;
   public long[] entityData = new long[10];
   public int entityDataIndex = 0;

   public SVertexBuilder() {
      this.entityData[this.entityDataIndex] = 0L;
   }

   public static void initVertexBuilder(WorldRenderer worldRenderer) {
      worldRenderer.sVertexBuilder = new SVertexBuilder();
   }

   public void pushEntity(long l) {
      this.entityDataIndex++;
      this.entityData[this.entityDataIndex] = l;
   }

   public void popEntity() {
      this.entityData[this.entityDataIndex] = 0L;
      this.entityDataIndex--;
   }

   public static void pushEntity(IBlockState blockState, BlockPos blockPos, IBlockAccess blockAccess, WorldRenderer worldRenderer) {
      Block block = blockState.getBlock();
      int n;
      int n2;
      if (blockState instanceof BlockStateBase) {
         BlockStateBase blockStateBase = (BlockStateBase)blockState;
         n = blockStateBase.getBlockId();
         n2 = blockStateBase.getMetadata();
      } else {
         n = Block.getIdFromBlock(block);
         n2 = block.getMetaFromState(blockState);
      }

      int blockAliasId = BlockAliases.getBlockAliasId(n, n2);
      if (blockAliasId >= 0) {
         n = blockAliasId;
      }

      int renderType = block.getRenderType();
      int n3 = ((renderType & 65535) << 16) + (n & 65535);
      int n4 = n2 & 65535;
      worldRenderer.sVertexBuilder.pushEntity(((long)n4 << 32) + n3);
   }

   public static void popEntity(WorldRenderer worldRenderer) {
      worldRenderer.sVertexBuilder.popEntity();
   }

   public static boolean popEntity(boolean flag, WorldRenderer worldRenderer) {
      worldRenderer.sVertexBuilder.popEntity();
      return flag;
   }

   public static void endSetVertexFormat(WorldRenderer worldRenderer) {
      SVertexBuilder sVertexBuilder = worldRenderer.sVertexBuilder;
      VertexFormat vertexFormat = worldRenderer.getVertexFormat();
      sVertexBuilder.vertexSize = vertexFormat.getNextOffset() / 4;
      sVertexBuilder.hasNormal = vertexFormat.hasNormal();
      sVertexBuilder.hasTangent = sVertexBuilder.hasNormal;
      sVertexBuilder.hasUV = vertexFormat.hasUvOffset(0);
      sVertexBuilder.offsetNormal = sVertexBuilder.hasNormal ? vertexFormat.getNormalOffset() / 4 : 0;
      sVertexBuilder.offsetUV = sVertexBuilder.hasUV ? vertexFormat.getUvOffsetById(0) / 4 : 0;
      sVertexBuilder.offsetUVCenter = 8;
   }

   public static void beginAddVertex(WorldRenderer worldRenderer) {
      if (worldRenderer.vertexCount == 0) {
         endSetVertexFormat(worldRenderer);
      }
   }

   public static void endAddVertex(WorldRenderer worldRenderer) {
      SVertexBuilder sVertexBuilder = worldRenderer.sVertexBuilder;
      if (sVertexBuilder.vertexSize == 14) {
         if (worldRenderer.drawMode == 7 && worldRenderer.vertexCount % 4 == 0) {
            sVertexBuilder.calcNormal(worldRenderer, worldRenderer.getBufferSize() - 4 * sVertexBuilder.vertexSize);
         }

         long l = sVertexBuilder.entityData[sVertexBuilder.entityDataIndex];
         int n = worldRenderer.getBufferSize() - 14 + 12;
         worldRenderer.rawIntBuffer.put(n, (int)l);
         worldRenderer.rawIntBuffer.put(n + 1, (int)(l >> 32));
      }
   }

   public static void beginAddVertexData(WorldRenderer worldRenderer, int[] ints) {
      if (worldRenderer.vertexCount == 0) {
         endSetVertexFormat(worldRenderer);
      }

      SVertexBuilder sVertexBuilder = worldRenderer.sVertexBuilder;
      if (sVertexBuilder.vertexSize == 14) {
         long l = sVertexBuilder.entityData[sVertexBuilder.entityDataIndex];

         for (int i = 12; i + 1 < ints.length; i += 14) {
            ints[i] = (int)l;
            ints[i + 1] = (int)(l >> 32);
         }
      }
   }

   public static void beginAddVertexData(WorldRenderer worldRenderer, ByteBuffer byteBuffer) {
      if (worldRenderer.vertexCount == 0) {
         endSetVertexFormat(worldRenderer);
      }

      SVertexBuilder sVertexBuilder = worldRenderer.sVertexBuilder;
      if (sVertexBuilder.vertexSize == 14) {
         long l = sVertexBuilder.entityData[sVertexBuilder.entityDataIndex];
         int n = byteBuffer.limit() / 4;

         for (int i = 12; i + 1 < n; i += 14) {
            int n2 = (int)l;
            int n3 = (int)(l >> 32);
            byteBuffer.putInt(i * 4, n2);
            byteBuffer.putInt((i + 1) * 4, n3);
         }
      }
   }

   public static void endAddVertexData(WorldRenderer worldRenderer) {
      SVertexBuilder sVertexBuilder = worldRenderer.sVertexBuilder;
      if (sVertexBuilder.vertexSize == 14 && worldRenderer.drawMode == 7 && worldRenderer.vertexCount % 4 == 0) {
         sVertexBuilder.calcNormal(worldRenderer, worldRenderer.getBufferSize() - 4 * sVertexBuilder.vertexSize);
      }
   }

   public void calcNormal(WorldRenderer worldRenderer, int n) {
      FloatBuffer rawFloatBuffer = worldRenderer.rawFloatBuffer;
      IntBuffer rawIntBuffer = worldRenderer.rawIntBuffer;
      int bufferSize = worldRenderer.getBufferSize();
      float f = rawFloatBuffer.get(n + 0 * this.vertexSize);
      float f2 = rawFloatBuffer.get(n + 0 * this.vertexSize + 1);
      float f3 = rawFloatBuffer.get(n + 0 * this.vertexSize + 2);
      float f4 = rawFloatBuffer.get(n + 0 * this.vertexSize + this.offsetUV);
      float f5 = rawFloatBuffer.get(n + 0 * this.vertexSize + this.offsetUV + 1);
      float f6 = rawFloatBuffer.get(n + 1 * this.vertexSize);
      float f7 = rawFloatBuffer.get(n + 1 * this.vertexSize + 1);
      float f8 = rawFloatBuffer.get(n + 1 * this.vertexSize + 2);
      float f9 = rawFloatBuffer.get(n + 1 * this.vertexSize + this.offsetUV);
      float f10 = rawFloatBuffer.get(n + 1 * this.vertexSize + this.offsetUV + 1);
      float f11 = rawFloatBuffer.get(n + 2 * this.vertexSize);
      float f12 = rawFloatBuffer.get(n + 2 * this.vertexSize + 1);
      float f13 = rawFloatBuffer.get(n + 2 * this.vertexSize + 2);
      float f14 = rawFloatBuffer.get(n + 2 * this.vertexSize + this.offsetUV);
      float f15 = rawFloatBuffer.get(n + 2 * this.vertexSize + this.offsetUV + 1);
      float f16 = rawFloatBuffer.get(n + 3 * this.vertexSize);
      float f17 = rawFloatBuffer.get(n + 3 * this.vertexSize + 1);
      float f18 = rawFloatBuffer.get(n + 3 * this.vertexSize + 2);
      float f19 = rawFloatBuffer.get(n + 3 * this.vertexSize + this.offsetUV);
      float f20 = rawFloatBuffer.get(n + 3 * this.vertexSize + this.offsetUV + 1);
      float f21 = f11 - f;
      float f22 = f12 - f2;
      float f23 = f13 - f3;
      float f24 = f16 - f6;
      float f25 = f17 - f7;
      float f26 = f18 - f8;
      float f27 = f22 * f26 - f25 * f23;
      float f28 = f23 * f24 - f26 * f21;
      float f29 = f21 * f25 - f24 * f22;
      float f30 = f27 * f27 + f28 * f28 + f29 * f29;
      float f31 = f30 != 0.0 ? (float)(1.0 / Math.sqrt(f30)) : 1.0F;
      f27 *= f31;
      f28 *= f31;
      f29 *= f31;
      f21 = f6 - f;
      f22 = f7 - f2;
      f23 = f8 - f3;
      float f32 = f9 - f4;
      float f33 = f10 - f5;
      f24 = f11 - f;
      f25 = f12 - f2;
      f26 = f13 - f3;
      float f34 = f14 - f4;
      float f35 = f15 - f5;
      float f36 = f32 * f35 - f34 * f33;
      float f37 = f36 != 0.0F ? 1.0F / f36 : 1.0F;
      float f38 = (f35 * f21 - f33 * f24) * f37;
      float f39 = (f35 * f22 - f33 * f25) * f37;
      float f40 = (f35 * f23 - f33 * f26) * f37;
      float f41 = (f32 * f24 - f34 * f21) * f37;
      float f42 = (f32 * f25 - f34 * f22) * f37;
      float f43 = (f32 * f26 - f34 * f23) * f37;
      f30 = f38 * f38 + f39 * f39 + f40 * f40;
      f31 = f30 != 0.0 ? (float)(1.0 / Math.sqrt(f30)) : 1.0F;
      f38 *= f31;
      f39 *= f31;
      f40 *= f31;
      f30 = f41 * f41 + f42 * f42 + f43 * f43;
      f31 = f30 != 0.0 ? (float)(1.0 / Math.sqrt(f30)) : 1.0F;
      f41 *= f31;
      f42 *= f31;
      f43 *= f31;
      float f44 = f29 * f39 - f28 * f40;
      float f45 = f27 * f40 - f29 * f38;
      float f46 = f28 * f38 - f27 * f39;
      float f47 = f41 * f44 + f42 * f45 + f43 * f46 < 0.0F ? -1.0F : 1.0F;
      int n2 = (int)(f27 * 127.0F) & 0xFF;
      int n3 = (int)(f28 * 127.0F) & 0xFF;
      int n4 = (int)(f29 * 127.0F) & 0xFF;
      int n5 = (n4 << 16) + (n3 << 8) + n2;
      rawIntBuffer.put(n + 0 * this.vertexSize + this.offsetNormal, n5);
      rawIntBuffer.put(n + 1 * this.vertexSize + this.offsetNormal, n5);
      rawIntBuffer.put(n + 2 * this.vertexSize + this.offsetNormal, n5);
      rawIntBuffer.put(n + 3 * this.vertexSize + this.offsetNormal, n5);
      int n6 = ((int)(f38 * 32767.0F) & 65535) + (((int)(f39 * 32767.0F) & 65535) << 16);
      int n7 = ((int)(f40 * 32767.0F) & 65535) + (((int)(f47 * 32767.0F) & 65535) << 16);
      rawIntBuffer.put(n + 0 * this.vertexSize + 10, n6);
      rawIntBuffer.put(n + 0 * this.vertexSize + 10 + 1, n7);
      rawIntBuffer.put(n + 1 * this.vertexSize + 10, n6);
      rawIntBuffer.put(n + 1 * this.vertexSize + 10 + 1, n7);
      rawIntBuffer.put(n + 2 * this.vertexSize + 10, n6);
      rawIntBuffer.put(n + 2 * this.vertexSize + 10 + 1, n7);
      rawIntBuffer.put(n + 3 * this.vertexSize + 10, n6);
      rawIntBuffer.put(n + 3 * this.vertexSize + 10 + 1, n7);
      float f48 = (f4 + f9 + f14 + f19) / 4.0F;
      float f49 = (f5 + f10 + f15 + f20) / 4.0F;
      rawFloatBuffer.put(n + 0 * this.vertexSize + 8, f48);
      rawFloatBuffer.put(n + 0 * this.vertexSize + 8 + 1, f49);
      rawFloatBuffer.put(n + 1 * this.vertexSize + 8, f48);
      rawFloatBuffer.put(n + 1 * this.vertexSize + 8 + 1, f49);
      rawFloatBuffer.put(n + 2 * this.vertexSize + 8, f48);
      rawFloatBuffer.put(n + 2 * this.vertexSize + 8 + 1, f49);
      rawFloatBuffer.put(n + 3 * this.vertexSize + 8, f48);
      rawFloatBuffer.put(n + 3 * this.vertexSize + 8 + 1, f49);
   }

   public static void calcNormalChunkLayer(WorldRenderer worldRenderer) {
      if (worldRenderer.getVertexFormat().hasNormal() && worldRenderer.drawMode == 7 && worldRenderer.vertexCount % 4 == 0) {
         SVertexBuilder sVertexBuilder = worldRenderer.sVertexBuilder;
         endSetVertexFormat(worldRenderer);
         int n = worldRenderer.vertexCount * sVertexBuilder.vertexSize;

         for (int i = 0; i < n; i += sVertexBuilder.vertexSize * 4) {
            sVertexBuilder.calcNormal(worldRenderer, i);
         }
      }
   }

   public static void drawArrays(int n, int n2, int n3, WorldRenderer worldRenderer) {
      if (n3 != 0) {
         VertexFormat vertexFormat = worldRenderer.getVertexFormat();
         int nextOffset = vertexFormat.getNextOffset();
         if (nextOffset == 56) {
            ByteBuffer byteBuffer = worldRenderer.getByteBuffer();
            ((Buffer)byteBuffer).position(32);
            GL20.glVertexAttribPointer(Shaders.midTexCoordAttrib, 2, 5126, false, nextOffset, byteBuffer);
            ((Buffer)byteBuffer).position(40);
            GL20.glVertexAttribPointer(Shaders.tangentAttrib, 4, 5122, false, nextOffset, byteBuffer);
            ((Buffer)byteBuffer).position(48);
            GL20.glVertexAttribPointer(Shaders.entityAttrib, 3, 5122, false, nextOffset, byteBuffer);
            ((Buffer)byteBuffer).position(0);
            GL20.glEnableVertexAttribArray(Shaders.midTexCoordAttrib);
            GL20.glEnableVertexAttribArray(Shaders.tangentAttrib);
            GL20.glEnableVertexAttribArray(Shaders.entityAttrib);
            GlStateManager.glDrawArrays(n, n2, n3);
            GL20.glDisableVertexAttribArray(Shaders.midTexCoordAttrib);
            GL20.glDisableVertexAttribArray(Shaders.tangentAttrib);
            GL20.glDisableVertexAttribArray(Shaders.entityAttrib);
         } else {
            GlStateManager.glDrawArrays(n, n2, n3);
         }
      }
   }
}
