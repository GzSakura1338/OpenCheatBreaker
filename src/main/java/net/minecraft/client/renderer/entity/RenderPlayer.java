package net.minecraft.client.renderer.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.EntityLivingBaseDoubleEvent2;
import com.cheatbreaker.client.event.EntityLivingBaseDoubleEvent3;
import com.cheatbreaker.client.event.EntityLivingBaseDoubleEvent4;
import com.cheatbreaker.client.event.SimpleEvent11;
import com.cheatbreaker.client.event.SimpleEvent30;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;

public class RenderPlayer extends RendererLivingEntity<AbstractClientPlayer> {
   public boolean smallArms;

   public RenderPlayer(RenderManager renderManager) {
      this(renderManager, false);
   }

   public RenderPlayer(RenderManager renderManager, boolean useSmallArms) {
      super(renderManager, new ModelPlayer(0.0F, useSmallArms), 0.5F);
      this.smallArms = useSmallArms;
      this.addLayer(new LayerBipedArmor(this));
      this.addLayer(new LayerHeldItem(this));
      this.addLayer(new LayerArrow(this));
      this.addLayer(new LayerDeadmau5Head(this));
      this.addLayer(new LayerCape(this));
      this.addLayer(new LayerCustomHead(this.getMainModel().bipedHead));
   }

   public ModelPlayer getMainModel() {
      return (ModelPlayer)super.getMainModel();
   }

   public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (!entity.isUser() || this.renderManager.livingPlayer == entity) {
         double d = y;
         if (entity.isSneaking() && !(entity instanceof EntityPlayerSP)) {
            d = y - 0.125;
         }

         SimpleEvent11 simpleEvent11 = new SimpleEvent11(entity, this, partialTicks, x, d, z);
         CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent11);
         if (simpleEvent11.isFlag()) {
            return;
         }

