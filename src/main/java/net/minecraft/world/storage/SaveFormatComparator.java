package net.minecraft.world.storage;

import net.minecraft.world.WorldSettings.GameType;

public class SaveFormatComparator implements Comparable {
    public String fileName;
    public String displayName;
    public long lastTimePlayed;
    public long sizeOnDisk;
    public GameType theEnumGameType;
    public boolean requiresConversion;
    public boolean hardcore;
    public boolean cheatsEnabled;

    public SaveFormatComparator(String str, String str2, long l, long l2, GameType gameType, boolean flag, boolean flag2, boolean flag3) {
        this.fileName = str;
        this.displayName = str2;
        this.lastTimePlayed = l;
        this.sizeOnDisk = l2;
        this.theEnumGameType = gameType;
        this.requiresConversion = flag;
        this.hardcore = flag2;
        this.cheatsEnabled = flag3;
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

    @Override
    public int compareTo(Object obj) {
        return this.compareTo((SaveFormatComparator)obj);
    }
}
