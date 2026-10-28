package net.minecraft.item;

import net.minecraft.block.BlockLeaves;
import net.minecraft.util.IIcon;

public class ItemLeaves extends ItemBlock {
    public BlockLeaves leavesBlock;

    public ItemLeaves(BlockLeaves blockLeaves) {
        super(blockLeaves);
        this.leavesBlock = blockLeaves;
        this.setMaxDurability(0);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int n) {
        return n | 4;
    }

    @Override
    public IIcon getIconFromDamage(int n) {
        return this.leavesBlock.getIcon(0, n);
    }

    @Override
    public int getColorFromItemStack(ItemStack itemStack, int n) {
        return this.leavesBlock.getRenderColor(itemStack.getMetadata());
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int metadata = stack.getMetadata();
        if (metadata < 0 || metadata >= this.leavesBlock.getStringArray().length) {
            metadata = 0;
        }

        return super.getUnlocalizedName() + "." + this.leavesBlock.getStringArray()[metadata];
    }
}
