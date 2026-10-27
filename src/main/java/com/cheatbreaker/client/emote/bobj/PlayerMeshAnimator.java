package com.cheatbreaker.client.emote.bobj;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;

public class PlayerMeshAnimator implements MeshAnimator {
   public List<ActionPlayer> overlayActions = new ArrayList<>();
   public ActionPlayer jumpAction;
   public double lastX = Float.MAX_VALUE;
   public double lastZ = Float.MAX_VALUE;
   public ActionPlayer consumeAction;
   public ActionPlayer sprintingAction;
   public ActionPlayer runningAction;
   public ActionPlayer flyingAction;
   public ActionPlayer previousAction;
   public boolean wasOnGround = true;
   public boolean usingBow = false;
   public ActionPlayer ridingIdleAction;
   public ActionPlayer shootAction;
   public boolean consuming = false;
   public ActionPlayer currentAction;
   public EntityMeshRenderer renderer;
   public ActionPlayer swipeAction;
   public ActionPlayer hurtAction;
   public ActionPlayer crouchingIdleAction;
   public ActionPlayer walkingAction;
   public ActionPlayer swimmingIdleAction;
   public ActionPlayer forcedAction;
   public ActionPlayer sleepingAction;
   public ActionPlayer idleAction;
   public double lastMotionY;
   public ActionPlayer ridingAction;
   public ActionPlayer flyingIdleAction;
   public ActionPlayer swimmingAction;
   public ActionPlayer dyingAction;
   public ActionPlayer crouchingAction;
   public ActionPlayer fallingAction;
   public ActionPlayer landAction;

   public void selectAction(EntityLivingBase entityLivingBase) {
      double d = entityLivingBase.posX - this.lastX;
      double d2 = entityLivingBase.posZ - this.lastZ;
      boolean flag = entityLivingBase instanceof EntityPlayer && ((EntityPlayer)entityLivingBase).capabilities.isFlying;
      boolean isInWater = entityLivingBase.isInWater();
      float f = flag ? 0.1F : (isInWater ? 0.025F : 0.01F);
      boolean flag2 = Math.abs(d) > f || Math.abs(d2) > f;
      if (this.forcedAction != null) {
         this.playAction(this.forcedAction);
      } else if (entityLivingBase.getHealth() <= 0.0F) {
         this.playAction(this.dyingAction);
      } else if (entityLivingBase.isPlayerSleeping()) {
         this.playAction(this.sleepingAction);
      } else if (isInWater) {
         this.playAction(flag2 ? this.swimmingAction : this.swimmingIdleAction);
      } else if (entityLivingBase.isRiding()) {
         Entity ridingEntity = entityLivingBase.ridingEntity;
         boolean flag3 = Math.abs(ridingEntity.posX - this.lastX) > f || Math.abs(ridingEntity.posZ - this.lastZ) > f;
         this.lastX = ridingEntity.posX;
         this.lastZ = ridingEntity.posZ;
         this.playAction(flag3 ? this.ridingAction : this.ridingIdleAction);
      } else if (!flag) {
         float f2 = (float)(Math.round(Math.sqrt(d * d + d2 * d2) * 1000.0) / 1000.0);
         if (entityLivingBase.isSneaking()) {
            float f3 = f2 / 0.065F;
            this.playAction(flag2 ? this.crouchingAction : this.crouchingIdleAction);
            if (this.crouchingAction != null) {
               this.crouchingAction.setSpeedFactor(entityLivingBase.moveForward > 0.0F ? f3 : -f3);
            }
         } else if (!entityLivingBase.onGround && entityLivingBase.motionY < 0.0 && entityLivingBase.fallDistance > 1.25) {
            this.playAction(this.fallingAction);
         } else if (entityLivingBase.isSprinting() && this.sprintingAction != null) {
            this.playAction(this.sprintingAction);
            this.sprintingAction.setSpeedFactor(f2 / 0.281F);
         } else {
            this.playAction(flag2 ? this.runningAction : this.idleAction);
            float f3 = f2 / 0.216F;
            if (this.runningAction != null) {
               this.runningAction.setSpeedFactor(entityLivingBase.moveForward >= 0.0F ? f3 : -f3);
            }

            if (this.walkingAction != null) {
               this.walkingAction.setSpeedFactor(entityLivingBase.moveForward > 0.0F ? f3 : -f3);
            }
         }

         if (entityLivingBase.onGround && !this.wasOnGround && !entityLivingBase.isSprinting() && this.lastMotionY < -0.5) {
         }
      } else {
         this.playAction(flag2 ? this.flyingAction : this.flyingIdleAction);
      }

      if (!entityLivingBase.onGround && this.wasOnGround && Math.abs(entityLivingBase.motionY) > 0.2F) {
         this.wasOnGround = false;
      }

      boolean usingBow = this.usingBow;
      boolean consuming = this.consuming;
      ItemStack heldItem = entityLivingBase.getHeldItem();
      if (heldItem != null) {
         if (((EntityPlayer)entityLivingBase).getItemInUseCount() > 0) {
            EnumAction itemUseAction = heldItem.getItemUseAction();
            if (itemUseAction == EnumAction.BOW) {
               if (!this.overlayActions.contains(this.shootAction)) {
               }

               this.usingBow = true;
            } else if (itemUseAction == EnumAction.DRINK || itemUseAction == EnumAction.EAT) {
               if (!this.overlayActions.contains(this.consumeAction)) {
               }

               this.consuming = true;
            }
         } else {
            this.usingBow = false;
            this.consuming = false;
         }
      } else {
         this.usingBow = false;
         this.consuming = false;
      }

      if (usingBow && !this.usingBow && this.shootAction != null) {
         this.shootAction.startFadeOut();
      }

      if (consuming && !this.consuming && this.consumeAction != null) {
         this.consumeAction.startFadeOut();
      }

      if (entityLivingBase.hurtTime == entityLivingBase.maxHurtTime - 1) {
      }

      if (entityLivingBase.isSwingInProgress && entityLivingBase.swingProgress == 0.0F && !entityLivingBase.isPlayerSleeping()) {
      }

      this.lastX = entityLivingBase.posX;
      this.lastZ = entityLivingBase.posZ;
      this.lastMotionY = entityLivingBase.motionY;
      this.wasOnGround = entityLivingBase.onGround;
   }

