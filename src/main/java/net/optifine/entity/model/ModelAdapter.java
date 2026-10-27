package net.optifine.entity.model;

import java.util.ArrayList;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public abstract class ModelAdapter {
   public Class entityClass;
   public String name;
   public float shadowSize;
   public String[] aliases;

   public ModelAdapter(Class clazz, String str, float f) {
      this.entityClass = clazz;
      this.name = str;
      this.shadowSize = f;
   }

   public ModelAdapter(Class clazz, String str, float f, String[] strs) {
      this.entityClass = clazz;
      this.name = str;
      this.shadowSize = f;
      this.aliases = strs;
   }

   public Class getEntityClass() {
      return this.entityClass;
   }

   public String getName() {
      return this.name;
   }

   public String[] getAliases() {
      return this.aliases;
   }

   public float getShadowSize() {
      return this.shadowSize;
   }

   public abstract ModelBase makeModel();

   public abstract ModelRenderer getModelRenderer(ModelBase modelBase, String str);

   public abstract String[] getModelRendererNames();

   public abstract IEntityRenderer makeEntityRender(ModelBase modelBase, float f);

   public ModelRenderer[] getModelRenderers(ModelBase modelBase) {
      String[] modelRendererNames = this.getModelRendererNames();
      ArrayList list = new ArrayList();

      for (int i = 0; i < modelRendererNames.length; i++) {
         String str = modelRendererNames[i];
         ModelRenderer modelRenderer = this.getModelRenderer(modelBase, str);
         if (modelRenderer != null) {
            list.add(modelRenderer);
         }
      }

      return (ModelRenderer[])list.toArray(new ModelRenderer[list.size()]);
   }
}
