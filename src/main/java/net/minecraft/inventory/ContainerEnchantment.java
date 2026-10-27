package net.minecraft.inventory;

import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ContainerEnchantment extends Container {
   public IInventory tableInventory = new InventoryBasic("Enchant", true, 2) {
      @Override
      public int getInventoryStackLimit() {
         return 64;
      }

      @Override
      public void markDirty() {
         super.markDirty();
         ContainerEnchantment.this.onCraftMatrixChanged(this);
      }
   };
   public Random rand = new Random();
   public int[] enchantLevels = new int[3];
   public int[] enchantmentIds = new int[]{-1, -1, -1};
   public World worldPointer;
   public BlockPos position;
   public int xpSeed;

   public ContainerEnchantment(InventoryPlayer playerInv, World worldIn) {
      this(playerInv, worldIn, BlockPos.ORIGIN);
   }

   public ContainerEnchantment(InventoryPlayer playerInv, World worldIn, BlockPos pos) {
      this.worldPointer = worldIn;
      this.position = pos;
      this.xpSeed = playerInv.player.getXPSeed();
      this.addSlotToContainer(new Slot(this.tableInventory, 0, 15, 47) {
         @Override
         public boolean isItemValid(ItemStack stack) {
            return true;
         }

         @Override
         public int getSlotStackLimit() {
            return 1;
         }
      });
      this.addSlotToContainer(new Slot(this.tableInventory, 1, 35, 47) {
         @Override
         public boolean isItemValid(ItemStack stack) {
            return stack.getItem() == Items.dye && EnumDyeColor.byDyeDamage(stack.getMetadata()) == EnumDyeColor.BLUE;
         }
      });

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
      }
   }

   @Override
   public void onCraftGuiOpened(ICrafting listener) {
      super.onCraftGuiOpened(listener);
      listener.sendProgressBarUpdate(this, 0, this.enchantLevels[0]);
      listener.sendProgressBarUpdate(this, 1, this.enchantLevels[1]);
      listener.sendProgressBarUpdate(this, 2, this.enchantLevels[2]);
      listener.sendProgressBarUpdate(this, 3, this.xpSeed & -16);
      listener.sendProgressBarUpdate(this, 4, this.enchantmentIds[0]);
      listener.sendProgressBarUpdate(this, 5, this.enchantmentIds[1]);
      listener.sendProgressBarUpdate(this, 6, this.enchantmentIds[2]);
   }

   @Override
   public void detectAndSendChanges() {
      super.detectAndSendChanges();

      for (int i = 0; i < this.crafters.size(); i++) {
         ICrafting crafting = this.crafters.get(i);
         crafting.sendProgressBarUpdate(this, 0, this.enchantLevels[0]);
         crafting.sendProgressBarUpdate(this, 1, this.enchantLevels[1]);
         crafting.sendProgressBarUpdate(this, 2, this.enchantLevels[2]);
         crafting.sendProgressBarUpdate(this, 3, this.xpSeed & -16);
         crafting.sendProgressBarUpdate(this, 4, this.enchantmentIds[0]);
         crafting.sendProgressBarUpdate(this, 5, this.enchantmentIds[1]);
         crafting.sendProgressBarUpdate(this, 6, this.enchantmentIds[2]);
      }
   }

   @Override
   public void updateProgressBar(int id, int data) {
      if (id >= 0 && id <= 2) {
         this.enchantLevels[id] = data;
      } else if (id == 3) {
         this.xpSeed = data;
      } else if (id >= 4 && id <= 6) {
         this.enchantmentIds[id - 4] = data;
      } else {
         super.updateProgressBar(id, data);
      }
   }

   @Override
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      if (inventoryIn == this.tableInventory) {
         ItemStack stackInSlot = inventoryIn.getStackInSlot(0);
         if (stackInSlot != null && stackInSlot.isItemEnchantable()) {
            if (!this.worldPointer.isRemote) {
               int n = 0;

               for (int i = -1; i <= 1; i++) {
                  for (int j = -1; j <= 1; j++) {
                     if ((i != 0 || j != 0)
                        && this.worldPointer.isAirBlock(this.position.add(j, 0, i))
                        && this.worldPointer.isAirBlock(this.position.add(j, 1, i))) {
                        if (this.worldPointer.getBlockState(this.position.add(j * 2, 0, i * 2)).getBlock() == Blocks.bookshelf) {
                           n++;
                        }

                        if (this.worldPointer.getBlockState(this.position.add(j * 2, 1, i * 2)).getBlock() == Blocks.bookshelf) {
                           n++;
                        }

                        if (j != 0 && i != 0) {
                           if (this.worldPointer.getBlockState(this.position.add(j * 2, 0, i)).getBlock() == Blocks.bookshelf) {
                              n++;
                           }

                           if (this.worldPointer.getBlockState(this.position.add(j * 2, 1, i)).getBlock() == Blocks.bookshelf) {
                              n++;
                           }

                           if (this.worldPointer.getBlockState(this.position.add(j, 0, i * 2)).getBlock() == Blocks.bookshelf) {
                              n++;
                           }

                           if (this.worldPointer.getBlockState(this.position.add(j, 1, i * 2)).getBlock() == Blocks.bookshelf) {
                              n++;
                           }
                        }
                     }
                  }
               }

               this.rand.setSeed(this.xpSeed);

               for (int i = 0; i < 3; i++) {
                  this.enchantLevels[i] = EnchantmentHelper.calcItemStackEnchantability(this.rand, i, n, stackInSlot);
                  this.enchantmentIds[i] = -1;
                  if (this.enchantLevels[i] < i + 1) {
                     this.enchantLevels[i] = 0;
                  }
               }

               for (int i = 0; i < 3; i++) {
                  if (this.enchantLevels[i] > 0) {
                     List randomEnchantments = this.getRandomEnchantments(stackInSlot, i, this.enchantLevels[i]);
                     if (randomEnchantments != null && !randomEnchantments.isEmpty()) {
                        EnchantmentData enchantmentData = (EnchantmentData)randomEnchantments.get(this.rand.nextInt(randomEnchantments.size()));
                        this.enchantmentIds[i] = enchantmentData.enchantmentobj.effectId | enchantmentData.enchantmentLevel << 8;
                     }
                  }
               }

               this.detectAndSendChanges();
            }
         } else {
            for (int i = 0; i < 3; i++) {
               this.enchantLevels[i] = 0;
               this.enchantmentIds[i] = -1;
            }
         }
      }
   }

   @Override
   public boolean enchantItem(EntityPlayer playerIn, int id) {
      ItemStack stackInSlot = this.tableInventory.getStackInSlot(0);
      ItemStack stackInSlot2 = this.tableInventory.getStackInSlot(1);
      int n = id + 1;
      if ((stackInSlot2 == null || stackInSlot2.stackSize < n) && !playerIn.capabilities.isCreativeMode) {
         return false;
      }

      if (this.enchantLevels[id] > 0
         && stackInSlot != null
         && (playerIn.experienceLevel >= n && playerIn.experienceLevel >= this.enchantLevels[id] || playerIn.capabilities.isCreativeMode)) {
         if (!this.worldPointer.isRemote) {
            List randomEnchantments = this.getRandomEnchantments(stackInSlot, id, this.enchantLevels[id]);
            boolean flag = stackInSlot.getItem() == Items.book;
            if (randomEnchantments != null) {
               playerIn.removeExperienceLevel(n);
               if (flag) {
                  stackInSlot.setItem(Items.enchanted_book);
               }

               for (int i = 0; i < randomEnchantments.size(); i++) {
                  EnchantmentData enchantmentData = (EnchantmentData)randomEnchantments.get(i);
                  if (flag) {
                     Items.enchanted_book.addEnchantment(stackInSlot, enchantmentData);
                  } else {
                     stackInSlot.addEnchantment(enchantmentData.enchantmentobj, enchantmentData.enchantmentLevel);
                  }
               }

               if (!playerIn.capabilities.isCreativeMode) {
                  stackInSlot2.stackSize -= n;
                  if (stackInSlot2.stackSize <= 0) {
                     this.tableInventory.setInventorySlotContents(1, (ItemStack)null);
                  }
               }

               playerIn.triggerAchievement(StatList.ITEM_ENCHANTED);
               this.tableInventory.markDirty();
               this.xpSeed = playerIn.getXPSeed();
               this.onCraftMatrixChanged(this.tableInventory);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public List<EnchantmentData> getRandomEnchantments(ItemStack stack, int n, int n2) {
      this.rand.setSeed(this.xpSeed + n);
      List buildEnchantmentList = EnchantmentHelper.buildEnchantmentList(this.rand, stack, n2);
      if (stack.getItem() == Items.book && buildEnchantmentList != null && buildEnchantmentList.size() > 1) {
         buildEnchantmentList.remove(this.rand.nextInt(buildEnchantmentList.size()));
      }

      return buildEnchantmentList;
   }

   public int getLapisAmount() {
      ItemStack stackInSlot = this.tableInventory.getStackInSlot(1);
      return stackInSlot == null ? 0 : stackInSlot.stackSize;
   }

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      if (!this.worldPointer.isRemote) {
         for (int i = 0; i < this.tableInventory.getSizeInventory(); i++) {
            ItemStack removeStackFromSlot = this.tableInventory.removeStackFromSlot(i);
            if (removeStackFromSlot != null) {
               playerIn.dropPlayerItemWithRandomChoice(removeStackFromSlot, false);
            }
         }
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.worldPointer.getBlockState(this.position).getBlock() != Blocks.enchanting_table
         ? false
         : playerIn.getDistanceSq(this.position.getX() + 0.5, this.position.getY() + 0.5, this.position.getZ() + 0.5) <= 64.0;
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack stack = slot.getStack();
         itemStack = stack.copy();
         if (index == 0) {
            if (!this.mergeItemStack(stack, 2, 38, true)) {
               return null;
            }
         } else if (index == 1) {
            if (!this.mergeItemStack(stack, 2, 38, true)) {
               return null;
            }
         } else if (stack.getItem() == Items.dye && EnumDyeColor.byDyeDamage(stack.getMetadata()) == EnumDyeColor.BLUE) {
            if (!this.mergeItemStack(stack, 1, 2, true)) {
               return null;
            }
         } else {
            if (this.inventorySlots.get(0).getHasStack() || !this.inventorySlots.get(0).isItemValid(stack)) {
               return null;
            }

            if (stack.hasTagCompound() && stack.stackSize == 1) {
               this.inventorySlots.get(0).putStack(stack.copy());
               stack.stackSize = 0;
            } else if (stack.stackSize >= 1) {
               this.inventorySlots.get(0).putStack(new ItemStack(stack.getItem(), 1, stack.getMetadata()));
               stack.stackSize--;
            }
         }

         if (stack.stackSize == 0) {
            slot.putStack((ItemStack)null);
         } else {
            slot.onSlotChanged();
         }

         if (stack.stackSize == itemStack.stackSize) {
            return null;
         }

         slot.onPickupFromSlot(playerIn, stack);
      }

      return itemStack;
   }
}
