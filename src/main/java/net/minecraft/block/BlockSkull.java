package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSkull extends BlockContainer {
    public BlockSkull() {
        super(Material.circuits);
        this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
    }

    @Override
    public int getRenderType() {
        return -1;
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
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        int n = worldIn.getBlockMetadata(x, y, z) & 7;
        switch (n) {
            case 1:
            default:
                this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
                break;
            case 2:
                this.setBlockBounds(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
                break;
            case 3:
                this.setBlockBounds(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
                break;
            case 4:
                this.setBlockBounds(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
                break;
            case 5:
                this.setBlockBounds(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        int n = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 2.5) & 3;
        worldIn.setBlockMetadataWithNotify(x, y, z, n, 2);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntitySkull();
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Items.skull;
    }

    @Override
    public int getDamageValue(World worldIn, int x, int y, int z) {
        TileEntity tileEntity = worldIn.getTileEntity(x, y, z);
        return tileEntity != null && tileEntity instanceof TileEntitySkull ? ((TileEntitySkull)tileEntity).getSkullType() : super.getDamageValue(worldIn, x, y, z);
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune) {
    }

    @Override
    public void onBlockHarvested(World worldIn, int x, int y, int z, int meta, EntityPlayer player) {
        if (player.capabilities.isCreativeMode) {
            meta |= 8;
            worldIn.setBlockMetadataWithNotify(x, y, z, meta, 4);
        }

        super.onBlockHarvested(worldIn, x, y, z, meta, player);
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        if (!worldIn.isRemote) {
            if ((meta & 8) == 0) {
                ItemStack itemStack = new ItemStack(Items.skull, 1, this.getDamageValue(worldIn, x, y, z));
                TileEntitySkull tileEntity = (TileEntitySkull)worldIn.getTileEntity(x, y, z);
                if (tileEntity.getSkullType() == 3 && tileEntity.getOwner() != null) {
                    itemStack.setTagCompound(new NBTTagCompound());
                    NBTTagCompound nbttagCompound = new NBTTagCompound();
                    NBTUtil.writeGameProfileToNBT(nbttagCompound, tileEntity.getOwner());
                    itemStack.getTagCompound().setTag("SkullOwner", nbttagCompound);
                }

                this.dropBlockAsItem(worldIn, x, y, z, itemStack);
            }

            super.breakBlock(worldIn, x, y, z, blockBroken, meta);
        }
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Items.skull;
    }

    public void makeWither(World world, int n, int n2, int n3, TileEntitySkull tileEntitySkull) {
        if (tileEntitySkull.getSkullType() == 1 && n2 >= 2 && world.difficultySetting != EnumDifficulty.PEACEFUL && !world.isRemote) {
            for (int i = -2; i <= 0; i++) {
                if (world.getBlock(n, n2 - 1, n3 + i) == Blocks.soul_sand
                    && world.getBlock(n, n2 - 1, n3 + i + 1) == Blocks.soul_sand
                    && world.getBlock(n, n2 - 2, n3 + i + 1) == Blocks.soul_sand
                    && world.getBlock(n, n2 - 1, n3 + i + 2) == Blocks.soul_sand
                    && this.checkWorld(world, n, n2, n3 + i, 1)
                    && this.checkWorld(world, n, n2, n3 + i + 1, 1)
                    && this.checkWorld(world, n, n2, n3 + i + 2, 1)) {
                    world.setBlockMetadataWithNotify(n, n2, n3 + i, 8, 2);
                    world.setBlockMetadataWithNotify(n, n2, n3 + i + 1, 8, 2);
                    world.setBlockMetadataWithNotify(n, n2, n3 + i + 2, 8, 2);
                    world.setBlock(n, n2, n3 + i, getBlockById(0), 0, 2);
                    world.setBlock(n, n2, n3 + i + 1, getBlockById(0), 0, 2);
                    world.setBlock(n, n2, n3 + i + 2, getBlockById(0), 0, 2);
                    world.setBlock(n, n2 - 1, n3 + i, getBlockById(0), 0, 2);
                    world.setBlock(n, n2 - 1, n3 + i + 1, getBlockById(0), 0, 2);
                    world.setBlock(n, n2 - 1, n3 + i + 2, getBlockById(0), 0, 2);
                    world.setBlock(n, n2 - 2, n3 + i + 1, getBlockById(0), 0, 2);
                    if (!world.isRemote) {
                        EntityWither entityWither = new EntityWither(world);
                        entityWither.setLocationAndAngles(n + 0.5, n2 - 1.45, n3 + i + 1.5, 90.0F, 0.0F);
                        entityWither.renderYawOffset = 90.0F;
                        entityWither.onSummoned();
                        if (!world.isRemote) {
                            for (EntityPlayer entityPlayer : (Iterable<EntityPlayer>)world.getEntitiesWithinAABB(EntityPlayer.class, entityWither.boundingBox.expand(50.0, 50.0, 50.0))) {
                                entityPlayer.triggerAchievement(AchievementList.spawnWither);
                            }
                        }

                        world.spawnEntityInWorld(entityWither);
                    }

                    for (int j = 0; j < 120; j++) {
                        world.spawnParticle(
                            "snowballpoof",
                            n + world.rand.nextDouble(),
                            n2 - 2 + world.rand.nextDouble() * 3.9,
                            n3 + i + 1 + world.rand.nextDouble(),
                            0.0,
                            0.0,
                            0.0
                        );
                    }

                    world.notifyBlockChange(n, n2, n3 + i, getBlockById(0));
                    world.notifyBlockChange(n, n2, n3 + i + 1, getBlockById(0));
                    world.notifyBlockChange(n, n2, n3 + i + 2, getBlockById(0));
                    world.notifyBlockChange(n, n2 - 1, n3 + i, getBlockById(0));
                    world.notifyBlockChange(n, n2 - 1, n3 + i + 1, getBlockById(0));
                    world.notifyBlockChange(n, n2 - 1, n3 + i + 2, getBlockById(0));
                    world.notifyBlockChange(n, n2 - 2, n3 + i + 1, getBlockById(0));
                    return;
                }
            }

            for (int i = -2; i <= 0; i++) {
                if (world.getBlock(n + i, n2 - 1, n3) == Blocks.soul_sand
                    && world.getBlock(n + i + 1, n2 - 1, n3) == Blocks.soul_sand
                    && world.getBlock(n + i + 1, n2 - 2, n3) == Blocks.soul_sand
                    && world.getBlock(n + i + 2, n2 - 1, n3) == Blocks.soul_sand
                    && this.checkWorld(world, n + i, n2, n3, 1)
                    && this.checkWorld(world, n + i + 1, n2, n3, 1)
                    && this.checkWorld(world, n + i + 2, n2, n3, 1)) {
                    world.setBlockMetadataWithNotify(n + i, n2, n3, 8, 2);
                    world.setBlockMetadataWithNotify(n + i + 1, n2, n3, 8, 2);
                    world.setBlockMetadataWithNotify(n + i + 2, n2, n3, 8, 2);
                    world.setBlock(n + i, n2, n3, getBlockById(0), 0, 2);
                    world.setBlock(n + i + 1, n2, n3, getBlockById(0), 0, 2);
                    world.setBlock(n + i + 2, n2, n3, getBlockById(0), 0, 2);
                    world.setBlock(n + i, n2 - 1, n3, getBlockById(0), 0, 2);
                    world.setBlock(n + i + 1, n2 - 1, n3, getBlockById(0), 0, 2);
                    world.setBlock(n + i + 2, n2 - 1, n3, getBlockById(0), 0, 2);
                    world.setBlock(n + i + 1, n2 - 2, n3, getBlockById(0), 0, 2);
                    if (!world.isRemote) {
                        EntityWither entityWither = new EntityWither(world);
                        entityWither.setLocationAndAngles(n + i + 1.5, n2 - 1.45, n3 + 0.5, 0.0F, 0.0F);
                        entityWither.onSummoned();
                        if (!world.isRemote) {
                            for (EntityPlayer entityPlayer : (Iterable<EntityPlayer>)world.getEntitiesWithinAABB(EntityPlayer.class, entityWither.boundingBox.expand(50.0, 50.0, 50.0))) {
                                entityPlayer.triggerAchievement(AchievementList.spawnWither);
                            }
                        }

                        world.spawnEntityInWorld(entityWither);
                    }

                    for (int j = 0; j < 120; j++) {
                        world.spawnParticle(
                            "snowballpoof",
                            n + i + 1 + world.rand.nextDouble(),
                            n2 - 2 + world.rand.nextDouble() * 3.9,
                            n3 + world.rand.nextDouble(),
                            0.0,
                            0.0,
                            0.0
                        );
                    }

                    world.notifyBlockChange(n + i, n2, n3, getBlockById(0));
                    world.notifyBlockChange(n + i + 1, n2, n3, getBlockById(0));
                    world.notifyBlockChange(n + i + 2, n2, n3, getBlockById(0));
                    world.notifyBlockChange(n + i, n2 - 1, n3, getBlockById(0));
                    world.notifyBlockChange(n + i + 1, n2 - 1, n3, getBlockById(0));
                    world.notifyBlockChange(n + i + 2, n2 - 1, n3, getBlockById(0));
                    world.notifyBlockChange(n + i + 1, n2 - 2, n3, getBlockById(0));
                    return;
                }
            }
        }
    }

    public boolean checkWorld(World world, int n, int n2, int n3, int n4) {
        if (world.getBlock(n, n2, n3) != this) {
            return false;
        }

        TileEntity tileEntity = world.getTileEntity(n, n2, n3);
        return tileEntity != null && tileEntity instanceof TileEntitySkull && ((TileEntitySkull)tileEntity).getSkullType() == n4;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return Blocks.soul_sand.getBlockTextureFromSide(side);
    }

    @Override
    public String getItemIconName() {
        return this.getTextureName() + "_" + ItemSkull.stringArray[0];
    }
}
