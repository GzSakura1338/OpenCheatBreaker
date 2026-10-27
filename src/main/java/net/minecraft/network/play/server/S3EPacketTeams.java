package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team.EnumVisible;

public class S3EPacketTeams implements Packet<INetHandlerPlayClient> {
   public String name = "";
   public String displayName = "";
   public String prefix = "";
   public String suffix = "";
   public String nameTagVisibility = EnumVisible.ALWAYS.internalName;
   public int color = -1;
   public Collection<String> players = Lists.newArrayList();
   public int action;
   public int friendlyFlags;

   public S3EPacketTeams() {
   }

   public S3EPacketTeams(ScorePlayerTeam teamIn, int actionIn) {
      this.name = teamIn.getRegisteredName();
      this.action = actionIn;
      if (actionIn == 0 || actionIn == 2) {
         this.displayName = teamIn.getTeamName();
         this.prefix = teamIn.getColorPrefix();
         this.suffix = teamIn.getColorSuffix();
         this.friendlyFlags = teamIn.getFriendlyFlagsBitwise();
         this.nameTagVisibility = teamIn.getNameTagVisibility().internalName;
         this.color = teamIn.getChatFormat().getColorIndex();
      }

      if (actionIn == 0) {
         this.players.addAll(teamIn.getMembershipCollection());
      }
   }

   public S3EPacketTeams(ScorePlayerTeam teamIn, Collection<String> playersIn, int actionIn) {
      if (actionIn != 3 && actionIn != 4) {
         throw new IllegalArgumentException("Method must be join or leave for player constructor");
      }

      if (playersIn != null && !playersIn.isEmpty()) {
         this.action = actionIn;
         this.name = teamIn.getRegisteredName();
         this.players.addAll(playersIn);
      } else {
         throw new IllegalArgumentException("Players cannot be null/empty");
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.name = buf.readStringFromBuffer(16);
      this.action = buf.readByte();
      if (this.action == 0 || this.action == 2) {
         this.displayName = buf.readStringFromBuffer(32);
         this.prefix = buf.readStringFromBuffer(16);
         this.suffix = buf.readStringFromBuffer(16);
         this.friendlyFlags = buf.readByte();
         this.nameTagVisibility = buf.readStringFromBuffer(32);
         this.color = buf.readByte();
      }

      if (this.action == 0 || this.action == 3 || this.action == 4) {
         int readVarIntFromBuffer = buf.readVarIntFromBuffer();

         for (int i = 0; i < readVarIntFromBuffer; i++) {
            this.players.add(buf.readStringFromBuffer(40));
         }
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeString(this.name);
      buf.writeByte(this.action);
      if (this.action == 0 || this.action == 2) {
         buf.writeString(this.displayName);
         buf.writeString(this.prefix);
         buf.writeString(this.suffix);
         buf.writeByte(this.friendlyFlags);
         buf.writeString(this.nameTagVisibility);
         buf.writeByte(this.color);
      }

      if (this.action == 0 || this.action == 3 || this.action == 4) {
         buf.writeVarIntToBuffer(this.players.size());

         for (String str : this.players) {
            buf.writeString(str);
         }
      }
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleTeams(this);
   }

   public String getName() {
      return this.name;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public String getSuffix() {
      return this.suffix;
   }

   public Collection<String> getPlayers() {
      return this.players;
   }

   public int getAction() {
      return this.action;
   }

   public int getFriendlyFlags() {
      return this.friendlyFlags;
   }

   public int getColor() {
      return this.color;
   }

   public String getNameTagVisibility() {
      return this.nameTagVisibility;
   }
}
