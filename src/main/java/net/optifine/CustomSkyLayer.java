package net.optifine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import org.lwjgl.opengl.GL11;

public class CustomSkyLayer {
    public String source = null;
    public int startFadeIn = -1;
    public int endFadeIn = -1;
    public int startFadeOut = -1;
    public int endFadeOut = -1;
    public int blend = 1;
    public boolean rotate = false;
    public float speed = 1.0F;
    public float[] axis;
    public RangeListInt days;
    public int daysLoop;
    public boolean weatherClear;
    public boolean weatherRain;
    public boolean weatherThunder;
    public BiomeGenBase[] biomes;
    public RangeListInt heights;
    public float transition;
    public SmoothFloat smoothPositionBrightness;
    public int textureId;
    public World lastWorld;
    public static float[] DEFAULT_AXIS = new float[]{1.0F, 0.0F, 0.0F};
    public static final String WEATHER_CLEAR = "clear";
    public static final String WEATHER_RAIN = "rain";
    public static final String WEATHER_THUNDER = "thunder";

    public CustomSkyLayer(Properties properties, String str) {
        this.axis = DEFAULT_AXIS;
        this.days = null;
        this.daysLoop = 8;
        this.weatherClear = true;
        this.weatherRain = false;
        this.weatherThunder = false;
        this.biomes = null;
        this.heights = null;
        this.transition = 1.0F;
        this.smoothPositionBrightness = null;
        this.textureId = -1;
        this.lastWorld = null;
        ConnectedParser connectedParser = new ConnectedParser("CustomSky");
        this.source = properties.getProperty("source", str);
        this.startFadeIn = this.parseTime(properties.getProperty("startFadeIn"));
        this.endFadeIn = this.parseTime(properties.getProperty("endFadeIn"));
        this.startFadeOut = this.parseTime(properties.getProperty("startFadeOut"));
        this.endFadeOut = this.parseTime(properties.getProperty("endFadeOut"));
        this.blend = Blender.parseBlend(properties.getProperty("blend"));
        this.rotate = this.parseBoolean(properties.getProperty("rotate"), true);
        this.speed = this.parseFloat(properties.getProperty("speed"), 1.0F);
        this.axis = this.parseAxis(properties.getProperty("axis"), DEFAULT_AXIS);
        this.days = connectedParser.parseRangeListInt(properties.getProperty("days"));
        this.daysLoop = connectedParser.parseInt(properties.getProperty("daysLoop"), 8);
        List parseWeatherList = this.parseWeatherList(properties.getProperty("weather", "clear"));
        this.weatherClear = parseWeatherList.contains("clear");
        this.weatherRain = parseWeatherList.contains("rain");
        this.weatherThunder = parseWeatherList.contains("thunder");
        this.biomes = connectedParser.parseBiomes(properties.getProperty("biomes"));
        this.heights = connectedParser.parseRangeListInt(properties.getProperty("heights"));
        this.transition = this.parseFloat(properties.getProperty("transition"), 1.0F);
    }

    public List<String> parseWeatherList(String str) {
        List asList = Arrays.asList("clear", "rain", "thunder");
        ArrayList list = new ArrayList();
        String[] tokenize = Config.tokenize(str, " ");

        for (int i = 0; i < tokenize.length; i++) {
            String str2 = tokenize[i];
            if (!asList.contains(str2)) {
                Config.warn("Unknown weather: " + str2);
            } else {
                list.add(str2);
            }
        }

        return list;
    }

    public int parseTime(String str) {
        if (str == null) {
            return -1;
        }

        String[] tokenize = Config.tokenize(str, ":");
        if (tokenize.length != 2) {
            Config.warn("Invalid time: " + str);
            return -1;
        }

        String str2 = tokenize[0];
        String str3 = tokenize[1];
        int parseInt = Config.parseInt(str2, -1);
        int parseInt2 = Config.parseInt(str3, -1);
        if (parseInt >= 0 && parseInt <= 23 && parseInt2 >= 0 && parseInt2 <= 59) {
            parseInt -= 6;
            if (parseInt < 0) {
                parseInt += 24;
            }

            return parseInt * 1000 + (int)(parseInt2 / 60.0 * 1000.0);
        } else {
            Config.warn("Invalid time: " + str);
            return -1;
        }
    }

    public boolean parseBoolean(String str, boolean flag) {
        if (str == null) {
            return flag;
        }

        if (str.toLowerCase().equals("true")) {
            return true;
        }

        if (str.toLowerCase().equals("false")) {
            return false;
        }

        Config.warn("Unknown boolean: " + str);
        return flag;
    }

