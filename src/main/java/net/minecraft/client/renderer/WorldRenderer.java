package net.minecraft.client.renderer;

import com.google.common.primitives.Floats;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.optifine.Config;
import net.optifine.SmartAnimations;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.SVertexBuilder;
import net.optifine.util.TextureUtils;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.GL11;

public class WorldRenderer {
   public EnumWorldBlockLayer blockLayer = null;
   public boolean[] drawnIcons = new boolean[256];
   public TextureAtlasSprite[] quadSprites = null;
   public TextureAtlasSprite[] quadSpritesPrev = null;
   public int vertexCount;
   public VertexFormatElement vertexFormatElement;
   public int vertexFormatIndex;
   public boolean noColor;
   public int drawMode;
   public double xOffset;
   public double yOffset;
   public double zOffset;
   public VertexFormat vertexFormat;
   public boolean isDrawing;
   public TextureAtlasSprite quadSprite = null;
   public RenderEnv renderEnv = null;
   public BitSet animatedSprites = null;
   public BitSet animatedSpritesCached = new BitSet();
   public boolean modeTriangles = false;
   public SVertexBuilder sVertexBuilder;
   public ByteBuffer byteBuffer;
   public IntBuffer rawIntBuffer;
   public ShortBuffer rawShortBuffer;
   public FloatBuffer rawFloatBuffer;
   public ByteBuffer byteBufferTriangles;

   public WorldRenderer(int bufferSizeIn) {
      this.byteBuffer = GLAllocation.createDirectByteBuffer(bufferSizeIn * 4);
      this.rawIntBuffer = this.byteBuffer.asIntBuffer();
      this.rawShortBuffer = this.byteBuffer.asShortBuffer();
      this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
      SVertexBuilder.initVertexBuilder(this);
   }

   public void growBuffer(int n) {
      if (n > this.rawIntBuffer.remaining()) {
         int capacity = this.byteBuffer.capacity();
         int n2 = capacity % 2097152;
         int n3 = n2 + (((this.rawIntBuffer.position() + n) * 4 - n2) / 2097152 + 1) * 2097152;
         LogManager.getLogger().warn("Needed to grow BufferBuilder buffer: Old size " + capacity + " bytes, new size " + n3 + " bytes.");
         int position = this.rawIntBuffer.position();
         ByteBuffer createDirectByteBuffer = GLAllocation.createDirectByteBuffer(n3);
         ((Buffer)this.byteBuffer).position(0);
         createDirectByteBuffer.put(this.byteBuffer);
         ((Buffer)createDirectByteBuffer).rewind();
         this.byteBuffer = createDirectByteBuffer;
         this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
         this.rawIntBuffer = this.byteBuffer.asIntBuffer();
         ((Buffer)this.rawIntBuffer).position(position);
         this.rawShortBuffer = this.byteBuffer.asShortBuffer();
         ((Buffer)this.rawShortBuffer).position(position << 1);
         if (this.quadSprites != null) {
            TextureAtlasSprite[] quadSprites = this.quadSprites;
            int bufferQuadSize = this.getBufferQuadSize();
            this.quadSprites = new TextureAtlasSprite[bufferQuadSize];
            System.arraycopy(quadSprites, 0, this.quadSprites, 0, Math.min(quadSprites.length, this.quadSprites.length));
            this.quadSpritesPrev = null;
         }
      }
   }

