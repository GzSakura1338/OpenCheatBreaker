package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.CheatBreaker;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class WaypointRenderer extends Render {
    public MinimapSettings minimapSettings = null;

    public void renderBeam(Waypoint waypoint, double d, double d2, double d3, float f, double d4) {
        Tessellator instance = Tessellator.instance;
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glDisable(2912);
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        short s = 256;
        float f2 = 0.06F;
        double d5 = 1.05;
        double d6 = 1.05;
        float red = waypoint.red;
        float blue = waypoint.blue;
        float green = waypoint.green;
        float f3 = d4 < 30.0 ? (float)d4 / 30.0F : 1.0F;
        if (f3 < 0.2F) {
            f3 = 0.2F;
        }

        for (int i = 0; i < 4; i++) {
            instance.startDrawing(5);
            instance.setColorRGBA_F(red * f2, green * f2, blue * f2, 0.8F * f3);
            double d7 = 0.1 + i * 0.2;
            d7 *= d5;
            double d8 = 0.1 + i * 0.2;
            d8 *= d6;

            for (int j = 0; j < 5; j++) {
                double d9 = d + 0.5 - d7;
                double d10 = d3 + 0.5 - d7;
                if (j == 1 || j == 2) {
                    d9 += d7 * 2.0;
                }

                if (j == 2 || j == 3) {
                    d10 += d7 * 2.0;
                }

                double d11 = d + 0.5 - d8;
                double d12 = d3 + 0.5 - d8;
                if (j == 1 || j == 2) {
                    d11 += d8 * 2.0;
                }

                if (j == 2 || j == 3) {
                    d12 += d8 * 2.0;
                }

                instance.addVertex(d11, d2 + 0.0, d12);
                instance.addVertex(d9, d2 + s, d10);
            }

            instance.draw();
        }

        GL11.glDisable(3042);
        GL11.glEnable(2912);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
        GL11.glDepthMask(true);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return new ResourceLocation("", "");
    }

    public WaypointRenderer(MinimapSettings minimapSettings) {
        this.minimapSettings = minimapSettings;
    }

    public void renderLabel(Waypoint waypoint, String str, double d, double d2, double d3, int n, double d4) {
        GL11.glAlphaFunc(516, 0.1F);
        if (d4 <= this.minimapSettings.waypointMaxDistance || this.minimapSettings.waypointMaxDistance < 0) {
            str = str + " [" + (int)d4 + "m]";
            double d5 = this.minimapSettings.mc.gameSettings.getOptionFloatValue(Options.RENDER_DISTANCE) * 16.0F * 0.75;
            double d6 = d4;
            if (d4 > d5) {
                d = d / d4 * d5;
                d2 = d2 / d4 * d5;
                d3 = d3 / d4 * d5;
                d6 = d5;
            }

            FontRenderer fontRendererFromRenderManager = this.getFontRendererFromRenderManager();
            float f = ((float)d6 * 0.1F + 1.0F) * 0.0266F;
            GL11.glPushMatrix();
            GL11.glTranslatef((float)d + 0.5F, (float)d2 + 1.3F, (float)d3 + 0.5F);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(-f, -f, f);
            GL11.glDisable(2896);
            GL11.glDisable(2912);
            GL11.glDepthMask(false);
            GL11.glDisable(2929);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            Tessellator instance = Tessellator.instance;
            byte b = 0;
            GL11.glDisable(3553);
            int n2 = fontRendererFromRenderManager.getStringWidth(str) / 2;
            int waypointBoxPadding = this.minimapSettings.waypointBoxPadding;
            GL11.glEnable(2929);
            if (d4 < d5) {
                GL11.glDepthMask(true);
            }

            float f2 = d4 < 30.0 ? (float)d4 / 30.0F : 1.0F;
            if (f2 < 0.2F) {
                f2 = 0.2F;
            }

            GL11.glEnable(32823);
            GL11.glPolygonOffset(1.0F, 3.0F);
            instance.startDrawingQuads();
            instance.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.4F * f2);
            instance.addVertex(-n2 - waypointBoxPadding, -waypointBoxPadding + b, 0.0);
            instance.addVertex(-n2 - waypointBoxPadding, 9 + waypointBoxPadding + b, 0.0);
            instance.addVertex(n2 + waypointBoxPadding, 9 + waypointBoxPadding + b, 0.0);
            instance.addVertex(n2 + waypointBoxPadding, -waypointBoxPadding + b, 0.0);
            instance.draw();
            if (this.minimapSettings.waypointBorder) {
                GL11.glPolygonOffset(1.0F, 2.0F);
                float f3 = 0.9F * f2;
                this.drawColoredQuad(instance, -n2 - waypointBoxPadding, -waypointBoxPadding + b, n2 + waypointBoxPadding, -waypointBoxPadding + 1 + b, waypoint, f3);
                this.drawColoredQuad(instance, -n2 - waypointBoxPadding, 8 + waypointBoxPadding + b, n2 + waypointBoxPadding, 9 + waypointBoxPadding + b, waypoint, f3);
                this.drawColoredQuad(instance, -n2 - waypointBoxPadding, -waypointBoxPadding + b, -n2 - waypointBoxPadding + 1, 9 + waypointBoxPadding + b, waypoint, f3);
                this.drawColoredQuad(instance, n2 + waypointBoxPadding - 1, -waypointBoxPadding + b, n2 + waypointBoxPadding, 9 + waypointBoxPadding + b, waypoint, f3);
            }

            GL11.glDisable(32823);
            GL11.glEnable(3553);
            int n3 = 16777215 | (int)(255.0F * f2) << 24;
            fontRendererFromRenderManager.drawString(str, -n2, b, n3, this.minimapSettings.waypointTextShadow);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glEnable(2912);
            GL11.glEnable(2896);
            GL11.glDisable(3042);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.renderWaypoints((EntityWaypointContainer)entity, d, d2, d3, f, f2);
    }

    public void drawColoredQuad(Tessellator tessellator, float f, float f2, float f3, float f4, Waypoint waypoint, float f5) {
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(waypoint.red, waypoint.green, waypoint.blue, f5);
        tessellator.addVertex(f, f2, 0.0);
        tessellator.addVertex(f, f4, 0.0);
        tessellator.addVertex(f3, f4, 0.0);
        tessellator.addVertex(f3, f2, 0.0);
        tessellator.draw();
    }

    public void renderWaypoints(EntityWaypointContainer entityWaypointContainer, double d, double d2, double d3, float f, float f2) {
        if (CheatBreaker.getInstance().getModManager().zansMinimapMod.isEnabled()) {
            for (Waypoint waypoint : entityWaypointContainer.waypoints) {
                if (waypoint.isActive()) {
                    int n = waypoint.getX();
                    int n2 = waypoint.getZ();
                    int n3 = waypoint.getY();
                    double sqrt = Math.sqrt(waypoint.getDistanceSqToEntity(this.renderManager.livingPlayer));
                    if (this.minimapSettings.waypointBeacons && entityWaypointContainer.worldObj.getChunkFromBlockCoords(n, n2).isChunkLoaded) {
                        double d4 = 0.0 - RenderManager.renderPosY;
                        this.renderBeam(waypoint, n - RenderManager.renderPosX, d4, n2 - RenderManager.renderPosZ, 64.0F, sqrt);
                    }

                    if (this.minimapSettings.waypointSigns && !this.minimapSettings.mc.gameSettings.hideGUI) {
                        String name = waypoint.name;
                        this.renderLabel(waypoint, name, d + n, d2 + n3 + 1.0, d3 + n2, 64, sqrt);
                    }
                }
            }
        }
    }
}
