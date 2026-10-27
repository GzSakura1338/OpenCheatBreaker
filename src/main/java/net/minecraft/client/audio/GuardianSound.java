package net.minecraft.client.audio;

import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.ResourceLocation;

public class GuardianSound extends MovingSound {
   public EntityGuardian guardian;

   public GuardianSound(EntityGuardian guardian) {
      super(new ResourceLocation("minecraft:mob.guardian.attack"));
      this.guardian = guardian;
      this.attenuationType = ISound.AttenuationType.NONE;
      this.repeat = true;
      this.repeatDelay = 0;
   }

   @Override
   public void update() {
      if (!this.guardian.isDead && this.guardian.hasTargetedEntity()) {
         this.xPosF = (float)this.guardian.posX;
         this.yPosF = (float)this.guardian.posY;
         this.zPosF = (float)this.guardian.posZ;
         float beamProgress = this.guardian.getBeamProgress(0.0F);
         this.volume = 0.0F + 1.0F * beamProgress * beamProgress;
         this.pitch = 0.7F + 0.5F * beamProgress;
      } else {
         this.donePlaying = true;
      }
   }
}
