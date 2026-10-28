package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTallGrass extends BlockBush implements IGrowable {
    public static String[] stringArray = new String[]{"deadbush", "tallgrass", "fern"};
    public IIcon[] iIconArray;

    public BlockTallGrass() {
        super(Material.vine);
        float f = 0.4F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.8F, 0.5F + f);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta >= this.iIconArray.length) {
            meta = 0;
        }

        return this.iIconArray[meta];
    }

    @Override
    public int getBlockColor() {
        double d = 0.5;
        double d2 = 1.0;
        return ColorizerGrass.getGrassColor(d, d2);
    }

    @Override
    public boolean canBlockStay(World worldIn, int x, int y, int z) {
        return this.canPlaceBlockOn(worldIn.getBlock(x, y - 1, z));
    }

    @Override
    public int getRenderColor(int meta) {
        return meta == 0 ? 16777215 : ColorizerGrass.getGrassColor(0.5, 1.0);
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        return blockMetadata == 0 ? 16777215 : worldIn.getBiomeGenForCoords(x, z).getBiomeGrassColor(x, y, z);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return random.nextInt(8) == 0 ? Items.wheat_seeds : null;
    }

    @Override
    public int quantityDroppedWithBonus(int maxBonus, Random random) {
        return 1 + random.nextInt(maxBonus * 2 + 1);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, int x, int y, int z, int meta) {
        if (!worldIn.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears) {
            player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(this)], 1);
            this.dropBlockAsItem(worldIn, x, y, z, new ItemStack(Blocks.tallgrass, 1, meta));
        } else {
            super.harvestBlock(worldIn, player, x, y, z, meta);
        }
    }

    @Override
    public int getDamageValue(World worldIn, int x, int y, int z) {
        return worldIn.getBlockMetadata(x, y, z);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        for (int i = 1; i < 3; i++) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iIconArray = new IIcon[stringArray.length];

        for (int i = 0; i < this.iIconArray.length; i++) {
            this.iIconArray[i] = reg.registerIcon(stringArray[i]);
        }
    }

    @Override
    public boolean canFertilize(World worldIn, int x, int y, int z, boolean isClient) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        return blockMetadata != 0;
    }

    @Override
    public boolean shouldFertilize(World worldIn, Random random, int x, int y, int z) {
        return true;
    }

    @Override
    public void fertilize(World worldIn, Random random, int x, int y, int z) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        byte b = 2;
        if (blockMetadata == 2) {
            b = 3;
        }

        if (Blocks.double_plant.canPlaceBlockAt(worldIn, x, y, z)) {
            Blocks.double_plant.handleWorld(worldIn, x, y, z, b, 2);
        }
    }
}
