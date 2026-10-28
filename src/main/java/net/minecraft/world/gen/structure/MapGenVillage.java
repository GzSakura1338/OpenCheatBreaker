package net.minecraft.world.gen.structure;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.StructureVillagePieces.Road;

public class MapGenVillage extends MapGenStructure {
    public static List villageSpawnBiomes = Arrays.asList(BiomeGenBase.plains, BiomeGenBase.desert, BiomeGenBase.savanna);
    public int terrainType;
    public int distance = 32;
    public int intValue = 8;

    public MapGenVillage() {
    }

    public MapGenVillage(Map map) {
        this();

        for (Entry entry : (Iterable<Entry>)map.entrySet()) {
            if (entry.getKey().equals("size")) {
                this.terrainType = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.terrainType, 0);
            } else if (entry.getKey().equals("distance")) {
                this.distance = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.distance, this.intValue + 1);
            }
        }
    }

    @Override
    public String getStructureName() {
        return "Village";
    }

    @Override
    public boolean canSpawnStructureAtCoords(int n, int n2) {
        int n3 = n;
        int n4 = n2;
        if (n < 0) {
            n -= this.distance - 1;
        }

        if (n2 < 0) {
            n2 -= this.distance - 1;
        }

        int n5 = n / this.distance;
        int n6 = n2 / this.distance;
        Random setRandomSeed = this.worldObj.setRandomSeed(n5, n6, 10387312);
        n5 *= this.distance;
        n6 *= this.distance;
        n5 += setRandomSeed.nextInt(this.distance - this.intValue);
        n6 += setRandomSeed.nextInt(this.distance - this.intValue);
        return n3 == n5 && n4 == n6 ? this.worldObj.getWorldChunkManager().areBiomesViable(n3 * 16 + 8, n4 * 16 + 8, 0, villageSpawnBiomes) : false;
    }

    @Override
    public StructureStart getStructureStart(int n, int n2) {
        return new MapGenVillage.Start(this.worldObj, this.rand, n, n2, this.terrainType);
    }

    public static class Start extends StructureStart {
        public boolean hasMoreThanTwoComponents;

        public Start() {
        }

        public Start(World world, Random random, int n, int n2, int n3) {
            super(n, n2);
            List list = StructureVillagePieces.getStructureVillageWeightedPieceList(random, n3);
            net.minecraft.world.gen.structure.StructureVillagePieces.Start start = new net.minecraft.world.gen.structure.StructureVillagePieces.Start(
                world.getWorldChunkManager(), 0, random, (n << 4) + 2, (n2 << 4) + 2, list, n3
            );
            this.components.add(start);
            start.buildComponent(start, this.components, random);
            List list2 = start.list2;
            List list3 = start.list;

            while (!list2.isEmpty() || !list3.isEmpty()) {
                if (list2.isEmpty()) {
                    int nextInt = random.nextInt(list3.size());
                    StructureComponent structureComponent = (StructureComponent)list3.remove(nextInt);
                    structureComponent.buildComponent(start, this.components, random);
                } else {
                    int nextInt = random.nextInt(list2.size());
                    StructureComponent structureComponent = (StructureComponent)list2.remove(nextInt);
                    structureComponent.buildComponent(start, this.components, random);
                }
            }

            this.updateBoundingBox();
            int n4 = 0;

            for (StructureComponent structureComponent : (Iterable<StructureComponent>)this.components) {
                if (!(structureComponent instanceof Road)) {
                    n4++;
                }
            }

            this.hasMoreThanTwoComponents = n4 > 2;
        }

        @Override
        public boolean isSizeableStructure() {
            return this.hasMoreThanTwoComponents;
        }

        @Override
        public void serialize(NBTTagCompound nbttagCompound) {
            super.serialize(nbttagCompound);
            nbttagCompound.setBoolean("Valid", this.hasMoreThanTwoComponents);
        }

        @Override
        public void handleNBTTagCompound(NBTTagCompound nbttagCompound) {
            super.handleNBTTagCompound(nbttagCompound);
            this.hasMoreThanTwoComponents = nbttagCompound.getBoolean("Valid");
        }
    }
}
