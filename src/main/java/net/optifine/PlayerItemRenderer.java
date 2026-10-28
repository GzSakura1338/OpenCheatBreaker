package net.optifine;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class PlayerItemRenderer {
    public int attachTo = 0;
    public float scaleFactor = 0.0F;
    public ModelRenderer modelRenderer = null;

    public PlayerItemRenderer(int n, float f, ModelRenderer modelRenderer) {
        this.attachTo = n;
        this.scaleFactor = f;
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

        this.modelRenderer.render(f * this.scaleFactor);
    }
}
