package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockPumpkin extends BlockDirectional {
    public boolean flag;
    public IIcon iIcon3;
    public IIcon iIcon2;

    public BlockPumpkin(boolean flag) {
        super(Material.gourd);
        this.setTickRandomly(true);
        this.flag = flag;
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1
            ? this.iIcon3
            : (
                side == 0
                    ? this.iIcon3
                    : (
                        meta == 2 && side == 2
                            ? this.iIcon2
                            : (meta == 3 && side == 5 ? this.iIcon2 : (meta == 0 && side == 3 ? this.iIcon2 : (meta == 1 && side == 4 ? this.iIcon2 : this.blockIcon)))
                    )
            );
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        super.onBlockAdded(worldIn, x, y, z);
        if (worldIn.getBlock(x, y - 1, z) == Blocks.snow && worldIn.getBlock(x, y - 2, z) == Blocks.snow) {
            if (!worldIn.isRemote) {
                worldIn.setBlock(x, y, z, getBlockById(0), 0, 2);
                worldIn.setBlock(x, y - 1, z, getBlockById(0), 0, 2);
                worldIn.setBlock(x, y - 2, z, getBlockById(0), 0, 2);
                EntitySnowman entitySnowman = new EntitySnowman(worldIn);
                entitySnowman.setLocationAndAngles(x + 0.5, y - 1.95, z + 0.5, 0.0F, 0.0F);
                worldIn.spawnEntityInWorld(entitySnowman);
                worldIn.notifyBlockChange(x, y, z, getBlockById(0));
                worldIn.notifyBlockChange(x, y - 1, z, getBlockById(0));
                worldIn.notifyBlockChange(x, y - 2, z, getBlockById(0));
            }

            for (int i = 0; i < 120; i++) {
                worldIn.spawnParticle(
                    "snowshovel", x + worldIn.rand.nextDouble(), y - 2 + worldIn.rand.nextDouble() * 2.5, z + worldIn.rand.nextDouble(), 0.0, 0.0, 0.0
                );
            }
        } else if (worldIn.getBlock(x, y - 1, z) == Blocks.iron_block && worldIn.getBlock(x, y - 2, z) == Blocks.iron_block) {
            boolean flag = worldIn.getBlock(x - 1, y - 1, z) == Blocks.iron_block && worldIn.getBlock(x + 1, y - 1, z) == Blocks.iron_block;
            boolean flag2 = worldIn.getBlock(x, y - 1, z - 1) == Blocks.iron_block && worldIn.getBlock(x, y - 1, z + 1) == Blocks.iron_block;
            if (flag || flag2) {
                worldIn.setBlock(x, y, z, getBlockById(0), 0, 2);
                worldIn.setBlock(x, y - 1, z, getBlockById(0), 0, 2);
                worldIn.setBlock(x, y - 2, z, getBlockById(0), 0, 2);
                if (flag) {
                    worldIn.setBlock(x - 1, y - 1, z, getBlockById(0), 0, 2);
                    worldIn.setBlock(x + 1, y - 1, z, getBlockById(0), 0, 2);
                } else {
                    worldIn.setBlock(x, y - 1, z - 1, getBlockById(0), 0, 2);
                    worldIn.setBlock(x, y - 1, z + 1, getBlockById(0), 0, 2);
                }

                EntityIronGolem entityIronGolem = new EntityIronGolem(worldIn);
                entityIronGolem.setPlayerCreated(true);
                entityIronGolem.setLocationAndAngles(x + 0.5, y - 1.95, z + 0.5, 0.0F, 0.0F);
                worldIn.spawnEntityInWorld(entityIronGolem);

                for (int i = 0; i < 120; i++) {
                    worldIn.spawnParticle(
                        "snowballpoof", x + worldIn.rand.nextDouble(), y - 2 + worldIn.rand.nextDouble() * 3.9, z + worldIn.rand.nextDouble(), 0.0, 0.0, 0.0
                    );
                }

                worldIn.notifyBlockChange(x, y, z, getBlockById(0));
                worldIn.notifyBlockChange(x, y - 1, z, getBlockById(0));
                worldIn.notifyBlockChange(x, y - 2, z, getBlockById(0));
                if (flag) {
                    worldIn.notifyBlockChange(x - 1, y - 1, z, getBlockById(0));
                    worldIn.notifyBlockChange(x + 1, y - 1, z, getBlockById(0));
                } else {
                    worldIn.notifyBlockChange(x, y - 1, z - 1, getBlockById(0));
                    worldIn.notifyBlockChange(x, y - 1, z + 1, getBlockById(0));
                }
            }
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return worldIn.getBlock(x, y, z).blockMaterial.isReplaceable() && World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        int n = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 2.5) & 3;
        worldIn.setBlockMetadataWithNotify(x, y, z, n, 2);
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iIcon2 = reg.registerIcon(this.getTextureName() + "_face_" + (this.flag ? "on" : "off"));
        this.iIcon3 = reg.registerIcon(this.getTextureName() + "_top");
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
    }
}
