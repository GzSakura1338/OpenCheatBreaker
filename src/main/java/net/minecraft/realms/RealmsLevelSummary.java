package net.minecraft.realms;

import net.minecraft.world.storage.SaveFormatComparator;

public class RealmsLevelSummary implements Comparable<RealmsLevelSummary> {
   public SaveFormatComparator levelSummary;

   public RealmsLevelSummary(SaveFormatComparator saveFormatComparator) {
      this.levelSummary = saveFormatComparator;
   }

   public int getGameMode() {
      return this.levelSummary.getEnumGameType().getID();
   }

   public String getLevelId() {
      return this.levelSummary.getFileName();
   }

   public String getLevelName() {
      return this.levelSummary.getDisplayName();
   }

   public long getLastPlayed() {
      return this.levelSummary.getLastTimePlayed();
   }

   public int compareTo(SaveFormatComparator saveFormatComparator) {
      return this.levelSummary.compareTo(saveFormatComparator);
   }

   public long getSizeOnDisk() {
      return this.levelSummary.getSizeOnDisk();
   }

   public int compareTo(RealmsLevelSummary realmsLevelSummary) {
      return this.levelSummary.getLastTimePlayed() < realmsLevelSummary.getLastPlayed()
         ? 1
         : (this.levelSummary.getLastTimePlayed() > realmsLevelSummary.getLastPlayed() ? -1 : this.levelSummary.getFileName().compareTo(realmsLevelSummary.getLevelId()));
   }

   public boolean isHardcoreModeEnabled() {
      return this.levelSummary.isHardcoreModeEnabled();
   }

   public boolean requiresConversion() {
      return this.levelSummary.requiresConversion();
   }

   public boolean getCheatsEnabled() {
      return this.levelSummary.getCheatsEnabled();
   }
}
