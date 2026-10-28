package net.minecraft.client.renderer.texture;

import com.cheatbreaker.client.util.SneakyThrow;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.ConnectedTextures;
import net.optifine.CustomItems;
import net.optifine.Reflector;
import net.optifine.ReflectorForge;
import net.optifine.TextureUtils;
import net.optifine.WrUpdates;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shadersmod.client.ShadersTex;

public class TextureMap extends AbstractTexture implements IIconRegister, ITickableTextureObject {
    public static boolean ENABLE_SKIP = Boolean.parseBoolean(System.getProperty("fml.skipFirstTextureLoad", "true"));
    public static Logger logger = LogManager.getLogger();
    public static ResourceLocation locationBlocksTexture = new ResourceLocation("textures/atlas/blocks.png");
    public static ResourceLocation locationItemsTexture = new ResourceLocation("textures/atlas/items.png");
    public List listAnimatedSprites = Lists.newArrayList();
    public Map mapRegisteredSprites = Maps.newHashMap();
    public Map mapUploadedSprites = Maps.newHashMap();
    public int anisotropicFiltering = 1;
    public TextureAtlasSprite missingImage = new TextureAtlasSprite("missingno");
    public int mipmapLevels;
    public boolean skipFirst = false;
    public TextureAtlasSprite[] iconGrid = null;
    public int iconGridSize = -1;
    public static TextureMap textureMapBlocks = null;
    public static TextureMap textureMapItems = null;
    public int iconGridCountX = -1;
    public int iconGridCountY = -1;
    public double iconGridSizeU = -1.0;
    public double iconGridSizeV = -1.0;
    public int counterIndexInMap = 0;
    public int atlasWidth = 0;
    public int atlasHeight = 0;
    public int textureType;
    public String basePath;

    public TextureMap(int n, String str) {
        this(n, str, false);
    }

    public TextureMap(int n, String str, boolean flag) {
        this.textureType = n;
        this.basePath = str;
        if (this.textureType == 0) {
            textureMapBlocks = this;
        }

        if (this.textureType == 1) {
            textureMapItems = this;
        }

        this.registerIcons();
        this.skipFirst = flag && ENABLE_SKIP;
    }

    public void initMissingImage() {
        int[] ints;
        if (this.anisotropicFiltering > 1.0F) {
            boolean flag = true;
            boolean flag2 = true;
            boolean flag3 = true;
            this.missingImage.setIconWidth(32);
            this.missingImage.setIconHeight(32);
            ints = new int[1024];
            System.arraycopy(TextureUtil.missingTextureData, 0, ints, 0, TextureUtil.missingTextureData.length);
            TextureUtil.prepareAnisotropicData(ints, 16, 16, 8);
        } else {
            ints = TextureUtil.missingTextureData;
            this.missingImage.setIconWidth(16);
            this.missingImage.setIconHeight(16);
        }

        int[][] intses = new int[this.mipmapLevels + 1][];
        intses[0] = ints;
        this.missingImage.setFramesTextureData(Lists.newArrayList(new int[][][]{intses}));
        this.missingImage.setIndexInMap(this.counterIndexInMap++);
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
        ShadersTex.resManager = resourceManager;
        this.counterIndexInMap = 0;
        this.initMissingImage();
        this.deleteGlTexture();
        this.loadTextureAtlas(resourceManager);
    }

