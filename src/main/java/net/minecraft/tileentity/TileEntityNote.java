package net.minecraft.tileentity;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileEntityNote extends TileEntity {
    public byte note;
    public boolean previousRedstoneState;

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setByte("note", this.note);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.note = compound.getByte("note");
        if (this.note < 0) {
            this.note = 0;
        }

        if (this.note > 24) {
            this.note = 24;
        }
    }

    public void changePitch() {
        this.note = (byte)((this.note + 1) % 25);
        this.markDirty();
    }

    public void triggerNote(World world, int n, int n2, int n3) {
        if (world.getBlock(n, n2 + 1, n3).getMaterial() == Material.air) {
            Material material = world.getBlock(n, n2 - 1, n3).getMaterial();
            byte b = 0;
            if (material == Material.rock) {
                b = 1;
            }

            if (material == Material.sand) {
                b = 2;
            }

            if (material == Material.glass) {
                b = 3;
            }

            if (material == Material.wood) {
                b = 4;
            }

            world.addBlockEvent(n, n2, n3, Blocks.noteblock, b, this.note);
        }
    }
}
