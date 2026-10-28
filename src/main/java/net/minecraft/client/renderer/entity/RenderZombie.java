package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderZombie extends RenderBiped {
    public static ResourceLocation zombiePigmanTextures = new ResourceLocation("textures/entity/zombie_pigman.png");
    public static ResourceLocation zombieTextures = new ResourceLocation("textures/entity/zombie/zombie.png");
    public static ResourceLocation zombieVillagerTextures = new ResourceLocation("textures/entity/zombie/zombie_villager.png");
    public int intValue = 1;
    public ModelBiped modelBiped6 = this.modelBipedMain;
    public ModelBiped modelBiped2;
    public ModelBiped modelBiped3;
    public ModelBiped modelBiped4;
    public ModelBiped modelBiped5;
    public ModelZombieVillager zombieVillagerModel = new ModelZombieVillager();

    public RenderZombie() {
        super(new ModelZombie(), 0.5F, 1.0F);
    }

    @Override
    public void run() {
        this.modelArmourChestplate = new ModelZombie(1.0F, true);
        this.modelBiped = new ModelZombie(0.5F, true);
        this.modelBiped2 = this.modelArmourChestplate;
        this.modelBiped3 = this.modelBiped;
        this.modelBiped4 = new ModelZombieVillager(1.0F, 0.0F, true);
        this.modelBiped5 = new ModelZombieVillager(0.5F, 0.0F, true);
    }

    public int shouldRenderPass(EntityZombie entityZombie, int n, float f) {
        this.handleEntityZombie(entityZombie);
        return super.shouldRenderPass(entityZombie, n, f);
    }

    public void doRender(EntityZombie entityZombie, double d, double d2, double d3, float f, float f2) {
        this.handleEntityZombie(entityZombie);
        super.doRender(entityZombie, d, d2, d3, f, f2);
    }

    public ResourceLocation getEntityTexture(EntityZombie entityZombie) {
        return entityZombie instanceof EntityPigZombie ? zombiePigmanTextures : (entityZombie.isVillager() ? zombieVillagerTextures : zombieTextures);
    }

    public void renderEquippedItems(EntityZombie entityZombie, float f) {
        this.handleEntityZombie(entityZombie);
        super.renderEquippedItems(entityZombie, f);
    }

    public void handleEntityZombie(EntityZombie entityZombie) {
        if (entityZombie.isVillager()) {
            if (this.intValue != this.zombieVillagerModel.constantInt()) {
                this.zombieVillagerModel = new ModelZombieVillager();
                this.intValue = this.zombieVillagerModel.constantInt();
                this.modelBiped4 = new ModelZombieVillager(1.0F, 0.0F, true);
                this.modelBiped5 = new ModelZombieVillager(0.5F, 0.0F, true);
            }

            this.mainModel = this.zombieVillagerModel;
            this.modelArmourChestplate = this.modelBiped4;
            this.modelBiped = this.modelBiped5;
        } else {
            this.mainModel = this.modelBiped6;
            this.modelArmourChestplate = this.modelBiped2;
            this.modelBiped = this.modelBiped3;
        }

        this.modelBipedMain = (ModelBiped)this.mainModel;
    }

    public void rotateCorpse(EntityZombie entityZombie, float f, float f2, float f3) {
        if (entityZombie.isConverting()) {
            f2 += (float)(Math.cos(entityZombie.ticksExisted * 3.25) * Math.PI * 0.25);
        }

        super.rotateCorpse(entityZombie, f, f2, f3);
    }

    @Override
    public void renderEquippedItems(EntityLiving entityLiving, float f) {
        this.renderEquippedItems((EntityZombie)entityLiving, f);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityLiving entityLiving) {
        return this.getEntityTexture((EntityZombie)entityLiving);
    }

    @Override
    public void doRender(EntityLiving entityLiving, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityZombie)entityLiving, d, d2, d3, f, f2);
    }

    @Override
    public int shouldRenderPass(EntityLiving entityLiving, int n, float f) {
        return this.shouldRenderPass((EntityZombie)entityLiving, n, f);
    }

    @Override
    public int shouldRenderPass(EntityLivingBase entityLivingBase, int n, float f) {
        return this.shouldRenderPass((EntityZombie)entityLivingBase, n, f);
    }

    @Override
    public void renderEquippedItems(EntityLivingBase entityLivingBase, float f) {
        this.renderEquippedItems((EntityZombie)entityLivingBase, f);
    }

    @Override
    public void rotateCorpse(EntityLivingBase entityLivingBase, float f, float f2, float f3) {
        this.rotateCorpse((EntityZombie)entityLivingBase, f, f2, f3);
    }

    @Override
    public void doRender(EntityLivingBase entityLivingBase, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityZombie)entityLivingBase, d, d2, d3, f, f2);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityZombie)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityZombie)entity, d, d2, d3, f, f2);
    }
}
