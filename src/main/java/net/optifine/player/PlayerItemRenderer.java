package net.optifine.player;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class PlayerItemRenderer {
   public int attachTo = 0;
   public ModelRenderer modelRenderer = null;

   public PlayerItemRenderer(int n, ModelRenderer modelRenderer) {
      this.attachTo = n;
      this.modelRenderer = modelRenderer;
   }

   public ModelRenderer getModelRenderer() {
      return this.modelRenderer;
   }

   public void render(ModelBiped modelBiped, float f) {
      ModelRenderer attachModel = PlayerItemModel.getAttachModel(modelBiped, this.attachTo);
      if (attachModel != null) {
         attachModel.postRender(f);
      }

      this.modelRenderer.render(f);
   }
}
