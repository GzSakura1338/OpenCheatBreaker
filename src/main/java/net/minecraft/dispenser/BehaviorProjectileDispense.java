package net.minecraft.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BehaviorProjectileDispense extends BehaviorDefaultDispenseItem {
    @Override
    public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
        World world = source.getWorld();
        IPosition iPositionFromBlockSource = BlockDispenser.getIPositionFromBlockSource(source);
        EnumFacing facingDirection = BlockDispenser.getFacingDirection(source.getBlockMetadata());
        IProjectile projectileEntity = this.getProjectileEntity(world, iPositionFromBlockSource);
        projectileEntity.setThrowableHeading(facingDirection.getFrontOffsetX(), facingDirection.getFrontOffsetY() + 0.1F, facingDirection.getFrontOffsetZ(), this.getFloatValue2(), this.getFloatValue());
        world.spawnEntityInWorld((Entity)projectileEntity);
        stack.splitStack(1);
        return stack;
    }

    @Override
    public void playDispenseSound(IBlockSource source) {
        source.getWorld().playAuxSFX(1002, source.getXInt(), source.getYInt(), source.getZInt(), 0);
    }

    public abstract IProjectile getProjectileEntity(World world, IPosition position);

    public float getFloatValue() {
        return 6.0F;
    }

    public float getFloatValue2() {
        return 1.1F;
    }
}
