package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

public class NextTickListEntry implements Comparable<NextTickListEntry> {
   public static long nextTickEntryID;
   public long tickEntryID;
   public BlockPos position;
   public long scheduledTime;
   public int priority;
   public Block block;

   public NextTickListEntry(BlockPos positionIn, Block blockIn) {
      this.tickEntryID = nextTickEntryID++;
      this.position = positionIn;
      this.block = blockIn;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof NextTickListEntry)) {
         return false;
      }

      NextTickListEntry nextTickListEntry = (NextTickListEntry)obj;
      return this.position.equals(nextTickListEntry.position) && Block.isEqualTo(this.block, nextTickListEntry.block);
   }

   @Override
   public int hashCode() {
      return this.position.hashCode();
   }

   public NextTickListEntry setScheduledTime(long scheduledTimeIn) {
      this.scheduledTime = scheduledTimeIn;
      return this;
   }

   public void setPriority(int priorityIn) {
      this.priority = priorityIn;
   }

   public int compareTo(NextTickListEntry nextTickListEntry) {
      return this.scheduledTime < nextTickListEntry.scheduledTime
         ? -1
         : (
            this.scheduledTime > nextTickListEntry.scheduledTime
               ? 1
               : (
                  this.priority != nextTickListEntry.priority
                     ? this.priority - nextTickListEntry.priority
                     : (this.tickEntryID < nextTickListEntry.tickEntryID ? -1 : (this.tickEntryID > nextTickListEntry.tickEntryID ? 1 : 0))
               )
         );
   }

   @Override
   public String toString() {
      return Block.getIdFromBlock(this.block) + ": " + this.position + ", " + this.scheduledTime + ", " + this.priority + ", " + this.tickEntryID;
   }

   public Block getBlock() {
      return this.block;
   }
}
