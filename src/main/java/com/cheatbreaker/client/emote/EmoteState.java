package com.cheatbreaker.client.emote;

import net.minecraft.entity.EntityLivingBase;

public interface EmoteState {
   void tick(EntityLivingBase entityLivingBase);

   EmoteDefinition getEmote();

   void setEmote(EmoteDefinition emoteDefinition, EntityLivingBase entityLivingBase);

   void setSource(EmoteSource emoteSource);

   EmoteSource getSource();

   boolean renderEmote(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f);
}