    public float parseFloat(String str, float f) {
        if (str == null) {
            return f;
        } else {
            float parseFloat = Config.parseFloat(str, Float.MIN_VALUE);
            if (parseFloat == Float.MIN_VALUE) {
                Config.warn("Invalid value: " + str);
                return f;
            } else {
                return parseFloat;
            }
        }
    }

    public float[] parseAxis(String str, float[] floats) {
        if (str == null) {
            return floats;
        }

        String[] tokenize = Config.tokenize(str, " ");
        if (tokenize.length != 3) {
            Config.warn("Invalid axis: " + str);
            return floats;
        }

        float[] floats2 = new float[3];

        for (int i = 0; i < tokenize.length; i++) {
            floats2[i] = Config.parseFloat(tokenize[i], Float.MIN_VALUE);
            if (floats2[i] == Float.MIN_VALUE) {
                Config.warn("Invalid axis: " + str);
                return floats;
            }

            if (floats2[i] < -1.0F || floats2[i] > 1.0F) {
                Config.warn("Invalid axis values: " + str);
                return floats;
            }
        }

        float f = floats2[0];
        float f2 = floats2[1];
        float f3 = floats2[2];
        if (f * f + f2 * f2 + f3 * f3 < 1.0E-5F) {
            Config.warn("Invalid axis values: " + str);
            return floats;
        } else {
            return new float[]{f3, f2, -f};
        }
    }

    public boolean isValid(String str) {
        if (this.source == null) {
            Config.warn("No source texture: " + str);
            return false;
        }

        this.source = TextureUtils.fixResourcePath(this.source, TextureUtils.getBasePath(str));
        if (this.startFadeIn >= 0 && this.endFadeIn >= 0 && this.endFadeOut >= 0) {
            int normalizeTime = this.normalizeTime(this.endFadeIn - this.startFadeIn);
            if (this.startFadeOut < 0) {
                this.startFadeOut = this.normalizeTime(this.endFadeOut - normalizeTime);
                if (this.timeBetween(this.startFadeOut, this.startFadeIn, this.endFadeIn)) {
                    this.startFadeOut = this.endFadeIn;
                }
            }

            int normalizeTime2 = this.normalizeTime(this.startFadeOut - this.endFadeIn);
            int normalizeTime3 = this.normalizeTime(this.endFadeOut - this.startFadeOut);
            int normalizeTime4 = this.normalizeTime(this.startFadeIn - this.endFadeOut);
            int n = normalizeTime + normalizeTime2 + normalizeTime3 + normalizeTime4;
            if (n != 24000) {
                Config.warn("Invalid fadeIn/fadeOut times, sum is not 24h: " + n);
                return false;
            } else if (this.speed < 0.0F) {
                Config.warn("Invalid speed: " + this.speed);
                return false;
            } else if (this.daysLoop <= 0) {
                Config.warn("Invalid daysLoop: " + this.daysLoop);
                return false;
            } else {
                return true;
            }
        } else {
            Config.warn("Invalid times, required are: startFadeIn, endFadeIn and endFadeOut.");
            return false;
        }
    }

    public int normalizeTime(int n) {
        while (n >= 24000) {
            n -= 24000;
        }

        while (n < 0) {
            n += 24000;
        }

        return n;
    }

    public void render(World world, int n, float f, float f2, float f3) {
        float positionBrightness = this.getPositionBrightness(world);
        float weatherBrightness = this.getWeatherBrightness(f2, f3);
        float fadeBrightness = this.getFadeBrightness(n);
        float f4 = positionBrightness * weatherBrightness * fadeBrightness;
        f4 = Config.limit(f4, 0.0F, 1.0F);
        if (f4 >= 1.0E-4F) {
            GL11.glBindTexture(3553, this.textureId);
            Blender.setupBlend(this.blend, f4);
            GL11.glPushMatrix();
            if (this.rotate) {
                float f5 = 0.0F;
                if (this.speed != Math.round(this.speed)) {
                    long l = (world.getWorldTime() + 18000L) / 24000L;
                    double d = this.speed % 1.0F;
                    double d2 = l * d;
                    f5 = (float)(d2 % 1.0);
                }

                GL11.glRotatef(360.0F * (f5 + f * this.speed), this.axis[0], this.axis[1], this.axis[2]);
            }

            Tessellator instance = Tessellator.instance;
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
            this.renderSide(instance, 4);
            GL11.glPushMatrix();
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            this.renderSide(instance, 1);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            this.renderSide(instance, 0);
            GL11.glPopMatrix();
            GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
            this.renderSide(instance, 5);
            GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
            this.renderSide(instance, 2);
            GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
            this.renderSide(instance, 3);
            GL11.glPopMatrix();
        }
    }

