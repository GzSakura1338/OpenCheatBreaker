package net.optifine.render;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.VboRenderList;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumWorldBlockLayer;
import net.optifine.Config;
import net.optifine.util.LinkedList;

public class VboRegion {
   public EnumWorldBlockLayer layer = null;
   public int glBufferId = OpenGlHelper.glGenBuffers();
   public int capacity = 4096;
   public int positionTop = 0;
   public int sizeUsed;
   public LinkedList<VboRange> rangeList = new LinkedList<>();
   public VboRange compactRangeLast = null;
   public IntBuffer bufferIndexVertex = Config.createDirectIntBuffer(this.capacity);
   public IntBuffer bufferCountVertex = Config.createDirectIntBuffer(this.capacity);
   public int drawMode = 7;
   public int vertexBytes = DefaultVertexFormats.BLOCK.getNextOffset();

   public VboRegion(EnumWorldBlockLayer enumWorldBlockLayer) {
      this.layer = enumWorldBlockLayer;
      this.bindBuffer();
      long toBytes = this.toBytes(this.capacity);
      OpenGlHelper.glBufferData(OpenGlHelper.GL_ARRAY_BUFFER, toBytes, OpenGlHelper.GL_STATIC_DRAW);
      this.unbindBuffer();
   }

   public void bufferData(ByteBuffer byteBuffer, VboRange vboRange) {
      int position = vboRange.getPosition();
      int size = vboRange.getSize();
      int toVertex = this.toVertex(byteBuffer.limit());
      if (toVertex <= 0) {
         if (position >= 0) {
            vboRange.setPosition(-1);
            vboRange.setSize(0);
            this.rangeList.remove(vboRange.getNode());
            this.sizeUsed -= size;
         }
      } else {
         if (toVertex > size) {
            vboRange.setPosition(this.positionTop);
            vboRange.setSize(toVertex);
            this.positionTop += toVertex;
            if (position >= 0) {
               this.rangeList.remove(vboRange.getNode());
            }

            this.rangeList.addLast(vboRange.getNode());
         }

         vboRange.setSize(toVertex);
         this.sizeUsed += toVertex - size;
         this.checkVboSize(vboRange.getPositionNext());
         long toBytes = this.toBytes(vboRange.getPosition());
         this.bindBuffer();
         OpenGlHelper.glBufferSubData(OpenGlHelper.GL_ARRAY_BUFFER, toBytes, byteBuffer);
         this.unbindBuffer();
         if (this.positionTop > this.sizeUsed * 11 / 10) {
            this.compactRanges(1);
         }
      }
   }

   public void compactRanges(int n) {
      if (!this.rangeList.isEmpty()) {
         VboRange compactRangeLast = this.compactRangeLast;
         if (compactRangeLast == null || !this.rangeList.contains(compactRangeLast.getNode())) {
            compactRangeLast = this.rangeList.getFirst().getItem();
         }

         int position = compactRangeLast.getPosition();
         VboRange prev = compactRangeLast.getPrev();
         if (prev == null) {
            position = 0;
         } else {
            position = prev.getPositionNext();
         }

         int n2 = 0;

         while (compactRangeLast != null && n2 < n) {
            n2++;
            if (compactRangeLast.getPosition() == position) {
               position += compactRangeLast.getSize();
               compactRangeLast = compactRangeLast.getNext();
            } else {
               int n3 = compactRangeLast.getPosition() - position;
               if (compactRangeLast.getSize() <= n3) {
                  this.copyVboData(compactRangeLast.getPosition(), position, compactRangeLast.getSize());
                  compactRangeLast.setPosition(position);
                  position += compactRangeLast.getSize();
                  compactRangeLast = compactRangeLast.getNext();
               } else {
                  this.checkVboSize(this.positionTop + compactRangeLast.getSize());
                  this.copyVboData(compactRangeLast.getPosition(), this.positionTop, compactRangeLast.getSize());
                  compactRangeLast.setPosition(this.positionTop);
                  this.positionTop = this.positionTop + compactRangeLast.getSize();
                  VboRange next = compactRangeLast.getNext();
                  this.rangeList.remove(compactRangeLast.getNode());
                  this.rangeList.addLast(compactRangeLast.getNode());
                  compactRangeLast = next;
               }
            }
         }

         if (compactRangeLast == null) {
            this.positionTop = this.rangeList.getLast().getItem().getPositionNext();
         }

         this.compactRangeLast = compactRangeLast;
      }
   }

