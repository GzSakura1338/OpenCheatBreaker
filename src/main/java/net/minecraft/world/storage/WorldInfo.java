package net.minecraft.world.storage;

import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.WorldSettings.GameType;

public class WorldInfo {
    public WorldType terrainType = WorldType.DEFAULT;
    public String generatorOptions = "";
    public GameRules theGameRules = new GameRules();
    public long randomSeed;
    public GameType theGameType;
    public boolean mapFeaturesEnabled;
    public int spawnX;
    public int spawnY;
    public int spawnZ;
    public long totalTime;
    public long worldTime;
    public long lastTimePlayed;
    public long sizeOnDisk;
    public String levelName;
    public int saveVersion;
    public int rainTime;
    public boolean raining;
    public int thunderTime;
    public boolean thundering;
    public boolean hardcore;
    public boolean initialized;
    public boolean allowCommands;
    public NBTTagCompound playerTag;
    public int dimension;

    public WorldInfo() {
    }

    public WorldInfo(NBTTagCompound nbttagCompound) {
        this.randomSeed = nbttagCompound.getLong("RandomSeed");
        if (nbttagCompound.hasKey("generatorName", 8)) {
            String string = nbttagCompound.getString("generatorName");
            this.terrainType = WorldType.parseWorldType(string);
            if (this.terrainType == null) {
                this.terrainType = WorldType.DEFAULT;
            } else if (this.terrainType.isVersioned()) {
                int n = 0;
                if (nbttagCompound.hasKey("generatorVersion", 99)) {
                    n = nbttagCompound.getInteger("generatorVersion");
                }

                this.terrainType = this.terrainType.getWorldTypeForGeneratorVersion(n);
            }

            if (nbttagCompound.hasKey("generatorOptions", 8)) {
                this.generatorOptions = nbttagCompound.getString("generatorOptions");
            }
        }

        this.theGameType = GameType.getByID(nbttagCompound.getInteger("GameType"));
        if (nbttagCompound.hasKey("MapFeatures", 99)) {
            this.mapFeaturesEnabled = nbttagCompound.getBoolean("MapFeatures");
        } else {
            this.mapFeaturesEnabled = true;
        }

        this.spawnX = nbttagCompound.getInteger("SpawnX");
        this.spawnY = nbttagCompound.getInteger("SpawnY");
        this.spawnZ = nbttagCompound.getInteger("SpawnZ");
        this.totalTime = nbttagCompound.getLong("Time");
        if (nbttagCompound.hasKey("DayTime", 99)) {
            this.worldTime = nbttagCompound.getLong("DayTime");
        } else {
            this.worldTime = this.totalTime;
        }

        this.lastTimePlayed = nbttagCompound.getLong("LastPlayed");
        this.sizeOnDisk = nbttagCompound.getLong("SizeOnDisk");
        this.levelName = nbttagCompound.getString("LevelName");
        this.saveVersion = nbttagCompound.getInteger("version");
        this.rainTime = nbttagCompound.getInteger("rainTime");
        this.raining = nbttagCompound.getBoolean("raining");
        this.thunderTime = nbttagCompound.getInteger("thunderTime");
        this.thundering = nbttagCompound.getBoolean("thundering");
        this.hardcore = nbttagCompound.getBoolean("hardcore");
        if (nbttagCompound.hasKey("initialized", 99)) {
            this.initialized = nbttagCompound.getBoolean("initialized");
        } else {
            this.initialized = true;
        }

        if (nbttagCompound.hasKey("allowCommands", 99)) {
            this.allowCommands = nbttagCompound.getBoolean("allowCommands");
        } else {
            this.allowCommands = this.theGameType == GameType.CREATIVE;
        }

        if (nbttagCompound.hasKey("Player", 10)) {
            this.playerTag = nbttagCompound.getCompoundTag("Player");
            this.dimension = this.playerTag.getInteger("Dimension");
        }

        if (nbttagCompound.hasKey("GameRules", 10)) {
            this.theGameRules.readGameRulesFromNBT(nbttagCompound.getCompoundTag("GameRules"));
        }
    }

    public WorldInfo(WorldSettings worldSettings, String str) {
        this.randomSeed = worldSettings.getSeed();
        this.theGameType = worldSettings.getGameType();
        this.mapFeaturesEnabled = worldSettings.isMapFeaturesEnabled();
        this.levelName = str;
        this.hardcore = worldSettings.getHardcoreEnabled();
        this.terrainType = worldSettings.getTerrainType();
        this.generatorOptions = worldSettings.getGeneratorOptions();
        this.allowCommands = worldSettings.areCommandsAllowed();
        this.initialized = false;
    }