   public void sortVertexData(float f, float f2, float f3) {
      int n = this.vertexCount / 4;
      final float[] floats = new float[n];

      for (int i = 0; i < n; i++) {
         floats[i] = getDistanceSq(
            this.rawFloatBuffer,
            (float)(f + this.xOffset),
            (float)(f2 + this.yOffset),
            (float)(f3 + this.zOffset),
            this.vertexFormat.getIntegerSize(),
            i * this.vertexFormat.getNextOffset()
         );
      }

      Integer[] integers = new Integer[n];

      for (int i = 0; i < integers.length; i++) {
         integers[i] = i;
      }

      Arrays.sort(integers, new Comparator<Integer>() {
         public int compare(Integer integer, Integer integer2) {
            return Floats.compare(floats[integer2], floats[integer]);
         }
      });
      BitSet bitSet = new BitSet();
      int nextOffset = this.vertexFormat.getNextOffset();
      int[] ints = new int[nextOffset];

      for (int i = 0; (i = bitSet.nextClearBit(i)) < integers.length; i++) {
         int n2 = integers[i];
         if (n2 != i) {
            ((Buffer)this.rawIntBuffer).limit(n2 * nextOffset + nextOffset);
            ((Buffer)this.rawIntBuffer).position(n2 * nextOffset);
            this.rawIntBuffer.get(ints);
            int n3 = n2;

            for (int j = integers[n2]; n3 != i; j = integers[j]) {
               ((Buffer)this.rawIntBuffer).limit(j * nextOffset + nextOffset);
               ((Buffer)this.rawIntBuffer).position(j * nextOffset);
               IntBuffer slice = this.rawIntBuffer.slice();
               ((Buffer)this.rawIntBuffer).limit(n3 * nextOffset + nextOffset);
               ((Buffer)this.rawIntBuffer).position(n3 * nextOffset);
               this.rawIntBuffer.put(slice);
               bitSet.set(n3);
               n3 = j;
            }

            ((Buffer)this.rawIntBuffer).limit(i * nextOffset + nextOffset);
            ((Buffer)this.rawIntBuffer).position(i * nextOffset);
            this.rawIntBuffer.put(ints);
         }

         bitSet.set(i);
      }

      ((Buffer)this.rawIntBuffer).limit(this.rawIntBuffer.capacity());
      ((Buffer)this.rawIntBuffer).position(this.getBufferSize());
      if (this.quadSprites != null) {
         TextureAtlasSprite[] textureAtlasSprites = new TextureAtlasSprite[this.vertexCount / 4];
         int n2 = this.vertexFormat.getNextOffset() / 4 * 4;

         for (int i = 0; i < integers.length; i++) {
            int n3 = integers[i];
            textureAtlasSprites[i] = this.quadSprites[n3];
         }

         System.arraycopy(textureAtlasSprites, 0, this.quadSprites, 0, textureAtlasSprites.length);
      }
   }

   public WorldRenderer.State getVertexState() {
      ((Buffer)this.rawIntBuffer).rewind();
      int bufferSize = this.getBufferSize();
      ((Buffer)this.rawIntBuffer).limit(bufferSize);
      int[] ints = new int[bufferSize];
      this.rawIntBuffer.get(ints);
      ((Buffer)this.rawIntBuffer).limit(this.rawIntBuffer.capacity());
      ((Buffer)this.rawIntBuffer).position(bufferSize);
      TextureAtlasSprite[] textureAtlasSprites = null;
      if (this.quadSprites != null) {
         int n = this.vertexCount / 4;
         textureAtlasSprites = new TextureAtlasSprite[n];
         System.arraycopy(this.quadSprites, 0, textureAtlasSprites, 0, n);
      }

      return new WorldRenderer.State(ints, new VertexFormat(this.vertexFormat), textureAtlasSprites);
   }

   public int getBufferSize() {
      return this.vertexCount * this.vertexFormat.getIntegerSize();
   }

   public static float getDistanceSq(FloatBuffer floatBuffer, float f, float f2, float f3, int n, int n2) {
      float f4 = floatBuffer.get(n2 + n * 0 + 0);
      float f5 = floatBuffer.get(n2 + n * 0 + 1);
      float f6 = floatBuffer.get(n2 + n * 0 + 2);
      float f7 = floatBuffer.get(n2 + n * 1 + 0);
      float f8 = floatBuffer.get(n2 + n * 1 + 1);
      float f9 = floatBuffer.get(n2 + n * 1 + 2);
      float f10 = floatBuffer.get(n2 + n * 2 + 0);
      float f11 = floatBuffer.get(n2 + n * 2 + 1);
      float f12 = floatBuffer.get(n2 + n * 2 + 2);
      float f13 = floatBuffer.get(n2 + n * 3 + 0);
      float f14 = floatBuffer.get(n2 + n * 3 + 1);
      float f15 = floatBuffer.get(n2 + n * 3 + 2);
      float f16 = (f4 + f7 + f10 + f13) * 0.25F - f;
      float f17 = (f5 + f8 + f11 + f14) * 0.25F - f2;
      float f18 = (f6 + f9 + f12 + f15) * 0.25F - f3;
      return f16 * f16 + f17 * f17 + f18 * f18;
   }

