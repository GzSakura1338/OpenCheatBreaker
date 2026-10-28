package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.IResourcePackEvent;
import com.cheatbreaker.client.event.SimpleEvent49;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.google.common.base.Throwables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import org.lwjgl.opengl.GL11;

public class MotionBlurMod extends AbstractMod {
    public Setting blurType;
    public long lastUpdateMillis;
    public Setting applyInMenus;
    public Setting amount;
    public Setting color;

    public MotionBlurMod() throws java.io.IOException {
        super("Motion Blur");
        this.setEnabledByDefault(false);
        if (Config.openGlVendor.contains("NVIDIA")) {
            this.blurType = new Setting(this, "Blur Type").changeValue("Accumulation").setStringValues("Phosphor", "Aggressive", "Accumulation");
        } else {
            this.blurType = new Setting(this, "Blur Type").changeValue("Aggressive").setStringValues("Phosphor", "Aggressive");
        }

        this.applyInMenus = new Setting(this, "Apply In Menus").changeValue(true).setCondition(() -> this.blurType.getValue().equals("Accumulation"));
        this.amount = new Setting(this, "Amount").setDescription("Change the amount of blur intensity.").changeValue(55.0F).setRange(0.0F, 100.0F).setUnit("%");
        this.color = new Setting(this, "Color")
            .setDescription("Change the color of the motion blur.")
            .changeValue(-1)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCondition(() -> this.blurType.getValue().equals("Phosphor"));
        this.setIcon(new ResourceLocation("client/icons/mods/swordblur.png"), 40, 40);
        this.setDescription("Blurs your surroundings while moving.");
        this.setAuthors("Fyu (Original Concept)", "Moonsworth LLC (Aggressive Phosphor), Gabriel Bolonkowski (Accumulation)");
        this.setCategories(ModCategory.MISC);
        this.addEventHandler(ClientTickEvent.class, this::onClientTickEvent);
        this.addEventHandler(IResourcePackEvent.class, this::onIResourcePackEvent);
        this.addEventHandler(SimpleEvent49.class, this::onSimpleEvent49);
    }

    public float getFloatValue() {
        if (Minecraft.getMinecraft().theWorld == null) {
            return 0.0F;
        }

        float f = this.getFloatValue2() * 10.0F;
        if (Minecraft.getMinecraft().currentScreen == null) {
            this.lastUpdateMillis = System.currentTimeMillis();
            f = Math.min(f, 996.0F);
        } else {
            f = Math.min(f, 990.0F);
        }

        long l = System.currentTimeMillis() - this.lastUpdateMillis;
        return l > 10000L ? 0.0F : Math.max(f / 1000.0F, 0.0F);
    }

    public float getFloatValue2() {
        float f = this.amount.getFloatValue() * 0.9F;
        int debugFPS = Minecraft.debugFPS;
        return debugFPS > 200 ? f : (debugFPS > 120 ? 0.5F * f : 0.0F);
    }

    public void onSimpleEvent49(SimpleEvent49 simpleEvent49) {
        if (this.blurType.getValue().equals("Accumulation")) {
            if (this.applyInMenus.getBooleanValue() || this.mc.currentScreen == null) {
                float floatValue = this.getFloatValue();
                GL11.glAccum(259, floatValue);
                GL11.glAccum(256, 1.0F - floatValue);
                GL11.glAccum(258, 1.0F);
            }
        }
    }

    public void onClientTickEvent(ClientTickEvent clientTickEvent) {
        if (!this.blurType.getValue().equals("Accumulation")) {
            if (this.mc.currentScreen == null && !this.mc.entityRenderer.isShaderActive()) {
                this.updateShaderUniforms();
            }
        }
    }

    public void onIResourcePackEvent(IResourcePackEvent resourcePackEvent) {
        this.mc.entityRenderer.deactivateShader();
    }

    public void updateShaderUniforms() {
        this.mc.entityRenderer.reloadMotionBlurShader();
        ShaderGroup shaderGroup = Minecraft.getMinecraft().entityRenderer.getShaderGroup();

        try {
            if (this.mc.entityRenderer.isShaderActive() && this.mc.thePlayer != null) {
                for (Shader shader : shaderGroup.getListShaders()) {
                    ShaderUniform shaderUniform = shader.getShaderManager().getShaderUniform("Phosphor");
                    if (shaderUniform != null) {
                        float f = 1.0F - this.amount.getFloatValue() / 100.0F * 0.9F;
                        if (this.blurType.getValue().equals("Phosphor")) {
                            f = 0.7F + this.amount.getFloatValue() * 0.95F / 1000.0F * 3.0F - 0.01F;
                        }

                        int n = this.blurType.getValue().equals("Phosphor") ? this.color.getColorValue() : -1;
                        float f2 = (n >> 16 & 0xFF) / 255.0F;
                        float f3 = (n >> 8 & 0xFF) / 255.0F;
                        float f4 = (n & 0xFF) / 255.0F;
                        shaderUniform.set(f * f2, f * f3, f * f4);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            Throwables.propagate(e);
        }
    }
}
