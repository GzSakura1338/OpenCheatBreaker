package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.world.WorldSavedData;

public class MapStorage {
   public Map<String, WorldSavedData> loadedDataMap = Maps.newHashMap();
   public List<WorldSavedData> loadedDataList = Lists.newArrayList();
   public Map<String, Short> idCounts = Maps.newHashMap();
   public ISaveHandler saveHandler;

   public MapStorage(ISaveHandler saveHandlerIn) {
      this.saveHandler = saveHandlerIn;
      this.loadIdCounts();
   }

   public WorldSavedData loadData(Class<? extends WorldSavedData> clazz, String dataIdentifier) {
      WorldSavedData worldSavedData = this.loadedDataMap.get(dataIdentifier);
      if (worldSavedData != null) {
         return worldSavedData;
      }

      if (this.saveHandler != null) {
         try {
            File mapFileFromName = this.saveHandler.getMapFileFromName(dataIdentifier);
            if (mapFileFromName != null && mapFileFromName.exists()) {
               try {
                  worldSavedData = (WorldSavedData)clazz.getConstructor(String.class).newInstance(dataIdentifier);
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
         this.loadedDataMap.put(dataIdentifier, worldSavedData);
         this.loadedDataList.add(worldSavedData);
      }

      return worldSavedData;
   }

   public void setData(String dataIdentifier, WorldSavedData data) {
      if (this.loadedDataMap.containsKey(dataIdentifier)) {
         this.loadedDataList.remove(this.loadedDataMap.remove(dataIdentifier));
      }

      this.loadedDataMap.put(dataIdentifier, data);
      this.loadedDataList.add(data);
   }

   public void saveAllData() {
      for (int i = 0; i < this.loadedDataList.size(); i++) {
         WorldSavedData worldSavedData = this.loadedDataList.get(i);
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

            for (String str : read.getKeySet()) {
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

   public int getUniqueDataId(String key) {
      Short shortValue = this.idCounts.get(key);
      if (shortValue == null) {
         shortValue = (short)0;
      } else {
         shortValue = (short)(shortValue + 1);
      }

      this.idCounts.put(key, shortValue);
      if (this.saveHandler == null) {
         return shortValue;
      }

      try {
         File mapFileFromName = this.saveHandler.getMapFileFromName("idcounts");
         if (mapFileFromName != null) {
            NBTTagCompound nbttagCompound = new NBTTagCompound();

            for (String str : this.idCounts.keySet()) {
               short s = this.idCounts.get(str);
               nbttagCompound.setShort(str, s);
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
