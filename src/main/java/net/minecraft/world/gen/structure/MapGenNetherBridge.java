package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;

public class MapGenNetherBridge extends MapGenStructure {
    public List spawnList = new ArrayList();

    public MapGenNetherBridge() {
        this.spawnList.add(new SpawnListEntry(EntityBlaze.class, 10, 2, 3));
        this.spawnList.add(new SpawnListEntry(EntityPigZombie.class, 5, 4, 4));
        this.spawnList.add(new SpawnListEntry(EntitySkeleton.class, 10, 4, 4));
        this.spawnList.add(new SpawnListEntry(EntityMagmaCube.class, 3, 4, 4));
    }

    @Override
    public String getStructureName() {
        return "Fortress";
    }

    public List getSpawnList() {
        return this.spawnList;
    }

    @Override
    public boolean canSpawnStructureAtCoords(int n, int n2) {
        int n3 = n >> 4;
        int n4 = n2 >> 4;
        this.rand.setSeed(n3 ^ n4 << 4 ^ this.worldObj.getSeed());
        this.rand.nextInt();
        return this.rand.nextInt(3) == 0 && n == (n3 << 4) + 4 + this.rand.nextInt(8) && n2 == (n4 << 4) + 4 + this.rand.nextInt(8);
    }

    @Override
    public StructureStart getStructureStart(int n, int n2) {
        return new MapGenNetherBridge.Start(this.worldObj, this.rand, n, n2);
    }

    public static class Start extends StructureStart {
        public Start() {
        }

        public Start(World world, Random random, int n, int n2) {
            super(n, n2);
            net.minecraft.world.gen.structure.StructureNetherBridgePieces.Start start = new net.minecraft.world.gen.structure.StructureNetherBridgePieces.Start(
                random, (n << 4) + 2, (n2 << 4) + 2
            );
            this.components.add(start);
            start.buildComponent(start, this.components, random);
            ArrayList list = start.list;

            while (!list.isEmpty()) {
                int nextInt = random.nextInt(list.size());
                StructureComponent structureComponent = (StructureComponent)list.remove(nextInt);
                structureComponent.buildComponent(start, this.components, random);
            }

            this.updateBoundingBox();
            this.setRandomHeight(world, random, 48, 70);
        }
    }
}
