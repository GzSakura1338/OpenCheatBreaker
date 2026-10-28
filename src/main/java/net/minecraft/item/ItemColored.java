package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

public class ItemColored extends ItemBlock {
    public Block coloredBlock;
    public String[] names;

    public ItemColored(Block block, boolean flag) {
        super(block);
        this.coloredBlock = block;
        if (flag) {
            this.setMaxDurability(0);
            this.setHasSubtypes(true);
        }
    }

    @Override
    public int getColorFromItemStack(ItemStack itemStack, int n) {
        return this.coloredBlock.getRenderColor(itemStack.getMetadata());
    }

    @Override
    public IIcon getIconFromDamage(int n) {
        return this.coloredBlock.getIcon(0, n);
    }

    @Override
    public int getMetadata(int n) {
        return n;
    }

    public ItemColored setNamed(String[] strs) {
        this.names = strs;
        return this;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (this.names == null) {
            return super.getUnlocalizedName(stack);
        }

        int metadata = stack.getMetadata();
        return metadata >= 0 && metadata < this.names.length ? super.getUnlocalizedName(stack) + "." + this.names[metadata] : super.getUnlocalizedName(stack);
    }
}
