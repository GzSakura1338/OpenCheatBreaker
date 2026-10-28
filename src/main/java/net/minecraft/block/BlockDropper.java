package net.minecraft.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

public class BlockDropper extends BlockDispenser {
    public IBehaviorDispenseItem BEHAVIOR = new BehaviorDefaultDispenseItem();

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon("furnace_side");
        this.iIcon4 = reg.registerIcon("furnace_top");
        this.iIcon2 = reg.registerIcon(this.getTextureName() + "_front_horizontal");
        this.iIcon3 = reg.registerIcon(this.getTextureName() + "_front_vertical");
    }

    @Override
    public IBehaviorDispenseItem getBehaviorForItem(ItemStack itemStack) {
        return this.BEHAVIOR;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDropper();
    }

    @Override
    public void handleWorld2(World world, int n, int n2, int n3) {
        BlockSourceImpl blockSourceImpl = new BlockSourceImpl(world, n, n2, n3);
        TileEntityDispenser blockTileEntity = (TileEntityDispenser)blockSourceImpl.getBlockTileEntity();
        if (blockTileEntity != null) {
            int chooseNonEmptySlot = blockTileEntity.chooseNonEmptySlot();
            if (chooseNonEmptySlot < 0) {
                world.playAuxSFX(1001, n, n2, n3, 0);
            } else {
                ItemStack stackInSlot = blockTileEntity.getStackInSlot(chooseNonEmptySlot);
                int n4 = world.getBlockMetadata(n, n2, n3) & 7;
                IInventory inventoryAt = TileEntityHopper.getInventoryAt(
                    world, n + Facing.offsetsXForSide[n4], n2 + Facing.offsetsYForSide[n4], n3 + Facing.offsetsZForSide[n4]
                );
                ItemStack itemStack;
                if (inventoryAt != null) {
                    itemStack = TileEntityHopper.findItemStack(inventoryAt, stackInSlot.copy().splitStack(1), Facing.oppositeSide[n4]);
                    if (itemStack == null) {
                        itemStack = stackInSlot.copy();
                        if (--itemStack.stackSize == 0) {
                            itemStack = null;
                        }
                    } else {
                        itemStack = stackInSlot.copy();
                    }
                } else {
                    itemStack = this.BEHAVIOR.dispense(blockSourceImpl, stackInSlot);
                    if (itemStack != null && itemStack.stackSize == 0) {
                        itemStack = null;
                    }
                }

                blockTileEntity.setInventorySlotContents(chooseNonEmptySlot, itemStack);
            }
        }
    }
}
