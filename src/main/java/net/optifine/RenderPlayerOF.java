package net.optifine;

import com.cheatbreaker.client.CheatBreaker;
import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class RenderPlayerOF extends RenderPlayer {
    @Override
    public void renderEquippedItems(EntityLivingBase entityLivingBase, float f) {
        super.renderEquippedItems(entityLivingBase, f);
        this.renderEquippedItems(entityLivingBase, 0.0625F, f);
    }

    public void renderEquippedItems(EntityLivingBase entityLivingBase, float f, float f2) {
        if (Config.isShowCapes() && (Boolean)CheatBreaker.getInstance().getKeybindSettings().showOptifineHats.getValue() && entityLivingBase instanceof AbstractClientPlayer) {
            AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)entityLivingBase;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(32826);
            GlStateManager.enableCull();
            ModelBiped mainModel = (ModelBiped)this.mainModel;
            PlayerConfigurations.renderPlayerItems(mainModel, abstractClientPlayer, f, f2);
            GlStateManager.disableCull();
        }
    }

    public static void register() {
        RenderManager instance = RenderManager.instance;
        Map mapRenderTypes = getMapRenderTypes(instance);
        if (mapRenderTypes == null) {
            Config.warn("RenderPlayerOF init() failed: RenderManager.MapRenderTypes not found");
        } else {
            RenderPlayerOF renderPlayerOF = new RenderPlayerOF();
            renderPlayerOF.setRenderManager(instance);
            mapRenderTypes.put(EntityPlayer.class, renderPlayerOF);
        }
    }

    public static Map getMapRenderTypes(RenderManager renderManager) {
        try {
            Field[] fields = Reflector.getFields(RenderManager.class, Map.class);

            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                Map map = (Map)field.get(renderManager);
                if (map != null) {
                    Object obj = map.get(EntityPlayer.class);
                    if (obj instanceof RenderPlayer) {
                        return map;
                    }
                }
            }

            return null;
        } catch (Exception e) {
            Config.warn("Error getting RenderManager.mapRenderTypes");
            Config.warn(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }
}
