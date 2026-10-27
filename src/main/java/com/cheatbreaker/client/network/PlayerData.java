package com.cheatbreaker.client.network;

import java.util.List;

public class PlayerData {
   public String name;
   public String text;
   public String text2;
   public List<Integer> integerList;
   public List<Integer> integerList2;

   public List<Integer> getIntegerList() {
      return this.integerList;
   }

   public String getText2() {
      return this.text2;
   }

   public void setName(String str) {
      this.name = str;
   }

   public void setText(String str) {
      this.text = str;
   }

   public String getName() {
      return this.name;
   }

   public String getText() {
      return this.text;
   }

   public List<Integer> getIntegerList2() {
      return this.integerList2;
   }

   public PlayerData(String str, String str2, String str3, List<Integer> list, List<Integer> list2) {
      this.name = str;
      this.text = str2;
      this.text2 = str3;
      this.integerList = list;
      this.integerList2 = list2;
   }
}
