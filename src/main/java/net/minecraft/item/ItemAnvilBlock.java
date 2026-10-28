package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;

public class ItemAnvilBlock extends ItemMultiTexture {
    public ItemAnvilBlock(Block block) {
        super(block, block, BlockAnvil.anvilDamageNames);
    }

    @Override
    public int getMetadata(int n) {
        return n << 2;
    }
}
