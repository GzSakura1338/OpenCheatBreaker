package net.minecraft.entity.ai;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class EntitySenses {
    public List seenEntities = new ArrayList();
    public List unseenEntities = new ArrayList();
    public EntityLiving entityObj;

    public EntitySenses(EntityLiving entityLiving) {
        this.entityObj = entityLiving;
    }

    public void clearSensingCache() {
        this.seenEntities.clear();
        this.unseenEntities.clear();
    }

    public boolean canSee(Entity entity) {
        if (this.seenEntities.contains(entity)) {
            return true;
        }

        if (this.unseenEntities.contains(entity)) {
            return false;
        }

        this.entityObj.worldObj.theProfiler.startSection("canSee");
        boolean canEntityBeSeen = this.entityObj.canEntityBeSeen(entity);
        this.entityObj.worldObj.theProfiler.endSection();
        if (canEntityBeSeen) {
            this.seenEntities.add(entity);
        } else {
            this.unseenEntities.add(entity);
        }

        return canEntityBeSeen;
    }
}
