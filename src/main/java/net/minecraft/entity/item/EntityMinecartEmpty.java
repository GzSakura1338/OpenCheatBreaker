package net.minecraft.entity.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityMinecartEmpty extends EntityMinecart {
    public EntityMinecartEmpty(World world) {
        super(world);
    }

    public EntityMinecartEmpty(World world, double d, double d2, double d3) {
        super(world, d, d2, d3);
    }

    @Override
    public boolean interactFirst(EntityPlayer player) {
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player) {
            return true;
        }

        if (this.riddenByEntity != null && this.riddenByEntity != player) {
            return false;
        }

        if (!this.worldObj.isRemote) {
            player.mountEntity(this);
        }

        return true;
    }

    @Override
    public int getMinecartType() {
        return 0;
    }
}
