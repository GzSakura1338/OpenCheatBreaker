package net.optifine.entity.model;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRabbit;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderRabbit;
import net.minecraft.entity.passive.EntityRabbit;
import net.optifine.reflect.Reflector;

public class ModelAdapterRabbit extends ModelAdapter {
   public static Map<String, Integer> mapPartFields = null;

   public ModelAdapterRabbit() {
      super(EntityRabbit.class, "rabbit", 0.3F);
   }

   @Override
   public ModelBase makeModel() {
      return new ModelRabbit();
   }

   @Override
   public ModelRenderer getModelRenderer(ModelBase modelBase, String str) {
      if (!(modelBase instanceof ModelRabbit)) {
         return null;
      } else {
         ModelRabbit modelRabbit = (ModelRabbit)modelBase;
         Map mapPartFields = getMapPartFields();
         if (mapPartFields.containsKey(str)) {
            int n = (Integer)mapPartFields.get(str);
            return (ModelRenderer)Reflector.getFieldValue(modelRabbit, Reflector.ModelRabbit_renderers, n);
         } else {
            return null;
         }
      }
   }

   @Override
   public String[] getModelRendererNames() {
      return new String[]{
         "left_foot", "right_foot", "left_thigh", "right_thigh", "body", "left_arm", "right_arm", "head", "right_ear", "left_ear", "tail", "nose"
      };
   }

   public static Map<String, Integer> getMapPartFields() {
      if (mapPartFields != null) {
         return mapPartFields;
      }

      mapPartFields = new HashMap<>();
      mapPartFields.put("left_foot", 0);
      mapPartFields.put("right_foot", 1);
      mapPartFields.put("left_thigh", 2);
      mapPartFields.put("right_thigh", 3);
      mapPartFields.put("body", 4);
      mapPartFields.put("left_arm", 5);
      mapPartFields.put("right_arm", 6);
      mapPartFields.put("head", 7);
      mapPartFields.put("right_ear", 8);
      mapPartFields.put("left_ear", 9);
      mapPartFields.put("tail", 10);
      mapPartFields.put("nose", 11);
      return mapPartFields;
   }

   @Override
   public IEntityRenderer makeEntityRender(ModelBase modelBase, float f) {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      return new RenderRabbit(renderManager, modelBase, f);
   }
}
