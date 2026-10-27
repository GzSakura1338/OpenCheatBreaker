package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.IScoreObjectiveCriteria.EnumRenderType;
import net.minecraft.scoreboard.ScoreObjective;

public class S3BPacketScoreboardObjective implements Packet<INetHandlerPlayClient> {
   public String objectiveName;
   public String objectiveValue;
   public EnumRenderType type;
   public int mode;

   public S3BPacketScoreboardObjective() {
   }

   public S3BPacketScoreboardObjective(ScoreObjective scoreObjective, int n) {
      this.objectiveName = scoreObjective.getName();
      this.objectiveValue = scoreObjective.getDisplayName();
      this.type = scoreObjective.getCriteria().getRenderType();
      this.mode = n;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.objectiveName = buf.readStringFromBuffer(16);
      this.mode = buf.readByte();
      if (this.mode == 0 || this.mode == 2) {
         this.objectiveValue = buf.readStringFromBuffer(32);
         this.type = EnumRenderType.getByName(buf.readStringFromBuffer(16));
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeString(this.objectiveName);
      buf.writeByte(this.mode);
      if (this.mode == 0 || this.mode == 2) {
         buf.writeString(this.objectiveValue);
         buf.writeString(this.type.getName());
      }
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleScoreboardObjective(this);
   }

   public String getName() {
      return this.objectiveName;
   }

   public String getDisplayName() {
      return this.objectiveValue;
   }

   public int getMode() {
      return this.mode;
   }

   public EnumRenderType getType() {
      return this.type;
   }
}
