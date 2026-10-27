package com.cheatbreaker.client.emote.bobj;

import com.cheatbreaker.client.emote.AnimationAction;
import com.cheatbreaker.client.emote.Bone;

public class ActionPlayer {
   public int fadeTicks;
   public float speed = 1.0F;
   public boolean looping = false;
   public boolean fadingOut = false;
   public boolean playing = true;
   public BobjAction action;
   public AnimationAction animationAction;
   public float time;
   public int priority;
   public int length;

   public void startFadeOut() {
      this.fadeTicks = (int)this.animationAction.fade;
      this.fadingOut = true;
   }

   public boolean isFadingOut() {
      return this.fadingOut && this.fadeTicks > 0;
   }

   public void applyPose(BobjArmature bobjArmature, float f) {
      for (BobjGroup bobjGroup : this.action.groups.values()) {
         Bone bone = bobjArmature.bonesByName.get(bobjGroup.boneName);
         if (bone != null) {
            bobjGroup.apply(bone, this.getInterpolatedTime(f));
         }
      }
   }

   public void resetFade() {
      this.fadeTicks = 0;
      this.fadingOut = false;
   }

   public boolean isFinished() {
      return this.fadingOut && this.fadeTicks <= 0;
   }

   public ActionPlayer(BobjAction bobjAction, AnimationAction animationAction) {
      this(bobjAction, animationAction, true);
   }

   public ActionPlayer(BobjAction bobjAction, AnimationAction animationAction, boolean flag, int n) {
      this(bobjAction, animationAction, flag);
      this.priority = n;
   }

   public void restart() {
      if (this.animationAction.reset) {
         this.time = Math.copySign(1.0F, this.speed) < 0.0F ? this.length : 0.0F;
      }

      this.resetFade();
   }

   public float getInterpolatedTime(float f) {
      float f2 = this.time + f * this.speed;
      if (this.looping) {
         if (f2 >= this.length && this.speed > 0.0F && this.animationAction.clamp) {
            f2 -= this.length;
         } else if (this.time < 0.0F && this.speed < 0.0F && this.animationAction.clamp) {
            f2 += this.length;
         }
      }

      return f2;
   }

   public void applyBlendedPose(BobjArmature bobjArmature, float f, float f2) {
      for (BobjGroup bobjGroup : this.action.groups.values()) {
         Bone bone = bobjArmature.bonesByName.get(bobjGroup.boneName);
         if (bone != null) {
            bobjGroup.applyBlended(bone, this.time, f2);
         }
      }
   }

   public float getFadeProgress(float f) {
      return (this.fadeTicks - f) / this.animationAction.fade;
   }

   public void tick() {
      if (this.fadingOut && this.fadeTicks > 0) {
         this.fadeTicks--;
      } else if (this.playing) {
         this.time = this.time + this.speed;
         if (!this.looping && !this.fadingOut && this.time >= this.length) {
            this.startFadeOut();
         }

         if (this.looping) {
            if (this.time >= this.length && this.speed > 0.0F && this.animationAction.clamp) {
               this.time = this.time - this.length;
               this.time = this.time + this.animationAction.tick;
            } else if (this.time < 0.0F && this.speed < 0.0F && this.animationAction.clamp) {
               this.time = this.time + this.length;
               this.time = this.time - this.animationAction.tick;
            }
         }
      }
   }

   public void setSpeedFactor(float f) {
      this.speed = f * this.animationAction.speed;
   }

   public ActionPlayer(BobjAction bobjAction, AnimationAction animationAction, boolean flag) {
      this.action = bobjAction;
      this.animationAction = animationAction;
      this.length = bobjAction.getLength();
      this.looping = flag;
      this.setSpeedFactor(1.0F);
   }
}
