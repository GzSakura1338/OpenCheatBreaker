package net.minecraft.client.renderer;

import java.util.Comparator;
import net.minecraft.entity.EntityLivingBase;

public class RenderSorter implements Comparator {
    public EntityLivingBase baseEntity;

    public RenderSorter(EntityLivingBase entityLivingBase) {
        this.baseEntity = entityLivingBase;
    }

    public int compare(WorldRenderer worldRenderer, WorldRenderer worldRenderer2) {
        if (worldRenderer.isInFrustum && !worldRenderer2.isInFrustum) {
            return 1;
        }

        if (worldRenderer2.isInFrustum && !worldRenderer.isInFrustum) {
            return -1;
        }

        double distanceToEntitySquared = worldRenderer.distanceToEntitySquared(this.baseEntity);
        double distanceToEntitySquared2 = worldRenderer2.distanceToEntitySquared(this.baseEntity);
        return distanceToEntitySquared < distanceToEntitySquared2 ? 1 : (distanceToEntitySquared > distanceToEntitySquared2 ? -1 : (worldRenderer.chunkIndex < worldRenderer2.chunkIndex ? 1 : -1));
    }

    @Override
    public int compare(Object obj, Object obj2) {
        return this.compare((WorldRenderer)obj, (WorldRenderer)obj2);
    }
}
