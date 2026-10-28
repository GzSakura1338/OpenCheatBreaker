package net.minecraft.realms;

import net.minecraft.world.storage.SaveFormatComparator;

public class RealmsLevelSummary implements Comparable {
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

    @Override
    public int compareTo(Object obj) {
        return this.compareTo((RealmsLevelSummary)obj);
    }

    public boolean getCheatsEnabled() {
        return this.levelSummary.getCheatsEnabled();
    }

    public boolean requiresConversion() {
        return this.levelSummary.requiresConversion();
    }

    public boolean isHardcoreModeEnabled() {
        return this.levelSummary.isHardcoreModeEnabled();
    }
}
