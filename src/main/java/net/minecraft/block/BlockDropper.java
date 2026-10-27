package net.minecraft.block;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockDropper extends BlockDispenser {
   public IBehaviorDispenseItem dropBehavior = new BehaviorDefaultDispenseItem();

   @Override
   public IBehaviorDispenseItem getBehavior(ItemStack stack) {
      return this.dropBehavior;
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityDropper();
   }

   @Override
   public void dispense(World worldIn, BlockPos pos) {
      BlockSourceImpl blockSourceImpl = new BlockSourceImpl(worldIn, pos);
      TileEntityDispenser blockTileEntity = blockSourceImpl.getBlockTileEntity();
      if (blockTileEntity != null) {
         int dispenseSlot = blockTileEntity.getDispenseSlot();
         if (dispenseSlot < 0) {
            worldIn.playAuxSFX(1001, pos, 0);
         } else {
            ItemStack stackInSlot = blockTileEntity.getStackInSlot(dispenseSlot);
            if (stackInSlot != null) {
               EnumFacing value = worldIn.getBlockState(pos).getValue(FACING);
               BlockPos offset = pos.offset(value);
               IInventory inventoryAtPosition = TileEntityHopper.getInventoryAtPosition(worldIn, offset.getX(), offset.getY(), offset.getZ());
               ItemStack itemStack;
               if (inventoryAtPosition == null) {
                  itemStack = this.dropBehavior.dispense(blockSourceImpl, stackInSlot);
                  if (itemStack != null && itemStack.stackSize <= 0) {
                     itemStack = null;
                  }
               } else {
                  itemStack = TileEntityHopper.putStackInInventoryAllSlots(inventoryAtPosition, stackInSlot.copy().splitStack(1), value.getOpposite());
                  if (itemStack == null) {
                     itemStack = stackInSlot.copy();
                     if (--itemStack.stackSize <= 0) {
                        itemStack = null;
                     }
                  } else {
                     itemStack = stackInSlot.copy();
                  }
               }

               blockTileEntity.setInventorySlotContents(dispenseSlot, itemStack);
            }
         }
      }
   }
}
