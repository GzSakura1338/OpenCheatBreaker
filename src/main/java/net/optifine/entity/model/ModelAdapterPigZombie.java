package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPigZombie;
import net.minecraft.entity.monster.EntityPigZombie;

public class ModelAdapterPigZombie extends ModelAdapterBiped {
   public ModelAdapterPigZombie() {
      super(EntityPigZombie.class, "zombie_pigman", 0.5F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelZombie();
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      RenderPigZombie renderPigZombie = new RenderPigZombie(renderManager);
      Render.setModelBipedMain(renderPigZombie, (ModelBiped)modelBase);
      renderPigZombie.mainModel = modelBase;
      renderPigZombie.shadowSize = f;
      return renderPigZombie;
   }
}
