package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item.ToolMaterial;

public class ItemPickaxe extends ItemTool {
    public static Set effectiveBlocks = Sets.newHashSet(
        new Block[]{
            Blocks.cobblestone,
            Blocks.double_stone_slab,
            Blocks.stone_slab,
            Blocks.stone,
            Blocks.sandstone,
            Blocks.mossy_cobblestone,
            Blocks.iron_ore,
            Blocks.iron_block,
            Blocks.coal_ore,
            Blocks.gold_block,
            Blocks.gold_ore,
            Blocks.diamond_ore,
            Blocks.diamond_block,
            Blocks.ice,
            Blocks.netherrack,
            Blocks.lapis_ore,
            Blocks.lapis_block,
            Blocks.redstone_ore,
            Blocks.lit_redstone_ore,
            Blocks.rail,
            Blocks.detector_rail,
            Blocks.golden_rail,
            Blocks.activator_rail
        }
    );

    public ItemPickaxe(ToolMaterial toolMaterial) {
        super(2.0F, toolMaterial, effectiveBlocks);
    }

    @Override
    public boolean canItemHarvestBlock(Block block) {
        return block == Blocks.obsidian
            ? this.toolMaterial.getHarvestLevel() == 3
            : (
                block == Blocks.diamond_block || block == Blocks.diamond_ore
                    ? this.toolMaterial.getHarvestLevel() >= 2
                    : (
                        block == Blocks.emerald_ore || block == Blocks.emerald_block
                            ? this.toolMaterial.getHarvestLevel() >= 2
                            : (
                                block == Blocks.gold_block || block == Blocks.gold_ore
                                    ? this.toolMaterial.getHarvestLevel() >= 2
                                    : (
                                        block == Blocks.iron_block || block == Blocks.iron_ore
                                            ? this.toolMaterial.getHarvestLevel() >= 1
                                            : (
                                                block == Blocks.lapis_block || block == Blocks.lapis_ore
                                                    ? this.toolMaterial.getHarvestLevel() >= 1
                                                    : (
                                                        block != Blocks.redstone_ore && block != Blocks.lit_redstone_ore
                                                            ? block.getMaterial() == Material.rock
                                                                || block.getMaterial() == Material.iron
                                                                || block.getMaterial() == Material.anvil
                                                            : this.toolMaterial.getHarvestLevel() >= 2
                                                    )
                                            )
                                    )
                            )
                    )
            );
    }

    @Override
    public float getStrVsBlock(ItemStack itemStack, Block block) {
        return block.getMaterial() != Material.iron && block.getMaterial() != Material.anvil && block.getMaterial() != Material.rock
            ? super.getStrVsBlock(itemStack, block)
            : this.efficiencyOnProperMaterial;
    }
}
