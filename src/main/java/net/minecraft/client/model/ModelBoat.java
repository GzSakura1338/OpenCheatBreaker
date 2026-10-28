package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelBoat extends ModelBase {
    public ModelRenderer[] boatSides = new ModelRenderer[5];

    public ModelBoat() {
        this.boatSides[0] = new ModelRenderer(this, 0, 8);
        this.boatSides[1] = new ModelRenderer(this, 0, 0);
        this.boatSides[2] = new ModelRenderer(this, 0, 0);
        this.boatSides[3] = new ModelRenderer(this, 0, 0);
        this.boatSides[4] = new ModelRenderer(this, 0, 0);
        byte b = 24;
        byte b2 = 6;
        byte b3 = 20;
        byte b4 = 4;
        this.boatSides[0].addBox(-b / 2, -b3 / 2 + 2, -3.0F, b, b3 - 4, 4, 0.0F);
        this.boatSides[0].setRotationPoint(0.0F, b4, 0.0F);
        this.boatSides[1].addBox(-b / 2 + 2, -b2 - 1, -1.0F, b - 4, b2, 2, 0.0F);
        this.boatSides[1].setRotationPoint(-b / 2 + 1, b4, 0.0F);
        this.boatSides[2].addBox(-b / 2 + 2, -b2 - 1, -1.0F, b - 4, b2, 2, 0.0F);
        this.boatSides[2].setRotationPoint(b / 2 - 1, b4, 0.0F);
        this.boatSides[3].addBox(-b / 2 + 2, -b2 - 1, -1.0F, b - 4, b2, 2, 0.0F);
        this.boatSides[3].setRotationPoint(0.0F, b4, -b3 / 2 + 1);
        this.boatSides[4].addBox(-b / 2 + 2, -b2 - 1, -1.0F, b - 4, b2, 2, 0.0F);
        this.boatSides[4].setRotationPoint(0.0F, b4, b3 / 2 - 1);
        this.boatSides[0].rotateAngleX = (float) (Math.PI / 2);
        this.boatSides[1].rotateAngleY = (float) (Math.PI * 3.0 / 2.0);
        this.boatSides[2].rotateAngleY = (float) (Math.PI / 2);
        this.boatSides[3].rotateAngleY = (float) Math.PI;
    }

    @Override
    public void render(Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
        for (int i = 0; i < 5; i++) {
            this.boatSides[i].render(f6);
        }
    }
}
