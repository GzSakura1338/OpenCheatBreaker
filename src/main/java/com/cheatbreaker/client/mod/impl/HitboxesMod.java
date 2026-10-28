package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.EntityDoubleFloatEvent;
import com.cheatbreaker.client.event.IntEvent4;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.render.RenderUtils;
import java.awt.Color;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.AxisAlignedBB;
import net.optifine.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class HitboxesMod extends AbstractMod {
    public HitboxesMod.HitboxSettings player;
    public HitboxesMod.HitboxSettings mob;
    public HitboxesMod.HitboxSettings item;
    public HitboxesMod.HitboxSettings projectile;
    public HitboxesMod.HitboxSettings expOrb;

    public HitboxesMod() {
        super("Hitboxes");
        this.setDescription("Shows an outline around an entity's hitbox.");
        this.setCategories(ModCategory.HUD);
        this.setLabel("Hitboxes", 1.0F);
        this.setApplyDefaultColorOnEnable(false);
        this.player = new HitboxesMod.HitboxSettings("Player");
        this.mob = new HitboxesMod.HitboxSettings("Mob");
        this.item = new HitboxesMod.HitboxSettings("Item");
        this.projectile = new HitboxesMod.HitboxSettings("Projectile");
        this.expOrb = new HitboxesMod.HitboxSettings("Exp Orb");
        this.addEventHandler(EntityDoubleFloatEvent.class, this::onEntityDoubleFloatEvent);
        this.addEventHandler(IntEvent4.class, intEvent4 -> { try {
            if (Keyboard.getEventKey() == 48 && Keyboard.isKeyDown(61)) {
                this.setEnabled(!this.isEnabled());
            }
        } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } });
    }

    public void onEntityDoubleFloatEvent(EntityDoubleFloatEvent entityDoubleFloatEvent) {
        Entity entity = entityDoubleFloatEvent.getEntity();
        double doubleValue1 = entityDoubleFloatEvent.getDoubleValue1();
        double doubleValue2 = entityDoubleFloatEvent.getDoubleValue2();
        double doubleValue3 = entityDoubleFloatEvent.getDoubleValue3();
        boolean booleanValue = this.player.showOwnHitbox.getBooleanValue();
        boolean flag = entity == this.mc.thePlayer;
        HitboxesMod.HitboxSettings findHitboxSettings = this.findHitboxSettings(entity);
        Color toColor = RenderUtils.toColor((flag ? findHitboxSettings.ownHitboxOutlineColor : findHitboxSettings.outlineColor).getColorValue());
        Color toColor2 = RenderUtils.toColor((flag ? findHitboxSettings.ownHitboxOverlayColor : findHitboxSettings.overlayColor).getColorValue());
        GL11.glLineWidth(findHitboxSettings.lineThickness.getIntValue());
        if (findHitboxSettings.show.getBooleanValue()) {
            if (booleanValue || !flag) {
                GlStateManager.depthMask(false);
                GL11.glDisable(3553);
                GL11.glDisable(2896);
                GL11.glDisable(2884);
                GlStateManager.disableBlend();
                float f = entity.width / 2.0F;
                double d = doubleValue2;
                double d2 = doubleValue2 + entity.height;
                if (entity == this.mc.thePlayer) {
                    d = doubleValue2 - 1.61;
                    d2 -= 1.45;
                }

                AxisAlignedBB boundingBox = AxisAlignedBB.getBoundingBox(doubleValue1 - f, d, doubleValue3 - f, doubleValue1 + f, d2, doubleValue3 + f);
                GL11.glEnable(3042);
                if (findHitboxSettings.show2.getBooleanValue()) {
                    if (findHitboxSettings.show4.getBooleanValue()) {
                        GL11.glPushAttrib(8192);
                        GL11.glLineStipple(findHitboxSettings.lineDistance.getIntValue(), (short)-21846);
                        GL11.glEnable(2852);
                        GL11.glBegin(1);
                        GL11.glEnd();
                    }

                    GlStateManager.color(toColor.getRed() / 255.0F, toColor.getBlue() / 255.0F, toColor.getGreen() / 255.0F, toColor.getAlpha() / 255.0F);
                    RenderGlobal.drawOutlinedBoundingBox(boundingBox, -1);
                    if (findHitboxSettings.show4.getBooleanValue()) {
                        GL11.glPopAttrib();
                    }
                }

                if (findHitboxSettings.show3.getBooleanValue()) {
                    GlStateManager.color(toColor2.getRed() / 255.0F, toColor2.getBlue() / 255.0F, toColor2.getGreen() / 255.0F, toColor2.getAlpha() / 255.0F);
                    RenderGlobal.drawBox(boundingBox, -1);
                }

                GL11.glDisable(3042);
                GL11.glEnable(3553);
                GL11.glEnable(2896);
                GL11.glEnable(2884);
                GlStateManager.disableBlend();
                GlStateManager.depthMask(true);
            }
        }
    }

    public HitboxesMod.HitboxSettings findHitboxSettings(Entity entity) {
        if (entity instanceof AbstractClientPlayer) {
            return this.player;
        } else if (entity instanceof EntityFishHook) {
            return this.projectile;
        } else if (entity instanceof EntityItem) {
            return this.item;
        } else if (entity instanceof EntityXPOrb) {
            return this.expOrb;
        } else {
            return entity instanceof IProjectile ? this.projectile : this.mob;
        }
    }

    public class HitboxSettings {
        public Setting hitboxOptions;
        public Setting show;
        public Setting show2;
        public Setting show3;
        public Setting showOwnHitbox;
        public Setting show4;
        public Setting lineDistance;
        public Setting lineThickness;
        public Setting ownHitboxOutlineColor;
        public Setting ownHitboxOverlayColor;
        public Setting outlineColor;
        public Setting overlayColor;

        public HitboxSettings(String str) {
            this.hitboxOptions = new SettingGroup(HitboxesMod.this, str + " Hitbox Options").changeValue(true);
            boolean flag = str.equals("Player") || str.equals("Mob");
            this.show = new Setting(HitboxesMod.this, "Show " + str + " Hitbox").changeValue(flag).withParents(this.hitboxOptions);
            this.show2 = new Setting(HitboxesMod.this, "Show " + str + " Hitbox Outline")
                .changeValue(true)
                .setCustomizationLevel(SettingsLevel.MEDIUM)
                .withHierarchyParents(this.show);
            this.show3 = new Setting(HitboxesMod.this, "Show " + str + " Hitbox Overlay")
                .changeValue(false)
                .setCustomizationLevel(SettingsLevel.MEDIUM)
                .withHierarchyParents(this.show);
            if (str.equals("Player")) {
                this.showOwnHitbox = new Setting(HitboxesMod.this, "Show Own Hitbox")
                    .changeValue(false)
                    .setCustomizationLevel(SettingsLevel.MEDIUM)
                    .withHierarchyParents(this.show);
            }

            this.show4 = new Setting(HitboxesMod.this, "Show " + str + " Dashed Line")
                .changeValue(false)
                .setCustomizationLevel(SettingsLevel.MEDIUM)
                .withHierarchyParents(this.show2);
            this.lineDistance = new Setting(HitboxesMod.this, str + " Line Distance")
                .changeValue(10)
                .setRange(0, 20)
                .setUnit("px")
                .setCustomizationLevel(SettingsLevel.MEDIUM)
                .withHierarchyParents(this.show4);
            this.lineThickness = new Setting(HitboxesMod.this, str + " Line Thickness")
                .changeValue(1)
                .setRange(1, 5)
                .setUnit("px")
                .setCustomizationLevel(SettingsLevel.MEDIUM)
                .withHierarchyParents(this.show2);
            if (str.equals("Player")) {
                this.ownHitboxOutlineColor = new Setting(HitboxesMod.this, "Own Hitbox Outline Color")
                    .changeValue(-1)
                    .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
                    .setCustomizationLevel(SettingsLevel.MEDIUM)
                    .withHierarchyParents(this.show2);
                this.ownHitboxOverlayColor = new Setting(HitboxesMod.this, "Own Hitbox Overlay Color")
                    .changeValue(553648127)
                    .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
                    .setCustomizationLevel(SettingsLevel.MEDIUM)
                    .withHierarchyParents(this.show3);
            }

            this.outlineColor = new Setting(HitboxesMod.this, str + " Outline Color")
                .changeValue(-1)
                .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
                .withHierarchyParents(this.show2);
            this.overlayColor = new Setting(HitboxesMod.this, str + " Overlay Color")
                .changeValue(553648127)
                .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
                .withHierarchyParents(this.show3);
        }
    }
}
