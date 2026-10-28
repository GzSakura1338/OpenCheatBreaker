package net.minecraft.client.renderer;

import java.util.Comparator;
import net.minecraft.entity.Entity;

public class EntitySorter implements Comparator {
    public double entityPosX;
    public double entityPosY;
    public double entityPosZ;

    public EntitySorter(Entity entity) {
        this.entityPosX = -entity.posX;
        this.entityPosY = -entity.posY;
        this.entityPosZ = -entity.posZ;
    }

    public int compare(WorldRenderer worldRenderer, WorldRenderer worldRenderer2) {
        double d = worldRenderer.posXPlus + this.entityPosX;
        double d2 = worldRenderer.posYPlus + this.entityPosY;
        double d3 = worldRenderer.posZPlus + this.entityPosZ;
        double d4 = worldRenderer2.posXPlus + this.entityPosX;
        double d5 = worldRenderer2.posYPlus + this.entityPosY;
        double d6 = worldRenderer2.posZPlus + this.entityPosZ;
        return (int)((d * d + d2 * d2 + d3 * d3 - (d4 * d4 + d5 * d5 + d6 * d6)) * 1024.0);
    }

    @Override
    public int compare(Object obj, Object obj2) {
        return this.compare((WorldRenderer)obj, (WorldRenderer)obj2);
    }
}
