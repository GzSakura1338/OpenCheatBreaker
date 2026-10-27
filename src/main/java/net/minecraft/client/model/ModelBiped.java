package net.minecraft.client.model;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.emote.AbstractEmote;
import com.cheatbreaker.client.emote.EmoteManager;
import com.cheatbreaker.client.event.AbstractClientPlayerCustomModelPlayerFloatEvent;
import com.cheatbreaker.client.event.EventPhase;
import com.cheatbreaker.client.event.FloatEvent10;
import java.util.UUID;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelBiped extends ModelBase {
   public float[] floatArray = new float[42];
   public ModelRenderer bipedHead;
   public ModelRenderer bipedHeadwear;
   public ModelRenderer bipedBody;
   public ModelRenderer bipedRightArm;
   public ModelRenderer bipedLeftArm;
   public ModelRenderer bipedRightLeg;
   public int heldItemLeft;
   public int heldItemRight;
   public boolean isSneak;
   public boolean aimedBow;
   public boolean flag;
   public UUID uuid;
   public ModelRenderer bipedLeftLeg;

   public ModelBiped() {
      this(0.0F);
   }

   public ModelBiped(float modelSize) {
      this(modelSize, 0.0F, 64, 32);
   }

   public ModelBiped(float modelSize, float f, int textureWidthIn, int textureHeightIn) {
      this.textureWidth = textureWidthIn;
      this.textureHeight = textureHeightIn;
      this.bipedHead = new ModelRenderer(this, 0, 0);
      this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize);
      this.bipedHead.setRotationPoint(0.0F, 0.0F + f, 0.0F);
      this.bipedHeadwear = new ModelRenderer(this, 32, 0);
      this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize + 0.5F);
      this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + f, 0.0F);
      this.bipedBody = new ModelRenderer(this, 16, 16);
      this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize);
      this.bipedBody.setRotationPoint(0.0F, 0.0F + f, 0.0F);
      this.bipedRightArm = new ModelRenderer(this, 40, 16);
      this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
      this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + f, 0.0F);
      this.bipedLeftArm = new ModelRenderer(this, 40, 16);
      this.bipedLeftArm.mirror = true;
      this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
      this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + f, 0.0F);
      this.bipedRightLeg = new ModelRenderer(this, 0, 16);
      this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
      this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F + f, 0.0F);
      this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
      this.bipedLeftLeg.mirror = true;
      this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
      this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F + f, 0.0F);
   }

   @Override
   public void render(Entity entityIn, float f, float f2, float f3, float f4, float f5, float scale) {
      if (this.flag) {
         this.run();
      }

      this.setRotationAngles(f, f2, f3, f4, f5, scale, entityIn);
      if (entityIn instanceof AbstractClientPlayer && !(this instanceof ModelPlayer)) {
         this.handleEntity2(entityIn);
      }

      if (entityIn instanceof AbstractClientPlayer && this instanceof ModelPlayer) {
         CheatBreaker.getInstance()
            .getEventBus()
            .handleEvent(new AbstractClientPlayerCustomModelPlayerFloatEvent(EventPhase.START, (AbstractClientPlayer)entityIn, (ModelPlayer)this, scale));
      }

      GlStateManager.pushMatrix();
      if (this.isChild) {
         float f6 = 2.0F;
         GlStateManager.scale(1.5F / f6, 1.5F / f6, 1.5F / f6);
         GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
         this.bipedHead.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scale(1.0F / f6, 1.0F / f6, 1.0F / f6);
         GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
         this.bipedBody.render(scale);
         this.bipedRightArm.render(scale);
         this.bipedLeftArm.render(scale);
         this.bipedRightLeg.render(scale);
         this.bipedLeftLeg.render(scale);
         this.bipedHeadwear.render(scale);
      } else {
         if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
         }

         if (CheatBreaker.isAprilFoolsEnabled() && CheatBreaker.getInstance().getKeybindSettings().aprilFools.getBooleanValue()) {
            float floatValue = CheatBreaker.getInstance().getKeybindSettings().theScale.getFloatValue();
            GlStateManager.pushMatrix();
            GlStateManager.scale(floatValue, floatValue, floatValue);
            this.bipedHead.render(scale);
            GlStateManager.popMatrix();
         } else {
            this.bipedHead.render(scale);
         }

         this.bipedBody.render(scale);
         this.bipedRightArm.render(scale);
         this.bipedLeftArm.render(scale);
         this.bipedRightLeg.render(scale);
         this.bipedLeftLeg.render(scale);
         if (CheatBreaker.isAprilFoolsEnabled() && CheatBreaker.getInstance().getKeybindSettings().aprilFools.getBooleanValue()) {
            float floatValue = CheatBreaker.getInstance().getKeybindSettings().theScale.getFloatValue();
            GlStateManager.pushMatrix();
            GlStateManager.scale(floatValue, floatValue, floatValue);
            this.bipedHeadwear.render(scale);
            GlStateManager.popMatrix();
         } else {
            this.bipedHeadwear.render(scale);
         }
      }

      GlStateManager.popMatrix();
   }

   @Override
   public void setRotationAngles(
      float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn
   ) {
      this.bipedHead.rotateAngleY = netHeadYaw / (180.0F / (float)Math.PI);
      this.bipedHead.rotateAngleX = headPitch / (180.0F / (float)Math.PI);
      this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
      this.bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
      this.bipedLeftArm.rotateAngleZ = 0.0F;
      this.bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      this.bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
      this.bipedRightLeg.rotateAngleY = 0.0F;
      this.bipedLeftLeg.rotateAngleY = 0.0F;
      if (this.isRiding) {
         this.bipedRightArm.rotateAngleX += (float) (-Math.PI / 5);
         this.bipedLeftArm.rotateAngleX += (float) (-Math.PI / 5);
         this.bipedRightLeg.rotateAngleX = (float) (-Math.PI * 2.0 / 5.0);
         this.bipedLeftLeg.rotateAngleX = (float) (-Math.PI * 2.0 / 5.0);
         this.bipedRightLeg.rotateAngleY = (float) (Math.PI / 10);
         this.bipedLeftLeg.rotateAngleY = (float) (-Math.PI / 10);
      }

      if (this.heldItemLeft != 0) {
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - (float) (Math.PI / 10) * this.heldItemLeft;
      }

      this.bipedRightArm.rotateAngleY = 0.0F;
      this.bipedRightArm.rotateAngleZ = 0.0F;
      switch (this.heldItemRight) {
         case 0:
         case 2:
         default:
            break;
         case 1:
            this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - (float) (Math.PI / 10) * this.heldItemRight;
            break;
         case 3:
            this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - (float) (Math.PI / 10) * this.heldItemRight;
            FloatEvent10 floatEvent10 = new FloatEvent10((float) (-Math.PI / 6));
            CheatBreaker.getInstance().getEventBus().handleEvent(floatEvent10);
            this.bipedRightArm.rotateAngleY = floatEvent10.getFloatValue();
      }

      this.bipedLeftArm.rotateAngleY = 0.0F;
      if (this.swingProgress > -9990.0F) {
         float swingProgress = this.swingProgress;
         this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0F) * 0.2F;
         this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
         this.bipedRightArm.rotateAngleY = this.bipedRightArm.rotateAngleY + this.bipedBody.rotateAngleY;
         this.bipedLeftArm.rotateAngleY = this.bipedLeftArm.rotateAngleY + this.bipedBody.rotateAngleY;
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX + this.bipedBody.rotateAngleY;
         swingProgress = 1.0F - this.swingProgress;
         swingProgress *= swingProgress;
         swingProgress *= swingProgress;
         swingProgress = 1.0F - swingProgress;
         float sin = MathHelper.sin(swingProgress * (float) Math.PI);
         float f = MathHelper.sin(this.swingProgress * (float) Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
         this.bipedRightArm.rotateAngleX = (float)(this.bipedRightArm.rotateAngleX - (sin * 1.2 + f));
         this.bipedRightArm.rotateAngleY = this.bipedRightArm.rotateAngleY + this.bipedBody.rotateAngleY * 2.0F;
         this.bipedRightArm.rotateAngleZ = this.bipedRightArm.rotateAngleZ + MathHelper.sin(this.swingProgress * (float) Math.PI) * -0.4F;
      }

      EmoteManager emoteManager = CheatBreaker.getInstance().getEmoteManager();
      AbstractEmote abstractEmote = emoteManager.getAbstractEmote();
      if (!this.isSneak && (abstractEmote == null || !abstractEmote.getText().equalsIgnoreCase("Naruto Run"))) {
         this.bipedBody.rotateAngleX = 0.0F;
         this.bipedRightLeg.rotationPointZ = 0.1F;
         this.bipedLeftLeg.rotationPointZ = 0.1F;
         this.bipedRightLeg.rotationPointY = 12.0F;
         this.bipedLeftLeg.rotationPointY = 12.0F;
         this.bipedHead.rotationPointY = 0.0F;
      } else {
         this.bipedBody.rotateAngleX = 0.5F;
         this.bipedRightArm.rotateAngleX += 0.4F;
         this.bipedLeftArm.rotateAngleX += 0.4F;
         this.bipedRightLeg.rotationPointZ = 4.0F;
         this.bipedLeftLeg.rotationPointZ = 4.0F;
         this.bipedRightLeg.rotationPointY = 9.0F;
         this.bipedLeftLeg.rotationPointY = 9.0F;
         this.bipedHead.rotationPointY = 1.0F;
      }

      this.bipedRightArm.rotateAngleZ = this.bipedRightArm.rotateAngleZ + (MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F);
      this.bipedLeftArm.rotateAngleZ = this.bipedLeftArm.rotateAngleZ - (MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F);
      this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX + MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
      this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX - MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
      if (this.aimedBow) {
         float f = 0.0F;
         float f2 = 0.0F;
         this.bipedRightArm.rotateAngleZ = 0.0F;
         this.bipedLeftArm.rotateAngleZ = 0.0F;
         this.bipedRightArm.rotateAngleY = -(0.1F - f * 0.6F) + this.bipedHead.rotateAngleY;
         this.bipedLeftArm.rotateAngleY = 0.1F - f * 0.6F + this.bipedHead.rotateAngleY + 0.4F;
         this.bipedRightArm.rotateAngleX = (float) (-Math.PI / 2) + this.bipedHead.rotateAngleX;
         this.bipedLeftArm.rotateAngleX = (float) (-Math.PI / 2) + this.bipedHead.rotateAngleX;
         this.bipedRightArm.rotateAngleX -= f * 1.2F - f2 * 0.4F;
         this.bipedLeftArm.rotateAngleX -= f * 1.2F - f2 * 0.4F;
         this.bipedRightArm.rotateAngleZ = this.bipedRightArm.rotateAngleZ + (MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F);
         this.bipedLeftArm.rotateAngleZ = this.bipedLeftArm.rotateAngleZ - (MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F);
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX + MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX - MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
      }

      copyModelAngles(this.bipedHead, this.bipedHeadwear);
   }

   @Override
   public void setModelAttributes(ModelBase model) {
      super.setModelAttributes(model);
      if (model instanceof ModelBiped) {
         ModelBiped model2 = (ModelBiped)model;
         this.heldItemLeft = model2.heldItemLeft;
         this.heldItemRight = model2.heldItemRight;
         this.isSneak = model2.isSneak;
         this.aimedBow = model2.aimedBow;
      }
   }

   public void setInvisible(boolean invisible) {
      this.bipedHead.showModel = invisible;
      this.bipedHeadwear.showModel = invisible;
      this.bipedBody.showModel = invisible;
      this.bipedRightArm.showModel = invisible;
      this.bipedLeftArm.showModel = invisible;
      this.bipedRightLeg.showModel = invisible;
      this.bipedLeftLeg.showModel = invisible;
   }

   public void postRenderArm(float scale) {
      this.bipedRightArm.postRender(scale);
   }

   public void handleEntity(Entity entity) {
      if (!CheatBreaker.getInstance().getEmoteManager().isEmoting(entity)) {
         this.uuid = null;
      } else {
         ModelRenderer[] modelRendererArray = this.getModelRendererArray();

         for (int i = 0; i < modelRendererArray.length; i++) {
            ModelRenderer modelRenderer = modelRendererArray[i];
            int n = i * 6;
            this.floatArray[n] = modelRenderer.rotateAngleX;
            this.floatArray[n + 1] = modelRenderer.rotateAngleY;
            this.floatArray[n + 2] = modelRenderer.rotateAngleZ;
            this.floatArray[n + 3] = modelRenderer.offsetX;
            this.floatArray[n + 4] = modelRenderer.offsetY;
            this.floatArray[n + 5] = modelRenderer.offsetZ;
         }

         this.uuid = entity.getUniqueID();
      }
   }

   public void run() {
      for (ModelRenderer modelRenderer : this.getModelRendererArray()) {
         modelRenderer.rotateAngleZ = 0.0F;
         modelRenderer.offsetX = 0.0F;
         modelRenderer.offsetY = 0.0F;
         modelRenderer.offsetZ = 0.0F;
      }

      this.flag = false;
   }

   public ModelRenderer[] getModelRendererArray() {
      return new ModelRenderer[]{
         this.bipedHead, this.bipedHeadwear, this.bipedBody, this.bipedRightArm, this.bipedLeftArm, this.bipedRightLeg, this.bipedLeftLeg
      };
   }

   public void handleEntity2(Entity entity) {
      ModelBiped modelBiped = CheatBreaker.getInstance().getEmoteManager().getModelBackups().get(entity.getUniqueID());
      if (modelBiped != null && entity.getUniqueID().equals(modelBiped.uuid)) {
         ModelRenderer[] modelRendererArray = this.getModelRendererArray();

         for (int i = 0; i < modelRendererArray.length; i++) {
            ModelRenderer modelRenderer = modelRendererArray[i];
            int n = i * 6;
            modelRenderer.rotateAngleX = modelBiped.floatArray[n];
            modelRenderer.rotateAngleY = modelBiped.floatArray[n + 1];
            modelRenderer.rotateAngleZ = modelBiped.floatArray[n + 2];
            modelRenderer.offsetX = modelBiped.floatArray[n + 3];
            modelRenderer.offsetY = modelBiped.floatArray[n + 4];
            modelRenderer.offsetZ = modelBiped.floatArray[n + 5];
         }

         this.flag = true;
      }
   }
}
