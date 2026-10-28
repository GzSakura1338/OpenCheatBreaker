package net.optifine;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.InputStream;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.ITickableTextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import shadersmod.client.MultiTexID;
import shadersmod.client.Shaders;

public class TextureUtils {
    public static final String texGrassTop = "grass_top";
    public static final String texStone = "stone";
    public static final String texDirt = "dirt";
    public static final String texGrassSide = "grass_side";
    public static final String texStoneslabSide = "stone_slab_side";
    public static final String texStoneslabTop = "stone_slab_top";
    public static final String texBedrock = "bedrock";
    public static final String texSand = "sand";
    public static final String texGravel = "gravel";
    public static final String texLogOak = "log_oak";
    public static final String texLogOakTop = "log_oak_top";
    public static final String texGoldOre = "gold_ore";
    public static final String texIronOre = "iron_ore";
    public static final String texCoalOre = "coal_ore";
    public static final String texObsidian = "obsidian";
    public static final String texGrassSideOverlay = "grass_side_overlay";
    public static final String texSnow = "snow";
    public static final String texGrassSideSnowed = "grass_side_snowed";
    public static final String texMyceliumSide = "mycelium_side";
    public static final String texMyceliumTop = "mycelium_top";
    public static final String texDiamondOre = "diamond_ore";
    public static final String texRedstoneOre = "redstone_ore";
    public static final String texLapisOre = "lapis_ore";
    public static final String texLeavesOak = "leaves_oak";
    public static final String texLeavesOakOpaque = "leaves_oak_opaque";
    public static final String texLeavesJungle = "leaves_jungle";
    public static final String texLeavesJungleOpaque = "leaves_jungle_opaque";
    public static final String texCactusSide = "cactus_side";
    public static final String texClay = "clay";
    public static final String texFarmlandWet = "farmland_wet";
    public static final String texFarmlandDry = "farmland_dry";
    public static final String texNetherrack = "netherrack";
    public static final String texSoulSand = "soul_sand";
    public static final String texGlowstone = "glowstone";
    public static final String texLogSpruce = "log_spruce";
    public static final String texLogBirch = "log_birch";
    public static final String texLeavesSpruce = "leaves_spruce";
    public static final String texLeavesSpruceOpaque = "leaves_spruce_opaque";
    public static final String texLogJungle = "log_jungle";
    public static final String texEndStone = "end_stone";
    public static final String texSandstoneTop = "sandstone_top";
    public static final String texSandstoneBottom = "sandstone_bottom";
    public static final String texRedstoneLampOff = "redstone_lamp_off";
    public static final String texRedstoneLampOn = "redstone_lamp_on";
    public static final String texWaterStill = "water_still";
    public static final String texWaterFlow = "water_flow";
    public static final String texLavaStill = "lava_still";
    public static final String texLavaFlow = "lava_flow";
    public static final String texFireLayer0 = "fire_layer_0";
    public static final String texFireLayer1 = "fire_layer_1";
    public static final String texPortal = "portal";
    public static final String texGlass = "glass";
    public static final String texGlassPaneTop = "glass_pane_top";
    public static final String texCompass = "compass";
    public static final String texClock = "clock";
    public static IIcon iconGrassTop;
    public static IIcon iconGrassSide;
    public static IIcon iconGrassSideOverlay;
    public static IIcon iconSnow;
    public static IIcon iconGrassSideSnowed;
    public static IIcon iconMyceliumSide;
    public static IIcon iconMyceliumTop;
    public static IIcon iconWaterStill;
    public static IIcon iconWaterFlow;
    public static IIcon iconLavaStill;
    public static IIcon iconLavaFlow;
    public static IIcon iconPortal;
    public static IIcon iconFireLayer0;
    public static IIcon iconFireLayer1;
    public static IIcon iconGlass;
    public static IIcon iconGlassPaneTop;
    public static IIcon iconCompass;
    public static IIcon iconClock;
    public static IntBuffer staticBuffer = GLAllocation.createDirectIntBuffer(256);

