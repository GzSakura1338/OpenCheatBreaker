package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S0APacketUseBed extends Packet {
    public int x;
    public int y;
    public int z;
    public int playerID;

    public S0APacketUseBed() {
    }

    public S0APacketUseBed(EntityPlayer player, int xPos, int yPos, int zPos) {
        this.x = xPos;
        this.y = yPos;
        this.z = zPos;
        this.playerID = player.getEntityId();
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.playerID = data.readInt();
        this.x = data.readInt();
        this.y = data.readByte();
        this.z = data.readInt();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.playerID);
        data.writeInt(this.x);
        data.writeByte(this.y);
        data.writeInt(this.z);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleUseBed(this);
    }

    public EntityPlayer getPlayer(World world) {
        return (EntityPlayer)world.getEntityByID(this.playerID);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
