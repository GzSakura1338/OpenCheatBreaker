package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.world.World;

public class C02PacketUseEntity extends Packet {
    public int entityId;
    public C02PacketUseEntity.Action action;

    public C02PacketUseEntity() {
    }

    public C02PacketUseEntity(Entity entity, C02PacketUseEntity.Action action) {
        this.entityId = entity.getEntityId();
        this.action = action;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.entityId = data.readInt();
        this.action = C02PacketUseEntity.Action.actionArray[data.readByte() % C02PacketUseEntity.Action.actionArray.length];
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.entityId);
        data.writeByte(this.action.id);
    }

    public void processPacket(INetHandlerPlayServer handler) throws IOException {
        handler.processUseEntity(this);
    }

    public Entity getEntityFromWorld(World worldIn) {
        return worldIn.getEntityByID(this.entityId);
    }

    public C02PacketUseEntity.Action getAction() {
        return this.action;
    }

    @Override
    public void processPacket(INetHandler netHandler) throws IOException {
        this.processPacket((INetHandlerPlayServer)netHandler);
    }

    public enum Action {
        INTERACT("INTERACT", 0, 0),
        ATTACK("ATTACK", 1, 1);

        public int id;
        public static C02PacketUseEntity.Action[] actionArray = new C02PacketUseEntity.Action[values().length];
        public static C02PacketUseEntity.Action[] ACTIONS = new C02PacketUseEntity.Action[]{INTERACT, ATTACK};

        static {
            for (C02PacketUseEntity.Action action : values()) {
                actionArray[action.id] = action;
            }
        }

        Action(String str, int n, int n2) {
            this.id = n2;
        }
    }
}
