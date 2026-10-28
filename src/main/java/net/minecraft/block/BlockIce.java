package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockIce extends BlockBreakable {
    public BlockIce() {
        super("ice", Material.ice, false);
        this.slipperiness = 0.98F;
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return super.shouldSideBeRendered(worldIn, x, y, z, 1 - side);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, int x, int y, int z, int meta) {
        player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(this)], 1);
        player.addExhaustion(0.025F);
        if (this.canSilkHarvest() && EnchantmentHelper.getSilkTouchModifier(player)) {
            ItemStack createStackedBlock = this.createStackedBlock(meta);
            if (createStackedBlock != null) {
                this.dropBlockAsItem(worldIn, x, y, z, createStackedBlock);
            }
        } else {
            if (worldIn.provider.isHellWorld) {
                worldIn.setBlockToAir(x, y, z);
                return;
            }

            int fortuneModifier = EnchantmentHelper.getFortuneModifier(player);
            this.dropBlockAsItem(worldIn, x, y, z, meta, fortuneModifier);
            Material material = worldIn.getBlock(x, y - 1, z).getMaterial();
            if (material.blocksMovement() || material.isLiquid()) {
                worldIn.setBlock(x, y, z, Blocks.flowing_water);
            }
        }
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (worldIn.getSavedLightValue(EnumSkyBlock.Block, x, y, z) > 11 - this.getLightOpacity()) {
            if (worldIn.provider.isHellWorld) {
                worldIn.setBlockToAir(x, y, z);
                return;
            }

            this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
            worldIn.setBlock(x, y, z, Blocks.water);
        }
    }

    @Override
    public int getMobilityFlag() {
        return 0;
    }
}
