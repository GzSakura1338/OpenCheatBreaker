package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class ItemEmptyMap extends ItemMapBase {
    public ItemEmptyMap() {
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        ItemStack itemStack = new ItemStack(Items.filled_map, 1, worldIn.getUniqueDataId("map"));
        String str = "map_" + itemStack.getMetadata();
        MapData mapData = new MapData(str);
        worldIn.setItemData(str, mapData);
        mapData.scale = 0;
        int n = 128 * (1 << mapData.scale);
        mapData.xCenter = (int)(Math.round(player.posX / n) * n);
        mapData.zCenter = (int)(Math.round(player.posZ / n) * n);
        mapData.dimension = (byte)worldIn.provider.dimensionId;
        mapData.markDirty();
        itemStackIn.stackSize--;
        if (itemStackIn.stackSize <= 0) {
            return itemStack;
        }

        if (!player.inventory.addItemStackToInventory(itemStack.copy())) {
            player.dropPlayerItemWithRandomChoice(itemStack, false);
        }

        return itemStackIn;
    }
}
