package net.minecraft.world.storage;

import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.util.IProgressUpdate;

public interface ISaveFormat {
    String constantText();

    ISaveHandler getSaveLoader(String str, boolean flag);

    List getSaveList() throws AnvilConverterException;

    void flushCache();

    WorldInfo getWorldInfo(String str);

    boolean deleteWorldDirectory(String str);

    void renameWorld(String str, String str2);

    boolean isOldMapFormat(String str);

    boolean convertMapFormat(String str, IProgressUpdate progressUpdate) throws java.io.IOException;

    boolean canLoadWorld(String str);

    boolean checkString(String str);

    boolean checkString2(String str);
}
