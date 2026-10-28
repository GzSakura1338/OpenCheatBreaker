package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldType;
import net.minecraft.world.WorldSettings.GameType;

public class S01PacketJoinGame extends Packet {
    public int playerEntityId;
    public int viewDistance;
    public EnumDifficulty difficulty;
    public GameType gameMode;
    public int maxPlayers;
    public boolean hardcore;
    public WorldType levelGeneratorType;

    public S01PacketJoinGame() {
    }

    public S01PacketJoinGame(int n, GameType gameType, boolean flag, int n2, EnumDifficulty enumDifficulty, int n3, WorldType worldType) {
        this.playerEntityId = n;
        this.viewDistance = n2;
        this.difficulty = enumDifficulty;
        this.gameMode = gameType;
        this.maxPlayers = n3;
        this.hardcore = flag;
        this.levelGeneratorType = worldType;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.playerEntityId = data.readInt();
        short readUnsignedByte = data.readUnsignedByte();
        this.hardcore = (readUnsignedByte & 8) == 8;
        int n = readUnsignedByte & -9;
        this.gameMode = GameType.getByID(n);
        this.viewDistance = data.readByte();
        this.difficulty = EnumDifficulty.getDifficultyEnum(data.readUnsignedByte());
        this.maxPlayers = data.readUnsignedByte();
        this.levelGeneratorType = WorldType.parseWorldType(data.readStringFromBuffer(16));
        if (this.levelGeneratorType == null) {
            this.levelGeneratorType = WorldType.DEFAULT;
        }
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.playerEntityId);
        int iD = this.gameMode.getID();
        if (this.hardcore) {
            iD |= 8;
        }

        data.writeByte(iD);
        data.writeByte(this.viewDistance);
        data.writeByte(this.difficulty.getDifficultyId());
        data.writeByte(this.maxPlayers);
        data.writeStringToBuffer(this.levelGeneratorType.getWorldTypeName());
    }

    public void processPacket(INetHandlerPlayClient handler) throws IOException {
        handler.handleJoinGame(this);
    }

    @Override
    public String serialize() {
        return String.format(
            "eid=%d, gameType=%d, hardcore=%b, dimension=%d, difficulty=%s, maxplayers=%d",
            this.playerEntityId,
            this.gameMode.getID(),
            this.hardcore,
            this.viewDistance,
            this.difficulty,
            this.maxPlayers
        );
    }

    public int getEntityId() {
        return this.playerEntityId;
    }

    public boolean isHardcore() {
        return this.hardcore;
    }

    public GameType getGameMode() {
        return this.gameMode;
    }

    public int getChunkLoadDistance() {
        return this.viewDistance;
    }

    public EnumDifficulty getDifficulty() {
        return this.difficulty;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public WorldType getGeneratorType() {
        return this.levelGeneratorType;
    }

    @Override
    public void processPacket(INetHandler netHandler) throws IOException {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
