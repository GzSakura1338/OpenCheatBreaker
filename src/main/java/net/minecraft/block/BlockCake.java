package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCake extends Block {
    public IIcon iconTop;
    public IIcon iconBottom;
    public IIcon iconInner;

    public BlockCake() {
        super(Material.cake);
        this.setTickRandomly(true);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        float f = 0.0625F;
        float f2 = (1 + blockMetadata * 2) / 16.0F;
        float f3 = 0.5F;
        this.setBlockBounds(f2, 0.0F, f, 1.0F - f, f3, 1.0F - f);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        float f = 0.0625F;
        float f2 = 0.5F;
        this.setBlockBounds(f, 0.0F, f, 1.0F - f, f2, 1.0F - f);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        float f = 0.0625F;
        float f2 = (1 + blockMetadata * 2) / 16.0F;
        float f3 = 0.5F;
        return AxisAlignedBB.getBoundingBox(x + f2, y, z + f, x + 1 - f, y + f3 - f, z + 1 - f);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        float f = 0.0625F;
        float f2 = (1 + blockMetadata * 2) / 16.0F;
        float f3 = 0.5F;
        return AxisAlignedBB.getBoundingBox(x + f2, y, z + f, x + 1 - f, y + f3, z + 1 - f);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.iconTop : (side == 0 ? this.iconBottom : (meta > 0 && side == 4 ? this.iconInner : this.blockIcon));
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
        this.iconInner = reg.registerIcon(this.getTextureName() + "_inner");
        this.iconTop = reg.registerIcon(this.getTextureName() + "_top");
        this.iconBottom = reg.registerIcon(this.getTextureName() + "_bottom");
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        this.handleWorld(worldIn, x, y, z, player);
        return true;
    }

    @Override
    public void onBlockClicked(World worldIn, int x, int y, int z, EntityPlayer player) {
        this.handleWorld(worldIn, x, y, z, player);
    }

    public void handleWorld(World world, int n, int n2, int n3, EntityPlayer entityPlayer) {
        if (entityPlayer.canEat(false)) {
            entityPlayer.getFoodStats().addStats(2, 0.1F);
            int n4 = world.getBlockMetadata(n, n2, n3) + 1;
            if (n4 >= 6) {
                world.setBlockToAir(n, n2, n3);
            } else {
                world.setBlockMetadataWithNotify(n, n2, n3, n4, 2);
            }
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return super.canPlaceBlockAt(worldIn, x, y, z) && this.canBlockStay(worldIn, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!this.canBlockStay(worldIn, x, y, z)) {
            worldIn.setBlockToAir(x, y, z);
        }
    }

    @Override
    public boolean canBlockStay(World worldIn, int x, int y, int z) {
        return worldIn.getBlock(x, y - 1, z).getMaterial().isSolid();
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return null;
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Items.cake;
    }
}
