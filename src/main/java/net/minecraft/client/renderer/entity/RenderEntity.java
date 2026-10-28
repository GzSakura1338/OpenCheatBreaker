package net.minecraft.client.renderer.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderEntity extends Render {
    @Override
    public void doRender(Entity entity, double d, double d2, double d3, float f, float f2) {
        GL11.glPushMatrix();
        renderOffsetAABB(entity.boundingBox, d - entity.lastTickPosX, d2 - entity.lastTickPosY, d3 - entity.lastTickPosZ);
        GL11.glPopMatrix();
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}
