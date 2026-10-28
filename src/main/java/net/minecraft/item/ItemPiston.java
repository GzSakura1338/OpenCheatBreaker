package net.minecraft.item;

import net.minecraft.block.Block;

public class ItemPiston extends ItemBlock {
    public ItemPiston(Block block) {
        super(block);
    }

    @Override
    public int getMetadata(int n) {
        return 7;
    }
}
