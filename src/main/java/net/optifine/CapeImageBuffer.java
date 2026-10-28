package net.optifine;

import java.awt.image.BufferedImage;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.util.ResourceLocation;

public class CapeImageBuffer extends ImageBufferDownload {
    public AbstractClientPlayer player;
    public ResourceLocation resourceLocation;

    public CapeImageBuffer(AbstractClientPlayer abstractClientPlayer, ResourceLocation resourceLocation) {
        this.player = abstractClientPlayer;
        this.resourceLocation = resourceLocation;
    }

    @Override
    public BufferedImage parseUserSkin(BufferedImage bufferedImage) {
        return CapeUtils.parseCape(bufferedImage);
    }

    @Override
    public void run() {
        if (this.player != null) {
            this.player.setLocationOfCape(this.resourceLocation);
        }
    }

    public void cleanup() {
        this.player = null;
    }
}
