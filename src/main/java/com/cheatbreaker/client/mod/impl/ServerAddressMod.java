package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.SimpleEvent26;
import com.cheatbreaker.client.event.WorldClientEvent;
import com.cheatbreaker.client.mod.AbstractIconHudMod;
import com.cheatbreaker.client.mod.ModCategory;
import net.minecraft.util.ResourceLocation;

public class ServerAddressMod extends AbstractIconHudMod {
   public String text = "";

   public void run() {
      if (this.mc.isSingleplayer()) {
         this.setResourceLocation2(new ResourceLocation("textures/misc/unknown_server.png"));
      } else {
         this.setResourceLocation2(new ResourceLocation("servers/" + this.text + "/icon"));
      }
   }

   public void run2() {
      if (this.mc.isSingleplayer()) {
         this.text = "Singleplayer";
      } else {
         this.text = this.mc.getCurrentServerData().serverIP;
      }
   }

   @Override
   public String getLabelText() {
      return "IP";
   }

   @Override
   public String getStaticText() {
      return this.text;
   }

   @Override
   public String getDynamicText() {
      return this.text;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.mc.theWorld != null) {
         this.run3();
      }
   }

   public void run3() {
      this.run2();
      this.run();
   }

   public ServerAddressMod() {
      super("Server Address");
      this.setDescription("Displays the address of the current connected server.");
      this.setCategories(ModCategory.HUD, ModCategory.SERVER);
      this.handleString("[minehq.com]");
      this.addEventHandler(SimpleEvent26.class, simpleEvent26 -> this.run3());
      this.addEventHandler(WorldClientEvent.class, worldClientEvent -> this.run3());
   }
}
