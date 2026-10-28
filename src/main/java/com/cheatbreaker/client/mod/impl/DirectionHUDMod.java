package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.minimap.Waypoint;
import com.cheatbreaker.client.mod.AbstractHudMod;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.AnchorPoint;
import com.cheatbreaker.client.ui.GuiUtils;
import java.util.UUID;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.GL11;

public class DirectionHUDMod extends AbstractHudMod {
    public ResourceLocation compassPng = new ResourceLocation("textures/gui/compass.png");
    public Setting generalOptions;
    public Setting detailed;
    public Setting width;
    public Setting highlightNorth;
    public Setting showBackground;
    public Setting showBorder;
    public Setting borderThickness;
    public Setting colorOptions;
    public Setting markerColor;
    public Setting directionColor;
    public Setting highlightColor;
    public Setting yawColor;
    public Setting backgroundColor;
    public Setting borderColor;

    public void render(float f, float f2, float f3, int n, float f4) {
        int n2 = n % 90;
        int n3 = n % 45;
        float f5 = 300.0F * n / 360.0F - 300.0F * f4 / 360.0F;
        if (f5 > 150.0F) {
            f5 -= 300.0F;
        }

        if (f5 < -150.0F) {
            f5 += 300.0F;
        }

        int n4;
        if ((n4 = 255 - (int)(Math.abs(f5) / (f3 / 2.0F) * 255.0F)) >= 35) {
            int n5 = n4 << 24 | this.directionColor.getColorValue() & 16777215;
            int n6 = n4 << 24 | this.highlightColor.getColorValue() & 16777215;
            if (n2 == 0) {
                RenderUtils.drawRectSized(f + f5 - 0.5F, f2, 1.0F, 8.0F, n5);
                switch (n) {
                    case 0:
                    case 360:
                        CheatBreaker.getInstance().getFontManager().robotoBold14.drawCenteredString("S", f + f5, f2 + 10.0F, n5);
                        break;
                    case 90:
                        CheatBreaker.getInstance().getFontManager().robotoBold14.drawCenteredString("W", f + f5, f2 + 10.0F, n5);
                        break;
                    case 180:
                        CheatBreaker.getInstance()
                            .getFontManager()
                            .robotoBold14
                            .drawCenteredString("N", f + f5, f2 + 10.0F, this.highlightNorth.getBooleanValue() ? n6 : n5);
                        break;
                    case 270:
                        CheatBreaker.getInstance().getFontManager().robotoBold14.drawCenteredString("E", f + f5, f2 + 10.0F, n5);
                }
            } else if (n3 == 0) {
                switch (n) {
                    case 45:
                        CheatBreaker.getInstance().getFontManager().robotoBold14.drawCenteredString("SW", f + f5, f2, n5);
                        break;
                    case 135:
                        CheatBreaker.getInstance().getFontManager().robotoBold14.drawCenteredString("NW", f + f5, f2, n5);
                        break;
                    case 225:
                        CheatBreaker.getInstance().getFontManager().robotoBold14.drawCenteredString("NE", f + f5, f2, n5);
                        break;
                    case 315:
                        CheatBreaker.getInstance().getFontManager().robotoBold14.drawCenteredString("SE", f + f5, f2, n5);
                }
            } else {
                RenderUtils.drawRectSized(f + f5 - 0.25F, f2, 0.5F, 5.0F, n5);
                CheatBreaker.getInstance().getFontManager().robotoBold10.drawCenteredString("" + n, f + f5 - 0.5F, f2 + 6.0F, n5);
            }
        }
    }

