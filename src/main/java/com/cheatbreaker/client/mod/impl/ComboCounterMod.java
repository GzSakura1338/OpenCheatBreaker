package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.EntityS19PacketEntityStatusEvent;
import com.cheatbreaker.client.event.PacketEvent;
import com.cheatbreaker.client.mod.AbstractCombatValueHudMod;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;

public class ComboCounterMod extends AbstractCombatValueHudMod {
   public List<Long> longValueList = new ArrayList<>();
   public int intValue = -1;
   public int intValue2;
   public long longValue2;

   public void onPacketEvent(PacketEvent packetEvent) {
      if (packetEvent.getPacket() instanceof C02PacketUseEntity) {
         Entity entityFromWorld = ((C02PacketUseEntity)packetEvent.getPacket()).getEntityFromWorld(this.mc.theWorld);
         if (entityFromWorld != null && ((C02PacketUseEntity)packetEvent.getPacket()).getAction() == Action.ATTACK) {
            this.intValue = entityFromWorld.getEntityId();
            this.longValue = System.currentTimeMillis();
         }
      }
   }

   @Override
   public Number getNumericValue() {
      return this.longValueList.size();
   }

   public ComboCounterMod() {
      super("Combo Counter");
      this.setDescription("Displays how many times you hit someone in a row within 2 seconds.");
      this.setAuthors("Erouax");
      this.handleString("[6 Combo]");
      this.addEventHandler(EntityS19PacketEntityStatusEvent.class, this::onEntityS19PacketEntityStatusEvent);
      this.addEventHandler(PacketEvent.class, this::onPacketEvent);
   }

   public void onEntityS19PacketEntityStatusEvent(EntityS19PacketEntityStatusEvent entityS19PacketEntityStatusEvent) {
      if (entityS19PacketEntityStatusEvent.getS19PacketEntityStatus().getOpCode() == 2) {
         if (this.intValue == entityS19PacketEntityStatusEvent.getEntity().getEntityId()) {
            this.intValue = -1;
            if (this.intValue2 != entityS19PacketEntityStatusEvent.getEntity().getEntityId()) {
               this.longValueList.clear();
            }

            this.longValueList.add(System.currentTimeMillis());
            this.longValue2 = System.currentTimeMillis();
            this.intValue2 = entityS19PacketEntityStatusEvent.getEntity().getEntityId();
         } else if (entityS19PacketEntityStatusEvent.getEntity().getEntityId() == this.mc.thePlayer.getEntityId()) {
            this.longValueList.clear();
         }
      }
   }

   @Override
   public void reset() {
      this.longValueList.clear();
   }

   @Override
   public String constantText() {
      return "No %LABEL%";
   }

   @Override
   public String getLabelText() {
      return "Combo";
   }

   @Override
   public String getStaticText() {
      return "6";
   }
}
