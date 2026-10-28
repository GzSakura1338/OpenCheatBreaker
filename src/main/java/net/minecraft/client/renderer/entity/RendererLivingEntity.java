package net.minecraft.client.renderer.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.EntityLivingBaseRendererLivingEntityDoubleFloatIntEvent;
import com.cheatbreaker.client.event.FloatEvent4;
import com.cheatbreaker.client.mod.impl.EnchantmentGlintMod;
import com.cheatbreaker.client.mod.impl.NametagMod;
import com.cheatbreaker.client.network.PlayerData;
import com.cheatbreaker.client.network.PlayerDataManager;
import java.util.Random;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public abstract class RendererLivingEntity extends Render {
    public static Logger logger = LogManager.getLogger();
    public static ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public Pattern pattern2 = Pattern.compile("(?i)§[0-689A-E]");
    public ModelBase renderPassModel;
    public static boolean flag;
    public ModelBase mainModel;

    public RendererLivingEntity(ModelBase modelBase, float f) {
        this.mainModel = modelBase;
        this.shadowSize = f;
    }

    public void setRenderPassModel(ModelBase modelBase) {
        this.renderPassModel = modelBase;
    }

    public float interpolateRotation(float f, float f2, float f3) {
        float f4 = f2 - f;

        while (f4 < -180.0F) {
            f4 += 360.0F;
        }

        while (f4 >= 180.0F) {
            f4 -= 360.0F;
        }

        return f + f3 * f4;
    }

    public void doRender(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f, float f2) {
        if (CheatBreaker.getInstance().getKeybindSettings().shouldRenderEntity(entityLivingBase)) {
            GL11.glPushMatrix();
            GL11.glDisable(2884);
            this.mainModel.swingProgress = this.getSwingProgress(entityLivingBase, f2);
            if (this.renderPassModel != null) {
                this.renderPassModel.swingProgress = this.mainModel.swingProgress;
            }

            this.mainModel.isRiding = entityLivingBase.isRiding();
            if (this.renderPassModel != null) {
                this.renderPassModel.isRiding = this.mainModel.isRiding;
            }

            this.mainModel.isChild = entityLivingBase.isChild();
            if (this.renderPassModel != null) {
                this.renderPassModel.isChild = this.mainModel.isChild;
            }

            try {
                float interpolateRotation = this.interpolateRotation(entityLivingBase.prevRenderYawOffset, entityLivingBase.renderYawOffset, f2);
                float interpolateRotation2 = this.interpolateRotation(entityLivingBase.prevRotationYawHead, entityLivingBase.rotationYawHead, f2);
                if (entityLivingBase.isRiding() && entityLivingBase.ridingEntity instanceof EntityLivingBase) {
                    EntityLivingBase ridingEntity = (EntityLivingBase)entityLivingBase.ridingEntity;
                    interpolateRotation = this.interpolateRotation(ridingEntity.prevRenderYawOffset, ridingEntity.renderYawOffset, f2);
                    float f3 = MathHelper.wrapAngleTo180_float(interpolateRotation2 - interpolateRotation);
                    if (f3 < -85.0F) {
                        f3 = -85.0F;
                    }

                    if (f3 >= 85.0F) {
                        f3 = 85.0F;
                    }

                    interpolateRotation = interpolateRotation2 - f3;
                    if (f3 * f3 > 2500.0F) {
                        interpolateRotation += f3 * 0.2F;
                    }
                }

                float f3 = entityLivingBase.prevRotationPitch + (entityLivingBase.rotationPitch - entityLivingBase.prevRotationPitch) * f2;
                this.renderLivingAt(entityLivingBase, d, d2, d3);
                float handleRotationFloat = this.handleRotationFloat(entityLivingBase, f2);
                this.rotateCorpse(entityLivingBase, handleRotationFloat, interpolateRotation, f2);
                float f4 = 0.0625F;
                GL11.glEnable(32826);
                GL11.glScalef(-1.0F, -1.0F, 1.0F);
                this.preRenderCallback(entityLivingBase, f2);
                GL11.glTranslatef(0.0F, -24.0F * f4 - 0.0078125F, 0.0F);
                float f5 = entityLivingBase.prevLimbSwingAmount + (entityLivingBase.limbSwingAmount - entityLivingBase.prevLimbSwingAmount) * f2;
                float f6 = entityLivingBase.limbSwing - entityLivingBase.limbSwingAmount * (1.0F - f2);
                if (entityLivingBase.isChild()) {
                    f6 *= 3.0F;
                }

                if (f5 > 1.0F) {
                    f5 = 1.0F;
                }

                GL11.glEnable(3008);
                this.mainModel.setLivingAnimations(entityLivingBase, f6, f5, f2);
                this.renderModel(entityLivingBase, f6, f5, handleRotationFloat, interpolateRotation2 - interpolateRotation, f3, f4);

                for (int i = 0; i < 4; i++) {
                    int shouldRenderPass = this.shouldRenderPass(entityLivingBase, i, f2);
                    if (shouldRenderPass > 0) {
                        if (this.renderPassModel instanceof ModelBiped) {
                            ((ModelBiped)this.renderPassModel).flag = true;
                        }

                        this.renderPassModel.setLivingAnimations(entityLivingBase, f6, f5, f2);
                        this.renderPassModel.render(entityLivingBase, f6, f5, handleRotationFloat, interpolateRotation2 - interpolateRotation, f3, f4);
                        if ((shouldRenderPass & 240) == 16) {
                            this.handleEntityLiving(entityLivingBase, i, f2);
                            this.renderPassModel.render(entityLivingBase, f6, f5, handleRotationFloat, interpolateRotation2 - interpolateRotation, f3, f4);
                        }

                        EnchantmentGlintMod enchantmentGlintMod = CheatBreaker.getInstance().getModManager().enchantmentGlintMod;
                        if ((shouldRenderPass & 15) == 15 && enchantmentGlintMod.isEnabled() && (Boolean)enchantmentGlintMod.armor.glint.getValue()) {
                            float f7 = entityLivingBase.ticksExisted + f2;
                            this.bindTexture(RES_ITEM_GLINT);
                            GL11.glEnable(3042);
                            float f8 = 0.5F;
                            GL11.glColor4f(f8, f8, f8, 1.0F);
                            GL11.glDepthFunc(514);
                            GL11.glDepthMask(false);

                            for (int j = 0; j < 2; j++) {
                                GL11.glDisable(2896);
                                int colorValue = enchantmentGlintMod.armor.glintColor.getColorValue();
                                float f9 = (colorValue >> 24 & 0xFF) / 255.0F;
                                float f10 = (colorValue >> 16 & 0xFF) / 255.0F;
                                float f11 = (colorValue >> 8 & 0xFF) / 255.0F;
                                float f12 = (colorValue & 0xFF) / 255.0F;
                                float f13 = f9 * 0.76F;
                                GL11.glColor4f(f10 * f13, f11 * f13, f12 * f13, 1.0F);
                                GL11.glBlendFunc(768, 1);
                                GL11.glMatrixMode(5890);
                                GL11.glLoadIdentity();
                                float f14 = f7 * (0.001F + j * 0.003F) * 20.0F;
                                float f15 = 0.33333334F;
                                GL11.glScalef(f15, f15, f15);
                                GL11.glRotatef(30.0F - j * 60.0F, 0.0F, 0.0F, 1.0F);
                                GL11.glTranslatef(0.0F, f14, 0.0F);
                                GL11.glMatrixMode(5888);
                                this.renderPassModel.render(entityLivingBase, f6, f5, handleRotationFloat, interpolateRotation2 - interpolateRotation, f3, f4);
                            }

                            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                            GL11.glMatrixMode(5890);
                            GL11.glDepthMask(true);
                            GL11.glLoadIdentity();
                            GL11.glMatrixMode(5888);
                            GL11.glEnable(2896);
                            GL11.glDisable(3042);
                            GL11.glDepthFunc(515);
                        }

                        GL11.glDisable(3042);
                        GL11.glEnable(3008);
                    }
                }

                GL11.glDepthMask(true);
                this.renderEquippedItems(entityLivingBase, f2);
                float brightness = entityLivingBase.getBrightness(f2);
                int colorMultiplier = this.getColorMultiplier(entityLivingBase, brightness, f2);
                OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                GL11.glDisable(3553);
                OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
                if ((colorMultiplier >> 24 & 0xFF) > 0 || entityLivingBase.hurtTime > 0 || entityLivingBase.deathTime > 0) {
                    flag = true;
                    EntityLivingBaseRendererLivingEntityDoubleFloatIntEvent entityLivingBaseRendererLivingEntityDoubleFloatIntEvent = new EntityLivingBaseRendererLivingEntityDoubleFloatIntEvent(
                        this, entityLivingBase, d, d2, d3, f, f2, colorMultiplier, f6, f5, handleRotationFloat, interpolateRotation2, interpolateRotation, f3, f4
                    );
                    CheatBreaker.getInstance().getEventBus().handleEvent(entityLivingBaseRendererLivingEntityDoubleFloatIntEvent);
                    if (!entityLivingBaseRendererLivingEntityDoubleFloatIntEvent.isCancelled()) {
                        GL11.glDisable(3553);
                        GL11.glDisable(3008);
                        GL11.glEnable(3042);
                        GL11.glBlendFunc(770, 771);
                        GL11.glDepthFunc(514);
                        if (entityLivingBase.hurtTime > 0 || entityLivingBase.deathTime > 0) {
                            GL11.glColor4f(brightness, 0.0F, 0.0F, 0.4F);
                            this.mainModel.render(entityLivingBase, f6, f5, handleRotationFloat, interpolateRotation2 - interpolateRotation, f3, f4);
                            GL11.glColor4f(brightness, 0.0F, 0.0F, 0.4F);
                            this.handleEntityLivingBase(entityLivingBase, f2);

                            for (int i = 0; i < 4; i++) {
                                if (this.inheritRenderPass(entityLivingBase, i, f2) >= 0) {
                                    GL11.glColor4f(brightness, 0.0F, 0.0F, 0.4F);
                                    this.renderPassModel.render(entityLivingBase, f6, f5, handleRotationFloat, interpolateRotation2 - interpolateRotation, f3, f4);
                                }
                            }
                        }

                        if ((colorMultiplier >> 24 & 0xFF) > 0) {
                            float f7 = (colorMultiplier >> 16 & 0xFF) / 255.0F;
                            float f8 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
                            float f9 = (colorMultiplier & 0xFF) / 255.0F;
                            float f10 = (colorMultiplier >> 24 & 0xFF) / 255.0F;
                            GL11.glColor4f(f7, f8, f9, f10);
                            this.mainModel.render(entityLivingBase, f6, f5, handleRotationFloat, interpolateRotation2 - interpolateRotation, f3, f4);
                            GL11.glColor4f(f7, f8, f9, f10);
                            this.handleEntityLivingBase(entityLivingBase, f2);

                            for (int i = 0; i < 4; i++) {
                                if (this.inheritRenderPass(entityLivingBase, i, f2) >= 0) {
                                    GL11.glColor4f(f7, f8, f9, f10);
                                    this.renderPassModel.render(entityLivingBase, f6, f5, handleRotationFloat, interpolateRotation2 - interpolateRotation, f3, f4);
                                }
                            }
                        }
                    }

                    flag = false;
                }

                GL11.glDepthFunc(515);
                GL11.glDisable(3042);
                GL11.glEnable(3008);
                GL11.glEnable(3553);
                GL11.glDisable(32826);
            } catch (Exception e) {
                flag = false;
                logger.error("Couldn't render entity", e);
            }

            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glEnable(3553);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glEnable(2884);
            GL11.glPopMatrix();
            this.passSpecialRender(entityLivingBase, d, d2, d3);
        }
    }

    public void renderModel(EntityLivingBase entityLivingBase, float f, float f2, float f3, float f4, float f5, float f6) {
        this.bindEntityTexture(entityLivingBase);
        if (!entityLivingBase.isInvisible()) {
            this.mainModel.render(entityLivingBase, f, f2, f3, f4, f5, f6);
        } else if (!entityLivingBase.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer)) {
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.15F);
            GL11.glDepthMask(false);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glAlphaFunc(516, 0.003921569F);
            this.mainModel.render(entityLivingBase, f, f2, f3, f4, f5, f6);
            GL11.glDisable(3042);
            GL11.glAlphaFunc(516, 0.1F);
            GL11.glPopMatrix();
            GL11.glDepthMask(true);
        } else {
            this.mainModel.setRotationAngles(f, f2, f3, f4, f5, f6, entityLivingBase);
        }
    }

    public void renderLivingAt(EntityLivingBase entityLivingBase, double d, double d2, double d3) {
        GL11.glTranslatef((float)d, (float)d2, (float)d3);
    }

    public void rotateCorpse(EntityLivingBase entityLivingBase, float f, float f2, float f3) {
        GL11.glRotatef(180.0F - f2, 0.0F, 1.0F, 0.0F);
        if (entityLivingBase.deathTime > 0) {
            float f4 = (entityLivingBase.deathTime + f3 - 1.0F) / 20.0F * 1.6F;
            f4 = MathHelper.sqrt_float(f4);
            if (f4 > 1.0F) {
                f4 = 1.0F;
            }

            GL11.glRotatef(f4 * this.getDeathMaxRotation(entityLivingBase), 0.0F, 0.0F, 1.0F);
        } else {
            String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(entityLivingBase.getCommandSenderName());
            if ((textWithoutFormattingCodes.equals("Dinnerbone") || textWithoutFormattingCodes.equals("Grumm")) && (!(entityLivingBase instanceof EntityPlayer) || !((EntityPlayer)entityLivingBase).getHideCape())) {
                GL11.glTranslatef(0.0F, entityLivingBase.height + 0.1F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
            }
        }
    }

    public float getSwingProgress(EntityLivingBase entityLivingBase, float f) {
        return entityLivingBase.getSwingProgress(f);
    }

    public float handleRotationFloat(EntityLivingBase entityLivingBase, float f) {
        return entityLivingBase.ticksExisted + f;
    }

    public void renderEquippedItems(EntityLivingBase entityLivingBase, float f) {
    }

    public void renderArrowsStuckInEntity(EntityLivingBase entityLivingBase, float f) {
        if (!CheatBreaker.getInstance().getKeybindSettings().hideStuckArrows.getBooleanValue()) {
            int arrowCountInEntity = entityLivingBase.getArrowCountInEntity();
            if (arrowCountInEntity > 0) {
                EntityArrow entityArrow = new EntityArrow(entityLivingBase.worldObj, entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ);
                Random random = new Random(entityLivingBase.getEntityId());

                for (int i = 0; i < arrowCountInEntity; i++) {
                    GL11.glPushMatrix();
                    ModelRenderer randomModelBox = this.mainModel.getRandomModelBox(random);
                    ModelBox modelBox = (ModelBox)randomModelBox.cubeList.get(random.nextInt(randomModelBox.cubeList.size()));
                    randomModelBox.postRender(0.0625F);
                    float nextFloat = random.nextFloat();
                    float nextFloat2 = random.nextFloat();
                    float nextFloat3 = random.nextFloat();
                    float f2 = (modelBox.posX1 + (modelBox.posX2 - modelBox.posX1) * nextFloat) / 16.0F;
                    float f3 = (modelBox.posY1 + (modelBox.posY2 - modelBox.posY1) * nextFloat2) / 16.0F;
                    float f4 = (modelBox.posZ1 + (modelBox.posZ2 - modelBox.posZ1) * nextFloat3) / 16.0F;
                    GL11.glTranslatef(f2, f3, f4);
                    nextFloat = nextFloat * 2.0F - 1.0F;
                    nextFloat2 = nextFloat2 * 2.0F - 1.0F;
                    nextFloat3 = nextFloat3 * 2.0F - 1.0F;
                    nextFloat *= -1.0F;
                    nextFloat2 *= -1.0F;
                    nextFloat3 *= -1.0F;
                    float f5 = MathHelper.sqrt_float(nextFloat * nextFloat + nextFloat3 * nextFloat3);
                    entityArrow.prevRotationYaw = entityArrow.rotationYaw = (float)(Math.atan2(nextFloat, nextFloat3) * 180.0 / Math.PI);
                    entityArrow.prevRotationPitch = entityArrow.rotationPitch = (float)(Math.atan2(nextFloat2, f5) * 180.0 / Math.PI);
                    double d = 0.0;
                    double d2 = 0.0;
                    double d3 = 0.0;
                    float f6 = 0.0F;
                    this.renderManager.renderEntityWithPosYaw(entityArrow, d, d2, d3, f6, f);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    public int inheritRenderPass(EntityLivingBase entityLivingBase, int n, float f) {
        return this.shouldRenderPass(entityLivingBase, n, f);
    }

    public int shouldRenderPass(EntityLivingBase entityLivingBase, int n, float f) {
        return -1;
    }

    public void handleEntityLiving(EntityLivingBase entityLivingBase, int n, float f) {
    }

    public float getDeathMaxRotation(EntityLivingBase entityLivingBase) {
        return 90.0F;
    }

    public int getColorMultiplier(EntityLivingBase entityLivingBase, float f, float f2) {
        return 0;
    }

    public void preRenderCallback(EntityLivingBase entityLivingBase, float f) {
    }

    public void passSpecialRender(EntityLivingBase entityLivingBase, double d, double d2, double d3) {
        NametagMod nametagMod = CheatBreaker.getInstance().getModManager().nametagMod;
        boolean isEnabled = nametagMod.isEnabled();
        PlayerDataManager playerDataManager = CheatBreaker.getInstance().getPlayerDataManager();
        boolean flag = isEnabled
            && (Boolean)nametagMod.showCheatbreakerLogo.getValue()
            && (nametagMod.isNametagAllowed(entityLivingBase.getFormattedCommandSenderName().getFormattedText()) || playerDataManager.containsPlayerName(entityLivingBase.getUniqueID().toString(), false))
            && playerDataManager.containsPlayerName(entityLivingBase.getFormattedCommandSenderName().getFormattedText(), true);
        boolean value = (Boolean)nametagMod.cheatbreakerLogoType.getValue();
        PlayerData findPlayerData = playerDataManager.findPlayerData(entityLivingBase.getUniqueID().toString());
        GL11.glAlphaFunc(516, 0.1F);
        if (this.canRenderName(entityLivingBase) || entityLivingBase.equals(Minecraft.getMinecraft().thePlayer) && isEnabled && (Boolean)nametagMod.shownOwnNametag.getValue()) {
            float f = 1.6F;
            float f2 = 0.016666668F * f;
            double distanceSqToEntity = entityLivingBase.getDistanceSqToEntity(this.renderManager.livingPlayer);
            float f3 = entityLivingBase.isSneaking() ? 32.0F : 64.0F;
            if (!CheatBreaker.getInstance().getModManager().nametagMod.flag && distanceSqToEntity < f3 * f3) {
                String formattedText = entityLivingBase.getFormattedCommandSenderName().getFormattedText();
                if (entityLivingBase.isSneaking()) {
                    FontRenderer fontRendererFromRenderManager = this.getFontRendererFromRenderManager();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)d + 0.0F, (float)d2 + entityLivingBase.height + 0.5F, (float)d3);
                    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                    GL11.glScalef(-f2, -f2, f2);
                    GL11.glDisable(2896);
                    GL11.glTranslatef(0.0F, 0.25F / f2, 0.0F);
                    GL11.glDepthMask(false);
                    GL11.glEnable(3042);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    Tessellator instance = Tessellator.instance;
                    GL11.glDisable(3553);
                    boolean flag2 = (Boolean)nametagMod.stripColorFormatting.getValue() && isEnabled;
                    String str = flag2 ? this.formatText(formattedText) : formattedText;
                    int n = fontRendererFromRenderManager.getStringWidth(str) / 2;
                    int n2 = flag ? (str.contains(" ") ? 6 : 5) : 0;
                    int n3 = flag ? (str.contains(" ") ? 8 : 7) : 0;
                    n = value ? n + n2 : n + n3;
                    if (!isEnabled || (Boolean)nametagMod.showBackground.getValue()) {
                        instance.startDrawingQuads();
                        FloatEvent4 floatEvent4 = new FloatEvent4(0.0F, 0.0F, 0.0F, 0.25F);
                        CheatBreaker.getInstance().getEventBus().handleEvent(floatEvent4);
                        instance.setColorRGBA_F(floatEvent4.getFloatValue1(), floatEvent4.getFloatValue2(), floatEvent4.getFloatValue3(), floatEvent4.getFloatValue4());
                        instance.addVertex(-n - 1, -1.0, 0.0);
                        instance.addVertex(-n - 1, 8.0, 0.0);
                        instance.addVertex(n + 1, 8.0, 0.0);
                        instance.addVertex(n + 1, -1.0, 0.0);
                        instance.draw();
                    }

                    GL11.glEnable(3553);
                    GL11.glDepthMask(true);
                    this.render(n, 0, n2, n3, fontRendererFromRenderManager, str, findPlayerData, nametagMod, flag, value, 32);
                    GL11.glEnable(2896);
                    GL11.glDisable(3042);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glPopMatrix();
                } else {
                    this.renderOffsetLivingLabel(entityLivingBase, d, d2, d3, formattedText, f2, distanceSqToEntity);
                }
            }
        }
    }

    public boolean canRenderName(EntityLivingBase targetEntity) {
        NametagMod nametagMod = CheatBreaker.getInstance().getModManager().nametagMod;
        boolean isEnabled = nametagMod.isEnabled();
        boolean flag = isEnabled && (Boolean)nametagMod.showNametagsWhenGuiIsHidden.getValue();
        return (Minecraft.isGuiEnabled() || flag)
            && targetEntity != this.renderManager.livingPlayer
            && !targetEntity.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer)
            && targetEntity.riddenByEntity == null;
    }

    public void renderOffsetLivingLabel(EntityLivingBase entityLivingBase, double d, double d2, double d3, String str, float f, double d4) {
        if (entityLivingBase.isPlayerSleeping()) {
            this.renderLivingLabel(entityLivingBase, str, d, d2 - 1.5, d3, 64);
        } else {
            this.renderLivingLabel(entityLivingBase, str, d, d2, d3, 64);
        }
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityLivingBase)entity, d, d2, d3, f, f2);
    }

    public void handleEntityLivingBase(EntityLivingBase entityLivingBase, float f) {
    }

    @Override
    public String formatText(String str) {
        return str == null ? null : this.pattern2.matcher(str).replaceAll("§r");
    }

    public static boolean isFlag() {
        return flag;
    }
}
