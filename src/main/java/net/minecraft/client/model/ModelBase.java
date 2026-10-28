package net.minecraft.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public abstract class ModelBase {
    public float swingProgress;
    public boolean isRiding;
    public List boxList = new ArrayList();
    public boolean isChild = true;
    public Map modelTextureMap = new HashMap();
    public int textureWidth = 64;
    public int textureHeight = 32;

    public void render(Entity entity, float f, float f2, float f3, float f4, float f5, float f6) {
    }

    public void setRotationAngles(float f, float f2, float f3, float f4, float f5, float f6, Entity entity) {
    }

    public void setLivingAnimations(EntityLivingBase entityLivingBase, float f, float f2, float f3) {
    }

    public ModelRenderer getRandomModelBox(Random random) {
        return (ModelRenderer)this.boxList.get(random.nextInt(this.boxList.size()));
    }

    public void setTextureOffset(String str, int n, int n2) {
        this.modelTextureMap.put(str, new TextureOffset(n, n2));
    }

    public TextureOffset getTextureOffset(String str) {
        return (TextureOffset)this.modelTextureMap.get(str);
    }

    public static void handleModelRenderer(ModelRenderer modelRenderer, ModelRenderer modelRenderer2) {
        modelRenderer2.rotateAngleX = modelRenderer.rotateAngleX;
        modelRenderer2.rotateAngleY = modelRenderer.rotateAngleY;
        modelRenderer2.rotateAngleZ = modelRenderer.rotateAngleZ;
        modelRenderer2.rotationPointX = modelRenderer.rotationPointX;
        modelRenderer2.rotationPointY = modelRenderer.rotationPointY;
        modelRenderer2.rotationPointZ = modelRenderer.rotationPointZ;
    }
}
