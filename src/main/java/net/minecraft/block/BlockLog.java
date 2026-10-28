package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public abstract class BlockLog extends BlockRotatedPillar {
    public IIcon[] iIconArray;
    public IIcon[] iIconArray2;

    public BlockLog() {
        super(Material.wood);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setHardness(2.0F);
        this.setStepSound(soundTypeWood);
    }

    public static int computeInt4(int n) {
        return n & 3;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        byte b = 4;
        int n = b + 1;
        if (worldIn.checkChunksExist(x - n, y - n, z - n, x + n, y + n, z + n)) {
            for (int i = -b; i <= b; i++) {
                for (int j = -b; j <= b; j++) {
                    for (int k = -b; k <= b; k++) {
                        if (worldIn.getBlock(x + i, y + j, z + k).getMaterial() == Material.leaves) {
                            int blockMetadata = worldIn.getBlockMetadata(x + i, y + j, z + k);
                            if ((blockMetadata & 8) == 0) {
                                worldIn.setBlockMetadataWithNotify(x + i, y + j, z + k, blockMetadata | 8, 4);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public IIcon getSideIcon(int n) {
        return this.iIconArray[n % this.iIconArray.length];
    }

    @Override
    public IIcon getTopIcon(int n) {
        return this.iIconArray2[n % this.iIconArray2.length];
    }
}
