package net.minecraft.client.model;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.cosmetic.CosmeticType;
import com.cheatbreaker.client.cosmetic.WingModel;
import com.cheatbreaker.client.emote.AbstractEmote;
import com.cheatbreaker.client.emote.BlockModelParser;
import com.cheatbreaker.client.emote.EmoteManager;
import com.cheatbreaker.client.event.AbstractClientPlayerCustomModelPlayerFloatEvent;
import com.cheatbreaker.client.event.EventPhase;
import com.cheatbreaker.client.mod.impl.HitColorMod;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelBiped extends ModelBase {
    public boolean flag = false;
    public float[] floatArray = new float[42];
    public ModelRenderer bipedCloak;
    public ModelRenderer bipedEars;
    public ModelRenderer bipedHead;
    public ModelRenderer bipedHeadwear;
    public ModelRenderer bipedBody;
    public ModelRenderer bipedRightArm;
    public ModelRenderer bipedLeftArm;
    public int heldItemLeft;
    public int heldItemRight;
    public boolean isSneak;
    public boolean aimedBow;
    public ModelRenderer bipedRightLeg;
    public ModelRenderer bipedLeftLeg;
    public UUID uuid;
    public WingModel wingModel;
    public boolean flag2;

    public ModelBiped() {
        this(0.0F);
    }

    public ModelBiped(float f) {
        this(f, 0.0F, 64, 32);
    }

    public ModelBiped(float f, float f2, int n, int n2) {
        this.textureWidth = n;
        this.textureHeight = n2;
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.bipedCloak = new ModelRenderer(this, 0, 0);
        this.bipedCloak.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, f);
        this.textureWidth = n;
        this.textureHeight = n2;
        this.bipedEars = new ModelRenderer(this, 24, 0);
        this.bipedEars.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, f);
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f);
        this.bipedHead.setRotationPoint(0.0F, 0.0F + f2, 0.0F);
        this.bipedHeadwear = new ModelRenderer(this, 32, 0);
        this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f + 0.5F);
        this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + f2, 0.0F);
        this.bipedBody = new ModelRenderer(this, 16, 16);
        this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, f);
        this.bipedBody.setRotationPoint(0.0F, 0.0F + f2, 0.0F);
        this.bipedRightArm = new ModelRenderer(this, 40, 16);
        this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, f);
        this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + f2, 0.0F);
        this.bipedLeftArm = new ModelRenderer(this, 40, 16);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
        this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + f2, 0.0F);
        this.bipedRightLeg = new ModelRenderer(this, 0, 16);
        this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
        this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F + f2, 0.0F);
        this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
        this.bipedLeftLeg.mirror = true;
        this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
        this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F + f2, 0.0F);
        this.wingModel = new WingModel();
    }

    @Override
    public void render(Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        if (this.flag2) {
            this.run();
        }

        this.setRotationAngles(f, f2, f3, f4, f5, f6, entity);
        if (entity instanceof AbstractClientPlayer && !(this instanceof ModelPlayer)) {
            this.handleEntity2(entity);
        }

        if (entity instanceof AbstractClientPlayer && this instanceof ModelPlayer) {
            CheatBreaker.getInstance()
                .getEventBus()
                .handleEvent(new AbstractClientPlayerCustomModelPlayerFloatEvent(EventPhase.START, (AbstractClientPlayer)entity, (ModelPlayer)this, f6));
        }

        if (this.isChild) {
            float f7 = 2.0F;
            GL11.glPushMatrix();
            GL11.glScalef(1.5F / f7, 1.5F / f7, 1.5F / f7);
            GL11.glTranslatef(0.0F, 16.0F * f6, 0.0F);
            this.bipedHead.render(f6);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f7, 1.0F / f7, 1.0F / f7);
            GL11.glTranslatef(0.0F, 24.0F * f6, 0.0F);
            this.bipedBody.render(f6);
            this.bipedRightArm.render(f6);
            this.bipedLeftArm.render(f6);
            this.bipedRightLeg.render(f6);
            this.bipedLeftLeg.render(f6);
            this.bipedHeadwear.render(f6);
            GL11.glPopMatrix();
        } else {
            GL11.glPushMatrix();
            if (entity.isSneaking()) {
                GL11.glTranslatef(0.0F, 0.2F, 0.0F);
            }

            if (CheatBreaker.isAprilFoolsEnabled() && CheatBreaker.getInstance().getKeybindSettings().aprilFools.getBooleanValue()) {
                float floatValue = CheatBreaker.getInstance().getKeybindSettings().theScale.getFloatValue();
                GL11.glPushMatrix();
                GL11.glScalef(floatValue, floatValue, floatValue);
                this.bipedHead.render(f6);
                GL11.glPopMatrix();
            } else {
                this.bipedHead.render(f6);
            }

            this.bipedBody.render(f6);
            this.bipedRightArm.render(f6);
            this.bipedLeftArm.render(f6);
            this.bipedRightLeg.render(f6);
            this.bipedLeftLeg.render(f6);
            if (CheatBreaker.isAprilFoolsEnabled() && CheatBreaker.getInstance().getKeybindSettings().aprilFools.getBooleanValue()) {
                float floatValue = CheatBreaker.getInstance().getKeybindSettings().theScale.getFloatValue();
                GL11.glPushMatrix();
                GL11.glScalef(floatValue, floatValue, floatValue);
                this.bipedHeadwear.render(f6);
                GL11.glPopMatrix();
            } else {
                this.bipedHeadwear.render(f6);
            }

            GL11.glPopMatrix();
        }

        HitColorMod hitColorMod = CheatBreaker.getInstance().getModManager().hitColorMod;
        boolean isFlag = RendererLivingEntity.isFlag();
        EntityPlayer entityPlayer;
        if (!this.flag
            && (!isFlag || hitColorMod.checkSetting(hitColorMod.affectWings))
            && entity instanceof EntityPlayer
            && (entityPlayer = (EntityPlayer)entity).hasCosmetic2()
            && entityPlayer.getCosmetic2().getCosmeticType() == CosmeticType.WINGS
            && (Boolean)CheatBreaker.getInstance().getKeybindSettings().showCheatbreakerWings.getValue()) {
            this.wingModel.render(entity, f6, entityPlayer.getCosmetic2().getScale(), entityPlayer.getCosmetic2().getTexture());
            if (entity instanceof AbstractClientPlayer) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(((AbstractClientPlayer)entity).getLocationSkin());
            }
        }

        BlockModelParser blockModelParser = !this.flag && (!isFlag || hitColorMod.checkSetting(hitColorMod.affectHats)) ? BlockModelParser.forEntity(entity) : null;
        if (blockModelParser != null) {
            GL11.glPushMatrix();
            if (entity.isSneaking()) {
                GL11.glTranslatef(0.0F, 0.2F, 0.0F);
            }

            blockModelParser.renderOnHead(this.bipedHead, f6);
            GL11.glPopMatrix();
            if (entity instanceof AbstractClientPlayer) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(((AbstractClientPlayer)entity).getLocationSkin());
            }
        }

        if (entity instanceof AbstractClientPlayer) {
            if (this instanceof ModelPlayer) {
                CheatBreaker.getInstance()
                    .getEventBus()
                    .handleEvent(new AbstractClientPlayerCustomModelPlayerFloatEvent(EventPhase.END, (AbstractClientPlayer)entity, (ModelPlayer)this, f6));
                this.handleEntity(entity);
            }

            this.bipedRightLeg.rotateAngleZ = 0.0F;
            this.bipedLeftLeg.rotateAngleZ = 0.0F;
            this.bipedLeftLeg.offsetX = 0.0F;
            this.bipedRightLeg.offsetX = 0.0F;
            this.bipedBody.rotateAngleZ = 0.0F;
            this.bipedHead.rotateAngleZ = 0.0F;
            this.bipedHeadwear.rotateAngleZ = 0.0F;
        }
    }

    @Override
    public void setRotationAngles(float f, float f2, float f3, float f4, float f5, float f6, Entity entity) {
        this.bipedHead.rotateAngleY = f4 / (180.0F / (float)Math.PI);
        this.bipedHead.rotateAngleX = f5 / (180.0F / (float)Math.PI);
        this.bipedHeadwear.rotateAngleY = this.bipedHead.rotateAngleY;
        this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX;
        this.bipedRightArm.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 2.0F * f2 * 0.5F;
        this.bipedLeftArm.rotateAngleX = MathHelper.cos(f * 0.6662F) * 2.0F * f2 * 0.5F;
        this.bipedRightArm.rotateAngleZ = 0.0F;
        this.bipedLeftArm.rotateAngleZ = 0.0F;
        this.bipedRightLeg.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f2;
        this.bipedLeftLeg.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * f2;
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

        if (this.heldItemRight != 0) {
            this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - (float) (Math.PI / 10) * this.heldItemRight;
        }

        this.bipedRightArm.rotateAngleY = 0.0F;
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
            float f7 = MathHelper.sin(this.swingProgress * (float) Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
            this.bipedRightArm.rotateAngleX = (float)(this.bipedRightArm.rotateAngleX - (sin * 1.2 + f7));
            this.bipedRightArm.rotateAngleY = this.bipedRightArm.rotateAngleY + this.bipedBody.rotateAngleY * 2.0F;
            this.bipedRightArm.rotateAngleZ = MathHelper.sin(this.swingProgress * (float) Math.PI) * -0.4F;
        }

        EmoteManager emoteManager = CheatBreaker.getInstance().getEmoteManager();
        AbstractEmote abstractEmote = emoteManager.getAbstractEmote();
        if (!this.isSneak && (abstractEmote == null || !abstractEmote.getDisplayName().equalsIgnoreCase("Naruto Run"))) {
            this.bipedBody.rotateAngleX = 0.0F;
            this.bipedRightLeg.rotationPointZ = 0.1F;
            this.bipedLeftLeg.rotationPointZ = 0.1F;
            this.bipedRightLeg.rotationPointY = 12.0F;
            this.bipedLeftLeg.rotationPointY = 12.0F;
            this.bipedHead.rotationPointY = 0.0F;
            this.bipedHeadwear.rotationPointY = 0.0F;
        } else {
            this.bipedBody.rotateAngleX = 0.5F;
            this.bipedRightArm.rotateAngleX += 0.4F;
            this.bipedLeftArm.rotateAngleX += 0.4F;
            this.bipedRightLeg.rotationPointZ = 4.0F;
            this.bipedLeftLeg.rotationPointZ = 4.0F;
            this.bipedRightLeg.rotationPointY = 9.0F;
            this.bipedLeftLeg.rotationPointY = 9.0F;
            this.bipedHead.rotationPointY = 1.0F;
            this.bipedHeadwear.rotationPointY = 1.0F;
        }

        this.bipedRightArm.rotateAngleZ = this.bipedRightArm.rotateAngleZ + (MathHelper.cos(f3 * 0.09F) * 0.05F + 0.05F);
        this.bipedLeftArm.rotateAngleZ = this.bipedLeftArm.rotateAngleZ - (MathHelper.cos(f3 * 0.09F) * 0.05F + 0.05F);
        this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX + MathHelper.sin(f3 * 0.067F) * 0.05F;
        this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX - MathHelper.sin(f3 * 0.067F) * 0.05F;
        if (this.aimedBow) {
            float f7 = 0.0F;
            float f8 = 0.0F;
            this.bipedRightArm.rotateAngleZ = 0.0F;
            this.bipedLeftArm.rotateAngleZ = 0.0F;
            this.bipedRightArm.rotateAngleY = -(0.1F - f7 * 0.6F) + this.bipedHead.rotateAngleY;
            this.bipedLeftArm.rotateAngleY = 0.1F - f7 * 0.6F + this.bipedHead.rotateAngleY + 0.4F;
            this.bipedRightArm.rotateAngleX = (float) (-Math.PI / 2) + this.bipedHead.rotateAngleX;
            this.bipedLeftArm.rotateAngleX = (float) (-Math.PI / 2) + this.bipedHead.rotateAngleX;
            this.bipedRightArm.rotateAngleX -= f7 * 1.2F - f8 * 0.4F;
            this.bipedLeftArm.rotateAngleX -= f7 * 1.2F - f8 * 0.4F;
            this.bipedRightArm.rotateAngleZ = this.bipedRightArm.rotateAngleZ + (MathHelper.cos(f3 * 0.09F) * 0.05F + 0.05F);
            this.bipedLeftArm.rotateAngleZ = this.bipedLeftArm.rotateAngleZ - (MathHelper.cos(f3 * 0.09F) * 0.05F + 0.05F);
            this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX + MathHelper.sin(f3 * 0.067F) * 0.05F;
            this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX - MathHelper.sin(f3 * 0.067F) * 0.05F;
        }
    }

    public void renderEars(float f) {
        this.bipedEars.rotateAngleY = this.bipedHead.rotateAngleY;
        this.bipedEars.rotateAngleX = this.bipedHead.rotateAngleX;
        this.bipedEars.rotationPointX = 0.0F;
        this.bipedEars.rotationPointY = 0.0F;
        this.bipedEars.render(f);
    }

    public void renderCloak(float f) {
        this.bipedCloak.render(f);
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

    public ModelRenderer[] getModelRendererArray() {
        return new ModelRenderer[]{
            this.bipedHead, this.bipedHeadwear, this.bipedBody, this.bipedRightArm, this.bipedLeftArm, this.bipedRightLeg, this.bipedLeftLeg
        };
    }

    public void run() {
        for (ModelRenderer modelRenderer : this.getModelRendererArray()) {
            modelRenderer.rotateAngleZ = 0.0F;
            modelRenderer.offsetX = 0.0F;
            modelRenderer.offsetY = 0.0F;
            modelRenderer.offsetZ = 0.0F;
        }

        this.flag2 = false;
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

            this.flag2 = true;
        }
    }
}
