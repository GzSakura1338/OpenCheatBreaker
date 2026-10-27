package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.CustomEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent;
import com.cheatbreaker.client.event.EntityLivingBaseFloatEvent2;
import com.cheatbreaker.client.event.EntityLivingBaseFloatEvent3;
import com.cheatbreaker.client.event.EntityLivingBaseIBakedModelItemCameraTransformsTransformTypeItemStackStringEvent;
import com.cheatbreaker.client.event.EntityPlayerSPItemStackBooleanFloatIntEvent;
import com.cheatbreaker.client.event.FloatEvent10;
import com.cheatbreaker.client.event.FloatEvent2;
import com.cheatbreaker.client.event.IBakedModelFloatEvent1;
import com.cheatbreaker.client.event.IBakedModelFloatEvent2;
import com.cheatbreaker.client.event.IBakedModelItemCameraTransformsTransformTypeItemStackEvent1;
import com.cheatbreaker.client.event.IBakedModelItemCameraTransformsTransformTypeItemStackEvent2;
import com.cheatbreaker.client.event.ItemRendererItemStackFloatEvent;
import com.cheatbreaker.client.event.ItemStackStringIntEvent;
import com.cheatbreaker.client.event.SimpleEvent23;
import com.cheatbreaker.client.event.SimpleEvent25;
import com.cheatbreaker.client.event.SimpleEvent35;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.HudElementType;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailDetector;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.BlockVine;
import net.minecraft.block.BlockWeb;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.optifine.Config;
import net.optifine.CustomItems;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;

public class AnimationsMod extends AbstractMod {
   public Setting positionSettings;
   public Setting firstPersonItemPositions;
   public Setting thirdPersonItemPositions;
   public Setting itemTransformations;
   public Setting bowPullback;
   public int swingProgressInt;
   public float prevEyeHeight;
   public float eyeHeight;
   public Setting blockAnimation;
   public Setting rodPosition;
   public Setting thirdPersonBlockAnimation;
   public Setting interactionSettings;
   public static ResourceLocation enchantedItemGlintPng = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   public float prevSwingProgress;
   public Setting consumeAnimation;
   public Setting blockHittingAnimation;
   public Setting smoothSneaking;
   public Setting longerUnsneak;
   public static Setting unusedSetting1;
   public static Setting unusedSetting2;
   public Setting punchingDuringUsage;
   public static Setting unusedSetting3;
   public Setting punchingDuringUsageParticles;
   public Setting itemSwitchingAnimation;
   public Setting oldEnchantmentGlint;
   public Setting hitColorArmor;
   public Setting hitColorArmorGlint;
   public Setting hitColorBrightness;
   public float swingProgress;
   public Setting missPenaltyBehavior;
   public boolean swinging;
   public Setting itemVisualSettings;
   public static final float SNEAK_EYE_HEIGHT = 1.54F;
   public static final float STAND_EYE_HEIGHT = 1.62F;
   public Setting droppedItemDirection;
   public Setting droppedItemSprites;
   public static ItemStack itemStack;
   public Setting projectileSprites;
   public Setting hudSettings;
   public Setting healthBarFlashing;

   public float getSwingProgress(float f) {
      float f2 = this.swingProgress - this.prevSwingProgress;
      if (!this.swinging) {
         return this.mc.thePlayer.getSwingProgress(f);
      }

      if (f2 < 0.0F) {
         f2++;
      }

      return this.prevSwingProgress + f2 * f;
   }

   public void onClientTickEvent(ClientTickEvent clientTickEvent) {
      this.tickSwing();
      this.tickEyeHeight();
   }

