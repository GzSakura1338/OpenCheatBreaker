package net.minecraft.world.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.world.WorldSavedData;

public class MapStorage {
    public Map loadedDataMap = new HashMap();
    public List loadedDataList = new ArrayList();
    public Map idCounts = new HashMap();
    public ISaveHandler saveHandler;

    public MapStorage(ISaveHandler saveHandler) {
        this.saveHandler = saveHandler;
        this.loadIdCounts();
    }

    public WorldSavedData loadData(Class clazz, String str) {
        WorldSavedData worldSavedData = (WorldSavedData)this.loadedDataMap.get(str);
        if (worldSavedData != null) {
            return worldSavedData;
        }

        if (this.saveHandler != null) {
            try {
                File mapFileFromName = this.saveHandler.getMapFileFromName(str);
                if (mapFileFromName != null && mapFileFromName.exists()) {
                    try {
                        worldSavedData = (WorldSavedData)clazz.getConstructor(String.class).newInstance(str);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to instantiate " + clazz.toString(), e);
                    }

                    FileInputStream fileInputStream = new FileInputStream(mapFileFromName);
                    NBTTagCompound readCompressed = CompressedStreamTools.readCompressed(fileInputStream);
                    fileInputStream.close();
                    worldSavedData.readFromNBT(readCompressed.getCompoundTag("data"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (worldSavedData != null) {
            this.loadedDataMap.put(str, worldSavedData);
            this.loadedDataList.add(worldSavedData);
        }

        return worldSavedData;
    }

    public void setData(String str, WorldSavedData worldSavedData) {
        if (worldSavedData == null) {
            throw new RuntimeException("Can't set null data");
        }

        if (this.loadedDataMap.containsKey(str)) {
            this.loadedDataList.remove(this.loadedDataMap.remove(str));
        }

        this.loadedDataMap.put(str, worldSavedData);
        this.loadedDataList.add(worldSavedData);
    }

    public void saveAllData() {
        for (int i = 0; i < this.loadedDataList.size(); i++) {
            WorldSavedData worldSavedData = (WorldSavedData)this.loadedDataList.get(i);
            if (worldSavedData.isDirty()) {
                this.saveData(worldSavedData);
                worldSavedData.setDirty(false);
            }
        }
    }

    public void saveData(WorldSavedData worldSavedData) {
        if (this.saveHandler != null) {
            try {
                File mapFileFromName = this.saveHandler.getMapFileFromName(worldSavedData.mapName);
                if (mapFileFromName != null) {
                    NBTTagCompound nbttagCompound = new NBTTagCompound();
                    worldSavedData.writeToNBT(nbttagCompound);
                    NBTTagCompound nbttagCompound2 = new NBTTagCompound();
                    nbttagCompound2.setTag("data", nbttagCompound);
                    FileOutputStream fileOutputStream = new FileOutputStream(mapFileFromName);
                    CompressedStreamTools.writeCompressed(nbttagCompound2, fileOutputStream);
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadIdCounts() {
        try {
            this.idCounts.clear();
            if (this.saveHandler == null) {
                return;
            }

            File mapFileFromName = this.saveHandler.getMapFileFromName("idcounts");
            if (mapFileFromName != null && mapFileFromName.exists()) {
                DataInputStream dataInputStream = new DataInputStream(new FileInputStream(mapFileFromName));
                NBTTagCompound read = CompressedStreamTools.read(dataInputStream);
                dataInputStream.close();

                for (String str : (Iterable<String>)read.getKeySet()) {
                    NBTBase tag = read.getTag(str);
                    if (tag instanceof NBTTagShort) {
                        NBTTagShort nbttagShort = (NBTTagShort)tag;
                        short short2 = nbttagShort.getShort();
                        this.idCounts.put(str, short2);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getUniqueDataId(String str) {
        Short shortValue = (Short)this.idCounts.get(str);
        if (shortValue == null) {
            shortValue = (short)0;
        } else {
            shortValue = (short)(shortValue + 1);
        }

        this.idCounts.put(str, shortValue);
        if (this.saveHandler == null) {
            return shortValue;
        }

        try {
            File mapFileFromName = this.saveHandler.getMapFileFromName("idcounts");
            if (mapFileFromName != null) {
                NBTTagCompound nbttagCompound = new NBTTagCompound();

                for (String str2 : (Iterable<String>)this.idCounts.keySet()) {
                    short s = (Short)this.idCounts.get(str2);
                    nbttagCompound.setShort(str2, s);
                }

                DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(mapFileFromName));
                CompressedStreamTools.write(nbttagCompound, dataOutputStream);
                dataOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return shortValue;
    }
}
