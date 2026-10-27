package com.cheatbreaker.client.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class VertexFormatWrapper {
   public VertexFormat v;

   public VertexFormatWrapper(VertexFormat vIn) {
      this.v = vIn;
   }

   public VertexFormatWrapper from(VertexFormat vertexFormat) {
      this.v = vertexFormat;
      return this;
   }

   public VertexFormat getVertexFormat() {
      return this.v;
   }

   public void clear() {
      this.v.clear();
   }

   public boolean hasUv(int n) {
      return this.v.hasUvOffset(n);
   }

   public VertexElementWrapper getElement(int n) {
      return new VertexElementWrapper(this.v.getElement(n));
   }

   public VertexFormatWrapper addElement(VertexElementWrapper vertexElementWrapper) {
      return this.from(this.v.addElement(vertexElementWrapper.getVertexFormatElement()));
   }

   public List<VertexElementWrapper> getElements() {
      ArrayList list = new ArrayList();

      for (VertexFormatElement vertexFormatElement : this.v.getElements()) {
         list.add(new VertexElementWrapper(vertexFormatElement));
      }

      return list;
   }

   @Override
   public boolean equals(Object obj) {
      return this.v.equals(obj);
   }

   @Override
   public String toString() {
      return this.v.toString();
   }

   public int getNormalOffset() {
      return this.v.getNormalOffset();
   }

   @Override
   public int hashCode() {
      return this.v.hashCode();
   }

   public int getNextOffset() {
      return this.v.getNextOffset();
   }

   public int getUvOffsetById(int n) {
      return this.v.getUvOffsetById(n);
   }

   public int getElementCount() {
      return this.v.getElementCount();
   }

   public boolean hasNormal() {
      return this.v.hasNormal();
   }

   public int getOffset(int n) {
      return this.v.getOffset(n);
   }

   public boolean hasColor() {
      return this.v.hasColor();
   }

   public int getColorOffset() {
      return this.v.getColorOffset();
   }

   public int getIntegerSize() {
      return this.v.getIntegerSize();
   }
}
