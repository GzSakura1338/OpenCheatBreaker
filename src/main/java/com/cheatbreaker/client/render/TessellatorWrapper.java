package com.cheatbreaker.client.render;

import net.minecraft.client.renderer.Tessellator;

public class TessellatorWrapper {
   public static Tessellator t = Tessellator.getInstance();
   public static TessellatorWrapper instance = new TessellatorWrapper();

   public void draw() {
      t.draw();
   }

   public TessellatorWrapper pos(double d, double d2, double d3) {
      t.getWorldRenderer().pos(d, d2, d3);
      return this;
   }

   public void color(float f, float f2, float f3, float f4) {
      t.getWorldRenderer().color(f, f2, f3, f4);
   }

   public void lightmap(short s, short s2) {
      t.getWorldRenderer().lightmap(s, s2);
   }

   public void normal(float f, float f2, float f3) {
      t.getWorldRenderer().normal(f, f2, f3);
   }

   public void begin(int n, VertexFormatWrapper vertexFormatWrapper) {
      t.getWorldRenderer().begin(n, vertexFormatWrapper.getVertexFormat());
   }

   public void endVertex() {
      t.getWorldRenderer().endVertex();
   }

   public void setTranslation(double d, double d2, double d3) {
      t.getWorldRenderer().setTranslation(d, d2, d3);
   }

   public WorldRendererWrapper colorInt(int n, int n2, int n3, int n4) {
      return new WorldRendererWrapper(t.getWorldRenderer().color(n, n2, n3, n4));
   }

   public TessellatorWrapper tex(double d, double d2) {
      t.getWorldRenderer().tex(d, d2);
      return this;
   }
}
