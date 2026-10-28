package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderFireball extends Render {
    public float scale;

    public RenderFireball(float f) {
        this.scale = f;
    }

    public void doRender(EntityFireball entityFireball, double d, double d2, double d3, float f, float f2) {
        GL11.glPushMatrix();
        this.bindEntityTexture(entityFireball);
        GL11.glTranslatef((float)d, (float)d2, (float)d3);
        GL11.glEnable(32826);
        float scale = this.scale;
        GL11.glScalef(scale / 1.0F, scale / 1.0F, scale / 1.0F);
        IIcon iconFromDamage = Items.fire_charge.getIconFromDamage(0);
        Tessellator instance = Tessellator.instance;
        float minU = iconFromDamage.getMinU();
        float maxU = iconFromDamage.getMaxU();
        float minV = iconFromDamage.getMinV();
        float maxV = iconFromDamage.getMaxV();
        float f3 = 1.0F;
        float f4 = 0.5F;
        float f5 = 0.25F;
        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        instance.startDrawingQuads();
        instance.setNormal(0.0F, 1.0F, 0.0F);
        instance.addVertexWithUV(0.0F - f4, 0.0F - f5, 0.0, minU, maxV);
        instance.addVertexWithUV(f3 - f4, 0.0F - f5, 0.0, maxU, maxV);
        instance.addVertexWithUV(f3 - f4, 1.0F - f5, 0.0, maxU, minV);
        instance.addVertexWithUV(0.0F - f4, 1.0F - f5, 0.0, minU, minV);
        instance.draw();
        GL11.glDisable(32826);
        GL11.glPopMatrix();
    }

    public ResourceLocation getEntityTexture(EntityFireball entityFireball) {
        return TextureMap.locationItemsTexture;
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityFireball)entity);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        this.doRender((EntityFireball)entity, d, d2, d3, f, f2);
    }
}
