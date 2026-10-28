package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class ItemGlassBottle extends Item {
    public ItemGlassBottle() {
        this.setCreativeTab(CreativeTabs.tabBrewing);
    }

    @Override
    public IIcon getIconFromDamage(int n) {
        return Items.potionitem.getIconFromDamage(0);
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

            if (worldIn.getBlock(blockX, blockY, blockZ).getMaterial() == Material.water) {
                itemStackIn.stackSize--;
                if (itemStackIn.stackSize <= 0) {
                    return new ItemStack(Items.potionitem);
                }

                if (!player.inventory.addItemStackToInventory(new ItemStack(Items.potionitem))) {
                    player.dropPlayerItemWithRandomChoice(new ItemStack(Items.potionitem, 1, 0), false);
                }
            }
        }

        return itemStackIn;
    }

    @Override
    public void registerIcons(IIconRegister register) {
    }
}
