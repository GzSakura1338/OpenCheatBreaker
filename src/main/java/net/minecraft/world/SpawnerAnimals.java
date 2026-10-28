package net.minecraft.world;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.optifine.Reflector;

public class SpawnerAnimals {
    public HashMap eligibleChunksForSpawning = new HashMap();
    public Map mapSampleEntitiesByClass = new HashMap();
    public int lastPlayerChunkX = Integer.MAX_VALUE;
    public int lastPlayerChunkZ = Integer.MAX_VALUE;

    public static ChunkPosition getRandomPosInChunk(World world, int n, int n2) {
        Chunk chunkFromChunkCoords = world.getChunkFromChunkCoords(n, n2);
        int n3 = n * 16 + world.rand.nextInt(16);
        int n4 = n2 * 16 + world.rand.nextInt(16);
        int nextInt = world.rand.nextInt(chunkFromChunkCoords == null ? world.getActualHeight() : chunkFromChunkCoords.getTopFilledSegment() + 16 - 1);
        return new ChunkPosition(n3, nextInt, n4);
    }

    public int findChunksForSpawning(WorldServer worldServer, boolean flag, boolean flag2, boolean flag3) {
        if (!flag && !flag2) {
            return 0;
        }

        EntityPlayer entityPlayer = null;
        if (worldServer.playerEntities.size() == 1) {
            entityPlayer = worldServer.playerEntities.get(0);
        }

        if (entityPlayer == null || entityPlayer.chunkCoordX != this.lastPlayerChunkX || entityPlayer.chunkCoordZ != this.lastPlayerChunkZ || this.eligibleChunksForSpawning.size() <= 0) {
            this.eligibleChunksForSpawning.clear();

            for (int i = 0; i < worldServer.playerEntities.size(); i++) {
                EntityPlayer entityPlayer2 = worldServer.playerEntities.get(i);
                int n = MathHelper.floor_double(entityPlayer2.posX / 16.0);
                int n2 = MathHelper.floor_double(entityPlayer2.posZ / 16.0);
                byte b = 8;

                for (int j = -b; j <= b; j++) {
                    for (int k = -b; k <= b; k++) {
                        boolean flag4 = j == -b || j == b || k == -b || k == b;
                        ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(j + n, k + n2);
                        if (!flag4) {
                            this.eligibleChunksForSpawning.put(chunkCoordIntPair, false);
                        } else if (!this.eligibleChunksForSpawning.containsKey(chunkCoordIntPair)) {
                            this.eligibleChunksForSpawning.put(chunkCoordIntPair, true);
                        }
                    }
                }
            }

            if (entityPlayer != null) {
                this.lastPlayerChunkX = entityPlayer.chunkCoordX;
                this.lastPlayerChunkZ = entityPlayer.chunkCoordZ;
            }
        }

        int n = 0;
        ChunkCoordinates spawnPoint = worldServer.getSpawnPoint();

        for (EnumCreatureType enumCreatureType : EnumCreatureType.values()) {
            boolean flag4 = false;
            int n2;
            if (Reflector.ForgeWorld_countEntities.exists()) {
                n2 = Reflector.callInt(worldServer, Reflector.ForgeWorld_countEntities, enumCreatureType, true);
            } else {
                n2 = worldServer.countEntities(enumCreatureType.getCreatureClass());
            }

            if ((!enumCreatureType.getPeacefulCreature() || flag2)
                && (enumCreatureType.getPeacefulCreature() || flag)
                && (!enumCreatureType.getAnimal() || flag3)
                && n2 <= enumCreatureType.getMaxNumberOfCreature() * this.eligibleChunksForSpawning.size() / 256) {
                label164:
                for (ChunkCoordIntPair chunkCoordIntPair : (Iterable<ChunkCoordIntPair>)this.eligibleChunksForSpawning.keySet()) {
                    if (!(Boolean)this.eligibleChunksForSpawning.get(chunkCoordIntPair)) {
                        Chunk chunkFromChunkCoords = worldServer.getChunkFromChunkCoords(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos);
                        int n3 = chunkCoordIntPair.chunkXPos * 16 + worldServer.rand.nextInt(16);
                        int n4 = chunkCoordIntPair.chunkZPos * 16 + worldServer.rand.nextInt(16);
                        int nextInt = worldServer.rand.nextInt(chunkFromChunkCoords == null ? worldServer.getActualHeight() : chunkFromChunkCoords.getTopFilledSegment() + 16 - 1);
                        if (!worldServer.getBlock(n3, nextInt, n4).isNormalCube() && worldServer.getBlock(n3, nextInt, n4).getMaterial() == enumCreatureType.getCreatureMaterial()) {
                            int n5 = 0;

                            for (int i = 0; i < 3; i++) {
                                int n6 = n3;
                                int n7 = nextInt;
                                int n8 = n4;
                                byte b = 6;
                                SpawnListEntry spawnListEntry = null;
                                IEntityLivingData entityLivingData = null;

                                for (int j = 0; j < 4; j++) {
                                    n6 += worldServer.rand.nextInt(b) - worldServer.rand.nextInt(b);
                                    n7 += worldServer.rand.nextInt(1) - worldServer.rand.nextInt(1);
                                    n8 += worldServer.rand.nextInt(b) - worldServer.rand.nextInt(b);
                                    if (canCreatureTypeSpawnAtLocation(enumCreatureType, worldServer, n6, n7, n8)) {
                                        float f = n6 + 0.5F;
                                        float f2 = n7;
                                        float f3 = n8 + 0.5F;
                                        if (worldServer.getClosestPlayer(f, f2, f3, 24.0) == null) {
                                            float f4 = f - spawnPoint.posX;
                                            float f5 = f2 - spawnPoint.posY;
                                            float f6 = f3 - spawnPoint.posZ;
                                            float f7 = f4 * f4 + f5 * f5 + f6 * f6;
                                            if (f7 >= 576.0F) {
                                                if (spawnListEntry == null) {
                                                    spawnListEntry = worldServer.spawnRandomCreature(enumCreatureType, n6, n7, n8);
                                                    if (spawnListEntry == null) {
                                                        break;
                                                    }
                                                }

                                                EntityLiving entityLiving;
                                                try {
                                                    entityLiving = (EntityLiving)this.mapSampleEntitiesByClass.get(spawnListEntry.entityClass);
                                                    if (entityLiving == null) {
                                                        entityLiving = (EntityLiving)spawnListEntry.entityClass.getConstructor(World.class).newInstance(worldServer);
                                                        this.mapSampleEntitiesByClass.put(spawnListEntry.entityClass, entityLiving);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    return n;
                                                }

                                                entityLiving.setLocationAndAngles(f, f2, f3, worldServer.rand.nextFloat() * 360.0F, 0.0F);
                                                boolean flag5 = false;
                                                if (!Reflector.ForgeEventFactory_canEntitySpawn.exists()) {
                                                    flag5 = entityLiving.getCanSpawnHere();
                                                } else {
                                                    Object obj = Reflector.call(Reflector.ForgeEventFactory_canEntitySpawn, entityLiving, worldServer, f, f2, f3);
                                                    Object fieldValue = Reflector.getFieldValue(Reflector.Event_Result_ALLOW);
                                                    Object fieldValue2 = Reflector.getFieldValue(Reflector.Event_Result_DEFAULT);
                                                    flag5 = obj == fieldValue || obj == fieldValue2 && entityLiving.getCanSpawnHere();
                                                }

                                                if (flag5) {
                                                    this.mapSampleEntitiesByClass.put(spawnListEntry.entityClass, null);
                                                    n5++;
                                                    worldServer.spawnEntityInWorld(entityLiving);
                                                    entityLivingData = entityLiving.onSpawnWithEgg(entityLivingData);
                                                    if (n5 >= entityLiving.getMaxSpawnedInChunk()) {
                                                        continue label164;
                                                    }
                                                }

                                                n += n5;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return n;
    }

    public static boolean canCreatureTypeSpawnAtLocation(EnumCreatureType enumCreatureType, World world, int n, int n2, int n3) {
        if (enumCreatureType.getCreatureMaterial() == Material.water) {
            return world.getBlock(n, n2, n3).getMaterial().isLiquid()
                && world.getBlock(n, n2 - 1, n3).getMaterial().isLiquid()
                && !world.getBlock(n, n2 + 1, n3).isNormalCube();
        }

        if (!World.doesBlockHaveSolidTopSurface(world, n, n2 - 1, n3)) {
            return false;
        }

        Block block = world.getBlock(n, n2 - 1, n3);
        return Reflector.ForgeBlock_canCreatureSpawn.exists()
                && !Reflector.callBoolean(block, Reflector.ForgeBlock_canCreatureSpawn, enumCreatureType, world, n, n2, n3)
            ? false
            : block != Blocks.bedrock
                && !world.getBlock(n, n2, n3).isNormalCube()
                && !world.getBlock(n, n2, n3).getMaterial().isLiquid()
                && !world.getBlock(n, n2 + 1, n3).isNormalCube();
    }

    public static void performWorldGenSpawning(World world, BiomeGenBase biomeGenBase, int n, int n2, int n3, int n4, Random random) {
        List spawnableList = biomeGenBase.getSpawnableList(EnumCreatureType.creature);
        if (!spawnableList.isEmpty()) {
            while (random.nextFloat() < biomeGenBase.getSpawningChance()) {
                SpawnListEntry randomItem = (SpawnListEntry)WeightedRandom.getRandomItem(world.rand, spawnableList);
                IEntityLivingData entityLivingData = null;
                int n5 = randomItem.minGroupCount + random.nextInt(1 + randomItem.maxGroupCount - randomItem.minGroupCount);
                int n6 = n + random.nextInt(n3);
                int n7 = n2 + random.nextInt(n4);
                int n8 = n6;
                int n9 = n7;

                for (int i = 0; i < n5; i++) {
                    boolean flag = false;

                    for (int j = 0; !flag && j < 4; j++) {
                        int topSolidOrLiquidBlock = world.getTopSolidOrLiquidBlock(n6, n7);
                        if (canCreatureTypeSpawnAtLocation(EnumCreatureType.creature, world, n6, topSolidOrLiquidBlock, n7)) {
                            float f = n6 + 0.5F;
                            float f2 = topSolidOrLiquidBlock;
                            float f3 = n7 + 0.5F;

                            EntityLiving entityLiving;
                            try {
                                entityLiving = (EntityLiving)randomItem.entityClass.getConstructor(World.class).newInstance(world);
                            } catch (Exception e) {
                                e.printStackTrace();
                                continue;
                            }

                            entityLiving.setLocationAndAngles(f, f2, f3, random.nextFloat() * 360.0F, 0.0F);
                            world.spawnEntityInWorld(entityLiving);
                            entityLivingData = entityLiving.onSpawnWithEgg(entityLivingData);
                            flag = true;
                        }

                        n6 += random.nextInt(5) - random.nextInt(5);

                        for (n7 += random.nextInt(5) - random.nextInt(5);
                            n6 < n || n6 >= n + n3 || n7 < n2 || n7 >= n2 + n3;
                            n7 = n9 + random.nextInt(5) - random.nextInt(5)
                        ) {
                            n6 = n8 + random.nextInt(5) - random.nextInt(5);
                        }
                    }
                }
            }
        }
    }
}
