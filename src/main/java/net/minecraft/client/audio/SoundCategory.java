package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import java.util.Map;

public enum SoundCategory {
   MASTER("master", 0),
   MUSIC("music", 1),
   RECORDS("record", 2),
   WEATHER("weather", 3),
   BLOCKS("block", 4),
   MOBS("hostile", 5),
   ANIMALS("neutral", 6),
   PLAYERS("player", 7),
   AMBIENT("ambient", 8);

   public static Map<String, SoundCategory> NAME_CATEGORY_MAP = Maps.newHashMap();
   public String categoryName;
   public int categoryId;
   public static Map<Integer, SoundCategory> ID_CATEGORY_MAP = Maps.newHashMap();

   SoundCategory(String name, int id) {
      this.categoryName = name;
      this.categoryId = id;
   }

   public String getCategoryName() {
      return this.categoryName;
   }

   public int getCategoryId() {
      return this.categoryId;
   }

   public static SoundCategory getCategory(String name) {
      return NAME_CATEGORY_MAP.get(name);
   }

   static {
      for (SoundCategory soundCategory : values()) {
         if (NAME_CATEGORY_MAP.containsKey(soundCategory.getCategoryName()) || ID_CATEGORY_MAP.containsKey(soundCategory.getCategoryId())) {
            throw new Error("Clash in Sound Category ID & Name pools! Cannot insert " + soundCategory);
         }

         NAME_CATEGORY_MAP.put(soundCategory.getCategoryName(), soundCategory);
         ID_CATEGORY_MAP.put(soundCategory.getCategoryId(), soundCategory);
      }
   }
}
