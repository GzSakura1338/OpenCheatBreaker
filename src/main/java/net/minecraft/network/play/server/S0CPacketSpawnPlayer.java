package net.minecraft.network.play.server;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S0CPacketSpawnPlayer extends Packet {
    public int id;
    public GameProfile gameProfile;
    public int x;
    public int y;
    public int z;
    public byte yaw;
    public byte pitch;
    public int handStackId;
    public DataWatcher tracker;
    public List dataTrackerEntries;

    public S0CPacketSpawnPlayer() {
    }

    public S0CPacketSpawnPlayer(EntityPlayer entityPlayer) {
        this.id = entityPlayer.getEntityId();
        this.gameProfile = entityPlayer.getGameProfile();
        this.x = MathHelper.floor_double(entityPlayer.posX * 32.0);
        this.y = MathHelper.floor_double(entityPlayer.posY * 32.0);
        this.z = MathHelper.floor_double(entityPlayer.posZ * 32.0);
        this.yaw = (byte)(entityPlayer.rotationYaw * 256.0F / 360.0F);
        this.pitch = (byte)(entityPlayer.rotationPitch * 256.0F / 360.0F);
        ItemStack currentItem = entityPlayer.inventory.getCurrentItem();
        this.handStackId = currentItem == null ? 0 : Item.getIdFromItem(currentItem.getItem());
        this.tracker = entityPlayer.getDataWatcher();
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.id = data.readVarIntFromBuffer();
        UUID fromString = UUID.fromString(data.readStringFromBuffer(36));
        this.gameProfile = new GameProfile(fromString, data.readStringFromBuffer(16));
        int readVarIntFromBuffer = data.readVarIntFromBuffer();

        for (int i = 0; i < readVarIntFromBuffer; i++) {
            String readStringFromBuffer = data.readStringFromBuffer(32767);
            String readStringFromBuffer2 = data.readStringFromBuffer(32767);
            String readStringFromBuffer3 = data.readStringFromBuffer(32767);
            this.gameProfile.getProperties().put(readStringFromBuffer, new Property(readStringFromBuffer, readStringFromBuffer2, readStringFromBuffer3));
        }

        this.x = data.readInt();
        this.y = data.readInt();
        this.z = data.readInt();
        this.yaw = data.readByte();
        this.pitch = data.readByte();
        this.handStackId = data.readShort();
        this.dataTrackerEntries = DataWatcher.readWatchedListFromPacketBuffer(data);
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeVarIntToBuffer(this.id);
        UUID id = this.gameProfile.getId();
        data.writeStringToBuffer(id == null ? "" : id.toString());
        data.writeStringToBuffer(this.gameProfile.getName());
        data.writeVarIntToBuffer(this.gameProfile.getProperties().size());

        for (Property property : this.gameProfile.getProperties().values()) {
            data.writeStringToBuffer(property.getName());
            data.writeStringToBuffer(property.getValue());
            data.writeStringToBuffer(property.getSignature());
        }

        data.writeInt(this.x);
        data.writeInt(this.y);
        data.writeInt(this.z);
        data.writeByte(this.yaw);
        data.writeByte(this.pitch);
        data.writeShort(this.handStackId);
        this.tracker.write(data);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleSpawnPlayer(this);
    }

    public List getDataTrackerEntries() {
        if (this.dataTrackerEntries == null) {
            this.dataTrackerEntries = this.tracker.getAllWatched();
        }

        return this.dataTrackerEntries;
    }

    @Override
    public String serialize() {
        return String.format(
            "id=%d, gameProfile='%s', x=%.2f, y=%.2f, z=%.2f, carried=%d",
            this.id,
            this.gameProfile,
            this.x / 32.0F,
            this.y / 32.0F,
            this.z / 32.0F,
            this.handStackId
        );
    }

    public int getId() {
        return this.id;
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
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

    public byte getYaw() {
        return this.yaw;
    }

    public byte getPitch() {
        return this.pitch;
    }

    public int getHandStackId() {
        return this.handStackId;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
