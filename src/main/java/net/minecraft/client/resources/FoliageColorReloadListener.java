package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ColorizerFoliage;

public class FoliageColorReloadListener implements IResourceManagerReloadListener {
    public static ResourceLocation FOLIAGE_TEXTURE = new ResourceLocation("textures/colormap/foliage.png");

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        try {
            ColorizerFoliage.setFoliageBiomeColorizer(TextureUtil.readImageData(resourceManager, FOLIAGE_TEXTURE));
        } catch (IOException e) {
        }
    }
}