    public WorldInfo(WorldInfo worldInfo) {
        this.randomSeed = worldInfo.randomSeed;
        this.terrainType = worldInfo.terrainType;
        this.generatorOptions = worldInfo.generatorOptions;
        this.theGameType = worldInfo.theGameType;
        this.mapFeaturesEnabled = worldInfo.mapFeaturesEnabled;
        this.spawnX = worldInfo.spawnX;
        this.spawnY = worldInfo.spawnY;
        this.spawnZ = worldInfo.spawnZ;
        this.totalTime = worldInfo.totalTime;
        this.worldTime = worldInfo.worldTime;
        this.lastTimePlayed = worldInfo.lastTimePlayed;
        this.sizeOnDisk = worldInfo.sizeOnDisk;
        this.playerTag = worldInfo.playerTag;
        this.dimension = worldInfo.dimension;
        this.levelName = worldInfo.levelName;
        this.saveVersion = worldInfo.saveVersion;
        this.rainTime = worldInfo.rainTime;
        this.raining = worldInfo.raining;
        this.thunderTime = worldInfo.thunderTime;
        this.thundering = worldInfo.thundering;
        this.hardcore = worldInfo.hardcore;
        this.allowCommands = worldInfo.allowCommands;
        this.initialized = worldInfo.initialized;
        this.theGameRules = worldInfo.theGameRules;
    }

    public NBTTagCompound getNBTTagCompound() {
        NBTTagCompound nbttagCompound = new NBTTagCompound();
        this.updateTagCompound(nbttagCompound, this.playerTag);
        return nbttagCompound;
    }

    public NBTTagCompound cloneNBTCompound(NBTTagCompound nbttagCompound) {
        NBTTagCompound nbttagCompound2 = new NBTTagCompound();
        this.updateTagCompound(nbttagCompound2, nbttagCompound);
        return nbttagCompound2;
    }

    public void updateTagCompound(NBTTagCompound nbttagCompound, NBTTagCompound nbttagCompound2) {
        nbttagCompound.setLong("RandomSeed", this.randomSeed);
        nbttagCompound.setString("generatorName", this.terrainType.getWorldTypeName());
        nbttagCompound.setInteger("generatorVersion", this.terrainType.getGeneratorVersion());
        nbttagCompound.setString("generatorOptions", this.generatorOptions);
        nbttagCompound.setInteger("GameType", this.theGameType.getID());
        nbttagCompound.setBoolean("MapFeatures", this.mapFeaturesEnabled);
        nbttagCompound.setInteger("SpawnX", this.spawnX);
        nbttagCompound.setInteger("SpawnY", this.spawnY);
        nbttagCompound.setInteger("SpawnZ", this.spawnZ);
        nbttagCompound.setLong("Time", this.totalTime);
        nbttagCompound.setLong("DayTime", this.worldTime);
        nbttagCompound.setLong("SizeOnDisk", this.sizeOnDisk);
        nbttagCompound.setLong("LastPlayed", MinecraftServer.getCurrentTimeMillis());
        nbttagCompound.setString("LevelName", this.levelName);
        nbttagCompound.setInteger("version", this.saveVersion);
        nbttagCompound.setInteger("rainTime", this.rainTime);
        nbttagCompound.setBoolean("raining", this.raining);
        nbttagCompound.setInteger("thunderTime", this.thunderTime);
        nbttagCompound.setBoolean("thundering", this.thundering);
        nbttagCompound.setBoolean("hardcore", this.hardcore);
        nbttagCompound.setBoolean("allowCommands", this.allowCommands);
        nbttagCompound.setBoolean("initialized", this.initialized);
        nbttagCompound.setTag("GameRules", this.theGameRules.writeGameRulesToNBT());
        if (nbttagCompound2 != null) {
            nbttagCompound.setTag("Player", nbttagCompound2);
        }
    }

    public long getSeed() {
        return this.randomSeed;
    }

    public int getSpawnX() {
        return this.spawnX;
    }

    public int getSpawnY() {
        return this.spawnY;
    }

    public int getSpawnZ() {
        return this.spawnZ;
    }

    public long getWorldTotalTime() {
        return this.totalTime;
    }

    public long getWorldTime() {
        return this.worldTime;
    }

    public long getSizeOnDisk() {
        return this.sizeOnDisk;
    }

    public NBTTagCompound getPlayerNBTTagCompound() {
        return this.playerTag;
    }

    public int getDimension() {
        return this.dimension;
    }

    public void setSpawnX(int n) {
        this.spawnX = n;
    }

    public void setSpawnY(int n) {
        this.spawnY = n;
    }

    public void setSpawnZ(int n) {
        this.spawnZ = n;
    }

    public void incrementTotalWorldTime(long l) {
        this.totalTime = l;
    }

    public void setWorldTime(long l) {
        this.worldTime = l;
    }

