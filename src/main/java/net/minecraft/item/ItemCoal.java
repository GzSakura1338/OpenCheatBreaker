package net.minecraft.item;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;

public class ItemCoal extends Item {
    public IIcon iIcon;

    public ItemCoal() {
        this.setHasSubtypes(true);
        this.setMaxDurability(0);
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return stack.getMetadata() == 1 ? "item.charcoal" : "item.coal";
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    public IIcon getIconFromDamage(int n) {
        return n == 1 ? this.iIcon : super.getIconFromDamage(n);
    }

    @Override
    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);
        this.iIcon = register.registerIcon("charcoal");
    }
}