    public void render(EntityPlayer entityPlayer, double d, double d2, int n, float f, float f2) {
        float f3 = (float)(entityPlayer.posZ - d2);
        float f4 = (float)(entityPlayer.posX - d);
        float f5 = (float)(Math.atan2(f3, f4) * 180.0 / Math.PI + 90.0);
        float f6 = 300.0F * f5 / 360.0F - 300.0F * f2 / 360.0F;
        if (f6 > 150.0F) {
            f6 -= 300.0F;
        }

        if (f6 < -150.0F) {
            f6 += 300.0F;
        }

        if (255 - (int)(Math.abs(f6) / ((Float)this.width.getValue() / 2.0F) * 255.0F) >= 35) {
            Tessellator instance = Tessellator.instance;
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            byte b = 6;
            byte b2 = 8;
            int n2 = -4 - b2;
            float f7 = (n >> 16 & 0xFF) / 255.0F;
            float f8 = (n >> 8 & 0xFF) / 255.0F;
            float f9 = (n & 0xFF) / 255.0F;
            GL11.glColor4f(f7, f8, f9, 0.75F);
            GlStateManager.pushMatrix();
            GL11.glTranslatef(f + f6, 18.0F, 0.0F);
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            GL11.glTranslatef(-20.0F, 0.0F, 0.0F);
            GlStateManager.rotate(45.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(b * 2, 0.0F, 0.0F);
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, -1.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            instance.startDrawingQuads();
            instance.addVertex(b, n2 + b2 / 2.0F, 0.0);
            instance.addVertex(b, n2, 0.0);
            instance.addVertex(-b, n2, 0.0);
            instance.addVertex(-b, n2 + b2 / 2.0F, 0.0);
            instance.draw();
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, -1.0F);
            GL11.glTranslatef(b * 2 + 3.0F, b2 / 2.0F + 2.0F, 0.0F);
            instance.startDrawingQuads();
            instance.addVertex(b - 1, n2 + b2 / 2.0F, 0.0);
            instance.addVertex(b - 1, n2, 0.0);
            instance.addVertex(-b / 2.0F, n2, 0.0);
            instance.addVertex(-b / 2.0F, n2 + b2 / 2.0F, 0.0);
            instance.draw();
            GlStateManager.popMatrix();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }

    public DirectionHUDMod() throws java.io.IOException {
        super("Direction HUD");
        this.setAnchorPointAndDefault(AnchorPoint.MIDDLE_TOP);
        this.setEnabled(false);
        this.setApplyDefaultColorOnEnable(false);
        this.generalOptions = new SettingGroup(this, "General Options").changeValue(true);
        this.detailed = new Setting(this, "Detailed").changeValue(true).withParents(this.generalOptions);
        this.width = new Setting(this, "Width").changeValue(300.0F).setRange(100.0F, 450.0F).withParents(this.generalOptions);
        this.highlightNorth = new Setting(this, "Highlight North")
            .setDescription("Make North highlight a different color.")
            .changeValue(false)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withParents(this.generalOptions);
        this.showBackground = new Setting(this, "Show Background").setDescription("Draw a background.").changeValue(true).withParents(this.generalOptions);
        this.showBorder = new Setting(this, "Show Border")
            .setDescription("Draw a border around the background.")
            .changeValue(false)
            .withHierarchyParents(this.showBackground);
        this.borderThickness = new Setting(this, "Border Thickness")
            .setDescription("Change the thickness of the border.")
            .changeValue(1.0F)
            .setRange(0.25F, 3.0F)
            .setUnit("px")
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withHierarchyParents(this.showBorder);
        this.colorOptions = new SettingGroup(this, "Color Options").changeValue(true);
        this.markerColor = new Setting(this, "Marker Color")
            .setDescription("Change the marker color.")
            .changeValue(-43691)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .withParents(this.colorOptions);
        this.directionColor = new Setting(this, "Direction Color")
            .setDescription("Change the direction color.")
            .changeValue(-1)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.highlightColor = new Setting(this, "Highlight Color")
            .setDescription("Change the highlight color.")
            .changeValue(-43691)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withParents(this.colorOptions, this.highlightNorth);
        this.yawColor = new Setting(this, "Yaw Color")
            .setDescription("Change the yaw color.")
            .changeValue(-1)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .withParents(this.colorOptions, this.detailed);
        this.backgroundColor = new Setting(this, "Background Color")
            .setDescription("Change the background color.")
            .changeValue(1862270976)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .withParents(this.colorOptions, this.showBackground);
        this.borderColor = new Setting(this, "Border Color")
            .setDescription("Change the border color.")
            .changeValue(-1627389952)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .withParents(this.colorOptions, this.showBorder);
        this.setIcon(new ResourceLocation("client/icons/mods/dirhud.png"), 65, 12);
        this.setDescription("Displays your cardinal direction.");
        this.setAuthors("bspkrs", "jadedcat");
        this.addEventHandler(ScaledResolutionEvent3.class, this::onScaledResolutionEvent3);
    }

    public void renderWaypoint(EntityPlayer entityPlayer, Waypoint waypoint, float f, float f2, float f3) {
        float f4 = (float)(entityPlayer.posZ - waypoint.getZ());
        float f5 = (float)(entityPlayer.posX - waypoint.getX());
        float f6 = (float)(Math.atan2(f4, f5) * 180.0 / Math.PI + 90.0);
        float f7 = 300.0F * f6 / 360.0F - 300.0F * f3 / 360.0F;
        if (f7 > 150.0F) {
            f7 -= 300.0F;
        }

        if (f7 < -150.0F) {
            f7 += 300.0F;
        }

        int n;
        if ((n = 255 - (int)(Math.abs(f7) / ((Float)this.width.getValue() / 2.0F) * 255.0F)) >= 35) {
            RenderUtils.drawTriangleDown(f + f7, f2, 6.0F, n << 24 | waypoint.getRgbColor() & 16777215);
        }
    }

    public void render() {
        int n = MathHelper.floor_double(this.mc.thePlayer.rotationYaw * 256.0F / 360.0F + 0.5) & 0xFF;
        byte b = 0;
        byte b2 = 0;
        if (this.directionColor.getIntValue() != 4095) {
            int colorValue = this.directionColor.getColorValue();
            int colorValue2 = this.backgroundColor.getColorValue();
            int colorValue3 = this.highlightColor.getColorValue();
            this.mc.getTextureManager().bindTexture(this.compassPng);
            if (this.showBackground.getBooleanValue()) {
                GlStateManager.color((colorValue2 >> 16 & 0xFF) / 255.0F, (colorValue2 >> 8 & 0xFF) / 255.0F, (colorValue2 & 0xFF) / 255.0F, (colorValue2 >> 24 & 0xFF) / 255.0F);
                if (n < 128) {
                    GuiUtils.drawTexturedRect(b, b2, n, 0, 66, 12, -100.0F);
                } else {
                    GuiUtils.drawTexturedRect(b, b2, n - 128, 12, 66, 12, -100.0F);
                }
            }

            GlStateManager.color((colorValue >> 16 & 0xFF) / 255.0F, (colorValue >> 8 & 0xFF) / 255.0F, (colorValue & 0xFF) / 255.0F, (colorValue >> 24 & 0xFF) / 255.0F);
            if (n < 128) {
                GuiUtils.drawTexturedRect(b, b2, n, 24, 66, 12, -100.0F);
            } else {
                GuiUtils.drawTexturedRect(b, b2, n - 128, 36, 66, 12, -100.0F);
            }

            if (this.highlightNorth.getBooleanValue()) {
                GlStateManager.color((colorValue3 >> 16 & 0xFF) / 255.0F, (colorValue3 >> 8 & 0xFF) / 255.0F, (colorValue3 & 0xFF) / 255.0F, (colorValue3 >> 24 & 0xFF) / 255.0F);
                if (n < 128) {
                    GuiUtils.drawTexturedRect(b, b2, n, 72, 66, 12, -100.0F);
                } else {
                    GuiUtils.drawTexturedRect(b, b2, n - 128, 84, 66, 12, -100.0F);
                }
            }
        } else {
            this.mc.getTextureManager().bindTexture(this.compassPng);
            if (n < 128) {
                GuiUtils.drawTexturedRect(b, b2, n, 0, 66, 12, -100.0F);
            } else {
                GuiUtils.drawTexturedRect(b, b2, n - 128, 12, 66, 12, -100.0F);
            }
        }

        this.mc.fontRendererObj.drawString("|", b + 32, b2 + 1, this.markerColor.getColorValue());
        this.mc.fontRendererObj.drawString("|§r", b + 32, b2 + 5, this.markerColor.getColorValue());
        if (this.showBorder.getBooleanValue()) {
            float floatValue = this.borderThickness.getFloatValue();
            Gui.drawInsetBorder(-floatValue, -floatValue, 66.0F + floatValue, 12.0F + floatValue, floatValue, this.borderColor.getColorValue());
        }
    }

    public float normalizeAngle(int n) {
        int n2 = n;
        if (n < 0) {
            while (n2 < 0) {
                n2 += 360;
            }
        } else if (n > 360) {
            while (n2 > 360) {
                n2 -= 360;
            }
        }

        return n2;
    }

    public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) {
        if (this.shouldRender()) {
            GlStateManager.pushMatrix();
            this.applyHudTransformForSize(scaledResolutionEvent3.getScaledResolution());
            if ((Boolean)this.detailed.getValue()) {
                if (this.showBackground.getBooleanValue()) {
                    RenderUtils.drawRoundedRectFilled(0.0F, 0.0F, this.hudWidth, this.hudHeight, 0.0F, this.backgroundColor.getColorValue());
                }

                EntityClientPlayerMP thePlayer = this.mc.thePlayer;
                float normalizeAngle = this.normalizeAngle((int)thePlayer.rotationYaw);
                float f = (Float)this.width.getValue() / 2.0F;
                float f2 = -1.0F;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                CheatBreaker.getInstance().getFontManager().robotoBold10.drawCenteredString(String.format("%d", (int)normalizeAngle), f + 0.5F, f2 + 1.0F, 805306368);
                CheatBreaker.getInstance()
                    .getFontManager()
                    .robotoBold10
                    .drawCenteredString(String.format("%d", (int)normalizeAngle), f, f2 + 0.5F, this.yawColor.getColorValue());
                f2 += CheatBreaker.getInstance().getFontManager().robotoBold10.getLineHeight() * 3 + 1.0F;
                RenderUtils.drawTriangleDown(f, f2, 6.0F, this.markerColor.getColorValue());
                this.setHudSize((Float)this.width.getValue(), 32.0F);

                for (Waypoint waypoint : this.cheatBreaker.getModManager().zansMinimapMod.getVoxelMap().getWaypointManager().getWaypoints()) {
                    if (waypoint.isActive()) {
                        this.renderWaypoint(thePlayer, waypoint, f, f2, normalizeAngle);
                    }
                }

                for (TeamMember teamMember : CheatBreaker.getInstance().getModManager().teamManager.getTeamMemberList()) {
                    EntityPlayer playerEntityByUUID = this.mc.theWorld.getPlayerEntityByUUID(UUID.fromString(teamMember.getPlayerId()));
                    if (playerEntityByUUID == null) {
                        this.render(thePlayer, teamMember.getVec3().xCoord, teamMember.getVec3().zCoord, teamMember.getColor().getRGB(), f, normalizeAngle);
                    } else if (playerEntityByUUID != this.mc.thePlayer) {
                        this.render(thePlayer, playerEntityByUUID.posX, playerEntityByUUID.posZ, teamMember.getColor().getRGB(), f, normalizeAngle);
                    }
                }

                f2 += 8.0F;

                for (int i = 0; i <= 360; i += 15) {
                    this.render(f, f2, (Float)this.width.getValue(), i, normalizeAngle);
                }
            } else {
                GlStateManager.enableBlend();
                this.setHudSize(66.0F, 12.0F);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.render();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableBlend();
            }

            GlStateManager.popMatrix();
        }
    }
}