    public void loadTextureAtlas(IResourceManager resourceManager) {
        ShadersTex.resManager = resourceManager;
        Config.dbg("Loading texture map: " + this.basePath);
        WrUpdates.finishCurrentUpdate();
        this.registerIcons();
        int gLMaximumTextureSize = TextureUtils.getGLMaximumTextureSize();
        Stitcher stitcher = new Stitcher(gLMaximumTextureSize, gLMaximumTextureSize, true, 0, this.mipmapLevels);
        this.mapUploadedSprites.clear();
        this.listAnimatedSprites.clear();
        int n = Integer.MAX_VALUE;
        Reflector.callVoid(Reflector.ForgeHooksClient_onTextureStitchedPre, this);
        Iterator iterator = this.mapRegisteredSprites.entrySet().iterator();

        while (iterator.hasNext() && !this.skipFirst) {
            Entry entry = (Entry)iterator.next();
            ResourceLocation resourceLocation = new ResourceLocation((String)entry.getKey());
            TextureAtlasSprite value = (TextureAtlasSprite)entry.getValue();
            ResourceLocation completeResourceLocation = this.completeResourceLocation(resourceLocation, 0);
            if (value.getIndexInMap() < 0) {
                value.setIndexInMap(this.counterIndexInMap++);
            }

            if (value.hasCustomLoader(resourceManager, resourceLocation)) {
                if (!value.load(resourceManager, resourceLocation)) {
                    n = Math.min(n, Math.min(value.getIconWidth(), value.getIconHeight()));
                    stitcher.addSprite(value);
                }

                Config.dbg("Custom loader: " + value);
            } else {
                try {
                    IResource loadResource = ShadersTex.loadResource(resourceManager, completeResourceLocation);
                    BufferedImage[] bufferedImages = new BufferedImage[1 + this.mipmapLevels];
                    bufferedImages[0] = ImageIO.read(loadResource.getInputStream());
                    TextureMetadataSection metadata = (TextureMetadataSection)loadResource.getMetadata("texture");
                    if (metadata != null) {
                        List listMipmaps = metadata.getListMipmaps();
                        if (!listMipmaps.isEmpty()) {
                            int width = bufferedImages[0].getWidth();
                            int height = bufferedImages[0].getHeight();
                            if (MathHelper.roundUpToPowerOfTwo(width) != width || MathHelper.roundUpToPowerOfTwo(height) != height) {
                                throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
                            }
                        }

                        for (int n2 : (Iterable<Integer>)listMipmaps) {
                            if (n2 > 0 && n2 < bufferedImages.length - 1 && bufferedImages[n2] == null) {
                                ResourceLocation completeResourceLocation2 = this.completeResourceLocation(resourceLocation, n2);

                                try {
                                    bufferedImages[n2] = ImageIO.read(ShadersTex.loadResource(resourceManager, completeResourceLocation2).getInputStream());
                                } catch (IOException e) {
                                    logger.error("Unable to load miplevel {} from: {}", new Object[]{n2, completeResourceLocation2, e});
                                }
                            }
                        }
                    }

                    AnimationMetadataSection metadata2 = (AnimationMetadataSection)loadResource.getMetadata("animation");
                    value.loadSprite(bufferedImages, metadata2, this.anisotropicFiltering > 1.0F);
                } catch (RuntimeException e) {
                    logger.error("Unable to parse metadata from " + completeResourceLocation, e);
                    ReflectorForge.FMLClientHandler_trackBrokenTexture(completeResourceLocation, e.getMessage());
                    continue;
                } catch (IOException e) {
                    logger.error("Using missing texture, unable to load " + completeResourceLocation + ", " + e.getClass().getName());
                    ReflectorForge.FMLClientHandler_trackMissingTexture(completeResourceLocation);
                    continue;
                }

                n = Math.min(n, Math.min(value.getIconWidth(), value.getIconHeight()));
                stitcher.addSprite(value);
            }
        }

        int calculateLogBaseTwo = MathHelper.calculateLogBaseTwo(n);
        if (calculateLogBaseTwo < 0) {
            calculateLogBaseTwo = 0;
        }

        if (calculateLogBaseTwo < this.mipmapLevels) {
            logger.info("{}: dropping miplevel from {} to {}, because of minTexel: {}", new Object[]{this.basePath, this.mipmapLevels, calculateLogBaseTwo, n});
            this.mipmapLevels = calculateLogBaseTwo;
        }

        Iterator iterator2 = this.mapRegisteredSprites.values().iterator();

        while (iterator2.hasNext() && !this.skipFirst) {
            TextureAtlasSprite textureAtlasSprite = (TextureAtlasSprite)iterator2.next();

            try {
                textureAtlasSprite.generateMipmaps(this.mipmapLevels);
            } catch (Throwable e) {
                CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Applying mipmap");
                CrashReportCategory makeCategory = makeCrashReport.makeCategory("Sprite being mipmapped");
                makeCategory.addCrashSectionCallable("Sprite name", () -> textureAtlasSprite.getIconName());
                makeCategory.addCrashSectionCallable("Sprite size", () -> textureAtlasSprite.getIconWidth() + " x " + textureAtlasSprite.getIconHeight());
                makeCategory.addCrashSectionCallable("Sprite frames", () -> textureAtlasSprite.getFrameCount() + " frames");
                makeCategory.addCrashSection("Mipmap levels", this.mipmapLevels);
                throw new ReportedException(makeCrashReport);
            }
        }

        this.missingImage.generateMipmaps(this.mipmapLevels);
        stitcher.addSprite(this.missingImage);
        this.skipFirst = false;

        try {
            stitcher.doStitch();
        } catch (StitcherException e) {
            throw SneakyThrow.sneakyThrow(e);
        }

        Config.dbg("Texture size: " + this.basePath + ", " + stitcher.getCurrentWidth() + "x" + stitcher.getCurrentHeight());
        int currentWidth = stitcher.getCurrentWidth();
        int currentHeight = stitcher.getCurrentHeight();
        BufferedImage bufferedImage = null;
        if (System.getProperty("saveTextureMap", "false").equalsIgnoreCase("true")) {
            bufferedImage = this.makeDebugImage(currentWidth, currentHeight);
        }

        logger.info("Created: {}x{} {}-atlas", new Object[]{stitcher.getCurrentWidth(), stitcher.getCurrentHeight(), this.basePath});
        if (Config.isShaders()) {
            ShadersTex.allocateTextureMap(this.getGlTextureId(), this.mipmapLevels, stitcher.getCurrentWidth(), stitcher.getCurrentHeight(), stitcher, this);
        } else {
            TextureUtil.allocateTextureImpl(this.getGlTextureId(), this.mipmapLevels, stitcher.getCurrentWidth(), stitcher.getCurrentHeight(), this.anisotropicFiltering);
        }

        HashMap newHashMap = Maps.newHashMap(this.mapRegisteredSprites);

        for (TextureAtlasSprite textureAtlasSprite : (Iterable<TextureAtlasSprite>)stitcher.getStichSlots()) {
            if (Config.isShaders()) {
                ShadersTex.setIconName(ShadersTex.setSprite(textureAtlasSprite).getIconName());
            }

            String iconName = textureAtlasSprite.getIconName();
            newHashMap.remove(iconName);
            this.mapUploadedSprites.put(iconName, textureAtlasSprite);

            try {
                if (Config.isShaders()) {
                    ShadersTex.uploadTexSubForLoadAtlas(
                        textureAtlasSprite.getFrameTextureData(0), textureAtlasSprite.getIconWidth(), textureAtlasSprite.getIconHeight(), textureAtlasSprite.getOriginX(), textureAtlasSprite.getOriginY(), false, false
                    );
                } else {
                    TextureUtil.uploadTextureMipmap(
                        textureAtlasSprite.getFrameTextureData(0), textureAtlasSprite.getIconWidth(), textureAtlasSprite.getIconHeight(), textureAtlasSprite.getOriginX(), textureAtlasSprite.getOriginY(), false, false
                    );
                }

                if (bufferedImage != null) {
                    this.addDebugSprite(textureAtlasSprite, bufferedImage);
                }
            } catch (Throwable e) {
                CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Stitching texture atlas");
                CrashReportCategory makeCategory = makeCrashReport.makeCategory("Texture being stitched together");
                makeCategory.addCrashSection("Atlas path", this.basePath);
                makeCategory.addCrashSection("Sprite", textureAtlasSprite);
                throw new ReportedException(makeCrashReport);
            }

            if (textureAtlasSprite.hasAnimationMetadata()) {
                this.listAnimatedSprites.add(textureAtlasSprite);
            } else {
                textureAtlasSprite.clearFramesTextureData();
            }
        }

        for (TextureAtlasSprite textureAtlasSprite : (Iterable<TextureAtlasSprite>)newHashMap.values()) {
            textureAtlasSprite.copyFrom(this.missingImage);
        }

        if (bufferedImage != null) {
            this.writeDebugImage(bufferedImage, "debug_" + this.basePath.replace('/', '_') + ".png");
        }

        Reflector.callVoid(Reflector.ForgeHooksClient_onTextureStitchedPost, this);
    }