         this.setModelVisibilities(entity);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         super.doRender(entity, x, d, z, entityYaw, partialTicks);
         GlStateManager.disableBlend();
         CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent30(entity, this, partialTicks, x, d, z));
      }
   }

   public void setModelVisibilities(AbstractClientPlayer clientPlayer) {
      ModelPlayer mainModel = this.getMainModel();
      if (clientPlayer.isSpectator()) {
         mainModel.setInvisible(false);
         mainModel.bipedHead.showModel = true;
         mainModel.bipedHeadwear.showModel = true;
      } else {
         ItemStack currentItem = clientPlayer.inventory.getCurrentItem();
         mainModel.setInvisible(true);
         mainModel.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);
         mainModel.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
         mainModel.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
         mainModel.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
         mainModel.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
         mainModel.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
         mainModel.heldItemLeft = 0;
         mainModel.aimedBow = false;
         mainModel.isSneak = clientPlayer.isSneaking();
         if (currentItem == null) {
            mainModel.heldItemRight = 0;
         } else {
            mainModel.heldItemRight = 1;
            if (clientPlayer.getItemInUseCount() > 0) {
               EnumAction itemUseAction = currentItem.getItemUseAction();
               if (itemUseAction == EnumAction.BLOCK) {
                  mainModel.heldItemRight = 3;
               } else if (itemUseAction == EnumAction.BOW) {
                  mainModel.aimedBow = true;
               }
            }
         }
      }
   }

   public ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
      return entity.getLocationSkin();
   }

   @Override
   public void transformHeldFull3DItemLayer() {
      GlStateManager.translate(0.0F, 0.1875F, 0.0F);
   }

   public void preRenderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime) {
      float f = 0.9375F;
      GlStateManager.scale(f, f, f);
   }

   public void renderOffsetLivingLabel(AbstractClientPlayer entityIn, double x, double y, double z, String str, float f, double d) {
      EntityLivingBaseDoubleEvent2 entityLivingBaseDoubleEvent2 = new EntityLivingBaseDoubleEvent2(entityIn, x, y, z, f);
      CheatBreaker.getInstance().getEventBus().handleEvent(entityLivingBaseDoubleEvent2);
      if (!entityLivingBaseDoubleEvent2.isFlag()) {
         if (entityLivingBaseDoubleEvent2.getY() != y) {
            y = entityLivingBaseDoubleEvent2.getY();
         }

         if (d < 100.0) {
            Scoreboard worldScoreboard = entityIn.getWorldScoreboard();
            ScoreObjective objectiveInDisplaySlot = worldScoreboard.getObjectiveInDisplaySlot(2);
            if (objectiveInDisplaySlot != null) {
               Score valueFromObjective = worldScoreboard.getValueFromObjective(entityIn.getName(), objectiveInDisplaySlot);
               this.renderLivingLabel(entityIn, valueFromObjective.getScorePoints() + " " + objectiveInDisplaySlot.getDisplayName(), x, y, z, 64);
               y += this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * f;
            }
         }

         EntityLivingBaseDoubleEvent3 entityLivingBaseDoubleEvent3 = new EntityLivingBaseDoubleEvent3(entityIn, x, y, z, f);
         CheatBreaker.getInstance().getEventBus().handleEvent(entityLivingBaseDoubleEvent3);
         if (!entityLivingBaseDoubleEvent3.isFlag()) {
            if (entityLivingBaseDoubleEvent3.getY() != y) {
               y = entityLivingBaseDoubleEvent3.getY();
            }

            super.renderOffsetLivingLabel(entityIn, x, y, z, str, f, d);
            EntityLivingBaseDoubleEvent4 entityLivingBaseDoubleEvent4 = new EntityLivingBaseDoubleEvent4(entityIn, x, y, z, f);
            CheatBreaker.getInstance().getEventBus().handleEvent(entityLivingBaseDoubleEvent4);
            if (!entityLivingBaseDoubleEvent4.isFlag()) {
               if (entityLivingBaseDoubleEvent4.getY() != y) {
                  y = entityLivingBaseDoubleEvent4.getY();
               }

               if (CheatBreaker.getInstance().getNetHandler().getNametagOverrides().containsKey(entityIn.getUniqueID())) {
                  for (String str2 : CheatBreaker.getInstance().getNetHandler().getNametagOverrides().get(entityIn.getUniqueID())) {
                     if (!str2.contains(entityIn.getNameClear())) {
                        y += this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * f;
                        this.renderLivingLabel(entityIn, str2, x, y, z, 64);
                     }
                  }
               }
            }
         }
      }
   }

   public void renderRightArm(AbstractClientPlayer clientPlayer) {
      float f = 1.0F;
      GlStateManager.color(f, f, f);
      ModelPlayer mainModel = this.getMainModel();
      this.setModelVisibilities(clientPlayer);
      mainModel.swingProgress = 0.0F;
      mainModel.isSneak = false;
      mainModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
      mainModel.renderRightArm();
   }

   public void renderLeftArm(AbstractClientPlayer clientPlayer) {
      float f = 1.0F;
      GlStateManager.color(f, f, f);
      ModelPlayer mainModel = this.getMainModel();
      this.setModelVisibilities(clientPlayer);
      mainModel.isSneak = false;
      mainModel.swingProgress = 0.0F;
      mainModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
      mainModel.renderLeftArm();
   }

   public void renderLivingAt(AbstractClientPlayer entityLivingBaseIn, double x, double y, double z) {
      if (entityLivingBaseIn.isEntityAlive() && entityLivingBaseIn.isPlayerSleeping()) {
         super.renderLivingAt(
            entityLivingBaseIn, x + entityLivingBaseIn.renderOffsetX, y + entityLivingBaseIn.renderOffsetY, z + entityLivingBaseIn.renderOffsetZ
         );
      } else {
         super.renderLivingAt(entityLivingBaseIn, x, y, z);
      }
   }

   public void rotateCorpse(AbstractClientPlayer bat, float f, float f2, float partialTicks) {
      if (bat.isEntityAlive() && bat.isPlayerSleeping()) {
         GlStateManager.rotate(bat.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(this.getDeathMaxRotation(bat), 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
      } else {
         super.rotateCorpse(bat, f, f2, partialTicks);
      }
   }
}
