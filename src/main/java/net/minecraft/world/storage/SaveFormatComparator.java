package net.minecraft.world.storage;

import net.minecraft.world.WorldSettings.GameType;

public class SaveFormatComparator implements Comparable<SaveFormatComparator> {
   public String fileName;
   public String displayName;
   public long lastTimePlayed;
   public long sizeOnDisk;
   public GameType theEnumGameType;
   public boolean requiresConversion;
   public boolean hardcore;
   public boolean cheatsEnabled;

   public SaveFormatComparator(
      String fileNameIn,
      String displayNameIn,
      long lastTimePlayedIn,
      long sizeOnDiskIn,
      GameType theEnumGameTypeIn,
      boolean requiresConversionIn,
      boolean hardcoreIn,
      boolean cheatsEnabledIn
   ) {
      this.fileName = fileNameIn;
      this.displayName = displayNameIn;
      this.lastTimePlayed = lastTimePlayedIn;
      this.sizeOnDisk = sizeOnDiskIn;
      this.theEnumGameType = theEnumGameTypeIn;
      this.requiresConversion = requiresConversionIn;
      this.hardcore = hardcoreIn;
      this.cheatsEnabled = cheatsEnabledIn;
   }

   public String getFileName() {
      return this.fileName;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public long getSizeOnDisk() {
      return this.sizeOnDisk;
   }

   public boolean requiresConversion() {
      return this.requiresConversion;
   }

   public long getLastTimePlayed() {
      return this.lastTimePlayed;
   }

   public int compareTo(SaveFormatComparator saveFormatComparator) {
      return this.lastTimePlayed < saveFormatComparator.lastTimePlayed ? 1 : (this.lastTimePlayed > saveFormatComparator.lastTimePlayed ? -1 : this.fileName.compareTo(saveFormatComparator.fileName));
   }

   public GameType getEnumGameType() {
      return this.theEnumGameType;
   }

   public boolean isHardcoreModeEnabled() {
      return this.hardcore;
   }

   public boolean getCheatsEnabled() {
      return this.cheatsEnabled;
   }
}
