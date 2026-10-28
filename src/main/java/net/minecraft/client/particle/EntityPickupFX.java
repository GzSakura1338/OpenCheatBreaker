package net.minecraft.client.particle;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.optifine.Config;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;

public class EntityPickupFX extends EntityFX {
    public Entity entityToPickUp;
    public Entity entityPickingUp;
    public int age;
    public int maxAge;
    public float yOffs;

    public EntityPickupFX(World world, Entity entity, Entity entity2, float f) {
        super(world, entity.posX, entity.posY, entity.posZ, entity.motionX, entity.motionY, entity.motionZ);
        this.entityToPickUp = entity;
        this.entityPickingUp = entity2;
        this.maxAge = 3;
        this.yOffs = f;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float f, float f2, float f3, float f4, float f5, float f6) {
        int n = 0;
        if (Config.isShaders()) {
            n = Shaders.activeProgram;
            Shaders.nextEntity(this.entityToPickUp);
        }

        float f7 = (this.age + f) / this.maxAge;
        f7 *= f7;
        double posX = this.entityToPickUp.posX;
        double posY = this.entityToPickUp.posY;
        double posZ = this.entityToPickUp.posZ;
        double d = this.entityPickingUp.lastTickPosX + (this.entityPickingUp.posX - this.entityPickingUp.lastTickPosX) * f;
        double d2 = this.entityPickingUp.lastTickPosY + (this.entityPickingUp.posY - this.entityPickingUp.lastTickPosY) * f + this.yOffs;
        double d3 = this.entityPickingUp.lastTickPosZ + (this.entityPickingUp.posZ - this.entityPickingUp.lastTickPosZ) * f;
        double d4 = posX + (d - posX) * f7;
        double d5 = posY + (d2 - posY) * f7;
        double d6 = posZ + (d3 - posZ) * f7;
        int brightnessForRender = this.getBrightnessForRender(f);
        int n2 = brightnessForRender % 65536;
        int n3 = brightnessForRender / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n2 / 1.0F, n3 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        d4 -= interpPosX;
        d5 -= interpPosY;
        d6 -= interpPosZ;
        RenderManager.instance.renderEntityWithPosYaw(this.entityToPickUp, (float)d4, (float)d5, (float)d6, this.entityToPickUp.rotationYaw, f);
        if (Config.isShaders()) {
            Shaders.useProgram(n);
        }
    }

    @Override
    public void onUpdate() {
        this.age++;
        if (this.age == this.maxAge) {
            this.setDead();
        }
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}
