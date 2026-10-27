package net.optifine.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.optifine.render.VboRange;

public class LinkedListTest {
   public static void main(String[] strs) throws Exception {
      LinkedList linkedList = new LinkedList();
      ArrayList list = new ArrayList();
      ArrayList list2 = new ArrayList();
      Random random = new Random();
      byte b = 100;

      for (int i = 0; i < b; i++) {
         VboRange vboRange = new VboRange();
         vboRange.setPosition(i);
         list.add(vboRange);
      }

      for (int i = 0; i < 100000; i++) {
         checkLists(list, list2, b);
         checkLinkedList(linkedList, list2.size());
         if (i % 5 == 0) {
            dbgLinkedList(linkedList);
         }

         if (random.nextBoolean()) {
            if (!list.isEmpty()) {
               VboRange vboRange = (VboRange)list.get(random.nextInt(list.size()));
               LinkedList.Node node = vboRange.getNode();
               if (random.nextBoolean()) {
                  linkedList.addFirst(node);
                  dbg("Add first: " + vboRange.getPosition());
               } else if (random.nextBoolean()) {
                  linkedList.addLast(node);
                  dbg("Add last: " + vboRange.getPosition());
               } else {
                  if (list2.isEmpty()) {
                     continue;
                  }

                  VboRange vboRange2 = (VboRange)list2.get(random.nextInt(list2.size()));
                  LinkedList.Node node2 = vboRange2.getNode();
                  linkedList.addAfter(node2, node);
                  dbg("Add after: " + vboRange2.getPosition() + ", " + vboRange.getPosition());
               }

               list.remove(vboRange);
               list2.add(vboRange);
            }
         } else if (!list2.isEmpty()) {
            VboRange vboRange = (VboRange)list2.get(random.nextInt(list2.size()));
            LinkedList.Node node = vboRange.getNode();
            linkedList.remove(node);
            dbg("Remove: " + vboRange.getPosition());
            list2.remove(vboRange);
            list.add(vboRange);
         }
      }
   }

   public static void dbgLinkedList(LinkedList<VboRange> linkedList) {
      StringBuffer sb = new StringBuffer();
      linkedList.iterator().forEachRemaining(node -> {
         VboRange item = (VboRange)node.getItem();
         if (sb.length() > 0) {
            sb.append(", ");
         }

         sb.append(item.getPosition());
      });
      dbg("List: " + sb);
   }

   public static void checkLinkedList(LinkedList<VboRange> linkedList, int n) {
      if (linkedList.getSize() != n) {
         throw new RuntimeException("Wrong size, linked: " + linkedList.getSize() + ", used: " + n);
      }

      int n2 = 0;

      for (LinkedList.Node first = linkedList.getFirst(); first != null; first = first.getNext()) {
         n2++;
      }

      if (linkedList.getSize() != n2) {
         throw new RuntimeException("Wrong count, linked: " + linkedList.getSize() + ", count: " + n2);
      }

      int n3 = 0;

      for (LinkedList.Node last = linkedList.getLast(); last != null; last = last.getPrev()) {
         n3++;
      }

      if (linkedList.getSize() != n3) {
         throw new RuntimeException("Wrong count back, linked: " + linkedList.getSize() + ", count: " + n3);
      }
   }

   public static void checkLists(List<VboRange> list, List<VboRange> list2, int n) {
      int n2 = list.size() + list2.size();
      if (n2 != n) {
         throw new RuntimeException("Total size: " + n2);
      }
   }

   public static void dbg(String str) {
      System.out.println(str);
   }
}
