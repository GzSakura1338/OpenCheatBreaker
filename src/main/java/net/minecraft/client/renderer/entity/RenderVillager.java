package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelVillager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderVillager extends RenderLiving {
    public static ResourceLocation villagerTextures = new ResourceLocation("textures/entity/villager/villager.png");
    public static ResourceLocation farmerVillagerTextures = new ResourceLocation("textures/entity/villager/farmer.png");
    public static ResourceLocation librarianVillagerTextures = new ResourceLocation("textures/entity/villager/librarian.png");
    public static ResourceLocation priestVillagerTextures = new ResourceLocation("textures/entity/villager/priest.png");
    public static ResourceLocation smithVillagerTextures = new ResourceLocation("textures/entity/villager/smith.png");
    public static ResourceLocation butcherVillagerTextures = new ResourceLocation("textures/entity/villager/butcher.png");
    public ModelVillager villagerModel = (ModelVillager)this.mainModel;

    public RenderVillager() {
        super(new ModelVillager(0.0F), 0.5F);
    }

    public int shouldRenderPass(EntityVillager entityVillager, int n, float f) {
        return -1;
    }

    public void doRender(EntityVillager entityVillager, double d, double d2, double d3, float f, float f2) {
        super.doRender(entityVillager, d, d2, d3, f, f2);
    }

    public ResourceLocation getEntityTexture(EntityVillager entityVillager) {
        switch (entityVillager.getProfession()) {
            case 0:
                return farmerVillagerTextures;
            case 1:
                return librarianVillagerTextures;
            case 2:
                return priestVillagerTextures;
            case 3:
                return smithVillagerTextures;
            case 4:
                return butcherVillagerTextures;
            default:
                return villagerTextures;
        }
    }

    public void renderEquippedItems(EntityVillager entityVillager, float f) {
        super.renderEquippedItems(entityVillager, f);
    }

    public void preRenderCallback(EntityVillager entityVillager, float f) {
        float f2 = 0.9375F;
        if (entityVillager.getGrowingAge() < 0) {
            f2 = (float)(f2 * 0.5);
            this.shadowSize = 0.25F;
        } else {
            this.shadowSize = 0.5F;
        }

        GL11.glScalef(f2, f2, f2);
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityVillager)entityLiving, d, d2, d3, f, f2);
    }

    @Override
    public void preRenderCallback(EntityLivingBase entityLivingBase, float f) {
        this.preRenderCallback((EntityVillager)entityLivingBase, f);
    }

    @Override
    public int shouldRenderPass(EntityLivingBase entityLivingBase, int n, float f) {
        return this.shouldRenderPass((EntityVillager)entityLivingBase, n, f);
    }

    @Override
    public void renderEquippedItems(EntityLivingBase entityLivingBase, float f) {
        this.renderEquippedItems((EntityVillager)entityLivingBase, f);
    }

    @Override
    public void doRender(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityVillager)entityLivingBase, d, d2, d3, f, f2);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityVillager)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityVillager)entity, d, d2, d3, f, f2);
    }
}
