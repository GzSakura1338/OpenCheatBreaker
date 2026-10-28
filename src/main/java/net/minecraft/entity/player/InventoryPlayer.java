package net.minecraft.entity.player;

import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ReportedException;

public class InventoryPlayer implements IInventory {
    public ItemStack[] mainInventory = new ItemStack[36];
    public ItemStack[] armorInventory = new ItemStack[4];
    public int currentItem;
    public ItemStack currentItemStack;
    public EntityPlayer player;
    public ItemStack itemStack;
    public boolean inventoryChanged;

    public InventoryPlayer(EntityPlayer entityPlayer) {
        this.player = entityPlayer;
    }

    public ItemStack getCurrentItem() {
        return this.currentItem < 9 && this.currentItem >= 0 ? this.mainInventory[this.currentItem] : null;
    }

    public static int getHotbarSize() {
        return 9;
    }

    public int getInventorySlotContainItem(Item itemIn) {
        for (int i = 0; i < this.mainInventory.length; i++) {
            if (this.mainInventory[i] != null && this.mainInventory[i].getItem() == itemIn) {
                return i;
            }
        }

        return -1;
    }

    public int getInventorySlotContainItemAndDamage(Item item, int n) {
        for (int i = 0; i < this.mainInventory.length; i++) {
            if (this.mainInventory[i] != null && this.mainInventory[i].getItem() == item && this.mainInventory[i].getMetadata() == n) {
                return i;
            }
        }

        return -1;
    }

    public int storeItemStack(ItemStack itemStack) {
        for (int i = 0; i < this.mainInventory.length; i++) {
            if (this.mainInventory[i] != null
                && this.mainInventory[i].getItem() == itemStack.getItem()
                && this.mainInventory[i].isStackable()
                && this.mainInventory[i].stackSize < this.mainInventory[i].getMaxStackSize()
                && this.mainInventory[i].stackSize < this.getInventoryStackLimit()
                && (!this.mainInventory[i].getHasSubtypes() || this.mainInventory[i].getMetadata() == itemStack.getMetadata())
                && ItemStack.areItemStackTagsEqual(this.mainInventory[i], itemStack)) {
                return i;
            }
        }

        return -1;
    }

    public int getFirstEmptyStack() {
        for (int i = 0; i < this.mainInventory.length; i++) {
            if (this.mainInventory[i] == null) {
                return i;
            }
        }

        return -1;
    }

    public void setCurrentItem(Item item, int n, boolean flag, boolean flag2) {
        boolean flag3 = true;
        this.currentItemStack = this.getCurrentItem();
        int n2;
        if (flag) {
            n2 = this.getInventorySlotContainItemAndDamage(item, n);
        } else {
            n2 = this.getInventorySlotContainItem(item);
        }

        if (n2 >= 0 && n2 < 9) {
            this.currentItem = n2;
        } else if (flag2 && item != null) {
            int firstEmptyStack = this.getFirstEmptyStack();
            if (firstEmptyStack >= 0 && firstEmptyStack < 9) {
                this.currentItem = firstEmptyStack;
            }

            this.handleItem(item, n);
        }
    }

    public void changeCurrentItem(int n) {
        if (n > 0) {
            n = 1;
        }

        if (n < 0) {
            n = -1;
        }

        this.currentItem -= n;

        while (this.currentItem < 0) {
            this.currentItem += 9;
        }

        while (this.currentItem >= 9) {
            this.currentItem -= 9;
        }
    }

