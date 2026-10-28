package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class BlockBed extends BlockDirectional {
    public static int[][] bedDirections = new int[][]{{0, 1}, {-1, 0}, {0, -1}, {1, 0}};
    public IIcon[] iconEnd;
    public IIcon[] iconSide;
    public IIcon[] iconTop;

    public BlockBed() {
        super(Material.cloth);
        this.setBedBounds();
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        if (worldIn.isRemote) {
            return true;
        }

        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        if (!isBlockHeadOfBed(blockMetadata)) {
            int direction = getDirection(blockMetadata);
            x += bedDirections[direction][0];
            z += bedDirections[direction][1];
            if (worldIn.getBlock(x, y, z) != this) {
                return true;
            }

            blockMetadata = worldIn.getBlockMetadata(x, y, z);
        }

        if (worldIn.provider.canRespawnHere() && worldIn.getBiomeGenForCoords(x, z) != BiomeGenBase.hell) {
            if (isBedOccupied(blockMetadata)) {
                EntityPlayer entityPlayer = null;

                for (EntityPlayer entityPlayer2 : worldIn.playerEntities) {
                    if (entityPlayer2.isPlayerSleeping()) {
                        ChunkCoordinates playerLocation = entityPlayer2.playerLocation;
                        if (playerLocation.posX == x && playerLocation.posY == y && playerLocation.posZ == z) {
                            entityPlayer = entityPlayer2;
                        }
                    }
                }

                if (entityPlayer != null) {
                    player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.occupied"));
                    return true;
                }

                setBedOccupied(worldIn, x, y, z, false);
            }

            EnumStatus sleepInBedAt = player.sleepInBedAt(x, y, z);
            if (sleepInBedAt == EnumStatus.OK) {
                setBedOccupied(worldIn, x, y, z, true);
                return true;
            }

            if (sleepInBedAt == EnumStatus.NOT_POSSIBLE_NOW) {
                player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.noSleep"));
            } else if (sleepInBedAt == EnumStatus.NOT_SAFE) {
                player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.notSafe"));
            }

            return true;
        } else {
            double d = x + 0.5;
            double d2 = y + 0.5;
            double d3 = z + 0.5;
            worldIn.setBlockToAir(x, y, z);
            int direction = getDirection(blockMetadata);
            x += bedDirections[direction][0];
            z += bedDirections[direction][1];
            if (worldIn.getBlock(x, y, z) == this) {
                worldIn.setBlockToAir(x, y, z);
                d = (d + x + 0.5) / 2.0;
                d2 = (d2 + y + 0.5) / 2.0;
                d3 = (d3 + z + 0.5) / 2.0;
            }

            worldIn.newExplosion(null, x + 0.5F, y + 0.5F, z + 0.5F, 5.0F, true, true);
            return true;
        }
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 0) {
            return Blocks.planks.getBlockTextureFromSide(side);
        }

        int direction = getDirection(meta);
        int n = Direction.bedDirection[direction][side];
        int n2 = isBlockHeadOfBed(meta) ? 1 : 0;
        return (n2 != 1 || n != 2) && (n2 != 0 || n != 3) ? (n != 5 && n != 4 ? this.iconTop[n2] : this.iconSide[n2]) : this.iconEnd[n2];
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iconTop = new IIcon[]{reg.registerIcon(this.getTextureName() + "_feet_top"), reg.registerIcon(this.getTextureName() + "_head_top")};
        this.iconEnd = new IIcon[]{reg.registerIcon(this.getTextureName() + "_feet_end"), reg.registerIcon(this.getTextureName() + "_head_end")};
        this.iconSide = new IIcon[]{reg.registerIcon(this.getTextureName() + "_feet_side"), reg.registerIcon(this.getTextureName() + "_head_side")};
    }

    @Override
    public int getRenderType() {
        return 14;
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
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        this.setBedBounds();
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        int direction = getDirection(blockMetadata);
        if (isBlockHeadOfBed(blockMetadata)) {
            if (worldIn.getBlock(x - bedDirections[direction][0], y, z - bedDirections[direction][1]) != this) {
                worldIn.setBlockToAir(x, y, z);
            }
        } else if (worldIn.getBlock(x + bedDirections[direction][0], y, z + bedDirections[direction][1]) != this) {
            worldIn.setBlockToAir(x, y, z);
            if (!worldIn.isRemote) {
                this.dropBlockAsItem(worldIn, x, y, z, blockMetadata, 0);
            }
        }
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return isBlockHeadOfBed(meta) ? Item.getItemById(0) : Items.bed;
    }

    public void setBedBounds() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
    }

    public static boolean isBlockHeadOfBed(int meta) {
        return (meta & 8) != 0;
    }

    public static boolean isBedOccupied(int meta) {
        return (meta & 4) != 0;
    }

    public static void setBedOccupied(World worldIn, int x, int y, int z, boolean occupied) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        if (occupied) {
            blockMetadata |= 4;
        } else {
            blockMetadata &= -5;
        }

        worldIn.setBlockMetadataWithNotify(x, y, z, blockMetadata, 4);
    }

    public static ChunkCoordinates getSafeExitLocation(World worldIn, int x, int y, int z, int safeIndex) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        int direction = BlockDirectional.getDirection(blockMetadata);

        for (int i = 0; i <= 1; i++) {
            int n = x - bedDirections[direction][0] * i - 1;
            int n2 = z - bedDirections[direction][1] * i - 1;
            int n3 = n + 2;
            int n4 = n2 + 2;

            for (int j = n; j <= n3; j++) {
                for (int k = n2; k <= n4; k++) {
                    if (World.doesBlockHaveSolidTopSurface(worldIn, j, y - 1, k)
                        && !worldIn.getBlock(j, y, k).getMaterial().isOpaque()
                        && !worldIn.getBlock(j, y + 1, k).getMaterial().isOpaque()) {
                        if (safeIndex <= 0) {
                            return new ChunkCoordinates(j, y, k);
                        }

                        safeIndex--;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune) {
        if (!isBlockHeadOfBed(meta)) {
            super.dropBlockAsItemWithChance(worldIn, x, y, z, meta, chance, 0);
        }
    }

    @Override
    public int getMobilityFlag() {
        return 1;
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Items.bed;
    }

    @Override
    public void onBlockHarvested(World worldIn, int x, int y, int z, int meta, EntityPlayer player) {
        if (player.capabilities.isCreativeMode && isBlockHeadOfBed(meta)) {
            int direction = getDirection(meta);
            x -= bedDirections[direction][0];
            z -= bedDirections[direction][1];
            if (worldIn.getBlock(x, y, z) == this) {
                worldIn.setBlockToAir(x, y, z);
            }
        }
    }
}
