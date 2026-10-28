package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemDoor extends Item {
    public Material doorMaterial;

    public ItemDoor(Material material) {
        this.doorMaterial = material;
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        if (n4 != 1) {
            return false;
        }

        n2++;
        Block block;
        if (this.doorMaterial == Material.wood) {
            block = Blocks.wooden_door;
        } else {
            block = Blocks.iron_door;
        }

        if (!entityPlayer.canPlayerEdit(n, n2, n3, n4, itemStack) || !entityPlayer.canPlayerEdit(n, n2 + 1, n3, n4, itemStack)) {
            return false;
        }

        if (!block.canPlaceBlockAt(world, n, n2, n3)) {
            return false;
        }

        int n5 = MathHelper.floor_double((entityPlayer.rotationYaw + 180.0F) * 4.0F / 360.0F - 0.5) & 3;
        placeDoorBlock(world, n, n2, n3, n5, block);
        itemStack.stackSize--;
        return true;
    }

    public static void placeDoorBlock(World world, int n, int n2, int n3, int n4, Block block) {
        byte b = 0;
        byte b2 = 0;
        if (n4 == 0) {
            b2 = 1;
        }

        if (n4 == 1) {
            b = -1;
        }

        if (n4 == 2) {
            b2 = -1;
        }

        if (n4 == 3) {
            b = 1;
        }

        int n5 = (world.getBlock(n - b, n2, n3 - b2).isNormalCube() ? 1 : 0)
            + (world.getBlock(n - b, n2 + 1, n3 - b2).isNormalCube() ? 1 : 0);
        int n6 = (world.getBlock(n + b, n2, n3 + b2).isNormalCube() ? 1 : 0)
            + (world.getBlock(n + b, n2 + 1, n3 + b2).isNormalCube() ? 1 : 0);
        boolean flag = world.getBlock(n - b, n2, n3 - b2) == block || world.getBlock(n - b, n2 + 1, n3 - b2) == block;
        boolean flag2 = world.getBlock(n + b, n2, n3 + b2) == block || world.getBlock(n + b, n2 + 1, n3 + b2) == block;
        boolean flag3 = false;
        if (flag && !flag2) {
            flag3 = true;
        } else if (n6 > n5) {
            flag3 = true;
        }

        world.setBlock(n, n2, n3, block, n4, 2);
        world.setBlock(n, n2 + 1, n3, block, 8 | (flag3 ? 1 : 0), 2);
        world.notifyBlocksOfNeighborChange(n, n2, n3, block);
        world.notifyBlocksOfNeighborChange(n, n2 + 1, n3, block);
    }
}
