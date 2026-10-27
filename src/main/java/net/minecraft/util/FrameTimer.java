package net.minecraft.util;

public class FrameTimer {
   public long[] frames = new long[240];
   public int lastIndex;
   public int counter;
   public int index;

   public void addFrame(long runningTime) {
      this.frames[this.index] = runningTime;
      this.index++;
      if (this.index == 240) {
         this.index = 0;
      }

      if (this.counter < 240) {
         this.lastIndex = 0;
         this.counter++;
      } else {
         this.lastIndex = this.parseIndex(this.index + 1);
      }
   }

   public int getLagometerValue(long time, int multiplier) {
      double d = time / 1.6666666E7;
      return (int)(d * multiplier);
   }

   public int getLastIndex() {
      return this.lastIndex;
   }

   public int getIndex() {
      return this.index;
   }

   public int parseIndex(int rawIndex) {
      return rawIndex % 240;
   }

   public long[] getFrames() {
      return this.frames;
   }
}
