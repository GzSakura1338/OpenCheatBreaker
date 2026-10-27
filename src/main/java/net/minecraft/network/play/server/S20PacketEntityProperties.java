package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S20PacketEntityProperties implements Packet<INetHandlerPlayClient> {
   public List<S20PacketEntityProperties.Snapshot> entries = Lists.newArrayList();
   public int entityId;

   public S20PacketEntityProperties() {
   }

   public S20PacketEntityProperties(int entityIdIn, Collection<IAttributeInstance> collection) {
      this.entityId = entityIdIn;

      for (IAttributeInstance attributeInstance : collection) {
         this.entries.add(new S20PacketEntityProperties.Snapshot(attributeInstance.getAttribute().getAttributeUnlocalizedName(), attributeInstance.getBaseValue(), attributeInstance.getCollection()));
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarIntFromBuffer();
      int readInt = buf.readInt();

      for (int i = 0; i < readInt; i++) {
         String readStringFromBuffer = buf.readStringFromBuffer(64);
         double readDouble = buf.readDouble();
         ArrayList newArrayList = Lists.newArrayList();
         int readVarIntFromBuffer = buf.readVarIntFromBuffer();

         for (int j = 0; j < readVarIntFromBuffer; j++) {
            UUID readUuid = buf.readUuid();
            newArrayList.add(new AttributeModifier(readUuid, "Unknown synced attribute modifier", buf.readDouble(), buf.readByte()));
         }

         this.entries.add(new S20PacketEntityProperties.Snapshot(readStringFromBuffer, readDouble, newArrayList));
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.entityId);
      buf.writeInt(this.entries.size());

      for (S20PacketEntityProperties.Snapshot snapshot : this.entries) {
         buf.writeString(snapshot.getId());
         buf.writeDouble(snapshot.getBaseValue());
         buf.writeVarIntToBuffer(snapshot.getModifiers().size());

         for (AttributeModifier attributeModifier : snapshot.getModifiers()) {
            buf.writeUuid(attributeModifier.getID());
            buf.writeDouble(attributeModifier.getAmount());
            buf.writeByte(attributeModifier.getOperation());
         }
      }
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleEntityProperties(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public List<S20PacketEntityProperties.Snapshot> getEntries() {
      return this.entries;
   }

   public class Snapshot {
      public String id;
      public double baseValue;
      public Collection<AttributeModifier> modifiers;

      public Snapshot(String str, double d, Collection<AttributeModifier> collection) {
         this.id = str;
         this.baseValue = d;
         this.modifiers = collection;
      }

      public String getId() {
         return this.id;
      }

      public double getBaseValue() {
         return this.baseValue;
      }

      public Collection<AttributeModifier> getModifiers() {
         return this.modifiers;
      }
   }
}
