package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemReed extends Item {
    public Block block;

    public ItemReed(Block block) {
        this.block = block;
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        Block block = world.getBlock(n, n2, n3);
        if (block == Blocks.snow_layer && (world.getBlockMetadata(n, n2, n3) & 7) < 1) {
            n4 = 1;
        } else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush) {
            if (n4 == 0) {
                n2--;
            }

            if (n4 == 1) {
                n2++;
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
        }

        if (!entityPlayer.canPlayerEdit(n, n2, n3, n4, itemStack)) {
            return false;
        }

        if (itemStack.stackSize == 0) {
            return false;
        }

        if (world.canPlaceEntityOnSide(this.block, n, n2, n3, false, n4, null, itemStack)) {
            int onBlockPlaced = this.block.onBlockPlaced(world, n, n2, n3, n4, f, f2, f3, 0);
            if (world.setBlock(n, n2, n3, this.block, onBlockPlaced, 3)) {
                if (world.getBlock(n, n2, n3) == this.block) {
                    this.block.onBlockPlacedBy(world, n, n2, n3, entityPlayer, itemStack);
                    this.block.onPostBlockPlaced(world, n, n2, n3, onBlockPlaced);
                }

                world.playSoundEffect(
                    n + 0.5F,
                    n2 + 0.5F,
                    n3 + 0.5F,
                    this.block.stepSound.getPlaceSound(),
                    (this.block.stepSound.getVolume() + 1.0F) / 2.0F,
                    this.block.stepSound.getFrequency() * 0.8F
                );
                itemStack.stackSize--;
            }
        }

        return true;
    }
}
