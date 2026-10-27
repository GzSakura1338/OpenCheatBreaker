package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.model.ModelEnderman;
import net.minecraft.client.renderer.entity.layers.LayerEndermanEyes;
import net.minecraft.client.renderer.entity.layers.LayerHeldBlock;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.ResourceLocation;

public class RenderEnderman extends RenderLiving<EntityEnderman> {
   public static ResourceLocation endermanTextures = new ResourceLocation("textures/entity/enderman/enderman.png");
   public Random rnd = new Random();
   public ModelEnderman endermanModel = (ModelEnderman)this.mainModel;

   public RenderEnderman(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelEnderman(0.0F), 0.5F);
      this.addLayer(new LayerEndermanEyes(this));
      this.addLayer(new LayerHeldBlock(this));
   }

   public void doRender(EntityEnderman entity, double x, double y, double z, float entityYaw, float partialTicks) {
      this.endermanModel.isCarrying = entity.getHeldBlockState().getBlock().getMaterial() != Material.air;
      this.endermanModel.isAttacking = entity.isScreaming();
      if (entity.isScreaming()) {
         double d = 0.02;
         x += this.rnd.nextGaussian() * d;
         z += this.rnd.nextGaussian() * d;
      }

      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   public ResourceLocation getEntityTexture(EntityEnderman entity) {
      return endermanTextures;
   }
}
