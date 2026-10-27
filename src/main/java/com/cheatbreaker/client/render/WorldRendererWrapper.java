package com.cheatbreaker.client.render;

import java.nio.ByteBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.WorldRenderer.State;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class WorldRendererWrapper {
   public WorldRenderer b;

   public WorldRendererWrapper(WorldRenderer worldRenderer) {
      this.b = worldRenderer;
   }

   public WorldRendererWrapper from(WorldRenderer worldRenderer) {
      this.b = worldRenderer;
      return this;
   }

   public void fixupQuadColor(int n) {
      this.b.putColor4(n);
   }

   public ByteBuffer getBuffer() {
      return this.b.getByteBuffer();
   }

   public void restoreState(State state) {
      this.b.setVertexState(state);
   }

   public WorldRendererWrapper normal(float f, float f2, float f3) {
      return this.from(this.b.normal(f, f2, f3));
   }

   public void begin(int n, VertexFormat vertexFormat) {
      this.b.begin(n, vertexFormat);
   }

   public WorldRendererWrapper color(int n, int n2, int n3, int n4) {
      return this.from(this.b.color(n, n2, n3, n4));
   }

   public void faceTex2(int n, int n2, int n3, int n4) {
      this.b.putBrightness4(n, n2, n3, n4);
   }

   public WorldRendererWrapper color(float f, float f2, float f3, float f4) {
      return this.from(this.b.color(f, f2, f3, f4));
   }

   public VertexFormatWrapper getVertexFormat() {
      return new VertexFormatWrapper(this.b.getVertexFormat());
   }

   public WorldRendererWrapper tex2(int n, int n2) {
      return this.from(this.b.lightmap(n, n2));
   }

   public void putBulkData(int[] ints) {
      this.b.addVertexData(ints);
   }

   public WorldRendererWrapper tex(double d, double d2) {
      return this.from(this.b.tex(d, d2));
   }

   public WorldRendererWrapper vertex(double d, double d2, double d3) {
      return this.from(this.b.pos(d, d2, d3));
   }

   public void putNormal(float f, float f2, float f3) {
      this.b.putNormal(f, f2, f3);
   }

   public void endVertex() {
      this.b.endVertex();
   }

   public void finishDrawing() {
      this.b.finishDrawing();
   }

   public int getDrawMode() {
      return this.b.getDrawMode();
   }

   public void sortVertexData(float f, float f2, float f3) {
      this.b.sortVertexData(f, f2, f3);
   }

   public void setTranslation(double d, double d2, double d3) {
      this.b.setTranslation(d, d2, d3);
   }

   public int getVertexCount() {
      return this.b.getVertexCount();
   }

   public void putColorMultiplier(float f, float f2, float f3, int n) {
      this.b.putColorMultiplier(f, f2, f3, n);
   }

   public void putColorRGB_F(float f, float f2, float f3, int n) {
      this.b.putColorRGB_F(f, f2, f3, n);
   }

   public void putColorRGB_F4(float f, float f2, float f3) {
      this.b.putColorRGB_F4(f, f2, f3);
   }

   public void reset() {
      this.b.reset();
   }

   public void noColor() {
      this.b.noColor();
   }

   public void putPosition(double d, double d2, double d3) {
      this.b.putPosition(d, d2, d3);
   }
}
