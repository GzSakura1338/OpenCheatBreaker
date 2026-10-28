package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockTNT extends Block {
    public IIcon top;
    public IIcon bottom;

    public BlockTNT() {
        super(Material.tnt);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 0 ? this.bottom : (side == 1 ? this.top : this.blockIcon);
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        super.onBlockAdded(worldIn, x, y, z);
        if (worldIn.isBlockIndirectlyGettingPowered(x, y, z)) {
            this.onBlockDestroyedByPlayer(worldIn, x, y, z, 1);
            worldIn.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (worldIn.isBlockIndirectlyGettingPowered(x, y, z)) {
            this.onBlockDestroyedByPlayer(worldIn, x, y, z, 1);
            worldIn.setBlockToAir(x, y, z);
        }
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public void onBlockDestroyedByExplosion(World worldIn, int x, int y, int z, Explosion explosionIn) {
        if (!worldIn.isRemote) {
            EntityTNTPrimed entityTNTPrimed = new EntityTNTPrimed(worldIn, x + 0.5F, y + 0.5F, z + 0.5F, explosionIn.getExplosivePlacedBy());
            entityTNTPrimed.fuse = worldIn.rand.nextInt(entityTNTPrimed.fuse / 4) + entityTNTPrimed.fuse / 8;
            worldIn.spawnEntityInWorld(entityTNTPrimed);
        }
    }

    @Override
    public void onBlockDestroyedByPlayer(World worldIn, int x, int y, int z, int meta) {
        this.onTntPrimed(worldIn, x, y, z, meta, null);
    }

    public void onTntPrimed(World world, int n, int n2, int n3, int n4, EntityLivingBase entityLivingBase) {
        if (!world.isRemote && (n4 & 1) == 1) {
            EntityTNTPrimed entityTNTPrimed = new EntityTNTPrimed(world, n + 0.5F, n2 + 0.5F, n3 + 0.5F, entityLivingBase);
            world.spawnEntityInWorld(entityTNTPrimed);
            world.playSoundAtEntity(entityTNTPrimed, "game.tnt.primed", 1.0F, 1.0F);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) throws java.io.IOException {
        if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.flint_and_steel) {
            this.onTntPrimed(worldIn, x, y, z, 1, player);
            worldIn.setBlockToAir(x, y, z);
            player.getCurrentEquippedItem().damageItem(1, player);
            return true;
        } else {
            return super.onBlockActivated(worldIn, x, y, z, player, side, subX, subY, subZ);
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn) {
        if (entityIn instanceof EntityArrow && !worldIn.isRemote) {
            EntityArrow entityIn2 = (EntityArrow)entityIn;
            if (entityIn2.isBurning()) {
                this.onTntPrimed(worldIn, x, y, z, 1, entityIn2.shootingEntity instanceof EntityLivingBase ? (EntityLivingBase)entityIn2.shootingEntity : null);
                worldIn.setBlockToAir(x, y, z);
            }
        }
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosionIn) {
        return false;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
        this.top = reg.registerIcon(this.getTextureName() + "_top");
        this.bottom = reg.registerIcon(this.getTextureName() + "_bottom");
    }
}
