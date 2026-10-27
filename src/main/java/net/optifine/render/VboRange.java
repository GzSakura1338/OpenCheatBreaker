package net.optifine.render;

import net.optifine.util.LinkedList.Node;

public class VboRange {
   public int position = -1;
   public int size = 0;
   public Node<VboRange> node = new Node<>(this);

   public int getPosition() {
      return this.position;
   }

   public int getSize() {
      return this.size;
   }

   public int getPositionNext() {
      return this.position + this.size;
   }

   public void setPosition(int n) {
      this.position = n;
   }

   public void setSize(int n) {
      this.size = n;
   }

   public Node<VboRange> getNode() {
      return this.node;
   }

   public VboRange getPrev() {
      Node prev = this.node.getPrev();
      return prev == null ? null : (VboRange)prev.getItem();
   }

   public VboRange getNext() {
      Node next = this.node.getNext();
      return next == null ? null : (VboRange)next.getItem();
   }

   @Override
   public String toString() {
      return "" + this.position + "/" + this.size + "/" + (this.position + this.size);
   }
}
