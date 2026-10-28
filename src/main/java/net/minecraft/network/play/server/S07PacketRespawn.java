package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldType;
import net.minecraft.world.WorldSettings.GameType;

public class S07PacketRespawn extends Packet {
    public int dimensionId;
    public EnumDifficulty difficulty;
    public GameType gameType;
    public WorldType generatorType;

    public S07PacketRespawn() {
    }

    public S07PacketRespawn(int n, EnumDifficulty enumDifficulty, WorldType worldType, GameType gameType) {
        this.dimensionId = n;
        this.difficulty = enumDifficulty;
        this.gameType = gameType;
        this.generatorType = worldType;
    }

    public void processPacket(INetHandlerPlayClient handler) throws IOException {
        handler.handleRespawn(this);
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.dimensionId = data.readInt();
        this.difficulty = EnumDifficulty.getDifficultyEnum(data.readUnsignedByte());
        this.gameType = GameType.getByID(data.readUnsignedByte());
        this.generatorType = WorldType.parseWorldType(data.readStringFromBuffer(16));
        if (this.generatorType == null) {
            this.generatorType = WorldType.DEFAULT;
        }
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.dimensionId);
        data.writeByte(this.difficulty.getDifficultyId());
        data.writeByte(this.gameType.getID());
        data.writeStringToBuffer(this.generatorType.getWorldTypeName());
    }

    public int getDimensionId() {
        return this.dimensionId;
    }

    public EnumDifficulty getDifficulty() {
        return this.difficulty;
    }

    public GameType getGameType() {
        return this.gameType;
    }

    public WorldType getGeneratorType() {
        return this.generatorType;
    }

    @Override
    public void processPacket(INetHandler netHandler) throws IOException {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
