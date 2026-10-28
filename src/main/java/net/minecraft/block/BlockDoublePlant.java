package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDoublePlant extends BlockBush implements IGrowable {
    public static String[] stringArray = new String[]{"sunflower", "syringa", "grass", "fern", "rose", "paeonia"};
    public IIcon[] doublePlantBottomIcons;
    public IIcon[] doublePlantTopIcons;
    public IIcon[] sunflowerIcons;

    public BlockDoublePlant() {
        super(Material.plants);
        this.setHardness(0.0F);
        this.setStepSound(soundTypeGrass);
        this.setUnlocalizedName("doublePlant");
    }

    @Override
    public int getRenderType() {
        return 40;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public int computeInt(IBlockAccess blockAccess, int n, int n2, int n3) {
        int blockMetadata = blockAccess.getBlockMetadata(n, n2, n3);
        return !check(blockMetadata) ? blockMetadata & 7 : blockAccess.getBlockMetadata(n, n2 - 1, n3) & 7;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return super.canPlaceBlockAt(worldIn, x, y, z) && worldIn.isAirBlock(x, y + 1, z);
    }

    @Override
    public void checkAndDropBlock(World worldIn, int x, int y, int z) {
        if (!this.canBlockStay(worldIn, x, y, z)) {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            if (!check(blockMetadata)) {
                this.dropBlockAsItem(worldIn, x, y, z, blockMetadata, 0);
                if (worldIn.getBlock(x, y + 1, z) == this) {
                    worldIn.setBlock(x, y + 1, z, Blocks.air, 0, 2);
                }
            }

            worldIn.setBlock(x, y, z, Blocks.air, 0, 2);
        }
    }

    @Override
    public boolean canBlockStay(World worldIn, int x, int y, int z) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        return check(blockMetadata) ? worldIn.getBlock(x, y - 1, z) == this : worldIn.getBlock(x, y + 1, z) == this && super.canBlockStay(worldIn, x, y, z);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        if (check(meta)) {
            return null;
        }

        int computeInt2 = computeInt(meta);
        return computeInt2 != 3 && computeInt2 != 2 ? Item.getItemFromBlock(this) : null;
    }

    @Override
    public int damageDropped(int meta) {
        return check(meta) ? 0 : meta & 7;
    }

    public static boolean check(int n) {
        return (n & 8) != 0;
    }

    public static int computeInt(int n) {
        return n & 7;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return check(meta) ? this.doublePlantBottomIcons[0] : this.doublePlantBottomIcons[meta & 7];
    }

    public IIcon findIIcon(boolean flag, int n) {
        return flag ? this.doublePlantTopIcons[n] : this.doublePlantBottomIcons[n];
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        int computeInt = this.computeInt(worldIn, x, y, z);
        return computeInt != 2 && computeInt != 3 ? 16777215 : worldIn.getBiomeGenForCoords(x, z).getBiomeGrassColor(x, y, z);
    }

    public void handleWorld(World world, int n, int n2, int n3, int n4, int n5) {
        world.setBlock(n, n2, n3, this, n4, n5);
        world.setBlock(n, n2 + 1, n3, this, 8, n5);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        int n = ((MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5) & 3) + 2) % 4;
        worldIn.setBlock(x, y + 1, z, this, 8 | n, 2);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, int x, int y, int z, int meta) {
        if (worldIn.isRemote
            || player.getCurrentEquippedItem() == null
            || player.getCurrentEquippedItem().getItem() != Items.shears
            || check(meta)
            || !this.checkWorld(worldIn, x, y, z, meta, player)) {
            super.harvestBlock(worldIn, player, x, y, z, meta);
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, int x, int y, int z, int meta, EntityPlayer player) {
        if (check(meta)) {
            if (worldIn.getBlock(x, y - 1, z) == this) {
                if (!player.capabilities.isCreativeMode) {
                    int blockMetadata = worldIn.getBlockMetadata(x, y - 1, z);
                    int computeInt2 = computeInt(blockMetadata);
                    if (computeInt2 != 3 && computeInt2 != 2) {
                        worldIn.breakBlock(x, y - 1, z, true);
                    } else {
                        if (!worldIn.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears) {
                            this.checkWorld(worldIn, x, y, z, blockMetadata, player);
                        }

                        worldIn.setBlockToAir(x, y - 1, z);
                    }
                } else {
                    worldIn.setBlockToAir(x, y - 1, z);
                }
            }
        } else if (player.capabilities.isCreativeMode && worldIn.getBlock(x, y + 1, z) == this) {
            worldIn.setBlock(x, y + 1, z, Blocks.air, 0, 2);
        }

        super.onBlockHarvested(worldIn, x, y, z, meta, player);
    }

    public boolean checkWorld(World world, int n, int n2, int n3, int n4, EntityPlayer entityPlayer) {
        int computeInt2 = computeInt(n4);
        if (computeInt2 != 3 && computeInt2 != 2) {
            return false;
        }

        entityPlayer.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(this)], 1);
        byte b = 1;
        if (computeInt2 == 3) {
            b = 2;
        }

        this.dropBlockAsItem(world, n, n2, n3, new ItemStack(Blocks.tallgrass, 2, b));
        return true;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.doublePlantBottomIcons = new IIcon[stringArray.length];
        this.doublePlantTopIcons = new IIcon[stringArray.length];

        for (int i = 0; i < this.doublePlantBottomIcons.length; i++) {
            this.doublePlantBottomIcons[i] = reg.registerIcon("double_plant_" + stringArray[i] + "_bottom");
            this.doublePlantTopIcons[i] = reg.registerIcon("double_plant_" + stringArray[i] + "_top");
        }

        this.sunflowerIcons = new IIcon[2];
        this.sunflowerIcons[0] = reg.registerIcon("double_plant_sunflower_front");
        this.sunflowerIcons[1] = reg.registerIcon("double_plant_sunflower_back");
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        for (int i = 0; i < this.doublePlantBottomIcons.length; i++) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    public int getDamageValue(World worldIn, int x, int y, int z) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        return check(blockMetadata) ? computeInt(worldIn.getBlockMetadata(x, y - 1, z)) : computeInt(blockMetadata);
    }

    @Override
    public boolean canFertilize(World worldIn, int x, int y, int z, boolean isClient) {
        int computeInt = this.computeInt(worldIn, x, y, z);
        return computeInt != 2 && computeInt != 3;
    }

    @Override
    public boolean shouldFertilize(World worldIn, Random random, int x, int y, int z) {
        return true;
    }

    @Override
    public void fertilize(World worldIn, Random random, int x, int y, int z) {
        int computeInt = this.computeInt(worldIn, x, y, z);
        this.dropBlockAsItem(worldIn, x, y, z, new ItemStack(this, 1, computeInt));
    }
}
