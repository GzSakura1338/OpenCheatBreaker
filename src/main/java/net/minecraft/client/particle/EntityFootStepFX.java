package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class EntityFootStepFX extends EntityFX {
    public static ResourceLocation FOOTPRINT = new ResourceLocation("textures/particle/footprint.png");
    public int footstepAge;
    public TextureManager currentFootSteps;
    public int footstepMaxAge;

    public EntityFootStepFX(TextureManager textureManager, World world, double d, double d2, double d3) {
        super(world, d, d2, d3, 0.0, 0.0, 0.0);
        this.currentFootSteps = textureManager;
        this.motionX = this.motionY = this.motionZ = 0.0;
        this.footstepMaxAge = 200;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float f, float f2, float f3, float f4, float f5, float f6) {
        float f7 = (this.footstepAge + f) / this.footstepMaxAge;
        f7 *= f7;
        float f8 = 2.0F - f7 * 2.0F;
        if (f8 > 1.0F) {
            f8 = 1.0F;
        }

        f8 *= 0.2F;
        GL11.glDisable(2896);
        float f9 = 0.125F;
        float f10 = (float)(this.posX - interpPosX);
        float f11 = (float)(this.posY - interpPosY);
        float f12 = (float)(this.posZ - interpPosZ);
        float lightBrightness = this.worldObj
            .getLightBrightness(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
        this.currentFootSteps.bindTexture(FOOTPRINT);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(lightBrightness, lightBrightness, lightBrightness, f8);
        tessellator.addVertexWithUV(f10 - f9, f11, f12 + f9, 0.0, 1.0);
        tessellator.addVertexWithUV(f10 + f9, f11, f12 + f9, 1.0, 1.0);
        tessellator.addVertexWithUV(f10 + f9, f11, f12 - f9, 1.0, 0.0);
        tessellator.addVertexWithUV(f10 - f9, f11, f12 - f9, 0.0, 0.0);
        tessellator.draw();
        GL11.glDisable(3042);
        GL11.glEnable(2896);
    }

    @Override
    public void onUpdate() {
        this.footstepAge++;
        if (this.footstepAge == this.footstepMaxAge) {
            this.setDead();
        }
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}
