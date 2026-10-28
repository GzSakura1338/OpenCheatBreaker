package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.world.World;

public class ItemHoe extends Item {
    public ToolMaterial theToolMaterial;

    public ItemHoe(ToolMaterial toolMaterial) {
        this.theToolMaterial = toolMaterial;
        this.maxStackSize = 1;
        this.setMaxDurability(toolMaterial.getMaxUses());
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        if (!entityPlayer.canPlayerEdit(n, n2, n3, n4, itemStack)) {
            return false;
        }

        Block block = world.getBlock(n, n2, n3);
        if (n4 != 0 && world.getBlock(n, n2 + 1, n3).getMaterial() == Material.air && (block == Blocks.grass || block == Blocks.dirt)) {
            Block farmland = Blocks.farmland;
            world.playSoundEffect(
                n + 0.5F,
                n2 + 0.5F,
                n3 + 0.5F,
                farmland.stepSound.getStepSound(),
                (farmland.stepSound.getVolume() + 1.0F) / 2.0F,
                farmland.stepSound.getFrequency() * 0.8F
            );
            if (world.isRemote) {
                return true;
            }

            world.setBlock(n, n2, n3, farmland);
            itemStack.damageItem(1, entityPlayer);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    public String getMaterialName() {
        return this.theToolMaterial.toString();
    }
}
