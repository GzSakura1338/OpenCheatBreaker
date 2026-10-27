package net.minecraft.world.storage;

import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.util.IProgressUpdate;

public interface ISaveFormat {
   String getName();

   ISaveHandler getSaveLoader(String str, boolean flag);

   List<SaveFormatComparator> getSaveList() throws AnvilConverterException;

   void flushCache();

   WorldInfo getWorldInfo(String str);

   boolean deleteWorldDirectory(String str);

   void renameWorld(String str, String str2);

   boolean isOldMapFormat(String str);

   boolean convertMapFormat(String str, IProgressUpdate progressUpdate);

   boolean canLoadWorld(String str);

   boolean isNewLevelIdAcceptable(String str);

   boolean isConvertible(String str);
}
