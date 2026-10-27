package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.IntEvent1;
import com.cheatbreaker.client.event.PacketEvent;
import com.cheatbreaker.client.mod.AbstractCombatValueHudMod;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.render.RenderUtils;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class CPSMod extends AbstractCombatValueHudMod {
   public Setting lineColor;
   public Deque<Long> longValueQueue = new ArrayDeque<>();
   public Setting counter;
   public Deque<Long> longValueQueue2 = new ArrayDeque<>();
   public Deque<Long> longValueQueue3 = new ArrayDeque<>();
   public Deque<Long> longValueQueue4 = new ArrayDeque<>();
   public String text;
   public Deque<Long> longValueQueue5 = new ArrayDeque<>();

   public CPSMod() {
      super("CPS");
      this.setDescription("Displays your input clicks per second.");
      this.setAuthors("Fyu");
      this.handleString("[9 CPS]");
      this.addEventHandler(IntEvent1.class, this::onIntEvent1);
      this.addEventHandler(ClientTickEvent.class, this::onClientTickEvent4);
      this.addEventHandler(PacketEvent.class, this::onPacketEvent);
   }

   @Override
   public void renderText(String str) {
      super.renderText(str);
      if (this.counter.getStringValue().endsWith(" Clicks")) {
         this.handleString((this.showBackground.getBooleanValue() ? this.formatBackground : this.formatNoBackground).getStringValue(), str);
      }
   }

   @Override
   public boolean check22() {
      if (this.counter.getStringValue().endsWith("Primary Clicks")) {
         return this.longValueQueue.size() == 0 && this.longValueQueue2.size() == 0;
      } else {
         return !this.counter.getStringValue().endsWith("Thumb Clicks")
            ? this.getNumericValue().intValue() == 0
            : this.longValueQueue4.size() == 0 && this.longValueQueue5.size() == 0;
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.run();
   }

   @Override
   public Number getNumericValue() {
      switch (this.counter.getStringValue()) {
         case "Left":
            return this.longValueQueue.size();
         case "Right":
            return this.longValueQueue2.size();
         case "Middle":
            return this.longValueQueue3.size();
         case "Backward":
            return this.longValueQueue4.size();
         case "Forward":
            return this.longValueQueue5.size();
         case "Higher":
            int[] ints = new int[]{
               this.longValueQueue.size(), this.longValueQueue2.size(), this.longValueQueue3.size(), this.longValueQueue4.size(), this.longValueQueue5.size()
            };
            return Collections.max(Arrays.asList(ArrayUtils.toObject(ints)));
         default:
            return null;
      }
   }

   @Override
   public String getStaticText() {
      return this.counter.getStringValue().endsWith(" Clicks") ? "9  2" : "9";
   }

   public void handleString(String str, String str2) {
      float f = this.mc.fontRendererObj.getStringWidth(StringUtils.substringBefore(str, "%VALUE%"))
            + this.mc.fontRendererObj.getStringWidth(String.valueOf(this.longValueQueue.size()))
         + (this.mc.fontRendererObj.getStringWidth("  ") / 2.0F - 0.4F);
      float f2 = !this.showBackground.getBooleanValue() && !this.alwaysCenter.getBooleanValue() ? -1.0F : this.backgroundHeight.getFloatValue() / 2.0F - 4.5F;
      if (this.showBackground.getBooleanValue() || this.alwaysCenter.getBooleanValue()) {
         float f3 = this.hudWidth / 2.0F
            - this.mc.fontRendererObj.getStringWidth(str.replaceAll("%LABEL%", this.getLabelText()).replaceAll("%VALUE%", str2)) / 2;
         f += f3;
      }

      RenderUtils.drawRectWithShadow(
         f, f2, f + 1.0F, f2 + this.mc.fontRendererObj.FONT_HEIGHT, this.lineColor.getColorValue(), this.shouldDrawShadow()
      );
   }

   @Override
   public String getDynamicText() {
      if (this.longValue == 0L && this.hideWhenNotAttacking.getBooleanValue()) {
         return null;
      } else if (!this.counter.getStringValue().endsWith(" Clicks")) {
         return super.getDynamicText();
      } else {
         return this.counter.getStringValue().equals("Thumb Clicks")
            ? this.longValueQueue4.size() + "  " + this.longValueQueue5.size()
            : this.longValueQueue.size() + "  " + this.longValueQueue2.size();
      }
   }

   public void onClientTickEvent4(ClientTickEvent clientTickEvent) {
      super.onClientTickEvent(clientTickEvent);
      this.longValueQueue.removeIf(longValue -> longValue < System.currentTimeMillis() - 1000L);
      this.longValueQueue2.removeIf(longValue -> longValue < System.currentTimeMillis() - 1000L);
      this.longValueQueue3.removeIf(longValue -> longValue < System.currentTimeMillis() - 1000L);
      this.longValueQueue4.removeIf(longValue -> longValue < System.currentTimeMillis() - 1000L);
      this.longValueQueue5.removeIf(longValue -> longValue < System.currentTimeMillis() - 1000L);
      if (this.counter.getStringValue().equals("Higher")) {
         this.run();
      }
   }

   public void run() {
      StringBuilder sb = new StringBuilder();
      switch (this.counter.getStringValue()) {
         case "Right":
            sb.append("R");
            break;
         case "Middle":
            sb.append("M");
            break;
         case "Backward":
            sb.append("TB");
            break;
         case "Forward":
            sb.append("TF");
            break;
         case "Thumb Clicks":
            sb.append("T");
            break;
         case "Higher":
            if (this.getNumericValue().intValue() != 0) {
               if (this.getNumericValue().intValue() == this.longValueQueue2.size()) {
                  sb.append("R");
               } else if (this.getNumericValue().intValue() == this.longValueQueue3.size()) {
                  sb.append("M");
               } else if (this.getNumericValue().intValue() == this.longValueQueue4.size()) {
                  sb.append("TB");
               } else if (this.getNumericValue().intValue() == this.longValueQueue5.size()) {
                  sb.append("TF");
               }
            }
      }

      sb.append("CPS");
      this.text = sb.toString();
   }

   @Override
   public String getLabelText() {
      return this.text;
   }

   @Override
   public void createGeneralOptions() {
      super.createGeneralOptions();
      this.hideWhenValueIs.setCondition(() -> !this.counter.getStringValue().endsWith(" Clicks"));
      this.hiddenValueS.setCondition(() -> !this.hideWhenValueIs.getValue().equals("OFF") && !this.counter.getStringValue().endsWith(" Clicks"));
      this.counter = new Setting(this, "Counter")
         .setDescription(
            "Determines which and when clicks should be shown.\n\n§bLeft Clicks:§r Only show left clicks.\n§bRight Clicks:§r Only show right clicks.\n§bBoth:§r Show both left and right clicks.\n§bHigher:§r Shows either left or right clicks depending which has a higher amount."
         )
         .withHierarchyParents(this.generalOptions)
         .changeValue("Higher")
         .setStringValues("Left", "Right", "Middle", "Backward", "Forward", "Primary Clicks", "Thumb Clicks", "Higher")
         .setOnUpdate(obj -> this.run());
   }

   public void onPacketEvent(PacketEvent packetEvent) {
      if (packetEvent.getPacket() instanceof C02PacketUseEntity) {
         Entity entityFromWorld = ((C02PacketUseEntity)packetEvent.getPacket()).getEntityFromWorld(this.mc.theWorld);
         if (entityFromWorld != null && ((C02PacketUseEntity)packetEvent.getPacket()).getAction() == Action.ATTACK) {
            this.longValue = System.currentTimeMillis();
         }
      }
   }

   public void onIntEvent1(IntEvent1 intEvent1) {
      if (intEvent1.getEventButton() == 0) {
         this.longValueQueue.add(System.currentTimeMillis());
      }

      if (intEvent1.getEventButton() == 1) {
         this.longValueQueue2.add(System.currentTimeMillis());
      }

      if (intEvent1.getEventButton() == 2) {
         this.longValueQueue3.add(System.currentTimeMillis());
      }

      if (intEvent1.getEventButton() == 3) {
         this.longValueQueue4.add(System.currentTimeMillis());
      }

      if (intEvent1.getEventButton() == 4) {
         this.longValueQueue5.add(System.currentTimeMillis());
      }

      if (this.counter.getStringValue().equals("Higher")) {
         this.run();
      }
   }

   @Override
   public void createColorOptions() {
      this.lineColor = new Setting(this, "Line Color")
         .withHierarchyParents(this.colorOptions)
         .changeValue(-14671840)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .setCondition(() -> this.counter.getStringValue().endsWith(" Clicks"));
   }
}
