package com.cheatbreaker.client.cosmetic;

import com.cheatbreaker.client.util.FastTrig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.GL11;

public class WingModel extends ModelBase {
    public ModelRenderer wing;
    public static long lastUpdateMillis = System.currentTimeMillis();
    public static long elapsedMillis;
    public ModelRenderer wingtip;

    public WingModel() {
        this.textureWidth = 256;
        this.textureHeight = 256;
        this.setTextureOffset("wing.bone", 112, 88);
        this.setTextureOffset("wingtip.bone", 112, 136);
        this.wing = new ModelRenderer(this, "wing");
        this.wing.setRotationPoint(-12.0F, 5.0F, 2.0F);
        this.wing.addBox("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8);
        this.wing.cubeList.add(new WingBox(this.wing, 56, 88, -56.0F, 0.0F, 2.0F, 56, 56));
        this.wingtip = new ModelRenderer(this, "wingtip");
        this.wingtip.setRotationPoint(-56.0F, 0.0F, 0.0F);
        this.wingtip.addBox("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4);
        this.wingtip.cubeList.add(new WingBox(this.wingtip, 56, 144, -56.0F, 0.0F, 2.0F, 56, 56));
        this.wing.addChild(this.wingtip);
    }

    public void render(Entity entity, float f, float f2, ResourceLocation resourceLocation) {
        if (entity instanceof EntityPlayer) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
            GlStateManager.pushMatrix();
            GL11.glScaled(f2, f2, f2);
            GlStateManager.rotate(15.0F, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, 0.5F, 0.25F);
            long currentTimeMillis = System.currentTimeMillis();
            if (!Minecraft.getMinecraft().isGamePaused()) {
                elapsedMillis = elapsedMillis + (currentTimeMillis - lastUpdateMillis);
            }

            lastUpdateMillis = currentTimeMillis;
            float f3 = (float)(elapsedMillis % 2000L) / 2000.0F * (float) Math.PI * 2.0F;

            for (int i = 0; i < 2; i++) {
                GL11.glEnable(2884);
                this.wing.rotateAngleX = -0.125F - (float)FastTrig.cos(f3) * 0.2F;
                this.wing.rotateAngleY = 0.75F;
                this.wing.rotateAngleZ = (float)(FastTrig.sin(f3) + 0.125) * 0.8F;
                this.wingtip.rotateAngleZ = (float)(FastTrig.sin(f3 + 2.0F) + 0.5) * 0.75F;
                this.wing.render(f);
                GL11.glScalef(-1.0F, 1.0F, 1.0F);
                if (i == 0) {
                    GL11.glCullFace(1028);
                }
            }

            GlStateManager.popMatrix();
            GL11.glCullFace(1029);
            GL11.glDisable(2884);
        }
    }
}
