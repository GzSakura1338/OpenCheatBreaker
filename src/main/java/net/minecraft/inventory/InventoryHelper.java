package net.minecraft.inventory;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class InventoryHelper {
   public static Random RANDOM = new Random();

   public static void dropInventoryItems(World worldIn, BlockPos pos, IInventory inventory) {
      dropInventoryItems(worldIn, pos.getX(), pos.getY(), pos.getZ(), inventory);
   }

   public static void dropInventoryItems(World worldIn, Entity entityAt, IInventory inventory) {
      dropInventoryItems(worldIn, entityAt.posX, entityAt.posY, entityAt.posZ, inventory);
   }

   public static void dropInventoryItems(World worldIn, double x, double y, double z, IInventory inventory) {
      for (int i = 0; i < inventory.getSizeInventory(); i++) {
         ItemStack stackInSlot = inventory.getStackInSlot(i);
         if (stackInSlot != null) {
            spawnItemStack(worldIn, x, y, z, stackInSlot);
         }
      }
   }

   public static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack) {
      float f = RANDOM.nextFloat() * 0.8F + 0.1F;
      float f2 = RANDOM.nextFloat() * 0.8F + 0.1F;
      float f3 = RANDOM.nextFloat() * 0.8F + 0.1F;

      while (stack.stackSize > 0) {
         int n = RANDOM.nextInt(21) + 10;
         if (n > stack.stackSize) {
            n = stack.stackSize;
         }

         stack.stackSize -= n;
         EntityItem entityItem = new EntityItem(worldIn, x + f, y + f2, z + f3, new ItemStack(stack.getItem(), n, stack.getMetadata()));
         if (stack.hasTagCompound()) {
            entityItem.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
         }

         float f4 = 0.05F;
         entityItem.motionX = RANDOM.nextGaussian() * f4;
         entityItem.motionY = RANDOM.nextGaussian() * f4 + 0.2F;
         entityItem.motionZ = RANDOM.nextGaussian() * f4;
         worldIn.spawnEntityInWorld(entityItem);
      }
   }
}
