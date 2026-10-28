package net.minecraft.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockJukebox;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemRecord extends Item {
    public static Map records = new HashMap();
    public String recordName;

    public ItemRecord(String str) {
        this.recordName = str;
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabMisc);
        records.put(str, this);
    }

    @Override
    public IIcon getIconFromDamage(int n) {
        return this.itemIcon;
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        if (world.getBlock(n, n2, n3) != Blocks.jukebox || world.getBlockMetadata(n, n2, n3) != 0) {
            return false;
        }

        if (world.isRemote) {
            return true;
        }

        ((BlockJukebox)Blocks.jukebox).handleWorld(world, n, n2, n3, itemStack);
        world.playAuxSFXAtEntity(null, 1005, n, n2, n3, Item.getIdFromItem(this));
        itemStack.stackSize--;
        return true;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean flag) {
        list.add(this.getRecordNameLocal());
    }

    public String getRecordNameLocal() {
        return StatCollector.translateToLocal("item.record." + this.recordName + ".desc");
    }

    @Override
    public EnumRarity getRarity(ItemStack itemStack) {
        return EnumRarity.rare;
    }

    public static ItemRecord getRecord(String str) {
        return (ItemRecord)records.get(str);
    }
}