    public static void update() {
        TextureMap textureMapBlocks = TextureMap.textureMapBlocks;
        if (textureMapBlocks != null) {
            iconGrassTop = textureMapBlocks.getIconSafe("grass_top");
            iconGrassSide = textureMapBlocks.getIconSafe("grass_side");
            iconGrassSideOverlay = textureMapBlocks.getIconSafe("grass_side_overlay");
            iconSnow = textureMapBlocks.getIconSafe("snow");
            iconGrassSideSnowed = textureMapBlocks.getIconSafe("grass_side_snowed");
            iconMyceliumSide = textureMapBlocks.getIconSafe("mycelium_side");
            iconMyceliumTop = textureMapBlocks.getIconSafe("mycelium_top");
            iconWaterStill = textureMapBlocks.getIconSafe("water_still");
            iconWaterFlow = textureMapBlocks.getIconSafe("water_flow");
            iconLavaStill = textureMapBlocks.getIconSafe("lava_still");
            iconLavaFlow = textureMapBlocks.getIconSafe("lava_flow");
            iconFireLayer0 = textureMapBlocks.getIconSafe("fire_layer_0");
            iconFireLayer1 = textureMapBlocks.getIconSafe("fire_layer_1");
            iconPortal = textureMapBlocks.getIconSafe("portal");
            iconGlass = textureMapBlocks.getIconSafe("glass");
            iconGlassPaneTop = textureMapBlocks.getIconSafe("glass_pane_top");
            TextureMap textureMapItems = TextureMap.textureMapItems;
            if (textureMapItems != null) {
                iconCompass = textureMapItems.getIconSafe("compass");
                iconClock = textureMapItems.getIconSafe("clock");
            }
        }
    }

    public static BufferedImage fixTextureDimensions(String str, BufferedImage bufferedImage) {
        if (str.startsWith("/mob/zombie") || str.startsWith("/mob/pigzombie")) {
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            if (width == height * 2) {
                BufferedImage bufferedImage2 = new BufferedImage(width, height * 2, 2);
                Graphics2D createGraphics = bufferedImage2.createGraphics();
                createGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                createGraphics.drawImage(bufferedImage, 0, 0, width, height, (ImageObserver)null);
                return bufferedImage2;
            }
        }

        return bufferedImage;
    }

    public static TextureAtlasSprite getTextureAtlasSprite(IIcon icon) {
        return icon instanceof TextureAtlasSprite ? (TextureAtlasSprite)icon : null;
    }

    public static int ceilPowerOfTwo(int n) {
        int n2 = 1;

        while (n2 < n) {
            n2 *= 2;
        }

        return n2;
    }

    public static int getPowerOfTwo(int n) {
        int n2 = 1;

        int n3;
        for (n3 = 0; n2 < n; n3++) {
            n2 *= 2;
        }

        return n3;
    }

    public static int twoToPower(int n) {
        int n2 = 1;

        for (int i = 0; i < n; i++) {
            n2 *= 2;
        }

        return n2;
    }

    public static void refreshBlockTextures() {
        Config.dbg("*** Reloading block textures ***");
        WrUpdates.finishCurrentUpdate();
        TextureMap.textureMapBlocks.loadTextureSafe(Config.getResourceManager());
        update();
        NaturalTextures.update();
        TextureMap.textureMapBlocks.updateAnimations();
    }

    public static ITextureObject getTexture(String str) {
        return getTexture(new ResourceLocation(str));
    }

    public static ITextureObject getTexture(ResourceLocation resourceLocation) {
        ITextureObject texture = Config.getTextureManager().getTexture(resourceLocation);
        if (texture != null) {
            return texture;
        }

        if (!Config.hasResource(resourceLocation)) {
            return null;
        }

        SimpleTexture simpleTexture = new SimpleTexture(resourceLocation);
        Config.getTextureManager().loadTexture(resourceLocation, simpleTexture);
        return simpleTexture;
    }

    public static void resourcesReloaded(IResourceManager resourceManager) {
        if (TextureMap.textureMapBlocks != null) {
            Config.dbg("*** Reloading custom textures ***");
            CustomSky.reset();
            TextureAnimations.reset();
            WrUpdates.finishCurrentUpdate();
            update();
            NaturalTextures.update();
            TextureAnimations.update();
            CustomColorizer.update();
            CustomSky.update();
            RandomMobs.resetTextures();
            Shaders.resourcesReloaded();
            Lang.resourcesReloaded();
            Config.updateTexturePackClouds();
            Config.getTextureManager().tick();
        }
    }

