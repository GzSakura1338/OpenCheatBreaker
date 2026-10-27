package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class ItemEmptyMap extends ItemMapBase {
   public ItemEmptyMap() {
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      ItemStack itemStack = new ItemStack(Items.filled_map, 1, worldIn.getUniqueDataId("map"));
      String str = "map_" + itemStack.getMetadata();
      MapData mapData = new MapData(str);
      worldIn.setItemData(str, mapData);
      mapData.scale = 0;
      mapData.calculateMapCenter(playerIn.posX, playerIn.posZ, mapData.scale);
      mapData.dimension = (byte)worldIn.provider.getDimensionId();
      mapData.markDirty();
      itemStackIn.stackSize--;
      if (itemStackIn.stackSize <= 0) {
         return itemStack;
      }

      if (!playerIn.inventory.addItemStackToInventory(itemStack.copy())) {
         playerIn.dropPlayerItemWithRandomChoice(itemStack, false);
      }

      playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      return itemStackIn;
   }
}
