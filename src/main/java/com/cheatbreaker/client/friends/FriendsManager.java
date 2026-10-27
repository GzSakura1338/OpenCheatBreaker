package com.cheatbreaker.client.friends;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.StringBooleanIntEvent;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

public class FriendsManager {
   public Object2ObjectOpenHashMap<String, Friend> object2ObjectOpenHashMap = new Object2ObjectOpenHashMap();
   public Object2ObjectOpenHashMap<String, FriendRequest> object2ObjectOpenHashMap2 = new Object2ObjectOpenHashMap();
   public Object2ObjectOpenHashMap<String, List<String>> object2ObjectOpenHashMap3 = new Object2ObjectOpenHashMap();
   public Object2ObjectOpenHashMap<String, List<String>> object2ObjectOpenHashMap4 = new Object2ObjectOpenHashMap();
   public Object2ObjectOpenHashMap<String, List<String>> object2ObjectOpenHashMap5 = new Object2ObjectOpenHashMap();
   public DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

   public FriendsManager() {
      CheatBreaker.getInstance().getEventBus().checkClass2(StringBooleanIntEvent.class, this::onStringBooleanIntEvent);
   }

   public Friend get(String str) {
      return (Friend)this.object2ObjectOpenHashMap.get(str);
   }

   public void handleString(String str, String str2) {
      Friend friend = this.get(str);
      if (friend != null) {
         if (!this.object2ObjectOpenHashMap5.containsKey(str)) {
            this.object2ObjectOpenHashMap5.put(str, new ObjectArrayList());
         }

         ((List)this.object2ObjectOpenHashMap5.get(str)).add(friend.getName() + ": " + str2);
         String str3 = EnumChatFormatting.GRAY + LocalDateTime.now().format(this.dateTimeFormatter);
         String str4 = EnumChatFormatting.AQUA + Minecraft.getMinecraft().getSession().getUsername() + EnumChatFormatting.RESET;
         String str5 = str3 + " " + str4 + ": " + str2;
         this.handleString3(str, str5);
      }
   }

   public void handleString2(String str, String str2) {
      Friend friend = this.get(str);
      if (friend != null) {
         if (!this.object2ObjectOpenHashMap4.containsKey(str)) {
            this.object2ObjectOpenHashMap4.put(str, new ObjectArrayList());
         }

         String str3 = EnumChatFormatting.GRAY + LocalDateTime.now().format(this.dateTimeFormatter);
         String str4 = EnumChatFormatting.GREEN + friend.getName() + EnumChatFormatting.RESET;
         String str5 = str3 + " " + str4 + ": " + str2;
         ((List)this.object2ObjectOpenHashMap4.get(str)).add(str5);
      }
   }

   public Object2ObjectOpenHashMap<String, Friend> getObject2ObjectOpenHashMap() {
      return this.object2ObjectOpenHashMap;
   }

   public Object2ObjectOpenHashMap<String, List<String>> getObject2ObjectOpenHashMap4() {
      return this.object2ObjectOpenHashMap4;
   }

   public void onStringBooleanIntEvent(StringBooleanIntEvent stringBooleanIntEvent) {
      this.object2ObjectOpenHashMap.clear();
      this.object2ObjectOpenHashMap2.clear();
   }

   public Object2ObjectOpenHashMap<String, List<String>> getObject2ObjectOpenHashMap5() {
      return this.object2ObjectOpenHashMap5;
   }

   public Object2ObjectOpenHashMap<String, FriendRequest> getObject2ObjectOpenHashMap2() {
      return this.object2ObjectOpenHashMap2;
   }

   public void handleString3(String str, String str2) {
      Friend friend = this.get(str);
      if (friend != null) {
         if (!this.object2ObjectOpenHashMap3.containsKey(str)) {
            this.object2ObjectOpenHashMap3.put(str, new ObjectArrayList());
         }

         ((List)this.object2ObjectOpenHashMap3.get(str)).add(str2);
      }
   }

   public void handleString(String str) {
      if (this.object2ObjectOpenHashMap4.containsKey(str)) {
         List list = (List)this.object2ObjectOpenHashMap4.get(str);
         if (!this.object2ObjectOpenHashMap3.containsKey(str)) {
            this.object2ObjectOpenHashMap3.put(str, new ObjectArrayList());
         }

         ((List)this.object2ObjectOpenHashMap3.get(str)).addAll(list);
         this.object2ObjectOpenHashMap4.remove(str);
      }
   }

   public String getText() {
      switch (CheatBreaker.getInstance().getWebsocketController().friendOnlineStatus) {
         case AWAY:
            return "Away";
         case BUSY:
            return "Busy";
         case OFFLINE:
            return "Offline";
         default:
            return "Online";
      }
   }

   public Object2ObjectOpenHashMap<String, List<String>> getObject2ObjectOpenHashMap3() {
      return this.object2ObjectOpenHashMap3;
   }
}
