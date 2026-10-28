package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

public class ItemBlockWithMetadata extends ItemBlock {
    public Block blockItem;

    public ItemBlockWithMetadata(Block block, Block block2) {
        super(block);
        this.blockItem = block2;
        this.setMaxDurability(0);
        this.setHasSubtypes(true);
    }

    @Override
    public IIcon getIconFromDamage(int n) {
        return this.blockItem.getIcon(2, n);
    }

    @Override
    public int getMetadata(int n) {
        return n;
    }
}
