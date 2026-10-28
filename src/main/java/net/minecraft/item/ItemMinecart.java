package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemMinecart extends Item {
    public static IBehaviorDispenseItem dispenserMinecartBehavior = new BehaviorDefaultDispenseItem() {
        public BehaviorDefaultDispenseItem behaviourDefaultDispenseItem = new BehaviorDefaultDispenseItem();

        @Override
        public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            EnumFacing facingDirection = BlockDispenser.getFacingDirection(source.getBlockMetadata());
            World world = source.getWorld();
            double d = source.getX() + facingDirection.getFrontOffsetX() * 1.125F;
            double d2 = source.getY() + facingDirection.getFrontOffsetY() * 1.125F;
            double d3 = source.getZ() + facingDirection.getFrontOffsetZ() * 1.125F;
            int n = source.getXInt() + facingDirection.getFrontOffsetX();
            int n2 = source.getYInt() + facingDirection.getFrontOffsetY();
            int n3 = source.getZInt() + facingDirection.getFrontOffsetZ();
            Block block = world.getBlock(n, n2, n3);
            double d4;
            if (BlockRailBase.isRailBlock(block)) {
                d4 = 0.0;
            } else {
                if (block.getMaterial() != Material.air || !BlockRailBase.isRailBlock(world.getBlock(n, n2 - 1, n3))) {
                    return this.behaviourDefaultDispenseItem.dispense(source, stack);
                }

                d4 = -1.0;
            }

            EntityMinecart createMinecart = EntityMinecart.createMinecart(world, d, d2 + d4, d3, ((ItemMinecart)stack.getItem()).minecartType);
            if (stack.hasDisplayName()) {
                createMinecart.setMinecartName(stack.getDisplayName());
            }

            world.spawnEntityInWorld(createMinecart);
            stack.splitStack(1);
            return stack;
        }

        @Override
        public void playDispenseSound(IBlockSource source) {
            source.getWorld().playAuxSFX(1000, source.getXInt(), source.getYInt(), source.getZInt(), 0);
        }
    };
    public int minecartType;

    public ItemMinecart(int n) {
        this.maxStackSize = 1;
        this.minecartType = n;
        this.setCreativeTab(CreativeTabs.tabTransport);
        BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenserMinecartBehavior);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        if (BlockRailBase.isRailBlock(world.getBlock(n, n2, n3))) {
            if (!world.isRemote) {
                EntityMinecart createMinecart = EntityMinecart.createMinecart(world, n + 0.5F, n2 + 0.5F, n3 + 0.5F, this.minecartType);
                if (itemStack.hasDisplayName()) {
                    createMinecart.setMinecartName(itemStack.getDisplayName());
                }

                world.spawnEntityInWorld(createMinecart);
            }

            itemStack.stackSize--;
            return true;
        } else {
            return false;
        }
    }
}
