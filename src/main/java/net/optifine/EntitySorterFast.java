package net.optifine;

import java.util.Comparator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;

public class EntitySorterFast implements Comparator {
    public Entity entity;

    public EntitySorterFast(Entity entity) {
        this.entity = entity;
    }

    public void prepareToSort(WorldRenderer[] worldRenderers, int n) {
        for (int i = 0; i < n; i++) {
            WorldRenderer worldRenderer = worldRenderers[i];
            worldRenderer.updateDistanceToEntitySquared(this.entity);
        }
    }

    public int compare(WorldRenderer worldRenderer, WorldRenderer worldRenderer2) {
        float sortDistanceToEntitySquared = worldRenderer.sortDistanceToEntitySquared;
        float sortDistanceToEntitySquared2 = worldRenderer2.sortDistanceToEntitySquared;
        return sortDistanceToEntitySquared == sortDistanceToEntitySquared2 ? 0 : (sortDistanceToEntitySquared > sortDistanceToEntitySquared2 ? 1 : -1);
    }

    @Override
    public int compare(Object obj, Object obj2) {
        return this.compare((WorldRenderer)obj, (WorldRenderer)obj2);
    }
}
