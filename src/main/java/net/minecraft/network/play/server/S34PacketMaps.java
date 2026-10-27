package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.Vec4b;
import net.minecraft.world.storage.MapData;

public class S34PacketMaps implements Packet<INetHandlerPlayClient> {
   public int mapId;
   public byte mapScale;
   public Vec4b[] mapVisiblePlayersVec4b;
   public int mapMinX;
   public int mapMinY;
   public int mapMaxX;
   public int mapMaxY;
   public byte[] mapDataBytes;

   public S34PacketMaps() {
   }

   public S34PacketMaps(int mapIdIn, byte scale, Collection<Vec4b> visiblePlayers, byte[] colors, int minX, int minY, int maxX, int maxY) {
      this.mapId = mapIdIn;
      this.mapScale = scale;
      this.mapVisiblePlayersVec4b = visiblePlayers.toArray(new Vec4b[visiblePlayers.size()]);
      this.mapMinX = minX;
      this.mapMinY = minY;
      this.mapMaxX = maxX;
      this.mapMaxY = maxY;
      this.mapDataBytes = new byte[maxX * maxY];

      for (int i = 0; i < maxX; i++) {
         for (int j = 0; j < maxY; j++) {
            this.mapDataBytes[i + j * maxX] = colors[minX + i + (minY + j) * 128];
         }
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.mapId = buf.readVarIntFromBuffer();
      this.mapScale = buf.readByte();
      this.mapVisiblePlayersVec4b = new Vec4b[buf.readVarIntFromBuffer()];

      for (int i = 0; i < this.mapVisiblePlayersVec4b.length; i++) {
         short readByte = buf.readByte();
         this.mapVisiblePlayersVec4b[i] = new Vec4b((byte)(readByte >> 4 & 15), buf.readByte(), buf.readByte(), (byte)(readByte & 15));
      }

      this.mapMaxX = buf.readUnsignedByte();
      if (this.mapMaxX > 0) {
         this.mapMaxY = buf.readUnsignedByte();
         this.mapMinX = buf.readUnsignedByte();
         this.mapMinY = buf.readUnsignedByte();
         this.mapDataBytes = buf.readByteArray();
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.mapId);
      buf.writeByte(this.mapScale);
      buf.writeVarIntToBuffer(this.mapVisiblePlayersVec4b.length);

      for (Vec4b vec4b : this.mapVisiblePlayersVec4b) {
         buf.writeByte((vec4b.getTypeId() & 15) << 4 | vec4b.getRotation() & 15);
         buf.writeByte(vec4b.getX());
         buf.writeByte(vec4b.getY());
      }

      buf.writeByte(this.mapMaxX);
      if (this.mapMaxX > 0) {
         buf.writeByte(this.mapMaxY);
         buf.writeByte(this.mapMinX);
         buf.writeByte(this.mapMinY);
         buf.writeByteArray(this.mapDataBytes);
      }
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleMaps(this);
   }

   public int getMapId() {
      return this.mapId;
   }

   public void setMapdataTo(MapData mapdataIn) {
      mapdataIn.scale = this.mapScale;
      mapdataIn.mapDecorations.clear();

      for (int i = 0; i < this.mapVisiblePlayersVec4b.length; i++) {
         Vec4b vec4b = this.mapVisiblePlayersVec4b[i];
         mapdataIn.mapDecorations.put("icon-" + i, vec4b);
      }

      for (int i = 0; i < this.mapMaxX; i++) {
         for (int j = 0; j < this.mapMaxY; j++) {
            mapdataIn.colors[this.mapMinX + i + (this.mapMinY + j) * 128] = this.mapDataBytes[i + j * this.mapMaxX];
         }
      }
   }
}
