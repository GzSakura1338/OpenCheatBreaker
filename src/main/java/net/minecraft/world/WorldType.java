package net.minecraft.world;

public class WorldType {
   public static WorldType[] worldTypes = new WorldType[16];
   public static WorldType DEFAULT = new WorldType(0, "default", 1).setVersioned();
   public static WorldType FLAT = new WorldType(1, "flat");
   public static WorldType LARGE_BIOMES = new WorldType(2, "largeBiomes");
   public static WorldType AMPLIFIED = new WorldType(3, "amplified").setNotificationData();
   public static WorldType CUSTOMIZED = new WorldType(4, "customized");
   public static WorldType DEBUG_WORLD = new WorldType(5, "debug_all_block_states");
   public static WorldType DEFAULT_1_1 = new WorldType(8, "default_1_1", 0).setCanBeCreated(false);
   public String worldType;
   public int generatorVersion;
   public boolean canBeCreated;
   public int worldTypeId;
   public boolean isWorldTypeVersioned;
   public boolean hasNotificationData;

   public WorldType(int id, String name) {
      this(id, name, 0);
   }

   public WorldType(int id, String name, int version) {
      this.worldType = name;
      this.generatorVersion = version;
      this.canBeCreated = true;
      this.worldTypeId = id;
      worldTypes[id] = this;
   }

   public String getWorldTypeName() {
      return this.worldType;
   }

   public String getTranslateName() {
      return "generator." + this.worldType;
   }

   public String getTranslatedInfo() {
      return this.getTranslateName() + ".info";
   }

   public int getGeneratorVersion() {
      return this.generatorVersion;
   }

   public WorldType getWorldTypeForGeneratorVersion(int version) {
      return this == DEFAULT && version == 0 ? DEFAULT_1_1 : this;
   }

   public WorldType setCanBeCreated(boolean enable) {
      this.canBeCreated = enable;
      return this;
   }

   public boolean getCanBeCreated() {
      return this.canBeCreated;
   }

   public WorldType setVersioned() {
      this.isWorldTypeVersioned = true;
      return this;
   }

   public boolean isVersioned() {
      return this.isWorldTypeVersioned;
   }

   public static WorldType parseWorldType(String type) {
      for (int i = 0; i < worldTypes.length; i++) {
         if (worldTypes[i] != null && worldTypes[i].worldType.equalsIgnoreCase(type)) {
            return worldTypes[i];
         }
      }

      return null;
   }

   public int getWorldTypeID() {
      return this.worldTypeId;
   }

   public boolean showWorldInfoNotice() {
      return this.hasNotificationData;
   }

   public WorldType setNotificationData() {
      this.hasNotificationData = true;
      return this;
   }
}
