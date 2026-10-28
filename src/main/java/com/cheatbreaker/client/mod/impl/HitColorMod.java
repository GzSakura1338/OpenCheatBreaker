package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.EntityLivingBaseRendererLivingEntityDoubleFloatIntEvent;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.GL11;

public class HitColorMod extends AbstractMod {
    public Setting generalOptions;
    public Setting enableHitColor;
    public Setting affectArmor;
    public Setting affectCape;
    public Setting affectWings;
    public Setting affectHats;
    public Setting affectedByBrightness;
    public Setting animationType;
    public Setting colorOptions;
    public Setting hitColor;

    public void onEntityLivingBaseRendererLivingEntityDoubleFloatIntEvent(EntityLivingBaseRendererLivingEntityDoubleFloatIntEvent entityLivingBaseRendererLivingEntityDoubleFloatIntEvent) {
        entityLivingBaseRendererLivingEntityDoubleFloatIntEvent.setCancelled(true);
        if (this.enableHitColor.getBooleanValue()) {
            RendererLivingEntity rendererLivingEntity = entityLivingBaseRendererLivingEntityDoubleFloatIntEvent.getRendererLivingEntity();
            EntityLivingBase entityLivingBase = entityLivingBaseRendererLivingEntityDoubleFloatIntEvent.getEntityLivingBase();
            float partialTicks = entityLivingBaseRendererLivingEntityDoubleFloatIntEvent.getPartialTicks();
            int colorMultiplier = entityLivingBaseRendererLivingEntityDoubleFloatIntEvent.getColorMultiplier();
            float floatValue3 = entityLivingBaseRendererLivingEntityDoubleFloatIntEvent.getFloatValue3();
            float floatValue4 = entityLivingBaseRendererLivingEntityDoubleFloatIntEvent.getFloatValue4();
            float floatValue5 = entityLivingBaseRendererLivingEntityDoubleFloatIntEvent.getFloatValue5();
            float floatValue6 = entityLivingBaseRendererLivingEntityDoubleFloatIntEvent.getFloatValue6();
            float floatValue7 = entityLivingBaseRendererLivingEntityDoubleFloatIntEvent.getFloatValue7();
            float floatValue8 = entityLivingBaseRendererLivingEntityDoubleFloatIntEvent.getFloatValue8();
            float floatValue9 = entityLivingBaseRendererLivingEntityDoubleFloatIntEvent.getFloatValue9();
            GL11.glDisable(3553);
            GL11.glDisable(3008);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GL11.glDepthFunc(514);
            float f;
            if ((Boolean)this.affectedByBrightness.getValue()) {
                f = entityLivingBase.getBrightness(partialTicks);
            } else {
                f = 1.0F;
            }

            int colorValue = this.hitColor.getColorValue();
            float f2 = (colorValue >> 24 & 0xFF) / 255.0F;
            if (!this.animationType.getValue().equals("None")) {
                float f3 = 1.0F - entityLivingBase.hurtTime / 10.0F;
                if (this.animationType.getValue().equals("Linear In/Out")) {
                    f3 = f3 < 0.5F ? f3 / 0.5F : (1.0F - f3) / 0.5F;
                } else if (this.animationType.getValue().equals("Linear Out")) {
                    f3 = 1.0F - f3;
                }

                f2 *= f3;
            }

            float f3 = (colorValue >> 16 & 0xFF) / 255.0F;
            float f4 = (colorValue >> 8 & 0xFF) / 255.0F;
            float f5 = (colorValue & 0xFF) / 255.0F;
            if (entityLivingBase.hurtTime > 0 || entityLivingBase.deathTime > 0) {
                GlStateManager.color(f * f3, f * f4, f * f5, f2);
                rendererLivingEntity.mainModel.render(entityLivingBase, floatValue3, floatValue4, floatValue5, floatValue6 - floatValue7, floatValue8, floatValue9);
                GlStateManager.color(f * f3, f * f4, f * f5, f2);
                rendererLivingEntity.handleEntityLivingBase(entityLivingBase, partialTicks);
                if ((Boolean)this.affectArmor.getValue()) {
                    for (int i = 0; i < 4; i++) {
                        if (rendererLivingEntity.inheritRenderPass(entityLivingBase, i, partialTicks) >= 0) {
                            GlStateManager.color(f * f3, f * f4, f * f5, f2);
                            rendererLivingEntity.renderPassModel.render(entityLivingBase, floatValue3, floatValue4, floatValue5, floatValue6 - floatValue7, floatValue8, floatValue9);
                        }
                    }
                }
            }

            if ((colorMultiplier >> 24 & 0xFF) > 0) {
                float f6 = (colorMultiplier >> 16 & 0xFF) / 255.0F;
                float f7 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
                float f8 = (colorMultiplier & 0xFF) / 255.0F;
                float f9 = (colorMultiplier >> 24 & 0xFF) / 255.0F;
                GlStateManager.color(f6, f7, f8, f9);
                rendererLivingEntity.mainModel.render(entityLivingBase, floatValue3, floatValue4, floatValue5, floatValue6 - floatValue7, floatValue8, floatValue9);
                GlStateManager.color(f6, f7, f8, f9);
                rendererLivingEntity.handleEntityLivingBase(entityLivingBase, partialTicks);

                for (int i = 0; i < 4; i++) {
                    if (rendererLivingEntity.inheritRenderPass(entityLivingBase, i, partialTicks) >= 0) {
                        GlStateManager.color(f6, f7, f8, f9);
                        rendererLivingEntity.renderPassModel.render(entityLivingBase, floatValue3, floatValue4, floatValue5, floatValue6 - floatValue7, floatValue8, floatValue9);
                    }
                }
            }
        }
    }

