package net.minecraft.client.renderer.tileentity;

import com.cheatbreaker.client.CheatBreaker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;

public class TileEntityMobSpawnerRenderer extends TileEntitySpecialRenderer<TileEntityMobSpawner> {
   public void renderTileEntityAt(TileEntityMobSpawner te, double x, double y, double z, float partialTicks, int destroyStage) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x + 0.5F, (float)y, (float)z + 0.5F);
      renderMob(te.getSpawnerBaseLogic(), x, y, z, partialTicks);
      GlStateManager.popMatrix();
   }

   public static void renderMob(MobSpawnerBaseLogic mobSpawnerLogic, double posX, double posY, double posZ, float partialTicks) {
      Entity renderedEntity = mobSpawnerLogic.getRenderedEntity(mobSpawnerLogic.getSpawnerWorld());
      BlockPos spawnerPosition = mobSpawnerLogic.getSpawnerPosition();
      if (CheatBreaker.getInstance().getKeybindSettings().shouldRenderSpawnerMob(renderedEntity, spawnerPosition.getX(), spawnerPosition.getY(), spawnerPosition.getZ())) {
         if (renderedEntity != null) {
            float f = 0.4375F;
            GlStateManager.translate(0.0F, 0.4F, 0.0F);
            GlStateManager.rotate(
               (float)(mobSpawnerLogic.getPrevMobRotation() + (mobSpawnerLogic.getMobRotation() - mobSpawnerLogic.getPrevMobRotation()) * partialTicks) * 10.0F,
               0.0F,
               1.0F,
               0.0F
            );
            GlStateManager.rotate(-30.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(0.0F, -0.4F, 0.0F);
            GlStateManager.scale(f, f, f);
            renderedEntity.setLocationAndAngles(posX, posY, posZ, 0.0F, 0.0F);
            Minecraft.getMinecraft().getRenderManager().renderEntityWithPosYaw(renderedEntity, 0.0, 0.0, 0.0, 0.0F, partialTicks);
         }
      }
   }
}
