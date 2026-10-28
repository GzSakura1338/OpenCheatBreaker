package net.minecraft.world;

import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.FlatGeneratorInfo;

public abstract class WorldProvider {
    public static float[] moonPhaseFactors = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
    public World worldObj;
    public WorldType terrainType;
    public String generatorOptions;
    public WorldChunkManager worldChunkMgr;
    public boolean isHellWorld;
    public boolean hasNoSky;
    public float[] lightBrightnessTable = new float[16];
    public int dimensionId;
    public float[] colorsSunriseSunset = new float[4];

    public void registerWorld(World world) {
        this.worldObj = world;
        this.terrainType = world.getWorldInfo().getTerrainType();
        this.generatorOptions = world.getWorldInfo().getGeneratorOptions();
        this.registerWorldChunkManager();
        this.generateLightBrightnessTable();
    }

    public void generateLightBrightnessTable() {
        float f = 0.0F;

        for (int i = 0; i <= 15; i++) {
            float f2 = 1.0F - i / 15.0F;
            this.lightBrightnessTable[i] = (1.0F - f2) / (f2 * 3.0F + 1.0F) * (1.0F - f) + f;
        }
    }

    public void registerWorldChunkManager() {
        if (this.worldObj.getWorldInfo().getTerrainType() == WorldType.FLAT) {
            FlatGeneratorInfo createFlatGeneratorFromString = FlatGeneratorInfo.createFlatGeneratorFromString(this.worldObj.getWorldInfo().getGeneratorOptions());
            this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.getBiome(createFlatGeneratorFromString.getBiome()), 0.5F);
        } else {
            this.worldChunkMgr = new WorldChunkManager(this.worldObj);
        }
    }

    public IChunkProvider createChunkGenerator() {
        return this.terrainType == WorldType.FLAT
            ? new ChunkProviderFlat(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled(), this.generatorOptions)
            : new ChunkProviderGenerate(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled());
    }

    public boolean canCoordinateBeSpawn(int n, int n2) {
        return this.worldObj.getTopBlock(n, n2) == Blocks.grass;
    }

    public float calculateCelestialAngle(long l, float f) {
        int n = (int)(l % 24000L);
        float f2 = (n + f) / 24000.0F - 0.25F;
        if (f2 < 0.0F) {
            f2++;
        }

        if (f2 > 1.0F) {
            f2--;
        }

        float f3 = f2;
        f2 = 1.0F - (float)((Math.cos(f2 * Math.PI) + 1.0) / 2.0);
        return f3 + (f2 - f3) / 3.0F;
    }

    public int getMoonPhase(long l) {
        return (int)(l / 24000L % 8L + 8L) % 8;
    }

    public boolean isSurfaceWorld() {
        return true;
    }

    public float[] calcSunriseSunsetColors(float f, float f2) {
        float f3 = 0.4F;
        float f4 = MathHelper.cos(f * (float) Math.PI * 2.0F) - 0.0F;
        float f5 = -0.0F;
        if (f4 >= f5 - f3 && f4 <= f5 + f3) {
            float f6 = (f4 - f5) / f3 * 0.5F + 0.5F;
            float f7 = 1.0F - (1.0F - MathHelper.sin(f6 * (float) Math.PI)) * 0.99F;
            f7 *= f7;
            this.colorsSunriseSunset[0] = f6 * 0.3F + 0.7F;
            this.colorsSunriseSunset[1] = f6 * f6 * 0.7F + 0.2F;
            this.colorsSunriseSunset[2] = f6 * f6 * 0.0F + 0.2F;
            this.colorsSunriseSunset[3] = f7;
            return this.colorsSunriseSunset;
        } else {
            return null;
        }
    }

    public Vec3 getFogColor(float f, float f2) {
        float f3 = MathHelper.cos(f * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
        if (f3 < 0.0F) {
            f3 = 0.0F;
        }

        if (f3 > 1.0F) {
            f3 = 1.0F;
        }

        float f4 = 0.7529412F;
        float f5 = 0.84705883F;
        float f6 = 1.0F;
        f4 *= f3 * 0.94F + 0.06F;
        f5 *= f3 * 0.94F + 0.06F;
        f6 *= f3 * 0.91F + 0.09F;
        return Vec3.createVectorHelper(f4, f5, f6);
    }

    public boolean canRespawnHere() {
        return true;
    }

    public static WorldProvider getProviderForDimension(int n) {
        return n == -1 ? new WorldProviderHell() : (n == 0 ? new WorldProviderSurface() : (n == 1 ? new WorldProviderEnd() : null));
    }

    public float getCloudHeight() {
        return 128.0F;
    }

    public boolean isSkyColored() {
        return true;
    }

    public ChunkCoordinates getEntrancePortalLocation() {
        return null;
    }

    public int getAverageGroundLevel() {
        return this.terrainType == WorldType.FLAT ? 4 : 64;
    }

    public boolean getWorldHasVoidParticles() {
        return this.terrainType != WorldType.FLAT && !this.hasNoSky;
    }

    public double getVoidFogYFactor() {
        return this.terrainType == WorldType.FLAT ? 1.0 : 0.03125;
    }

    public boolean doesXZShowFog(int n, int n2) {
        return false;
    }

    public abstract String getDimensionName();
}
