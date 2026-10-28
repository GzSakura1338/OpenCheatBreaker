package com.cheatbreaker.client.emote;

import net.minecraft.entity.EntityLivingBase;

public interface EmoteState {
    EmoteDefinition getEmote();

    void setSource(EmoteSource emoteSource);

    EmoteSource getSource();

    void setEmote(EmoteDefinition emoteDefinition, EntityLivingBase entityLivingBase);

    void tick(EntityLivingBase entityLivingBase);

    boolean renderEmote(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f);
}
