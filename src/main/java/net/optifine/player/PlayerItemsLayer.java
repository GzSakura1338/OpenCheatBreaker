package net.optifine.player;

import java.util.Map;
import java.util.Set;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.optifine.Config;

public class PlayerItemsLayer implements LayerRenderer {
   public RenderPlayer renderPlayer = null;

   public PlayerItemsLayer(RenderPlayer renderPlayer) {
      this.renderPlayer = renderPlayer;
   }

   @Override
   public void doRenderLayer(EntityLivingBase entityLivingBase, float f, float f2, float f3, float f4, float f5, float f6, float f7) {
      this.renderEquippedItems(entityLivingBase, f7, f3);
   }

   public void renderEquippedItems(EntityLivingBase entityLivingBase, float f, float f2) {
      if (Config.isShowCapes() && !entityLivingBase.isInvisible() && entityLivingBase instanceof AbstractClientPlayer) {
         AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)entityLivingBase;
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableRescaleNormal();
         GlStateManager.enableCull();
         ModelPlayer mainModel = this.renderPlayer.getMainModel();
         PlayerConfigurations.renderPlayerItems(mainModel, abstractClientPlayer, f, f2);
         GlStateManager.disableCull();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }

   public static void register(Map map) {
      Set keySet = map.keySet();
      boolean flag = false;

      for (Object obj : keySet) {
         Object obj2 = map.get(obj);
         if (obj2 instanceof RenderPlayer) {
            RenderPlayer renderPlayer = (RenderPlayer)obj2;
            renderPlayer.addLayer(new PlayerItemsLayer(renderPlayer));
            flag = true;
         }
      }

      if (!flag) {
         Config.warn("PlayerItemsLayer not registered");
      }
   }
}