   public void onIBakedModelItemCameraTransformsTransformTypeItemStackEvent2(IBakedModelItemCameraTransformsTransformTypeItemStackEvent2 bakedModelItemCameraTransformsTransformTypeItemStackEvent2) {
      if ((Boolean)this.firstPersonItemPositions.getValue() && bakedModelItemCameraTransformsTransformTypeItemStackEvent2.getTransformType() != TransformType.THIRD_PERSON) {
         bakedModelItemCameraTransformsTransformTypeItemStackEvent2.setFlag(true);
         GlStateManager.translate(-0.01, 0.002, 5.0E-4);
         GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.scale(1.0F, 1.0F, 1.0F);
         if (bakedModelItemCameraTransformsTransformTypeItemStackEvent2.getItemStack().getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock)bakedModelItemCameraTransformsTransformTypeItemStackEvent2.getItemStack().getItem()).getBlock();
            if (block instanceof BlockCarpet || block instanceof BlockSnow) {
               GlStateManager.translate(0.015, -0.331, -5.0E-4);
               GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scale(1.0F, 1.0F, 1.0F);
            } else if (block instanceof BlockLadder
               || block instanceof BlockPane
               || block instanceof BlockRail
               || block instanceof BlockRailPowered
               || block instanceof BlockRailDetector
               || block instanceof BlockVine
               || block instanceof BlockWeb
               || block instanceof BlockLever
               || block instanceof BlockBush
               || block instanceof BlockTorch
               || block instanceof BlockTripWireHook) {
               GlStateManager.translate(0.0, 0.0, 5.0E-4);
               GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scale(1.0F, 1.0F, 1.0F);
            } else if (block instanceof BlockFurnace
               || block instanceof BlockDispenser
               || block instanceof BlockPumpkin
               || block instanceof BlockChest
               || block instanceof BlockEnderChest
               || block instanceof BlockFence
               || block instanceof BlockFenceGate) {
               GlStateManager.translate(0.015, -0.004, -5.0E-4);
               GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scale(1.0F, 1.0F, 1.0F);
            } else if (block != null) {
               GlStateManager.translate(0.015, -0.004, -5.0E-4);
               GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scale(1.0F, 1.0F, 1.0F);
            }
         }
      }
   }

   public void onSimpleEvent35(SimpleEvent35 simpleEvent35) {
      String value = (String)this.missPenaltyBehavior.getValue();
      if (value.contains("Swing")) {
         MovingObjectPosition objectMouseOver = this.mc.objectMouseOver;
         EntityPlayerSP thePlayer = this.mc.thePlayer;
         if (objectMouseOver != null
            && objectMouseOver.typeOfHit == MovingObjectType.ENTITY
            && value.contains("Particles")
            && !objectMouseOver.entityHit.hitByEntity(thePlayer)
            && objectMouseOver.entityHit instanceof EntityLivingBase) {
            if (thePlayer.fallDistance > 0.0F
               && !thePlayer.onGround
               && !thePlayer.isOnLadder()
               && !thePlayer.isInWater()
               && !thePlayer.isPotionActive(Potion.blindness)
               && thePlayer.ridingEntity == null) {
               thePlayer.onCriticalHit(objectMouseOver.entityHit);
            }

            if (EnchantmentHelper.getModifierForCreature(thePlayer.getHeldItem(), ((EntityLivingBase)objectMouseOver.entityHit).getCreatureAttribute()) > 0.0F) {
               thePlayer.onEnchantmentCritical(objectMouseOver.entityHit);
            } else if (CheatBreaker.getInstance().getModManager().particlesMod.isEnabled()
               && CheatBreaker.getInstance().getModManager().particlesMod.sharpnessParticles.getValue().equals("Always")) {
               this.mc.effectRenderer.emitParticleAtEntity(objectMouseOver.entityHit, EnumParticleTypes.CRIT_MAGIC);
            }
         }

         if (!thePlayer.isSwingInProgress || thePlayer.swingProgressInt >= thePlayer.getArmSwingAnimationEnd() / 2 || thePlayer.swingProgressInt < 0) {
            thePlayer.swingProgressInt = -1;
            thePlayer.isSwingInProgress = true;
         }
      }
   }

   public void onIBakedModelFloatEvent2(IBakedModelFloatEvent2 bakedModelFloatEvent2) {
      if (!bakedModelFloatEvent2.getBakedModel().isGui3d() && this.droppedItemDirection.getValue().equals("Face Player")) {
         GlStateManager.rotate(-this.mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
      }
   }

   public void applyBowTransform(ItemStack itemStack, int n, float f) {
      GlStateManager.rotate(-18.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(-12.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-8.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(-0.9F, 0.2F, 0.0F);
      float f2 = itemStack.getMaxItemUseDuration() - (n - f + 1.0F);
      float f3 = f2 / 20.0F;
      f3 = (f3 * f3 + f3 * 2.0F) / 3.0F;
      if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      if (f3 > 0.1F) {
         GlStateManager.translate(0.0F, MathHelper.sin((f2 - 0.1F) * 1.3F) * 0.01F * (f3 - 0.1F), 0.0F);
      }

      GlStateManager.translate(0.0F, 0.0F, f3 * 0.1F);
      if (this.bowPullback.getBooleanValue()) {
         GlStateManager.rotate(-335.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(-50.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0F, 0.5F, 0.0F);
      }

      float f4 = 1.0F + f3 * 0.2F;
      GlStateManager.scale(1.0F, 1.0F, f4);
      if (this.bowPullback.getBooleanValue()) {
         GlStateManager.translate(0.0F, -0.5F, 0.0F);
         GlStateManager.rotate(50.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(335.0F, 0.0F, 0.0F, 1.0F);
      }
   }

   public void onEntityLivingBaseIBakedModelItemCameraTransformsTransformTypeItemStackStringEvent(
      EntityLivingBaseIBakedModelItemCameraTransformsTransformTypeItemStackStringEvent entityLivingBaseIBakedModelItemCameraTransformsTransformTypeItemStackStringEvent
   ) {
      if (entityLivingBaseIBakedModelItemCameraTransformsTransformTypeItemStackStringEvent.getTransformType() == TransformType.THIRD_PERSON) {
         EntityLivingBase entityLivingBase = entityLivingBaseIBakedModelItemCameraTransformsTransformTypeItemStackStringEvent.getEntityLivingBase();
         if (entityLivingBase instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer)entityLivingBase;
            ItemStack heldItem = entityPlayer.getHeldItem();
            if (heldItem != null && entityPlayer.getItemInUseCount() > 0 && heldItem.getItemUseAction() == EnumAction.BLOCK) {
               this.applyHandTransform();
            }
         }
      }
   }

   public void onIBakedModelFloatEvent1(IBakedModelFloatEvent1 bakedModelFloatEvent1) {
      if (!bakedModelFloatEvent1.getBakedModel().isGui3d() && !this.droppedItemDirection.getValue().equals("Default")) {
         bakedModelFloatEvent1.setFloatValue(180.0F - this.mc.getRenderManager().playerViewY);
      }
   }

   public void onEntityPlayerSPItemStackBooleanFloatIntEvent(EntityPlayerSPItemStackBooleanFloatIntEvent entityPlayerSPItemStackBooleanFloatIntEvent) {
      if ((Boolean)this.itemSwitchingAnimation.getValue()) {
         entityPlayerSPItemStackBooleanFloatIntEvent.setFlag(true);
         ItemStack itemToRender = entityPlayerSPItemStackBooleanFloatIntEvent.getItemToRender();
         ItemStack currentItem = entityPlayerSPItemStackBooleanFloatIntEvent.getCurrentItem();
         boolean flag = entityPlayerSPItemStackBooleanFloatIntEvent.getEquippedItemSlot() != entityPlayerSPItemStackBooleanFloatIntEvent.getEntityPlayerSP().inventory.currentItem || currentItem != itemToRender;
         if (itemToRender == null && currentItem == null) {
            flag = false;
         }

         if (currentItem != null && itemToRender != null && currentItem != itemToRender && currentItem.getItem() == itemToRender.getItem() && currentItem.getItemDamage() == itemToRender.getItemDamage()) {
            entityPlayerSPItemStackBooleanFloatIntEvent.setItemToRender(currentItem);
            flag = false;
         }

         entityPlayerSPItemStackBooleanFloatIntEvent.setFlag3(flag);
      }
   }

   public void applySwingTranslation(float f) {
      float sin = MathHelper.sin(f * (float) Math.PI);
      float sin2 = MathHelper.sin(MathHelper.sqrt_float(f) * (float) Math.PI);
      GlStateManager.translate(-sin2 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(f) * (float) Math.PI * 2.0F) * 0.2F, -sin * 0.2F);
   }

   public int getSwingDuration(EntityPlayerSP entityPlayerSP) {
      return entityPlayerSP.isPotionActive(Potion.digSpeed)
         ? 5 - entityPlayerSP.getActivePotionEffect(Potion.digSpeed).getAmplifier()
         : (entityPlayerSP.isPotionActive(Potion.digSlowdown) ? 8 + entityPlayerSP.getActivePotionEffect(Potion.digSlowdown).getAmplifier() * 2 : 6);
   }

   public void onFloatEvent10(FloatEvent10 floatEvent10) {
      if (this.thirdPersonBlockAnimation.getBooleanValue()) {
         floatEvent10.setFloatValue(0.0F);
      }
   }

   public void applyBlockTransform() {
      GlStateManager.translate(-0.5F, 0.2F, 0.0F);
      GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
   }

   public void onIBakedModelItemCameraTransformsTransformTypeItemStackEvent1(IBakedModelItemCameraTransformsTransformTypeItemStackEvent1 bakedModelItemCameraTransformsTransformTypeItemStackEvent1) {
      if ((Boolean)this.thirdPersonItemPositions.getValue() && bakedModelItemCameraTransformsTransformTypeItemStackEvent1.getTransformType() == TransformType.THIRD_PERSON) {
         bakedModelItemCameraTransformsTransformTypeItemStackEvent1.setFlag(true);
         EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
         GlStateManager.translate(-0.01, 0.002, 5.0E-4);
         GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.scale(1.0F, 1.0F, 1.0F);
         if (bakedModelItemCameraTransformsTransformTypeItemStackEvent1.getItemStack().getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock)bakedModelItemCameraTransformsTransformTypeItemStackEvent1.getItemStack().getItem()).getBlock();
            if (block instanceof BlockCarpet || block instanceof BlockSnow) {
               GlStateManager.translate(0.01, 0.0, 0.0);
               GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(5.0F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scale(1.0F, 1.0F, 1.0F);
            } else if (block instanceof BlockFence) {
               GlStateManager.translate(0.01, 0.0, 0.0);
               GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(5.0F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scale(1.0F, 1.0F, 1.0F);
            } else if (block instanceof BlockFenceGate) {
               GlStateManager.translate(0.01, 0.0, 0.0);
               GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(5.0F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scale(1.0F, 1.0F, 1.0F);
            } else if (block instanceof BlockChest || block instanceof BlockEnderChest) {
               GlStateManager.translate(0.01, 0.0, 0.0);
               GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(5.0F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scale(1.0F, 1.0F, 1.0F);
               if (thePlayer.isSneaking()) {
                  GlStateManager.translate(0.0, -0.35, 0.0);
               }
            } else if (block instanceof BlockLadder
               || block instanceof BlockPane
               || block instanceof BlockRail
               || block instanceof BlockRailPowered
               || block instanceof BlockRailDetector
               || block instanceof BlockVine
               || block instanceof BlockWeb
               || block instanceof BlockLever
               || block instanceof BlockBush
               || block instanceof BlockTorch
               || block instanceof BlockTripWireHook) {
               GlStateManager.translate(0.004, 0.15, -0.02);
               GlStateManager.rotate(-15.0F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(-15.0F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotate(193.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scale(1.0, 1.0, 1.0);
            } else if (block instanceof BlockFurnace || block instanceof BlockDispenser || block instanceof BlockPumpkin) {
               GlStateManager.translate(0.01, 0.0, 0.0);
               GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(5.0F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scale(1.0F, 1.0F, 1.0F);
            } else if (block instanceof BlockBanner) {
               GlStateManager.translate(0.01, 0.0, 0.0);
               GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(5.0F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scale(1.0F, 1.0F, 1.0F);
            } else if (block instanceof BlockButton) {
               GlStateManager.translate(0.01, 0.0, 0.0);
               GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(5.0F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scale(1.0F, 1.0F, 1.0F);
            } else if (block != null) {
               GlStateManager.translate(0.01, 0.0, 0.0);
               GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(5.0F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scale(1.0F, 1.0F, 1.0F);
            } else {
               itemStack = null;
            }
         } else if (bakedModelItemCameraTransformsTransformTypeItemStackEvent1.getItemStack().getItem() instanceof ItemBow) {
            GlStateManager.translate(0.03, -0.0225, -0.05);
            GlStateManager.rotate(-13.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-10.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(8.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(1.0F, 1.0F, 1.0F);
         } else if (bakedModelItemCameraTransformsTransformTypeItemStackEvent1.getItemStack().getItem().shouldRotateAroundWhenRendering()) {
            GlStateManager.translate(0.026, -0.051, 0.03);
            GlStateManager.rotate(2.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(8.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(1.1, 1.1, 1.1);
         } else if (bakedModelItemCameraTransformsTransformTypeItemStackEvent1.getItemStack().getItem().isFull3D()) {
            GlStateManager.translate(0.004, 0.027, 0.06);
            GlStateManager.rotate(-2.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(6.5F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(1.1, 1.1, 1.1);
         } else if (bakedModelItemCameraTransformsTransformTypeItemStackEvent1.getItemStack().getItem() instanceof ItemSkull) {
            GlStateManager.translate(0.0, 0.0, 0.0);
            GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(15.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(1.0F, 1.0F, 1.0F);
         } else if (bakedModelItemCameraTransformsTransformTypeItemStackEvent1.getItemStack().getItem() != null) {
            GlStateManager.translate(0.004, 0.15, -0.02);
            GlStateManager.rotate(-15.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-15.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(193.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(1.0, 1.0, 1.0);
         } else {
            itemStack = null;
         }
      }
   }

   public boolean shouldUseCustomTransform(ItemStack itemStack) {
      switch (itemStack.getItemUseAction()) {
         case EAT:
         case DRINK:
            if (!this.consumeAnimation.getBooleanValue()) {
               return true;
            }
            break;
         case BLOCK:
            if (!this.blockAnimation.getBooleanValue()) {
               return true;
            }
            break;
         case BOW:
            if (!this.bowPullback.getBooleanValue()) {
               return true;
            }
            break;
         case NONE:
            if (!this.firstPersonItemPositions.getBooleanValue()) {
               return true;
            }
      }

      GlStateManager.translate(0.58800083F, 0.36999986F, -0.77000016F);
      GlStateManager.translate(0.0F, -0.3F, 0.0F);
      GlStateManager.scale(1.5F, 1.5F, 1.5F);
      GlStateManager.rotate(50.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(335.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(-0.9375F, -0.0625F, 0.0F);
      GlStateManager.scale(-2.0F, 2.0F, -2.0F);
      if (this.mc.getRenderItem().shouldRenderItemIn3D(itemStack)) {
         GlStateManager.scale(0.58823526F, 0.58823526F, 0.58823526F);
         GlStateManager.rotate(-25.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0F, -0.25F, -0.125F);
         GlStateManager.scale(0.5F, 0.5F, 0.5F);
         return true;
      } else {
         GlStateManager.scale(0.5F, 0.5F, 0.5F);
         return false;
      }
   }

   public void onFloatEvent2(FloatEvent2 floatEvent2) {
      if (this.mc.getRenderViewEntity() == this.mc.thePlayer) {
         if (this.smoothSneaking.getBooleanValue()) {
            float f = this.prevEyeHeight + (this.eyeHeight - this.prevEyeHeight) * floatEvent2.getPartialTicks();
            floatEvent2.setEyeHeight(f);
         }
      }
   }

   public AnimationsMod() {
      super("Animations");
      this.setDescription("Set visuals to legacy and modern versions alike.");
      this.setLabel("1.7 <-> 1.8", 1.0F);
      this.setAuthors("OrangeMarshall (Original)", "Sk1er (Revised)", "Wyvest and Mixces (Overflow Animations)", "Microextent (2D Items)");
      this.setKeywords("1.7 Visuals", "Old Animations");
      this.setCategories(ModCategory.MISC, ModCategory.COMBAT);
      this.positionSettings = new SettingGroup(this, "Position Settings").changeValue(true);
      this.firstPersonItemPositions = new Setting(this, "First Person Item Positions")
         .changeValue(true)
         .setDescription("Change all item models to be in the same position as 1.7.")
         .setToggleTexts("1.8", "1.7")
         .withParents(this.positionSettings);
      this.thirdPersonItemPositions = new Setting(this, "Third Person Item Positions")
         .changeValue(true)
         .setDescription("Change all item models to be in the same position as 1.7.")
         .setToggleTexts("1.8", "1.7")
         .withParents(this.positionSettings);
      this.itemTransformations = new Setting(this, "Item Transformations")
         .changeValue(true)
         .setDescription("Change all item models to be in the same position as 1.7.")
         .setToggleTexts("1.8", "1.7")
         .withParents(this.positionSettings);
      this.bowPullback = new Setting(this, "Bow Pullback")
         .changeValue(true)
         .setDescription("Change the bow pullback animation to be like 1.7.")
         .setToggleTexts("1.8", "1.7")
         .withParents(this.positionSettings);
      this.blockAnimation = new Setting(this, "Block Animation")
         .changeValue(true)
         .setDescription("Change the sword block animation to be like 1.7.")
         .setToggleTexts("1.8", "1.7")
         .withParents(this.positionSettings);
      this.rodPosition = new Setting(this, "Rod Position")
         .changeValue(true)
         .setDescription("Change all item models to be in the same position as 1.7.")
         .setToggleTexts("1.8", "1.7")
         .withParents(this.positionSettings);
      this.thirdPersonBlockAnimation = new Setting(this, "3rd Person Block Animation")
         .changeValue(true)
         .setDescription("Change the 3rd person blocking animation to be like 1.7.")
         .setToggleTexts("1.8", "1.7")
         .withParents(this.positionSettings);
      this.interactionSettings = new SettingGroup(this, "Interaction Settings").changeValue(true);
      this.consumeAnimation = new Setting(this, "Consume Animation")
         .changeValue(true)
         .setDescription("Change the eating and drinking animation to look like 1.7.")
         .setToggleTexts("1.8", "1.7")
         .withParents(this.interactionSettings);
      this.blockHittingAnimation = new Setting(this, "Block-Hitting Animation")
         .changeValue(true)
         .setDescription("Makes block hitting look much smoother, like it did in 1.7.")
         .setToggleTexts("1.8", "1.7")
         .withParents(this.interactionSettings);
      this.smoothSneaking = new Setting(this, "Smooth Sneaking")
         .setDescription("Makes the transition between sneaking/not sneaking smooth.\\n§eCombine with longer unsneak to match 1.7.")
         .changeValue(true)
         .withParents(this.interactionSettings);
      this.longerUnsneak = new Setting(this, "Longer Unsneak")
         .changeValue(true)
         .setDescription("Makes moving up take longer than moving down\\n§eCombine with smooth sneaking to match 1.7.")
         .withHierarchyParents(this.smoothSneaking);
      this.punchingDuringUsage = new Setting(this, "Punching During Usage")
         .changeValue(true)
         .setDescription("Allows you to punch blocks whilst using an item.\\n§eVisual only.")
         .setToggleTexts("1.8", "1.7")
         .withParents(this.interactionSettings);
      this.punchingDuringUsageParticles = new Setting(this, "Punching During Usage Particles")
         .changeValue(true)
         .setDescription("Renders particles when punching during usage.\\n§eVisual only.")
         .setToggleTexts("1.8", "1.7")
         .withParents(this.interactionSettings);
      this.itemSwitchingAnimation = new Setting(this, "Item Switching Animation")
         .changeValue(true)
         .setDescription("Stop held items from playing the switching animation when right clicking on blocks.")
         .setToggleTexts("1.8", "1.7")
         .withParents(this.interactionSettings);
      this.oldEnchantmentGlint = new Setting(this, "Old Enchantment Glint")
         .changeValue(true)
         .setDescription("Renders the enchantment glint in the style of 1.7's glint")
         .setToggleTexts("1.8", "1.7")
         .withParents(this.interactionSettings);
      this.hitColorArmor = new Setting(this, "Hit Color Armor")
         .changeValue(true)
         .setDescription("1.7: Make the hit color apply to the armor.\n1.8: Make the hit color only apply to the player skin.")
         .setOnUpdate(obj -> CheatBreaker.getInstance().getModManager().hitColorMod.affectArmor.changeValueNotify(obj, false))
         .setToggleTexts("1.8", "1.7")
         .withParents(this.interactionSettings);
      this.hitColorArmorGlint = new Setting(this, "Hit Color Armor Glint")
         .changeValue(true)
         .setToggleTexts("1.8", "1.7")
         .withHierarchyParents(this.hitColorArmor);
      this.hitColorBrightness = new Setting(this, "Hit Color Brightness")
         .changeValue(true)
         .setDescription("Set the hit color to use 1.7's brightness system, darkening the hit color.")
         .setToggleTexts("1.8", "1.7")
         .withParents(this.interactionSettings);
      this.missPenaltyBehavior = new Setting(this, "Miss Penalty Behavior")
         .changeValue("Swing and Particles")
         .setStringValues("Do Nothing", "Show Swing", "Swing and Particles")
         .withParents(this.interactionSettings);
      this.itemVisualSettings = new SettingGroup(this, "Item Visual Settings").changeValue(true);
      this.droppedItemDirection = new Setting(this, "Dropped Item Direction")
         .changeValue("Still Bob")
         .setDescription("Changes how dropped items face the player")
         .setStringValues("Default", "Still Bob", "Face Player")
         .withParents(this.itemVisualSettings);
      this.droppedItemSprites = new Setting(this, "Dropped Item Sprites")
         .changeValue(true)
         .setDescription("Makes the items sprites like in 1.7")
         .withParents(this.itemVisualSettings)
         .setOnUpdate(obj -> {
            if (this.droppedItemSprites.getBooleanValue()) {
               this.droppedItemSprites.changeValueNotify(false, false);
            }
         })
         .setCondition(() -> false);
      this.projectileSprites = new Setting(this, "Projectile Sprites")
         .changeValue(true)
         .setDescription("Makes Potions, snowballs, ect. into sprites like 1.7")
         .withParents(this.itemVisualSettings)
         .setOnUpdate(obj -> {
            if (this.projectileSprites.getBooleanValue()) {
               this.projectileSprites.changeValueNotify(false, false);
            }
         })
         .setCondition(() -> false);
      this.hudSettings = new SettingGroup(this, "HUD Settings").changeValue(true);
      this.healthBarFlashing = new Setting(this, "Health Bar Flashing")
         .changeValue(true)
         .setDescription("Stops your health bar flashing when you take damage.")
         .setToggleTexts("1.8", "1.7")
         .withParents(this.hudSettings);
      this.addEventHandler(
         CustomEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.class,
         this::onCustomEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent
      );
      this.addEventHandler(
         IBakedModelItemCameraTransformsTransformTypeItemStackEvent2.class, this::onIBakedModelItemCameraTransformsTransformTypeItemStackEvent2
      );
      this.addEventHandler(
         IBakedModelItemCameraTransformsTransformTypeItemStackEvent1.class, this::onIBakedModelItemCameraTransformsTransformTypeItemStackEvent1
      );
      this.addEventHandler(FloatEvent2.class, this::onFloatEvent2);
      this.addEventHandler(ItemRendererItemStackFloatEvent.class, this::onItemRendererItemStackFloatEvent);
      this.addEventHandler(
         EntityLivingBaseIBakedModelItemCameraTransformsTransformTypeItemStackStringEvent.class,
         this::onEntityLivingBaseIBakedModelItemCameraTransformsTransformTypeItemStackStringEvent
      );
      this.addEventHandler(ClientTickEvent.class, this::onClientTickEvent);
      this.addEventHandler(EntityLivingBaseFloatEvent3.class, this::onEntityLivingBaseFloatEvent3);
      this.addEventHandler(SimpleEvent25.class, this::onSimpleEvent25);
      this.addEventHandler(SimpleEvent35.class, this::onSimpleEvent35);
      this.addEventHandler(IBakedModelFloatEvent1.class, this::onIBakedModelFloatEvent1);
      this.addEventHandler(IBakedModelFloatEvent2.class, this::onIBakedModelFloatEvent2);
      this.addEventHandler(SimpleEvent23.class, this::onSimpleEvent23);
      this.addEventHandler(FloatEvent10.class, this::onFloatEvent10);
      this.addEventHandler(EntityPlayerSPItemStackBooleanFloatIntEvent.class, this::onEntityPlayerSPItemStackBooleanFloatIntEvent);
      this.addEventHandler(EntityLivingBaseFloatEvent2.class, this::onEntityLivingBaseFloatEvent2);
      this.setEnabledByDefault(true);
   }

   public void renderEnchantmentGlint(IBakedModel bakedModel, String str, boolean flag, RenderItem renderItem, ItemStack itemStack) {
      if ((!Config.isCustomItems() || CustomItems.isUseGlint()) && (!Config.isShaders() || !Shaders.isShadowPass)) {
         GlStateManager.depthMask(false);
         GlStateManager.depthFunc(514);
         GlStateManager.disableLighting();
         GlStateManager.blendFunc(768, 1);
         this.mc.getTextureManager().bindTexture(enchantedItemGlintPng);
         if (Config.isShaders() && !flag) {
            ShadersRender.renderEnchantedGlintBegin();
         }

         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scale(8.0, 8.0, 8.0);
         GlStateManager.translate((float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F, 0.0F, 0.0F);
         GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         int n = -8372020;
         ItemStackStringIntEvent itemStackStringIntEvent = new ItemStackStringIntEvent(str, itemStack, n);
         CheatBreaker.getInstance().getEventBus().handleEvent(itemStackStringIntEvent);
         n = itemStackStringIntEvent.getIntValue();
         renderItem.renderModel(bakedModel, n);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scale(8.0, 8.0, 8.0);
         GlStateManager.translate((float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F, 0.0F, 0.0F);
         GlStateManager.rotate(150.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         renderItem.renderModel(bakedModel, n);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
         GlStateManager.blendFunc(770, 771);
         GlStateManager.enableLighting();
         GlStateManager.depthFunc(515);
         GlStateManager.depthMask(true);
         this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
         if (Config.isShaders() && !flag) {
            ShadersRender.renderEnchantedGlintEnd();
         }
      }
   }

   public void transformFirstPersonItem(float f, float f2) {
      GlStateManager.translate(0.56F, -0.52F - (1.0F - f) * 0.6F, -0.72F);
      GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
      float sin = MathHelper.sin(f2 * f2 * (float) Math.PI);
      float sin2 = MathHelper.sin(MathHelper.sqrt_float(f2) * (float) Math.PI);
      GlStateManager.rotate(-sin * 20.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-sin2 * 20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(-sin2 * 80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(0.4F, 0.4F, 0.4F);
   }

   public void onEntityLivingBaseFloatEvent3(EntityLivingBaseFloatEvent3 entityLivingBaseFloatEvent3) {
      if ((Boolean)this.punchingDuringUsage.getValue() && this.mc.thePlayer.capabilities.allowEdit && entityLivingBaseFloatEvent3.getEntityLivingBase() instanceof EntityPlayerSP) {
         entityLivingBaseFloatEvent3.setFloatValue1(this.getSwingProgress(entityLivingBaseFloatEvent3.getPartialTickTime()));
      }
   }

   public void doConsumeAnimation(ItemStack itemStack, int n, float f) {
      if (this.consumeAnimation.getBooleanValue()) {
         float f2 = n - f + 1.0F;
         float f3 = 1.0F - f2 / itemStack.getMaxItemUseDuration();
         float f4 = 1.0F - f3;
         f4 = f4 * f4 * f4;
         f4 = f4 * f4 * f4;
         f4 = f4 * f4 * f4;
         float f5 = 1.0F - f4;
         GlStateManager.translate(0.0F, MathHelper.abs(MathHelper.cos(f2 / 4.0F * (float) Math.PI) * 0.1F) * (f3 > 0.2 ? 1 : 0), 0.0F);
         GlStateManager.translate(f5 * 0.6F, -f5 * 0.5F, 0.0F);
         GlStateManager.rotate(f5 * 90.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(f5 * 10.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(f5 * 30.0F, 0.0F, 0.0F, 1.0F);
      } else {
         float f2 = n - f + 1.0F;
         float f3 = f2 / itemStack.getMaxItemUseDuration();
         float abs = MathHelper.abs(MathHelper.cos(f2 / 4.0F * (float) Math.PI) * 0.1F);
         if (f3 >= 0.8F) {
            abs = 0.0F;
         }

         GlStateManager.translate(0.0F, abs, 0.0F);
         float f4 = 1.0F - (float)Math.pow(f3, 27.0);
         GlStateManager.translate(f4 * 0.6F, f4 * -0.5F, f4 * 0.0F);
         GlStateManager.rotate(f4 * 90.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(f4 * 10.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(f4 * 30.0F, 0.0F, 0.0F, 1.0F);
      }
   }

   public void onCustomEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent(
      CustomEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent
   ) {
      if (customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getHudElementType() == HudElementType.ARMOR) {
         if (this.hitColorArmorGlint.getBooleanValue()) {
            EntityLivingBase entity = (EntityLivingBase)customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getEntity();
            if (entity.hurtTime > 0 || entity.deathTime > 0) {
               customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.setFlag(true);
            }
         }
      } else if ((Boolean)this.oldEnchantmentGlint.getValue()) {
         if (customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getRenderItem() == null) {
            return;
         }

         customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.setFlag(true);
         if (customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getTransformType() == TransformType.GUI) {
            switch (customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getText()) {
               case "inventory":
                  if (!this.isGlintEnabled(CheatBreaker.getInstance().getModManager().enchantmentGlintMod.inventory.glint)) {
                     return;
                  }
               case "dropped":
               case "held":
               default:
                  this.renderEnchantmentGlintCopy(customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getBakedModel(), customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getText(), customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.renderItemGui(), customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getRenderItem(), customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getItemStack());
            }
         } else {
            switch (customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getText()) {
               case "inventory":
                  break;
               case "dropped":
                  if (!this.isGlintEnabled(CheatBreaker.getInstance().getModManager().enchantmentGlintMod.dropped.glint)) {
                     return;
                  }
                  break;
               case "held":
                  if (!this.isGlintEnabled(CheatBreaker.getInstance().getModManager().enchantmentGlintMod.held.glint)) {
                     return;
                  }
                  break;
               default:
                  if (!this.isGlintEnabled(CheatBreaker.getInstance().getModManager().enchantmentGlintMod.thrown.glint)) {
                     return;
                  }
            }

            this.renderEnchantmentGlint(customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getBakedModel(), customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getText(), customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.renderItemGui(), customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getRenderItem(), customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getItemStack());
         }
      }
   }

   public void onSimpleEvent23(SimpleEvent23 simpleEvent23) {
      if (this.healthBarFlashing.getBooleanValue()) {
         simpleEvent23.setFlag(true);
      }
   }

   public void onItemRendererItemStackFloatEvent(ItemRendererItemStackFloatEvent itemRendererItemStackFloatEvent) {
      float prevEquippedProgress = itemRendererItemStackFloatEvent.getPrevEquippedProgress();
      float equippedProgress = itemRendererItemStackFloatEvent.getEquippedProgress();
      float partialTicks = itemRendererItemStackFloatEvent.getPartialTicks();
      ItemStack itemStack = itemRendererItemStackFloatEvent.getItemStack();
      ItemRenderer itemRenderer = itemRendererItemStackFloatEvent.getItemRenderer();
      float f = prevEquippedProgress + (equippedProgress - prevEquippedProgress) * partialTicks;
      if (itemStack != null) {
         Item item = itemStack.getItem();
         if (item != Items.filled_map && !this.mc.getRenderItem().shouldRenderItemIn3D(itemStack)) {
            EnumAction itemUseAction = itemStack.getItemUseAction();
            if ((item != Items.fishing_rod || this.rodPosition.getBooleanValue())
               && (itemUseAction != EnumAction.NONE || this.firstPersonItemPositions.getBooleanValue())
               && (itemUseAction != EnumAction.BLOCK || this.blockAnimation.getBooleanValue())
               && (itemUseAction != EnumAction.BOW || this.bowPullback.getBooleanValue())) {
               EntityPlayerSP thePlayer = this.mc.thePlayer;
               float f2 = thePlayer.prevRotationPitch + (thePlayer.rotationPitch - thePlayer.prevRotationPitch) * partialTicks;
               GlStateManager.pushMatrix();
               GlStateManager.rotate(f2, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotate(thePlayer.prevRotationYaw + (thePlayer.rotationYaw - thePlayer.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
               RenderHelper.enableStandardItemLighting();
               GlStateManager.popMatrix();
               float f3 = thePlayer.prevRenderArmPitch + (thePlayer.renderArmPitch - thePlayer.prevRenderArmPitch) * partialTicks;
               float f4 = thePlayer.prevRenderArmYaw + (thePlayer.renderArmYaw - thePlayer.prevRenderArmYaw) * partialTicks;
               GlStateManager.rotate((thePlayer.rotationPitch - f3) * 0.1F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotate((thePlayer.rotationYaw - f4) * 0.1F, 0.0F, 1.0F, 0.0F);
               GlStateManager.enableRescaleNormal();
               if (item instanceof ItemCloth) {
                  GlStateManager.enableBlend();
                  GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
               }

               int combinedLight = this.mc.theWorld.getCombinedLight(new BlockPos(thePlayer.posX, thePlayer.posY + thePlayer.getEyeHeight(), thePlayer.posZ), 0);
               float f5 = combinedLight & 65535;
               float f6 = combinedLight >> 16;
               OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f5, f6);
               int colorFromItemStack = item.getColorFromItemStack(itemStack, 0);
               float f7 = (colorFromItemStack >> 16 & 0xFF) / 255.0F;
               float f8 = (colorFromItemStack >> 8 & 0xFF) / 255.0F;
               float f9 = (colorFromItemStack & 0xFF) / 255.0F;
               GlStateManager.color(f7, f8, f9, 1.0F);
               GlStateManager.pushMatrix();
               int itemInUseCount = thePlayer.getItemInUseCount();
               float swingProgress = this.getSwingProgress(partialTicks);
               boolean flag = false;
               if (this.punchingDuringUsage.getBooleanValue() && itemInUseCount <= 0 && this.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                  boolean flag2 = itemUseAction == EnumAction.BLOCK;
                  boolean flag3 = false;
                  if (item instanceof ItemFood && thePlayer.canEat(((ItemFood)item).isAlwaysEdible())) {
                     flag3 = itemUseAction == EnumAction.EAT || itemUseAction == EnumAction.DRINK;
                  }

                  if (flag2 || flag3) {
                     flag = true;
                  }
               }

               if ((itemInUseCount > 0 || flag) && itemUseAction != EnumAction.NONE && this.mc.thePlayer.isUsingItem()) {
                  switch (itemUseAction) {
                     case EAT:
                     case DRINK:
                        this.doConsumeAnimation(itemStack, itemInUseCount, partialTicks);
                        this.transformFirstPersonItem(f, this.blockHittingAnimation.getBooleanValue() ? swingProgress : 0.0F);
                        break;
                     case BLOCK:
                        this.transformFirstPersonItem(f, this.blockHittingAnimation.getBooleanValue() ? swingProgress : 0.0F);
                        this.applyBlockTransform();
                        break;
                     case BOW:
                        this.transformFirstPersonItem(f, this.blockHittingAnimation.getBooleanValue() ? swingProgress : 0.0F);
                        this.applyBowTransform(itemStack, itemInUseCount, partialTicks);
                  }
               } else {
                  this.applySwingTranslation(swingProgress);
                  this.transformFirstPersonItem(f, swingProgress);
               }

               if (item.shouldRotateAroundWhenRendering()) {
                  GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
               }

               if (this.itemTransformations.getBooleanValue() && !this.shouldUseCustomTransform(itemStack)) {
                  itemRenderer.renderItem(thePlayer, itemStack, TransformType.NONE);
               } else {
                  itemRenderer.renderItem(thePlayer, itemStack, TransformType.FIRST_PERSON);
               }

               GlStateManager.popMatrix();
               if (item instanceof ItemCloth) {
                  GlStateManager.disableBlend();
               }

               GlStateManager.disableRescaleNormal();
               RenderHelper.disableStandardItemLighting();
               itemRendererItemStackFloatEvent.setFlag(true);
            }
         }
      }
   }

   public boolean isGlintEnabled(Setting setting) {
      return !CheatBreaker.getInstance().getModManager().enchantmentGlintMod.isEnabled() || setting.getBooleanValue();
   }

   public void applyHandTransform() {
      EntityPlayerSP thePlayer = this.mc.thePlayer;
      if (this.thirdPersonBlockAnimation.getBooleanValue()) {
         GlStateManager.translate(-0.15F, -0.2F, 0.0F);
         GlStateManager.rotate(70.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translate(0.119F, 0.2F, -0.024F);
      }

      if (this.firstPersonItemPositions.getBooleanValue()) {
         GlStateManager.rotate(20.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(10.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.translate(0.072, 0.035, 0.0);
         if (thePlayer.isSneaking()) {
            GlStateManager.translate(-0.033, -0.0035, 0.05);
         }
      }
   }

   public void onSimpleEvent25(SimpleEvent25 simpleEvent25) {
      if (this.punchingDuringUsage.getBooleanValue()) {
         this.mc.playerController.setIsHittingBlock(false);
      }
   }

   public void tickEyeHeight() {
      this.prevEyeHeight = this.eyeHeight;
      EntityPlayerSP thePlayer = this.mc.thePlayer;
      if (thePlayer != null && (thePlayer.isSneaking() || this.longerUnsneak.getBooleanValue())) {
         if (thePlayer.isSneaking()) {
            this.eyeHeight = 1.54F;
         } else {
            if (this.eyeHeight < 1.62F) {
               this.eyeHeight = 1.62F - 0.4F * (1.62F - this.eyeHeight);
            }
         }
      } else {
         this.eyeHeight = 1.62F;
      }
   }

   public void onEntityLivingBaseFloatEvent2(EntityLivingBaseFloatEvent2 entityLivingBaseFloatEvent2) {
      if (this.hitColorBrightness.getBooleanValue()) {
         entityLivingBaseFloatEvent2.setBrightness(entityLivingBaseFloatEvent2.getBrightness() * 0.73F);
      }
   }

   public void renderEnchantmentGlintCopy(IBakedModel bakedModel, String str, boolean flag, RenderItem renderItem, ItemStack itemStack) {
      if ((!Config.isCustomItems() || CustomItems.isUseGlint()) && (!Config.isShaders() || !Shaders.isShadowPass)) {
         GlStateManager.depthMask(false);
         GlStateManager.depthFunc(514);
         GlStateManager.disableLighting();
         GlStateManager.blendFunc(768, 1);
         this.mc.getTextureManager().bindTexture(enchantedItemGlintPng);
         if (Config.isShaders() && !flag) {
            ShadersRender.renderEnchantedGlintBegin();
         }

         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scale(8.0, 8.0, 8.0);
         GlStateManager.translate((float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F, 0.0F, 0.0F);
         GlStateManager.rotate(70.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         int n = -8372020;
         ItemStackStringIntEvent itemStackStringIntEvent = new ItemStackStringIntEvent(str, itemStack, n);
         CheatBreaker.getInstance().getEventBus().handleEvent(itemStackStringIntEvent);
         n = itemStackStringIntEvent.getIntValue();
         renderItem.renderModel(bakedModel, n);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scale(8.0, 8.0, 8.0);
         GlStateManager.translate((float)(Minecraft.getSystemTime() % 4873L) / 3000.0F / 8.0F, 0.0F, 0.0F);
         GlStateManager.rotate(70.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         renderItem.renderModel(bakedModel, n);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
         GlStateManager.blendFunc(770, 771);
         GlStateManager.enableLighting();
         GlStateManager.depthFunc(515);
         GlStateManager.depthMask(true);
         this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
         if (Config.isShaders() && !flag) {
            ShadersRender.renderEnchantedGlintEnd();
         }
      }
   }

   public void tickSwing() {
      EntityPlayerSP thePlayer = this.mc.thePlayer;
      if (thePlayer != null) {
         this.prevSwingProgress = this.swingProgress;
         int swingDuration = this.getSwingDuration(thePlayer);
         if (this.swingProgressInt >= swingDuration) {
            this.swingProgressInt = 0;
            this.swinging = false;
         }

         if (this.punchingDuringUsage.getBooleanValue()
            && this.mc.gameSettings.keyBindAttack.isKeyDown()
            && thePlayer.capabilities.allowEdit
            && this.mc.gameSettings.keyBindUseItem.isKeyDown()
            && this.mc.objectMouseOver != null
            && this.mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
            if (this.punchingDuringUsageParticles.getBooleanValue()) {
               this.mc.effectRenderer.addBlockHitEffects(this.mc.objectMouseOver.getBlockPos(), this.mc.objectMouseOver.sideHit);
            }

            if (!this.swinging || this.swingProgressInt >= swingDuration >> 1 || this.swingProgressInt < 0) {
               this.swingProgressInt = -1;
               this.swinging = true;
            }
         }

         if (this.swinging) {
            this.swingProgressInt++;
         } else {
            this.swingProgressInt = 0;
         }

         this.swingProgress = (float)this.swingProgressInt / swingDuration;
      }
   }
}
