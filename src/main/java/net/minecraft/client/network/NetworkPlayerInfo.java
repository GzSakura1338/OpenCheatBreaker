package net.minecraft.client.network;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import net.minecraft.network.play.server.S38PacketPlayerListItem.AddPlayerData;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldSettings.GameType;

public class NetworkPlayerInfo {
   public boolean playerTexturesLoaded = false;
   public int intValue = 0;
   public int intValue2 = 0;
   public long longValue = 0L;
   public ResourceLocation locationSkin;
   public ResourceLocation locationCape;
   public String skinType;
   public long longValue2 = 0L;
   public long longValue3 = 0L;
   public GameProfile gameProfile;
   public GameType gameType;
   public int responseTime;
   public IChatComponent displayName;

   public NetworkPlayerInfo(GameProfile gameProfile) {
      this.gameProfile = gameProfile;
   }

   public NetworkPlayerInfo(AddPlayerData addPlayerData) {
      this.gameProfile = addPlayerData.getProfile();
      this.gameType = addPlayerData.getGameMode();
      this.responseTime = addPlayerData.getPing();
      this.displayName = addPlayerData.getDisplayName();
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public int getResponseTime() {
      return this.responseTime;
   }

   public void setGameType(GameType gameType) {
      this.gameType = gameType;
   }

   public void setResponseTime(int n) {
      this.responseTime = n;
   }

   public boolean hasLocationSkin() {
      return this.locationSkin != null;
   }

   public String getSkinType() {
      return this.skinType == null ? DefaultPlayerSkin.getSkinType(this.gameProfile.getId()) : this.skinType;
   }

   public ResourceLocation getLocationSkin() {
      if (this.locationSkin == null) {
         this.loadPlayerTextures();
      }

      return (ResourceLocation)MoreObjects.firstNonNull(this.locationSkin, DefaultPlayerSkin.getDefaultSkin(this.gameProfile.getId()));
   }

   public ResourceLocation getLocationCape() {
      if (this.locationCape == null) {
         this.loadPlayerTextures();
      }

      return this.locationCape;
   }

   public ScorePlayerTeam getPlayerTeam() {
      return Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam(this.getGameProfile().getName());
   }

   public void loadPlayerTextures() {
      synchronized (this) {
         if (!this.playerTexturesLoaded) {
            this.playerTexturesLoaded = true;
            Minecraft.getMinecraft().getSkinManager().loadProfileTextures(this.gameProfile, new SkinAvailableCallback() {
               @Override
               public void skinAvailable(Type type, ResourceLocation location, MinecraftProfileTexture profileTexture) {
                  switch (type) {
                     case SKIN:
                        NetworkPlayerInfo.this.locationSkin = location;
                        NetworkPlayerInfo.this.skinType = profileTexture.getMetadata("model");
                        if (NetworkPlayerInfo.this.skinType == null) {
                           NetworkPlayerInfo.this.skinType = "default";
                        }
                        break;
                     case CAPE:
                        NetworkPlayerInfo.this.locationCape = location;
                  }
               }
            }, true);
         }
      }
   }

   public void setDisplayName(IChatComponent displayNameIn) {
      this.displayName = displayNameIn;
   }

   public IChatComponent getDisplayName() {
      return this.displayName;
   }

   public int getLastHealth() {
      return this.intValue;
   }

   public void setLastHealth(int n) {
      this.intValue = n;
   }

   public int getHealth() {
      return this.intValue2;
   }

   public void setHealth(int n) {
      this.intValue2 = n;
   }

   public long getLastHealthTime() {
      return this.longValue;
   }

   public void setLastHealthTime(long l) {
      this.longValue = l;
   }

   public long getBlinkingHeartTime() {
      return this.longValue2;
   }

   public void setBlinkingHeartTime(long l) {
      this.longValue2 = l;
   }

   public long getShowTime() {
      return this.longValue3;
   }

   public void setShowTime(long l) {
      this.longValue3 = l;
   }
}
