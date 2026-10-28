package net.minecraft.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityHopper extends TileEntity implements IHopper {
    public ItemStack[] items = new ItemStack[5];
    public String customName;
    public int transferCooldown = -1;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList tagList = compound.getTagList("Items", 10);
        this.items = new ItemStack[this.getSizeInventory()];
        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }

        this.transferCooldown = compound.getInteger("TransferCooldown");

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
            byte byte2 = compoundTagAt.getByte("Slot");
            if (byte2 >= 0 && byte2 < this.items.length) {
                this.items[byte2] = ItemStack.loadItemStackFromNBT(compoundTagAt);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagList nbttagList = new NBTTagList();

        for (int i = 0; i < this.items.length; i++) {
            if (this.items[i] != null) {
                NBTTagCompound nbttagCompound = new NBTTagCompound();
                nbttagCompound.setByte("Slot", (byte)i);
                this.items[i].writeToNBT(nbttagCompound);
                nbttagList.appendTag(nbttagCompound);
            }
        }

        compound.setTag("Items", nbttagList);
        compound.setInteger("TransferCooldown", this.transferCooldown);
        if (this.isCustomInventoryName()) {
            compound.setString("CustomName", this.customName);
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    public int getSizeInventory() {
        return this.items.length;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return this.items[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.items[index] != null) {
            if (this.items[index].stackSize <= count) {
                ItemStack itemStack = this.items[index];
                this.items[index] = null;
                return itemStack;
            }

            ItemStack splitStack = this.items[index].splitStack(count);
            if (this.items[index].stackSize == 0) {
                this.items[index] = null;
            }

            return splitStack;
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (this.items[index] != null) {
            ItemStack itemStack = this.items[index];
            this.items[index] = null;
            return itemStack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.items[index] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public String getInventoryName() {
        return this.isCustomInventoryName() ? this.customName : "container.hopper";
    }

    @Override
    public boolean isCustomInventoryName() {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setCustomName(String str) {
        this.customName = str;
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
    public void updateEntity() {
        if (this.worldObj != null && !this.worldObj.isRemote) {
            this.transferCooldown--;
            if (!this.isOnTransferCooldown()) {
                this.setTransferCooldown(0);
                this.insertAndExtract();
            }
        }
    }

    public boolean insertAndExtract() {
        if (this.worldObj != null && !this.worldObj.isRemote) {
            if (!this.isOnTransferCooldown() && BlockHopper.getActiveStateFromMetadata(this.getBlockMetadata())) {
                boolean flag = false;
                if (!this.isHopperEmpty()) {
                    flag = this.insert();
                }

                if (!this.isFull()) {
                    flag = extract(this) || flag;
                }

                if (flag) {
                    this.setTransferCooldown(8);
                    this.markDirty();
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public boolean isHopperEmpty() {
        for (ItemStack itemStack : this.items) {
            if (itemStack != null) {
                return false;
            }
        }

        return true;
    }

    public boolean isFull() {
        for (ItemStack itemStack : this.items) {
            if (itemStack == null || itemStack.stackSize != itemStack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    public boolean insert() {
        IInventory outputInventory = this.getOutputInventory();
        if (outputInventory == null) {
            return false;
        }

        int n = Facing.oppositeSide[BlockHopper.getDirectionFromMetadata(this.getBlockMetadata())];
        if (this.checkIInventory(outputInventory, n)) {
            return false;
        }

        for (int i = 0; i < this.getSizeInventory(); i++) {
            if (this.getStackInSlot(i) != null) {
                ItemStack itemStack = this.getStackInSlot(i).copy();
                ItemStack findItemStack2 = findItemStack(outputInventory, this.decrStackSize(i, 1), n);
                if (findItemStack2 == null || findItemStack2.stackSize == 0) {
                    outputInventory.markDirty();
                    return true;
                }

                this.setInventorySlotContents(i, itemStack);
            }
        }

        return false;
    }

    public boolean checkIInventory(IInventory inventory, int n) {
        if (inventory instanceof ISidedInventory && n > -1) {
            ISidedInventory sidedInventory = (ISidedInventory)inventory;
            int[] slotsForFace = sidedInventory.getSlotsForFace(n);

            for (int i = 0; i < slotsForFace.length; i++) {
                ItemStack stackInSlot = sidedInventory.getStackInSlot(slotsForFace[i]);
                if (stackInSlot == null || stackInSlot.stackSize != stackInSlot.getMaxStackSize()) {
                    return false;
                }
            }
        } else {
            int sizeInventory = inventory.getSizeInventory();

            for (int i = 0; i < sizeInventory; i++) {
                ItemStack stackInSlot = inventory.getStackInSlot(i);
                if (stackInSlot == null || stackInSlot.stackSize != stackInSlot.getMaxStackSize()) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean checkIInventory2(IInventory inventory, int n) {
        if (inventory instanceof ISidedInventory && n > -1) {
            ISidedInventory sidedInventory = (ISidedInventory)inventory;
            int[] slotsForFace = sidedInventory.getSlotsForFace(n);

            for (int i = 0; i < slotsForFace.length; i++) {
                if (sidedInventory.getStackInSlot(slotsForFace[i]) != null) {
                    return false;
                }
            }
        } else {
            int sizeInventory = inventory.getSizeInventory();

            for (int i = 0; i < sizeInventory; i++) {
                if (inventory.getStackInSlot(i) != null) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean extract(IHopper hopper) {
        IInventory inputInventory = getInputInventory(hopper);
        if (inputInventory != null) {
            byte b = 0;
            if (checkIInventory2(inputInventory, b)) {
                return false;
            }

            if (inputInventory instanceof ISidedInventory && b > -1) {
                ISidedInventory sidedInventory = (ISidedInventory)inputInventory;
                int[] slotsForFace = sidedInventory.getSlotsForFace(b);

                for (int i = 0; i < slotsForFace.length; i++) {
                    if (checkIHopper(hopper, inputInventory, slotsForFace[i], b)) {
                        return true;
                    }
                }
            } else {
                int sizeInventory = inputInventory.getSizeInventory();

                for (int i = 0; i < sizeInventory; i++) {
                    if (checkIHopper(hopper, inputInventory, i, b)) {
                        return true;
                    }
                }
            }
        } else {
            EntityItem findEntityItem2 = findEntityItem(hopper.getWorld(), hopper.getXPos(), hopper.getYPos() + 1.0, hopper.getZPos());
            if (findEntityItem2 != null) {
                return extract(hopper, findEntityItem2);
            }
        }

        return false;
    }

    public static boolean checkIHopper(IHopper hopper, IInventory inventory, int n, int n2) {
        ItemStack stackInSlot = inventory.getStackInSlot(n);
        if (stackInSlot != null && canExtractItem(inventory, stackInSlot, n, n2)) {
            ItemStack itemStack = stackInSlot.copy();
            ItemStack findItemStack2 = findItemStack(hopper, inventory.decrStackSize(n, 1), -1);
            if (findItemStack2 == null || findItemStack2.stackSize == 0) {
                inventory.markDirty();
                return true;
            }

            inventory.setInventorySlotContents(n, itemStack);
        }

        return false;
    }

    public static boolean extract(IInventory inventory, EntityItem entityItem) {
        boolean flag = false;
        if (entityItem == null) {
            return false;
        }

        ItemStack itemStack = entityItem.getEntityItem().copy();
        ItemStack findItemStack2 = findItemStack(inventory, itemStack, -1);
        if (findItemStack2 != null && findItemStack2.stackSize != 0) {
            entityItem.setEntityItemStack(findItemStack2);
        } else {
            flag = true;
            entityItem.setDead();
        }

        return flag;
    }

    public static ItemStack findItemStack(IInventory inventory, ItemStack itemStack, int n) {
        if (inventory instanceof ISidedInventory && n > -1) {
            ISidedInventory sidedInventory = (ISidedInventory)inventory;
            int[] slotsForFace = sidedInventory.getSlotsForFace(n);

            for (int i = 0; i < slotsForFace.length && itemStack != null && itemStack.stackSize > 0; i++) {
                itemStack = findItemStack(inventory, itemStack, slotsForFace[i], n);
            }
        } else {
            int sizeInventory = inventory.getSizeInventory();

            for (int i = 0; i < sizeInventory && itemStack != null && itemStack.stackSize > 0; i++) {
                itemStack = findItemStack(inventory, itemStack, i, n);
            }
        }

        if (itemStack != null && itemStack.stackSize == 0) {
            itemStack = null;
        }

        return itemStack;
    }

    public static boolean checkIInventory(IInventory inventory, ItemStack itemStack, int n, int n2) {
        return inventory.isItemValidForSlot(n, itemStack) && (!(inventory instanceof ISidedInventory) || ((ISidedInventory)inventory).canInsertItem(n, itemStack, n2));
    }

    public static boolean canExtractItem(IInventory inventory, ItemStack itemStack, int n, int n2) {
        return !(inventory instanceof ISidedInventory) || ((ISidedInventory)inventory).canExtractItem(n, itemStack, n2);
    }

    public static ItemStack findItemStack(IInventory inventory, ItemStack itemStack, int n, int n2) {
        ItemStack stackInSlot = inventory.getStackInSlot(n);
        if (checkIInventory(inventory, itemStack, n, n2)) {
            boolean flag = false;
            if (stackInSlot == null) {
                inventory.setInventorySlotContents(n, itemStack);
                itemStack = null;
                flag = true;
            } else if (canMergeItems(stackInSlot, itemStack)) {
                int n3 = itemStack.getMaxStackSize() - stackInSlot.stackSize;
                int min = Math.min(itemStack.stackSize, n3);
                itemStack.stackSize -= min;
                stackInSlot.stackSize += min;
                flag = min > 0;
            }

            if (flag) {
                if (inventory instanceof TileEntityHopper) {
                    ((TileEntityHopper)inventory).setTransferCooldown(8);
                    inventory.markDirty();
                }

                inventory.markDirty();
            }
        }

        return itemStack;
    }

    public IInventory getOutputInventory() {
        int directionFromMetadata = BlockHopper.getDirectionFromMetadata(this.getBlockMetadata());
        return getInventoryAt(
            this.getWorld(), this.xCoord + Facing.offsetsXForSide[directionFromMetadata], this.yCoord + Facing.offsetsYForSide[directionFromMetadata], this.zCoord + Facing.offsetsZForSide[directionFromMetadata]
        );
    }

    public static IInventory getInputInventory(IHopper hopper) {
        return getInventoryAt(hopper.getWorld(), hopper.getXPos(), hopper.getYPos() + 1.0, hopper.getZPos());
    }

    public static EntityItem findEntityItem(World world, double d, double d2, double d3) {
        List selectEntitiesWithinAABB = world.selectEntitiesWithinAABB(
            EntityItem.class, AxisAlignedBB.getBoundingBox(d, d2, d3, d + 1.0, d2 + 1.0, d3 + 1.0), IEntitySelector.selectAnything
        );
        return selectEntitiesWithinAABB.size() > 0 ? (EntityItem)selectEntitiesWithinAABB.get(0) : null;
    }

    public static IInventory getInventoryAt(World world, double d, double d2, double d3) {
        IInventory inventory = null;
        int n = MathHelper.floor_double(d);
        int n2 = MathHelper.floor_double(d2);
        int n3 = MathHelper.floor_double(d3);
        TileEntity tileEntity = world.getTileEntity(n, n2, n3);
        if (tileEntity != null && tileEntity instanceof IInventory) {
            inventory = (IInventory)tileEntity;
            if (inventory instanceof TileEntityChest) {
                Block block = world.getBlock(n, n2, n3);
                if (block instanceof BlockChest) {
                    inventory = ((BlockChest)block).getInventory(world, n, n2, n3);
                }
            }
        }

        if (inventory == null) {
            List list = world.getEntitiesWithinAABBExcludingEntity(
                null, AxisAlignedBB.getBoundingBox(d, d2, d3, d + 1.0, d2 + 1.0, d3 + 1.0), IEntitySelector.selectInventories
            );
            if (list != null && list.size() > 0) {
                inventory = (IInventory)list.get(world.rand.nextInt(list.size()));
            }
        }

        return inventory;
    }

    public static boolean canMergeItems(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack.getItem() == itemStack2.getItem()
            && itemStack.getMetadata() == itemStack2.getMetadata()
            && itemStack.stackSize <= itemStack.getMaxStackSize()
            && ItemStack.areItemStackTagsEqual(itemStack, itemStack2);
    }

    @Override
    public double getXPos() {
        return this.xCoord;
    }

    @Override
    public double getYPos() {
        return this.yCoord;
    }

    @Override
    public double getZPos() {
        return this.zCoord;
    }

    public void setTransferCooldown(int n) {
        this.transferCooldown = n;
    }

    public boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }
}
