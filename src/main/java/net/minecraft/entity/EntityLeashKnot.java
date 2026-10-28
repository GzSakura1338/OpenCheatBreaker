package net.minecraft.entity;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityLeashKnot extends EntityHanging {
    public EntityLeashKnot(World world) {
        super(world);
    }

    public EntityLeashKnot(World world, int n, int n2, int n3) {
        super(world, n, n2, n3, 0);
        this.setPosition(n + 0.5, n2 + 0.5, n3 + 0.5);
    }

    @Override
    public void entityInit() {
        super.entityInit();
    }

    @Override
    public void setDirection(int n) {
    }

    @Override
    public int getWidthPixels() {
        return 9;
    }

    @Override
    public int getHeightPixels() {
        return 9;
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 1024.0;
    }

    @Override
    public void onBroken(Entity entity) {
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound tagCompund) {
        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
    }

    @Override
    public boolean interactFirst(EntityPlayer player) {
        ItemStack heldItem = player.getHeldItem();
        boolean flag = false;
        if (heldItem != null && heldItem.getItem() == Items.lead && !this.worldObj.isRemote) {
            double d = 7.0;
            List entitiesWithinAABB = this.worldObj
                .getEntitiesWithinAABB(
                    EntityLiving.class,
                    AxisAlignedBB.getBoundingBox(this.posX - d, this.posY - d, this.posZ - d, this.posX + d, this.posY + d, this.posZ + d)
                );
            if (entitiesWithinAABB != null) {
                for (EntityLiving entityLiving : (Iterable<EntityLiving>)entitiesWithinAABB) {
                    if (entityLiving.getLeashed() && entityLiving.getLeashedToEntity() == player) {
                        entityLiving.setLeashedToEntity(this, true);
                        flag = true;
                    }
                }
            }
        }

        if (!this.worldObj.isRemote && !flag) {
            this.setDead();
            if (player.capabilities.isCreativeMode) {
                double d = 7.0;
                List entitiesWithinAABB = this.worldObj
                    .getEntitiesWithinAABB(
                        EntityLiving.class,
                        AxisAlignedBB.getBoundingBox(this.posX - d, this.posY - d, this.posZ - d, this.posX + d, this.posY + d, this.posZ + d)
                    );
                if (entitiesWithinAABB != null) {
                    for (EntityLiving entityLiving : (Iterable<EntityLiving>)entitiesWithinAABB) {
                        if (entityLiving.getLeashed() && entityLiving.getLeashedToEntity() == this) {
                            entityLiving.clearLeashed(true, false);
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean onValidSurface() {
        return this.worldObj.getBlock(this.intValue, this.intValue2, this.intValue3).getRenderType() == 11;
    }

    public static EntityLeashKnot createEntityLeashKnot(World world, int n, int n2, int n3) {
        EntityLeashKnot entityLeashKnot = new EntityLeashKnot(world, n, n2, n3);
        entityLeashKnot.forceSpawn = true;
        world.spawnEntityInWorld(entityLeashKnot);
        return entityLeashKnot;
    }

    public static EntityLeashKnot getKnotForBlock(World world, int n, int n2, int n3) {
        List entitiesWithinAABB = world.getEntitiesWithinAABB(
            EntityLeashKnot.class, AxisAlignedBB.getBoundingBox(n - 1.0, n2 - 1.0, n3 - 1.0, n + 1.0, n2 + 1.0, n3 + 1.0)
        );
        if (entitiesWithinAABB != null) {
            for (EntityLeashKnot entityLeashKnot : (Iterable<EntityLeashKnot>)entitiesWithinAABB) {
                if (entityLeashKnot.intValue == n && entityLeashKnot.intValue2 == n2 && entityLeashKnot.intValue3 == n3) {
                    return entityLeashKnot;
                }
            }
        }

        return null;
    }
}
