package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockFarmland extends Block {
    public IIcon iconDry;
    public IIcon iconWet;

    public BlockFarmland() {
        super(Material.ground);
        this.setTickRandomly(true);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
        this.setLightOpacity(255);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x + 0, y + 0, z + 0, x + 1, y + 1, z + 1);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? (meta > 0 ? this.iconDry : this.iconWet) : Blocks.dirt.getBlockTextureFromSide(side);
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!this.checkWorld(worldIn, x, y, z) && !worldIn.isRainingAt(x, y + 1, z)) {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            if (blockMetadata > 0) {
                worldIn.setBlockMetadataWithNotify(x, y, z, blockMetadata - 1, 2);
            } else if (!this.getBlock(worldIn, x, y, z)) {
                worldIn.setBlock(x, y, z, Blocks.dirt);
            }
        } else {
            worldIn.setBlockMetadataWithNotify(x, y, z, 7, 2);
        }
    }

    @Override
    public void onFallenUpon(World worldIn, int x, int y, int z, Entity entityIn, float fallDistance) {
        if (!worldIn.isRemote && worldIn.rand.nextFloat() < fallDistance - 0.5F) {
            if (!(entityIn instanceof EntityPlayer) && !worldIn.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
                return;
            }

            worldIn.setBlock(x, y, z, Blocks.dirt);
        }
    }

    public boolean getBlock(World world, int n, int n2, int n3) {
        byte b = 0;

        for (int i = n - b; i <= n + b; i++) {
            for (int j = n3 - b; j <= n3 + b; j++) {
                Block block = world.getBlock(i, n2 + 1, j);
                if (block == Blocks.wheat || block == Blocks.melon_stem || block == Blocks.pumpkin_stem || block == Blocks.potatoes || block == Blocks.carrots) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean checkWorld(World world, int n, int n2, int n3) {
        for (int i = n - 4; i <= n + 4; i++) {
            for (int j = n2; j <= n2 + 1; j++) {
                for (int k = n3 - 4; k <= n3 + 4; k++) {
                    if (world.getBlock(i, j, k).getMaterial() == Material.water) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
        Material material = worldIn.getBlock(x, y + 1, z).getMaterial();
        if (material.isSolid()) {
            worldIn.setBlock(x, y, z, Blocks.dirt);
        }
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Blocks.dirt.getItemDropped(0, random, fortune);
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Item.getItemFromBlock(Blocks.dirt);
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iconDry = reg.registerIcon(this.getTextureName() + "_wet");
        this.iconWet = reg.registerIcon(this.getTextureName() + "_dry");
    }
}
