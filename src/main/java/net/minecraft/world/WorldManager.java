package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.server.MinecraftServer;

public class WorldManager implements IWorldAccess {
    public MinecraftServer mcServer;
    public WorldServer theWorldServer;

    public WorldManager(MinecraftServer minecraftServer, WorldServer worldServer) {
        this.mcServer = minecraftServer;
        this.theWorldServer = worldServer;
    }

    @Override
    public void spawnParticle(String str, double d, double d2, double d3, double d4, double d5, double d6) {
    }

    @Override
    public void onEntityCreate(Entity entity) {
        this.theWorldServer.getEntityTracker().trackEntity(entity);
    }

    @Override
    public void onEntityDestroy(Entity entity) {
        this.theWorldServer.getEntityTracker().untrackEntity(entity);
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
                this.theWorldServer.provider.dimensionId,
                new S29PacketSoundEffect(soundName, x, y, z, volume, pitch)
            );
    }

    @Override
    public void playSoundToNearExcept(EntityPlayer entityPlayer, String str, double d, double d2, double d3, float f, float f2) {
        this.mcServer
            .getConfigurationManager()
            .sendToAllNearExcept(
                entityPlayer,
                d,
                d2,
                d3,
                f > 1.0F ? 16.0F * f : 16.0,
                this.theWorldServer.provider.dimensionId,
                new S29PacketSoundEffect(str, d, d2, d3, f, f2)
            );
    }

    @Override
    public void markBlockRangeForRenderUpdate(int n, int n2, int n3, int n4, int n5, int n6) {
    }

    @Override
    public void markBlockForUpdate(int n, int n2, int n3) {
        this.theWorldServer.getPlayerManager().markBlockForUpdate(n, n2, n3);
    }

    @Override
    public void markBlockForRenderUpdate(int n, int n2, int n3) {
    }

    @Override
    public void playRecord(String str, int n, int n2, int n3) {
    }

    @Override
    public void playAuxSFX(EntityPlayer entityPlayer, int n, int n2, int n3, int n4, int n5) {
        this.mcServer
            .getConfigurationManager()
            .sendToAllNearExcept(entityPlayer, n2, n3, n4, 64.0, this.theWorldServer.provider.dimensionId, new S28PacketEffect(n, n2, n3, n4, n5, false));
    }

    @Override
    public void broadcastSound(int n, int n2, int n3, int n4, int n5) {
        this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S28PacketEffect(n, n2, n3, n4, n5, true));
    }

    @Override
    public void destroyBlockPartially(int n, int n2, int n3, int n4, int n5) {
        for (EntityPlayerMP entityPlayerMP : (Iterable<EntityPlayerMP>)this.mcServer.getConfigurationManager().playerEntityList) {
            if (entityPlayerMP != null && entityPlayerMP.worldObj == this.theWorldServer && entityPlayerMP.getEntityId() != n) {
                double d = n2 - entityPlayerMP.posX;
                double d2 = n3 - entityPlayerMP.posY;
                double d3 = n4 - entityPlayerMP.posZ;
                if (d * d + d2 * d2 + d3 * d3 < 1024.0) {
                    entityPlayerMP.playerNetServerHandler.sendPacket(new S25PacketBlockBreakAnim(n, n2, n3, n4, n5));
                }
            }
        }
    }

    @Override
    public void onStaticEntitiesChanged() {
    }
}
