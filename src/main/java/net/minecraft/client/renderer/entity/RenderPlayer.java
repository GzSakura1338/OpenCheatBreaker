package net.minecraft.client.renderer.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.SimpleEvent11;
import com.cheatbreaker.client.event.SimpleEvent30;
import com.cheatbreaker.client.mod.impl.HitColorMod;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

public class RenderPlayer extends RendererLivingEntity {
    public static ResourceLocation steveTextures = new ResourceLocation("textures/entity/steve.png");
    public ModelPlayer modelPlayer = (ModelPlayer)this.mainModel;
    public ModelBiped modelArmorChestplate = new ModelBiped(1.0F);
    public ModelBiped modelArmor = new ModelBiped(0.5F);

    public RenderPlayer() {
        this(false);
    }

    public int shouldRenderPass(AbstractClientPlayer abstractClientPlayer, int n, float f) {
        ItemStack armorItemInSlot = abstractClientPlayer.inventory.armorItemInSlot(3 - n);
        if (armorItemInSlot != null) {
            Item item = armorItemInSlot.getItem();
            if (item instanceof ItemArmor) {
                ItemArmor itemArmor = (ItemArmor)item;
                this.bindTexture(RenderBiped.findResourceLocation(itemArmor, n));
                ModelBiped modelBiped = n == 2 ? this.modelArmor : this.modelArmorChestplate;
                modelBiped.bipedHead.showModel = n == 0;
                modelBiped.bipedHeadwear.showModel = n == 0;
                modelBiped.bipedBody.showModel = n == 1 || n == 2;
                modelBiped.bipedRightArm.showModel = n == 1;
                modelBiped.bipedLeftArm.showModel = n == 1;
                modelBiped.bipedRightLeg.showModel = n == 2 || n == 3;
                modelBiped.bipedLeftLeg.showModel = n == 2 || n == 3;
                this.setRenderPassModel(modelBiped);
                modelBiped.swingProgress = this.mainModel.swingProgress;
                modelBiped.isRiding = this.mainModel.isRiding;
                modelBiped.isChild = this.mainModel.isChild;
                if (itemArmor.getArmorMaterial() == ArmorMaterial.CLOTH) {
                    int color = itemArmor.getColor(armorItemInSlot);
                    float f2 = (color >> 16 & 0xFF) / 255.0F;
                    float f3 = (color >> 8 & 0xFF) / 255.0F;
                    float f4 = (color & 0xFF) / 255.0F;
                    GL11.glColor3f(f2, f3, f4);
                    if (armorItemInSlot.isItemEnchanted()) {
                        return 31;
                    }

                    return 16;
                }

                GL11.glColor3f(1.0F, 1.0F, 1.0F);
                if (armorItemInSlot.isItemEnchanted()) {
                    return 15;
                }

                return 1;
            }
        }

        return -1;
    }

    public void handleEntityLiving(AbstractClientPlayer abstractClientPlayer, int n, float f) {
        ItemStack armorItemInSlot = abstractClientPlayer.inventory.armorItemInSlot(3 - n);
        if (armorItemInSlot != null) {
            Item item = armorItemInSlot.getItem();
            if (item instanceof ItemArmor) {
                this.bindTexture(RenderBiped.createResourceLocation((ItemArmor)item, n, "overlay"));
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
            }
        }
    }

