package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerVillagerArmor;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderZombie extends RenderBiped<EntityZombie> {
   public static ResourceLocation zombieTextures = new ResourceLocation("textures/entity/zombie/zombie.png");
   public static ResourceLocation zombieVillagerTextures = new ResourceLocation("textures/entity/zombie/zombie_villager.png");
   public ModelBiped modelBiped;
   public ModelZombieVillager zombieVillagerModel;
   public List<LayerRenderer<EntityZombie>> layerRendererList;
   public List<LayerRenderer<EntityZombie>> layerRendererList2;

   public RenderZombie(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelZombie(), 0.5F, 1.0F);
      LayerRenderer layerRenderer = this.layerRenderers.get(0);
      this.modelBiped = this.modelBipedMain;
      this.zombieVillagerModel = new ModelZombieVillager();
      this.addLayer(new LayerHeldItem(this));
      LayerBipedArmor layerBipedArmor = new LayerBipedArmor(this) {
         @Override
         public void initArmor() {
            this.modelLeggings = new ModelZombie(0.5F, true);
            this.modelArmor = new ModelZombie(1.0F, true);
         }
      };
      this.addLayer(layerBipedArmor);
      this.layerRendererList = Lists.newArrayList(this.layerRenderers);
      if (layerRenderer instanceof LayerCustomHead) {
         this.removeLayer(layerRenderer);
         this.addLayer(new LayerCustomHead(this.zombieVillagerModel.bipedHead));
      }

      this.removeLayer(layerBipedArmor);
      this.addLayer(new LayerVillagerArmor(this));
      this.layerRendererList2 = Lists.newArrayList(this.layerRenderers);
   }

   public void doRender(EntityZombie entity, double x, double y, double z, float entityYaw, float partialTicks) {
      this.handleEntityZombie(entity);
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   public ResourceLocation getEntityTexture(EntityZombie entity) {
      return entity.isVillager() ? zombieVillagerTextures : zombieTextures;
   }

   public void handleEntityZombie(EntityZombie zombie) {
      if (zombie.isVillager()) {
         this.mainModel = this.zombieVillagerModel;
         this.layerRenderers = this.layerRendererList2;
      } else {
         this.mainModel = this.modelBiped;
         this.layerRenderers = this.layerRendererList;
      }

      this.modelBipedMain = (ModelBiped)this.mainModel;
   }

   public void rotateCorpse(EntityZombie bat, float f, float f2, float partialTicks) {
      if (bat.isConverting()) {
         f2 += (float)(Math.cos(bat.ticksExisted * 3.25) * Math.PI * 0.25);
      }

      super.rotateCorpse(bat, f, f2, partialTicks);
   }
}
