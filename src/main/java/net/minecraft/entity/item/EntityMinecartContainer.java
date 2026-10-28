package net.minecraft.entity.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class EntityMinecartContainer extends EntityMinecart implements IInventory {
    public ItemStack[] minecartContainerItems = new ItemStack[36];
    public boolean dropContentsWhenDead = true;

    public EntityMinecartContainer(World world) {
        super(world);
    }

    public EntityMinecartContainer(World world, double d, double d2, double d3) {
        super(world, d, d2, d3);
    }

    @Override
    public void killMinecart(DamageSource damageSource) {
        super.killMinecart(damageSource);

        for (int i = 0; i < this.getSizeInventory(); i++) {
            ItemStack stackInSlot = this.getStackInSlot(i);
            if (stackInSlot != null) {
                float f = this.rand.nextFloat() * 0.8F + 0.1F;
                float f2 = this.rand.nextFloat() * 0.8F + 0.1F;
                float f3 = this.rand.nextFloat() * 0.8F + 0.1F;

                while (stackInSlot.stackSize > 0) {
                    int n = this.rand.nextInt(21) + 10;
                    if (n > stackInSlot.stackSize) {
                        n = stackInSlot.stackSize;
                    }

                    stackInSlot.stackSize -= n;
                    EntityItem entityItem = new EntityItem(
                        this.worldObj, this.posX + f, this.posY + f2, this.posZ + f3, new ItemStack(stackInSlot.getItem(), n, stackInSlot.getMetadata())
                    );
                    float f4 = 0.05F;
                    entityItem.motionX = (float)this.rand.nextGaussian() * f4;
                    entityItem.motionY = (float)this.rand.nextGaussian() * f4 + 0.2F;
                    entityItem.motionZ = (float)this.rand.nextGaussian() * f4;
                    this.worldObj.spawnEntityInWorld(entityItem);
                }
            }
        }
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return this.minecartContainerItems[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.minecartContainerItems[index] != null) {
            if (this.minecartContainerItems[index].stackSize <= count) {
                ItemStack itemStack = this.minecartContainerItems[index];
                this.minecartContainerItems[index] = null;
                return itemStack;
            }

            ItemStack splitStack = this.minecartContainerItems[index].splitStack(count);
            if (this.minecartContainerItems[index].stackSize == 0) {
                this.minecartContainerItems[index] = null;
            }

            return splitStack;
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (this.minecartContainerItems[index] != null) {
            ItemStack itemStack = this.minecartContainerItems[index];
            this.minecartContainerItems[index] = null;
            return itemStack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.minecartContainerItems[index] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return !this.isDead && player.getDistanceSqToEntity(this) <= 64.0;
    }

    @Override
    public void openChest() {
    }

    @Override
    public void closeChest() {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public String getInventoryName() {
        return this.isCustomInventoryName() ? this.getEntityName() : "container.minecart";
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void travelToDimension(int dimensionId) throws java.io.IOException {
        this.dropContentsWhenDead = false;
        super.travelToDimension(dimensionId);
    }

    @Override
    public void setDead() {
        if (this.dropContentsWhenDead) {
            for (int i = 0; i < this.getSizeInventory(); i++) {
                ItemStack stackInSlot = this.getStackInSlot(i);
                if (stackInSlot != null) {
                    float f = this.rand.nextFloat() * 0.8F + 0.1F;
                    float f2 = this.rand.nextFloat() * 0.8F + 0.1F;
                    float f3 = this.rand.nextFloat() * 0.8F + 0.1F;

                    while (stackInSlot.stackSize > 0) {
                        int n = this.rand.nextInt(21) + 10;
                        if (n > stackInSlot.stackSize) {
                            n = stackInSlot.stackSize;
                        }

                        stackInSlot.stackSize -= n;
                        EntityItem entityItem = new EntityItem(
                            this.worldObj, this.posX + f, this.posY + f2, this.posZ + f3, new ItemStack(stackInSlot.getItem(), n, stackInSlot.getMetadata())
                        );
                        if (stackInSlot.hasTagCompound()) {
                            entityItem.getEntityItem().setTagCompound((NBTTagCompound)stackInSlot.getTagCompound().copy());
                        }

                        float f4 = 0.05F;
                        entityItem.motionX = (float)this.rand.nextGaussian() * f4;
                        entityItem.motionY = (float)this.rand.nextGaussian() * f4 + 0.2F;
                        entityItem.motionZ = (float)this.rand.nextGaussian() * f4;
                        this.worldObj.spawnEntityInWorld(entityItem);
                    }
                }
            }
        }

        super.setDead();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        NBTTagList nbttagList = new NBTTagList();

        for (int i = 0; i < this.minecartContainerItems.length; i++) {
            if (this.minecartContainerItems[i] != null) {
                NBTTagCompound nbttagCompound = new NBTTagCompound();
                nbttagCompound.setByte("Slot", (byte)i);
                this.minecartContainerItems[i].writeToNBT(nbttagCompound);
                nbttagList.appendTag(nbttagCompound);
            }
        }

        tagCompound.setTag("Items", nbttagList);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        NBTTagList tagList = tagCompund.getTagList("Items", 10);
        this.minecartContainerItems = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
            int n = compoundTagAt.getByte("Slot") & 255;
            if (n >= 0 && n < this.minecartContainerItems.length) {
                this.minecartContainerItems[n] = ItemStack.loadItemStackFromNBT(compoundTagAt);
            }
        }
    }

    @Override
    public boolean interactFirst(EntityPlayer player) throws java.io.IOException {
        if (!this.worldObj.isRemote) {
            player.displayGUIChest(this);
        }

        return true;
    }

    @Override
    public void applyDrag() {
        int n = 15 - Container.calcRedstoneFromInventory(this);
        float f = 0.98F + n * 0.001F;
        this.motionX *= f;
        this.motionY *= 0.0;
        this.motionZ *= f;
    }
}
