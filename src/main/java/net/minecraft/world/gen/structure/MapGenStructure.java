package net.minecraft.world.gen.structure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;

public abstract class MapGenStructure extends MapGenBase {
    public MapGenStructureData mapGenStructureData;
    public Map structureMap = new HashMap();

    public abstract String getStructureName();

    @Override
    public void handleWorld(World worldIn, final int n, final int n2, int n3, int n4, Block[] blocks) {
        this.handleWorld(worldIn);
        if (!this.structureMap.containsKey(ChunkCoordIntPair.chunkXZ2Int(n, n2))) {
            this.rand.nextInt();

            try {
                if (this.canSpawnStructureAtCoords(n, n2)) {
                    StructureStart structureStart = this.getStructureStart(n, n2);
                    this.structureMap.put(ChunkCoordIntPair.chunkXZ2Int(n, n2), structureStart);
                    this.process(n, n2, structureStart);
                }
            } catch (Throwable e) {
                CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Exception preparing structure feature");
                CrashReportCategory makeCategory = makeCrashReport.makeCategory("Feature being prepared");
                makeCategory.addCrashSectionCallable("Is feature chunk", new Callable() {
                    public String call() {
                        return MapGenStructure.this.canSpawnStructureAtCoords(n, n2) ? "True" : "False";
                    }
                });
                makeCategory.addCrashSection("Chunk location", String.format("%d,%d", n, n2));
                makeCategory.addCrashSectionCallable("Chunk pos hash", new Callable() {
                    public String call() {
                        return String.valueOf(ChunkCoordIntPair.chunkXZ2Int(n, n2));
                    }
                });
                makeCategory.addCrashSectionCallable("Structure type", new Callable() {
                    public String call() {
                        return MapGenStructure.this.getClass().getCanonicalName();
                    }
                });
                throw new ReportedException(makeCrashReport);
            }
        }
    }

    public boolean generateStructuresInChunk(World world, Random random, int n, int n2) {
        this.handleWorld(world);
        int n3 = (n << 4) + 8;
        int n4 = (n2 << 4) + 8;
        boolean flag = false;

        for (StructureStart structureStart : (Iterable<StructureStart>)this.structureMap.values()) {
            if (structureStart.isSizeableStructure() && structureStart.getBoundingBox().intersectsWith(n3, n4, n3 + 15, n4 + 15)) {
                structureStart.generateStructure(world, random, new StructureBoundingBox(n3, n4, n3 + 15, n4 + 15));
                flag = true;
                this.process(structureStart.getChunkX(), structureStart.getChunkZ(), structureStart);
            }
        }

        return flag;
    }

    public boolean hasStructureAt(int n, int n2, int n3) {
        this.handleWorld(this.worldObj);
        return this.findStructureStart(n, n2, n3) != null;
    }

    public StructureStart findStructureStart(int n, int n2, int n3) {
        for (StructureStart structureStart : (Iterable<StructureStart>)this.structureMap.values()) {
            if (structureStart.isSizeableStructure() && structureStart.getBoundingBox().intersectsWith(n, n3, n, n3)) {
                for (StructureComponent structureComponent : (Iterable<StructureComponent>)structureStart.getComponents()) {
                    if (structureComponent.getBoundingBox().isVecInside(n, n2, n3)) {
                        return structureStart;
                    }
                }
            }
        }

        return null;
    }

    public boolean check(int n, int n2, int n3) {
        this.handleWorld(this.worldObj);

        for (StructureStart structureStart : (Iterable<StructureStart>)this.structureMap.values()) {
            if (structureStart.isSizeableStructure()) {
                return structureStart.getBoundingBox().intersectsWith(n, n3, n, n3);
            }
        }

        return false;
    }

    public ChunkPosition getNearestInstance(World world, int n, int n2, int n3) {
        this.worldObj = world;
        this.handleWorld(world);
        this.rand.setSeed(world.getSeed());
        long nextLong = this.rand.nextLong();
        long nextLong2 = this.rand.nextLong();
        long l = (n >> 4) * nextLong;
        long l2 = (n3 >> 4) * nextLong2;
        this.rand.setSeed(l ^ l2 ^ world.getSeed());
        this.handleWorld(world, n >> 4, n3 >> 4, 0, 0, null);
        double d = Double.MAX_VALUE;
        ChunkPosition chunkPosition = null;

        for (StructureStart structureStart : (Iterable<StructureStart>)this.structureMap.values()) {
            if (structureStart.isSizeableStructure()) {
                StructureComponent structureComponent = (StructureComponent)structureStart.getComponents().get(0);
                ChunkPosition createChunkPosition = structureComponent.createChunkPosition();
                int n4 = createChunkPosition.chunkPosX - n;
                int n5 = createChunkPosition.chunkPosY - n2;
                int n6 = createChunkPosition.chunkPosZ - n3;
                double d2 = n4 * n4 + n5 * n5 + n6 * n6;
                if (d2 < d) {
                    d = d2;
                    chunkPosition = createChunkPosition;
                }
            }
        }

        if (chunkPosition != null) {
            return chunkPosition;
        }

        List coordList = this.getCoordList();
        if (coordList != null) {
            ChunkPosition chunkPosition2 = null;

            for (ChunkPosition chunkPosition3 : (Iterable<ChunkPosition>)coordList) {
                int n4 = chunkPosition3.chunkPosX - n;
                int n5 = chunkPosition3.chunkPosY - n2;
                int n6 = chunkPosition3.chunkPosZ - n3;
                double d2 = n4 * n4 + n5 * n5 + n6 * n6;
                if (d2 < d) {
                    d = d2;
                    chunkPosition2 = chunkPosition3;
                }
            }

            return chunkPosition2;
        } else {
            return null;
        }
    }

    public List getCoordList() {
        return null;
    }

    public void handleWorld(World world) {
        if (this.mapGenStructureData == null) {
            this.mapGenStructureData = (MapGenStructureData)world.loadItemData(MapGenStructureData.class, this.getStructureName());
            if (this.mapGenStructureData == null) {
                this.mapGenStructureData = new MapGenStructureData(this.getStructureName());
                world.setItemData(this.getStructureName(), this.mapGenStructureData);
            } else {
                NBTTagCompound features = this.mapGenStructureData.getFeatures();

                for (String str : (Iterable<String>)features.getKeySet()) {
                    NBTBase tag = features.getTag(str);
                    if (tag.getId() == 10) {
                        NBTTagCompound nbttagCompound = (NBTTagCompound)tag;
                        if (nbttagCompound.hasKey("ChunkX") && nbttagCompound.hasKey("ChunkZ")) {
                            int integer = nbttagCompound.getInteger("ChunkX");
                            int integer2 = nbttagCompound.getInteger("ChunkZ");
                            StructureStart generatorConfigFromNbt = MapGenStructureIO.getGeneratorConfigFromNbt(nbttagCompound, world);
                            if (generatorConfigFromNbt != null) {
                                this.structureMap.put(ChunkCoordIntPair.chunkXZ2Int(integer, integer2), generatorConfigFromNbt);
                            }
                        }
                    }
                }
            }
        }
    }

    public void process(int n, int n2, StructureStart structureStart) {
        this.mapGenStructureData.putFeature(structureStart.toNbt(n, n2), n, n2);
        this.mapGenStructureData.markDirty();
    }

    public abstract boolean canSpawnStructureAtCoords(int n, int n2);

    public abstract StructureStart getStructureStart(int n, int n2);
}
