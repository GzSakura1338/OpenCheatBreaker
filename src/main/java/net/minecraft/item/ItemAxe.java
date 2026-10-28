package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item.ToolMaterial;

public class ItemAxe extends ItemTool {
    public static Set EFFECTIVE_BLOCKS = Sets.newHashSet(
        new Block[]{Blocks.planks, Blocks.bookshelf, Blocks.log, Blocks.log2, Blocks.chest, Blocks.pumpkin, Blocks.lit_pumpkin}
    );

    public ItemAxe(ToolMaterial toolMaterial) {
        super(3.0F, toolMaterial, EFFECTIVE_BLOCKS);
    }

    @Override
    public float getStrVsBlock(ItemStack itemStack, Block block) {
        return block.getMaterial() != Material.wood && block.getMaterial() != Material.plants && block.getMaterial() != Material.vine
            ? super.getStrVsBlock(itemStack, block)
            : this.efficiencyOnProperMaterial;
    }
}
