package net.minecraft.world.gen.structure;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class MapGenStructureData extends WorldSavedData {
    public NBTTagCompound features = new NBTTagCompound();

    public MapGenStructureData(String str) {
        super(str);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagCompound) {
        this.features = nbttagCompound.getCompoundTag("Features");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagCompound) {
        nbttagCompound.setTag("Features", this.features);
    }

    public void putFeature(NBTTagCompound nbttagCompound, int n, int n2) {
        this.features.setTag(format(n, n2), nbttagCompound);
    }

    public static String format(int n, int n2) {
        return "[" + n + "," + n2 + "]";
    }

    public NBTTagCompound getFeatures() {
        return this.features;
    }
}
