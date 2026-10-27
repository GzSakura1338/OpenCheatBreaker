package com.cheatbreaker.client.emote.bobj;

import net.minecraft.entity.EntityLivingBase;

public interface MeshAnimator {
   void setForcedAction(ActionPlayer actionPlayer);

   void loadActions();

   void applyActions(BobjArmature bobjArmature, float f);

   void tick(EntityLivingBase entityLivingBase);
}
