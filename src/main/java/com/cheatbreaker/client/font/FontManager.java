package com.cheatbreaker.client.font;

import com.cheatbreaker.client.manager.AbstractManager;
import com.cheatbreaker.client.util.Logging;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;

public class FontManager extends AbstractManager {
    public CustomFontRenderer playRegular22;
    public Object2ObjectOpenHashMap<String, ResourceLocation> object2ObjectOpenHashMap = new Object2ObjectOpenHashMap();
    public ResourceLocation playBoldTtf = new ResourceLocation("client/font/Play-Bold.ttf");
    public CustomFontRenderer playRegular14;
    public ResourceLocation robotoRegularTtf = new ResourceLocation("client/font/Roboto-Regular.ttf");
    public CustomFontRenderer playBold16;
    public CustomFontRenderer robotoRegular13;
    public ResourceLocation robotoBoldTtf = new ResourceLocation("client/font/Roboto-Bold.ttf");
    public CustomFontRenderer playBold18;
    public CustomFontRenderer playRegular16;
    public CustomFontRenderer robotoBold10;
    public CustomFontRenderer ubuntuM14;
    public ResourceLocation playRegularTtf = new ResourceLocation("client/font/Play-Regular.ttf");
    public CustomFontRenderer robotoRegular24;
    public ResourceLocation ubuntuMTtf = new ResourceLocation("client/font/Ubuntu-M.ttf");
    public CustomFontRenderer playRegular12;
    public CustomFontRenderer playRegular18;
    public CustomFontRenderer playBold22;
    public CustomFontRenderer robotoBold14;
    public CustomFontRenderer ubuntuM16;

    @Override
    public void initialize() throws IOException {
        this.createFonts();
    }

    public void loadAppProperties() {
        try {
            ResourceLocation resourceLocation = new ResourceLocation("client/properties/app.properties");
            InputStream inputStream = this.mc.getResourceManager().getResource(resourceLocation).getInputStream();
            if (inputStream == null) {
                return;
            }

            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
            this.cheatBreaker.setCommitId(properties.getProperty("git.commit.id.abbrev"));
            this.cheatBreaker.setGitCommit(properties.getProperty("git.commit.id"));
            this.cheatBreaker.setBranch(properties.getProperty("git.branch"));
            this.cheatBreaker.setClientVersion(properties.getProperty("client.version"));
            this.cheatBreaker.setClientBuildType(properties.getProperty("client.build.type"));
        } catch (IOException e) {
            Logging.debug("Failed to load client properties.");
        }
    }

    public void createFonts() throws IOException {
        this.playBold22 = new CustomFontRenderer(this.playBoldTtf, 22.0F);
        this.playRegular22 = new CustomFontRenderer(this.playRegularTtf, 22.0F);
        this.playRegular18 = new CustomFontRenderer(this.playRegularTtf, 18.0F);
        this.playRegular14 = new CustomFontRenderer(this.playRegularTtf, 14.0F);
        this.playRegular12 = new CustomFontRenderer(this.playRegularTtf, 12.0F);
        this.playRegular16 = new CustomFontRenderer(this.playRegularTtf, 16.0F);
        this.playBold18 = new CustomFontRenderer(this.playBoldTtf, 18.0F);
        this.playBold16 = new CustomFontRenderer(this.playBoldTtf, 16.0F);
        this.ubuntuM16 = new CustomFontRenderer(this.ubuntuMTtf, 16.0F);
        this.ubuntuM14 = new CustomFontRenderer(this.ubuntuMTtf, 14.0F);
        this.robotoRegular13 = new CustomFontRenderer(this.robotoRegularTtf, 13.0F);
        this.robotoBold14 = new CustomFontRenderer(this.robotoBoldTtf, 14.0F);
        this.robotoBold10 = new CustomFontRenderer(this.robotoBoldTtf, 10.0F);
        this.robotoRegular24 = new CustomFontRenderer(this.robotoRegularTtf, 24.0F);
    }

    public ResourceLocation getHeadTexture(String str) {
        ResourceLocation orDefault = (ResourceLocation)this.object2ObjectOpenHashMap.getOrDefault(str, new ResourceLocation("client/heads/" + str + ".png"));
        ResourceLocation resourceLocation = new ResourceLocation("client/defaults/steve.png");
        if (!this.object2ObjectOpenHashMap.containsKey(str)) {
            ThreadDownloadImageData threadDownloadImageData = new ThreadDownloadImageData(null, "https://minotar.net/helm/" + str + "/32.png", resourceLocation, null);
            this.mc.renderEngine.loadTexture(orDefault, threadDownloadImageData);
            this.object2ObjectOpenHashMap.put(str, orDefault);
        }

        return orDefault;
    }
}
