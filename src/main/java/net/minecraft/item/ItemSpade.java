package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item.ToolMaterial;

public class ItemSpade extends ItemTool {
    public static Set EFFECTIVE_BLOCKS = Sets.newHashSet(
        new Block[]{
            Blocks.grass, Blocks.dirt, Blocks.sand, Blocks.gravel, Blocks.snow_layer, Blocks.snow, Blocks.clay, Blocks.farmland, Blocks.soul_sand, Blocks.mycelium
        }
    );

    public ItemSpade(ToolMaterial toolMaterial) {
        super(1.0F, toolMaterial, EFFECTIVE_BLOCKS);
    }

    @Override
    public boolean canItemHarvestBlock(Block block) {
        return block == Blocks.snow_layer || block == Blocks.snow;
    }
}
