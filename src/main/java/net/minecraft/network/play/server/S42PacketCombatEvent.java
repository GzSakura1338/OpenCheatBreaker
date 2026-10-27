package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.CombatTracker;

public class S42PacketCombatEvent implements Packet<INetHandlerPlayClient> {
   public S42PacketCombatEvent.Event eventType;
   public int timeSinceLastAttack;
   public int attackerEntityId;
   public int entityId;
   public String deathMessage;

   public S42PacketCombatEvent() {
   }

   public S42PacketCombatEvent(CombatTracker combatTrackerIn, S42PacketCombatEvent.Event combatEventType) {
      this.eventType = combatEventType;
      EntityLivingBase lastAttacker = combatTrackerIn.getLastAttacker();
      switch (combatEventType) {
         case END_COMBAT:
            this.timeSinceLastAttack = combatTrackerIn.getTimeSinceLastAttack();
            this.attackerEntityId = lastAttacker == null ? -1 : lastAttacker.getEntityId();
            break;
         case ENTITY_DIED:
            this.entityId = combatTrackerIn.getFighter().getEntityId();
            this.attackerEntityId = lastAttacker == null ? -1 : lastAttacker.getEntityId();
            this.deathMessage = combatTrackerIn.getDeathMessage().getUnformattedText();
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.eventType = buf.readEnumValue(S42PacketCombatEvent.Event.class);
      if (this.eventType == S42PacketCombatEvent.Event.END_COMBAT) {
         this.timeSinceLastAttack = buf.readVarIntFromBuffer();
         this.attackerEntityId = buf.readInt();
      } else if (this.eventType == S42PacketCombatEvent.Event.ENTITY_DIED) {
         this.entityId = buf.readVarIntFromBuffer();
         this.attackerEntityId = buf.readInt();
         this.deathMessage = buf.readStringFromBuffer(32767);
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeEnumValue(this.eventType);
      if (this.eventType == S42PacketCombatEvent.Event.END_COMBAT) {
         buf.writeVarIntToBuffer(this.timeSinceLastAttack);
         buf.writeInt(this.attackerEntityId);
      } else if (this.eventType == S42PacketCombatEvent.Event.ENTITY_DIED) {
         buf.writeVarIntToBuffer(this.entityId);
         buf.writeInt(this.attackerEntityId);
         buf.writeString(this.deathMessage);
      }
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleCombatEvent(this);
   }

   public enum Event {
      ENTER_COMBAT,
      END_COMBAT,
      ENTITY_DIED;
   }
}
