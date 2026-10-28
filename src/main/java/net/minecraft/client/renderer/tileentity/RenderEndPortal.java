package net.minecraft.client.renderer.tileentity;

import com.cheatbreaker.client.CheatBreaker;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import org.lwjgl.opengl.GL11;
import shadersmod.client.ShadersRender;

public class RenderEndPortal extends TileEntitySpecialRenderer {
    public static ResourceLocation SKY_TEXTURE = new ResourceLocation("textures/environment/end_sky.png");
    public static ResourceLocation PORTAL_TEXTURE = new ResourceLocation("textures/entity/end_portal.png");
    public static Random RANDOM = new Random(31100L);
    public FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);

    public void renderTileEntityAt(TileEntityEndPortal tileEntityEndPortal, double d, double d2, double d3, float f) {
        if (!CheatBreaker.getInstance().getKeybindSettings().hideEndPortals.getBooleanValue()) {
            if (!Config.isShaders() || !ShadersRender.renderEndPortal(tileEntityEndPortal, d, d2, d3, f, 0, 0.75F)) {
                float cameraX = (float)this.dispatcher.cameraX;
                float cameraY = (float)this.dispatcher.cameraY;
                float cameraZ = (float)this.dispatcher.cameraZ;
                GL11.glDisable(2896);
                RANDOM.setSeed(31100L);
                float f2 = 0.75F;

                for (int i = 0; i < 16; i++) {
                    GL11.glPushMatrix();
                    float f3 = 16 - i;
                    float f4 = 0.0625F;
                    float f5 = 1.0F / (f3 + 1.0F);
                    if (i == 0) {
                        this.bindTexture(SKY_TEXTURE);
                        f5 = 0.1F;
                        f3 = 65.0F;
                        f4 = 0.125F;
                        GL11.glEnable(3042);
                        GL11.glBlendFunc(770, 771);
                    }

                    if (i == 1) {
                        this.bindTexture(PORTAL_TEXTURE);
                        GL11.glEnable(3042);
                        GL11.glBlendFunc(1, 1);
                        f4 = 0.5F;
                    }

                    float f6 = (float)(-(d2 + f2));
                    float f7 = f6 + ActiveRenderInfo.objectY;
                    float f8 = f6 + f3 + ActiveRenderInfo.objectY;
                    float f9 = f7 / f8;
                    f9 += (float)(d2 + f2);
                    GL11.glTranslatef(cameraX, f9, cameraZ);
                    GL11.glTexGeni(8192, 9472, 9217);
                    GL11.glTexGeni(8193, 9472, 9217);
                    GL11.glTexGeni(8194, 9472, 9217);
                    GL11.glTexGeni(8195, 9472, 9216);
                    GL11.glTexGen(8192, 9473, this.createBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GL11.glTexGen(8193, 9473, this.createBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GL11.glTexGen(8194, 9473, this.createBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GL11.glTexGen(8195, 9474, this.createBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GL11.glEnable(3168);
                    GL11.glEnable(3169);
                    GL11.glEnable(3170);
                    GL11.glEnable(3171);
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(5890);
                    GL11.glPushMatrix();
                    GL11.glLoadIdentity();
                    GL11.glTranslatef(0.0F, (float)(Minecraft.getSystemTime() % 700000L) / 700000.0F, 0.0F);
                    GL11.glScalef(f4, f4, f4);
                    GL11.glTranslatef(0.5F, 0.5F, 0.0F);
                    GL11.glRotatef((i * i * 4321 + i * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
                    GL11.glTranslatef(-cameraX, -cameraZ, -cameraY);
                    f7 = f6 + ActiveRenderInfo.objectY;
                    GL11.glTranslatef(ActiveRenderInfo.objectX * f3 / f7, ActiveRenderInfo.objectZ * f3 / f7, -cameraY);
                    Tessellator instance = Tessellator.instance;
                    instance.startDrawingQuads();
                    f9 = RANDOM.nextFloat() * 0.5F + 0.1F;
                    float f10 = RANDOM.nextFloat() * 0.5F + 0.4F;
                    float f11 = RANDOM.nextFloat() * 0.5F + 0.5F;
                    if (i == 0) {
                        f11 = 1.0F;
                        f10 = 1.0F;
                        f9 = 1.0F;
                    }

                    instance.setColorRGBA_F(f9 * f5, f10 * f5, f11 * f5, 1.0F);
                    instance.addVertex(d, d2 + f2, d3);
                    instance.addVertex(d, d2 + f2, d3 + 1.0);
                    instance.addVertex(d + 1.0, d2 + f2, d3 + 1.0);
                    instance.addVertex(d + 1.0, d2 + f2, d3);
                    instance.draw();
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(5888);
                }

                GL11.glDisable(3042);
                GL11.glDisable(3168);
                GL11.glDisable(3169);
                GL11.glDisable(3170);
                GL11.glDisable(3171);
                GL11.glEnable(2896);
            }
        }
    }

    public FloatBuffer createBuffer(float f, float f2, float f3, float f4) {
        ((Buffer)this.buffer).clear();
        this.buffer.put(f).put(f2).put(f3).put(f4);
        ((Buffer)this.buffer).flip();
        return this.buffer;
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double d, double d2, double d3, float f) {
        this.renderTileEntityAt((TileEntityEndPortal)tileEntity, d, d2, d3, f);
    }
}
