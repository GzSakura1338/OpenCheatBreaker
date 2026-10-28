package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemSnow extends ItemBlockWithMetadata {
    public ItemSnow(Block block, Block block2) {
        super(block, block2);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        if (itemStack.stackSize == 0) {
            return false;
        }

        if (!entityPlayer.canPlayerEdit(n, n2, n3, n4, itemStack)) {
            return false;
        }

        Block block = world.getBlock(n, n2, n3);
        if (block == Blocks.snow_layer) {
            int blockMetadata = world.getBlockMetadata(n, n2, n3);
            int n5 = blockMetadata & 7;
            if (n5 <= 6
                && world.checkNoEntityCollision(this.blockInstance.getCollisionBoundingBoxFromPool(world, n, n2, n3))
                && world.setBlockMetadataWithNotify(n, n2, n3, n5 + 1 | blockMetadata & -8, 2)) {
                world.playSoundEffect(
                    n + 0.5F,
                    n2 + 0.5F,
                    n3 + 0.5F,
                    this.blockInstance.stepSound.getPlaceSound(),
                    (this.blockInstance.stepSound.getVolume() + 1.0F) / 2.0F,
                    this.blockInstance.stepSound.getFrequency() * 0.8F
                );
                itemStack.stackSize--;
                return true;
            }
        }

        return super.onItemUse(itemStack, entityPlayer, world, n, n2, n3, n4, f, f2, f3);
    }
}