    public float getPositionBrightness(World world) {
        if (this.biomes == null && this.heights == null) {
            return 1.0F;
        }

        float positionBrightnessRaw = this.getPositionBrightnessRaw(world);
        if (this.smoothPositionBrightness == null) {
            this.smoothPositionBrightness = new SmoothFloat(positionBrightnessRaw, this.transition);
        }

        return this.smoothPositionBrightness.getSmoothValue(positionBrightnessRaw);
    }

    public float getPositionBrightnessRaw(World world) {
        EntityLivingBase renderViewEntity = Minecraft.getMinecraft().renderViewEntity;
        if (renderViewEntity == null) {
            return 0.0F;
        }

        double posX = renderViewEntity.posX;
        double posY = renderViewEntity.posY;
        double posZ = renderViewEntity.posZ;
        if (this.biomes != null) {
            BiomeGenBase biomeGenForCoords = world.getBiomeGenForCoords((int)posX, (int)posZ);
            if (biomeGenForCoords == null) {
                return 0.0F;
            }

            if (!Matches.biome(biomeGenForCoords, this.biomes)) {
                return 0.0F;
            }
        }

        return this.heights != null && !this.heights.isInRange((int)posY) ? 0.0F : 1.0F;
    }

    public float getWeatherBrightness(float f, float f2) {
        float f3 = 1.0F - f;
        float f4 = f - f2;
        float f5 = 0.0F;
        if (this.weatherClear) {
            f5 += f3;
        }

        if (this.weatherRain) {
            f5 += f4;
        }

        if (this.weatherThunder) {
            f5 += f2;
        }

        return NumUtils.limit(f5, 0.0F, 1.0F);
    }

    public float getFadeBrightness(int n) {
        if (this.timeBetween(n, this.startFadeIn, this.endFadeIn)) {
            int normalizeTime = this.normalizeTime(this.endFadeIn - this.startFadeIn);
            int normalizeTime2 = this.normalizeTime(n - this.startFadeIn);
            return (float)normalizeTime2 / normalizeTime;
        } else if (this.timeBetween(n, this.endFadeIn, this.startFadeOut)) {
            return 1.0F;
        } else if (this.timeBetween(n, this.startFadeOut, this.endFadeOut)) {
            int normalizeTime = this.normalizeTime(this.endFadeOut - this.startFadeOut);
            int normalizeTime2 = this.normalizeTime(n - this.startFadeOut);
            return 1.0F - (float)normalizeTime2 / normalizeTime;
        } else {
            return 0.0F;
        }
    }

    public void renderSide(Tessellator tessellator, int n) {
        double d = n % 3 / 3.0;
        double d2 = n / 3 / 2.0;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-70.0, -70.0, -70.0, d, d2);
        tessellator.addVertexWithUV(-70.0, -70.0, 70.0, d, d2 + 0.5);
        tessellator.addVertexWithUV(70.0, -70.0, 70.0, d + 0.3333333333333333, d2 + 0.5);
        tessellator.addVertexWithUV(70.0, -70.0, -70.0, d + 0.3333333333333333, d2);
        tessellator.draw();
    }

    public boolean isActive(World world, int n) {
        if (world != this.lastWorld) {
            this.lastWorld = world;
            this.smoothPositionBrightness = null;
        }

        if (this.timeBetween(n, this.endFadeOut, this.startFadeIn)) {
            return false;
        }

        if (this.days != null) {
            long worldTime = world.getWorldTime();
            long l = worldTime - this.startFadeIn;

            while (l < 0L) {
                l += 24000 * this.daysLoop;
            }

            int n2 = (int)(l / 24000L);
            int n3 = n2 % this.daysLoop;
            if (!this.days.isInRange(n3)) {
                return false;
            }
        }

        return true;
    }

    public boolean timeBetween(int n, int n2, int n3) {
        return n2 <= n3 ? n >= n2 && n <= n3 : n >= n2 || n <= n3;
    }

    @Override
    public String toString() {
        return "" + this.source + ", " + this.startFadeIn + "-" + this.endFadeIn + " " + this.startFadeOut + "-" + this.endFadeOut;
    }
}
