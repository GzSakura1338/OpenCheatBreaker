package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSquid extends RenderLiving {
    public static ResourceLocation squidTextures = new ResourceLocation("textures/entity/squid.png");

    public RenderSquid(ModelBase modelBase, float f) {
        super(modelBase, f);
    }

    public void doRender(EntitySquid entitySquid, double d, double d2, double d3, float f, float f2) {
        super.doRender(entitySquid, d, d2, d3, f, f2);
    }

    public ResourceLocation getEntityTexture(EntitySquid entitySquid) {
        return squidTextures;
    }

    public void rotateCorpse(EntitySquid entitySquid, float f, float f2, float f3) {
        float f4 = entitySquid.prevSquidPitch + (entitySquid.squidPitch - entitySquid.prevSquidPitch) * f3;
        float f5 = entitySquid.prevSquidYaw + (entitySquid.squidYaw - entitySquid.prevSquidYaw) * f3;
        GL11.glTranslatef(0.0F, 0.5F, 0.0F);
        GL11.glRotatef(180.0F - f2, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(f4, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(f5, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, -1.2F, 0.0F);
    }

    public float handleRotationFloat(EntitySquid entitySquid, float f) {
        return entitySquid.lastTentacleAngle + (entitySquid.tentacleAngle - entitySquid.lastTentacleAngle) * f;
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntitySquid)entityLiving, d, d2, d3, f, f2);
    }

    @Override
    public float handleRotationFloat(EntityLivingBase entityLivingBase, float f) {
        return this.handleRotationFloat((EntitySquid)entityLivingBase, f);
    }

    @Override
    public void rotateCorpse(EntityLivingBase entityLivingBase, float f, float f2, float f3) {
        this.rotateCorpse((EntitySquid)entityLivingBase, f, f2, f3);
    }

    @Override
    public void doRender(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntitySquid)entityLivingBase, d, d2, d3, f, f2);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntitySquid)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntitySquid)entity, d, d2, d3, f, f2);
    }
}
