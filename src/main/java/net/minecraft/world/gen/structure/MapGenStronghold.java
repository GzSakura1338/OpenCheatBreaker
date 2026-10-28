package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.StructureStrongholdPieces.Stairs2;

public class MapGenStronghold extends MapGenStructure {
    public ChunkCoordIntPair[] structureCoords = new ChunkCoordIntPair[3];
    public boolean ranBiomeCheck;
    public double distance = 32.0;
    public int spread = 3;
    public List biomes = new ArrayList();

    public MapGenStronghold() {
        for (BiomeGenBase biomeGenBase : BiomeGenBase.getBiomeGenArray()) {
            if (biomeGenBase != null && biomeGenBase.minHeight > 0.0F) {
                this.biomes.add(biomeGenBase);
            }
        }
    }

    public MapGenStronghold(Map map) {
        this();

        for (Entry entry : (Iterable<Entry>)map.entrySet()) {
            if (entry.getKey().equals("distance")) {
                this.distance = MathHelper.parseDoubleWithDefaultAndMax((String)entry.getValue(), this.distance, 1.0);
            } else if (entry.getKey().equals("count")) {
                this.structureCoords = new ChunkCoordIntPair[MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.structureCoords.length, 1)];
            } else if (entry.getKey().equals("spread")) {
                this.spread = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.spread, 1);
            }
        }
    }

    @Override
    public String getStructureName() {
        return "Stronghold";
    }

    @Override
    public boolean canSpawnStructureAtCoords(int n, int n2) {
        if (!this.ranBiomeCheck) {
            Random random = new Random();
            random.setSeed(this.worldObj.getSeed());
            double d = random.nextDouble() * Math.PI * 2.0;
            int n3 = 1;

            for (int i = 0; i < this.structureCoords.length; i++) {
                double d2 = (1.25 * n3 + random.nextDouble()) * this.distance * n3;
                int round = (int)Math.round(Math.cos(d) * d2);
                int round2 = (int)Math.round(Math.sin(d) * d2);
                ChunkPosition findBiomePosition = this.worldObj.getWorldChunkManager().findBiomePosition((round << 4) + 8, (round2 << 4) + 8, 112, this.biomes, random);
                if (findBiomePosition != null) {
                    round = findBiomePosition.chunkPosX >> 4;
                    round2 = findBiomePosition.chunkPosZ >> 4;
                }

                this.structureCoords[i] = new ChunkCoordIntPair(round, round2);
                d += (Math.PI * 2) * n3 / this.spread;
                if (i == this.spread) {
                    n3 += 2 + random.nextInt(5);
                    this.spread = this.spread + 1 + random.nextInt(2);
                }
            }

            this.ranBiomeCheck = true;
        }

        for (ChunkCoordIntPair chunkCoordIntPair : this.structureCoords) {
            if (n == chunkCoordIntPair.chunkXPos && n2 == chunkCoordIntPair.chunkZPos) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List getCoordList() {
        ArrayList list = new ArrayList();

        for (ChunkCoordIntPair chunkCoordIntPair : this.structureCoords) {
            if (chunkCoordIntPair != null) {
                list.add(chunkCoordIntPair.getCenter(64));
            }
        }

        return list;
    }

    @Override
    public StructureStart getStructureStart(int n, int n2) {
        MapGenStronghold.Start start = new MapGenStronghold.Start(this.worldObj, this.rand, n, n2);

        while (start.getComponents().isEmpty() || ((Stairs2)start.getComponents().get(0)).strongholdPortalRoom == null) {
            start = new MapGenStronghold.Start(this.worldObj, this.rand, n, n2);
        }

        return start;
    }

    public static class Start extends StructureStart {
        public Start() {
        }

        public Start(World world, Random random, int n, int n2) {
            super(n, n2);
            StructureStrongholdPieces.prepareStructurePieces();
            Stairs2 stairs2 = new Stairs2(0, random, (n << 4) + 2, (n2 << 4) + 2);
            this.components.add(stairs2);
            stairs2.buildComponent(stairs2, this.components, random);
            List list = stairs2.list;

            while (!list.isEmpty()) {
                int nextInt = random.nextInt(list.size());
                StructureComponent structureComponent = (StructureComponent)list.remove(nextInt);
                structureComponent.buildComponent(stairs2, this.components, random);
            }

            this.updateBoundingBox();
            this.markAvailableHeight(world, random, 10);
        }
    }
}
