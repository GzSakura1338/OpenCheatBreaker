package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.util.IIcon;

public class ItemCloth extends ItemBlock {
    public ItemCloth(Block block) {
        super(block);
        this.setMaxDurability(0);
        this.setHasSubtypes(true);
    }

    @Override
    public IIcon getIconFromDamage(int n) {
        return this.blockInstance.getItemIcon(2, BlockColored.computeInt(n));
    }

    @Override
    public int getMetadata(int n) {
        return n;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + ItemDye.dyeColorNames[BlockColored.computeInt(stack.getMetadata())];
    }
}
