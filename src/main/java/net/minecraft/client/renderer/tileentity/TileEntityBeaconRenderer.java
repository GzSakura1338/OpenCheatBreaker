package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;

public class TileEntityBeaconRenderer extends TileEntitySpecialRenderer {
    public static ResourceLocation beaconBeam = new ResourceLocation("textures/entity/beacon_beam.png");

    public void renderTileEntityAt(TileEntityBeacon tileEntityBeacon, double d, double d2, double d3, float f) {
        float shouldBeamRender = tileEntityBeacon.shouldBeamRender();
        if (shouldBeamRender > 0.0F) {
            if (Config.isShaders()) {
                Shaders.beginBeacon();
            }

            GL11.glAlphaFunc(516, 0.1F);
            if (shouldBeamRender > 0.0F) {
                Tessellator instance = Tessellator.instance;
                this.bindTexture(beaconBeam);
                GL11.glTexParameterf(3553, 10242, 10497.0F);
                GL11.glTexParameterf(3553, 10243, 10497.0F);
                GL11.glDisable(2896);
                GL11.glDisable(2884);
                GL11.glDisable(3042);
                GL11.glDepthMask(true);
                OpenGlHelper.glBlendFunc(770, 1, 1, 0);
                float f2 = (float)tileEntityBeacon.getWorld().getTotalWorldTime() + f;
                float f3 = -f2 * 0.2F - MathHelper.floor_float(-f2 * 0.1F);
                byte b = 1;
                double d4 = f2 * 0.025 * (1.0 - (b & 1) * 2.5);
                instance.startDrawingQuads();
                instance.setColorRGBA(255, 255, 255, 32);
                double d5 = b * 0.2;
                double d6 = 0.5 + Math.cos(d4 + (Math.PI * 3.0 / 4.0)) * d5;
                double d7 = 0.5 + Math.sin(d4 + (Math.PI * 3.0 / 4.0)) * d5;
                double d8 = 0.5 + Math.cos(d4 + (Math.PI / 4)) * d5;
                double d9 = 0.5 + Math.sin(d4 + (Math.PI / 4)) * d5;
                double d10 = 0.5 + Math.cos(d4 + (Math.PI * 5.0 / 4.0)) * d5;
                double d11 = 0.5 + Math.sin(d4 + (Math.PI * 5.0 / 4.0)) * d5;
                double d12 = 0.5 + Math.cos(d4 + (Math.PI * 7.0 / 4.0)) * d5;
                double d13 = 0.5 + Math.sin(d4 + (Math.PI * 7.0 / 4.0)) * d5;
                double d14 = 256.0F * shouldBeamRender;
                double d15 = 0.0;
                double d16 = 1.0;
                double d17 = -1.0F + f3;
                double d18 = 256.0F * shouldBeamRender * (0.5 / d5) + d17;
                instance.addVertexWithUV(d + d6, d2 + d14, d3 + d7, d16, d18);
                instance.addVertexWithUV(d + d6, d2, d3 + d7, d16, d17);
                instance.addVertexWithUV(d + d8, d2, d3 + d9, d15, d17);
                instance.addVertexWithUV(d + d8, d2 + d14, d3 + d9, d15, d18);
                instance.addVertexWithUV(d + d12, d2 + d14, d3 + d13, d16, d18);
                instance.addVertexWithUV(d + d12, d2, d3 + d13, d16, d17);
                instance.addVertexWithUV(d + d10, d2, d3 + d11, d15, d17);
                instance.addVertexWithUV(d + d10, d2 + d14, d3 + d11, d15, d18);
                instance.addVertexWithUV(d + d8, d2 + d14, d3 + d9, d16, d18);
                instance.addVertexWithUV(d + d8, d2, d3 + d9, d16, d17);
                instance.addVertexWithUV(d + d12, d2, d3 + d13, d15, d17);
                instance.addVertexWithUV(d + d12, d2 + d14, d3 + d13, d15, d18);
                instance.addVertexWithUV(d + d10, d2 + d14, d3 + d11, d16, d18);
                instance.addVertexWithUV(d + d10, d2, d3 + d11, d16, d17);
                instance.addVertexWithUV(d + d6, d2, d3 + d7, d15, d17);
                instance.addVertexWithUV(d + d6, d2 + d14, d3 + d7, d15, d18);
                instance.draw();
                GL11.glEnable(3042);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                GL11.glDepthMask(false);
                instance.startDrawingQuads();
                instance.setColorRGBA(255, 255, 255, 32);
                double d19 = 0.2;
                double d20 = 0.2;
                double d21 = 0.8;
                double d22 = 0.2;
                double d23 = 0.2;
                double d24 = 0.8;
                double d25 = 0.8;
                double d26 = 0.8;
                double d27 = 256.0F * shouldBeamRender;
                double d28 = 0.0;
                double d29 = 1.0;
                double d30 = -1.0F + f3;
                double d31 = 256.0F * shouldBeamRender + d30;
                instance.addVertexWithUV(d + d19, d2 + d27, d3 + d20, d29, d31);
                instance.addVertexWithUV(d + d19, d2, d3 + d20, d29, d30);
                instance.addVertexWithUV(d + d21, d2, d3 + d22, d28, d30);
                instance.addVertexWithUV(d + d21, d2 + d27, d3 + d22, d28, d31);
                instance.addVertexWithUV(d + d25, d2 + d27, d3 + d26, d29, d31);
                instance.addVertexWithUV(d + d25, d2, d3 + d26, d29, d30);
                instance.addVertexWithUV(d + d23, d2, d3 + d24, d28, d30);
                instance.addVertexWithUV(d + d23, d2 + d27, d3 + d24, d28, d31);
                instance.addVertexWithUV(d + d21, d2 + d27, d3 + d22, d29, d31);
                instance.addVertexWithUV(d + d21, d2, d3 + d22, d29, d30);
                instance.addVertexWithUV(d + d25, d2, d3 + d26, d28, d30);
                instance.addVertexWithUV(d + d25, d2 + d27, d3 + d26, d28, d31);
                instance.addVertexWithUV(d + d23, d2 + d27, d3 + d24, d29, d31);
                instance.addVertexWithUV(d + d23, d2, d3 + d24, d29, d30);
                instance.addVertexWithUV(d + d19, d2, d3 + d20, d28, d30);
                instance.addVertexWithUV(d + d19, d2 + d27, d3 + d20, d28, d31);
                instance.draw();
                GL11.glEnable(2896);
                GL11.glEnable(3553);
                GL11.glDepthMask(true);
            }

            if (Config.isShaders()) {
                Shaders.endBeacon();
            }
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double d, double d2, double d3, float f) {
        this.renderTileEntityAt((TileEntityBeacon)tileEntity, d, d2, d3, f);
    }
}
