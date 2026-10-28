package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NBTTagList extends NBTBase {
    public List tagList = new ArrayList();
    public byte tagType = 0;

    @Override
    public void write(DataOutput output) throws IOException {
        if (!this.tagList.isEmpty()) {
            this.tagType = ((NBTBase)this.tagList.get(0)).getId();
        } else {
            this.tagType = 0;
        }

        output.writeByte(this.tagType);
        output.writeInt(this.tagList.size());

        for (int i = 0; i < this.tagList.size(); i++) {
            ((NBTBase)this.tagList.get(i)).write(output);
        }
    }

    @Override
    public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        if (depth > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        }

        sizeTracker.addSpaceRead(8L);
        this.tagType = input.readByte();
        int readInt = input.readInt();
        this.tagList = new ArrayList();

        for (int i = 0; i < readInt; i++) {
            NBTBase createNewByType = NBTBase.createNewByType(this.tagType);
            createNewByType.read(input, depth + 1, sizeTracker);
            this.tagList.add(createNewByType);
        }
    }

    @Override
    public byte getId() {
        return 9;
    }

    @Override
    public String toString() {
        String str = "[";
        int n = 0;

        for (NBTBase nbtbase : (Iterable<NBTBase>)this.tagList) {
            str = str + "" + n + ':' + nbtbase + ',';
            n++;
        }

        return str + "]";
    }

    public void appendTag(NBTBase nbtbase) {
        if (this.tagType == 0) {
            this.tagType = nbtbase.getId();
        } else if (this.tagType != nbtbase.getId()) {
            System.err.println("WARNING: Adding mismatching tag types to tag list");
            return;
        }

        this.tagList.add(nbtbase);
    }

    public void setTag(int i, NBTBase nbtbase) {
        if (i >= 0 && i < this.tagList.size()) {
            if (this.tagType == 0) {
                this.tagType = nbtbase.getId();
            } else if (this.tagType != nbtbase.getId()) {
                System.err.println("WARNING: Adding mismatching tag types to tag list");
                return;
            }

            this.tagList.set(i, nbtbase);
        } else {
            System.err.println("WARNING: index out of bounds to set tag in tag list");
        }
    }

    public NBTBase removeTag(int i) {
        return (NBTBase)this.tagList.remove(i);
    }

    public NBTTagCompound getCompoundTagAt(int i) {
        if (i >= 0 && i < this.tagList.size()) {
            NBTBase nbtbase = (NBTBase)this.tagList.get(i);
            return nbtbase.getId() == 10 ? (NBTTagCompound)nbtbase : new NBTTagCompound();
        } else {
            return new NBTTagCompound();
        }
    }

    public int[] getIntArrayAt(int i) {
        if (i >= 0 && i < this.tagList.size()) {
            NBTBase nbtbase = (NBTBase)this.tagList.get(i);
            return nbtbase.getId() == 11 ? ((NBTTagIntArray)nbtbase).getIntArray() : new int[0];
        } else {
            return new int[0];
        }
    }

    public double getDoubleAt(int i) {
        if (i >= 0 && i < this.tagList.size()) {
            NBTBase nbtbase = (NBTBase)this.tagList.get(i);
            return nbtbase.getId() == 6 ? ((NBTTagDouble)nbtbase).getDouble() : 0.0;
        } else {
            return 0.0;
        }
    }

    public float getFloatAt(int i) {
        if (i >= 0 && i < this.tagList.size()) {
            NBTBase nbtbase = (NBTBase)this.tagList.get(i);
            return nbtbase.getId() == 5 ? ((NBTTagFloat)nbtbase).getFloat() : 0.0F;
        } else {
            return 0.0F;
        }
    }

    public String getStringTagAt(int i) {
        if (i >= 0 && i < this.tagList.size()) {
            NBTBase nbtbase = (NBTBase)this.tagList.get(i);
            return nbtbase.getId() == 8 ? nbtbase.getString() : nbtbase.toString();
        } else {
            return "";
        }
    }

    public int tagCount() {
        return this.tagList.size();
    }

    @Override
    public NBTBase copy() {
        NBTTagList nbttagList = new NBTTagList();
        nbttagList.tagType = this.tagType;

        for (NBTBase nbtbase : (Iterable<NBTBase>)this.tagList) {
            NBTBase nbtbase2 = nbtbase.copy();
            nbttagList.tagList.add(nbtbase2);
        }

        return nbttagList;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            NBTTagList nbttagList = (NBTTagList)obj;
            if (this.tagType == nbttagList.tagType) {
                return this.tagList.equals(nbttagList.tagList);
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.tagList.hashCode();
    }

    public int getTagType() {
        return this.tagType;
    }
}