    public ResourceLocation completeResourceLocation(ResourceLocation resourceLocation, int n) {
        return this.isAbsoluteLocation(resourceLocation)
            ? (
                n == 0
                    ? new ResourceLocation(resourceLocation.getResourceDomain(), resourceLocation.getResourcePath() + ".png")
                    : new ResourceLocation(resourceLocation.getResourceDomain(), resourceLocation.getResourcePath() + "mipmap" + n + ".png")
            )
            : (
                n == 0
                    ? new ResourceLocation(resourceLocation.getResourceDomain(), String.format("%s/%s%s", this.basePath, resourceLocation.getResourcePath(), ".png"))
                    : new ResourceLocation(resourceLocation.getResourceDomain(), String.format("%s/mipmaps/%s.%d%s", this.basePath, resourceLocation.getResourcePath(), n, ".png"))
            );
    }

    public void registerIcons() {
        this.mapRegisteredSprites.clear();
        if (this.textureType == 0) {
            for (Block block : Block.blockRegistry) {
                if (block.getMaterial() != Material.air) {
                    block.registerIcons(this);
                }
            }

            Minecraft.getMinecraft().renderGlobal.registerDestroyBlockIcons(this);
            RenderManager.instance.updateIcons(this);
            ConnectedTextures.updateIcons(this);
        }

        if (this.textureType == 1) {
            CustomItems.updateIcons(this);
        }

        for (Item item : (Iterable<Item>)(Iterable)Item.itemRegistry) {
            if (item != null && item.getSpriteNumber() == this.textureType) {
                item.registerIcons(this);
            }
        }
    }

