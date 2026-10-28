package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSnowball extends Render {
    public Item itemForRender;
    public int metadata;

    public RenderSnowball(Item item, int n) {
        this.itemForRender = item;
        this.metadata = n;
    }

    public RenderSnowball(Item item) {
        this(item, 0);
    }

    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        IIcon iconFromDamage = this.itemForRender.getIconFromDamage(this.metadata);
        if (iconFromDamage != null) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)d, (float)d2, (float)d3);
            GL11.glEnable(32826);
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            this.bindEntityTexture(entity);
            Tessellator instance = Tessellator.instance;
            if (iconFromDamage == ItemPotion.findIIcon("bottle_splash")) {
                int color = PotionHelper.getColor(((EntityPotion)entity).getPotionDamage(), false);
                float f3 = (color >> 16 & 0xFF) / 255.0F;
                float f4 = (color >> 8 & 0xFF) / 255.0F;
                float f5 = (color & 0xFF) / 255.0F;
                GL11.glColor3f(f3, f4, f5);
                GL11.glPushMatrix();
                this.renderEntity(instance, ItemPotion.findIIcon("overlay"));
                GL11.glPopMatrix();
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
            }

            this.renderEntity(instance, iconFromDamage);
            GL11.glDisable(32826);
            GL11.glPopMatrix();
        }
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.locationItemsTexture;
    }

    public void renderEntity(Tessellator tessellator, IIcon icon) {
        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();
        float f = 1.0F;
        float f2 = 0.5F;
        float f3 = 0.25F;
        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.addVertexWithUV(0.0F - f2, 0.0F - f3, 0.0, minU, maxV);
        tessellator.addVertexWithUV(f - f2, 0.0F - f3, 0.0, maxU, maxV);
        tessellator.addVertexWithUV(f - f2, f - f3, 0.0, maxU, minV);
        tessellator.addVertexWithUV(0.0F - f2, f - f3, 0.0, minU, minV);
        tessellator.draw();
    }
}