   public void setVertexState(WorldRenderer.State state) {
      ((Buffer)this.rawIntBuffer).clear();
      this.growBuffer(state.getRawBuffer().length);
      this.rawIntBuffer.put(state.getRawBuffer());
      this.vertexCount = state.getVertexCount();
      this.vertexFormat = new VertexFormat(state.getVertexFormat());
      if (state.stateQuadSprites != null) {
         if (this.quadSprites == null) {
            this.quadSprites = this.quadSpritesPrev;
         }

         if (this.quadSprites == null || this.quadSprites.length < this.getBufferQuadSize()) {
            this.quadSprites = new TextureAtlasSprite[this.getBufferQuadSize()];
         }

         TextureAtlasSprite[] stateQuadSprites = state.stateQuadSprites;
         System.arraycopy(stateQuadSprites, 0, this.quadSprites, 0, stateQuadSprites.length);
      } else {
         if (this.quadSprites != null) {
            this.quadSpritesPrev = this.quadSprites;
         }

         this.quadSprites = null;
      }
   }

   public void reset() {
      this.vertexCount = 0;
      this.vertexFormatElement = null;
      this.vertexFormatIndex = 0;
      this.quadSprite = null;
      if (SmartAnimations.isActive()) {
         if (this.animatedSprites == null) {
            this.animatedSprites = this.animatedSpritesCached;
         }

         this.animatedSprites.clear();
      } else if (this.animatedSprites != null) {
         this.animatedSprites = null;
      }

      this.modeTriangles = false;
   }

   public void begin(int glMode, VertexFormat format) {
      if (this.isDrawing) {
         throw new IllegalStateException("Already building!");
      }

      this.isDrawing = true;
      this.reset();
      this.drawMode = glMode;
      this.vertexFormat = format;
      this.vertexFormatElement = format.getElement(this.vertexFormatIndex);
      this.noColor = false;
      ((Buffer)this.byteBuffer).limit(this.byteBuffer.capacity());
      if (Config.isShaders()) {
         SVertexBuilder.endSetVertexFormat(this);
      }

      if (Config.isMultiTexture()) {
         if (this.blockLayer != null) {
            if (this.quadSprites == null) {
               this.quadSprites = this.quadSpritesPrev;
            }

            if (this.quadSprites == null || this.quadSprites.length < this.getBufferQuadSize()) {
               this.quadSprites = new TextureAtlasSprite[this.getBufferQuadSize()];
            }
         }
      } else {
         if (this.quadSprites != null) {
            this.quadSpritesPrev = this.quadSprites;
         }

         this.quadSprites = null;
      }
   }

