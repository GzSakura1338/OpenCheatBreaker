package net.optifine;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class CapeUtils {
    public static BufferedImage parseCape(BufferedImage bufferedImage) {
        int n = 64;
        int n2 = 32;
        int width = bufferedImage.getWidth();

        for (int height = bufferedImage.getHeight(); n < width || n2 < height; n2 *= 2) {
            n *= 2;
        }

        BufferedImage bufferedImage2 = new BufferedImage(n, n2, 2);
        Graphics graphics = bufferedImage2.getGraphics();
        graphics.drawImage(bufferedImage, 0, 0, (ImageObserver)null);
        graphics.dispose();
        return bufferedImage2;
    }

    public static void handleAbstractClientPlayer(AbstractClientPlayer abstractClientPlayer, UUID uuid) {
        String nameClear = abstractClientPlayer.getNameClear();
        if (nameClear != null && !nameClear.isEmpty() && !nameClear.contains("\u0000")) {
            String str = "http://s.optifine.net/capes/" + nameClear + ".png";
            ResourceLocation resourceLocation = new ResourceLocation("capeof/" + nameClear);
            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
            ITextureObject texture = textureManager.getTexture(resourceLocation);
            if (texture != null && texture instanceof ThreadDownloadImageData) {
                ThreadDownloadImageData threadDownloadImageData = (ThreadDownloadImageData)texture;
                if (threadDownloadImageData.imageFound != null) {
                    if (threadDownloadImageData.imageFound) {
                        abstractClientPlayer.setLocationOfCape(resourceLocation);
                    }

                    return;
                }
            }

            CapeImageBuffer capeImageBuffer = new CapeImageBuffer(abstractClientPlayer, resourceLocation);
            ThreadDownloadImageData threadDownloadImageData = new ThreadDownloadImageData((File)null, str, (ResourceLocation)null, capeImageBuffer);
            threadDownloadImageData.pipeline = true;
            textureManager.loadTexture(resourceLocation, threadDownloadImageData);
        }
    }
}
