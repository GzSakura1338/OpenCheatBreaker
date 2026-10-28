package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorDefaultDispenseItem implements IBehaviorDispenseItem {
    @Override
    public ItemStack dispense(IBlockSource source, ItemStack stack) {
        ItemStack dispenseStack = this.dispenseStack(source, stack);
        this.playDispenseSound(source);
        this.spawnDispenseParticles(source, BlockDispenser.getFacingDirection(source.getBlockMetadata()));
        return dispenseStack;
    }

    public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
        EnumFacing facingDirection = BlockDispenser.getFacingDirection(source.getBlockMetadata());
        IPosition iPositionFromBlockSource = BlockDispenser.getIPositionFromBlockSource(source);
        ItemStack splitStack = stack.splitStack(1);
        doDispense(source.getWorld(), splitStack, 6, facingDirection, iPositionFromBlockSource);
        return stack;
    }

    public static void doDispense(World worldIn, ItemStack stack, int speed, EnumFacing enumFacing, IPosition position) {
        double d = position.getX();
        double d2 = position.getY();
        double d3 = position.getZ();
        EntityItem entityItem = new EntityItem(worldIn, d, d2 - 0.3, d3, stack);
        double d4 = worldIn.rand.nextDouble() * 0.1 + 0.2;
        entityItem.motionX = enumFacing.getFrontOffsetX() * d4;
        entityItem.motionY = 0.2F;
        entityItem.motionZ = enumFacing.getFrontOffsetZ() * d4;
        entityItem.motionX = entityItem.motionX + worldIn.rand.nextGaussian() * 0.0075F * speed;
        entityItem.motionY = entityItem.motionY + worldIn.rand.nextGaussian() * 0.0075F * speed;
        entityItem.motionZ = entityItem.motionZ + worldIn.rand.nextGaussian() * 0.0075F * speed;
        worldIn.spawnEntityInWorld(entityItem);
    }

    public void playDispenseSound(IBlockSource source) {
        source.getWorld().playAuxSFX(1000, source.getXInt(), source.getYInt(), source.getZInt(), 0);
    }

    public void spawnDispenseParticles(IBlockSource source, EnumFacing facingIn) {
        source.getWorld().playAuxSFX(2000, source.getXInt(), source.getYInt(), source.getZInt(), this.getDataFromDirection(facingIn));
    }

    public int getDataFromDirection(EnumFacing facingIn) {
        return facingIn.getFrontOffsetX() + 1 + (facingIn.getFrontOffsetZ() + 1) * 3;
    }
}
