package net.minecraft.client.renderer.tileentity;

import com.cheatbreaker.client.CheatBreaker;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import org.lwjgl.opengl.GL11;

public class TileEntityMobSpawnerRenderer extends TileEntitySpecialRenderer {
    public void renderTileEntityAt(TileEntityMobSpawner tileEntityMobSpawner, double d, double d2, double d3, float f) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d + 0.5F, (float)d2, (float)d3 + 0.5F);
        renderEntity(tileEntityMobSpawner.getLogic(), d, d2, d3, f);
        GL11.glPopMatrix();
    }

    public static void renderEntity(MobSpawnerBaseLogic mobSpawnerBaseLogic, double d, double d2, double d3, float f) {
        Entity entityToRender = mobSpawnerBaseLogic.getEntityToRender();
        if (CheatBreaker.getInstance().getKeybindSettings().shouldRenderSpawnerMob(entityToRender, mobSpawnerBaseLogic.getSpawnerX(), mobSpawnerBaseLogic.getSpawnerY(), mobSpawnerBaseLogic.getSpawnerZ())) {
            if (entityToRender != null) {
                entityToRender.setWorld(mobSpawnerBaseLogic.getSpawnerWorld());
                float f2 = 0.4375F;
                GL11.glTranslatef(0.0F, 0.4F, 0.0F);
                GL11.glRotatef((float)(mobSpawnerBaseLogic.mobAngle + (mobSpawnerBaseLogic.mobAngleNext - mobSpawnerBaseLogic.mobAngle) * f) * 10.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.4F, 0.0F);
                GL11.glScalef(f2, f2, f2);
                entityToRender.setLocationAndAngles(d, d2, d3, 0.0F, 0.0F);
                RenderManager.instance.renderEntityWithPosYaw(entityToRender, 0.0, 0.0, 0.0, 0.0F, f);
            }
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double d, double d2, double d3, float f) {
        this.renderTileEntityAt((TileEntityMobSpawner)tileEntity, d, d2, d3, f);
    }
}
