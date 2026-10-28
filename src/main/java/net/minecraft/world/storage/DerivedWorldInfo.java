package net.minecraft.world.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldType;
import net.minecraft.world.WorldSettings.GameType;

public class DerivedWorldInfo extends WorldInfo {
    public WorldInfo theWorldInfo;

    public DerivedWorldInfo(WorldInfo worldInfo) {
        this.theWorldInfo = worldInfo;
    }

    @Override
    public NBTTagCompound getNBTTagCompound() {
        return this.theWorldInfo.getNBTTagCompound();
    }

    @Override
    public NBTTagCompound cloneNBTCompound(NBTTagCompound nbttagCompound) {
        return this.theWorldInfo.cloneNBTCompound(nbttagCompound);
    }

    @Override
    public long getSeed() {
        return this.theWorldInfo.getSeed();
    }

    @Override
    public int getSpawnX() {
        return this.theWorldInfo.getSpawnX();
    }

    @Override
    public int getSpawnY() {
        return this.theWorldInfo.getSpawnY();
    }

    @Override
    public int getSpawnZ() {
        return this.theWorldInfo.getSpawnZ();
    }

    @Override
    public long getWorldTotalTime() {
        return this.theWorldInfo.getWorldTotalTime();
    }

    @Override
    public long getWorldTime() {
        return this.theWorldInfo.getWorldTime();
    }

    @Override
    public long getSizeOnDisk() {
        return this.theWorldInfo.getSizeOnDisk();
    }

    @Override
    public NBTTagCompound getPlayerNBTTagCompound() {
        return this.theWorldInfo.getPlayerNBTTagCompound();
    }

    @Override
    public int getDimension() {
        return this.theWorldInfo.getDimension();
    }

    @Override
    public String getWorldName() {
        return this.theWorldInfo.getWorldName();
    }

    @Override
    public int getSaveVersion() {
        return this.theWorldInfo.getSaveVersion();
    }

    @Override
    public long getLastTimePlayed() {
        return this.theWorldInfo.getLastTimePlayed();
    }

    @Override
    public boolean isThundering() {
        return this.theWorldInfo.isThundering();
    }

    @Override
    public int getThunderTime() {
        return this.theWorldInfo.getThunderTime();
    }

    @Override
    public boolean isRaining() {
        return this.theWorldInfo.isRaining();
    }

    @Override
    public int getRainTime() {
        return this.theWorldInfo.getRainTime();
    }

    @Override
    public GameType getGameType() {
        return this.theWorldInfo.getGameType();
    }

    @Override
    public void setSpawnX(int n) {
    }

    @Override
    public void setSpawnY(int n) {
    }

    @Override
    public void setSpawnZ(int n) {
    }

    @Override
    public void incrementTotalWorldTime(long l) {
    }

    @Override
    public void setWorldTime(long l) {
    }

    @Override
    public void setSpawnPosition(int n, int n2, int n3) {
    }

    @Override
    public void setWorldName(String str) {
    }

    @Override
    public void setSaveVersion(int n) {
    }

    @Override
    public void setThundering(boolean flag) {
    }

    @Override
    public void setThunderTime(int n) {
    }

    @Override
    public void setRaining(boolean flag) {
    }

    @Override
    public void setRainTime(int n) {
    }

    @Override
    public boolean isMapFeaturesEnabled() {
        return this.theWorldInfo.isMapFeaturesEnabled();
    }

    @Override
    public boolean isHardcoreModeEnabled() {
        return this.theWorldInfo.isHardcoreModeEnabled();
    }

    @Override
    public WorldType getTerrainType() {
        return this.theWorldInfo.getTerrainType();
    }

    @Override
    public void setTerrainType(WorldType worldType) {
    }

    @Override
    public boolean areCommandsAllowed() {
        return this.theWorldInfo.areCommandsAllowed();
    }

    @Override
    public boolean isInitialized() {
        return this.theWorldInfo.isInitialized();
    }

    @Override
    public void setServerInitialized(boolean flag) {
    }

    @Override
    public GameRules getGameRulesInstance() {
        return this.theWorldInfo.getGameRulesInstance();
    }
}
