package net.minecraft.tileentity;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionHelper;

public class TileEntityBrewingStand extends TileEntity implements ISidedInventory {
    public static int[] inputSlots = new int[]{3};
    public static int[] outputSlots = new int[]{0, 1, 2};
    public ItemStack[] brewingItemStacks = new ItemStack[4];
    public int brewTime;
    public int filledSlots;
    public Item ingredientID;
    public String customName;

    @Override
    public String getInventoryName() {
        return this.isCustomInventoryName() ? this.customName : "container.brewing";
    }

    @Override
    public boolean isCustomInventoryName() {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setCustomName(String str) {
        this.customName = str;
    }

    @Override
    public int getSizeInventory() {
        return this.brewingItemStacks.length;
    }

    @Override
    public void updateEntity() {
        if (this.brewTime > 0) {
            this.brewTime--;
            if (this.brewTime == 0) {
                this.brewPotions();
                this.markDirty();
            } else if (!this.canBrew()) {
                this.brewTime = 0;
                this.markDirty();
            } else if (this.ingredientID != this.brewingItemStacks[3].getItem()) {
                this.brewTime = 0;
                this.markDirty();
            }
        } else if (this.canBrew()) {
            this.brewTime = 400;
            this.ingredientID = this.brewingItemStacks[3].getItem();
        }

        int filledSlots = this.getFilledSlots();
        if (filledSlots != this.filledSlots) {
            this.filledSlots = filledSlots;
            this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, filledSlots, 2);
        }

        super.updateEntity();
    }

    public int getBrewTime() {
        return this.brewTime;
    }

    public boolean canBrew() {
        if (this.brewingItemStacks[3] != null && this.brewingItemStacks[3].stackSize > 0) {
            ItemStack itemStack = this.brewingItemStacks[3];
            if (!itemStack.getItem().isPotionIngredient(itemStack)) {
                return false;
            }

            boolean flag = false;

            for (int i = 0; i < 3; i++) {
                if (this.brewingItemStacks[i] != null && this.brewingItemStacks[i].getItem() == Items.potionitem) {
                    int metadata = this.brewingItemStacks[i].getMetadata();
                    int brewEffectData = this.getBrewEffectData(metadata, itemStack);
                    if (!ItemPotion.isSplash(metadata) && ItemPotion.isSplash(brewEffectData)) {
                        flag = true;
                        break;
                    }

                    List effects = Items.potionitem.getEffects(metadata);
                    List effects2 = Items.potionitem.getEffects(brewEffectData);
                    if ((metadata <= 0 || effects != effects2) && (effects == null || !effects.equals(effects2) && effects2 != null) && metadata != brewEffectData) {
                        flag = true;
                        break;
                    }
                }
            }

            return flag;
        } else {
            return false;
        }
    }

    public void brewPotions() {
        if (this.canBrew()) {
            ItemStack itemStack = this.brewingItemStacks[3];

            for (int i = 0; i < 3; i++) {
                if (this.brewingItemStacks[i] != null && this.brewingItemStacks[i].getItem() == Items.potionitem) {
                    int metadata = this.brewingItemStacks[i].getMetadata();
                    int brewEffectData = this.getBrewEffectData(metadata, itemStack);
                    List effects = Items.potionitem.getEffects(metadata);
                    List effects2 = Items.potionitem.getEffects(brewEffectData);
                    if ((metadata <= 0 || effects != effects2) && (effects == null || !effects.equals(effects2) && effects2 != null)) {
                        if (metadata != brewEffectData) {
                            this.brewingItemStacks[i].setMetadata(brewEffectData);
                        }
                    } else if (!ItemPotion.isSplash(metadata) && ItemPotion.isSplash(brewEffectData)) {
                        this.brewingItemStacks[i].setMetadata(brewEffectData);
                    }
                }
            }

            if (itemStack.getItem().hasContainerItem()) {
                this.brewingItemStacks[3] = new ItemStack(itemStack.getItem().getContainerItem());
            } else {
                this.brewingItemStacks[3].stackSize--;
                if (this.brewingItemStacks[3].stackSize <= 0) {
                    this.brewingItemStacks[3] = null;
                }
            }
        }
    }

    public int getBrewEffectData(int n, ItemStack itemStack) {
        return itemStack == null ? n : (itemStack.getItem().isPotionIngredient(itemStack) ? PotionHelper.applyIngredient(n, itemStack.getItem().getPotionEffect(itemStack)) : n);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList tagList = compound.getTagList("Items", 10);
        this.brewingItemStacks = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
            byte byte2 = compoundTagAt.getByte("Slot");
            if (byte2 >= 0 && byte2 < this.brewingItemStacks.length) {
                this.brewingItemStacks[byte2] = ItemStack.loadItemStackFromNBT(compoundTagAt);
            }
        }

        this.brewTime = compound.getShort("BrewTime");
        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setShort("BrewTime", (short)this.brewTime);
        NBTTagList nbttagList = new NBTTagList();

        for (int i = 0; i < this.brewingItemStacks.length; i++) {
            if (this.brewingItemStacks[i] != null) {
                NBTTagCompound nbttagCompound = new NBTTagCompound();
                nbttagCompound.setByte("Slot", (byte)i);
                this.brewingItemStacks[i].writeToNBT(nbttagCompound);
                nbttagList.appendTag(nbttagCompound);
            }
        }

        compound.setTag("Items", nbttagList);
        if (this.isCustomInventoryName()) {
            compound.setString("CustomName", this.customName);
        }
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return slotIn >= 0 && slotIn < this.brewingItemStacks.length ? this.brewingItemStacks[slotIn] : null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index >= 0 && index < this.brewingItemStacks.length) {
            ItemStack itemStack = this.brewingItemStacks[index];
            this.brewingItemStacks[index] = null;
            return itemStack;
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (index >= 0 && index < this.brewingItemStacks.length) {
            ItemStack itemStack = this.brewingItemStacks[index];
            this.brewingItemStacks[index] = null;
            return itemStack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index >= 0 && index < this.brewingItemStacks.length) {
            this.brewingItemStacks[index] = stack;
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
    public void openChest() {
    }

    @Override
    public void closeChest() {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 3 ? stack.getItem().isPotionIngredient(stack) : stack.getItem() == Items.potionitem || stack.getItem() == Items.glass_bottle;
    }

    public void setBrewTime(int n) {
        this.brewTime = n;
    }

    public int getFilledSlots() {
        int n = 0;

        for (int i = 0; i < 3; i++) {
            if (this.brewingItemStacks[i] != null) {
                n |= 1 << i;
            }
        }

        return n;
    }

    @Override
    public int[] getSlotsForFace(int n) {
        return n == 1 ? inputSlots : outputSlots;
    }

    @Override
    public boolean canInsertItem(int n, ItemStack itemStack, int n2) {
        return this.isItemValidForSlot(n, itemStack);
    }

    @Override
    public boolean canExtractItem(int n, ItemStack itemStack, int n2) {
        return true;
    }
}
