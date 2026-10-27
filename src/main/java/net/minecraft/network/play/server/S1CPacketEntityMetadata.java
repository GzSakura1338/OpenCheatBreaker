package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.DataWatcher.WatchableObject;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S1CPacketEntityMetadata implements Packet<INetHandlerPlayClient> {
   public int entityId;
   public List<WatchableObject> trackedValues;

   public S1CPacketEntityMetadata() {
   }

   public S1CPacketEntityMetadata(int entityIdIn, DataWatcher dataWatcher, boolean flag) {
      this.entityId = entityIdIn;
      if (flag) {
         this.trackedValues = dataWatcher.getAllWatched();
      } else {
         this.trackedValues = dataWatcher.getChanged();
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarIntFromBuffer();
      this.trackedValues = DataWatcher.readWatchedListFromPacketBuffer(buf);
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.entityId);
      DataWatcher.writeWatchedListToPacketBuffer(this.trackedValues, buf);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleEntityMetadata(this);
   }

   public List<WatchableObject> getTrackedValues() {
      return this.trackedValues;
   }

   public int getEntityId() {
      return this.entityId;
   }
}
