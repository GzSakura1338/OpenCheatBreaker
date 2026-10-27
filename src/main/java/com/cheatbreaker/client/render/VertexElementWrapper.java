package com.cheatbreaker.client.render;

import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class VertexElementWrapper {
   public VertexFormatElement v;

   public VertexElementWrapper(VertexFormatElement vIn) {
      this.v = vIn;
   }

   public VertexFormatElement getVertexFormatElement() {
      return this.v;
   }

   public boolean isPosition() {
      return this.v.isPositionElement();
   }

   @Override
   public boolean equals(Object obj) {
      return this.v.equals(obj);
   }

   @Override
   public String toString() {
      return this.v.toString();
   }

   public int getElementCount() {
      return this.v.getElementCount();
   }

   public int getIndex() {
      return this.v.getIndex();
   }

   @Override
   public int hashCode() {
      return this.v.hashCode();
   }

   public int getSize() {
      return this.v.getSize();
   }
}
