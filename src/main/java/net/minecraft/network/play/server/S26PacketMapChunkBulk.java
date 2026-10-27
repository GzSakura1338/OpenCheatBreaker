package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;

public class S26PacketMapChunkBulk implements Packet<INetHandlerPlayClient> {
   public int[] xPositions;
   public int[] zPositions;
   public S21PacketChunkData.Extracted[] chunksData;
   public boolean isOverworld;

   public S26PacketMapChunkBulk() {
   }

   public S26PacketMapChunkBulk(List<Chunk> chunks) {
      int size = chunks.size();
      this.xPositions = new int[size];
      this.zPositions = new int[size];
      this.chunksData = new S21PacketChunkData.Extracted[size];
      this.isOverworld = !((Chunk)chunks.get(0)).getWorld().provider.getHasNoSky();

      for (int i = 0; i < size; i++) {
         Chunk chunk = (Chunk)chunks.get(i);
         S21PacketChunkData.Extracted extractedData = S21PacketChunkData.getExtractedData(chunk, true, this.isOverworld, 65535);
         this.xPositions[i] = chunk.xPosition;
         this.zPositions[i] = chunk.zPosition;
         this.chunksData[i] = extractedData;
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.isOverworld = buf.readBoolean();
      int readVarIntFromBuffer = buf.readVarIntFromBuffer();
      this.xPositions = new int[readVarIntFromBuffer];
      this.zPositions = new int[readVarIntFromBuffer];
      this.chunksData = new S21PacketChunkData.Extracted[readVarIntFromBuffer];

      for (int i = 0; i < readVarIntFromBuffer; i++) {
         this.xPositions[i] = buf.readInt();
         this.zPositions[i] = buf.readInt();
         this.chunksData[i] = new S21PacketChunkData.Extracted();
         this.chunksData[i].dataSize = buf.readShort() & '\uffff';
         this.chunksData[i].data = new byte[S21PacketChunkData.computeInt(Integer.bitCount(this.chunksData[i].dataSize), this.isOverworld, true)];
      }

      for (int i = 0; i < readVarIntFromBuffer; i++) {
         buf.readBytes(this.chunksData[i].data);
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeBoolean(this.isOverworld);
      buf.writeVarIntToBuffer(this.chunksData.length);

      for (int i = 0; i < this.xPositions.length; i++) {
         buf.writeInt(this.xPositions[i]);
         buf.writeInt(this.zPositions[i]);
         buf.writeShort((short)(this.chunksData[i].dataSize & 65535));
      }

      for (int i = 0; i < this.xPositions.length; i++) {
         buf.writeBytes(this.chunksData[i].data);
      }
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleMapChunkBulk(this);
   }

   public int getChunkX(int n) {
      return this.xPositions[n];
   }

   public int getChunkZ(int n) {
      return this.zPositions[n];
   }

   public int getChunkCount() {
      return this.xPositions.length;
   }

   public byte[] getChunkBytes(int n) {
      return this.chunksData[n].data;
   }

   public int getChunkSize(int n) {
      return this.chunksData[n].dataSize;
   }
}
