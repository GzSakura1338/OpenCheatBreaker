package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TileEntityFurnace extends TileEntity implements ISidedInventory {
    public static int[] slotsTop = new int[]{0};
    public static int[] slotsBottom = new int[]{2, 1};
    public static int[] slotsSides = new int[]{1};
    public ItemStack[] furnaceItemStacks = new ItemStack[3];
    public int furnaceBurnTime;
    public int currentItemBurnTime;
    public int furnaceCookTime;
    public String furnaceCustomName;

    @Override
    public int getSizeInventory() {
        return this.furnaceItemStacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return this.furnaceItemStacks[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (this.furnaceItemStacks[index] != null) {
            if (this.furnaceItemStacks[index].stackSize <= count) {
                ItemStack itemStack = this.furnaceItemStacks[index];
                this.furnaceItemStacks[index] = null;
                return itemStack;
            }

            ItemStack splitStack = this.furnaceItemStacks[index].splitStack(count);
            if (this.furnaceItemStacks[index].stackSize == 0) {
                this.furnaceItemStacks[index] = null;
            }

            return splitStack;
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (this.furnaceItemStacks[index] != null) {
            ItemStack itemStack = this.furnaceItemStacks[index];
            this.furnaceItemStacks[index] = null;
            return itemStack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.furnaceItemStacks[index] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public String getInventoryName() {
        return this.isCustomInventoryName() ? this.furnaceCustomName : "container.furnace";
    }

    @Override
    public boolean isCustomInventoryName() {
        return this.furnaceCustomName != null && this.furnaceCustomName.length() > 0;
    }

    public void setCustomInventoryName(String str) {
        this.furnaceCustomName = str;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList tagList = compound.getTagList("Items", 10);
        this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
            byte byte2 = compoundTagAt.getByte("Slot");
            if (byte2 >= 0 && byte2 < this.furnaceItemStacks.length) {
                this.furnaceItemStacks[byte2] = ItemStack.loadItemStackFromNBT(compoundTagAt);
            }
        }

        this.furnaceBurnTime = compound.getShort("BurnTime");
        this.furnaceCookTime = compound.getShort("CookTime");
        this.currentItemBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);
        if (compound.hasKey("CustomName", 8)) {
            this.furnaceCustomName = compound.getString("CustomName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setShort("BurnTime", (short)this.furnaceBurnTime);
        compound.setShort("CookTime", (short)this.furnaceCookTime);
        NBTTagList nbttagList = new NBTTagList();

        for (int i = 0; i < this.furnaceItemStacks.length; i++) {
            if (this.furnaceItemStacks[i] != null) {
                NBTTagCompound nbttagCompound = new NBTTagCompound();
                nbttagCompound.setByte("Slot", (byte)i);
                this.furnaceItemStacks[i].writeToNBT(nbttagCompound);
                nbttagList.appendTag(nbttagCompound);
            }
        }

        compound.setTag("Items", nbttagList);
        if (this.isCustomInventoryName()) {
            compound.setString("CustomName", this.furnaceCustomName);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    public int getCookProgressScaled(int n) {
        return this.furnaceCookTime * n / 200;
    }

    public int getBurnTimeRemainingScaled(int n) {
        if (this.currentItemBurnTime == 0) {
            this.currentItemBurnTime = 200;
        }

        return this.furnaceBurnTime * n / this.currentItemBurnTime;
    }

    public boolean isBurning() {
        return this.furnaceBurnTime > 0;
    }

    @Override
    public void updateEntity() {
        boolean flag = this.furnaceBurnTime > 0;
        boolean flag2 = false;
        if (this.furnaceBurnTime > 0) {
            this.furnaceBurnTime--;
        }

        if (!this.worldObj.isRemote) {
            if (this.furnaceBurnTime != 0 || this.furnaceItemStacks[1] != null && this.furnaceItemStacks[0] != null) {
                if (this.furnaceBurnTime == 0 && this.canSmelt()) {
                    this.currentItemBurnTime = this.furnaceBurnTime = getItemBurnTime(this.furnaceItemStacks[1]);
                    if (this.furnaceBurnTime > 0) {
                        flag2 = true;
                        if (this.furnaceItemStacks[1] != null) {
                            this.furnaceItemStacks[1].stackSize--;
                            if (this.furnaceItemStacks[1].stackSize == 0) {
                                Item containerItem = this.furnaceItemStacks[1].getItem().getContainerItem();
                                this.furnaceItemStacks[1] = containerItem != null ? new ItemStack(containerItem) : null;
                            }
                        }
                    }
                }

                if (this.isBurning() && this.canSmelt()) {
                    this.furnaceCookTime++;
                    if (this.furnaceCookTime == 200) {
                        this.furnaceCookTime = 0;
                        this.smeltItem();
                        flag2 = true;
                    }
                } else {
                    this.furnaceCookTime = 0;
                }
            }

            if (flag != this.furnaceBurnTime > 0) {
                flag2 = true;
                BlockFurnace.updateFurnaceBlockState(this.furnaceBurnTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            }
        }

        if (flag2) {
            this.markDirty();
        }
    }

    public boolean canSmelt() {
        if (this.furnaceItemStacks[0] == null) {
            return false;
        }

        ItemStack smeltingResult = FurnaceRecipes.instance().getSmeltingResult(this.furnaceItemStacks[0]);
        return smeltingResult != null
            && (
                this.furnaceItemStacks[2] == null
                    || this.furnaceItemStacks[2].isItemEqual(smeltingResult)
                        && (
                            this.furnaceItemStacks[2].stackSize < this.getInventoryStackLimit()
                                    && this.furnaceItemStacks[2].stackSize < this.furnaceItemStacks[2].getMaxStackSize()
                                || this.furnaceItemStacks[2].stackSize < smeltingResult.getMaxStackSize()
                        )
            );
    }

    public void smeltItem() {
        if (this.canSmelt()) {
            ItemStack smeltingResult = FurnaceRecipes.instance().getSmeltingResult(this.furnaceItemStacks[0]);
            if (this.furnaceItemStacks[2] == null) {
                this.furnaceItemStacks[2] = smeltingResult.copy();
            } else if (this.furnaceItemStacks[2].getItem() == smeltingResult.getItem()) {
                this.furnaceItemStacks[2].stackSize++;
            }

            this.furnaceItemStacks[0].stackSize--;
            if (this.furnaceItemStacks[0].stackSize <= 0) {
                this.furnaceItemStacks[0] = null;
            }
        }
    }

    public static int getItemBurnTime(ItemStack itemStack) {
        if (itemStack == null) {
            return 0;
        }

        Item item = itemStack.getItem();
        if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air) {
            Block blockFromItem = Block.getBlockFromItem(item);
            if (blockFromItem == Blocks.wooden_slab) {
                return 150;
            }

            if (blockFromItem.getMaterial() == Material.wood) {
                return 300;
            }

            if (blockFromItem == Blocks.coal_block) {
                return 16000;
            }
        }

        return item instanceof ItemTool && ((ItemTool)item).getToolMaterialName().equals("WOOD")
            ? 200
            : (
                item instanceof ItemSword && ((ItemSword)item).getToolMaterialName().equals("WOOD")
                    ? 200
                    : (
                        item instanceof ItemHoe && ((ItemHoe)item).getMaterialName().equals("WOOD")
                            ? 200
                            : (
                                item == Items.stick
                                    ? 100
                                    : (
                                        item == Items.coal
                                            ? 1600
                                            : (
                                                item == Items.lava_bucket
                                                    ? 20000
                                                    : (item == Item.getItemFromBlock(Blocks.sapling) ? 100 : (item == Items.blaze_rod ? 2400 : 0))
                                            )
                                    )
                            )
                    )
            );
    }

    public static boolean isItemFuel(ItemStack itemStack) {
        return getItemBurnTime(itemStack) > 0;
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
        return index != 2 && (index != 1 || isItemFuel(stack));
    }

    @Override
    public int[] getSlotsForFace(int n) {
        return n == 0 ? slotsBottom : (n == 1 ? slotsTop : slotsSides);
    }

    @Override
    public boolean canInsertItem(int n, ItemStack itemStack, int n2) {
        return this.isItemValidForSlot(n, itemStack);
    }

    @Override
    public boolean canExtractItem(int n, ItemStack itemStack, int n2) {
        return n2 != 0 || n != 1 || itemStack.getItem() == Items.bucket;
    }
}
