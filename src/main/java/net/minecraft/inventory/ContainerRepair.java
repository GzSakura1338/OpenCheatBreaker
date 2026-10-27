package net.minecraft.inventory;

import java.util.Map;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ContainerRepair extends Container {
   public static Logger logger = LogManager.getLogger();
   public IInventory outputSlot = new InventoryCraftResult();
   public IInventory inputSlots = new InventoryBasic("Repair", true, 2) {
      @Override
      public void markDirty() {
         super.markDirty();
         ContainerRepair.this.onCraftMatrixChanged(this);
      }
   };
   public BlockPos selfPosition;
   public World theWorld;
   public int maximumCost;
   public int materialCost;
   public String repairedItemName;
   public EntityPlayer thePlayer;

   public ContainerRepair(InventoryPlayer playerInventory, World worldIn, EntityPlayer player) {
      this(playerInventory, worldIn, BlockPos.ORIGIN, player);
   }

   public ContainerRepair(InventoryPlayer playerInventory, final World worldIn, final BlockPos blockPosIn, EntityPlayer player) {
      this.selfPosition = blockPosIn;
      this.theWorld = worldIn;
      this.thePlayer = player;
      this.addSlotToContainer(new Slot(this.inputSlots, 0, 27, 47));
      this.addSlotToContainer(new Slot(this.inputSlots, 1, 76, 47));
      this.addSlotToContainer(
         new Slot(this.outputSlot, 2, 134, 47) {
            @Override
            public boolean isItemValid(ItemStack stack) {
               return false;
            }

            @Override
            public boolean canTakeStack(EntityPlayer playerIn) {
               return (playerIn.capabilities.isCreativeMode || playerIn.experienceLevel >= ContainerRepair.this.maximumCost)
                  && ContainerRepair.this.maximumCost > 0
                  && this.getHasStack();
            }

            @Override
            public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
               if (!playerIn.capabilities.isCreativeMode) {
                  playerIn.addExperienceLevel(-ContainerRepair.this.maximumCost);
               }

               ContainerRepair.this.inputSlots.setInventorySlotContents(0, (ItemStack)null);
               if (ContainerRepair.this.materialCost > 0) {
                  ItemStack stackInSlot = ContainerRepair.this.inputSlots.getStackInSlot(1);
                  if (stackInSlot != null && stackInSlot.stackSize > ContainerRepair.this.materialCost) {
                     stackInSlot.stackSize = stackInSlot.stackSize - ContainerRepair.this.materialCost;
                     ContainerRepair.this.inputSlots.setInventorySlotContents(1, stackInSlot);
                  } else {
                     ContainerRepair.this.inputSlots.setInventorySlotContents(1, (ItemStack)null);
                  }
               } else {
                  ContainerRepair.this.inputSlots.setInventorySlotContents(1, (ItemStack)null);
               }

               ContainerRepair.this.maximumCost = 0;
               IBlockState blockState = worldIn.getBlockState(blockPosIn);
               if (!playerIn.capabilities.isCreativeMode && !worldIn.isRemote && blockState.getBlock() == Blocks.anvil && playerIn.getRNG().nextFloat() < 0.12F) {
                  int value = blockState.getValue(BlockAnvil.DAMAGE);
                  if (++value > 2) {
                     worldIn.setBlockToAir(blockPosIn);
                     worldIn.playAuxSFX(1020, blockPosIn, 0);
                  } else {
                     worldIn.setBlockState(blockPosIn, blockState.withProperty(BlockAnvil.DAMAGE, value), 2);
                     worldIn.playAuxSFX(1021, blockPosIn, 0);
                  }
               } else if (!worldIn.isRemote) {
                  worldIn.playAuxSFX(1021, blockPosIn, 0);
               }
            }
         }
      );

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 9; j++) {
            this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for (int i = 0; i < 9; i++) {
         this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
      }
   }

   @Override
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      super.onCraftMatrixChanged(inventoryIn);
      if (inventoryIn == this.inputSlots) {
         this.updateRepairOutput();
      }
   }

   public void updateRepairOutput() {
      boolean flag = false;
      boolean flag2 = true;
      boolean flag3 = true;
      boolean flag4 = true;
      byte b = 2;
      boolean flag5 = true;
      boolean flag6 = true;
      ItemStack stackInSlot = this.inputSlots.getStackInSlot(0);
      this.maximumCost = 1;
      int n = 0;
      int n2 = 0;
      byte b2 = 0;
      if (stackInSlot == null) {
         this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
         this.maximumCost = 0;
      } else {
         ItemStack itemStack = stackInSlot.copy();
         ItemStack stackInSlot2 = this.inputSlots.getStackInSlot(1);
         Map enchantments = EnchantmentHelper.getEnchantments(itemStack);
         boolean flag7 = false;
         n2 = n2 + stackInSlot.getRepairCost() + (stackInSlot2 == null ? 0 : stackInSlot2.getRepairCost());
         this.materialCost = 0;
         if (stackInSlot2 != null) {
            flag7 = stackInSlot2.getItem() == Items.enchanted_book && Items.enchanted_book.getEnchantments(stackInSlot2).tagCount() > 0;
            if (itemStack.isItemStackDamageable() && itemStack.getItem().getIsRepairable(stackInSlot, stackInSlot2)) {
               int min = Math.min(itemStack.getItemDamage(), itemStack.getMaxDamage() / 4);
               if (min <= 0) {
                  this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
                  this.maximumCost = 0;
                  return;
               }

               int n3;
               for (n3 = 0; min > 0 && n3 < stackInSlot2.stackSize; n3++) {
                  int n4 = itemStack.getItemDamage() - min;
                  itemStack.setItemDamage(n4);
                  n++;
                  min = Math.min(itemStack.getItemDamage(), itemStack.getMaxDamage() / 4);
               }

               this.materialCost = n3;
            } else {
               if (!flag7 && (itemStack.getItem() != stackInSlot2.getItem() || !itemStack.isItemStackDamageable())) {
                  this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
                  this.maximumCost = 0;
                  return;
               }

               if (itemStack.isItemStackDamageable() && !flag7) {
                  int n3 = stackInSlot.getMaxDamage() - stackInSlot.getItemDamage();
                  int n4 = stackInSlot2.getMaxDamage() - stackInSlot2.getItemDamage();
                  int n5 = n4 + itemStack.getMaxDamage() * 12 / 100;
                  int n6 = n3 + n5;
                  int n7 = itemStack.getMaxDamage() - n6;
                  if (n7 < 0) {
                     n7 = 0;
                  }

                  if (n7 < itemStack.getMetadata()) {
                     itemStack.setItemDamage(n7);
                     n += 2;
                  }
               }

               Map enchantments2 = EnchantmentHelper.getEnchantments(stackInSlot2);

               for (int n3 : (Iterable<Integer>)enchantments2.keySet()) {
                  Enchantment enchantmentById = Enchantment.getEnchantmentById(n3);
                  if (enchantmentById != null) {
                     int n4 = enchantments.containsKey(n3) ? (Integer)enchantments.get(n3) : 0;
                     int n5 = (Integer)enchantments2.get(n3);
                     int n6;
                     if (n4 == n5) {
                        n6 = ++n5;
                     } else {
                        n6 = Math.max(n5, n4);
                     }

                     n5 = n6;
                     boolean canApply = enchantmentById.canApply(stackInSlot);
                     if (this.thePlayer.capabilities.isCreativeMode || stackInSlot.getItem() == Items.enchanted_book) {
                        canApply = true;
                     }

                     for (int n7 : (Iterable<Integer>)enchantments.keySet()) {
                        if (n7 != n3 && !enchantmentById.canApplyTogether(Enchantment.getEnchantmentById(n7))) {
                           canApply = false;
                           n++;
                        }
                     }

                     if (canApply) {
                        if (n5 > enchantmentById.getMaxLevel()) {
                           n5 = enchantmentById.getMaxLevel();
                        }

                        enchantments.put(n3, n5);
                        int n7 = 0;
                        switch (enchantmentById.getWeight()) {
                           case 1:
                              n7 = 8;
                              break;
                           case 2:
                              n7 = 4;
                           case 3:
                           case 4:
                           case 6:
                           case 7:
                           case 8:
                           case 9:
                           default:
                              break;
                           case 5:
                              n7 = 2;
                              break;
                           case 10:
                              n7 = 1;
                        }

                        if (flag7) {
                           n7 = Math.max(1, n7 / 2);
                        }

                        n += n7 * n5;
                     }
                  }
               }
            }
         }

         if (StringUtils.isBlank(this.repairedItemName)) {
            if (stackInSlot.hasDisplayName()) {
               b2 = 1;
               n += b2;
               itemStack.clearCustomName();
            }
         } else if (!this.repairedItemName.equals(stackInSlot.getDisplayName())) {
            b2 = 1;
            n += b2;
            itemStack.setStackDisplayName(this.repairedItemName);
         }

         this.maximumCost = n2 + n;
         if (n <= 0) {
            itemStack = null;
         }

         if (b2 == n && b2 > 0 && this.maximumCost >= 40) {
            this.maximumCost = 39;
         }

         if (this.maximumCost >= 40 && !this.thePlayer.capabilities.isCreativeMode) {
            itemStack = null;
         }

         if (itemStack != null) {
            int repairCost = itemStack.getRepairCost();
            if (stackInSlot2 != null && repairCost < stackInSlot2.getRepairCost()) {
               repairCost = stackInSlot2.getRepairCost();
            }

            repairCost = repairCost * 2 + 1;
            itemStack.setRepairCost(repairCost);
            EnchantmentHelper.setEnchantments(enchantments, itemStack);
         }

         this.outputSlot.setInventorySlotContents(0, itemStack);
         this.detectAndSendChanges();
      }
   }

   @Override
   public void onCraftGuiOpened(ICrafting listener) {
      super.onCraftGuiOpened(listener);
      listener.sendProgressBarUpdate(this, 0, this.maximumCost);
   }

   @Override
   public void updateProgressBar(int id, int data) {
      if (id == 0) {
         this.maximumCost = data;
      }
   }

   @Override
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      if (!this.theWorld.isRemote) {
         for (int i = 0; i < this.inputSlots.getSizeInventory(); i++) {
            ItemStack removeStackFromSlot = this.inputSlots.removeStackFromSlot(i);
            if (removeStackFromSlot != null) {
               playerIn.dropPlayerItemWithRandomChoice(removeStackFromSlot, false);
            }
         }
      }
   }

   @Override
   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.theWorld.getBlockState(this.selfPosition).getBlock() != Blocks.anvil
         ? false
         : playerIn.getDistanceSq(this.selfPosition.getX() + 0.5, this.selfPosition.getY() + 0.5, this.selfPosition.getZ() + 0.5) <= 64.0;
   }

   @Override
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemStack = null;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack stack = slot.getStack();
         itemStack = stack.copy();
         if (index == 2) {
            if (!this.mergeItemStack(stack, 3, 39, true)) {
               return null;
            }

            slot.onSlotChange(stack, itemStack);
         } else if (index != 0 && index != 1) {
            if (index >= 3 && index < 39 && !this.mergeItemStack(stack, 0, 2, false)) {
               return null;
            }
         } else if (!this.mergeItemStack(stack, 3, 39, false)) {
            return null;
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

   public void updateItemName(String newName) {
      this.repairedItemName = newName;
      if (this.getSlot(2).getHasStack()) {
         ItemStack stack = this.getSlot(2).getStack();
         if (StringUtils.isBlank(newName)) {
            stack.clearCustomName();
         } else {
            stack.setStackDisplayName(this.repairedItemName);
         }
      }

      this.updateRepairOutput();
   }
}
