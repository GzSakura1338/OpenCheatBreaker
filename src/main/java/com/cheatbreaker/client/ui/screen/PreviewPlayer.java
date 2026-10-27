package com.cheatbreaker.client.ui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.player.CapeUtils;

public class PreviewPlayer extends EntityPlayerSP {
   public PreviewNetHandler previewNetHandler;

   @Override
   public ResourceLocation getLocationSkin() {
      if (this.mc.thePlayer != null && this.mc.theWorld != null) {
         return this.mc.thePlayer.getLocationSkin();
      } else {
         return PlayerViewer.resourceLocation == null ? super.getLocationSkin() : PlayerViewer.resourceLocation;
      }
   }

   @Override
   public void entityInit() {
      super.entityInit();
      if (!this.dataWatcher.watchedObjects.containsKey(10)) {
         this.dataWatcher.addObject(10, "");
      }
   }

   @Override
   public boolean isSpectator() {
      return false;
   }

   public PreviewPlayer(Minecraft mc, World world, NetHandlerPlayClient netHandlerPlayClient, StatFileWriter statFileWriter) {
      super(mc, world, netHandlerPlayClient, statFileWriter);
      this.previewNetHandler = (PreviewNetHandler)netHandlerPlayClient;
      this.capabilities = new PlayerCapabilities();
   }

   @Override
   public boolean canAttackPlayer(EntityPlayer entityPlayer) {
      return false;
   }

   @Override
   public ItemStack getCurrentEquippedItem() {
      return null;
   }

   @Override
   public NetworkPlayerInfo getPlayerInfo() {
      if (this.playerInfo == null) {
         this.playerInfo = new NetworkPlayerInfo(this.getGameProfile());
      }

      return this.playerInfo;
   }

   @Override
   public boolean getAlwaysRenderNameTagForRender() {
      return false;
   }

   @Override
   public boolean isWearing(EnumPlayerModelParts enumPlayerModelParts) {
      return enumPlayerModelParts != EnumPlayerModelParts.CAPE
         ? true
         : PlayerViewer.guiButtonPress != null || this.getPlayerInfo().getLocationCape() != null || this.getLocationOfCape() != null;
   }

   @Override
   public ResourceLocation getLocationCape() {
      if (PlayerViewer.guiButtonPress != null) {
         return PlayerViewer.guiButtonPress;
      } else if (this.getPlayerInfo().getLocationCape() == null) {
         CapeUtils.downloadCape(this);
         return super.getLocationOfCape();
      } else {
         return super.getLocationCape() == null ? this.getPlayerInfo().getLocationCape() : super.getLocationCape();
      }
   }

   @Override
   public ItemStack getCurrentArmor(int n) {
      return null;
   }

   @Override
   public boolean hasSkin() {
      return true;
   }

   @Override
   public boolean check3() {
      return PlayerViewer.guiButtonPress != null;
   }
}