    public int clearInventory(Item item, int n) {
        int n2 = 0;

        for (int i = 0; i < this.mainInventory.length; i++) {
            ItemStack itemStack = this.mainInventory[i];
            if (itemStack != null && (item == null || itemStack.getItem() == item) && (n <= -1 || itemStack.getMetadata() == n)) {
                n2 += itemStack.stackSize;
                this.mainInventory[i] = null;
            }
        }

        for (int i = 0; i < this.armorInventory.length; i++) {
            ItemStack itemStack = this.armorInventory[i];
            if (itemStack != null && (item == null || itemStack.getItem() == item) && (n <= -1 || itemStack.getMetadata() == n)) {
                n2 += itemStack.stackSize;
                this.armorInventory[i] = null;
            }
        }

        if (this.itemStack != null) {
            if (item != null && this.itemStack.getItem() != item) {
                return n2;
            }

            if (n > -1 && this.itemStack.getMetadata() != n) {
                return n2;
            }

            n2 += this.itemStack.stackSize;
            this.setItemStack(null);
        }

        return n2;
    }

    public void handleItem(Item item, int n) {
        if (item != null) {
            if (this.currentItemStack != null
                && this.currentItemStack.isItemEnchantable()
                && this.getInventorySlotContainItemAndDamage(this.currentItemStack.getItem(), this.currentItemStack.getCurrentDurability()) == this.currentItem) {
                return;
            }

            int n2 = this.getInventorySlotContainItemAndDamage(item, n);
            if (n2 >= 0) {
                int stackSize = this.mainInventory[n2].stackSize;
                this.mainInventory[n2] = this.mainInventory[this.currentItem];
                this.mainInventory[this.currentItem] = new ItemStack(item, stackSize, n);
            } else {
                this.mainInventory[this.currentItem] = new ItemStack(item, 1, n);
            }
        }
    }

    public int storePartialItemStack(ItemStack itemStack) {
        Item item = itemStack.getItem();
        int stackSize = itemStack.stackSize;
        if (itemStack.getMaxStackSize() == 1) {
            int firstEmptyStack = this.getFirstEmptyStack();
            if (firstEmptyStack < 0) {
                return stackSize;
            }

            if (this.mainInventory[firstEmptyStack] == null) {
                this.mainInventory[firstEmptyStack] = ItemStack.copyItemStack(itemStack);
            }

            return 0;
        } else {
            int storeItemStack = this.storeItemStack(itemStack);
            if (storeItemStack < 0) {
                storeItemStack = this.getFirstEmptyStack();
            }

            if (storeItemStack < 0) {
                return stackSize;
            }

            if (this.mainInventory[storeItemStack] == null) {
                this.mainInventory[storeItemStack] = new ItemStack(item, 0, itemStack.getMetadata());
                if (itemStack.hasTagCompound()) {
                    this.mainInventory[storeItemStack].setTagCompound((NBTTagCompound)itemStack.getTagCompound().copy());
                }
            }

            int n = stackSize;
            if (stackSize > this.mainInventory[storeItemStack].getMaxStackSize() - this.mainInventory[storeItemStack].stackSize) {
                n = this.mainInventory[storeItemStack].getMaxStackSize() - this.mainInventory[storeItemStack].stackSize;
            }

            if (n > this.getInventoryStackLimit() - this.mainInventory[storeItemStack].stackSize) {
                n = this.getInventoryStackLimit() - this.mainInventory[storeItemStack].stackSize;
            }

            if (n == 0) {
                return stackSize;
            }

            stackSize -= n;
            this.mainInventory[storeItemStack].stackSize += n;
            this.mainInventory[storeItemStack].animationsToGo = 5;
            return stackSize;
        }
    }

    public void decrementAnimations() {
        for (int i = 0; i < this.mainInventory.length; i++) {
            if (this.mainInventory[i] != null) {
                this.mainInventory[i].updateAnimation(this.player.worldObj, this.player, i, this.currentItem == i);
            }
        }
    }

    public boolean consumeInventoryItem(Item item) {
        int inventorySlotContainItem = this.getInventorySlotContainItem(item);
        if (inventorySlotContainItem < 0) {
            return false;
        }

        if (--this.mainInventory[inventorySlotContainItem].stackSize <= 0) {
            this.mainInventory[inventorySlotContainItem] = null;
        }

        return true;
    }

    public boolean hasItem(Item item) {
        int inventorySlotContainItem = this.getInventorySlotContainItem(item);
        return inventorySlotContainItem >= 0;
    }

