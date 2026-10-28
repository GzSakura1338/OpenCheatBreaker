package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerGrass;

public class ItemDoublePlant extends ItemMultiTexture {
    public ItemDoublePlant(Block block, BlockDoublePlant blockDoublePlant, String[] strs) {
        super(block, blockDoublePlant, strs);
    }

    @Override
    public IIcon getIconFromDamage(int n) {
        return BlockDoublePlant.computeInt(n) == 0 ? ((BlockDoublePlant)this.block).sunflowerIcons[0] : ((BlockDoublePlant)this.block).findIIcon(true, n);
    }

    @Override
    public int getColorFromItemStack(ItemStack itemStack, int n) {
        int computeInt = BlockDoublePlant.computeInt(itemStack.getMetadata());
        return computeInt != 2 && computeInt != 3 ? super.getColorFromItemStack(itemStack, n) : ColorizerGrass.getGrassColor(0.5, 1.0);
    }
}
