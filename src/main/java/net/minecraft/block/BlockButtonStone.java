package net.minecraft.block;

import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class BlockButtonStone extends BlockButton {
    public BlockButtonStone() {
        super(false);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return Blocks.stone.getBlockTextureFromSide(1);
    }
}