    public static void refreshTextureMaps(IResourceManager resourceManager) {
        TextureMap.textureMapBlocks.loadTextureSafe(resourceManager);
        TextureMap.textureMapItems.loadTextureSafe(resourceManager);
        update();
        NaturalTextures.update();
    }

    public static void registerResourceListener() {
        IResourceManager resourceManager = Config.getResourceManager();
        if (resourceManager instanceof IReloadableResourceManager) {
            IReloadableResourceManager reloadableResourceManager = (IReloadableResourceManager)resourceManager;
            IResourceManagerReloadListener resourceManagerReloadListener = new IResourceManagerReloadListener() {
                @Override
                public void onResourceManagerReload(IResourceManager resourceManager2) {
                    TextureUtils.resourcesReloaded(resourceManager2);
                }
            };
            reloadableResourceManager.registerReloadListener(resourceManagerReloadListener);
        }

        ITickableTextureObject tickableTextureObject = new ITickableTextureObject() {
            @Override
            public void tick() {
                TextureAnimations.updateCustomAnimations();
            }

            @Override
            public void loadTexture(IResourceManager resourceManager2) {
            }

            @Override
            public int getGlTextureId() {
                return 0;
            }

            @Override
            public MultiTexID getMultiTexID() {
                return null;
            }
        };
        ResourceLocation resourceLocation = new ResourceLocation("optifine/TickableTextures");
        Config.getTextureManager().loadTickableTexture(resourceLocation, tickableTextureObject);
    }

    public static ResourceLocation fixResourceLocation(ResourceLocation resourceLocation, String str) {
        if (!resourceLocation.getResourceDomain().equals("minecraft")) {
            return resourceLocation;
        }

        String resourcePath = resourceLocation.getResourcePath();
        String fixResourcePath2 = fixResourcePath(resourcePath, str);
        if (fixResourcePath2 != resourcePath) {
            resourceLocation = new ResourceLocation(resourceLocation.getResourceDomain(), fixResourcePath2);
        }

        return resourceLocation;
    }

    public static String fixResourcePath(String str, String str2) {
        String str3 = "assets/minecraft/";
        if (str.startsWith(str3)) {
            return str.substring(str3.length());
        }

        if (str.startsWith("./")) {
            str = str.substring(2);
            if (!str2.endsWith("/")) {
                str2 = str2 + "/";
            }

            return str2 + str;
        } else {
            if (str.startsWith("/~")) {
                str = str.substring(1);
            }

            String str4 = "mcpatcher/";
            if (str.startsWith("~/")) {
                str = str.substring(2);
                return str4 + str;
            } else {
                return str.startsWith("/") ? str4 + str.substring(1) : str;
            }
        }
    }

    public static String getBasePath(String str) {
        int lastIndexOf = str.lastIndexOf(47);
        return lastIndexOf < 0 ? "" : str.substring(0, lastIndexOf);
    }

    public static BufferedImage readBufferedImage(InputStream inputStream) throws java.io.IOException {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(inputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return bufferedImage;
    }

    public static BufferedImage scaleImage(BufferedImage bufferedImage, int n) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int n2 = height * n / width;
        BufferedImage bufferedImage2 = new BufferedImage(n, n2, 2);
        Graphics2D createGraphics = bufferedImage2.createGraphics();
        Object obj = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
        if (n < width || n % width != 0) {
            obj = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
        }

        createGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, obj);
        createGraphics.drawImage(bufferedImage, 0, 0, n, n2, (ImageObserver)null);
        return bufferedImage2;
    }

    public static int getGLMaximumTextureSize() {
        for (int i = 65536; i > 0; i >>= 1) {
            GL11.glTexImage2D(32868, 0, 6408, i, i, 0, 6408, 5121, (IntBuffer)null);
            int glGetError = GL11.glGetError();
            int glGetTexLevelParameteri = GL11.glGetTexLevelParameteri(32868, 0, 4096);
            if (glGetTexLevelParameteri != 0) {
                return i;
            }
        }

        return -1;
    }
}
