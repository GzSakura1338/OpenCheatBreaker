package shadersmod.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.optifine.TextureUtils;
import org.apache.commons.io.IOUtils;
import shadersmod.common.SMCLog;

public class SimpleShaderTexture extends AbstractTexture {
    public String texturePath;
    public static IMetadataSerializer METADATA_SERIALIZER = makeMetadataSerializer();

    public SimpleShaderTexture(String str) {
        this.texturePath = str;
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws FileNotFoundException, java.io.IOException {
        this.deleteGlTexture();
        InputStream shaderPackResourceStream = Shaders.getShaderPackResourceStream(this.texturePath);
        if (shaderPackResourceStream == null) {
            throw new FileNotFoundException("Shader texture not found: " + this.texturePath);
        }

        try {
            BufferedImage readBufferedImage = TextureUtils.readBufferedImage(shaderPackResourceStream);
            TextureMetadataSection loadTextureMetadataSection = this.loadTextureMetadataSection();
            TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), readBufferedImage, loadTextureMetadataSection.getTextureBlur(), loadTextureMetadataSection.getTextureClamp());
        } finally {
            IOUtils.closeQuietly(shaderPackResourceStream);
        }
    }

    public TextureMetadataSection loadTextureMetadataSection() {
        String str = this.texturePath + ".mcmeta";
        String str2 = "texture";
        InputStream shaderPackResourceStream = Shaders.getShaderPackResourceStream(str);
        if (shaderPackResourceStream != null) {
            IMetadataSerializer metadataSerializer = METADATA_SERIALIZER;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(shaderPackResourceStream));

            TextureMetadataSection textureMetadataSection;
            try {
                JsonObject asJsonObject = new JsonParser().parse(bufferedReader).getAsJsonObject();
                TextureMetadataSection parseMetadataSection = (TextureMetadataSection)metadataSerializer.parseMetadataSection(str2, asJsonObject);
                if (parseMetadataSection == null) {
                    return new TextureMetadataSection(false, false, new ArrayList());
                }

                textureMetadataSection = parseMetadataSection;
            } catch (RuntimeException e) {
                SMCLog.warning("Error reading metadata: " + str);
                SMCLog.warning("" + e.getClass().getName() + ": " + e.getMessage());
                return new TextureMetadataSection(false, false, new ArrayList());
            } finally {
                IOUtils.closeQuietly(bufferedReader);
                IOUtils.closeQuietly(shaderPackResourceStream);
            }

            return textureMetadataSection;
        } else {
            return new TextureMetadataSection(false, false, new ArrayList());
        }
    }

    public static IMetadataSerializer makeMetadataSerializer() {
        IMetadataSerializer metadataSerializer = new IMetadataSerializer();
        metadataSerializer.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
        metadataSerializer.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
        metadataSerializer.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
        metadataSerializer.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
        metadataSerializer.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
        return metadataSerializer;
    }
}
