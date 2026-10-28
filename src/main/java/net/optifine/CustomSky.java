package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CustomSky {
    public static CustomSkyLayer[][] worldSkyLayers = (CustomSkyLayer[][])null;

    public static void reset() {
        worldSkyLayers = (CustomSkyLayer[][])null;
    }

    public static void update() {
        reset();
        if (Config.isCustomSky()) {
            worldSkyLayers = readCustomSkies();
        }
    }

    public static CustomSkyLayer[][] readCustomSkies() {
        CustomSkyLayer[][] customSkyLayerses = new CustomSkyLayer[10][0];
        String str = "mcpatcher/sky/world";
        int n = -1;

        for (int i = 0; i < customSkyLayerses.length; i++) {
            String str2 = str + i + "/sky";
            ArrayList list = new ArrayList();

            for (int j = 1; j < 1000; j++) {
                String str3 = str2 + j + ".properties";

                try {
                    ResourceLocation resourceLocation = new ResourceLocation(str3);
                    InputStream resourceStream = Config.getResourceStream(resourceLocation);
                    if (resourceStream == null) {
                        break;
                    }

                    Properties properties = new Properties();
                    properties.load(resourceStream);
                    resourceStream.close();
                    Config.dbg("CustomSky properties: " + str3);
                    String str4 = str2 + j + ".png";
                    CustomSkyLayer customSkyLayer = new CustomSkyLayer(properties, str4);
                    if (customSkyLayer.isValid(str3)) {
                        ResourceLocation resourceLocation2 = new ResourceLocation(customSkyLayer.source);
                        ITextureObject texture = TextureUtils.getTexture(resourceLocation2);
                        if (texture == null) {
                            Config.log("CustomSky: Texture not found: " + resourceLocation2);
                        } else {
                            customSkyLayer.textureId = texture.getGlTextureId();
                            list.add(customSkyLayer);
                            resourceStream.close();
                        }
                    }
                } catch (FileNotFoundException e) {
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (list.size() > 0) {
                CustomSkyLayer[] toArray = (CustomSkyLayer[])list.toArray(new CustomSkyLayer[list.size()]);
                customSkyLayerses[i] = toArray;
                n = i;
            }
        }

        if (n < 0) {
            return (CustomSkyLayer[][])null;
        }

        int n2 = n + 1;
        CustomSkyLayer[][] customSkyLayerses2 = new CustomSkyLayer[n2][0];

        for (int i = 0; i < customSkyLayerses2.length; i++) {
            customSkyLayerses2[i] = customSkyLayerses[i];
        }

        return customSkyLayerses2;
    }

    public static void renderSky(World world, TextureManager textureManager, float f) {
        if (worldSkyLayers != null) {
            int dimensionId = world.provider.dimensionId;
            if (dimensionId >= 0 && dimensionId < worldSkyLayers.length) {
                CustomSkyLayer[] customSkyLayers = worldSkyLayers[dimensionId];
                if (customSkyLayers != null) {
                    long worldTime = world.getWorldTime();
                    int n = (int)(worldTime % 24000L);
                    float celestialAngle = world.getCelestialAngle(f);
                    float rainStrength = world.getRainStrength(f);
                    float weightedThunderStrength = world.getWeightedThunderStrength(f);
                    if (rainStrength > 0.0F) {
                        weightedThunderStrength /= rainStrength;
                    }

                    for (int i = 0; i < customSkyLayers.length; i++) {
                        CustomSkyLayer customSkyLayer = customSkyLayers[i];
                        if (customSkyLayer.isActive(world, n)) {
                            customSkyLayer.render(world, n, celestialAngle, rainStrength, weightedThunderStrength);
                        }
                    }

                    float f2 = 1.0F - rainStrength;
                    Blender.clearBlend(f2);
                }
            }
        }
    }

    public static boolean hasSkyLayers(World world) {
        if (worldSkyLayers == null) {
            return false;
        } else {
            int dimensionId = world.provider.dimensionId;
            if (dimensionId >= 0 && dimensionId < worldSkyLayers.length) {
                CustomSkyLayer[] customSkyLayers = worldSkyLayers[dimensionId];
                return customSkyLayers == null ? false : customSkyLayers.length > 0;
            } else {
                return false;
            }
        }
    }

    public static CustomSkyLayer[][] getWorldSkyLayers() {
        return worldSkyLayers;
    }
}
