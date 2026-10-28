package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.imageio.ImageIO;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredTexture extends AbstractTexture {
    public static Logger logger = LogManager.getLogger();
    public List layeredTextureNames;

    public LayeredTexture(String... strs) {
        this.layeredTextureNames = Lists.newArrayList(strs);
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
        this.deleteGlTexture();
        BufferedImage bufferedImage = null;

        try {
            for (String str : (Iterable<String>)this.layeredTextureNames) {
                if (str != null) {
                    InputStream inputStream = resourceManager.getResource(new ResourceLocation(str)).getInputStream();
                    BufferedImage read = ImageIO.read(inputStream);
                    if (bufferedImage == null) {
                        bufferedImage = new BufferedImage(read.getWidth(), read.getHeight(), 2);
                    }

                    bufferedImage.getGraphics().drawImage(read, 0, 0, null);
                }
            }
        } catch (IOException e) {
            logger.error("Couldn't load layered image", e);
            return;
        }

        TextureUtil.uploadTextureImage(this.getGlTextureId(), bufferedImage);
    }
}