    public TextureAtlasSprite getAtlasSprite(String str) {
        TextureAtlasSprite textureAtlasSprite = (TextureAtlasSprite)this.mapUploadedSprites.get(str);
        if (textureAtlasSprite == null) {
            textureAtlasSprite = this.missingImage;
        }

        return textureAtlasSprite;
    }

    public void updateAnimations() {
        if (Config.isShaders()) {
            ShadersTex.updatingTex = this.getMultiTexID();
        }

        boolean flag = false;
        boolean flag2 = false;
        TextureUtil.bindTexture(this.getGlTextureId());

        for (TextureAtlasSprite textureAtlasSprite : (Iterable<TextureAtlasSprite>)this.listAnimatedSprites) {
            if (this.textureType == 0 ? this.isTerrainAnimationActive(textureAtlasSprite) : this.textureType != 1 || this.isItemAnimationActive(textureAtlasSprite)) {
                textureAtlasSprite.updateAnimation();
                if (textureAtlasSprite.spriteNormal != null) {
                    flag = true;
                }

                if (textureAtlasSprite.spriteSpecular != null) {
                    flag2 = true;
                }
            }
        }

        if (Config.isShaders()) {
            if (flag) {
                TextureUtil.bindTexture(this.getMultiTexID().norm);

                for (TextureAtlasSprite textureAtlasSprite : (Iterable<TextureAtlasSprite>)this.listAnimatedSprites) {
                    if (textureAtlasSprite.spriteNormal != null && this.isTerrainAnimationActive(textureAtlasSprite)) {
                        if (textureAtlasSprite == TextureUtils.iconClock || textureAtlasSprite == TextureUtils.iconCompass) {
                            textureAtlasSprite.spriteNormal.frameCounter = textureAtlasSprite.frameCounter;
                        }

                        textureAtlasSprite.spriteNormal.updateAnimation();
                    }
                }
            }

            if (flag2) {
                TextureUtil.bindTexture(this.getMultiTexID().spec);

                for (TextureAtlasSprite textureAtlasSprite : (Iterable<TextureAtlasSprite>)this.listAnimatedSprites) {
                    if (textureAtlasSprite.spriteSpecular != null && this.isTerrainAnimationActive(textureAtlasSprite)) {
                        if (textureAtlasSprite == TextureUtils.iconClock || textureAtlasSprite == TextureUtils.iconCompass) {
                            textureAtlasSprite.spriteNormal.frameCounter = textureAtlasSprite.frameCounter;
                        }

                        textureAtlasSprite.spriteSpecular.updateAnimation();
                    }
                }
            }

            if (flag || flag2) {
                TextureUtil.bindTexture(this.getGlTextureId());
            }
        }

        if (Config.isShaders()) {
            ShadersTex.updatingTex = null;
        }
    }

    public boolean isItemAnimationActive(TextureAtlasSprite textureAtlasSprite) {
        return textureAtlasSprite != TextureUtils.iconClock && textureAtlasSprite != TextureUtils.iconCompass ? Config.isAnimatedItems() : true;
    }

