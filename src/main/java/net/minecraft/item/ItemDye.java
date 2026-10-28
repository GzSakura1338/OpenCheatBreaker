package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockLog;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemDye extends Item {
    public static String[] dyeColorNames = new String[]{
        "black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white"
    };
    public static String[] dyeIcons = new String[]{
        "black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white"
    };
    public static int[] dyeColors = new int[]{
        1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 11250603, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320
    };
    public IIcon[] dyeIconArray;

    public ItemDye() {
        this.setHasSubtypes(true);
        this.setMaxDurability(0);
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    @Override
    public IIcon getIconFromDamage(int n) {
        int n2 = MathHelper.clamp_int(n, 0, 15);
        return this.dyeIconArray[n2];
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int n = MathHelper.clamp_int(stack.getMetadata(), 0, 15);
        return super.getUnlocalizedName() + "." + dyeColorNames[n];
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        if (!entityPlayer.canPlayerEdit(n, n2, n3, n4, itemStack)) {
            return false;
        }

        if (itemStack.getMetadata() == 15) {
            if (checkItemStack(itemStack, world, n, n2, n3)) {
                if (!world.isRemote) {
                    world.playAuxSFX(2005, n, n2, n3, 0);
                }

                return true;
            }
        } else if (itemStack.getMetadata() == 3) {
            Block block = world.getBlock(n, n2, n3);
            int blockMetadata = world.getBlockMetadata(n, n2, n3);
            if (block == Blocks.log && BlockLog.computeInt4(blockMetadata) == 3) {
                if (n4 == 0) {
                    return false;
                }

                if (n4 == 1) {
                    return false;
                }

                if (n4 == 2) {
                    n3--;
                }

                if (n4 == 3) {
                    n3++;
                }

                if (n4 == 4) {
                    n--;
                }

                if (n4 == 5) {
                    n++;
                }

                if (world.isAirBlock(n, n2, n3)) {
                    int onBlockPlaced = Blocks.cocoa.onBlockPlaced(world, n, n2, n3, n4, f, f2, f3, 0);
                    world.setBlock(n, n2, n3, Blocks.cocoa, onBlockPlaced, 2);
                    if (!entityPlayer.capabilities.isCreativeMode) {
                        itemStack.stackSize--;
                    }
                }

                return true;
            }
        }

        return false;
    }

    public static boolean checkItemStack(ItemStack stack, World worldIn, int x, int y, int z) {
        Block block = worldIn.getBlock(x, y, z);
        if (block instanceof IGrowable) {
            IGrowable growable = (IGrowable)block;
            if (growable.canFertilize(worldIn, x, y, z, worldIn.isRemote)) {
                if (!worldIn.isRemote) {
                    if (growable.shouldFertilize(worldIn, worldIn.rand, x, y, z)) {
                        growable.fertilize(worldIn, worldIn.rand, x, y, z);
                    }

                    stack.stackSize--;
                }

                return true;
            }
        }

        return false;
    }

    public static void handleWorld(World world, int n, int n2, int n3, int n4) {
        if (n4 == 0) {
            n4 = 15;
        }

        Block block = world.getBlock(n, n2, n3);
        if (block.getMaterial() != Material.air) {
            block.setBlockBoundsBasedOnState(world, n, n2, n3);

            for (int i = 0; i < n4; i++) {
                double d = itemRand.nextGaussian() * 0.02;
                double d2 = itemRand.nextGaussian() * 0.02;
                double d3 = itemRand.nextGaussian() * 0.02;
                world.spawnParticle(
                    "happyVillager",
                    n + itemRand.nextFloat(),
                    n2 + itemRand.nextFloat() * block.getBlockBoundsMaxY(),
                    n3 + itemRand.nextFloat(),
                    d,
                    d2,
                    d3
                );
            }
        }
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
        if (target instanceof EntitySheep) {
            EntitySheep target2 = (EntitySheep)target;
            int computeInt = BlockColored.computeInt(stack.getMetadata());
            if (!target2.getSheared() && target2.getFleeceColor() != computeInt) {
                target2.setFleeceColor(computeInt);
                stack.stackSize--;
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        for (int i = 0; i < 16; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public void registerIcons(IIconRegister register) {
        this.dyeIconArray = new IIcon[dyeIcons.length];

        for (int i = 0; i < dyeIcons.length; i++) {
            this.dyeIconArray[i] = register.registerIcon(this.getIconString() + "_" + dyeIcons[i]);
        }
    }
}
