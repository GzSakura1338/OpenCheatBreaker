package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class WorldManager implements IWorldAccess {
   public MinecraftServer mcServer;
   public WorldServer theWorldServer;

   public WorldManager(MinecraftServer mcServerIn, WorldServer worldServerIn) {
      this.mcServer = mcServerIn;
      this.theWorldServer = worldServerIn;
   }

   @Override
   public void spawnParticle(
      int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... parameters
   ) {
   }

   @Override
   public void onEntityAdded(Entity entityIn) {
      this.theWorldServer.getEntityTracker().trackEntity(entityIn);
   }

   @Override
   public void onEntityRemoved(Entity entityIn) {
      this.theWorldServer.getEntityTracker().untrackEntity(entityIn);
      this.theWorldServer.getScoreboard().resetEntityScore(entityIn);
   }

   @Override
   public void playSound(String soundName, double x, double y, double z, float volume, float pitch) {
      this.mcServer
         .getConfigurationManager()
         .sendToAllNear(
            x,
            y,
            z,
            volume > 1.0F ? 16.0F * volume : 16.0,
            this.theWorldServer.provider.getDimensionId(),
            new S29PacketSoundEffect(soundName, x, y, z, volume, pitch)
         );
   }

   @Override
   public void playSoundToNearExcept(EntityPlayer except, String soundName, double x, double y, double z, float volume, float pitch) {
      this.mcServer
         .getConfigurationManager()
         .sendToAllNearExcept(
            except,
            x,
            y,
            z,
            volume > 1.0F ? 16.0F * volume : 16.0,
            this.theWorldServer.provider.getDimensionId(),
            new S29PacketSoundEffect(soundName, x, y, z, volume, pitch)
         );
   }

   @Override
   public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
   }

   @Override
   public void markBlockForUpdate(BlockPos pos) {
      this.theWorldServer.getPlayerManager().markBlockForUpdate(pos);
   }

   @Override
   public void notifyLightSet(BlockPos pos) {
   }

   @Override
   public void playRecord(String recordName, BlockPos blockPosIn) {
   }

   @Override
   public void playAuxSFX(EntityPlayer player, int sfxType, BlockPos blockPosIn, int data) {
      this.mcServer
         .getConfigurationManager()
         .sendToAllNearExcept(
            player,
            blockPosIn.getX(),
            blockPosIn.getY(),
            blockPosIn.getZ(),
            64.0,
            this.theWorldServer.provider.getDimensionId(),
            new S28PacketEffect(sfxType, blockPosIn, data, false)
         );
   }

   @Override
   public void broadcastSound(int soundID, BlockPos pos, int data) {
      this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S28PacketEffect(soundID, pos, data, true));
   }

   @Override
   public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
      for (EntityPlayerMP entityPlayerMP : this.mcServer.getConfigurationManager().getPlayerList()) {
         if (entityPlayerMP != null && entityPlayerMP.worldObj == this.theWorldServer && entityPlayerMP.getEntityId() != breakerId) {
            double d = pos.getX() - entityPlayerMP.posX;
            double d2 = pos.getY() - entityPlayerMP.posY;
            double d3 = pos.getZ() - entityPlayerMP.posZ;
            if (d * d + d2 * d2 + d3 * d3 < 1024.0) {
               entityPlayerMP.playerNetServerHandler.sendPacket(new S25PacketBlockBreakAnim(breakerId, pos, progress));
            }
         }
      }
   }
}
