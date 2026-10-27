package net.minecraft.server.management;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.optifine.ChunkPosComparator;
import net.optifine.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerManager {
   public static Logger pmLogger = LogManager.getLogger();
   public List<EntityPlayerMP> players = Lists.newArrayList();
   public LongHashMap<PlayerManager.PlayerInstance> playerInstances = new LongHashMap<>();
   public List<PlayerManager.PlayerInstance> playerInstancesToUpdate = Lists.newArrayList();
   public List<PlayerManager.PlayerInstance> playerInstanceList = Lists.newArrayList();
   public int[][] xzDirectionsConst = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
   public int playerViewRadius;
   public long previousTotalWorldTime;
   public Map<EntityPlayerMP, Set<ChunkCoordIntPair>> mapPlayerPendingEntries = new HashMap<>();
   public WorldServer theWorldServer;

   public PlayerManager(WorldServer serverWorld) {
      this.theWorldServer = serverWorld;
      this.setPlayerViewRadius(serverWorld.getMinecraftServer().getConfigurationManager().getViewDistance());
   }

   public WorldServer getWorldServer() {
      return this.theWorldServer;
   }

   public void updatePlayerInstances() {
      Set entrySet = this.mapPlayerPendingEntries.entrySet();
      Iterator iterator = entrySet.iterator();

      while (iterator.hasNext()) {
         Entry entry = (Entry)iterator.next();
         Set value = (Set)entry.getValue();
         if (!value.isEmpty()) {
            EntityPlayerMP key = (EntityPlayerMP)entry.getKey();
            if (key.worldObj != this.theWorldServer) {
               iterator.remove();
            } else {
               int n = this.playerViewRadius / 3 + 1;
               if (!Config.isLazyChunkLoading()) {
                  n = this.playerViewRadius * 2 + 1;
               }

               for (ChunkCoordIntPair chunkCoordIntPair : (Iterable<ChunkCoordIntPair>)this.getNearest(value, key, n)) {
                  PlayerManager.PlayerInstance playerInstance = this.getPlayerInstance(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos, true);
                  playerInstance.addPlayer(key);
                  value.remove(chunkCoordIntPair);
               }
            }
         }
      }

      long totalWorldTime = this.theWorldServer.getTotalWorldTime();
      if (totalWorldTime - this.previousTotalWorldTime > 8000L) {
         this.previousTotalWorldTime = totalWorldTime;

         for (int i = 0; i < this.playerInstanceList.size(); i++) {
            PlayerManager.PlayerInstance playerInstance = this.playerInstanceList.get(i);
            playerInstance.onUpdate();
            playerInstance.processChunk();
         }
      } else {
         for (int i = 0; i < this.playerInstancesToUpdate.size(); i++) {
            PlayerManager.PlayerInstance playerInstance = this.playerInstancesToUpdate.get(i);
            playerInstance.onUpdate();
         }
      }

      this.playerInstancesToUpdate.clear();
      if (this.players.isEmpty()) {
         WorldProvider provider = this.theWorldServer.provider;
         if (!provider.canRespawnHere()) {
            this.theWorldServer.theChunkProviderServer.unloadAllChunks();
         }
      }
   }

   public boolean hasPlayerInstance(int chunkX, int chunkZ) {
      long l = chunkX + 2147483647L | chunkZ + 2147483647L << 32;
      return this.playerInstances.getValueByKey(l) != null;
   }

   public PlayerManager.PlayerInstance getPlayerInstance(int chunkX, int chunkZ, boolean createIfAbsent) {
      long l = chunkX + 2147483647L | chunkZ + 2147483647L << 32;
      PlayerManager.PlayerInstance valueByKey = this.playerInstances.getValueByKey(l);
      if (valueByKey == null && createIfAbsent) {
         valueByKey = new PlayerManager.PlayerInstance(chunkX, chunkZ);
         this.playerInstances.add(l, valueByKey);
         this.playerInstanceList.add(valueByKey);
      }

      return valueByKey;
   }

   public void markBlockForUpdate(BlockPos pos) {
      int n = pos.getX() >> 4;
      int n2 = pos.getZ() >> 4;
      PlayerManager.PlayerInstance playerInstance = this.getPlayerInstance(n, n2, false);
      if (playerInstance != null) {
         playerInstance.flagChunkForUpdate(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
      }
   }

   public void addPlayer(EntityPlayerMP player) {
      int n = (int)player.posX >> 4;
      int n2 = (int)player.posZ >> 4;
      player.managedPosX = player.posX;
      player.managedPosZ = player.posZ;
      int min = Math.min(this.playerViewRadius, 8);
      int n3 = n - min;
      int n4 = n + min;
      int n5 = n2 - min;
      int n6 = n2 + min;
      Set pendingEntriesSafe = this.getPendingEntriesSafe(player);

      for (int i = n - this.playerViewRadius; i <= n + this.playerViewRadius; i++) {
         for (int j = n2 - this.playerViewRadius; j <= n2 + this.playerViewRadius; j++) {
            if (i >= n3 && i <= n4 && j >= n5 && j <= n6) {
               this.getPlayerInstance(i, j, true).addPlayer(player);
            } else {
               pendingEntriesSafe.add(new ChunkCoordIntPair(i, j));
            }
         }
      }

      this.players.add(player);
      this.filterChunkLoadQueue(player);
   }

   public void filterChunkLoadQueue(EntityPlayerMP player) {
      ArrayList newArrayList = Lists.newArrayList(player.loadedChunks);
      int n = 0;
      int playerViewRadius = this.playerViewRadius;
      int n2 = (int)player.posX >> 4;
      int n3 = (int)player.posZ >> 4;
      int n4 = 0;
      int n5 = 0;
      ChunkCoordIntPair chunkCoords = this.getPlayerInstance(n2, n3, true).chunkCoords;
      player.loadedChunks.clear();
      if (newArrayList.contains(chunkCoords)) {
         player.loadedChunks.add(chunkCoords);
      }

      for (int i = 1; i <= playerViewRadius * 2; i++) {
         for (int j = 0; j < 2; j++) {
            int[] ints = this.xzDirectionsConst[n++ % 4];

            for (int k = 0; k < i; k++) {
               n4 += ints[0];
               n5 += ints[1];
               chunkCoords = this.getPlayerInstance(n2 + n4, n3 + n5, true).chunkCoords;
               if (newArrayList.contains(chunkCoords)) {
                  player.loadedChunks.add(chunkCoords);
               }
            }
         }
      }

      n %= 4;

      for (int i = 0; i < playerViewRadius * 2; i++) {
         n4 += this.xzDirectionsConst[n][0];
         n5 += this.xzDirectionsConst[n][1];
         chunkCoords = this.getPlayerInstance(n2 + n4, n3 + n5, true).chunkCoords;
         if (newArrayList.contains(chunkCoords)) {
            player.loadedChunks.add(chunkCoords);
         }
      }
   }

   public void removePlayer(EntityPlayerMP player) {
      this.mapPlayerPendingEntries.remove(player);
      int n = (int)player.managedPosX >> 4;
      int n2 = (int)player.managedPosZ >> 4;

      for (int i = n - this.playerViewRadius; i <= n + this.playerViewRadius; i++) {
         for (int j = n2 - this.playerViewRadius; j <= n2 + this.playerViewRadius; j++) {
            PlayerManager.PlayerInstance playerInstance = this.getPlayerInstance(i, j, false);
            if (playerInstance != null) {
               playerInstance.removePlayer(player);
            }
         }
      }

      this.players.remove(player);
   }

   public boolean overlaps(int x1, int z1, int x2, int z2, int radius) {
      int n = x1 - x2;
      int n2 = z1 - z2;
      return n >= -radius && n <= radius ? n2 >= -radius && n2 <= radius : false;
   }

   public void updateMountedMovingPlayer(EntityPlayerMP player) {
      int n = (int)player.posX >> 4;
      int n2 = (int)player.posZ >> 4;
      double d = player.managedPosX - player.posX;
      double d2 = player.managedPosZ - player.posZ;
      double d3 = d * d + d2 * d2;
      if (d3 >= 64.0) {
         int n3 = (int)player.managedPosX >> 4;
         int n4 = (int)player.managedPosZ >> 4;
         int playerViewRadius = this.playerViewRadius;
         int n5 = n - n3;
         int n6 = n2 - n4;
         if (n5 != 0 || n6 != 0) {
            Set pendingEntriesSafe = this.getPendingEntriesSafe(player);

            for (int i = n - playerViewRadius; i <= n + playerViewRadius; i++) {
               for (int j = n2 - playerViewRadius; j <= n2 + playerViewRadius; j++) {
                  if (!this.overlaps(i, j, n3, n4, playerViewRadius)) {
                     if (Config.isLazyChunkLoading()) {
                        pendingEntriesSafe.add(new ChunkCoordIntPair(i, j));
                     } else {
                        this.getPlayerInstance(i, j, true).addPlayer(player);
                     }
                  }

                  if (!this.overlaps(i - n5, j - n6, n, n2, playerViewRadius)) {
                     pendingEntriesSafe.remove(new ChunkCoordIntPair(i - n5, j - n6));
                     PlayerManager.PlayerInstance playerInstance = this.getPlayerInstance(i - n5, j - n6, false);
                     if (playerInstance != null) {
                        playerInstance.removePlayer(player);
                     }
                  }
               }
            }

            this.filterChunkLoadQueue(player);
            player.managedPosX = player.posX;
            player.managedPosZ = player.posZ;
         }
      }
   }

   public boolean isPlayerWatchingChunk(EntityPlayerMP player, int chunkX, int chunkZ) {
      PlayerManager.PlayerInstance playerInstance = this.getPlayerInstance(chunkX, chunkZ, false);
      return playerInstance != null && playerInstance.playersWatchingChunk.contains(player) && !player.loadedChunks.contains(playerInstance.chunkCoords);
   }

   public void setPlayerViewRadius(int radius) {
      radius = MathHelper.clamp_int(radius, 3, 64);
      if (radius != this.playerViewRadius) {
         int n = radius - this.playerViewRadius;

         for (EntityPlayerMP entityPlayerMP : Lists.newArrayList(this.players)) {
            int n2 = (int)entityPlayerMP.posX >> 4;
            int n3 = (int)entityPlayerMP.posZ >> 4;
            Set pendingEntriesSafe = this.getPendingEntriesSafe(entityPlayerMP);
            if (n > 0) {
               for (int i = n2 - radius; i <= n2 + radius; i++) {
                  for (int j = n3 - radius; j <= n3 + radius; j++) {
                     if (Config.isLazyChunkLoading()) {
                        pendingEntriesSafe.add(new ChunkCoordIntPair(i, j));
                     } else {
                        PlayerManager.PlayerInstance playerInstance = this.getPlayerInstance(i, j, true);
                        if (!playerInstance.playersWatchingChunk.contains(entityPlayerMP)) {
                           playerInstance.addPlayer(entityPlayerMP);
                        }
                     }
                  }
               }
            } else {
               for (int i = n2 - this.playerViewRadius; i <= n2 + this.playerViewRadius; i++) {
                  for (int j = n3 - this.playerViewRadius; j <= n3 + this.playerViewRadius; j++) {
                     if (!this.overlaps(i, j, n2, n3, radius)) {
                        pendingEntriesSafe.remove(new ChunkCoordIntPair(i, j));
                        PlayerManager.PlayerInstance playerInstance = this.getPlayerInstance(i, j, true);
                        if (playerInstance != null) {
                           playerInstance.removePlayer(entityPlayerMP);
                        }
                     }
                  }
               }
            }
         }

         this.playerViewRadius = radius;
      }
   }

   public static int getFurthestViewableBlock(int distance) {
      return distance * 16 - 16;
   }

   public PriorityQueue<ChunkCoordIntPair> getNearest(Set<ChunkCoordIntPair> set, EntityPlayerMP entityPlayerMP, int n) {
      float f = entityPlayerMP.rotationYaw + 90.0F;

      while (f <= -180.0F) {
         f += 360.0F;
      }

      while (f > 180.0F) {
         f -= 360.0F;
      }

      double d = f * (Math.PI / 180.0);
      double rotationPitch = entityPlayerMP.rotationPitch;
      double d2 = rotationPitch * (Math.PI / 180.0);
      ChunkPosComparator chunkPosComparator = new ChunkPosComparator(entityPlayerMP.chunkCoordX, entityPlayerMP.chunkCoordZ, d, d2);
      Comparator reverseOrder = Collections.reverseOrder(chunkPosComparator);
      PriorityQueue priorityQueue = new PriorityQueue(n, reverseOrder);

      for (ChunkCoordIntPair chunkCoordIntPair : set) {
         if (priorityQueue.size() < n) {
            priorityQueue.add(chunkCoordIntPair);
         } else {
            ChunkCoordIntPair peek = (ChunkCoordIntPair)priorityQueue.peek();
            if (chunkPosComparator.compare(chunkCoordIntPair, peek) < 0) {
               priorityQueue.remove();
               priorityQueue.add(chunkCoordIntPair);
            }
         }
      }

      return priorityQueue;
   }

   public Set<ChunkCoordIntPair> getPendingEntriesSafe(EntityPlayerMP entityPlayerMP) {
      Set set = this.mapPlayerPendingEntries.get(entityPlayerMP);
      if (set != null) {
         return set;
      }

      int min = Math.min(this.playerViewRadius, 8);
      int n = this.playerViewRadius * 2 + 1;
      int n2 = min * 2 + 1;
      int n3 = n * n - n2 * n2;
      n3 = Math.max(n3, 16);
      HashSet set2 = new HashSet(n3);
      this.mapPlayerPendingEntries.put(entityPlayerMP, set2);
      return set2;
   }

   class PlayerInstance {
      public List<EntityPlayerMP> playersWatchingChunk = Lists.newArrayList();
      public short[] locationOfBlockChange = new short[64];
      public ChunkCoordIntPair chunkCoords;
      public int numBlocksToUpdate;
      public int flagsYAreasToUpdate;
      public long previousWorldTime;

      public PlayerInstance(int chunkX, int chunkZ) {
         this.chunkCoords = new ChunkCoordIntPair(chunkX, chunkZ);
         PlayerManager.this.getWorldServer().theChunkProviderServer.loadChunk(chunkX, chunkZ);
      }

      public void addPlayer(EntityPlayerMP player) {
         if (this.playersWatchingChunk.contains(player)) {
            PlayerManager.pmLogger
               .debug("Failed to add player. {} already is in chunk {}, {}", new Object[]{player, this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos});
         } else {
            if (this.playersWatchingChunk.isEmpty()) {
               this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
            }

            this.playersWatchingChunk.add(player);
            player.loadedChunks.add(this.chunkCoords);
         }
      }

      public void removePlayer(EntityPlayerMP player) {
         if (this.playersWatchingChunk.contains(player)) {
            Chunk chunkFromChunkCoords = PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos);
            if (chunkFromChunkCoords.isPopulated()) {
               player.playerNetServerHandler.sendPacket(new S21PacketChunkData(chunkFromChunkCoords, true, 0));
            }

            this.playersWatchingChunk.remove(player);
            player.loadedChunks.remove(this.chunkCoords);
            if (this.playersWatchingChunk.isEmpty()) {
               long l = this.chunkCoords.chunkXPos + 2147483647L | this.chunkCoords.chunkZPos + 2147483647L << 32;
               this.increaseInhabitedTime(chunkFromChunkCoords);
               PlayerManager.this.playerInstances.remove(l);
               PlayerManager.this.playerInstanceList.remove(this);
               if (this.numBlocksToUpdate > 0) {
                  PlayerManager.this.playerInstancesToUpdate.remove(this);
               }

               PlayerManager.this.getWorldServer().theChunkProviderServer.dropChunk(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos);
            }
         }
      }

      public void processChunk() {
         this.increaseInhabitedTime(PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos));
      }

      public void increaseInhabitedTime(Chunk theChunk) {
         theChunk.setInhabitedTime(theChunk.getInhabitedTime() + PlayerManager.this.theWorldServer.getTotalWorldTime() - this.previousWorldTime);
         this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
      }

      public void flagChunkForUpdate(int x, int y, int z) {
         if (this.numBlocksToUpdate == 0) {
            PlayerManager.this.playerInstancesToUpdate.add(this);
         }

         this.flagsYAreasToUpdate |= 1 << (y >> 4);
         if (this.numBlocksToUpdate < 64) {
            short s = (short)(x << 12 | z << 8 | y);

            for (int i = 0; i < this.numBlocksToUpdate; i++) {
               if (this.locationOfBlockChange[i] == s) {
                  return;
               }
            }

            this.locationOfBlockChange[this.numBlocksToUpdate++] = s;
         }
      }

      public void sendToAllPlayersWatchingChunk(Packet thePacket) {
         for (int i = 0; i < this.playersWatchingChunk.size(); i++) {
            EntityPlayerMP entityPlayerMP = this.playersWatchingChunk.get(i);
            if (!entityPlayerMP.loadedChunks.contains(this.chunkCoords)) {
               entityPlayerMP.playerNetServerHandler.sendPacket(thePacket);
            }
         }
      }

      public void onUpdate() {
         if (this.numBlocksToUpdate != 0) {
            if (this.numBlocksToUpdate == 1) {
               int n = (this.locationOfBlockChange[0] >> 12 & 15) + this.chunkCoords.chunkXPos * 16;
               int n2 = this.locationOfBlockChange[0] & 255;
               int n3 = (this.locationOfBlockChange[0] >> 8 & 15) + this.chunkCoords.chunkZPos * 16;
               BlockPos blockPos = new BlockPos(n, n2, n3);
               this.sendToAllPlayersWatchingChunk(new S23PacketBlockChange(PlayerManager.this.theWorldServer, blockPos));
               if (PlayerManager.this.theWorldServer.getBlockState(blockPos).getBlock().hasTileEntity()) {
                  this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(blockPos));
               }
            } else if (this.numBlocksToUpdate != 64) {
               this.sendToAllPlayersWatchingChunk(
                  new S22PacketMultiBlockChange(
                     this.numBlocksToUpdate,
                     this.locationOfBlockChange,
                     PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos)
                  )
               );

               for (int i = 0; i < this.numBlocksToUpdate; i++) {
                  int n = (this.locationOfBlockChange[i] >> 12 & 15) + this.chunkCoords.chunkXPos * 16;
                  int n2 = this.locationOfBlockChange[i] & 255;
                  int n3 = (this.locationOfBlockChange[i] >> 8 & 15) + this.chunkCoords.chunkZPos * 16;
                  BlockPos blockPos = new BlockPos(n, n2, n3);
                  if (PlayerManager.this.theWorldServer.getBlockState(blockPos).getBlock().hasTileEntity()) {
                     this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(blockPos));
                  }
               }
            } else {
               int n = this.chunkCoords.chunkXPos * 16;
               int n2 = this.chunkCoords.chunkZPos * 16;
               this.sendToAllPlayersWatchingChunk(
                  new S21PacketChunkData(
                     PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos),
                     false,
                     this.flagsYAreasToUpdate
                  )
               );

               for (int i = 0; i < 16; i++) {
                  if ((this.flagsYAreasToUpdate & 1 << i) != 0) {
                     int n3 = i << 4;
                     List tileEntitiesIn = PlayerManager.this.theWorldServer.getTileEntitiesIn(n, n3, n2, n + 16, n3 + 16, n2 + 16);

                     for (int j = 0; j < tileEntitiesIn.size(); j++) {
                        this.sendTileToAllPlayersWatchingChunk((TileEntity)tileEntitiesIn.get(j));
                     }
                  }
               }
            }

            this.numBlocksToUpdate = 0;
            this.flagsYAreasToUpdate = 0;
         }
      }

      public void sendTileToAllPlayersWatchingChunk(TileEntity theTileEntity) {
         if (theTileEntity != null) {
            Packet descriptionPacket = theTileEntity.getDescriptionPacket();
            if (descriptionPacket != null) {
               this.sendToAllPlayersWatchingChunk(descriptionPacket);
            }
         }
      }
   }
}
