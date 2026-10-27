package com.cheatbreaker.client.emote;

import net.minecraft.entity.player.EntityPlayer;

public class EmotePlayHelper {
   public static void playEmoteByName(String str, EntityPlayer entityPlayer) {
      EmoteState forEntity = PlayerEmoteState.getForEntity(entityPlayer);
      if (forEntity != null) {
         EmoteDefinition resolve = EmoteRegistry.resolve(str);
         EmoteSource source = forEntity.getSource();
         if (source == EmoteSource.CLIENT || resolve == null) {
            forEntity.setEmote(resolve, entityPlayer);
         }
      }
   }
}
