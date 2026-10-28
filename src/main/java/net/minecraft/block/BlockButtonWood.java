package net.minecraft.block;

import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class BlockButtonWood extends BlockButton {
    public BlockButtonWood() {
        super(true);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return Blocks.planks.getBlockTextureFromSide(1);
    }
}
