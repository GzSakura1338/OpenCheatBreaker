package net.minecraft.server.management;

import java.util.Comparator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

public class PlayerPositionComparator implements Comparator {
    public ChunkCoordinates theChunkCoordinates;

    public PlayerPositionComparator(ChunkCoordinates chunkCoordinates) {
        this.theChunkCoordinates = chunkCoordinates;
    }

    public int compare(EntityPlayerMP entityPlayerMP, EntityPlayerMP entityPlayerMP2) {
        double distanceSq = entityPlayerMP.getDistanceSq(this.theChunkCoordinates.posX, this.theChunkCoordinates.posY, this.theChunkCoordinates.posZ);
        double distanceSq2 = entityPlayerMP2.getDistanceSq(this.theChunkCoordinates.posX, this.theChunkCoordinates.posY, this.theChunkCoordinates.posZ);
        return distanceSq < distanceSq2 ? -1 : (distanceSq > distanceSq2 ? 1 : 0);
    }

    @Override
    public int compare(Object obj, Object obj2) {
        return this.compare((EntityPlayerMP)obj, (EntityPlayerMP)obj2);
    }
}