   public void checkRanges() {
      int n = 0;
      int n2 = 0;

      for (VboRange item = this.rangeList.getFirst().getItem(); item != null; item = item.getNext()) {
         n++;
         n2 += item.getSize();
         if (item.getPosition() < 0 || item.getSize() <= 0 || item.getPositionNext() > this.positionTop) {
            throw new RuntimeException("Invalid range: " + item);
         }

         VboRange prev = item.getPrev();
         if (prev != null && item.getPosition() < prev.getPositionNext()) {
            throw new RuntimeException("Invalid range: " + item);
         }

         VboRange next = item.getNext();
         if (next != null && item.getPositionNext() > next.getPosition()) {
            throw new RuntimeException("Invalid range: " + item);
         }
      }

      if (n != this.rangeList.getSize()) {
         throw new RuntimeException("Invalid count: " + n + " <> " + this.rangeList.getSize());
      }

      if (n2 != this.sizeUsed) {
         throw new RuntimeException("Invalid size: " + n2 + " <> " + this.sizeUsed);
      }
   }

   public void checkVboSize(int n) {
      if (this.capacity < n) {
         this.expandVbo(n);
      }
   }

   public void copyVboData(int n, int n2, int n3) {
      long toBytes = this.toBytes(n);
      long toBytes2 = this.toBytes(n2);
      long toBytes3 = this.toBytes(n3);
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, this.glBufferId);
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, this.glBufferId);
      OpenGlHelper.glCopyBufferSubData(OpenGlHelper.GL_COPY_READ_BUFFER, OpenGlHelper.GL_COPY_WRITE_BUFFER, toBytes, toBytes2, toBytes3);
      Config.checkGlError("Copy VBO range");
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, 0);
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, 0);
   }

   public void expandVbo(int n) {
      int n2 = this.capacity * 6 / 4;

      while (n2 < n) {
         n2 = n2 * 6 / 4;
      }

      long toBytes = this.toBytes(this.capacity);
      long toBytes2 = this.toBytes(n2);
      int glGenBuffers = OpenGlHelper.glGenBuffers();
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, glGenBuffers);
      OpenGlHelper.glBufferData(OpenGlHelper.GL_ARRAY_BUFFER, toBytes2, OpenGlHelper.GL_STATIC_DRAW);
      Config.checkGlError("Expand VBO");
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, this.glBufferId);
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, glGenBuffers);
      OpenGlHelper.glCopyBufferSubData(OpenGlHelper.GL_COPY_READ_BUFFER, OpenGlHelper.GL_COPY_WRITE_BUFFER, 0L, 0L, toBytes);
      Config.checkGlError("Copy VBO: " + toBytes2);
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_READ_BUFFER, 0);
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_COPY_WRITE_BUFFER, 0);
      OpenGlHelper.glDeleteBuffers(this.glBufferId);
      this.bufferIndexVertex = Config.createDirectIntBuffer(n2);
      this.bufferCountVertex = Config.createDirectIntBuffer(n2);
      this.glBufferId = glGenBuffers;
      this.capacity = n2;
   }

   public void bindBuffer() {
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, this.glBufferId);
   }

   public void drawArrays(int n, VboRange vboRange) {
      if (this.drawMode != n) {
         if (this.bufferIndexVertex.position() > 0) {
            throw new IllegalArgumentException("Mixed region draw modes: " + this.drawMode + " != " + n);
         }

         this.drawMode = n;
      }

      this.bufferIndexVertex.put(vboRange.getPosition());
      this.bufferCountVertex.put(vboRange.getSize());
   }

   public void finishDraw(VboRenderList vboRenderList) {
      this.bindBuffer();
      vboRenderList.setupArrayPointers();
      ((Buffer)this.bufferIndexVertex).flip();
      ((Buffer)this.bufferCountVertex).flip();
      GlStateManager.glMultiDrawArrays(this.drawMode, this.bufferIndexVertex, this.bufferCountVertex);
      ((Buffer)this.bufferIndexVertex).limit(this.bufferIndexVertex.capacity());
      ((Buffer)this.bufferCountVertex).limit(this.bufferCountVertex.capacity());
      if (this.positionTop > this.sizeUsed * 11 / 10) {
         this.compactRanges(1);
      }
   }

   public void unbindBuffer() {
      OpenGlHelper.glBindBuffer(OpenGlHelper.GL_ARRAY_BUFFER, 0);
   }

   public void deleteGlBuffers() {
      if (this.glBufferId >= 0) {
         OpenGlHelper.glDeleteBuffers(this.glBufferId);
         this.glBufferId = -1;
      }
   }

   public long toBytes(int n) {
      return (long)n * this.vertexBytes;
   }

   public int toVertex(long l) {
      return (int)(l / this.vertexBytes);
   }

   public int getPositionTop() {
      return this.positionTop;
   }
}
