package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagByteArray extends NBTBase {
    public byte[] byteArray;

    public NBTTagByteArray() {
    }

    public NBTTagByteArray(byte[] bytes) {
        this.byteArray = bytes;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.byteArray.length);
        output.write(this.byteArray);
    }

    @Override
    public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        int readInt = input.readInt();
        sizeTracker.addSpaceRead(8 * readInt);
        this.byteArray = new byte[readInt];
        input.readFully(this.byteArray);
    }

    @Override
    public byte getId() {
        return 7;
    }

    @Override
    public String toString() {
        return "[" + this.byteArray.length + " bytes]";
    }

    @Override
    public NBTBase copy() {
        byte[] bytes = new byte[this.byteArray.length];
        System.arraycopy(this.byteArray, 0, bytes, 0, this.byteArray.length);
        return new NBTTagByteArray(bytes);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && Arrays.equals(this.byteArray, ((NBTTagByteArray)obj).byteArray);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Arrays.hashCode(this.byteArray);
    }

    public byte[] getByteArray() {
        return this.byteArray;
    }
}
