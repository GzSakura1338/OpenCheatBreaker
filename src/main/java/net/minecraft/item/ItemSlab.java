package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemSlab extends ItemBlock {
    public BlockSlab singleSlab;
    public BlockSlab doubleSlab;
    public boolean flag;

    public ItemSlab(Block block, BlockSlab blockSlab, BlockSlab blockSlab2, boolean flag) {
        super(block);
        this.singleSlab = blockSlab;
        this.doubleSlab = blockSlab2;
        this.flag = flag;
        this.setMaxDurability(0);
        this.setHasSubtypes(true);
    }

    @Override
    public IIcon getIconFromDamage(int n) {
        return Block.getBlockFromItem(this).getIcon(2, n);
    }

    @Override
    public int getMetadata(int n) {
        return n;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.singleSlab.getFullSlabName(stack.getMetadata());
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        if (this.flag) {
            return super.onItemUse(itemStack, entityPlayer, world, n, n2, n3, n4, f, f2, f3);
        }

        if (itemStack.stackSize == 0) {
            return false;
        }

        if (!entityPlayer.canPlayerEdit(n, n2, n3, n4, itemStack)) {
            return false;
        }

        Block block = world.getBlock(n, n2, n3);
        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        int n5 = blockMetadata & 7;
        boolean flag = (blockMetadata & 8) != 0;
        if ((n4 == 1 && !flag || n4 == 0 && flag) && block == this.singleSlab && n5 == itemStack.getMetadata()) {
            if (world.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBoxFromPool(world, n, n2, n3))
                && world.setBlock(n, n2, n3, this.doubleSlab, n5, 3)) {
                world.playSoundEffect(
                    n + 0.5F,
                    n2 + 0.5F,
                    n3 + 0.5F,
                    this.doubleSlab.stepSound.getPlaceSound(),
                    (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F,
                    this.doubleSlab.stepSound.getFrequency() * 0.8F
                );
                itemStack.stackSize--;
            }

            return true;
        } else {
            return this.checkItemStack(itemStack, entityPlayer, world, n, n2, n3, n4) || super.onItemUse(itemStack, entityPlayer, world, n, n2, n3, n4, f, f2, f3);
        }
    }

    @Override
    public boolean checkWorld(World world, int n, int n2, int n3, int n4, EntityPlayer entityPlayer, ItemStack itemStack) {
        int n5 = n;
        int n6 = n2;
        int n7 = n3;
        Block block = world.getBlock(n, n2, n3);
        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        int n8 = blockMetadata & 7;
        boolean flag = (blockMetadata & 8) != 0;
        if ((n4 == 1 && !flag || n4 == 0 && flag) && block == this.singleSlab && n8 == itemStack.getMetadata()) {
            return true;
        }

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

        Block block2 = world.getBlock(n, n2, n3);
        int blockMetadata2 = world.getBlockMetadata(n, n2, n3);
        n8 = blockMetadata2 & 7;
        return block2 == this.singleSlab && n8 == itemStack.getMetadata() || super.checkWorld(world, n5, n6, n7, n4, entityPlayer, itemStack);
    }

    public boolean checkItemStack(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4) {
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

        Block block = world.getBlock(n, n2, n3);
        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        int n5 = blockMetadata & 7;
        if (block == this.singleSlab && n5 == itemStack.getMetadata()) {
            if (world.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBoxFromPool(world, n, n2, n3))
                && world.setBlock(n, n2, n3, this.doubleSlab, n5, 3)) {
                world.playSoundEffect(
                    n + 0.5F,
                    n2 + 0.5F,
                    n3 + 0.5F,
                    this.doubleSlab.stepSound.getPlaceSound(),
                    (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F,
                    this.doubleSlab.stepSound.getFrequency() * 0.8F
                );
                itemStack.stackSize--;
            }

            return true;
        } else {
            return false;
        }
    }
}
