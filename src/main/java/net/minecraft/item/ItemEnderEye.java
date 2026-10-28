package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public class ItemEnderEye extends Item {
    public ItemEnderEye() {
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        Block block = world.getBlock(n, n2, n3);
        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        if (!entityPlayer.canPlayerEdit(n, n2, n3, n4, itemStack) || block != Blocks.end_portal_frame || BlockEndPortalFrame.isEnderEyeInserted(blockMetadata)) {
            return false;
        }

        if (world.isRemote) {
            return true;
        }

        world.setBlockMetadataWithNotify(n, n2, n3, blockMetadata + 4, 2);
        world.updateNeighborsAboutBlockChange(n, n2, n3, Blocks.end_portal_frame);
        itemStack.stackSize--;

        for (int i = 0; i < 16; i++) {
            double d = n + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F;
            double d2 = n2 + 0.8125F;
            double d3 = n3 + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F;
            double d4 = 0.0;
            double d5 = 0.0;
            double d6 = 0.0;
            world.spawnParticle("smoke", d, d2, d3, d4, d5, d6);
        }

        int n5 = blockMetadata & 3;
        int n6 = 0;
        int n7 = 0;
        boolean flag = false;
        boolean flag2 = true;
        int n8 = Direction.enderEyeMetaToDirection[n5];

        for (int i = -2; i <= 2; i++) {
            int n9 = n + Direction.offsetX[n8] * i;
            int n10 = n3 + Direction.offsetZ[n8] * i;
            if (world.getBlock(n9, n2, n10) == Blocks.end_portal_frame) {
                if (!BlockEndPortalFrame.isEnderEyeInserted(world.getBlockMetadata(n9, n2, n10))) {
                    flag2 = false;
                    break;
                }

                n7 = i;
                if (!flag) {
                    n6 = i;
                    flag = true;
                }
            }
        }

        if (flag2 && n7 == n6 + 2) {
            for (int i = n6; i <= n7; i++) {
                int n9 = n + Direction.offsetX[n8] * i;
                int n10 = n3 + Direction.offsetZ[n8] * i;
                n9 += Direction.offsetX[n5] * 4;
                n10 += Direction.offsetZ[n5] * 4;
                if (world.getBlock(n9, n2, n10) != Blocks.end_portal_frame
                    || !BlockEndPortalFrame.isEnderEyeInserted(world.getBlockMetadata(n9, n2, n10))) {
                    flag2 = false;
                    break;
                }
            }

            for (int i = n6 - 1; i <= n7 + 1; i += 4) {
                for (int j = 1; j <= 3; j++) {
                    int n9 = n + Direction.offsetX[n8] * i;
                    int n10 = n3 + Direction.offsetZ[n8] * i;
                    n9 += Direction.offsetX[n5] * j;
                    n10 += Direction.offsetZ[n5] * j;
                    if (world.getBlock(n9, n2, n10) != Blocks.end_portal_frame
                        || !BlockEndPortalFrame.isEnderEyeInserted(world.getBlockMetadata(n9, n2, n10))) {
                        flag2 = false;
                        break;
                    }
                }
            }

            if (flag2) {
                for (int i = n6; i <= n7; i++) {
                    for (int j = 1; j <= 3; j++) {
                        int n9 = n + Direction.offsetX[n8] * i;
                        int n10 = n3 + Direction.offsetZ[n8] * i;
                        n9 += Direction.offsetX[n5] * j;
                        n10 += Direction.offsetZ[n5] * j;
                        world.setBlock(n9, n2, n10, Blocks.end_portal, 0, 2);
                    }
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        MovingObjectPosition movingObjectPositionFromPlayer = this.getMovingObjectPositionFromPlayer(worldIn, player, false);
        if (movingObjectPositionFromPlayer != null && movingObjectPositionFromPlayer.typeOfHit == MovingObjectType.BLOCK && worldIn.getBlock(movingObjectPositionFromPlayer.blockX, movingObjectPositionFromPlayer.blockY, movingObjectPositionFromPlayer.blockZ) == Blocks.end_portal_frame) {
            return itemStackIn;
        }

        if (!worldIn.isRemote) {
            ChunkPosition findClosestStructure = worldIn.findClosestStructure("Stronghold", (int)player.posX, (int)player.posY, (int)player.posZ);
            if (findClosestStructure != null) {
                EntityEnderEye entityEnderEye = new EntityEnderEye(worldIn, player.posX, player.posY + 1.62 - player.yOffset, player.posZ);
                entityEnderEye.moveTowards(findClosestStructure.chunkPosX, findClosestStructure.chunkPosY, findClosestStructure.chunkPosZ);
                worldIn.spawnEntityInWorld(entityEnderEye);
                worldIn.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
                worldIn.playAuxSFXAtEntity(null, 1002, (int)player.posX, (int)player.posY, (int)player.posZ, 0);
                if (!player.capabilities.isCreativeMode) {
                    itemStackIn.stackSize--;
                }
            }
        }

        return itemStackIn;
    }
}