    public void doRender(AbstractClientPlayer abstractClientPlayer, double d, double d2, double d3, float f, float f2) {
        double d4 = d2 - abstractClientPlayer.yOffset;
        if (abstractClientPlayer.isSneaking() && !(abstractClientPlayer instanceof EntityPlayerSP)) {
            d4 -= 0.125;
        }

        SimpleEvent11 simpleEvent11 = new SimpleEvent11(abstractClientPlayer, this, f2, d, d4, d3);
        CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent11);
        if (!simpleEvent11.isCancelled()) {
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            GL11.glEnable(3042);
            GL14.glBlendFuncSeparate(770, 771, 1, 0);
            ItemStack currentItem = abstractClientPlayer.inventory.getCurrentItem();
            this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelPlayer.heldItemRight = currentItem != null ? 1 : 0;
            if (currentItem != null && abstractClientPlayer.getItemInUseCount() > 0) {
                EnumAction itemUseAction = currentItem.getItemUseAction();
                if (itemUseAction == EnumAction.block) {
                    this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelPlayer.heldItemRight = 3;
                } else if (itemUseAction == EnumAction.bow) {
                    this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelPlayer.aimedBow = true;
                }
            }

            this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelPlayer.isSneak = abstractClientPlayer.isSneaking();
            this.handleEntityPlayer(abstractClientPlayer);
            super.doRender(abstractClientPlayer, d, d4, d3, f, f2);
            GL11.glDisable(3042);
            this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelPlayer.aimedBow = false;
            this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelPlayer.isSneak = false;
            this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelPlayer.heldItemRight = 0;
            CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent30(abstractClientPlayer, this, f2, d, d4, d3));
        }
    }

    public ResourceLocation getEntityTexture(AbstractClientPlayer abstractClientPlayer) {
        return abstractClientPlayer.getLocationSkin();
    }

    public void renderEquippedItems(AbstractClientPlayer abstractClientPlayer, float f) {
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        super.renderEquippedItems(abstractClientPlayer, f);
        super.renderArrowsStuckInEntity(abstractClientPlayer, f);
        ItemStack armorItemInSlot = abstractClientPlayer.inventory.armorItemInSlot(3);
        if (armorItemInSlot != null) {
            GL11.glPushMatrix();
            this.modelPlayer.bipedHead.postRender(0.0625F);
            if (armorItemInSlot.getItem() instanceof ItemBlock) {
                if (RenderBlocks.renderItemIn3d(Block.getBlockFromItem(armorItemInSlot.getItem()).getRenderType())) {
                    float f2 = 0.625F;
                    GL11.glTranslatef(0.0F, -0.25F, 0.0F);
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(f2, -f2, -f2);
                }

                this.renderManager.itemRenderer.renderItem(abstractClientPlayer, armorItemInSlot, 0);
            } else if (armorItemInSlot.getItem() == Items.skull) {
                float f2 = 1.0625F;
                GL11.glScalef(f2, -f2, -f2);
                GameProfile gameProfile = null;
                if (armorItemInSlot.hasTagCompound()) {
                    NBTTagCompound tagCompound = armorItemInSlot.getTagCompound();
                    if (tagCompound.hasKey("SkullOwner", 10)) {
                        gameProfile = NBTUtil.readGameProfileFromNBT(tagCompound.getCompoundTag("SkullOwner"));
                    } else if (tagCompound.hasKey("SkullOwner", 8) && !StringUtils.isNullOrEmpty(tagCompound.getString("SkullOwner"))) {
                        gameProfile = new GameProfile(null, tagCompound.getString("SkullOwner"));
                    }
                }

                TileEntitySkullRenderer.instance.render(-0.5F, 0.0F, -0.5F, 1, 180.0F, armorItemInSlot.getMetadata(), gameProfile);
            }

            GL11.glPopMatrix();
        }

        if (abstractClientPlayer.getCommandSenderName().equals("deadmau5") && abstractClientPlayer.hasSkin()) {
            this.bindTexture(abstractClientPlayer.getLocationSkin());

            for (int i = 0; i < 2; i++) {
                float f2 = abstractClientPlayer.prevRotationYaw
                    + (abstractClientPlayer.rotationYaw - abstractClientPlayer.prevRotationYaw) * f
                    - (abstractClientPlayer.prevRenderYawOffset + (abstractClientPlayer.renderYawOffset - abstractClientPlayer.prevRenderYawOffset) * f);
                float f3 = abstractClientPlayer.prevRotationPitch + (abstractClientPlayer.rotationPitch - abstractClientPlayer.prevRotationPitch) * f;
                GL11.glPushMatrix();
                GL11.glRotatef(f2, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(f3, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.375F * (i * 2 - 1), 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.375F, 0.0F);
                GL11.glRotatef(-f3, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
                float f4 = 1.3333334F;
                GL11.glScalef(f4, f4, f4);
                this.modelPlayer.renderEars(0.0625F);
                GL11.glPopMatrix();
            }
        }

        this.render(abstractClientPlayer, f);
        ItemStack currentItem = abstractClientPlayer.inventory.getCurrentItem();
        if (currentItem != null) {
            GL11.glPushMatrix();
            this.modelPlayer.process(0.0625F);
            GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
            if (abstractClientPlayer.fishEntity != null) {
                currentItem = new ItemStack(Items.stick);
            }

            EnumAction enumAction = null;
            if (abstractClientPlayer.getItemInUseCount() > 0) {
                enumAction = currentItem.getItemUseAction();
            }

            if (currentItem.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(currentItem.getItem()).getRenderType())) {
                float f2 = 0.5F;
                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                f2 *= 0.75F;
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-f2, -f2, f2);
            } else if (currentItem.getItem() == Items.bow) {
                float f2 = 0.625F;
                GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
                GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(f2, -f2, f2);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            } else if (currentItem.getItem().isFull3D()) {
                float f2 = 0.625F;
                if (currentItem.getItem().shouldRotateAroundWhenRendering()) {
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                }

                if (abstractClientPlayer.getItemInUseCount() > 0 && enumAction == EnumAction.block) {
                    GL11.glTranslatef(0.05F, 0.0F, -0.1F);
                    GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
                }

                GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
                GL11.glScalef(f2, -f2, f2);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            } else {
                float f2 = 0.375F;
                GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                GL11.glScalef(f2, f2, f2);
                GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
            }

            if (currentItem.getItem().requiresMultipleRenderPasses()) {
                for (int i = 0; i <= 1; i++) {
                    int colorFromItemStack = currentItem.getItem().getColorFromItemStack(currentItem, i);
                    float f2 = (colorFromItemStack >> 16 & 0xFF) / 255.0F;
                    float f3 = (colorFromItemStack >> 8 & 0xFF) / 255.0F;
                    float f4 = (colorFromItemStack & 0xFF) / 255.0F;
                    GL11.glColor4f(f2, f3, f4, 1.0F);
                    this.renderManager.itemRenderer.renderItem(abstractClientPlayer, currentItem, i);
                }
            } else {
                int colorFromItemStack = currentItem.getItem().getColorFromItemStack(currentItem, 0);
                float f2 = (colorFromItemStack >> 16 & 0xFF) / 255.0F;
                float f3 = (colorFromItemStack >> 8 & 0xFF) / 255.0F;
                float f4 = (colorFromItemStack & 0xFF) / 255.0F;
                GL11.glColor4f(f2, f3, f4, 1.0F);
                this.renderManager.itemRenderer.renderItem(abstractClientPlayer, currentItem, 0);
            }

            GL11.glPopMatrix();
        }
    }

    public void preRenderCallback(AbstractClientPlayer abstractClientPlayer, float f) {
        float f2 = 0.9375F;
        GL11.glScalef(f2, f2, f2);
    }

    public void renderOffsetLivingLabel(AbstractClientPlayer abstractClientPlayer, double d, double d2, double d3, String str, float f, double d4) {
        if (d4 < 100.0) {
            Scoreboard worldScoreboard = abstractClientPlayer.getWorldScoreboard();
            ScoreObjective objectiveInDisplaySlot = worldScoreboard.getObjectiveInDisplaySlot(2);
            if (objectiveInDisplaySlot != null) {
                Score valueFromObjective = worldScoreboard.getValueFromObjective(abstractClientPlayer.getCommandSenderName(), objectiveInDisplaySlot);
                if (abstractClientPlayer.isPlayerSleeping()) {
                    this.renderLivingLabel(abstractClientPlayer, valueFromObjective.getScorePoints() + " " + objectiveInDisplaySlot.getDisplayName(), d, d2 - 1.5, d3, 64);
                } else {
                    this.renderLivingLabel(abstractClientPlayer, valueFromObjective.getScorePoints() + " " + objectiveInDisplaySlot.getDisplayName(), d, d2, d3, 64);
                }

                d2 += this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * f;
            }
        }

        super.renderOffsetLivingLabel(abstractClientPlayer, d, d2, d3, str, f, d4);
    }

    public void renderFirstPersonArm(EntityPlayer entityPlayer) {
        float f = 1.0F;
        GL11.glColor3f(f, f, f);
        this.modelPlayer.swingProgress = 0.0F;
        this.handleEntityPlayer(entityPlayer);
        this.modelPlayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, entityPlayer);
        this.modelPlayer.bipedRightArm.render(0.0625F);
        this.modelPlayer.modelRenderer2.render(0.0625F);
    }

    public void renderLivingAt(AbstractClientPlayer abstractClientPlayer, double d, double d2, double d3) {
        if (abstractClientPlayer.isEntityAlive() && abstractClientPlayer.isPlayerSleeping()) {
            super.renderLivingAt(abstractClientPlayer, d + abstractClientPlayer.floatValue5, d2 + abstractClientPlayer.floatValue2, d3 + abstractClientPlayer.floatValue3);
        } else {
            super.renderLivingAt(abstractClientPlayer, d, d2, d3);
        }
    }

    public void rotateCorpse(AbstractClientPlayer abstractClientPlayer, float f, float f2, float f3) {
        if (abstractClientPlayer.isEntityAlive() && abstractClientPlayer.isPlayerSleeping()) {
            GL11.glRotatef(abstractClientPlayer.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.getDeathMaxRotation(abstractClientPlayer), 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
        } else {
            super.rotateCorpse(abstractClientPlayer, f, f2, f3);
        }
    }

    @Override
    public void renderOffsetLivingLabel(EntityLivingBase entityLivingBase, double d, double d2, double d3, String str, float f, double d4) {
        this.renderOffsetLivingLabel((AbstractClientPlayer)entityLivingBase, d, d2, d3, str, f, d4);
    }

    @Override
    public void preRenderCallback(EntityLivingBase entityLivingBase, float f) {
        this.preRenderCallback((AbstractClientPlayer)entityLivingBase, f);
    }

    @Override
    public void handleEntityLiving(EntityLivingBase entityLivingBase, int n, float f) {
        this.handleEntityLiving((AbstractClientPlayer)entityLivingBase, n, f);
    }

    @Override
    public int shouldRenderPass(EntityLivingBase entityLivingBase, int n, float f) {
        return this.shouldRenderPass((AbstractClientPlayer)entityLivingBase, n, f);
    }

    @Override
    public void renderEquippedItems(EntityLivingBase entityLivingBase, float f) {
        this.renderEquippedItems((AbstractClientPlayer)entityLivingBase, f);
    }

    @Override
    public void rotateCorpse(EntityLivingBase entityLivingBase, float f, float f2, float f3) {
        this.rotateCorpse((AbstractClientPlayer)entityLivingBase, f, f2, f3);
    }

    @Override
    public void renderLivingAt(EntityLivingBase entityLivingBase, double d, double d2, double d3) {
        this.renderLivingAt((AbstractClientPlayer)entityLivingBase, d, d2, d3);
    }

    @Override
    public void doRender(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f, float f2) {
        this.doRender((AbstractClientPlayer)entityLivingBase, d, d2, d3, f, f2);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((AbstractClientPlayer)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((AbstractClientPlayer)entity, d, d2, d3, f, f2);
    }

    public void handleEntityPlayer(EntityPlayer entityPlayer) {
        this.modelPlayer.bipedHeadwear.showModel = entityPlayer.checkEnumPlayerModelParts(EnumPlayerModelParts.HAT);
        this.modelPlayer.modelRenderer5.showModel = entityPlayer.checkEnumPlayerModelParts(EnumPlayerModelParts.JACKET);
        this.modelPlayer.modelRenderer3.showModel = entityPlayer.checkEnumPlayerModelParts(EnumPlayerModelParts.LEFT_PANTS_LEG);
        this.modelPlayer.modelRenderer4.showModel = entityPlayer.checkEnumPlayerModelParts(EnumPlayerModelParts.RIGHT_PANTS_LEG);
        this.modelPlayer.modelRenderer.showModel = entityPlayer.checkEnumPlayerModelParts(EnumPlayerModelParts.LEFT_SLEEVE);
        this.modelPlayer.modelRenderer2.showModel = entityPlayer.checkEnumPlayerModelParts(EnumPlayerModelParts.RIGHT_SLEEVE);
    }

    public RenderPlayer(boolean flag) {
        super(new ModelPlayer(0.0F, flag), 0.5F);
    }

    @Override
    public void handleEntityLivingBase(EntityLivingBase entityLivingBase, float f) {
        HitColorMod hitColorMod = CheatBreaker.getInstance().getModManager().hitColorMod;
        if (hitColorMod.checkSetting(hitColorMod.affectCape)) {
            this.render((AbstractClientPlayer)entityLivingBase, f);
        }
    }

    public void render(AbstractClientPlayer abstractClientPlayer, float f) {
        if (abstractClientPlayer.hasCape() && !abstractClientPlayer.isInvisible() && !abstractClientPlayer.getHideCape()) {
            this.bindTexture(abstractClientPlayer.getLocationCape());
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.0F, 0.125F);
            double d = abstractClientPlayer.capeX + (abstractClientPlayer.prevCapeX - abstractClientPlayer.capeX) * f - (abstractClientPlayer.prevPosX + (abstractClientPlayer.posX - abstractClientPlayer.prevPosX) * f);
            double d2 = abstractClientPlayer.capeY + (abstractClientPlayer.prevCapeY - abstractClientPlayer.capeY) * f - (abstractClientPlayer.prevPosY + (abstractClientPlayer.posY - abstractClientPlayer.prevPosY) * f);
            double d3 = abstractClientPlayer.capeZ + (abstractClientPlayer.prevCapeZ - abstractClientPlayer.capeZ) * f - (abstractClientPlayer.prevPosZ + (abstractClientPlayer.posZ - abstractClientPlayer.prevPosZ) * f);
            float f2 = abstractClientPlayer.prevRenderYawOffset + (abstractClientPlayer.renderYawOffset - abstractClientPlayer.prevRenderYawOffset) * f;
            double sin = MathHelper.sin(f2 * (float) Math.PI / 180.0F);
            double d4 = -MathHelper.cos(f2 * (float) Math.PI / 180.0F);
            float f3 = (float)d2 * 10.0F;
            if (f3 < -6.0F) {
                f3 = -6.0F;
            }

            if (f3 > 32.0F) {
                f3 = 32.0F;
            }

            float f4 = (float)(d * sin + d3 * d4) * 100.0F;
            float f5 = (float)(d * d4 - d3 * sin) * 100.0F;
            if (f4 < 0.0F) {
                f4 = 0.0F;
            }

            float f6 = abstractClientPlayer.prevCameraYaw + (abstractClientPlayer.cameraYaw - abstractClientPlayer.prevCameraYaw) * f;
            f3 += MathHelper.sin((abstractClientPlayer.prevDistanceWalkedModified + (abstractClientPlayer.distanceWalkedModified - abstractClientPlayer.prevDistanceWalkedModified) * f) * 6.0F)
                * 32.0F
                * f6;
            if (abstractClientPlayer.isSneaking()) {
                f3 += 25.0F;
            }

            GL11.glRotatef(6.0F + f4 / 2.0F + f3, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(f5 / 2.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-f5 / 2.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            this.modelPlayer.renderCloak(0.0625F);
            GL11.glPopMatrix();
        }
    }
}
