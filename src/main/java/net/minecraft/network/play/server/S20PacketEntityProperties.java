package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S20PacketEntityProperties extends Packet {
    public List entries = new ArrayList();
    public int entityId;

    public S20PacketEntityProperties() {
    }

    public S20PacketEntityProperties(int n, Collection collection) {
        this.entityId = n;

        for (IAttributeInstance attributeInstance : (Iterable<IAttributeInstance>)collection) {
            this.entries.add(new S20PacketEntityProperties.Snapshot(attributeInstance.getAttribute().getAttributeUnlocalizedName(), attributeInstance.getBaseValue(), attributeInstance.createHashSet()));
        }
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.entityId = data.readInt();
        int readInt = data.readInt();

        for (int i = 0; i < readInt; i++) {
            String readStringFromBuffer = data.readStringFromBuffer(64);
            double readDouble = data.readDouble();
            ArrayList list = new ArrayList();
            short readShort = data.readShort();

            for (int j = 0; j < readShort; j++) {
                UUID uuid = new UUID(data.readLong(), data.readLong());
                list.add(new AttributeModifier(uuid, "Unknown synced attribute modifier", data.readDouble(), data.readByte()));
            }

            this.entries.add(new S20PacketEntityProperties.Snapshot(readStringFromBuffer, readDouble, list));
        }
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.entityId);
        data.writeInt(this.entries.size());

        for (S20PacketEntityProperties.Snapshot snapshot : (Iterable<S20PacketEntityProperties.Snapshot>)this.entries) {
            data.writeStringToBuffer(snapshot.getText());
            data.writeDouble(snapshot.getDoubleValue());
            data.writeShort(snapshot.getList().size());

            for (AttributeModifier attributeModifier : (Iterable<AttributeModifier>)snapshot.getList()) {
                data.writeLong(attributeModifier.getID().getMostSignificantBits());
                data.writeLong(attributeModifier.getID().getLeastSignificantBits());
                data.writeDouble(attributeModifier.getAmount());
                data.writeByte(attributeModifier.getOperation());
            }
        }
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityProperties(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public List getEntries() {
        return this.entries;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }

    public class Snapshot {
        public String text;
        public double doubleValue;
        public Collection list;

        public Snapshot(String str, double d, Collection collection) {
            this.text = str;
            this.doubleValue = d;
            this.list = collection;
        }

        public String getText() {
            return this.text;
        }

        public double getDoubleValue() {
            return this.doubleValue;
        }

        public Collection getList() {
            return this.list;
        }
    }
}
