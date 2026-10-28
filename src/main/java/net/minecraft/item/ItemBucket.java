package net.minecraft.item;

import com.cheatbreaker.client.CheatBreaker;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class ItemBucket extends Item {
    public Block isFull;

    public ItemBucket(Block block) {
        this.maxStackSize = 1;
        this.isFull = block;
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        boolean flag = this.isFull == Blocks.air;
        MovingObjectPosition movingObjectPositionFromPlayer = this.getMovingObjectPositionFromPlayer(worldIn, player, flag);
        if (movingObjectPositionFromPlayer == null) {
            return itemStackIn;
        }

        if (movingObjectPositionFromPlayer.typeOfHit == MovingObjectType.BLOCK) {
            int blockX = movingObjectPositionFromPlayer.blockX;
            int blockY = movingObjectPositionFromPlayer.blockY;
            int blockZ = movingObjectPositionFromPlayer.blockZ;
            if (!worldIn.canMineBlock(player, blockX, blockY, blockZ)) {
                return itemStackIn;
            }

            if (flag) {
                if (!player.canPlayerEdit(blockX, blockY, blockZ, movingObjectPositionFromPlayer.sideHit, itemStackIn)) {
                    return itemStackIn;
                }

                Material material = worldIn.getBlock(blockX, blockY, blockZ).getMaterial();
                int blockMetadata = worldIn.getBlockMetadata(blockX, blockY, blockZ);
                if (material == Material.water && blockMetadata == 0) {
                    worldIn.setBlockToAir(blockX, blockY, blockZ);
                    return this.fill(itemStackIn, player, Items.water_bucket);
                }

                if (material == Material.lava && blockMetadata == 0) {
                    worldIn.setBlockToAir(blockX, blockY, blockZ);
                    return this.fill(itemStackIn, player, Items.lava_bucket);
                }
            } else {
                if (this.isFull == Blocks.air) {
                    return new ItemStack(Items.bucket);
                }

                if (movingObjectPositionFromPlayer.sideHit == 0) {
                    blockY--;
                }

                if (movingObjectPositionFromPlayer.sideHit == 1) {
                    blockY++;
                }

                if (movingObjectPositionFromPlayer.sideHit == 2) {
                    blockZ--;
                }

                if (movingObjectPositionFromPlayer.sideHit == 3) {
                    blockZ++;
                }

                if (movingObjectPositionFromPlayer.sideHit == 4) {
                    blockX--;
                }

                if (movingObjectPositionFromPlayer.sideHit == 5) {
                    blockX++;
                }

                if (!player.canPlayerEdit(blockX, blockY, blockZ, movingObjectPositionFromPlayer.sideHit, itemStackIn)) {
                    return itemStackIn;
                }

                if ((
                        CheatBreaker.getInstance().getKeybindSettings().ghostLiquidFix.getBooleanValue()
                            ? this.checkWorld(worldIn, blockX, blockY, blockZ)
                            : this.tryPlaceContainedLiquid(worldIn, blockX, blockY, blockZ)
                    )
                    && !player.capabilities.isCreativeMode) {
                    return new ItemStack(Items.bucket);
                }
            }
        }

        return itemStackIn;
    }

    public ItemStack fill(ItemStack itemStack, EntityPlayer entityPlayer, Item item) {
        if (entityPlayer.capabilities.isCreativeMode) {
            return itemStack;
        }

        if (--itemStack.stackSize <= 0) {
            return new ItemStack(item);
        }

        if (!entityPlayer.inventory.addItemStackToInventory(new ItemStack(item))) {
            entityPlayer.dropPlayerItemWithRandomChoice(new ItemStack(item, 1, 0), false);
        }

        return itemStack;
    }

    public boolean tryPlaceContainedLiquid(World world, int n, int n2, int n3) {
        if (this.isFull == Blocks.air) {
            return false;
        }

        Material material = world.getBlock(n, n2, n3).getMaterial();
        boolean flag = !material.isSolid();
        if (!world.isAirBlock(n, n2, n3) && !flag) {
            return false;
        }

        if (world.provider.isHellWorld && this.isFull == Blocks.flowing_water) {
            world.playSoundEffect(n + 0.5F, n2 + 0.5F, n3 + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

            for (int i = 0; i < 8; i++) {
                world.spawnParticle("largesmoke", n + Math.random(), n2 + Math.random(), n3 + Math.random(), 0.0, 0.0, 0.0);
            }
        } else {
            if (!world.isRemote && flag && !material.isLiquid()) {
                world.breakBlock(n, n2, n3, true);
            }

            world.setBlock(n, n2, n3, this.isFull, 0, 3);
        }

        return true;
    }

    public boolean checkWorld(World world, int n, int n2, int n3) {
        if (this.isFull == Blocks.air) {
            return false;
        }

        Material material = world.getBlock(n, n2, n3).getMaterial();
        boolean flag = !material.isSolid();
        if (!world.isAirBlock(n, n2, n3) && !flag) {
            return false;
        }

        if (world.provider.isHellWorld && this.isFull == Blocks.flowing_water) {
            world.playSoundEffect(n + 0.5F, n2 + 0.5F, n3 + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

            for (int i = 0; i < 8; i++) {
                world.spawnParticle("largesmoke", n + Math.random(), n2 + Math.random(), n3 + Math.random(), 0.0, 0.0, 0.0);
            }
        } else {
            if (!world.isRemote && flag && !material.isLiquid()) {
                world.breakBlock(n, n2, n3, true);
            }

            if (!world.isRemote) {
                world.setBlock(n, n2, n3, this.isFull, 0, 3);
            }
        }

        return true;
    }
}