    public boolean addItemStackToInventory(final ItemStack itemStack) {
        if (itemStack != null && itemStack.stackSize != 0 && itemStack.getItem() != null) {
            try {
                if (itemStack.isItemDamaged()) {
                    int firstEmptyStack = this.getFirstEmptyStack();
                    if (firstEmptyStack >= 0) {
                        this.mainInventory[firstEmptyStack] = ItemStack.copyItemStack(itemStack);
                        this.mainInventory[firstEmptyStack].animationsToGo = 5;
                        itemStack.stackSize = 0;
                        return true;
                    } else if (this.player.capabilities.isCreativeMode) {
                        itemStack.stackSize = 0;
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    int n;
                    do {
                        n = itemStack.stackSize;
                        itemStack.stackSize = this.storePartialItemStack(itemStack);
                    } while (itemStack.stackSize > 0 && itemStack.stackSize < n);

                    if (itemStack.stackSize == n && this.player.capabilities.isCreativeMode) {
                        itemStack.stackSize = 0;
                        return true;
                    } else {
                        return itemStack.stackSize < n;
                    }
                }
            } catch (Throwable e) {
                CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Adding item to inventory");
                CrashReportCategory makeCategory = makeCrashReport.makeCategory("Item being added");
                makeCategory.addCrashSection("Item ID", Item.getIdFromItem(itemStack.getItem()));
                makeCategory.addCrashSection("Item data", itemStack.getMetadata());
                makeCategory.addCrashSectionCallable("Item name", new Callable() {
                    public String call() {
                        return itemStack.getDisplayName();
                    }
                });
                throw new ReportedException(makeCrashReport);
            }
        } else {
            return false;
        }
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack[] mainInventory = this.mainInventory;
        if (index >= this.mainInventory.length) {
            mainInventory = this.armorInventory;
            index -= this.mainInventory.length;
        }

        if (mainInventory[index] != null) {
            if (mainInventory[index].stackSize <= count) {
                ItemStack itemStack = mainInventory[index];
                mainInventory[index] = null;
                return itemStack;
            }

            ItemStack splitStack = mainInventory[index].splitStack(count);
            if (mainInventory[index].stackSize == 0) {
                mainInventory[index] = null;
            }

            return splitStack;
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        ItemStack[] mainInventory = this.mainInventory;
        if (index >= this.mainInventory.length) {
            mainInventory = this.armorInventory;
            index -= this.mainInventory.length;
        }

        if (mainInventory[index] != null) {
            ItemStack itemStack = mainInventory[index];
            mainInventory[index] = null;
            return itemStack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack[] mainInventory = this.mainInventory;
        if (index >= mainInventory.length) {
            index -= mainInventory.length;
            mainInventory = this.armorInventory;
        }

        mainInventory[index] = stack;
    }

    public float getStrVsBlock(Block block) {
        float f = 1.0F;
        if (this.mainInventory[this.currentItem] != null) {
            f *= this.mainInventory[this.currentItem].getStrVsBlock(block);
        }

        return f;
    }

    public NBTTagList writeToNBT(NBTTagList nbttagList) {
        for (int i = 0; i < this.mainInventory.length; i++) {
            if (this.mainInventory[i] != null) {
                NBTTagCompound nbttagCompound = new NBTTagCompound();
                nbttagCompound.setByte("Slot", (byte)i);
                this.mainInventory[i].writeToNBT(nbttagCompound);
                nbttagList.appendTag(nbttagCompound);
            }
        }

        for (int i = 0; i < this.armorInventory.length; i++) {
            if (this.armorInventory[i] != null) {
                NBTTagCompound nbttagCompound = new NBTTagCompound();
                nbttagCompound.setByte("Slot", (byte)(i + 100));
                this.armorInventory[i].writeToNBT(nbttagCompound);
                nbttagList.appendTag(nbttagCompound);
            }
        }

        return nbttagList;
    }

    public void readFromNBT(NBTTagList nbttagList) {
        this.mainInventory = new ItemStack[36];
        this.armorInventory = new ItemStack[4];

        for (int i = 0; i < nbttagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = nbttagList.getCompoundTagAt(i);
            int n = compoundTagAt.getByte("Slot") & 255;
            ItemStack loadItemStackFromNBT = ItemStack.loadItemStackFromNBT(compoundTagAt);
            if (loadItemStackFromNBT != null) {
                if (n >= 0 && n < this.mainInventory.length) {
                    this.mainInventory[n] = loadItemStackFromNBT;
                }

                if (n >= 100 && n < this.armorInventory.length + 100) {
                    this.armorInventory[n - 100] = loadItemStackFromNBT;
                }
            }
        }
    }

    @Override
    public int getSizeInventory() {
        return this.mainInventory.length + 4;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        ItemStack[] mainInventory = this.mainInventory;
        if (slotIn >= mainInventory.length) {
            slotIn -= mainInventory.length;
            mainInventory = this.armorInventory;
        }

        return mainInventory[slotIn];
    }

    @Override
    public String getInventoryName() {
        return "container.inventory";
    }

    @Override
    public boolean isCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean canToolBreak(Block block) {
        if (block.getMaterial().isToolNotRequired()) {
            return true;
        }

        ItemStack stackInSlot = this.getStackInSlot(this.currentItem);
        return stackInSlot != null && stackInSlot.canItemHarvestBlock(block);
    }

    public ItemStack armorItemInSlot(int n) {
        return this.armorInventory[n];
    }

    public int getTotalArmorValue() {
        int n = 0;

        for (int i = 0; i < this.armorInventory.length; i++) {
            if (this.armorInventory[i] != null && this.armorInventory[i].getItem() instanceof ItemArmor) {
                int damageReduceAmount = ((ItemArmor)this.armorInventory[i].getItem()).damageReduceAmount;
                n += damageReduceAmount;
            }
        }

        return n;
    }

    public void damageArmor(float f) {
        f /= 4.0F;
        if (f < 1.0F) {
            f = 1.0F;
        }

        for (int i = 0; i < this.armorInventory.length; i++) {
            if (this.armorInventory[i] != null && this.armorInventory[i].getItem() instanceof ItemArmor) {
                this.armorInventory[i].damageItem((int)f, this.player);
                if (this.armorInventory[i].stackSize == 0) {
                    this.armorInventory[i] = null;
                }
            }
        }
    }

    public void dropAllItems() {
        for (int i = 0; i < this.mainInventory.length; i++) {
            if (this.mainInventory[i] != null) {
                this.player.dropStack(this.mainInventory[i], true, false);
                this.mainInventory[i] = null;
            }
        }

        for (int i = 0; i < this.armorInventory.length; i++) {
            if (this.armorInventory[i] != null) {
                this.player.dropStack(this.armorInventory[i], true, false);
                this.armorInventory[i] = null;
            }
        }
    }

    @Override
    public void markDirty() {
        this.inventoryChanged = true;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return !this.player.isDead && player.getDistanceSqToEntity(this.player) <= 64.0;
    }

    public boolean hasItemStack(ItemStack itemStack) {
        for (int i = 0; i < this.armorInventory.length; i++) {
            if (this.armorInventory[i] != null && this.armorInventory[i].isItemEqual(itemStack)) {
                return true;
            }
        }

        for (int i = 0; i < this.mainInventory.length; i++) {
            if (this.mainInventory[i] != null && this.mainInventory[i].isItemEqual(itemStack)) {
                return true;
            }
        }

        return false;
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

    public void copyInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < this.mainInventory.length; i++) {
            this.mainInventory[i] = ItemStack.copyItemStack(inventoryPlayer.mainInventory[i]);
        }

        for (int i = 0; i < this.armorInventory.length; i++) {
            this.armorInventory[i] = ItemStack.copyItemStack(inventoryPlayer.armorInventory[i]);
        }

        this.currentItem = inventoryPlayer.currentItem;
    }
}
