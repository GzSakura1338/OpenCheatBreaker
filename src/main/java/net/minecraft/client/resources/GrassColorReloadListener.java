package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ColorizerGrass;

public class GrassColorReloadListener implements IResourceManagerReloadListener {
    public static ResourceLocation GRASS_COLOR_TEXTURE = new ResourceLocation("textures/colormap/grass.png");

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        try {
            ColorizerGrass.setGrassBiomeColorizer(TextureUtil.readImageData(resourceManager, GRASS_COLOR_TEXTURE));
        } catch (IOException e) {
        }
    }
}