    @Override
    public IIcon registerIcon(String str) {
        if (str == null) {
            throw new IllegalArgumentException("Name cannot be null!");
        }

        if (str.indexOf(92) != -1 && !this.isAbsoluteLocationPath(str)) {
            throw new IllegalArgumentException("Name cannot contain slashes!");
        }

        Object obj = (TextureAtlasSprite)this.mapRegisteredSprites.get(str);
        if (obj == null && this.textureType == 1 && Reflector.ModLoader_getCustomAnimationLogic.exists()) {
            obj = Reflector.call(Reflector.ModLoader_getCustomAnimationLogic, str);
        }

        if (obj == null) {
            if (this.textureType == 1) {
                if ("clock".equals(str)) {
                    obj = new TextureClock(str);
                } else if ("compass".equals(str)) {
                    obj = new TextureCompass(str);
                } else {
                    obj = new TextureAtlasSprite(str);
                }
            } else {
                obj = new TextureAtlasSprite(str);
            }

            this.mapRegisteredSprites.put(str, obj);
            if (obj instanceof TextureAtlasSprite) {
                TextureAtlasSprite textureAtlasSprite = (TextureAtlasSprite)obj;
                if (textureAtlasSprite.getIndexInMap() < 0) {
                    textureAtlasSprite.setIndexInMap(this.counterIndexInMap++);
                }
            }
        }

        return (IIcon)obj;
    }

    public int getTextureType() {
        return this.textureType;
    }

    @Override
    public void tick() {
        this.updateAnimations();
    }

    public void setMipmapLevels(int n) {
        this.mipmapLevels = n;
    }

    public void setAnisotropicFiltering(int n) {
        this.anisotropicFiltering = n;
    }

    public TextureAtlasSprite getTextureExtry(String str) {
        return (TextureAtlasSprite)this.mapRegisteredSprites.get(str);
    }

