package net.minecraft.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityHopper extends TileEntityLockable implements IHopper, ITickable {
   public ItemStack[] inventory = new ItemStack[5];
   public String customName;
   public int transferCooldown = -1;

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      NBTTagList tagList = compound.getTagList("Items", 10);
      this.inventory = new ItemStack[this.getSizeInventory()];
      if (compound.hasKey("CustomName", 8)) {
         this.customName = compound.getString("CustomName");
      }

      this.transferCooldown = compound.getInteger("TransferCooldown");

      for (int i = 0; i < tagList.tagCount(); i++) {
         NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
         byte byte2 = compoundTagAt.getByte("Slot");
         if (byte2 >= 0 && byte2 < this.inventory.length) {
            this.inventory[byte2] = ItemStack.loadItemStackFromNBT(compoundTagAt);
         }
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      NBTTagList nbttagList = new NBTTagList();

      for (int i = 0; i < this.inventory.length; i++) {
         if (this.inventory[i] != null) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            nbttagCompound.setByte("Slot", (byte)i);
            this.inventory[i].writeToNBT(nbttagCompound);
            nbttagList.appendTag(nbttagCompound);
         }
      }

      compound.setTag("Items", nbttagList);
      compound.setInteger("TransferCooldown", this.transferCooldown);
      if (this.hasCustomName()) {
         compound.setString("CustomName", this.customName);
      }
   }

   @Override
   public void markDirty() {
      super.markDirty();
   }

   @Override
   public int getSizeInventory() {
      return this.inventory.length;
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return this.inventory[index];
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      if (this.inventory[index] != null) {
         if (this.inventory[index].stackSize <= count) {
            ItemStack itemStack = this.inventory[index];
            this.inventory[index] = null;
            return itemStack;
         }

         ItemStack splitStack = this.inventory[index].splitStack(count);
         if (this.inventory[index].stackSize == 0) {
            this.inventory[index] = null;
         }

         return splitStack;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      if (this.inventory[index] != null) {
         ItemStack itemStack = this.inventory[index];
         this.inventory[index] = null;
         return itemStack;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.inventory[index] = stack;
      if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
         stack.stackSize = this.getInventoryStackLimit();
      }
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.hopper";
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null && this.customName.length() > 0;
   }

   public void setCustomName(String customNameIn) {
      this.customName = customNameIn;
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
      return true;
   }

   @Override
   public void update() {
      if (this.worldObj != null && !this.worldObj.isRemote) {
         this.transferCooldown--;
         if (!this.isOnTransferCooldown()) {
            this.setTransferCooldown(0);
            this.updateHopper();
         }
      }
   }

   public boolean updateHopper() {
      if (this.worldObj != null && !this.worldObj.isRemote) {
         if (!this.isOnTransferCooldown() && BlockHopper.isEnabled(this.getBlockMetadata())) {
            boolean flag = false;
            if (!this.isEmpty()) {
               flag = this.transferItemsOut();
            }

            if (!this.isFull()) {
               flag = captureDroppedItems(this) || flag;
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

   public boolean isEmpty() {
      for (ItemStack itemStack : this.inventory) {
         if (itemStack != null) {
            return false;
         }
      }

      return true;
   }

   public boolean isFull() {
      for (ItemStack itemStack : this.inventory) {
         if (itemStack == null || itemStack.stackSize != itemStack.getMaxStackSize()) {
            return false;
         }
      }

      return true;
   }

   public boolean transferItemsOut() {
      IInventory inventoryForHopperTransfer = this.getInventoryForHopperTransfer();
      if (inventoryForHopperTransfer == null) {
         return false;
      }

      EnumFacing opposite = BlockHopper.getFacing(this.getBlockMetadata()).getOpposite();
      if (this.isInventoryFull(inventoryForHopperTransfer, opposite)) {
         return false;
      }

      for (int i = 0; i < this.getSizeInventory(); i++) {
         if (this.getStackInSlot(i) != null) {
            ItemStack itemStack = this.getStackInSlot(i).copy();
            ItemStack putStackInInventoryAllSlots2 = putStackInInventoryAllSlots(inventoryForHopperTransfer, this.decrStackSize(i, 1), opposite);
            if (putStackInInventoryAllSlots2 == null || putStackInInventoryAllSlots2.stackSize == 0) {
               inventoryForHopperTransfer.markDirty();
               return true;
            }

            this.setInventorySlotContents(i, itemStack);
         }
      }

      return false;
   }

   public boolean isInventoryFull(IInventory inventoryIn, EnumFacing side) {
      if (inventoryIn instanceof ISidedInventory) {
         ISidedInventory inventoryIn2 = (ISidedInventory)inventoryIn;
         int[] slotsForFace = inventoryIn2.getSlotsForFace(side);

         for (int i = 0; i < slotsForFace.length; i++) {
            ItemStack stackInSlot = inventoryIn2.getStackInSlot(slotsForFace[i]);
            if (stackInSlot == null || stackInSlot.stackSize != stackInSlot.getMaxStackSize()) {
               return false;
            }
         }
      } else {
         int sizeInventory = inventoryIn.getSizeInventory();

         for (int i = 0; i < sizeInventory; i++) {
            ItemStack stackInSlot = inventoryIn.getStackInSlot(i);
            if (stackInSlot == null || stackInSlot.stackSize != stackInSlot.getMaxStackSize()) {
               return false;
            }
         }
      }

      return true;
   }

   public static boolean isInventoryEmpty(IInventory inventoryIn, EnumFacing side) {
      if (inventoryIn instanceof ISidedInventory) {
         ISidedInventory inventoryIn2 = (ISidedInventory)inventoryIn;
         int[] slotsForFace = inventoryIn2.getSlotsForFace(side);

         for (int i = 0; i < slotsForFace.length; i++) {
            if (inventoryIn2.getStackInSlot(slotsForFace[i]) != null) {
               return false;
            }
         }
      } else {
         int sizeInventory = inventoryIn.getSizeInventory();

         for (int i = 0; i < sizeInventory; i++) {
            if (inventoryIn.getStackInSlot(i) != null) {
               return false;
            }
         }
      }

      return true;
   }

   public static boolean captureDroppedItems(IHopper hopper) {
      IInventory hopperInventory = getHopperInventory(hopper);
      if (hopperInventory != null) {
         EnumFacing enumFacing = EnumFacing.DOWN;
         if (isInventoryEmpty(hopperInventory, enumFacing)) {
            return false;
         }

         if (hopperInventory instanceof ISidedInventory) {
            ISidedInventory sidedInventory = (ISidedInventory)hopperInventory;
            int[] slotsForFace = sidedInventory.getSlotsForFace(enumFacing);

            for (int i = 0; i < slotsForFace.length; i++) {
               if (pullItemFromSlot(hopper, hopperInventory, slotsForFace[i], enumFacing)) {
                  return true;
               }
            }
         } else {
            int sizeInventory = hopperInventory.getSizeInventory();

            for (int i = 0; i < sizeInventory; i++) {
               if (pullItemFromSlot(hopper, hopperInventory, i, enumFacing)) {
                  return true;
               }
            }
         }
      } else {
         for (EntityItem entityItem : getInputItemEntities(hopper.getWorld(), hopper.getXPos(), hopper.getYPos() + 1.0, hopper.getZPos())) {
            if (putDropInInventoryAllSlots(hopper, entityItem)) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean pullItemFromSlot(IHopper hopper, IInventory inventoryIn, int index, EnumFacing direction) {
      ItemStack stackInSlot = inventoryIn.getStackInSlot(index);
      if (stackInSlot != null && canExtractItemFromSlot(inventoryIn, stackInSlot, index, direction)) {
         ItemStack itemStack = stackInSlot.copy();
         ItemStack putStackInInventoryAllSlots2 = putStackInInventoryAllSlots(hopper, inventoryIn.decrStackSize(index, 1), (EnumFacing)null);
         if (putStackInInventoryAllSlots2 == null || putStackInInventoryAllSlots2.stackSize == 0) {
            inventoryIn.markDirty();
            return true;
         }

         inventoryIn.setInventorySlotContents(index, itemStack);
      }

      return false;
   }

   public static boolean putDropInInventoryAllSlots(IInventory inventory, EntityItem itemIn) {
      boolean flag = false;
      if (itemIn == null) {
         return false;
      }

      ItemStack itemStack = itemIn.getEntityItem().copy();
      ItemStack putStackInInventoryAllSlots2 = putStackInInventoryAllSlots(inventory, itemStack, (EnumFacing)null);
      if (putStackInInventoryAllSlots2 != null && putStackInInventoryAllSlots2.stackSize != 0) {
         itemIn.setEntityItemStack(putStackInInventoryAllSlots2);
      } else {
         flag = true;
         itemIn.setDead();
      }

      return flag;
   }

   public static ItemStack putStackInInventoryAllSlots(IInventory inventoryIn, ItemStack stack, EnumFacing side) {
      if (inventoryIn instanceof ISidedInventory && side != null) {
         ISidedInventory inventoryIn2 = (ISidedInventory)inventoryIn;
         int[] slotsForFace = inventoryIn2.getSlotsForFace(side);

         for (int i = 0; i < slotsForFace.length && stack != null && stack.stackSize > 0; i++) {
            stack = insertStack(inventoryIn, stack, slotsForFace[i], side);
         }
      } else {
         int sizeInventory = inventoryIn.getSizeInventory();

         for (int i = 0; i < sizeInventory && stack != null && stack.stackSize > 0; i++) {
            stack = insertStack(inventoryIn, stack, i, side);
         }
      }

      if (stack != null && stack.stackSize == 0) {
         stack = null;
      }

      return stack;
   }

   public static boolean canInsertItemInSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
      return !inventoryIn.isItemValidForSlot(index, stack)
         ? false
         : !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canInsertItem(index, stack, side);
   }

   public static boolean canExtractItemFromSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
      return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canExtractItem(index, stack, side);
   }

   public static ItemStack insertStack(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
      ItemStack stackInSlot = inventoryIn.getStackInSlot(index);
      if (canInsertItemInSlot(inventoryIn, stack, index, side)) {
         boolean flag = false;
         if (stackInSlot == null) {
            inventoryIn.setInventorySlotContents(index, stack);
            stack = null;
            flag = true;
         } else if (canCombine(stackInSlot, stack)) {
            int n = stack.getMaxStackSize() - stackInSlot.stackSize;
            int min = Math.min(stack.stackSize, n);
            stack.stackSize -= min;
            stackInSlot.stackSize += min;
            flag = min > 0;
         }

         if (flag) {
            if (inventoryIn instanceof TileEntityHopper) {
               TileEntityHopper inventoryIn2 = (TileEntityHopper)inventoryIn;
               if (inventoryIn2.mayTransfer()) {
                  inventoryIn2.setTransferCooldown(8);
               }

               inventoryIn.markDirty();
            }

            inventoryIn.markDirty();
         }
      }

      return stack;
   }

   public IInventory getInventoryForHopperTransfer() {
      EnumFacing facing = BlockHopper.getFacing(this.getBlockMetadata());
      return getInventoryAtPosition(
         this.getWorld(), this.pos.getX() + facing.getFrontOffsetX(), this.pos.getY() + facing.getFrontOffsetY(), this.pos.getZ() + facing.getFrontOffsetZ()
      );
   }

   public static IInventory getHopperInventory(IHopper hopper) {
      return getInventoryAtPosition(hopper.getWorld(), hopper.getXPos(), hopper.getYPos() + 1.0, hopper.getZPos());
   }

   public static List<EntityItem> getInputItemEntities(World world, double d, double d2, double d3) {
      return world.getEntitiesWithinAABB(
         EntityItem.class, new AxisAlignedBB(d - 0.5, d2 - 0.5, d3 - 0.5, d + 0.5, d2 + 0.5, d3 + 0.5), EntitySelectors.selectAnything
      );
   }

   public static IInventory getInventoryAtPosition(World worldIn, double x, double y, double z) {
      IInventory inventory = null;
      int n = MathHelper.floor_double(x);
      int n2 = MathHelper.floor_double(y);
      int n3 = MathHelper.floor_double(z);
      BlockPos blockPos = new BlockPos(n, n2, n3);
      Block block = worldIn.getBlockState(blockPos).getBlock();
      if (block.hasTileEntity()) {
         TileEntity tileEntity = worldIn.getTileEntity(blockPos);
         if (tileEntity instanceof IInventory) {
            inventory = (IInventory)tileEntity;
            if (inventory instanceof TileEntityChest && block instanceof BlockChest) {
               inventory = ((BlockChest)block).getLockableContainer(worldIn, blockPos);
            }
         }
      }

      if (inventory == null) {
         List entitiesInAABBexcluding = worldIn.getEntitiesInAABBexcluding(
            (Entity)null, new AxisAlignedBB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntitySelectors.selectInventories
         );
         if (entitiesInAABBexcluding.size() > 0) {
            inventory = (IInventory)entitiesInAABBexcluding.get(worldIn.rand.nextInt(entitiesInAABBexcluding.size()));
         }
      }

      return inventory;
   }

   public static boolean canCombine(ItemStack stack1, ItemStack stack2) {
      return stack1.getItem() != stack2.getItem()
         ? false
         : (
            stack1.getMetadata() != stack2.getMetadata()
               ? false
               : (stack1.stackSize > stack1.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(stack1, stack2))
         );
   }

   @Override
   public double getXPos() {
      return this.pos.getX() + 0.5;
   }

   @Override
   public double getYPos() {
      return this.pos.getY() + 0.5;
   }

   @Override
   public double getZPos() {
      return this.pos.getZ() + 0.5;
   }

   public void setTransferCooldown(int ticks) {
      this.transferCooldown = ticks;
   }

   public boolean isOnTransferCooldown() {
      return this.transferCooldown > 0;
   }

   public boolean mayTransfer() {
      return this.transferCooldown <= 1;
   }

   @Override
   public String getGuiID() {
      return "minecraft:hopper";
   }

   @Override
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerHopper(playerInventory, this, playerIn);
   }

   @Override
   public int getField(int id) {
      return 0;
   }

   @Override
   public void setField(int id, int value) {
   }

   @Override
   public int getFieldCount() {
      return 0;
   }

   @Override
   public void clear() {
      for (int i = 0; i < this.inventory.length; i++) {
         this.inventory[i] = null;
      }
   }
}
