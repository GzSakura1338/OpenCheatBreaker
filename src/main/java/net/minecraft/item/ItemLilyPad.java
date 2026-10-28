package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class ItemLilyPad extends ItemColored {
    public ItemLilyPad(Block block) {
        super(block, false);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        MovingObjectPosition movingObjectPositionFromPlayer = this.getMovingObjectPositionFromPlayer(worldIn, player, true);
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

            if (!player.canPlayerEdit(blockX, blockY, blockZ, movingObjectPositionFromPlayer.sideHit, itemStackIn)) {
                return itemStackIn;
            }

            if (worldIn.getBlock(blockX, blockY, blockZ).getMaterial() == Material.water
                && worldIn.getBlockMetadata(blockX, blockY, blockZ) == 0
                && worldIn.isAirBlock(blockX, blockY + 1, blockZ)) {
                worldIn.setBlock(blockX, blockY + 1, blockZ, Blocks.waterlily);
                if (!player.capabilities.isCreativeMode) {
                    itemStackIn.stackSize--;
                }
            }
        }

        return itemStackIn;
    }

    @Override
    public int getColorFromItemStack(ItemStack itemStack, int n) {
        return Blocks.waterlily.getRenderColor(itemStack.getMetadata());
    }
}
