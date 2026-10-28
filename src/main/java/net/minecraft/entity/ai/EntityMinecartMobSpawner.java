package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityMinecartMobSpawner extends EntityMinecart {
    public MobSpawnerBaseLogic mobSpawnerLogic = new MobSpawnerBaseLogic() {
        @Override
        public void addBlockEvent(int n) {
            EntityMinecartMobSpawner.this.worldObj.setEntityState(EntityMinecartMobSpawner.this, (byte)n);
        }

        @Override
        public World getSpawnerWorld() {
            return EntityMinecartMobSpawner.this.worldObj;
        }

        @Override
        public int getSpawnerX() {
            return MathHelper.floor_double(EntityMinecartMobSpawner.this.posX);
        }

        @Override
        public int getSpawnerY() {
            return MathHelper.floor_double(EntityMinecartMobSpawner.this.posY);
        }

        @Override
        public int getSpawnerZ() {
            return MathHelper.floor_double(EntityMinecartMobSpawner.this.posZ);
        }
    };

    public EntityMinecartMobSpawner(World world) {
        super(world);
    }

    public EntityMinecartMobSpawner(World world, double d, double d2, double d3) {
        super(world, d, d2, d3);
    }

    @Override
    public int getMinecartType() {
        return 4;
    }

    @Override
    public Block getDefaultDisplayTile() {
        return Blocks.mob_spawner;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.mobSpawnerLogic.readFromNBT(tagCompund);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        this.mobSpawnerLogic.writeToNBT(tagCompound);
    }

    @Override
    public void handleHealthUpdate(byte b) {
        this.mobSpawnerLogic.setDelayToMin(b);
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        super.onUpdate();
        this.mobSpawnerLogic.updateSpawner();
    }

    public MobSpawnerBaseLogic getSpawnerBehavior() {
        return this.mobSpawnerLogic;
    }
}
