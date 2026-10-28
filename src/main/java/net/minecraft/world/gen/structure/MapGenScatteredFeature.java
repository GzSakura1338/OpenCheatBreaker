package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces.DesertPyramid;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces.JunglePyramid;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces.SwampHut;

public class MapGenScatteredFeature extends MapGenStructure {
    public static List biomelist = Arrays.asList(
        BiomeGenBase.desert, BiomeGenBase.desertHills, BiomeGenBase.jungle, BiomeGenBase.jungleHills, BiomeGenBase.swampland
    );
    public List scatteredFeatureSpawnList = new ArrayList();
    public int maxDistanceBetweenScatteredFeatures = 32;
    public int minDistanceBetweenScatteredFeatures = 8;

    public MapGenScatteredFeature() {
        this.scatteredFeatureSpawnList.add(new SpawnListEntry(EntityWitch.class, 1, 1, 1));
    }

    public MapGenScatteredFeature(Map map) {
        this();

        for (Entry entry : (Iterable<Entry>)map.entrySet()) {
            if (entry.getKey().equals("distance")) {
                this.maxDistanceBetweenScatteredFeatures = MathHelper.parseIntWithDefaultAndMax(
                    (String)entry.getValue(), this.maxDistanceBetweenScatteredFeatures, this.minDistanceBetweenScatteredFeatures + 1
                );
            }
        }
    }

    @Override
    public String getStructureName() {
        return "Temple";
    }

    @Override
    public boolean canSpawnStructureAtCoords(int n, int n2) {
        int n3 = n;
        int n4 = n2;
        if (n < 0) {
            n -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        if (n2 < 0) {
            n2 -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        int n5 = n / this.maxDistanceBetweenScatteredFeatures;
        int n6 = n2 / this.maxDistanceBetweenScatteredFeatures;
        Random setRandomSeed = this.worldObj.setRandomSeed(n5, n6, 14357617);
        n5 *= this.maxDistanceBetweenScatteredFeatures;
        n6 *= this.maxDistanceBetweenScatteredFeatures;
        n5 += setRandomSeed.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);
        n6 += setRandomSeed.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);
        if (n3 == n5 && n4 == n6) {
            BiomeGenBase biomeGenAt = this.worldObj.getWorldChunkManager().getBiomeGenAt(n3 * 16 + 8, n4 * 16 + 8);

            for (BiomeGenBase biomeGenBase : (Iterable<BiomeGenBase>)biomelist) {
                if (biomeGenAt == biomeGenBase) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public StructureStart getStructureStart(int n, int n2) {
        return new MapGenScatteredFeature.Start(this.worldObj, this.rand, n, n2);
    }

    public boolean check3(int n, int n2, int n3) {
        StructureStart findStructureStart = this.findStructureStart(n, n2, n3);
        if (findStructureStart != null && findStructureStart instanceof MapGenScatteredFeature.Start && !findStructureStart.components.isEmpty()) {
            StructureComponent first = (StructureComponent)findStructureStart.components.getFirst();
            return first instanceof SwampHut;
        } else {
            return false;
        }
    }

    public List getScatteredFeatureSpawnList() {
        return this.scatteredFeatureSpawnList;
    }

    public static class Start extends StructureStart {
        public Start() {
        }

        public Start(World world, Random random, int n, int n2) {
            super(n, n2);
            BiomeGenBase biomeGenForCoords = world.getBiomeGenForCoords(n * 16 + 8, n2 * 16 + 8);
            if (biomeGenForCoords == BiomeGenBase.jungle || biomeGenForCoords == BiomeGenBase.jungleHills) {
                JunglePyramid junglePyramid = new JunglePyramid(random, n * 16, n2 * 16);
                this.components.add(junglePyramid);
            } else if (biomeGenForCoords == BiomeGenBase.swampland) {
                SwampHut swampHut = new SwampHut(random, n * 16, n2 * 16);
                this.components.add(swampHut);
            } else {
                DesertPyramid desertPyramid = new DesertPyramid(random, n * 16, n2 * 16);
                this.components.add(desertPyramid);
            }

            this.updateBoundingBox();
        }
    }
}