    public HitColorMod() throws java.io.IOException {
        super("Hit Color");
        this.setApplyDefaultColorOnEnable(false);
        this.setCategories(ModCategory.HUD);
        this.generalOptions = new SettingGroup(this, "General Options").changeValue(true);
        this.enableHitColor = new Setting(this, "Enable Hit Color").changeValue(true).withParents(this.generalOptions);
        this.affectArmor = new Setting(this, "Affect Armor").changeValue(true).withHierarchyParents(this.enableHitColor);
        this.affectCape = new Setting(this, "Affect Cape").changeValue(false).withHierarchyParents(this.enableHitColor);
        this.affectWings = new Setting(this, "Affect Wings").changeValue(false).withHierarchyParents(this.enableHitColor);
        this.affectHats = new Setting(this, "Affect Hats").changeValue(false).withHierarchyParents(this.enableHitColor);
        this.affectedByBrightness = new Setting(this, "Affected by brightness").changeValue(true).withHierarchyParents(this.enableHitColor);
        this.animationType = new Setting(this, "Animation Type")
            .changeValue("None")
            .setStringValues("None", "Linear In/Out", "Linear Out")
            .withHierarchyParents(this.enableHitColor);
        this.colorOptions = new SettingGroup(this, "Color Options").changeValue(true).withParents(this.enableHitColor);
        this.hitColor = new Setting(this, "Hit Color")
            .changeValue(1727987712)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .withHierarchyParents(this.colorOptions);
        this.setDescription("Customize the damage tint overlay on entities.");
        this.setAuthors("aycy (Fade animation)");
        this.setKeywords("Damage Tint");
        this.setIcon(new ResourceLocation("client/icons/mods/hittint.png"), 32, 32);
        this.addEventHandler(EntityLivingBaseRendererLivingEntityDoubleFloatIntEvent.class, this::onEntityLivingBaseRendererLivingEntityDoubleFloatIntEvent);
        this.setEnabledByDefault(true);
    }

    public boolean checkSetting(Setting setting) {
        return this.isEnabled() && setting.getBooleanValue();
    }
}
