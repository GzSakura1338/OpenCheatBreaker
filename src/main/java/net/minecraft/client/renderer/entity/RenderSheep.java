package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSheep extends RenderLiving {
    public static ResourceLocation sheepTextures = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
    public static ResourceLocation shearedSheepTextures = new ResourceLocation("textures/entity/sheep/sheep.png");

    public RenderSheep(ModelBase modelBase, ModelBase modelBase2, float f) {
        super(modelBase, f);
        this.setRenderPassModel(modelBase2);
    }

    public int shouldRenderPass(EntitySheep entitySheep, int n, float f) {
        if (n == 0 && !entitySheep.getSheared()) {
            this.bindTexture(sheepTextures);
            if (entitySheep.hasCustomNameTag() && "jeb_".equals(entitySheep.getCustomNameTag())) {
                boolean flag = true;
                int n2 = entitySheep.ticksExisted / 25 + entitySheep.getEntityId();
                int n3 = n2 % EntitySheep.fleeceColorTable.length;
                int n4 = (n2 + 1) % EntitySheep.fleeceColorTable.length;
                float f2 = (entitySheep.ticksExisted % 25 + f) / 25.0F;
                GL11.glColor3f(
                    EntitySheep.fleeceColorTable[n3][0] * (1.0F - f2) + EntitySheep.fleeceColorTable[n4][0] * f2,
                    EntitySheep.fleeceColorTable[n3][1] * (1.0F - f2) + EntitySheep.fleeceColorTable[n4][1] * f2,
                    EntitySheep.fleeceColorTable[n3][2] * (1.0F - f2) + EntitySheep.fleeceColorTable[n4][2] * f2
                );
            } else {
                int fleeceColor = entitySheep.getFleeceColor();
                GL11.glColor3f(EntitySheep.fleeceColorTable[fleeceColor][0], EntitySheep.fleeceColorTable[fleeceColor][1], EntitySheep.fleeceColorTable[fleeceColor][2]);
            }

            return 1;
        } else {
            return -1;
        }
    }

    public ResourceLocation getEntityTexture(EntitySheep entitySheep) {
        return shearedSheepTextures;
    }

    @Override
    public int shouldRenderPass(EntityLivingBase entityLivingBase, int n, float f) {
        return this.shouldRenderPass((EntitySheep)entityLivingBase, n, f);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntitySheep)entity);
    }
}
