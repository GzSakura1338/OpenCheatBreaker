package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.WorldType;

public class S01PacketJoinGame implements Packet<INetHandlerPlayClient> {
   public int entityId;
   public int dimension;
   public EnumDifficulty difficulty;
   public GameType gameType;
   public int maxPlayers;
   public boolean hardcoreMode;
   public WorldType worldType;
   public boolean reducedDebugInfo;

   public S01PacketJoinGame() {
   }

   public S01PacketJoinGame(
      int entityIdIn,
      GameType gameTypeIn,
      boolean hardcoreModeIn,
      int dimensionIn,
      EnumDifficulty difficultyIn,
      int maxPlayersIn,
      WorldType worldTypeIn,
      boolean reducedDebugInfoIn
   ) {
      this.entityId = entityIdIn;
      this.dimension = dimensionIn;
      this.difficulty = difficultyIn;
      this.gameType = gameTypeIn;
      this.maxPlayers = maxPlayersIn;
      this.hardcoreMode = hardcoreModeIn;
      this.worldType = worldTypeIn;
      this.reducedDebugInfo = reducedDebugInfoIn;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readInt();
      int readUnsignedByte = buf.readUnsignedByte();
      this.hardcoreMode = (readUnsignedByte & 8) == 8;
      readUnsignedByte &= -9;
      this.gameType = GameType.getByID(readUnsignedByte);
      this.dimension = buf.readByte();
      this.difficulty = EnumDifficulty.getDifficultyEnum(buf.readUnsignedByte());
      this.maxPlayers = buf.readUnsignedByte();
      this.worldType = WorldType.parseWorldType(buf.readStringFromBuffer(16));
      if (this.worldType == null) {
         this.worldType = WorldType.DEFAULT;
      }

      this.reducedDebugInfo = buf.readBoolean();
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(this.entityId);
      int iD = this.gameType.getID();
      if (this.hardcoreMode) {
         iD |= 8;
      }

      buf.writeByte(iD);
      buf.writeByte(this.dimension);
      buf.writeByte(this.difficulty.getDifficultyId());
      buf.writeByte(this.maxPlayers);
      buf.writeString(this.worldType.getWorldTypeName());
      buf.writeBoolean(this.reducedDebugInfo);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleJoinGame(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public boolean isHardcoreMode() {
      return this.hardcoreMode;
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public int getDimension() {
      return this.dimension;
   }

   public EnumDifficulty getDifficulty() {
      return this.difficulty;
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public WorldType getWorldType() {
      return this.worldType;
   }

   public boolean isReducedDebugInfo() {
      return this.reducedDebugInfo;
   }
}
