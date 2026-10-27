package net.optifine.entity.model;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSheep1;
import net.minecraft.client.model.ModelSheep2;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSheep;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerSheepWool;
import net.minecraft.entity.passive.EntitySheep;
import net.optifine.Config;

public class ModelAdapterSheepWool extends ModelAdapterQuadruped {
   public ModelAdapterSheepWool() {
      super(EntitySheep.class, "sheep_wool", 0.7F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelSheep1();
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      Render render = renderManager.getEntityRenderMap().get(EntitySheep.class);
      if (!(render instanceof RenderSheep)) {
         Config.warn("Not a RenderSheep: " + render);
         return null;
      }

      if (render.getEntityClass() == null) {
         render = new RenderSheep(renderManager, new ModelSheep2(), 0.7F);
      }

      RenderSheep renderSheep = (RenderSheep)render;
      List layerRenderers = renderSheep.getLayerRenderers();
      Iterator iterator = layerRenderers.iterator();

      while (iterator.hasNext()) {
         LayerRenderer layerRenderer = (LayerRenderer)iterator.next();
         if (layerRenderer instanceof LayerSheepWool) {
            iterator.remove();
         }
      }

      LayerSheepWool layerSheepWool = new LayerSheepWool(renderSheep);
      layerSheepWool.sheepModel = (ModelSheep1)modelBase;
      renderSheep.addLayer(layerSheepWool);
      return renderSheep;
   }
}
