package net.minecraft.client.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.cosmetic.Cosmetic;
import com.cheatbreaker.client.event.FloatEvent9;
import com.cheatbreaker.client.event.NetworkPlayerInfoStringEvent;
import com.cheatbreaker.client.event.SimpleEvent48;
import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;
import net.optifine.Config;
import net.optifine.player.CapeUtils;
import net.optifine.player.PlayerConfigurations;
import net.optifine.reflect.Reflector;

public abstract class AbstractClientPlayer extends EntityPlayer {
   public NetworkPlayerInfo playerInfo;
   public ResourceLocation locationOfCape = null;
   public long reloadCapeTimeMs = 0L;
   public boolean elytraOfCape = false;
   public String nameClear = null;
   public static ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");

   public AbstractClientPlayer(World worldIn, GameProfile playerProfile) {
      super(worldIn, playerProfile);
      this.nameClear = playerProfile.getName();
      if (this.nameClear != null && !this.nameClear.isEmpty()) {
         this.nameClear = StringUtils.stripControlCodes(this.nameClear);
      }

      Cosmetic findCosmetic3 = CheatBreaker.getInstance().getCosmeticsManager().findCosmetic3(this.getUniqueID());
      Cosmetic findCosmetic2 = CheatBreaker.getInstance().getCosmeticsManager().findCosmetic2(this.getUniqueID());
      Cosmetic findCosmetic = CheatBreaker.getInstance().getCosmeticsManager().findCosmetic(this.getUniqueID());

      try {
         if (CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet() != null) {
            CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().requestPlayerCosmetics(this);
         }
      } catch (NullPointerException e) {
         e.printStackTrace();
      }

      if (findCosmetic3 != null && CheatBreaker.getInstance().getKeybindSettings().showCheatbreakerCapes.getBooleanValue()) {
         this.setCosmetic(findCosmetic3);
      } else if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().showOptifineCapes.getValue()) {
         CapeUtils.downloadCape(this);
      }

      if (findCosmetic2 != null) {
         this.setCosmetic2(findCosmetic2);
      }

      if (findCosmetic != null) {
         this.setCosmetic3(findCosmetic);
      }

      PlayerConfigurations.getPlayerConfiguration(this);
   }

   @Override
   public boolean isSpectator() {
      NetworkPlayerInfo playerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getGameProfile().getId());
      return playerInfo != null && playerInfo.getGameType() == GameType.SPECTATOR;
   }

   public boolean hasPlayerInfo() {
      return this.getPlayerInfo() != null;
   }

   public NetworkPlayerInfo getPlayerInfo() {
      if (this.playerInfo == null) {
         this.playerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getUniqueID());
      }

      return this.playerInfo;
   }

   public boolean hasSkin() {
      NetworkPlayerInfo playerInfo = this.getPlayerInfo();
      return playerInfo != null && playerInfo.hasLocationSkin();
   }

   public ResourceLocation getLocationSkin() {
      NetworkPlayerInfo playerInfo = this.getPlayerInfo();
      NetworkPlayerInfoStringEvent networkPlayerInfoStringEvent = new NetworkPlayerInfoStringEvent(this.getName(), playerInfo);
      CheatBreaker.getInstance().getEventBus().handleEvent(networkPlayerInfoStringEvent);
      return playerInfo != null && !networkPlayerInfoStringEvent.isFlag() ? playerInfo.getLocationSkin() : DefaultPlayerSkin.getDefaultSkin(this.getUniqueID());
   }

   public ResourceLocation getLocationCape() {
      if (!Config.isShowCapes()) {
         return null;
      }

      if (this.reloadCapeTimeMs != 0L && System.currentTimeMillis() > this.reloadCapeTimeMs) {
         CapeUtils.reloadCape(this);
         this.reloadCapeTimeMs = 0L;
      }

      if (this.check3()) {
         return this.getCosmetic().getTexture();
      }

      if (this.locationOfCape != null) {
         return this.locationOfCape;
      }

      NetworkPlayerInfo playerInfo = this.getPlayerInfo();
      return playerInfo == null ? null : playerInfo.getLocationCape();
   }

   public static ThreadDownloadImageData getDownloadImageSkin(ResourceLocation resourceLocationIn, String username) {
      TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
      ITextureObject texture = textureManager.getTexture(resourceLocationIn);
      if (texture == null) {
         texture = new ThreadDownloadImageData(
            (File)null,
            String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(username)),
            DefaultPlayerSkin.getDefaultSkin(getOfflineUUID(username)),
            new ImageBufferDownload()
         );
         textureManager.loadTexture(resourceLocationIn, texture);
      }

      return (ThreadDownloadImageData)texture;
   }

   public static ResourceLocation getLocationSkin(String username) {
      return new ResourceLocation("skins/" + StringUtils.stripControlCodes(username));
   }

   public String getSkinType() {
      NetworkPlayerInfo playerInfo = this.getPlayerInfo();
      return playerInfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : playerInfo.getSkinType();
   }

   public float getFovModifier() {
      float f = 1.0F;
      if (this.capabilities.isFlying) {
         f *= 1.1F;
      }

      SimpleEvent48 simpleEvent48 = new SimpleEvent48();
      CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent48);
      if (!simpleEvent48.isFlag()) {
         IAttributeInstance entityAttribute = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
         f = (float)(f * ((entityAttribute.getAttributeValue() / this.capabilities.getWalkSpeed() + 1.0) / 2.0));
      }

      if (this.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
         f = 1.0F;
      }

      if (this.isUsingItem() && this.getItemInUse().getItem() == Items.bow) {
         int itemInUseDuration = this.getItemInUseDuration();
         float f2 = itemInUseDuration / 20.0F;
         if (f2 > 1.0F) {
            f2 = 1.0F;
         } else {
            f2 *= f2;
         }

         FloatEvent9 floatEvent9 = new FloatEvent9(0.15F);
         CheatBreaker.getInstance().getEventBus().handleEvent(floatEvent9);
         f *= 1.0F - f2 * floatEvent9.getFloatValue();
      }

      return Reflector.ForgeHooksClient_getOffsetFOV.exists() ? Reflector.callFloat(Reflector.ForgeHooksClient_getOffsetFOV, this, f) : f;
   }

   public String getNameClear() {
      return this.nameClear;
   }

   public ResourceLocation getLocationOfCape() {
      return this.locationOfCape;
   }

   public void setLocationOfCape(ResourceLocation resourceLocation) {
      this.locationOfCape = resourceLocation;
   }

   public boolean hasElytraCape() {
      ResourceLocation locationCape = this.getLocationCape();
      return locationCape == null ? false : (locationCape == this.locationOfCape ? this.elytraOfCape : true);
   }

   public void setElytraOfCape(boolean flag) {
      this.elytraOfCape = flag;
   }

   public boolean isElytraOfCape() {
      return this.elytraOfCape;
   }

   public long getReloadCapeTimeMs() {
      return this.reloadCapeTimeMs;
   }

   public void setReloadCapeTimeMs(long l) {
      this.reloadCapeTimeMs = l;
   }

   @Override
   public Vec3 getLook(float f) {
      return this.getVectorForRotation(this.rotationPitch, this.rotationYaw);
   }
}
