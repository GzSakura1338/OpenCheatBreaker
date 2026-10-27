package net.optifine.util;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;
import net.optifine.Config;

public class IntegratedServerUtils {
   public static WorldServer getWorldServer() {
      Minecraft minecraft = Config.getMinecraft();
      WorldClient theWorld = minecraft.theWorld;
      if (theWorld == null) {
         return null;
      }

      if (!minecraft.isIntegratedServerRunning()) {
         return null;
      }

      IntegratedServer integratedServer = minecraft.getIntegratedServer();
      if (integratedServer == null) {
         return null;
      }

      WorldProvider provider = theWorld.provider;
      if (provider == null) {
         return null;
      }

      int dimensionId = provider.getDimensionId();

      try {
         return integratedServer.worldServerForDimension(dimensionId);
      } catch (NullPointerException e) {
         return null;
      }
   }

   public static Entity getEntity(UUID uuid) {
      WorldServer worldServer = getWorldServer();
      return worldServer == null ? null : worldServer.getEntityFromUuid(uuid);
   }

   public static TileEntity getTileEntity(BlockPos blockPos) {
      WorldServer worldServer = getWorldServer();
      if (worldServer == null) {
         return null;
      }

      Chunk provideChunk = worldServer.getChunkProvider().provideChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
      return provideChunk == null ? null : provideChunk.getTileEntity(blockPos, EnumCreateEntityType.CHECK);
   }
}
