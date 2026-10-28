package net.minecraft.world;

import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.world.storage.WorldInfo;

public class WorldSettings {
    public String generatorOptions = "";
    public long seed;
    public WorldSettings.GameType theGameType;
    public boolean mapFeaturesEnabled;
    public boolean hardcoreEnabled;
    public boolean commandsAllowed;
    public boolean bonusChestEnabled;
    public WorldType terrainType;

    public WorldSettings(long l, WorldSettings.GameType gameType, boolean flag, boolean flag2, WorldType worldType) {
        this.seed = l;
        this.theGameType = gameType;
        this.mapFeaturesEnabled = flag;
        this.hardcoreEnabled = flag2;
        this.terrainType = worldType;
    }

    public WorldSettings(WorldInfo worldInfo) {
        this(worldInfo.getSeed(), worldInfo.getGameType(), worldInfo.isMapFeaturesEnabled(), worldInfo.isHardcoreModeEnabled(), worldInfo.getTerrainType());
    }

    public WorldSettings enableBonusChest() {
        this.bonusChestEnabled = true;
        return this;
    }

    public WorldSettings enableCommands() {
        this.commandsAllowed = true;
        return this;
    }

    public WorldSettings setWorldName(String str) {
        this.generatorOptions = str;
        return this;
    }

    public boolean isBonusChestEnabled() {
        return this.bonusChestEnabled;
    }

    public long getSeed() {
        return this.seed;
    }

    public WorldSettings.GameType getGameType() {
        return this.theGameType;
    }

    public boolean getHardcoreEnabled() {
        return this.hardcoreEnabled;
    }

    public boolean isMapFeaturesEnabled() {
        return this.mapFeaturesEnabled;
    }

    public WorldType getTerrainType() {
        return this.terrainType;
    }

    public boolean areCommandsAllowed() {
        return this.commandsAllowed;
    }

    public static WorldSettings.GameType getGameTypeById(int n) {
        return WorldSettings.GameType.getByID(n);
    }

    public String getGeneratorOptions() {
        return this.generatorOptions;
    }

    public enum GameType {
        NOT_SET("NOT_SET", 0, -1, ""),
        SURVIVAL("SURVIVAL", 1, 0, "survival"),
        CREATIVE("CREATIVE", 2, 1, "creative"),
        ADVENTURE("ADVENTURE", 3, 2, "adventure");

        public int id;
        public String name;

        public int getID() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public void configurePlayerCapabilities(PlayerCapabilities playerCapabilities) {
            if (this == CREATIVE) {
                playerCapabilities.allowFlying = true;
                playerCapabilities.isCreativeMode = true;
                playerCapabilities.disableDamage = true;
            } else {
                playerCapabilities.allowFlying = false;
                playerCapabilities.isCreativeMode = false;
                playerCapabilities.disableDamage = false;
                playerCapabilities.isFlying = false;
            }

            playerCapabilities.allowEdit = !this.isAdventure();
        }

        public boolean isAdventure() {
            return this == ADVENTURE;
        }

        public boolean isCreative() {
            return this == CREATIVE;
        }

        public boolean isSurvivalOrAdventure() {
            return this == SURVIVAL || this == ADVENTURE;
        }

        public static WorldSettings.GameType getByID(int n) {
            for (WorldSettings.GameType gameType : values()) {
                if (gameType.id == n) {
                    return gameType;
                }
            }

            return SURVIVAL;
        }

        public static WorldSettings.GameType getByName(String str) {
            for (WorldSettings.GameType gameType : values()) {
                if (gameType.name.equals(str)) {
                    return gameType;
                }
            }

            return SURVIVAL;
        }

        GameType(String str, int n, int n2, String str2) {
            this.id = n2;
            this.name = str2;
        }
    }
}
