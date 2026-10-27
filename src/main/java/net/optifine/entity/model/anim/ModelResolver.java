package net.optifine.entity.model.anim;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;
import net.optifine.Config;
import net.optifine.entity.model.CustomModelRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.expr.IExpression;

public class ModelResolver implements IModelResolver {
   public ModelAdapter modelAdapter;
   public ModelBase model;
   public CustomModelRenderer[] customModelRenderers;
   public ModelRenderer thisModelRenderer;
   public ModelRenderer partModelRenderer;
   public IRenderResolver renderResolver;

   public ModelResolver(ModelAdapter modelAdapter, ModelBase modelBase, CustomModelRenderer[] customModelRenderers) {
      this.modelAdapter = modelAdapter;
      this.model = modelBase;
      this.customModelRenderers = customModelRenderers;
      Class entityClass = modelAdapter.getEntityClass();
      if (TileEntity.class.isAssignableFrom(entityClass)) {
         this.renderResolver = new RenderResolverTileEntity();
      } else {
         this.renderResolver = new RenderResolverEntity();
      }
   }

   @Override
   public IExpression getExpression(String str) {
      ModelVariableFloat modelVariable = this.getModelVariable(str);
      if (modelVariable != null) {
         return modelVariable;
      }

      IExpression parameter = this.renderResolver.getParameter(str);
      return parameter != null ? parameter : null;
   }

   @Override
   public ModelRenderer getModelRenderer(String str) {
      if (str == null) {
         return null;
      }

      if (str.indexOf(":") >= 0) {
         String[] tokenize = Config.tokenize(str, ":");
         ModelRenderer modelRenderer = this.getModelRenderer(tokenize[0]);

         for (int i = 1; i < tokenize.length; i++) {
            String str2 = tokenize[i];
            ModelRenderer childDeep = modelRenderer.getChildDeep(str2);
            if (childDeep == null) {
               return null;
            }

            modelRenderer = childDeep;
         }

         return modelRenderer;
      } else {
         if (this.thisModelRenderer != null && str.equals("this")) {
            return this.thisModelRenderer;
         }

         if (this.partModelRenderer != null && str.equals("part")) {
            return this.partModelRenderer;
         }

         ModelRenderer modelRenderer = this.modelAdapter.getModelRenderer(this.model, str);
         if (modelRenderer != null) {
            return modelRenderer;
         }

         for (int i = 0; i < this.customModelRenderers.length; i++) {
            CustomModelRenderer customModelRenderer = this.customModelRenderers[i];
            ModelRenderer modelRenderer2 = customModelRenderer.getModelRenderer();
            if (str.equals(modelRenderer2.getId())) {
               return modelRenderer2;
            }

            ModelRenderer childDeep = modelRenderer2.getChildDeep(str);
            if (childDeep != null) {
               return childDeep;
            }
         }

         return null;
      }
   }

   @Override
   public ModelVariableFloat getModelVariable(String str) {
      String[] tokenize = Config.tokenize(str, ".");
      if (tokenize.length != 2) {
         return null;
      }

      String str2 = tokenize[0];
      String str3 = tokenize[1];
      ModelRenderer modelRenderer = this.getModelRenderer(str2);
      if (modelRenderer == null) {
         return null;
      }

      ModelVariableType parse = ModelVariableType.parse(str3);
      return parse == null ? null : new ModelVariableFloat(str, modelRenderer, parse);
   }

   public void setPartModelRenderer(ModelRenderer modelRenderer) {
      this.partModelRenderer = modelRenderer;
   }

   public void setThisModelRenderer(ModelRenderer modelRenderer) {
      this.thisModelRenderer = modelRenderer;
   }
}