   public WorldRenderer tex(double u, double v) {
      if (this.quadSprite != null && this.quadSprites != null) {
         u = this.quadSprite.toSingleU((float)u);
         v = this.quadSprite.toSingleV((float)v);
         this.quadSprites[this.vertexCount / 4] = this.quadSprite;
      }

      int n = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(n, (float)u);
            this.byteBuffer.putFloat(n + 4, (float)v);
            break;
         case UINT:
         case INT:
            this.byteBuffer.putInt(n, (int)u);
            this.byteBuffer.putInt(n + 4, (int)v);
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(n, (short)v);
            this.byteBuffer.putShort(n + 2, (short)u);
            break;
         case UBYTE:
         case BYTE:
            this.byteBuffer.put(n, (byte)v);
            this.byteBuffer.put(n + 1, (byte)u);
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public WorldRenderer lightmap(int n, int n2) {
      int n3 = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(n3, n);
            this.byteBuffer.putFloat(n3 + 4, n2);
            break;
         case UINT:
         case INT:
            this.byteBuffer.putInt(n3, n);
            this.byteBuffer.putInt(n3 + 4, n2);
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(n3, (short)n2);
            this.byteBuffer.putShort(n3 + 2, (short)n);
            break;
         case UBYTE:
         case BYTE:
            this.byteBuffer.put(n3, (byte)n2);
            this.byteBuffer.put(n3 + 1, (byte)n);
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public void putBrightness4(int n, int n2, int n3, int n4) {
      int n5 = (this.vertexCount - 4) * this.vertexFormat.getIntegerSize() + this.vertexFormat.getUvOffsetById(1) / 4;
      int n6 = this.vertexFormat.getNextOffset() >> 2;
      this.rawIntBuffer.put(n5, n);
      this.rawIntBuffer.put(n5 + n6, n2);
      this.rawIntBuffer.put(n5 + n6 * 2, n3);
      this.rawIntBuffer.put(n5 + n6 * 3, n4);
   }

   public void putPosition(double x, double y, double z) {
      int integerSize = this.vertexFormat.getIntegerSize();
      int n = (this.vertexCount - 4) * integerSize;

      for (int i = 0; i < 4; i++) {
         int n2 = n + i * integerSize;
         int n3 = n2 + 1;
         int n4 = n3 + 1;
         this.rawIntBuffer.put(n2, Float.floatToRawIntBits((float)(x + this.xOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(n2))));
         this.rawIntBuffer.put(n3, Float.floatToRawIntBits((float)(y + this.yOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(n3))));
         this.rawIntBuffer.put(n4, Float.floatToRawIntBits((float)(z + this.zOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(n4))));
      }
   }

   public int getColorIndex(int n) {
      return ((this.vertexCount - n) * this.vertexFormat.getNextOffset() + this.vertexFormat.getColorOffset()) / 4;
   }

   public void putColorMultiplier(float red, float green, float blue, int n) {
      int colorIndex = this.getColorIndex(n);
      int n2 = -1;
      if (!this.noColor) {
         n2 = this.rawIntBuffer.get(colorIndex);
         if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            int n3 = (int)((n2 & 0xFF) * red);
            int n4 = (int)((n2 >> 8 & 0xFF) * green);
            int n5 = (int)((n2 >> 16 & 0xFF) * blue);
            n2 &= -16777216;
            n2 = n2 | n5 << 16 | n4 << 8 | n3;
         } else {
            int n3 = (int)((n2 >> 24 & 0xFF) * red);
            int n4 = (int)((n2 >> 16 & 0xFF) * green);
            int n5 = (int)((n2 >> 8 & 0xFF) * blue);
            n2 &= 255;
            n2 = n2 | n3 << 24 | n4 << 16 | n5 << 8;
         }
      }

      this.rawIntBuffer.put(colorIndex, n2);
   }

   public void putColor(int argb, int n) {
      int colorIndex = this.getColorIndex(n);
      int n2 = argb >> 16 & 0xFF;
      int n3 = argb >> 8 & 0xFF;
      int n4 = argb & 0xFF;
      int n5 = argb >> 24 & 0xFF;
      this.putColorRGBA(colorIndex, n2, n3, n4, n5);
   }

   public void putColorRGB_F(float red, float green, float blue, int n) {
      int colorIndex = this.getColorIndex(n);
      int n2 = MathHelper.clamp_int((int)(red * 255.0F), 0, 255);
      int n3 = MathHelper.clamp_int((int)(green * 255.0F), 0, 255);
      int n4 = MathHelper.clamp_int((int)(blue * 255.0F), 0, 255);
      this.putColorRGBA(colorIndex, n2, n3, n4, 255);
   }

   public void putColorRGBA(int index, int red, int n, int n2, int n3) {
      if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
         this.rawIntBuffer.put(index, n3 << 24 | n2 << 16 | n << 8 | red);
      } else {
         this.rawIntBuffer.put(index, red << 24 | n << 16 | n2 << 8 | n3);
      }
   }

