package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemFirework extends Item {
   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote) {
         EntityFireworkRocket entityFireworkRocket = new EntityFireworkRocket(worldIn, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, stack);
         worldIn.spawnEntityInWorld(entityFireworkRocket);
         if (!playerIn.capabilities.isCreativeMode) {
            stack.stackSize--;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      if (stack.hasTagCompound()) {
         NBTTagCompound compoundTag = stack.getTagCompound().getCompoundTag("Fireworks");
         if (compoundTag != null) {
            if (compoundTag.hasKey("Flight", 99)) {
               tooltip.add(StatCollector.translateToLocal("item.fireworks.flight") + " " + compoundTag.getByte("Flight"));
            }

            NBTTagList tagList = compoundTag.getTagList("Explosions", 10);
            if (tagList != null && tagList.tagCount() > 0) {
               for (int i = 0; i < tagList.tagCount(); i++) {
                  NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
                  ArrayList newArrayList = Lists.newArrayList();
                  ItemFireworkCharge.addExplosionInfo(compoundTagAt, newArrayList);
                  if (newArrayList.size() > 0) {
                     for (int j = 1; j < newArrayList.size(); j++) {
                        newArrayList.set(j, "  " + (String)newArrayList.get(j));
                     }

                     tooltip.addAll(newArrayList);
                  }
               }
            }
         }
      }
   }
}
