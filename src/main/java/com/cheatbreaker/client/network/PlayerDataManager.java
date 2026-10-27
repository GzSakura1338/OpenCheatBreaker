package com.cheatbreaker.client.network;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.impl.NickHiderMod;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;

public class PlayerDataManager {
   public Map<UUID, PlayerData> players = new HashMap<>();

   public boolean checkString(String str, boolean flag) {
      NickHiderMod nickHiderMod = CheatBreaker.getInstance().getModManager().nickHiderMod;
      if (flag) {
         try {
            for (PlayerData playerData : this.players.values()) {
               if (str.contains(playerData.getName()) || nickHiderMod.hideRealName.getBooleanValue() && str.contains(nickHiderMod.customNameString.getStringValue())) {
                  return true;
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      } else {
         try {
            for (UUID uuid : this.players.keySet()) {
               if (UUID.fromString(str).equals(uuid)) {
                  return true;
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      return false;
   }

   public PlayerData findPlayerData(String str) {
      NickHiderMod nickHiderMod = CheatBreaker.getInstance().getModManager().nickHiderMod;

      for (PlayerData playerData : this.players.values()) {
         if (str.contains(playerData.getName())) {
            return playerData;
         }

         if (nickHiderMod.hideRealName.getBooleanValue() && str.contains(nickHiderMod.customNameString.getStringValue())) {
            return this.findPlayerData2(Minecraft.getMinecraft().getSession().getPlayerID());
         }
      }

      return null;
   }

   public Map<UUID, PlayerData> getPlayers() {
      return this.players;
   }

   public String formatText(String str) {
      try {
         return UUID.fromString(str.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"))
            .toString();
      } catch (IllegalArgumentException e) {
         return str;
      }
   }

   public PlayerData findPlayerData2(String str) {
      return this.players.get(UUID.fromString(this.formatText(str)));
   }

   public boolean checkString(String str) {
      try {
         String str2 = UUID.fromString(str.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"))
            .toString();
         return true;
      } catch (IllegalArgumentException e) {
         return false;
      }
   }
}
