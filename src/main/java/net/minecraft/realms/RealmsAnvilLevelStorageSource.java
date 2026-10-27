package net.minecraft.realms;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.SaveFormatComparator;

public class RealmsAnvilLevelStorageSource {
   public ISaveFormat levelStorageSource;

   public RealmsAnvilLevelStorageSource(ISaveFormat levelStorageSourceIn) {
      this.levelStorageSource = levelStorageSourceIn;
   }

   public String getName() {
      return this.levelStorageSource.getName();
   }

   public boolean convertLevel(String str, IProgressUpdate progressUpdate) {
      return this.levelStorageSource.convertMapFormat(str, progressUpdate);
   }

   public void renameLevel(String str, String str2) {
      this.levelStorageSource.renameWorld(str, str2);
   }

   public void clearAll() {
      this.levelStorageSource.flushCache();
   }

   public List<RealmsLevelSummary> getLevelList() throws AnvilConverterException {
      ArrayList newArrayList = Lists.newArrayList();

      for (SaveFormatComparator saveFormatComparator : this.levelStorageSource.getSaveList()) {
         newArrayList.add(new RealmsLevelSummary(saveFormatComparator));
      }

      return newArrayList;
   }

   public boolean isConvertible(String str) {
      return this.levelStorageSource.isConvertible(str);
   }

   public boolean canLoadWorld(String str) {
      return this.levelStorageSource.canLoadWorld(str);
   }

   public boolean isNewLevelIdAcceptable(String str) {
      return this.levelStorageSource.isNewLevelIdAcceptable(str);
   }

   public boolean deleteWorldDirectory(String str) {
      return this.levelStorageSource.deleteWorldDirectory(str);
   }

   public boolean isOldMapFormat(String str) {
      return this.levelStorageSource.isOldMapFormat(str);
   }
}
