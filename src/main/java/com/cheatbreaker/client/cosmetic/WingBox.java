package com.cheatbreaker.client.cosmetic;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;

public class WingBox extends ModelBox {
    public TexturedQuad frontQuad;
    public TexturedQuad backQuad;

    @Override
    public void render(Tessellator tessellator, float f) {
        this.frontQuad.draw(tessellator, f);
        this.backQuad.draw(tessellator, f);
    }

    public WingBox(ModelRenderer modelRenderer, int n, int n2, float f, float f2, float f3, int n3, int n4) {
        super(modelRenderer, n, n2, f, f2, f3, n3, 0, n4, 0.0F);
        float f4 = f + n3;
        float f5 = f3 + n4;
        this.frontQuad = new TexturedQuad(
            new PositionTextureVertex[]{
                new PositionTextureVertex(f4, f2, f3, 0.0F, 0.0F),
                new PositionTextureVertex(f, f2, f3, 0.0F, 0.0F),
                new PositionTextureVertex(f, f2, f5, 0.0F, 0.0F),
                new PositionTextureVertex(f4, f2, f5, 0.0F, 0.0F)
            },
            n,
            n2 + n4,
            n + n3,
            n2,
            modelRenderer.textureWidth,
            modelRenderer.textureHeight
        );
        this.backQuad = new TexturedQuad((PositionTextureVertex[])this.frontQuad.vertexPositions.clone());
        this.backQuad.flipFace();
    }
}
