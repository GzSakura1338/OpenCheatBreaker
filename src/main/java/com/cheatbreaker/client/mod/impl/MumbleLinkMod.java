package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.ButtonSetting;
import com.cheatbreaker.client.util.Logging;
import com.cheatbreaker.client.util.mumble.MumbleIdentity;
import com.cheatbreaker.client.util.mumble.MumbleLink;
import com.cheatbreaker.client.util.mumble.MumblePluginInstaller;
import com.cheatbreaker.client.util.mumble.MumbleTalkingListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.List;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;

public class MumbleLinkMod extends AbstractMod {
   public boolean unavailable;
   public static final String DESCRIPTION = "Minecraft 1.8.9";
   public static final int IDENTITY_INTERVAL_TICKS = 40;
   public static final String CONTEXT = "{\"domain\":\"AllTalk\"}";
   public MumbleLink mumbleLink;
   public MumbleTalkingListener talkingListener = new MumbleTalkingListener();
   public int pluginWaitTicks;
   public boolean loggedWaiting;
   public int identityCooldown;
   public boolean loggedPluginMissing;
   public static final String LINK_NAME = "Minecraft";
   public boolean linked;
   public static final int PLUGIN_WAIT_TICKS = 200;

   public static float[] getPosition(Entity entity, float f) {
      return new float[]{(float)entity.posX, (float)(entity.posY + f), (float)entity.posZ};
   }

   public void unlink() {
      if (this.mumbleLink != null) {
         this.mumbleLink.unlink();
      }

      if (this.linked) {
         Logging.infoTagged("Mumble Link", "Unlinked from Mumble.");
      }

      this.linked = false;
      this.loggedWaiting = false;
      this.pluginWaitTicks = 0;
   }

   public List<MumbleIdentity> getTalking() {
      return this.talkingListener.getTalking();
   }

   public void onClientTickEvent(ClientTickEvent clientTickEvent) {
      if (!this.unavailable) {
         if (this.mumbleLink == null) {
            this.mumbleLink = MumbleLink.create();
            if (this.mumbleLink == null) {
               this.unavailable = true;
               return;
            }
         }

         if (this.mc.thePlayer != null && this.mc.theWorld != null) {
            EntityPlayerSP thePlayer = this.mc.thePlayer;
            Entity entity = this.mc.getRenderViewEntity() == null ? thePlayer : this.mc.getRenderViewEntity();
            float[] position = getPosition(thePlayer, thePlayer.getEyeHeight());
            float[] position2 = getPosition(entity, entity.getEyeHeight());
            boolean update = this.mumbleLink
               .update(
                  position,
                  getFrontVector(thePlayer.rotationYaw, thePlayer.rotationPitch),
                  getTopVector(thePlayer.rotationYaw, thePlayer.rotationPitch),
                  position2,
                  getFrontVector(entity.rotationYaw, entity.rotationPitch),
                  getTopVector(entity.rotationYaw, entity.rotationPitch),
                  "Minecraft",
                  this.buildIdentity(thePlayer),
                  "{\"domain\":\"AllTalk\"}",
                  "Minecraft 1.8.9"
               );
            if (update && !this.linked) {
               this.linked = true;
               this.loggedWaiting = false;
               Logging.infoTagged("Mumble Link", "Linked to Mumble.");
            } else if (!update && !this.loggedWaiting) {
               this.loggedWaiting = true;
               Logging.infoTagged("Mumble Link", "Waiting for Mumble; start it and enable the Link plugin.");
            }

            if (this.identityCooldown-- <= 0) {
               this.identityCooldown = 40;
               this.talkingListener.sendSelfIdentity(thePlayer.getUniqueID().toString(), thePlayer.getName());
            }

            if (this.linked && !this.loggedPluginMissing && ++this.pluginWaitTicks >= 200 && !this.talkingListener.isPluginRunning()) {
               this.loggedPluginMissing = true;
               Logging.infoTagged(
                  "Mumble Link",
                  "The CheatBreaker Mumble plugin isn't running; use \"Install Mumble plugin\" in this module's settings to get talking indicators."
               );
            }
         } else {
            this.unlink();
         }
      }
   }

   @Override
   public void setEnabled(boolean flag) {
      super.setEnabled(flag);
      if (flag) {
         this.unavailable = false;
         this.talkingListener.start();
      } else {
         this.unlink();
         this.talkingListener.stop();
      }
   }

   public static float[] getTopVector(float f, float f2) {
      double toRadians = Math.toRadians(f);
      double toRadians2 = Math.toRadians(f2);
      return new float[]{(float)(Math.sin(toRadians) * Math.sin(toRadians2)), (float)(-Math.cos(toRadians2)), (float)(-Math.cos(toRadians) * Math.sin(toRadians2))};
   }

   public String buildIdentity(EntityPlayerSP entityPlayerSP) {
      BlockPos spawnPoint = this.mc.theWorld.getSpawnPoint();
      JsonArray jsonArray = new JsonArray();
      jsonArray.add(new JsonPrimitive(spawnPoint.getX()));
      jsonArray.add(new JsonPrimitive(spawnPoint.getY()));
      jsonArray.add(new JsonPrimitive(spawnPoint.getZ()));
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("name", entityPlayerSP.getName());
      jsonObject.add("worldSpawn", jsonArray);
      jsonObject.addProperty("dimension", entityPlayerSP.dimension);
      return jsonObject.toString();
   }

   public MumbleLinkMod() {
      super("Mumble Link");
      this.setDescription("Shares your position and view direction with Mumble positional audio.");
      this.setCategories(ModCategory.MISC);
      this.setIcon(new ResourceLocation("client/icons/mods/mumble.png"), 40, 40);
      this.setEnabledByDefault(false);
      this.addEventHandler(ClientTickEvent.class, this::onClientTickEvent);
      new ButtonSetting(this, "Install Plugin").changeValue("Install").setOnUpdate(obj -> MumblePluginInstaller.run());
   }

   public static float[] getFrontVector(float f, float f2) {
      double toRadians = Math.toRadians(f);
      double toRadians2 = Math.toRadians(f2);
      float cos = (float)Math.cos(toRadians2);
      return new float[]{(float)(-Math.sin(toRadians)) * cos, (float)(-Math.sin(toRadians2)), (float)Math.cos(toRadians) * cos};
   }
}