    public boolean setTextureEntry(String str, TextureAtlasSprite textureAtlasSprite) {
        if (!this.mapRegisteredSprites.containsKey(str)) {
            this.mapRegisteredSprites.put(str, textureAtlasSprite);
            if (textureAtlasSprite.getIndexInMap() < 0) {
                textureAtlasSprite.setIndexInMap(this.counterIndexInMap++);
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean setTextureEntry(TextureAtlasSprite textureAtlasSprite) {
        return this.setTextureEntry(textureAtlasSprite.getIconName(), textureAtlasSprite);
    }

    public String getBasePath() {
        return this.basePath;
    }

    public int getMipmapLevels() {
        return this.mipmapLevels;
    }

    public boolean isAbsoluteLocation(ResourceLocation resourceLocation) {
        String resourcePath = resourceLocation.getResourcePath();
        return this.isAbsoluteLocationPath(resourcePath);
    }

    public boolean isAbsoluteLocationPath(String str) {
        String toLowerCase = str.toLowerCase();
        return toLowerCase.startsWith("mcpatcher/") || toLowerCase.startsWith("optifine/");
    }

    public TextureAtlasSprite getIconSafe(String str) {
        return (TextureAtlasSprite)this.mapRegisteredSprites.get(str);
    }

    public int getStandardTileSize(Collection collection) {
        int[] ints = new int[16];

        for (TextureAtlasSprite textureAtlasSprite : (Iterable<TextureAtlasSprite>)collection) {
            if (textureAtlasSprite != null) {
                int powerOfTwo = TextureUtils.getPowerOfTwo(textureAtlasSprite.getWidth());
                int powerOfTwo2 = TextureUtils.getPowerOfTwo(textureAtlasSprite.getHeight());
                int max = Math.max(powerOfTwo, powerOfTwo2);
                if (max < ints.length) {
                    ints[max]++;
                }
            }
        }

        int n = 4;
        int n2 = 0;

        for (int i = 0; i < ints.length; i++) {
            int n3 = ints[i];
            if (n3 > n2) {
                n = i;
                n2 = n3;
            }
        }

        if (n < 4) {
            n = 4;
        }

        return TextureUtils.twoToPower(n);
    }

    public void updateIconGrid(int n, int n2) {
        this.iconGridCountX = -1;
        this.iconGridCountY = -1;
        this.iconGrid = null;
        if (this.iconGridSize > 0) {
            this.iconGridCountX = n / this.iconGridSize;
            this.iconGridCountY = n2 / this.iconGridSize;
            this.iconGrid = new TextureAtlasSprite[this.iconGridCountX * this.iconGridCountY];
            this.iconGridSizeU = 1.0 / this.iconGridCountX;
            this.iconGridSizeV = 1.0 / this.iconGridCountY;

            for (TextureAtlasSprite textureAtlasSprite : (Iterable<TextureAtlasSprite>)this.mapUploadedSprites.values()) {
                double d = 0.5 / n;
                double d2 = 0.5 / n2;
                double d3 = Math.min(textureAtlasSprite.getMinU(), textureAtlasSprite.getMaxU()) + d;
                double d4 = Math.min(textureAtlasSprite.getMinV(), textureAtlasSprite.getMaxV()) + d2;
                double d5 = Math.max(textureAtlasSprite.getMinU(), textureAtlasSprite.getMaxU()) - d;
                double d6 = Math.max(textureAtlasSprite.getMinV(), textureAtlasSprite.getMaxV()) - d2;
                int n3 = (int)(d3 / this.iconGridSizeU);
                int n4 = (int)(d4 / this.iconGridSizeV);
                int n5 = (int)(d5 / this.iconGridSizeU);
                int n6 = (int)(d6 / this.iconGridSizeV);

                for (int i = n3; i <= n5; i++) {
                    if (i >= 0 && i < this.iconGridCountX) {
                        for (int j = n4; j <= n6; j++) {
                            if (j >= 0 && j < this.iconGridCountX) {
                                int n7 = j * this.iconGridCountX + i;
                                this.iconGrid[n7] = textureAtlasSprite;
                            } else {
                                Config.warn("Invalid grid V: " + j + ", icon: " + textureAtlasSprite.getIconName());
                            }
                        }
                    } else {
                        Config.warn("Invalid grid U: " + i + ", icon: " + textureAtlasSprite.getIconName());
                    }
                }
            }
        }
    }

    public TextureAtlasSprite getIconByUV(double d, double d2) {
        if (this.iconGrid == null) {
            return null;
        }

        int n = (int)(d / this.iconGridSizeU);
        int n2 = (int)(d2 / this.iconGridSizeV);
        int n3 = n2 * this.iconGridCountX + n;
        return n3 >= 0 && n3 <= this.iconGrid.length ? this.iconGrid[n3] : null;
    }

    public TextureAtlasSprite getMissingSprite() {
        return this.missingImage;
    }

    public int getMaxTextureIndex() {
        return this.mapRegisteredSprites.size();
    }

    public boolean isTerrainAnimationActive(TextureAtlasSprite textureAtlasSprite) {
        return textureAtlasSprite == TextureUtils.iconWaterStill || textureAtlasSprite == TextureUtils.iconWaterFlow
            ? Config.isAnimatedTerrain()
            : (
                textureAtlasSprite == TextureUtils.iconLavaStill || textureAtlasSprite == TextureUtils.iconLavaFlow
                    ? Config.check2()
                    : (
                        textureAtlasSprite == TextureUtils.iconFireLayer0 || textureAtlasSprite == TextureUtils.iconFireLayer1
                            ? Config.check6()
                            : (
                                textureAtlasSprite == TextureUtils.iconPortal
                                    ? Config.isAnimatedPortal()
                                    : (textureAtlasSprite != TextureUtils.iconClock && textureAtlasSprite != TextureUtils.iconCompass ? Config.check() : true)
                            )
                    )
            );
    }

    public int getCountRegisteredSprites() {
        return this.counterIndexInMap;
    }

    public void loadTextureSafe(IResourceManager resourceManager) {
        try {
            this.loadTexture(resourceManager);
        } catch (IOException e) {
            Config.warn("Error loading texture map: " + this.basePath);
            e.printStackTrace();
        }
    }

    public BufferedImage makeDebugImage(int n, int n2) {
        BufferedImage bufferedImage = new BufferedImage(n, n2, 2);
        Graphics2D createGraphics = bufferedImage.createGraphics();
        createGraphics.setPaint(new Color(255, 255, 0));
        createGraphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        return bufferedImage;
    }

    public void addDebugSprite(TextureAtlasSprite textureAtlasSprite, BufferedImage bufferedImage) {
        if (textureAtlasSprite.getFrameCount() < 1) {
            Config.warn("Debug sprite has no data: " + textureAtlasSprite.getIconName());
        } else {
            int[] ints = textureAtlasSprite.getFrameTextureData(0)[0];
            bufferedImage.setRGB(textureAtlasSprite.getOriginX(), textureAtlasSprite.getOriginY(), textureAtlasSprite.getIconWidth(), textureAtlasSprite.getIconHeight(), ints, 0, textureAtlasSprite.getIconWidth());
        }
    }

    public void writeDebugImage(BufferedImage bufferedImage, String str) {
        try {
            ImageIO.write(bufferedImage, "png", new File(Config.getMinecraft().mcDataDir, str));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
