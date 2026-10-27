package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

public class S37PacketStatistics implements Packet<INetHandlerPlayClient> {
   public Map<StatBase, Integer> stats;

   public S37PacketStatistics() {
   }

   public S37PacketStatistics(Map<StatBase, Integer> map) {
      this.stats = map;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleStatistics(this);
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      int readVarIntFromBuffer = buf.readVarIntFromBuffer();
      this.stats = Maps.newHashMap();

      for (int i = 0; i < readVarIntFromBuffer; i++) {
         StatBase oneShotStat = StatList.getOneShotStat(buf.readStringFromBuffer(32767));
         int readVarIntFromBuffer2 = buf.readVarIntFromBuffer();
         if (oneShotStat != null) {
            this.stats.put(oneShotStat, readVarIntFromBuffer2);
         }
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.stats.size());

      for (Entry entry : this.stats.entrySet()) {
         buf.writeString(((StatBase)entry.getKey()).statId);
         buf.writeVarIntToBuffer((Integer)entry.getValue());
      }
   }

   public Map<StatBase, Integer> getStatMap() {
      return this.stats;
   }
}
