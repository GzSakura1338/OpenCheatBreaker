package net.minecraft.realms;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.SaveFormatComparator;

public class RealmsAnvilLevelStorageSource {
    public ISaveFormat levelStorageSource;

    public RealmsAnvilLevelStorageSource(ISaveFormat saveFormat) {
        this.levelStorageSource = saveFormat;
    }

    public String getName() {
        return this.levelStorageSource.constantText();
    }

    public boolean convertLevel(String str, IProgressUpdate progressUpdate) throws java.io.IOException {
        return this.levelStorageSource.convertMapFormat(str, progressUpdate);
    }

    public void renameLevel(String str, String str2) {
        this.levelStorageSource.renameWorld(str, str2);
    }

    public void clearAll() {
        this.levelStorageSource.flushCache();
    }

    public List getLevelList() throws AnvilConverterException {
        ArrayList list = new ArrayList();

        for (SaveFormatComparator saveFormatComparator : (Iterable<SaveFormatComparator>)this.levelStorageSource.getSaveList()) {
            list.add(new RealmsLevelSummary(saveFormatComparator));
        }

        return list;
    }

    public boolean isOldMapFormat(String str) {
        return this.levelStorageSource.isOldMapFormat(str);
    }

    public boolean checkString(String str) {
        return this.levelStorageSource.checkString2(str);
    }

    public boolean checkString2(String str) {
        return this.levelStorageSource.checkString(str);
    }

    public boolean canLoadWorld(String str) {
        return this.levelStorageSource.canLoadWorld(str);
    }

    public boolean deleteWorldDirectory(String str) {
        return this.levelStorageSource.deleteWorldDirectory(str);
    }
}
