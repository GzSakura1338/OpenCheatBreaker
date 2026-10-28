package com.cheatbreaker.client.emote;

import com.cheatbreaker.client.event.SimpleEvent11;
import com.cheatbreaker.client.ui.screen.PreviewPlayer;
import net.minecraft.client.entity.AbstractClientPlayer;

public class EntityPoseSnapshot {
    public void onSimpleEvent11(SimpleEvent11 simpleEvent11) {
        if (simpleEvent11.getEntityPlayer() instanceof AbstractClientPlayer && !(simpleEvent11.getEntityPlayer() instanceof PreviewPlayer)) {
            AbstractClientPlayer entityPlayer = (AbstractClientPlayer)simpleEvent11.getEntityPlayer();
            EmoteState forEntity = PlayerEmoteState.getForEntity(entityPlayer);
            if (forEntity != null
                && forEntity.getEmote() != null
                && forEntity.renderEmote(entityPlayer, simpleEvent11.getDoubleValue1(), simpleEvent11.getDoubleValue2(), simpleEvent11.getDoubleValue3(), simpleEvent11.getFloatValue())) {
                simpleEvent11.setCancelled(true);
            }
        }
    }
}