   public void noColor() {
      this.noColor = true;
   }

   public WorldRenderer color(float red, float green, float blue, float alpha) {
      return this.color((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
   }

   public WorldRenderer color(int red, int green, int blue, int alpha) {
      if (this.noColor) {
         return this;
      }

      int n = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(n, red / 255.0F);
            this.byteBuffer.putFloat(n + 4, green / 255.0F);
            this.byteBuffer.putFloat(n + 8, blue / 255.0F);
            this.byteBuffer.putFloat(n + 12, alpha / 255.0F);
            break;
         case UINT:
         case INT:
            this.byteBuffer.putFloat(n, red);
            this.byteBuffer.putFloat(n + 4, green);
            this.byteBuffer.putFloat(n + 8, blue);
            this.byteBuffer.putFloat(n + 12, alpha);
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(n, (short)red);
            this.byteBuffer.putShort(n + 2, (short)green);
            this.byteBuffer.putShort(n + 4, (short)blue);
            this.byteBuffer.putShort(n + 6, (short)alpha);
            break;
         case UBYTE:
         case BYTE:
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
               this.byteBuffer.put(n, (byte)red);
               this.byteBuffer.put(n + 1, (byte)green);
               this.byteBuffer.put(n + 2, (byte)blue);
               this.byteBuffer.put(n + 3, (byte)alpha);
            } else {
               this.byteBuffer.put(n, (byte)alpha);
               this.byteBuffer.put(n + 1, (byte)blue);
               this.byteBuffer.put(n + 2, (byte)green);
               this.byteBuffer.put(n + 3, (byte)red);
            }
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public void addVertexData(int[] vertexData) {
      if (Config.isShaders()) {
         SVertexBuilder.beginAddVertexData(this, vertexData);
      }

      this.growBuffer(vertexData.length);
      ((Buffer)this.rawIntBuffer).position(this.getBufferSize());
      this.rawIntBuffer.put(vertexData);
      this.vertexCount = this.vertexCount + vertexData.length / this.vertexFormat.getIntegerSize();
      if (Config.isShaders()) {
         SVertexBuilder.endAddVertexData(this);
      }
   }

   public void endVertex() {
      this.vertexCount++;
      this.growBuffer(this.vertexFormat.getIntegerSize());
      this.vertexFormatIndex = 0;
      this.vertexFormatElement = this.vertexFormat.getElement(this.vertexFormatIndex);
      if (Config.isShaders()) {
         SVertexBuilder.endAddVertex(this);
      }
   }

   public WorldRenderer pos(double x, double y, double z) {
      if (Config.isShaders()) {
         SVertexBuilder.beginAddVertex(this);
      }

      int n = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(n, (float)(x + this.xOffset));
            this.byteBuffer.putFloat(n + 4, (float)(y + this.yOffset));
            this.byteBuffer.putFloat(n + 8, (float)(z + this.zOffset));
            break;
         case UINT:
         case INT:
            this.byteBuffer.putInt(n, Float.floatToRawIntBits((float)(x + this.xOffset)));
            this.byteBuffer.putInt(n + 4, Float.floatToRawIntBits((float)(y + this.yOffset)));
            this.byteBuffer.putInt(n + 8, Float.floatToRawIntBits((float)(z + this.zOffset)));
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(n, (short)(x + this.xOffset));
            this.byteBuffer.putShort(n + 2, (short)(y + this.yOffset));
            this.byteBuffer.putShort(n + 4, (short)(z + this.zOffset));
            break;
         case UBYTE:
         case BYTE:
            this.byteBuffer.put(n, (byte)(x + this.xOffset));
            this.byteBuffer.put(n + 1, (byte)(y + this.yOffset));
            this.byteBuffer.put(n + 2, (byte)(z + this.zOffset));
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public void putNormal(float x, float y, float z) {
      int n = (byte)(x * 127.0F) & 255;
      int n2 = (byte)(y * 127.0F) & 255;
      int n3 = (byte)(z * 127.0F) & 255;
      int n4 = n | n2 << 8 | n3 << 16;
      int n5 = this.vertexFormat.getNextOffset() >> 2;
      int n6 = (this.vertexCount - 4) * n5 + this.vertexFormat.getNormalOffset() / 4;
      this.rawIntBuffer.put(n6, n4);
      this.rawIntBuffer.put(n6 + n5, n4);
      this.rawIntBuffer.put(n6 + n5 * 2, n4);
      this.rawIntBuffer.put(n6 + n5 * 3, n4);
   }

   public void nextVertexFormatIndex() {
      this.vertexFormatIndex++;
      this.vertexFormatIndex = this.vertexFormatIndex % this.vertexFormat.getElementCount();
      this.vertexFormatElement = this.vertexFormat.getElement(this.vertexFormatIndex);
      if (this.vertexFormatElement.getUsage() == EnumUsage.PADDING) {
         this.nextVertexFormatIndex();
      }
   }

   public WorldRenderer normal(float f, float f2, float f3) {
      int n = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(n, f);
            this.byteBuffer.putFloat(n + 4, f2);
            this.byteBuffer.putFloat(n + 8, f3);
            break;
         case UINT:
         case INT:
            this.byteBuffer.putInt(n, (int)f);
            this.byteBuffer.putInt(n + 4, (int)f2);
            this.byteBuffer.putInt(n + 8, (int)f3);
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(n, (short)((int)(f * 32767.0F) & 65535));
            this.byteBuffer.putShort(n + 2, (short)((int)(f2 * 32767.0F) & 65535));
            this.byteBuffer.putShort(n + 4, (short)((int)(f3 * 32767.0F) & 65535));
            break;
         case UBYTE:
         case BYTE:
            this.byteBuffer.put(n, (byte)((int)(f * 127.0F) & 0xFF));
            this.byteBuffer.put(n + 1, (byte)((int)(f2 * 127.0F) & 0xFF));
            this.byteBuffer.put(n + 2, (byte)((int)(f3 * 127.0F) & 0xFF));
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public void setTranslation(double x, double y, double z) {
      this.xOffset = x;
      this.yOffset = y;
      this.zOffset = z;
   }

   public void finishDrawing() {
      if (!this.isDrawing) {
         throw new IllegalStateException("Not building!");
      }

      this.isDrawing = false;
      ((Buffer)this.byteBuffer).position(0);
      ((Buffer)this.byteBuffer).limit(this.getBufferSize() * 4);
   }

   public ByteBuffer getByteBuffer() {
      return this.modeTriangles ? this.byteBufferTriangles : this.byteBuffer;
   }

   public VertexFormat getVertexFormat() {
      return this.vertexFormat;
   }

   public int getVertexCount() {
      return this.modeTriangles ? this.vertexCount / 4 * 6 : this.vertexCount;
   }

   public int getDrawMode() {
      return this.modeTriangles ? 4 : this.drawMode;
   }

   public void putColor4(int argb) {
      for (int i = 0; i < 4; i++) {
         this.putColor(argb, i + 1);
      }
   }

   public void putColorRGB_F4(float red, float green, float blue) {
      for (int i = 0; i < 4; i++) {
         this.putColorRGB_F(red, green, blue, i + 1);
      }
   }

   public void putSprite(TextureAtlasSprite textureAtlasSprite) {
      if (this.animatedSprites != null && textureAtlasSprite != null && textureAtlasSprite.getAnimationIndex() >= 0) {
         this.animatedSprites.set(textureAtlasSprite.getAnimationIndex());
      }

      if (this.quadSprites != null) {
         int n = this.vertexCount / 4;
         this.quadSprites[n - 1] = textureAtlasSprite;
      }
   }

   public void setSprite(TextureAtlasSprite textureAtlasSprite) {
      if (this.animatedSprites != null && textureAtlasSprite != null && textureAtlasSprite.getAnimationIndex() >= 0) {
         this.animatedSprites.set(textureAtlasSprite.getAnimationIndex());
      }

      if (this.quadSprites != null) {
         this.quadSprite = textureAtlasSprite;
      }
   }

   public boolean isMultiTexture() {
      return this.quadSprites != null;
   }

   public void drawMultiTexture() {
      if (this.quadSprites != null) {
         int countRegisteredSprites = Config.getMinecraft().getTextureMapBlocks().getCountRegisteredSprites();
         if (this.drawnIcons.length <= countRegisteredSprites) {
            this.drawnIcons = new boolean[countRegisteredSprites + 1];
         }

         Arrays.fill(this.drawnIcons, false);
         int n = 0;
         int n2 = -1;
         int n3 = this.vertexCount / 4;

         for (int i = 0; i < n3; i++) {
            TextureAtlasSprite textureAtlasSprite = this.quadSprites[i];
            if (textureAtlasSprite != null) {
               int indexInMap = textureAtlasSprite.getIndexInMap();
               if (!this.drawnIcons[indexInMap]) {
                  if (textureAtlasSprite == TextureUtils.iconGrassSideOverlay) {
                     if (n2 < 0) {
                        n2 = i;
                     }
                  } else {
                     i = this.drawForIcon(textureAtlasSprite, i) - 1;
                     n++;
                     if (this.blockLayer != EnumWorldBlockLayer.TRANSLUCENT) {
                        this.drawnIcons[indexInMap] = true;
                     }
                  }
               }
            }
         }

         if (n2 >= 0) {
            this.drawForIcon(TextureUtils.iconGrassSideOverlay, n2);
            n++;
         }

         if (n > 0) {
         }
      }
   }

   public int drawForIcon(TextureAtlasSprite textureAtlasSprite, int n) {
      GL11.glBindTexture(3553, textureAtlasSprite.glSpriteTextureId);
      int n2 = -1;
      int n3 = -1;
      int n4 = this.vertexCount / 4;

      for (int i = n; i < n4; i++) {
         TextureAtlasSprite textureAtlasSprite2 = this.quadSprites[i];
         if (textureAtlasSprite2 == textureAtlasSprite) {
            if (n3 < 0) {
               n3 = i;
            }
         } else if (n3 >= 0) {
            this.draw(n3, i);
            if (this.blockLayer == EnumWorldBlockLayer.TRANSLUCENT) {
               return i;
            }

            n3 = -1;
            if (n2 < 0) {
               n2 = i;
            }
         }
      }

      if (n3 >= 0) {
         this.draw(n3, n4);
      }

      if (n2 < 0) {
         n2 = n4;
      }

      return n2;
   }

   public void draw(int n, int n2) {
      int n3 = n2 - n;
      if (n3 > 0) {
         int n4 = n * 4;
         int n5 = n3 * 4;
         GL11.glDrawArrays(this.drawMode, n4, n5);
      }
   }

   public void setBlockLayer(EnumWorldBlockLayer enumWorldBlockLayer) {
      this.blockLayer = enumWorldBlockLayer;
      if (enumWorldBlockLayer == null) {
         if (this.quadSprites != null) {
            this.quadSpritesPrev = this.quadSprites;
         }

         this.quadSprites = null;
         this.quadSprite = null;
      }
   }

   public int getBufferQuadSize() {
      return this.rawIntBuffer.capacity() * 4 / (this.vertexFormat.getIntegerSize() * 4);
   }

   public RenderEnv getRenderEnv(IBlockState blockState, BlockPos blockPos) {
      if (this.renderEnv == null) {
         this.renderEnv = new RenderEnv(blockState, blockPos);
         return this.renderEnv;
      } else {
         this.renderEnv.reset(blockState, blockPos);
         return this.renderEnv;
      }
   }

   public boolean isDrawing() {
      return this.isDrawing;
   }

   public double getXOffset() {
      return this.xOffset;
   }

   public double getYOffset() {
      return this.yOffset;
   }

   public double getZOffset() {
      return this.zOffset;
   }

   public EnumWorldBlockLayer getBlockLayer() {
      return this.blockLayer;
   }

   public void putColorMultiplierRgba(float f, float f2, float f3, float f4, int n) {
      int colorIndex = this.getColorIndex(n);
      int n2 = -1;
      if (!this.noColor) {
         n2 = this.rawIntBuffer.get(colorIndex);
         if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            int n3 = (int)((n2 & 0xFF) * f);
            int n4 = (int)((n2 >> 8 & 0xFF) * f2);
            int n5 = (int)((n2 >> 16 & 0xFF) * f3);
            int n6 = (int)((n2 >> 24 & 0xFF) * f4);
            n2 = n6 << 24 | n5 << 16 | n4 << 8 | n3;
         } else {
            int n3 = (int)((n2 >> 24 & 0xFF) * f);
            int n4 = (int)((n2 >> 16 & 0xFF) * f2);
            int n5 = (int)((n2 >> 8 & 0xFF) * f3);
            int n6 = (int)((n2 & 0xFF) * f4);
            n2 = n3 << 24 | n4 << 16 | n5 << 8 | n6;
         }
      }

      this.rawIntBuffer.put(colorIndex, n2);
   }

   public void quadsToTriangles() {
      if (this.drawMode == 7) {
         if (this.byteBufferTriangles == null) {
            this.byteBufferTriangles = GLAllocation.createDirectByteBuffer(this.byteBuffer.capacity() * 2);
         }

         if (this.byteBufferTriangles.capacity() < this.byteBuffer.capacity() * 2) {
            this.byteBufferTriangles = GLAllocation.createDirectByteBuffer(this.byteBuffer.capacity() * 2);
         }

         int nextOffset = this.vertexFormat.getNextOffset();
         int limit = this.byteBuffer.limit();
         ((Buffer)this.byteBuffer).rewind();
         ((Buffer)this.byteBufferTriangles).clear();

         for (int i = 0; i < this.vertexCount; i += 4) {
            ((Buffer)this.byteBuffer).limit((i + 3) * nextOffset);
            ((Buffer)this.byteBuffer).position(i * nextOffset);
            this.byteBufferTriangles.put(this.byteBuffer);
            ((Buffer)this.byteBuffer).limit((i + 1) * nextOffset);
            ((Buffer)this.byteBuffer).position(i * nextOffset);
            this.byteBufferTriangles.put(this.byteBuffer);
            ((Buffer)this.byteBuffer).limit((i + 2 + 2) * nextOffset);
            ((Buffer)this.byteBuffer).position((i + 2) * nextOffset);
            this.byteBufferTriangles.put(this.byteBuffer);
         }

         ((Buffer)this.byteBuffer).limit(limit);
         ((Buffer)this.byteBuffer).rewind();
         ((Buffer)this.byteBufferTriangles).flip();
         this.modeTriangles = true;
      }
   }

   public boolean isColorDisabled() {
      return this.noColor;
   }

   public class State {
      public int[] stateRawBuffer;
      public VertexFormat stateVertexFormat;
      public TextureAtlasSprite[] stateQuadSprites;

      public State(int[] ints, VertexFormat vertexFormat, TextureAtlasSprite[] textureAtlasSprites) {
         this.stateRawBuffer = ints;
         this.stateVertexFormat = vertexFormat;
         this.stateQuadSprites = textureAtlasSprites;
      }

      public State(int[] buffer, VertexFormat format) {
         this.stateRawBuffer = buffer;
         this.stateVertexFormat = format;
      }

      public int[] getRawBuffer() {
         return this.stateRawBuffer;
      }

      public int getVertexCount() {
         return this.stateRawBuffer.length / this.stateVertexFormat.getIntegerSize();
      }

      public VertexFormat getVertexFormat() {
         return this.stateVertexFormat;
      }
   }
}
