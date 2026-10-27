package net.minecraft.tileentity;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntityBrewingStand extends TileEntityLockable implements ISidedInventory, ITickable {
   public static int[] inputSlots = new int[]{3};
   public static int[] outputSlots = new int[]{0, 1, 2};
   public ItemStack[] brewingItemStacks = new ItemStack[4];
   public int brewTime;
   public boolean[] filledSlots;
   public Item ingredientID;
   public String customName;

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.brewing";
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null && this.customName.length() > 0;
   }

   public void setName(String name) {
      this.customName = name;
   }

   @Override
   public int getSizeInventory() {
      return this.brewingItemStacks.length;
   }

   @Override
   public void update() {
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

      if (!this.worldObj.isRemote) {
         boolean[] slotsEmpty = this.getSlotsEmpty();
         if (!Arrays.equals(slotsEmpty, this.filledSlots)) {
            this.filledSlots = slotsEmpty;
            IBlockState blockState = this.worldObj.getBlockState(this.getPos());
            if (!(blockState.getBlock() instanceof BlockBrewingStand)) {
               return;
            }

            for (int i = 0; i < BlockBrewingStand.HAS_BOTTLE.length; i++) {
               blockState = blockState.withProperty(BlockBrewingStand.HAS_BOTTLE[i], slotsEmpty[i]);
            }

            this.worldObj.setBlockState(this.pos, blockState, 2);
         }
      }
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
               int potionResult = this.getPotionResult(metadata, itemStack);
               if (!ItemPotion.isSplash(metadata) && ItemPotion.isSplash(potionResult)) {
                  flag = true;
                  break;
               }

               List effects = Items.potionitem.getEffects(metadata);
               List effects2 = Items.potionitem.getEffects(potionResult);
               if ((metadata <= 0 || effects != effects2) && (effects == null || !effects.equals(effects2) && effects2 != null) && metadata != potionResult) {
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
               int potionResult = this.getPotionResult(metadata, itemStack);
               List effects = Items.potionitem.getEffects(metadata);
               List effects2 = Items.potionitem.getEffects(potionResult);
               if (metadata > 0 && effects == effects2 || effects != null && (effects.equals(effects2) || effects2 == null)) {
                  if (!ItemPotion.isSplash(metadata) && ItemPotion.isSplash(potionResult)) {
                     this.brewingItemStacks[i].setItemDamage(potionResult);
                  }
               } else if (metadata != potionResult) {
                  this.brewingItemStacks[i].setItemDamage(potionResult);
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

   public int getPotionResult(int meta, ItemStack stack) {
      return stack == null
         ? meta
         : (stack.getItem().isPotionIngredient(stack) ? PotionHelper.applyIngredient(meta, stack.getItem().getPotionEffect(stack)) : meta);
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
      if (this.hasCustomName()) {
         compound.setString("CustomName", this.customName);
      }
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return index >= 0 && index < this.brewingItemStacks.length ? this.brewingItemStacks[index] : null;
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
   public ItemStack removeStackFromSlot(int index) {
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
      return this.worldObj.getTileEntity(this.pos) != this
         ? false
         : player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
   }

   @Override
   public void openInventory(EntityPlayer player) {
   }

   @Override
   public void closeInventory(EntityPlayer player) {
   }

   @Override
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return index == 3 ? stack.getItem().isPotionIngredient(stack) : stack.getItem() == Items.potionitem || stack.getItem() == Items.glass_bottle;
   }

   public boolean[] getSlotsEmpty() {
      boolean[] flags = new boolean[3];

      for (int i = 0; i < 3; i++) {
         if (this.brewingItemStacks[i] != null) {
            flags[i] = true;
         }
      }

      return flags;
   }

   @Override
   public int[] getSlotsForFace(EnumFacing side) {
      return side == EnumFacing.UP ? inputSlots : outputSlots;
   }

   @Override
   public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
      return this.isItemValidForSlot(index, itemStackIn);
   }

   @Override
   public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
      return true;
   }

   @Override
   public String getGuiID() {
      return "minecraft:brewing_stand";
   }

   @Override
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerBrewingStand(playerInventory, this);
   }

   @Override
   public int getField(int id) {
      switch (id) {
         case 0:
            return this.brewTime;
         default:
            return 0;
      }
   }

   @Override
   public void setField(int id, int value) {
      switch (id) {
         case 0:
            this.brewTime = value;
      }
   }

   @Override
   public int getFieldCount() {
      return 1;
   }

   @Override
   public void clear() {
      for (int i = 0; i < this.brewingItemStacks.length; i++) {
         this.brewingItemStacks[i] = null;
      }
   }
}
