package net.minecraft.block;

import net.minecraft.util.BlockPos;

public class BlockEventData {
   public BlockPos position;
   public int eventID;
   public int eventParameter;
   public Block blockType;

   public BlockEventData(BlockPos pos, Block blockType, int eventId, int n) {
      this.position = pos;
      this.eventID = eventId;
      this.eventParameter = n;
      this.blockType = blockType;
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public int getEventID() {
      return this.eventID;
   }

   public int getEventParameter() {
      return this.eventParameter;
   }

   public Block getBlock() {
      return this.blockType;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof BlockEventData)) {
         return false;
      }

      BlockEventData blockEventData = (BlockEventData)obj;
      return this.position.equals(blockEventData.position)
         && this.eventID == blockEventData.eventID
         && this.eventParameter == blockEventData.eventParameter
         && this.blockType == blockEventData.blockType;
   }

   @Override
   public String toString() {
      return "TE(" + this.position + ")," + this.eventID + "," + this.eventParameter + "," + this.blockType;
   }
}
