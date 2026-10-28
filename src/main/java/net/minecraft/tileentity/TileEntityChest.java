package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityChest extends TileEntity implements IInventory {
    public ItemStack[] chestContents = new ItemStack[36];
    public boolean adjacentChestChecked;
    public TileEntityChest adjacentChestZNeg;
    public TileEntityChest adjacentChestXPos;
    public TileEntityChest adjacentChestXNeg;
    public TileEntityChest adjacentChestZPos;
    public float lidAngle;
    public float prevLidAngle;
    public int numPlayersUsing;
    public int ticksSinceSync;
    public int cachedChestType;
    public String customName;

    public TileEntityChest() {
        this.cachedChestType = -1;
    }

    public TileEntityChest(int n) {
        this.cachedChestType = n;
    }

    @Override
    public int getSizeInventory() {
        return 27;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return this.chestContents[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.chestContents[index] != null) {
            if (this.chestContents[index].stackSize <= count) {
                ItemStack itemStack = this.chestContents[index];
                this.chestContents[index] = null;
                this.markDirty();
                return itemStack;
            }

            ItemStack splitStack = this.chestContents[index].splitStack(count);
            if (this.chestContents[index].stackSize == 0) {
                this.chestContents[index] = null;
            }

            this.markDirty();
            return splitStack;
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (this.chestContents[index] != null) {
            ItemStack itemStack = this.chestContents[index];
            this.chestContents[index] = null;
            return itemStack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.chestContents[index] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
    }

    @Override
    public String getInventoryName() {
        return this.isCustomInventoryName() ? this.customName : "container.chest";
    }

    @Override
    public boolean isCustomInventoryName() {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setCustomName(String str) {
        this.customName = str;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList tagList = compound.getTagList("Items", 10);
        this.chestContents = new ItemStack[this.getSizeInventory()];
        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
            int n = compoundTagAt.getByte("Slot") & 255;
            if (n >= 0 && n < this.chestContents.length) {
                this.chestContents[n] = ItemStack.loadItemStackFromNBT(compoundTagAt);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagList nbttagList = new NBTTagList();

        for (int i = 0; i < this.chestContents.length; i++) {
            if (this.chestContents[i] != null) {
                NBTTagCompound nbttagCompound = new NBTTagCompound();
                nbttagCompound.setByte("Slot", (byte)i);
                this.chestContents[i].writeToNBT(nbttagCompound);
                nbttagList.appendTag(nbttagCompound);
            }
        }

        compound.setTag("Items", nbttagList);
        if (this.isCustomInventoryName()) {
            compound.setString("CustomName", this.customName);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
            && player.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64.0;
    }

    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        this.adjacentChestChecked = false;
    }

    public void handleTileEntityChest(TileEntityChest tileEntityChest, int n) {
        if (tileEntityChest.isInvalid()) {
            this.adjacentChestChecked = false;
        } else if (this.adjacentChestChecked) {
            switch (n) {
                case 0:
                    if (this.adjacentChestZPos != tileEntityChest) {
                        this.adjacentChestChecked = false;
                    }
                    break;
                case 1:
                    if (this.adjacentChestXNeg != tileEntityChest) {
                        this.adjacentChestChecked = false;
                    }
                    break;
                case 2:
                    if (this.adjacentChestZNeg != tileEntityChest) {
                        this.adjacentChestChecked = false;
                    }
                    break;
                case 3:
                    if (this.adjacentChestXPos != tileEntityChest) {
                        this.adjacentChestChecked = false;
                    }
            }
        }
    }

    public void checkForAdjacentChests() {
        if (!this.adjacentChestChecked) {
            this.adjacentChestChecked = true;
            this.adjacentChestZNeg = null;
            this.adjacentChestXPos = null;
            this.adjacentChestXNeg = null;
            this.adjacentChestZPos = null;
            if (this.isSameTypeChestAt(this.xCoord - 1, this.yCoord, this.zCoord)) {
                this.adjacentChestXNeg = (TileEntityChest)this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord);
            }

            if (this.isSameTypeChestAt(this.xCoord + 1, this.yCoord, this.zCoord)) {
                this.adjacentChestXPos = (TileEntityChest)this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord);
            }

            if (this.isSameTypeChestAt(this.xCoord, this.yCoord, this.zCoord - 1)) {
                this.adjacentChestZNeg = (TileEntityChest)this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1);
            }

            if (this.isSameTypeChestAt(this.xCoord, this.yCoord, this.zCoord + 1)) {
                this.adjacentChestZPos = (TileEntityChest)this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1);
            }

            if (this.adjacentChestZNeg != null) {
                this.adjacentChestZNeg.handleTileEntityChest(this, 0);
            }

            if (this.adjacentChestZPos != null) {
                this.adjacentChestZPos.handleTileEntityChest(this, 2);
            }

            if (this.adjacentChestXPos != null) {
                this.adjacentChestXPos.handleTileEntityChest(this, 1);
            }

            if (this.adjacentChestXNeg != null) {
                this.adjacentChestXNeg.handleTileEntityChest(this, 3);
            }
        }
    }

    public boolean isSameTypeChestAt(int n, int n2, int n3) {
        if (this.worldObj == null) {
            return false;
        }

        Block block = this.worldObj.getBlock(n, n2, n3);
        return block instanceof BlockChest && ((BlockChest)block).chestType == this.getChestType();
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        this.checkForAdjacentChests();
        this.ticksSinceSync++;
        if (!this.worldObj.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + this.xCoord + this.yCoord + this.zCoord) % 200 == 0) {
            this.numPlayersUsing = 0;
            float f = 5.0F;

            for (EntityPlayer entityPlayer : (Iterable<EntityPlayer>)(this.worldObj
                .getEntitiesWithinAABB(
                    EntityPlayer.class,
                    AxisAlignedBB.getBoundingBox(
                        this.xCoord - f, this.yCoord - f, this.zCoord - f, this.xCoord + 1 + f, this.yCoord + 1 + f, this.zCoord + 1 + f
                    )
                ))) {
                if (entityPlayer.openContainer instanceof ContainerChest) {
                    IInventory lowerChestInventory = ((ContainerChest)entityPlayer.openContainer).getLowerChestInventory();
                    if (lowerChestInventory == this || lowerChestInventory instanceof InventoryLargeChest && ((InventoryLargeChest)lowerChestInventory).isPartOfLargeChest(this)) {
                        this.numPlayersUsing++;
                    }
                }
            }
        }

        this.prevLidAngle = this.lidAngle;
        float f = 0.1F;
        if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
            double d = this.xCoord + 0.5;
            double d2 = this.zCoord + 0.5;
            if (this.adjacentChestZPos != null) {
                d2 += 0.5;
            }

            if (this.adjacentChestXPos != null) {
                d += 0.5;
            }

            this.worldObj.playSoundEffect(d, this.yCoord + 0.5, d2, "random.chestopen", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
            float lidAngle = this.lidAngle;
            if (this.numPlayersUsing > 0) {
                this.lidAngle += f;
            } else {
                this.lidAngle -= f;
            }

            if (this.lidAngle > 1.0F) {
                this.lidAngle = 1.0F;
            }

            float f2 = 0.5F;
            if (this.lidAngle < f2 && lidAngle >= f2 && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null) {
                double d = this.xCoord + 0.5;
                double d2 = this.zCoord + 0.5;
                if (this.adjacentChestZPos != null) {
                    d2 += 0.5;
                }

                if (this.adjacentChestXPos != null) {
                    d += 0.5;
                }

                this.worldObj.playSoundEffect(d, this.yCoord + 0.5, d2, "random.chestclosed", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (this.lidAngle < 0.0F) {
                this.lidAngle = 0.0F;
            }
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            this.numPlayersUsing = type;
            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }

    @Override
    public void openChest() {
        if (this.numPlayersUsing < 0) {
            this.numPlayersUsing = 0;
        }

        this.numPlayersUsing++;
        this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, this.numPlayersUsing);
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType());
    }

    @Override
    public void closeChest() {
        if (this.getBlockType() instanceof BlockChest) {
            this.numPlayersUsing--;
            this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, this.numPlayersUsing);
            this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
            this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType());
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.updateContainingBlockInfo();
        this.checkForAdjacentChests();
    }

    public int getChestType() {
        if (this.cachedChestType == -1) {
            if (this.worldObj == null || !(this.getBlockType() instanceof BlockChest)) {
                return 0;
            }

            this.cachedChestType = ((BlockChest)this.getBlockType()).chestType;
        }

        return this.cachedChestType;
    }
}
