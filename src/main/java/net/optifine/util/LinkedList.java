package net.optifine.util;

import java.util.Iterator;

public class LinkedList<T> {
   public LinkedList.Node<T> first;
   public LinkedList.Node<T> last;
   public int size;

   public void addFirst(LinkedList.Node<T> node) {
      this.checkNoParent(node);
      if (this.isEmpty()) {
         this.first = node;
         this.last = node;
      } else {
         LinkedList.Node first = this.first;
         node.setNext(first);
         first.setPrev(node);
         this.first = node;
      }

      node.setParent(this);
      this.size++;
   }

   public void addLast(LinkedList.Node<T> node) {
      this.checkNoParent(node);
      if (this.isEmpty()) {
         this.first = node;
         this.last = node;
      } else {
         LinkedList.Node last = this.last;
         node.setPrev(last);
         last.setNext(node);
         this.last = node;
      }

      node.setParent(this);
      this.size++;
   }

   public void addAfter(LinkedList.Node<T> node, LinkedList.Node<T> node2) {
      if (node == null) {
         this.addFirst(node2);
      } else if (node == this.last) {
         this.addLast(node2);
      } else {
         this.checkParent(node);
         this.checkNoParent(node2);
         LinkedList.Node next = node.getNext();
         node.setNext(node2);
         node2.setPrev(node);
         next.setPrev(node2);
         node2.setNext(next);
         node2.setParent(this);
         this.size++;
      }
   }

   public LinkedList.Node<T> remove(LinkedList.Node<T> node) {
      this.checkParent(node);
      LinkedList.Node prev = node.getPrev();
      LinkedList.Node next = node.getNext();
      if (prev != null) {
         prev.setNext(next);
      } else {
         this.first = next;
      }

      if (next != null) {
         next.setPrev(prev);
      } else {
         this.last = prev;
      }

      node.setPrev(null);
      node.setNext(null);
      node.setParent(null);
      this.size--;
      return node;
   }

   public void moveAfter(LinkedList.Node<T> node, LinkedList.Node<T> node2) {
      this.remove(node2);
      this.addAfter(node, node2);
   }

   public boolean find(LinkedList.Node<T> node, LinkedList.Node<T> node2, LinkedList.Node<T> node3) {
      this.checkParent(node2);
      if (node3 != null) {
         this.checkParent(node3);
      }

      LinkedList.Node node4;
      for (node4 = node2; node4 != null && node4 != node3; node4 = node4.getNext()) {
         if (node4 == node) {
            return true;
         }
      }

      if (node4 != node3) {
         throw new IllegalArgumentException("Sublist is not linked, from: " + node2 + ", to: " + node3);
      } else {
         return false;
      }
   }

   public void checkParent(LinkedList.Node<T> node) {
      if (node.parent != this) {
         throw new IllegalArgumentException("Node has different parent, node: " + node + ", parent: " + node.parent + ", this: " + this);
      }
   }

   public void checkNoParent(LinkedList.Node<T> node) {
      if (node.parent != null) {
         throw new IllegalArgumentException("Node has different parent, node: " + node + ", parent: " + node.parent + ", this: " + this);
      }
   }

   public boolean contains(LinkedList.Node<T> node) {
      return node.parent == this;
   }

   public Iterator<LinkedList.Node<T>> iterator() {
      return new Iterator<LinkedList.Node<T>>() {
         public LinkedList.Node<T> node = LinkedList.this.getFirst();

         @Override
         public boolean hasNext() {
            return this.node != null;
         }

         public LinkedList.Node<T> next() {
            LinkedList.Node node = this.node;
            if (this.node != null) {
               this.node = this.node.next;
            }

            return node;
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("remove");
         }
      };
   }

   public LinkedList.Node<T> getFirst() {
      return this.first;
   }

   public LinkedList.Node<T> getLast() {
      return this.last;
   }

   public int getSize() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size <= 0;
   }

   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer();

      for (Iterator<LinkedList.Node<T>> iterator = this.iterator(); iterator.hasNext(); ) {
         LinkedList.Node node = iterator.next();
         if (sb.length() > 0) {
            sb.append(", ");
         }

         sb.append(node.getItem());
      }

      return "" + this.size + " [" + sb.toString() + "]";
   }

   public static class Node<T> {
      public T item;
      public LinkedList.Node<T> prev;
      public LinkedList.Node<T> next;
      public LinkedList<T> parent;

      public Node(T value) {
         this.item = (T)value;
      }

      public T getItem() {
         return this.item;
      }

      public LinkedList.Node<T> getPrev() {
         return this.prev;
      }

      public LinkedList.Node<T> getNext() {
         return this.next;
      }

      public void setPrev(LinkedList.Node<T> node) {
         this.prev = node;
      }

      public void setNext(LinkedList.Node<T> node) {
         this.next = node;
      }

      public void setParent(LinkedList<T> linkedList) {
         this.parent = linkedList;
      }

      @Override
      public String toString() {
         return "" + this.item;
      }
   }
}
