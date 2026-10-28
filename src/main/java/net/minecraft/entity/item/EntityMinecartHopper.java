package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityMinecartHopper extends EntityMinecartContainer implements IHopper {
    public boolean isBlocked = true;
    public int transferTicker = -1;

    public EntityMinecartHopper(World world) {
        super(world);
    }

    public EntityMinecartHopper(World world, double d, double d2, double d3) {
        super(world, d, d2, d3);
    }

    @Override
    public int getMinecartType() {
        return 5;
    }

    @Override
    public Block getDefaultDisplayTile() {
        return Blocks.hopper;
    }

    @Override
    public int getDefaultDisplayTileOffset() {
        return 1;
    }

    @Override
    public int getSizeInventory() {
        return 5;
    }

    @Override
    public boolean interactFirst(EntityPlayer player) throws java.io.IOException {
        if (!this.worldObj.isRemote) {
            player.displayGUIHopperMinecart(this);
        }

        return true;
    }

    @Override
    public void onActivatorRailPass(int n, int n2, int n3, boolean flag) {
        boolean flag2 = !flag;
        if (flag2 != this.getBlocked()) {
            this.setBlocked(flag2);
        }
    }

    public boolean getBlocked() {
        return this.isBlocked;
    }

    public void setBlocked(boolean flag) {
        this.isBlocked = flag;
    }

    @Override
    public World getWorld() {
        return this.worldObj;
    }

    @Override
    public double getXPos() {
        return this.posX;
    }

    @Override
    public double getYPos() {
        return this.posY;
    }

    @Override
    public double getZPos() {
        return this.posZ;
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        super.onUpdate();
        if (!this.worldObj.isRemote && this.isEntityAlive() && this.getBlocked()) {
            this.transferTicker--;
            if (!this.canTransfer()) {
                this.setTransferTicker(0);
                if (this.canOperate()) {
                    this.setTransferTicker(4);
                    this.markDirty();
                }
            }
        }
    }

    public boolean canOperate() {
        if (TileEntityHopper.extract(this)) {
            return true;
        }

        List selectEntitiesWithinAABB = this.worldObj.selectEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(0.25, 0.0, 0.25), IEntitySelector.selectAnything);
        if (selectEntitiesWithinAABB.size() > 0) {
            TileEntityHopper.extract(this, (EntityItem)selectEntitiesWithinAABB.get(0));
        }

        return false;
    }

    @Override
    public void killMinecart(DamageSource damageSource) {
        super.killMinecart(damageSource);
        this.dropItemWithOffset(Item.getItemFromBlock(Blocks.hopper), 1, 0.0F);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("TransferCooldown", this.transferTicker);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.transferTicker = tagCompund.getInteger("TransferCooldown");
    }

    public void setTransferTicker(int n) {
        this.transferTicker = n;
    }

    public boolean canTransfer() {
        return this.transferTicker > 0;
    }
}
