package net.minecraft.entity.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityMinecartFurnace extends EntityMinecart {
    public int fuel;
    public double pushX;
    public double pushZ;

    public EntityMinecartFurnace(World world) {
        super(world);
    }

    public EntityMinecartFurnace(World world, double d, double d2, double d3) {
        super(world, d, d2, d3);
    }

    @Override
    public int getMinecartType() {
        return 2;
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, new Byte((byte)0));
    }

    @Override
    public void onUpdate() throws java.io.IOException {
        super.onUpdate();
        if (this.fuel > 0) {
            this.fuel--;
        }

        if (this.fuel <= 0) {
            this.pushX = this.pushZ = 0.0;
        }

        this.setMinecartPowered(this.fuel > 0);
        if (this.isMinecartPowered() && this.rand.nextInt(4) == 0) {
            this.worldObj.spawnParticle("largesmoke", this.posX, this.posY + 0.8, this.posZ, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void killMinecart(DamageSource damageSource) {
        super.killMinecart(damageSource);
        if (!damageSource.isExplosion()) {
            this.entityDropItem(new ItemStack(Blocks.furnace, 1), 0.0F);
        }
    }

    @Override
    public void process(int n, int n2, int n3, double d, double d2, Block block, int n4) {
        super.process(n, n2, n3, d, d2, block, n4);
        double d3 = this.pushX * this.pushX + this.pushZ * this.pushZ;
        if (d3 > 1.0E-4 && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.001) {
            d3 = MathHelper.sqrt_double(d3);
            this.pushX /= d3;
            this.pushZ /= d3;
            if (this.pushX * this.motionX + this.pushZ * this.motionZ < 0.0) {
                this.pushX = 0.0;
                this.pushZ = 0.0;
            } else {
                this.pushX = this.motionX;
                this.pushZ = this.motionZ;
            }
        }
    }

    @Override
    public void applyDrag() {
        double d = this.pushX * this.pushX + this.pushZ * this.pushZ;
        if (d > 1.0E-4) {
            d = MathHelper.sqrt_double(d);
            this.pushX /= d;
            this.pushZ /= d;
            double d2 = 0.05;
            this.motionX *= 0.8F;
            this.motionY *= 0.0;
            this.motionZ *= 0.8F;
            this.motionX = this.motionX + this.pushX * d2;
            this.motionZ = this.motionZ + this.pushZ * d2;
        } else {
            this.motionX *= 0.98F;
            this.motionY *= 0.0;
            this.motionZ *= 0.98F;
        }

        super.applyDrag();
    }

    @Override
    public boolean interactFirst(EntityPlayer player) {
        ItemStack currentItem = player.inventory.getCurrentItem();
        if (currentItem != null && currentItem.getItem() == Items.coal) {
            if (!player.capabilities.isCreativeMode && --currentItem.stackSize == 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }

            this.fuel += 3600;
        }

        this.pushX = this.posX - player.posX;
        this.pushZ = this.posZ - player.posZ;
        return true;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setDouble("PushX", this.pushX);
        tagCompound.setDouble("PushZ", this.pushZ);
        tagCompound.setShort("Fuel", (short)this.fuel);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.pushX = tagCompund.getDouble("PushX");
        this.pushZ = tagCompund.getDouble("PushZ");
        this.fuel = tagCompund.getShort("Fuel");
    }

    public boolean isMinecartPowered() {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void setMinecartPowered(boolean flag) {
        if (flag) {
            this.dataWatcher.updateObject(16, (byte)(this.dataWatcher.getWatchableObjectByte(16) | 1));
        } else {
            this.dataWatcher.updateObject(16, (byte)(this.dataWatcher.getWatchableObjectByte(16) & -2));
        }
    }

    @Override
    public Block getDefaultDisplayTile() {
        return Blocks.lit_furnace;
    }

    @Override
    public int getDefaultDisplayTileData() {
        return 2;
    }
}
