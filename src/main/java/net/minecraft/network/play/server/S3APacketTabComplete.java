package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import org.apache.commons.lang3.ArrayUtils;

public class S3APacketTabComplete extends Packet {
    public String[] suggestions;

    public S3APacketTabComplete() {
    }

    public S3APacketTabComplete(String[] strs) {
        this.suggestions = strs;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.suggestions = new String[data.readVarIntFromBuffer()];

        for (int i = 0; i < this.suggestions.length; i++) {
            this.suggestions[i] = data.readStringFromBuffer(32767);
        }
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeVarIntToBuffer(this.suggestions.length);

        for (String str : this.suggestions) {
            data.writeStringToBuffer(str);
        }
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleTabComplete(this);
    }

    public String[] getSuggestions() {
        return this.suggestions;
    }

    @Override
    public String serialize() {
        return String.format("candidates='%s'", ArrayUtils.toString(this.suggestions));
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
