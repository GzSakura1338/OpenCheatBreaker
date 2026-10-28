package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

public class ItemMultiTexture extends ItemBlock {
    public Block block;
    public String[] stringArray;

    public ItemMultiTexture(Block block, Block block2, String[] strs) {
        super(block);
        this.block = block2;
        this.stringArray = strs;
        this.setMaxDurability(0);
        this.setHasSubtypes(true);
    }

    @Override
    public IIcon getIconFromDamage(int n) {
        return this.block.getIcon(2, n);
    }

    @Override
    public int getMetadata(int n) {
        return n;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int metadata = stack.getMetadata();
        if (metadata < 0 || metadata >= this.stringArray.length) {
            metadata = 0;
        }

        return super.getUnlocalizedName() + "." + this.stringArray[metadata];
    }
}
