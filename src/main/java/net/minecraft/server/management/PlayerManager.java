package net.minecraft.server.management;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.optifine.CompactArrayList;
import net.optifine.Config;
import net.optifine.Reflector;
import net.optifine.ReflectorForge;
import net.optifine.WorldServerOF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerManager {
    public static Logger LOGGER = LogManager.getLogger();
    public List players = new ArrayList();
    public LongHashMap playerInstances = new LongHashMap();
    public List playerInstancesToUpdate = new ArrayList();
    public List playerInstanceList = new ArrayList();
    public CompactArrayList chunkCoordsNotLoaded = new CompactArrayList(100, 0.8F);
    public int[][] xzDirectionsConst = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
    public int playerViewRadius;
    public long previousTotalWorldTime;
    public WorldServer theWorldServer;

    public PlayerManager(WorldServer worldServer) {
        this.theWorldServer = worldServer;
        this.applyViewDistance(worldServer.getServer().getConfigurationManager().getViewDistance());
    }

    public WorldServer getMinecraftServer() {
        return this.theWorldServer;
    }

    public void updatePlayerInstances() {
        long totalWorldTime = this.theWorldServer.getTotalWorldTime();
        if (totalWorldTime - this.previousTotalWorldTime > 8000L) {
            this.previousTotalWorldTime = totalWorldTime;

            for (int i = 0; i < this.playerInstanceList.size(); i++) {
                PlayerManager.PlayerInstance playerInstance = (PlayerManager.PlayerInstance)this.playerInstanceList.get(i);
                playerInstance.onUpdate();
                playerInstance.processChunk();
            }
        } else {
            for (int i = 0; i < this.playerInstancesToUpdate.size(); i++) {
                PlayerManager.PlayerInstance playerInstance = (PlayerManager.PlayerInstance)this.playerInstancesToUpdate.get(i);
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

        if (this.chunkCoordsNotLoaded.size() > 0) {
            for (int i = 0; i < this.players.size(); i++) {
                EntityPlayerMP entityPlayerMP = (EntityPlayerMP)this.players.get(i);
                int chunkCoordX = entityPlayerMP.chunkCoordX;
                int chunkCoordZ = entityPlayerMP.chunkCoordZ;
                int n = this.playerViewRadius + 1;
                int n2 = n / 2;
                int n3 = n * n + n2 * n2;
                int n4 = n3;
                int n5 = -1;
                PlayerManager.PlayerInstance playerInstance = null;
                ChunkCoordIntPair chunkCoordIntPair = null;

                for (int j = 0; j < this.chunkCoordsNotLoaded.size(); j++) {
                    ChunkCoordIntPair chunkCoordIntPair2 = (ChunkCoordIntPair)this.chunkCoordsNotLoaded.get(j);
                    if (chunkCoordIntPair2 != null) {
                        PlayerManager.PlayerInstance playerInstance2 = this.getPlayerInstance(chunkCoordIntPair2.chunkXPos, chunkCoordIntPair2.chunkZPos, false);
                        if (playerInstance2 != null && !playerInstance2.chunkLoaded) {
                            int n6 = chunkCoordX - chunkCoordIntPair2.chunkXPos;
                            int n7 = chunkCoordZ - chunkCoordIntPair2.chunkZPos;
                            int n8 = n6 * n6 + n7 * n7;
                            if (n8 < n4) {
                                n4 = n8;
                                n5 = j;
                                playerInstance = playerInstance2;
                                chunkCoordIntPair = chunkCoordIntPair2;
                            }
                        } else {
                            this.chunkCoordsNotLoaded.set(j, null);
                        }
                    }
                }

                if (n5 >= 0) {
                    this.chunkCoordsNotLoaded.set(n5, null);
                }

                if (playerInstance != null) {
                    playerInstance.chunkLoaded = true;
                    this.getMinecraftServer().theChunkProviderServer.loadChunk(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos);
                    playerInstance.sendThisChunkToAllPlayers();
                    break;
                }
            }

            this.chunkCoordsNotLoaded.compact();
        }
    }

    public boolean getValueByKey(int n, int n2) {
        long l = n + 2147483647L | n2 + 2147483647L << 32;
        return this.playerInstances.getValueByKey(l) != null;
    }

    public PlayerManager.PlayerInstance getPlayerInstance(int n, int n2, boolean flag) {
        return this.getOrCreateChunkWatcher(n, n2, flag, false);
    }

    public PlayerManager.PlayerInstance getOrCreateChunkWatcher(int n, int n2, boolean flag, boolean flag2) {
        long l = n + 2147483647L | n2 + 2147483647L << 32;
        PlayerManager.PlayerInstance valueByKey = (PlayerManager.PlayerInstance)this.playerInstances.getValueByKey(l);
        if (valueByKey == null && flag) {
            valueByKey = new PlayerManager.PlayerInstance(n, n2, flag2);
            this.playerInstances.add(l, valueByKey);
            this.playerInstanceList.add(valueByKey);
        }

        return valueByKey;
    }

    public void markBlockForUpdate(int n, int n2, int n3) {
        int n4 = n >> 4;
        int n5 = n3 >> 4;
        PlayerManager.PlayerInstance playerInstance = this.getPlayerInstance(n4, n5, false);
        if (playerInstance != null) {
            playerInstance.flagChunkForUpdate(n & 15, n2, n3 & 15);
        }
    }

    public void addPlayer(EntityPlayerMP entityPlayerMP) {
        int n = (int)entityPlayerMP.posX >> 4;
        int n2 = (int)entityPlayerMP.posZ >> 4;
        entityPlayerMP.managedPosX = entityPlayerMP.posX;
        entityPlayerMP.managedPosZ = entityPlayerMP.posZ;
        ArrayList list = new ArrayList(1);

        for (int i = n - this.playerViewRadius; i <= n + this.playerViewRadius; i++) {
            for (int j = n2 - this.playerViewRadius; j <= n2 + this.playerViewRadius; j++) {
                this.getPlayerInstance(i, j, true).addPlayer(entityPlayerMP);
                if (i >= n - 1 && i <= n + 1 && j >= n2 - 1 && j <= n2 + 1) {
                    Chunk loadChunk = this.getMinecraftServer().theChunkProviderServer.loadChunk(i, j);
                    list.add(loadChunk);
                }
            }
        }

        entityPlayerMP.playerNetServerHandler.sendPacket(new S26PacketMapChunkBulk(list));
        this.players.add(entityPlayerMP);
        this.filterChunkLoadQueue(entityPlayerMP);
    }

    public void filterChunkLoadQueue(EntityPlayerMP entityPlayerMP) {
        ArrayList list = new ArrayList(entityPlayerMP.loadedChunks);
        int n = 0;
        int playerViewRadius = this.playerViewRadius;
        int n2 = (int)entityPlayerMP.posX >> 4;
        int n3 = (int)entityPlayerMP.posZ >> 4;
        int n4 = 0;
        int n5 = 0;
        ChunkCoordIntPair currentChunk = this.getPlayerInstance(n2, n3, true).currentChunk;
        entityPlayerMP.loadedChunks.clear();
        if (list.contains(currentChunk)) {
            entityPlayerMP.loadedChunks.add(currentChunk);
        }

        for (int i = 1; i <= playerViewRadius * 2; i++) {
            for (int j = 0; j < 2; j++) {
                int[] ints = this.xzDirectionsConst[n++ % 4];

                for (int k = 0; k < i; k++) {
                    n4 += ints[0];
                    n5 += ints[1];
                    currentChunk = this.getPlayerInstance(n2 + n4, n3 + n5, true).currentChunk;
                    if (list.contains(currentChunk)) {
                        entityPlayerMP.loadedChunks.add(currentChunk);
                    }
                }
            }
        }

        n %= 4;

        for (int i = 0; i < playerViewRadius * 2; i++) {
            n4 += this.xzDirectionsConst[n][0];
            n5 += this.xzDirectionsConst[n][1];
            currentChunk = this.getPlayerInstance(n2 + n4, n3 + n5, true).currentChunk;
            if (list.contains(currentChunk)) {
                entityPlayerMP.loadedChunks.add(currentChunk);
            }
        }
    }

    public void removePlayer(EntityPlayerMP entityPlayerMP) {
        int n = (int)entityPlayerMP.managedPosX >> 4;
        int n2 = (int)entityPlayerMP.managedPosZ >> 4;

        for (int i = n - this.playerViewRadius; i <= n + this.playerViewRadius; i++) {
            for (int j = n2 - this.playerViewRadius; j <= n2 + this.playerViewRadius; j++) {
                PlayerManager.PlayerInstance playerInstance = this.getPlayerInstance(i, j, false);
                if (playerInstance != null) {
                    playerInstance.removePlayer(entityPlayerMP, false);
                }
            }
        }

        this.players.remove(entityPlayerMP);
    }

    public boolean overlaps(int n, int n2, int n3, int n4, int n5) {
        int n6 = n - n3;
        int n7 = n2 - n4;
        return n6 >= -n5 && n6 <= n5 ? n7 >= -n5 && n7 <= n5 : false;
    }

    public void updateMountedMovingPlayer(EntityPlayerMP entityPlayerMP) {
        int n = (int)entityPlayerMP.posX >> 4;
        int n2 = (int)entityPlayerMP.posZ >> 4;
        double d = entityPlayerMP.managedPosX - entityPlayerMP.posX;
        double d2 = entityPlayerMP.managedPosZ - entityPlayerMP.posZ;
        double d3 = d * d + d2 * d2;
        if (d3 >= 64.0) {
            int n3 = (int)entityPlayerMP.managedPosX >> 4;
            int n4 = (int)entityPlayerMP.managedPosZ >> 4;
            int playerViewRadius = this.playerViewRadius;
            int n5 = n - n3;
            int n6 = n2 - n4;
            if (n5 != 0 || n6 != 0) {
                WorldServerOF worldServerOF = null;
                if (this.theWorldServer instanceof WorldServerOF) {
                    worldServerOF = (WorldServerOF)this.theWorldServer;
                }

                for (int i = n - playerViewRadius; i <= n + playerViewRadius; i++) {
                    for (int j = n2 - playerViewRadius; j <= n2 + playerViewRadius; j++) {
                        if (!this.overlaps(i, j, n3, n4, playerViewRadius)) {
                            this.getOrCreateChunkWatcher(i, j, true, true).addPlayer(entityPlayerMP);
                            if (worldServerOF != null) {
                                worldServerOF.addChunkToTickOnce(i, j);
                            }
                        }

                        if (!this.overlaps(i - n5, j - n6, n, n2, playerViewRadius)) {
                            PlayerManager.PlayerInstance playerInstance = this.getPlayerInstance(i - n5, j - n6, false);
                            if (playerInstance != null) {
                                playerInstance.removePlayer(entityPlayerMP);
                            }
                        }
                    }
                }

                this.filterChunkLoadQueue(entityPlayerMP);
                entityPlayerMP.managedPosX = entityPlayerMP.posX;
                entityPlayerMP.managedPosZ = entityPlayerMP.posZ;
            }
        }
    }

    public boolean isPlayerWatchingChunk(EntityPlayerMP entityPlayerMP, int n, int n2) {
        PlayerManager.PlayerInstance playerInstance = this.getPlayerInstance(n, n2, false);
        return playerInstance != null && playerInstance.playersWatchingChunk.contains(entityPlayerMP) && !entityPlayerMP.loadedChunks.contains(playerInstance.currentChunk);
    }

    public void applyViewDistance(int n) {
        n = MathHelper.clamp_int(n, 3, 32);
        if (n != this.playerViewRadius) {
            int n2 = n - this.playerViewRadius;

            for (Object obj : this.players) {
                EntityPlayerMP entityPlayerMP = (EntityPlayerMP)obj;
                int n3 = (int)entityPlayerMP.posX >> 4;
                int n4 = (int)entityPlayerMP.posZ >> 4;
                if (n2 > 0) {
                    for (int i = n3 - n; i <= n3 + n; i++) {
                        for (int j = n4 - n; j <= n4 + n; j++) {
                            PlayerManager.PlayerInstance orCreateChunkWatcher = this.getOrCreateChunkWatcher(i, j, true, true);
                            if (!orCreateChunkWatcher.playersWatchingChunk.contains(entityPlayerMP)) {
                                orCreateChunkWatcher.addPlayer(entityPlayerMP);
                            }
                        }
                    }
                } else {
                    for (int i = n3 - this.playerViewRadius; i <= n3 + this.playerViewRadius; i++) {
                        for (int j = n4 - this.playerViewRadius; j <= n4 + this.playerViewRadius; j++) {
                            if (!this.overlaps(i, j, n3, n4, n)) {
                                this.getPlayerInstance(i, j, true).removePlayer(entityPlayerMP);
                            }
                        }
                    }
                }
            }

            this.playerViewRadius = n;
        }
    }

    public static int getFurthestViewableBlock(int n) {
        return n * 16 - 16;
    }

    public class PlayerInstance {
        public List playersWatchingChunk = new ArrayList();
        public short[] locationOfBlockChange = new short[64];
        public boolean chunkLoaded = false;
        public int numBlocksToUpdate;
        public int flagsYAreasToUpdate;
        public long previousWorldTime;
        public ChunkCoordIntPair currentChunk;

        public PlayerInstance(int n, int n2) {
            this(n, n2, false);
        }

        public PlayerInstance(int n, int n2, boolean flag) {
            this.currentChunk = new ChunkCoordIntPair(n, n2);
            boolean flag2 = flag && Config.isLazyChunkLoading();
            if (flag2 && !PlayerManager.this.getMinecraftServer().theChunkProviderServer.chunkExists(n, n2)) {
                PlayerManager.this.chunkCoordsNotLoaded.add(this.currentChunk);
                this.chunkLoaded = false;
            } else {
                PlayerManager.this.getMinecraftServer().theChunkProviderServer.loadChunk(n, n2);
                this.chunkLoaded = true;
            }
        }

        public void addPlayer(EntityPlayerMP entityPlayerMP) {
            if (this.playersWatchingChunk.contains(entityPlayerMP)) {
                PlayerManager.LOGGER
                    .debug("Failed to add player. {} already is in chunk {}, {}", new Object[]{entityPlayerMP, this.currentChunk.chunkXPos, this.currentChunk.chunkZPos});
            } else {
                if (this.playersWatchingChunk.isEmpty()) {
                    this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
                }

                this.playersWatchingChunk.add(entityPlayerMP);
                entityPlayerMP.loadedChunks.add(this.currentChunk);
            }
        }

        public void removePlayer(EntityPlayerMP entityPlayerMP) {
            this.removePlayer(entityPlayerMP, true);
        }

        public void removePlayer(EntityPlayerMP entityPlayerMP, boolean flag) {
            if (this.playersWatchingChunk.contains(entityPlayerMP)) {
                Chunk chunkFromChunkCoords = PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.currentChunk.chunkXPos, this.currentChunk.chunkZPos);
                if (flag && chunkFromChunkCoords.isPopulated()) {
                    entityPlayerMP.playerNetServerHandler.sendPacket(new S21PacketChunkData(chunkFromChunkCoords, true, 0));
                }

                this.playersWatchingChunk.remove(entityPlayerMP);
                entityPlayerMP.loadedChunks.remove(this.currentChunk);
                if (Reflector.EventBus.exists()) {
                    Reflector.postForgeBusEvent(Reflector.ChunkWatchEvent_UnWatch_Constructor, this.currentChunk, entityPlayerMP);
                }

                if (this.playersWatchingChunk.isEmpty()) {
                    long l = this.currentChunk.chunkXPos + 2147483647L | this.currentChunk.chunkZPos + 2147483647L << 32;
                    this.increaseInhabitedTime(chunkFromChunkCoords);
                    PlayerManager.this.playerInstances.remove(l);
                    PlayerManager.this.playerInstanceList.remove(this);
                    if (this.numBlocksToUpdate > 0) {
                        PlayerManager.this.playerInstancesToUpdate.remove(this);
                    }

                    if (this.chunkLoaded) {
                        PlayerManager.this.getMinecraftServer().theChunkProviderServer.dropChunk(this.currentChunk.chunkXPos, this.currentChunk.chunkZPos);
                    }
                }
            }
        }

        public void processChunk() {
            this.increaseInhabitedTime(PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.currentChunk.chunkXPos, this.currentChunk.chunkZPos));
        }

        public void increaseInhabitedTime(Chunk chunk) {
            chunk.inhabitedTime = chunk.inhabitedTime + (PlayerManager.this.theWorldServer.getTotalWorldTime() - this.previousWorldTime);
            this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
        }

        public void flagChunkForUpdate(int n, int n2, int n3) {
            if (this.numBlocksToUpdate == 0) {
                PlayerManager.this.playerInstancesToUpdate.add(this);
            }

            this.flagsYAreasToUpdate |= 1 << (n2 >> 4);
            if (this.numBlocksToUpdate < 64) {
                short s = (short)(n << 12 | n3 << 8 | n2);

                for (int i = 0; i < this.numBlocksToUpdate; i++) {
                    if (this.locationOfBlockChange[i] == s) {
                        return;
                    }
                }

                this.locationOfBlockChange[this.numBlocksToUpdate++] = s;
            }
        }

        public void sendToAllPlayersWatchingChunk(Packet packet) {
            for (int i = 0; i < this.playersWatchingChunk.size(); i++) {
                EntityPlayerMP entityPlayerMP = (EntityPlayerMP)this.playersWatchingChunk.get(i);
                if (!entityPlayerMP.loadedChunks.contains(this.currentChunk)) {
                    entityPlayerMP.playerNetServerHandler.sendPacket(packet);
                }
            }
        }

        public void onUpdate() {
            if (this.numBlocksToUpdate != 0) {
                if (this.numBlocksToUpdate == 1) {
                    int n = this.currentChunk.chunkXPos * 16 + (this.locationOfBlockChange[0] >> 12 & 15);
                    int n2 = this.locationOfBlockChange[0] & 255;
                    int n3 = this.currentChunk.chunkZPos * 16 + (this.locationOfBlockChange[0] >> 8 & 15);
                    this.sendToAllPlayersWatchingChunk(new S23PacketBlockChange(n, n2, n3, PlayerManager.this.theWorldServer));
                    if (ReflectorForge.blockHasTileEntity(PlayerManager.this.theWorldServer, n, n2, n3)) {
                        this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(n, n2, n3));
                    }
                } else if (this.numBlocksToUpdate != 64) {
                    this.sendToAllPlayersWatchingChunk(
                        new S22PacketMultiBlockChange(
                            this.numBlocksToUpdate,
                            this.locationOfBlockChange,
                            PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.currentChunk.chunkXPos, this.currentChunk.chunkZPos)
                        )
                    );

                    for (int i = 0; i < this.numBlocksToUpdate; i++) {
                        int n = this.currentChunk.chunkXPos * 16 + (this.locationOfBlockChange[i] >> 12 & 15);
                        int n2 = this.locationOfBlockChange[i] & 255;
                        int n3 = this.currentChunk.chunkZPos * 16 + (this.locationOfBlockChange[i] >> 8 & 15);
                        if (ReflectorForge.blockHasTileEntity(PlayerManager.this.theWorldServer, n, n2, n3)) {
                            this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(n, n2, n3));
                        }
                    }
                } else {
                    int n = this.currentChunk.chunkXPos * 16;
                    int n2 = this.currentChunk.chunkZPos * 16;
                    this.sendToAllPlayersWatchingChunk(
                        new S21PacketChunkData(
                            PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.currentChunk.chunkXPos, this.currentChunk.chunkZPos),
                            false,
                            this.flagsYAreasToUpdate
                        )
                    );

                    for (int i = 0; i < 16; i++) {
                        if ((this.flagsYAreasToUpdate & 1 << i) != 0) {
                            int n3 = i << 4;
                            List createArrayList = PlayerManager.this.theWorldServer.createArrayList(n, n3, n2, n + 16, n3 + 16, n2 + 16);

                            for (int j = 0; j < createArrayList.size(); j++) {
                                this.sendTileToAllPlayersWatchingChunk((TileEntity)createArrayList.get(j));
                            }
                        }
                    }
                }

                this.numBlocksToUpdate = 0;
                this.flagsYAreasToUpdate = 0;
            }
        }

        public void sendTileToAllPlayersWatchingChunk(TileEntity tileEntity) {
            if (tileEntity != null) {
                Packet descriptionPacket = tileEntity.getDescriptionPacket();
                if (descriptionPacket != null) {
                    this.sendToAllPlayersWatchingChunk(descriptionPacket);
                }
            }
        }

        public void sendThisChunkToAllPlayers() {
            for (int i = 0; i < this.playersWatchingChunk.size(); i++) {
                EntityPlayerMP entityPlayerMP = (EntityPlayerMP)this.playersWatchingChunk.get(i);
                Chunk chunkFromChunkCoords = PlayerManager.this.getMinecraftServer().getChunkFromChunkCoords(this.currentChunk.chunkXPos, this.currentChunk.chunkZPos);
                ArrayList list = new ArrayList(1);
                list.add(chunkFromChunkCoords);
                entityPlayerMP.playerNetServerHandler.sendPacket(new S26PacketMapChunkBulk(list));
            }
        }
    }
}
