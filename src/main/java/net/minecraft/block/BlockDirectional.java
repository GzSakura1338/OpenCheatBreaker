package net.minecraft.block;

import net.minecraft.block.material.Material;

public abstract class BlockDirectional extends Block {
    public BlockDirectional(Material material) {
        super(material);
    }

    public static int getDirection(int n) {
        return n & 3;
    }
}
