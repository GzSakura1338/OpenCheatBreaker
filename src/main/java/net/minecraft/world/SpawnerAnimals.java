package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.optifine.BlockPosM;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;

public class SpawnerAnimals {
   public static int MOB_COUNT_DIV = (int)Math.pow(17.0, 2.0);
   public Set<ChunkCoordIntPair> eligibleChunksForSpawning = Sets.newHashSet();
   public Map<Class, EntityLiving> mapSampleEntitiesByClass = new HashMap<>();
   public int lastPlayerChunkX = Integer.MAX_VALUE;
   public int lastPlayerChunkZ = Integer.MAX_VALUE;
   public int countChunkPos;

   public int findChunksForSpawning(WorldServer worldServerIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean flag) {
      if (!spawnHostileMobs && !spawnPeacefulMobs) {
         return 0;
      }

      boolean flag2 = true;
      EntityPlayer entityPlayer = null;
      if (worldServerIn.playerEntities.size() == 1) {
         entityPlayer = worldServerIn.playerEntities.get(0);
         if (this.eligibleChunksForSpawning.size() > 0
            && entityPlayer != null
            && entityPlayer.chunkCoordX == this.lastPlayerChunkX
            && entityPlayer.chunkCoordZ == this.lastPlayerChunkZ) {
            flag2 = false;
         }
      }

      if (flag2) {
         this.eligibleChunksForSpawning.clear();
         int n = 0;

         for (EntityPlayer entityPlayer2 : worldServerIn.playerEntities) {
            if (!entityPlayer2.isSpectator()) {
               int n2 = MathHelper.floor_double(entityPlayer2.posX / 16.0);
               int n3 = MathHelper.floor_double(entityPlayer2.posZ / 16.0);
               byte b = 8;

               for (int i = -b; i <= b; i++) {
                  for (int j = -b; j <= b; j++) {
                     boolean flag3 = i == -b || i == b || j == -b || j == b;
                     ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(i + n2, j + n3);
                     if (!this.eligibleChunksForSpawning.contains(chunkCoordIntPair)) {
                        n++;
                        if (!flag3 && worldServerIn.getWorldBorder().contains(chunkCoordIntPair)) {
                           this.eligibleChunksForSpawning.add(chunkCoordIntPair);
                        }
                     }
                  }
               }
            }
         }

         this.countChunkPos = n;
         if (entityPlayer != null) {
            this.lastPlayerChunkX = entityPlayer.chunkCoordX;
            this.lastPlayerChunkZ = entityPlayer.chunkCoordZ;
         }
      }

      int n = 0;
      BlockPos spawnPoint = worldServerIn.getSpawnPoint();
      BlockPosM blockPosM = new BlockPosM(0, 0, 0);
      new MutableBlockPos();

      for (EnumCreatureType enumCreatureType : EnumCreatureType.values()) {
         if ((!enumCreatureType.getPeacefulCreature() || spawnPeacefulMobs) && (enumCreatureType.getPeacefulCreature() || spawnHostileMobs) && (!enumCreatureType.getAnimal() || flag)) {
            int n2 = Reflector.ForgeWorld_countEntities.exists()
               ? Reflector.callInt(worldServerIn, Reflector.ForgeWorld_countEntities, enumCreatureType, true)
               : worldServerIn.countEntities(enumCreatureType.getCreatureClass());
            int n3 = enumCreatureType.getMaxNumberOfCreature() * this.countChunkPos / MOB_COUNT_DIV;
            if (n2 <= n3) {
               Collection eligibleChunksForSpawning = this.eligibleChunksForSpawning;
               if (Reflector.ForgeHooksClient.exists()) {
                  ArrayList newArrayList = Lists.newArrayList(eligibleChunksForSpawning);
                  Collections.shuffle(newArrayList);
                  eligibleChunksForSpawning = newArrayList;
               }

               label166:
               for (ChunkCoordIntPair chunkCoordIntPair : (Iterable<ChunkCoordIntPair>)eligibleChunksForSpawning) {
                  BlockPosM randomChunkPosition = getRandomChunkPosition(worldServerIn, chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos, blockPosM);
                  int n4 = randomChunkPosition.getX();
                  int n5 = randomChunkPosition.getY();
                  int n6 = randomChunkPosition.getZ();
                  Block block = worldServerIn.getBlockState(randomChunkPosition).getBlock();
                  if (!block.isNormalCube()) {
                     int n7 = 0;

                     for (int i = 0; i < 3; i++) {
                        int n8 = n4;
                        int n9 = n5;
                        int n10 = n6;
                        byte b = 6;
                        SpawnListEntry spawnListEntry = null;
                        IEntityLivingData entityLivingData = null;

                        for (int j = 0; j < 4; j++) {
                           n8 += worldServerIn.rand.nextInt(b) - worldServerIn.rand.nextInt(b);
                           n9 += worldServerIn.rand.nextInt(1) - worldServerIn.rand.nextInt(1);
                           n10 += worldServerIn.rand.nextInt(b) - worldServerIn.rand.nextInt(b);
                           BlockPos blockPos = new BlockPos(n8, n9, n10);
                           float f = n8 + 0.5F;
                           float f2 = n10 + 0.5F;
                           if (!worldServerIn.isAnyPlayerWithinRangeAt(f, n9, f2, 24.0) && spawnPoint.distanceSq(f, n9, f2) >= 576.0) {
                              if (spawnListEntry == null) {
                                 spawnListEntry = worldServerIn.getSpawnListEntryForTypeAt(enumCreatureType, blockPos);
                                 if (spawnListEntry == null) {
                                    break;
                                 }
                              }

                              if (worldServerIn.canCreatureTypeSpawnHere(enumCreatureType, spawnListEntry, blockPos)
                                 && canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementForEntity(spawnListEntry.entityClass), worldServerIn, blockPos)
                                 )
                               {
                                 EntityLiving entityLiving;
                                 try {
                                    entityLiving = this.mapSampleEntitiesByClass.get(spawnListEntry.entityClass);
                                    if (entityLiving == null) {
                                       entityLiving = spawnListEntry.entityClass.getConstructor(World.class).newInstance(worldServerIn);
                                       this.mapSampleEntitiesByClass.put(spawnListEntry.entityClass, entityLiving);
                                    }
                                 } catch (Exception e) {
                                    e.printStackTrace();
                                    return n;
                                 }

                                 entityLiving.setLocationAndAngles(f, n9, f2, worldServerIn.rand.nextFloat() * 360.0F, 0.0F);
                                 boolean flag3 = Reflector.ForgeEventFactory_canEntitySpawn.exists()
                                    ? ReflectorForge.canEntitySpawn(entityLiving, worldServerIn, f, n9, f2)
                                    : entityLiving.getCanSpawnHere() && entityLiving.isNotColliding();
                                 if (flag3) {
                                    this.mapSampleEntitiesByClass.remove(spawnListEntry.entityClass);
                                    if (!ReflectorForge.doSpecialSpawn(entityLiving, worldServerIn, f, n9, f2)) {
                                       entityLivingData = entityLiving.onInitialSpawn(worldServerIn.getDifficultyForLocation(new BlockPos(entityLiving)), entityLivingData);
                                    }

                                    if (entityLiving.isNotColliding()) {
                                       n7++;
                                       worldServerIn.spawnEntityInWorld(entityLiving);
                                    }

                                    int n11 = Reflector.ForgeEventFactory_getMaxSpawnPackSize.exists()
                                       ? Reflector.callInt(Reflector.ForgeEventFactory_getMaxSpawnPackSize, entityLiving)
                                       : entityLiving.getMaxSpawnedInChunk();
                                    if (n7 >= n11) {
                                       continue label166;
                                    }
                                 }

                                 n += n7;
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

   public static BlockPos getRandomChunkPosition(World worldIn, int x, int z) {
      Chunk chunkFromChunkCoords = worldIn.getChunkFromChunkCoords(x, z);
      int n = x * 16 + worldIn.rand.nextInt(16);
      int n2 = z * 16 + worldIn.rand.nextInt(16);
      int roundUp = MathHelper.roundUp(chunkFromChunkCoords.getHeight(new BlockPos(n, 0, n2)) + 1, 16);
      int nextInt = worldIn.rand.nextInt(roundUp > 0 ? roundUp : chunkFromChunkCoords.getTopFilledSegment() + 16 - 1);
      return new BlockPos(n, nextInt, n2);
   }

   public static BlockPosM getRandomChunkPosition(World world, int n, int n2, BlockPosM blockPosM) {
      Chunk chunkFromChunkCoords = world.getChunkFromChunkCoords(n, n2);
      int n3 = n * 16 + world.rand.nextInt(16);
      int n4 = n2 * 16 + world.rand.nextInt(16);
      int roundUp = MathHelper.roundUp(chunkFromChunkCoords.getHeightValue(n3 & 15, n4 & 15) + 1, 16);
      int nextInt = world.rand.nextInt(roundUp > 0 ? roundUp : chunkFromChunkCoords.getTopFilledSegment() + 16 - 1);
      blockPosM.setXyz(n3, nextInt, n4);
      return blockPosM;
   }

   public static boolean canCreatureTypeSpawnAtLocation(SpawnPlacementType spawnPlacementTypeIn, World worldIn, BlockPos pos) {
      if (!worldIn.getWorldBorder().contains(pos)) {
         return false;
      }

      if (spawnPlacementTypeIn == null) {
         return false;
      }

      Block block = worldIn.getBlockState(pos).getBlock();
      if (spawnPlacementTypeIn == SpawnPlacementType.IN_WATER) {
         return block.getMaterial().isLiquid()
            && worldIn.getBlockState(pos.down()).getBlock().getMaterial().isLiquid()
            && !worldIn.getBlockState(pos.up()).getBlock().isNormalCube();
      }

      BlockPos down = pos.down();
      IBlockState blockState = worldIn.getBlockState(down);
      boolean flag = Reflector.ForgeBlock_canCreatureSpawn.exists()
         ? Reflector.callBoolean(blockState.getBlock(), Reflector.ForgeBlock_canCreatureSpawn, worldIn, down, spawnPlacementTypeIn)
         : World.doesBlockHaveSolidTopSurface(worldIn, down);
      if (!flag) {
         return false;
      }

      Block block2 = worldIn.getBlockState(down).getBlock();
      boolean flag2 = block2 != Blocks.bedrock && block2 != Blocks.barrier;
      return flag2 && !block.isNormalCube() && !block.getMaterial().isLiquid() && !worldIn.getBlockState(pos.up()).getBlock().isNormalCube();
   }

   public static void performWorldGenSpawning(World worldIn, BiomeGenBase biomeIn, int n, int n2, int n3, int n4, Random randomIn) {
      List spawnableList = biomeIn.getSpawnableList(EnumCreatureType.CREATURE);
      if (!spawnableList.isEmpty()) {
         while (randomIn.nextFloat() < biomeIn.getSpawningChance()) {
            SpawnListEntry randomItem = (SpawnListEntry)WeightedRandom.getRandomItem(worldIn.rand, spawnableList);
            int n5 = randomItem.minGroupCount + randomIn.nextInt(1 + randomItem.maxGroupCount - randomItem.minGroupCount);
            IEntityLivingData entityLivingData = null;
            int n6 = n + randomIn.nextInt(n3);
            int n7 = n2 + randomIn.nextInt(n4);
            int n8 = n6;
            int n9 = n7;

            for (int i = 0; i < n5; i++) {
               boolean flag = false;

               for (int j = 0; !flag && j < 4; j++) {
                  BlockPos topSolidOrLiquidBlock = worldIn.getTopSolidOrLiquidBlock(new BlockPos(n6, 0, n7));
                  if (canCreatureTypeSpawnAtLocation(SpawnPlacementType.ON_GROUND, worldIn, topSolidOrLiquidBlock)) {
                     EntityLiving entityLiving;
                     try {
                        entityLiving = randomItem.entityClass.getConstructor(World.class).newInstance(worldIn);
                     } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                     }

                     if (Reflector.ForgeEventFactory_canEntitySpawn.exists()) {
                        Object obj = Reflector.call(Reflector.ForgeEventFactory_canEntitySpawn, entityLiving, worldIn, n6 + 0.5F, topSolidOrLiquidBlock.getY(), n7 + 0.5F);
                        if (obj == ReflectorForge.EVENT_RESULT_DENY) {
                           continue;
                        }
                     }

                     entityLiving.setLocationAndAngles(n6 + 0.5F, topSolidOrLiquidBlock.getY(), n7 + 0.5F, randomIn.nextFloat() * 360.0F, 0.0F);
                     worldIn.spawnEntityInWorld(entityLiving);
                     entityLivingData = entityLiving.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityLiving)), entityLivingData);
                     flag = true;
                  }

                  n6 += randomIn.nextInt(5) - randomIn.nextInt(5);

                  for (n7 += randomIn.nextInt(5) - randomIn.nextInt(5);
                     n6 < n || n6 >= n + n3 || n7 < n2 || n7 >= n2 + n3;
                     n7 = n9 + randomIn.nextInt(5) - randomIn.nextInt(5)
                  ) {
                     n6 = n8 + randomIn.nextInt(5) - randomIn.nextInt(5);
                  }
               }
            }
         }
      }
   }
}
