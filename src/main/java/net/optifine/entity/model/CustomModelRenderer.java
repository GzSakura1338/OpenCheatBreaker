package net.optifine.entity.model;

import net.minecraft.client.model.ModelRenderer;
import net.optifine.entity.model.anim.ModelUpdater;

public class CustomModelRenderer {
   public String modelPart;
   public boolean attach;
   public ModelRenderer modelRenderer;
   public ModelUpdater modelUpdater;

   public CustomModelRenderer(String str, boolean flag, ModelRenderer modelRenderer, ModelUpdater modelUpdater) {
      this.modelPart = str;
      this.attach = flag;
      this.modelRenderer = modelRenderer;
      this.modelUpdater = modelUpdater;
   }

   public ModelRenderer getModelRenderer() {
      return this.modelRenderer;
   }

   public String getModelPart() {
      return this.modelPart;
   }

   public boolean isAttach() {
      return this.attach;
   }

   public ModelUpdater getModelUpdater() {
      return this.modelUpdater;
   }
}
