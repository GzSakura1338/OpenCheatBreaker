package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;

public class S22PacketMultiBlockChange implements Packet<INetHandlerPlayClient> {
   public ChunkCoordIntPair chunkPosCoord;
   public S22PacketMultiBlockChange.BlockUpdateData[] changedBlocks;

   public S22PacketMultiBlockChange() {
   }

   public S22PacketMultiBlockChange(int n, short[] crammedPositionsIn, Chunk chunkIn) {
      this.chunkPosCoord = new ChunkCoordIntPair(chunkIn.xPosition, chunkIn.zPosition);
      this.changedBlocks = new S22PacketMultiBlockChange.BlockUpdateData[n];

      for (int i = 0; i < this.changedBlocks.length; i++) {
         this.changedBlocks[i] = new S22PacketMultiBlockChange.BlockUpdateData(crammedPositionsIn[i], chunkIn);
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.chunkPosCoord = new ChunkCoordIntPair(buf.readInt(), buf.readInt());
      this.changedBlocks = new S22PacketMultiBlockChange.BlockUpdateData[buf.readVarIntFromBuffer()];

      for (int i = 0; i < this.changedBlocks.length; i++) {
         this.changedBlocks[i] = new S22PacketMultiBlockChange.BlockUpdateData(buf.readShort(), Block.BLOCK_STATE_IDS.getByValue(buf.readVarIntFromBuffer()));
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(this.chunkPosCoord.chunkXPos);
      buf.writeInt(this.chunkPosCoord.chunkZPos);
      buf.writeVarIntToBuffer(this.changedBlocks.length);

      for (S22PacketMultiBlockChange.BlockUpdateData blockUpdateData : this.changedBlocks) {
         buf.writeShort(blockUpdateData.getPosShort());
         buf.writeVarIntToBuffer(Block.BLOCK_STATE_IDS.get(blockUpdateData.getBlockState()));
      }
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleMultiBlockChange(this);
   }

   public S22PacketMultiBlockChange.BlockUpdateData[] getChangedBlocks() {
      return this.changedBlocks;
   }

   public class BlockUpdateData {
      public short chunkPosCrammed;
      public IBlockState blockState;

      public BlockUpdateData(short s, IBlockState state) {
         this.chunkPosCrammed = s;
         this.blockState = state;
      }

      public BlockUpdateData(short s, Chunk chunkIn) {
         this.chunkPosCrammed = s;
         this.blockState = chunkIn.getBlockState(this.getPos());
      }

      public BlockPos getPos() {
         return new BlockPos(
            S22PacketMultiBlockChange.this.chunkPosCoord.getBlock(this.chunkPosCrammed >> 12 & 15, this.chunkPosCrammed & 255, this.chunkPosCrammed >> 8 & 15)
         );
      }

      public short getPosShort() {
         return this.chunkPosCrammed;
      }

      public IBlockState getBlockState() {
         return this.blockState;
      }
   }
}