   public void playAction(ActionPlayer actionPlayer) {
      if (this.currentAction != actionPlayer && actionPlayer != null) {
         if (this.currentAction == null || actionPlayer.priority >= this.currentAction.priority) {
            if (this.currentAction != null) {
               (this.previousAction = this.currentAction).startFadeOut();
            }

            (this.currentAction = actionPlayer).restart();
         }
      }
   }

   @Override
   public void loadActions() {
      ActionMap actions = this.renderer.modelDefinition.actions;
      MeshModel model = this.renderer.model;
      this.idleAction = model.createActionPlayerDefault(this.idleAction, actions.getAction("idle"), true);
      this.walkingAction = model.createActionPlayerDefault(this.walkingAction, actions.getAction("walking"), true);
      this.runningAction = model.createActionPlayerDefault(this.runningAction, actions.getAction("running"), true);
      this.sprintingAction = model.createActionPlayerDefault(this.sprintingAction, actions.getAction("sprinting"), true);
      this.crouchingAction = model.createActionPlayerDefault(this.crouchingAction, actions.getAction("crouching"), true);
      this.crouchingIdleAction = model.createActionPlayerDefault(this.crouchingIdleAction, actions.getAction("crouching_idle"), true);
      this.swimmingAction = model.createActionPlayerDefault(this.swimmingAction, actions.getAction("swimming"), true);
      this.swimmingIdleAction = model.createActionPlayerDefault(this.swimmingIdleAction, actions.getAction("swimming_idle"), true);
      this.flyingAction = model.createActionPlayerDefault(this.flyingAction, actions.getAction("flying"), true);
      this.flyingIdleAction = model.createActionPlayerDefault(this.flyingIdleAction, actions.getAction("flying_idle"), true);
      this.ridingAction = model.createActionPlayerDefault(this.ridingAction, actions.getAction("riding"), true);
      this.ridingIdleAction = model.createActionPlayerDefault(this.ridingIdleAction, actions.getAction("riding_idle"), true);
      this.dyingAction = model.createActionPlayerDefault(this.dyingAction, actions.getAction("dying"), false);
      this.fallingAction = model.createActionPlayerDefault(this.fallingAction, actions.getAction("falling"), true);
      this.sleepingAction = model.createActionPlayerDefault(this.sleepingAction, actions.getAction("sleeping"), true);
      this.swipeAction = model.createActionPlayerDefault(this.swipeAction, actions.getAction("swipe"), false);
      this.jumpAction = model.createActionPlayer(this.jumpAction, actions.getAction("jump"), false, 2);
      this.hurtAction = model.createActionPlayer(this.hurtAction, actions.getAction("hurt"), false, 3);
      this.landAction = model.createActionPlayerDefault(this.landAction, actions.getAction("land"), false);
      this.shootAction = model.createActionPlayerDefault(this.shootAction, actions.getAction("shoot"), true);
      this.consumeAction = model.createActionPlayerDefault(this.consumeAction, actions.getAction("consume"), true);
   }

   @Override
   public void setForcedAction(ActionPlayer actionPlayer) {
      if (actionPlayer != null) {
         this.forcedAction = actionPlayer;
      } else if (this.forcedAction != null) {
         this.forcedAction = null;
      }
   }

   public PlayerMeshAnimator(EntityMeshRenderer entityMeshRenderer) {
      this.renderer = entityMeshRenderer;
      this.loadActions();
   }

   public void playOverlay(ActionPlayer actionPlayer) {
      if (actionPlayer != null) {
         if (this.overlayActions.contains(actionPlayer)) {
            actionPlayer.restart();
         } else {
            actionPlayer.restart();
            this.overlayActions.add(actionPlayer);
         }
      }
   }

   @Override
   public void applyActions(BobjArmature bobjArmature, float f) {
      if (this.currentAction != null) {
         this.currentAction.applyPose(bobjArmature, f);
      }

      if (this.previousAction != null && this.previousAction.isFadingOut()) {
         this.previousAction.applyBlendedPose(bobjArmature, f, 1.0F - this.previousAction.getFadeProgress(f));
      }

      for (ActionPlayer actionPlayer : this.overlayActions) {
         if (actionPlayer.isFadingOut()) {
            actionPlayer.applyBlendedPose(bobjArmature, f, 1.0F - actionPlayer.getFadeProgress(f));
         } else {
            actionPlayer.applyPose(bobjArmature, f);
         }
      }
   }

   @Override
   public void tick(EntityLivingBase entityLivingBase) {
      if (this.lastX == Float.MAX_VALUE) {
         this.lastX = entityLivingBase.posX;
         this.lastZ = entityLivingBase.posZ;
      }

      this.selectAction(entityLivingBase);
      if (this.currentAction != null) {
         this.currentAction.tick();
      }

      if (this.previousAction != null) {
         this.previousAction.tick();
      }

      Iterator iterator = this.overlayActions.iterator();

      while (iterator.hasNext()) {
         ActionPlayer actionPlayer = (ActionPlayer)iterator.next();
         actionPlayer.tick();
         if (actionPlayer.isFinished()) {
            actionPlayer.resetFade();
            iterator.remove();
         }
      }
   }
}