    public void setSpawnPosition(int n, int n2, int n3) {
        this.spawnX = n;
        this.spawnY = n2;
        this.spawnZ = n3;
    }

    public String getWorldName() {
        return this.levelName;
    }

    public void setWorldName(String str) {
        this.levelName = str;
    }

    public int getSaveVersion() {
        return this.saveVersion;
    }

    public void setSaveVersion(int n) {
        this.saveVersion = n;
    }

    public long getLastTimePlayed() {
        return this.lastTimePlayed;
    }

    public boolean isThundering() {
        return this.thundering;
    }

    public void setThundering(boolean flag) {
        this.thundering = flag;
    }

    public int getThunderTime() {
        return this.thunderTime;
    }

    public void setThunderTime(int n) {
        this.thunderTime = n;
    }

    public boolean isRaining() {
        return this.raining;
    }

    public void setRaining(boolean flag) {
        this.raining = flag;
    }

    public int getRainTime() {
        return this.rainTime;
    }

    public void setRainTime(int n) {
        this.rainTime = n;
    }

    public GameType getGameType() {
        return this.theGameType;
    }

    public boolean isMapFeaturesEnabled() {
        return this.mapFeaturesEnabled;
    }

    public void setGameType(GameType gameType) {
        this.theGameType = gameType;
    }

    public boolean isHardcoreModeEnabled() {
        return this.hardcore;
    }

    public WorldType getTerrainType() {
        return this.terrainType;
    }

    public void setTerrainType(WorldType worldType) {
        this.terrainType = worldType;
    }

    public String getGeneratorOptions() {
        return this.generatorOptions;
    }

    public boolean areCommandsAllowed() {
        return this.allowCommands;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public void setServerInitialized(boolean flag) {
        this.initialized = flag;
    }

    public GameRules getGameRulesInstance() {
        return this.theGameRules;
    }

    public void addToCrashReport(CrashReportCategory crashReportCategory) {
        crashReportCategory.addCrashSectionCallable("Level seed", new Callable() {
            public String call() {
                return String.valueOf(WorldInfo.this.getSeed());
            }
        });
        crashReportCategory.addCrashSectionCallable(
            "Level generator",
            new Callable() {
                public String call() {
                    return String.format(
                        "ID %02d - %s, ver %d. Features enabled: %b",
                        WorldInfo.this.terrainType.getWorldTypeID(),
                        WorldInfo.this.terrainType.getWorldTypeName(),
                        WorldInfo.this.terrainType.getGeneratorVersion(),
                        WorldInfo.this.mapFeaturesEnabled
                    );
                }
            }
        );
        crashReportCategory.addCrashSectionCallable("Level generator options", new Callable() {
            public String call() {
                return WorldInfo.this.generatorOptions;
            }
        });
        crashReportCategory.addCrashSectionCallable("Level spawn location", new Callable() {
            public String call() {
                return CrashReportCategory.getLocationInfo(WorldInfo.this.spawnX, WorldInfo.this.spawnY, WorldInfo.this.spawnZ);
            }
        });
        crashReportCategory.addCrashSectionCallable("Level time", new Callable() {
            public String call() {
                return String.format("%d game time, %d day time", WorldInfo.this.totalTime, WorldInfo.this.worldTime);
            }
        });
        crashReportCategory.addCrashSectionCallable("Level dimension", new Callable() {
            public String call() {
                return String.valueOf(WorldInfo.this.dimension);
            }
        });
        crashReportCategory.addCrashSectionCallable("Level storage version", new Callable() {
            public String call() {
                String str = "Unknown?";

                try {
                    switch (WorldInfo.this.saveVersion) {
                        case 19132:
                            str = "McRegion";
                            break;
                        case 19133:
                            str = "Anvil";
                    }
                } catch (Throwable e) {
                }

                return String.format("0x%05X - %s", WorldInfo.this.saveVersion, str);
            }
        });
        crashReportCategory.addCrashSectionCallable(
            "Level weather",
            new Callable() {
                public String call() {
                    return String.format(
                        "Rain time: %d (now: %b), thunder time: %d (now: %b)",
                        WorldInfo.this.rainTime,
                        WorldInfo.this.raining,
                        WorldInfo.this.thunderTime,
                        WorldInfo.this.thundering
                    );
                }
            }
        );
        crashReportCategory.addCrashSectionCallable(
            "Level game mode",
            new Callable() {
                public String call() {
                    return String.format(
                        "Game mode: %s (ID %d). Hardcore: %b. Cheats: %b",
                        WorldInfo.this.theGameType.getName(),
                        WorldInfo.this.theGameType.getID(),
                        WorldInfo.this.hardcore,
                        WorldInfo.this.allowCommands
                    );
                }
            }
        );
    }
}
