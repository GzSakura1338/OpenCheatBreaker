package net.minecraft.block;

import java.util.Random;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockNetherWart extends BlockBush {
    public IIcon[] iIconArray;

    public BlockNetherWart() {
        this.setTickRandomly(true);
        float f = 0.5F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.setCreativeTab(null);
    }

    @Override
    public boolean canPlaceBlockOn(Block ground) {
        return ground == Blocks.soul_sand;
    }

    @Override
    public boolean canBlockStay(World worldIn, int x, int y, int z) {
        return this.canPlaceBlockOn(worldIn.getBlock(x, y - 1, z));
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        if (blockMetadata < 3 && random.nextInt(10) == 0) {
            worldIn.setBlockMetadataWithNotify(x, y, z, ++blockMetadata, 2);
        }

        super.updateTick(worldIn, x, y, z, random);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return meta >= 3 ? this.iIconArray[2] : (meta > 0 ? this.iIconArray[1] : this.iIconArray[0]);
    }

    @Override
    public int getRenderType() {
        return 6;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune) {
        if (!worldIn.isRemote) {
            int n = 1;
            if (meta >= 3) {
                n = 2 + worldIn.rand.nextInt(3);
                if (fortune > 0) {
                    n += worldIn.rand.nextInt(fortune + 1);
                }
            }

            for (int i = 0; i < n; i++) {
                this.dropBlockAsItem(worldIn, x, y, z, new ItemStack(Items.nether_wart));
            }
        }
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return null;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Items.nether_wart;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iIconArray = new IIcon[3];

        for (int i = 0; i < this.iIconArray.length; i++) {
            this.iIconArray[i] = reg.registerIcon(this.getTextureName() + "_stage_" + i);
        }
    }
}
