package net.minecraft.client.resources.data;

public class AnimationFrame {
   public int frameIndex;
   public int frameTime;

   public AnimationFrame(int n) {
      this(n, -1);
   }

   public AnimationFrame(int n, int n2) {
      this.frameIndex = n;
      this.frameTime = n2;
   }

   public boolean hasNoTime() {
      return this.frameTime == -1;
   }

   public int getFrameTime() {
      return this.frameTime;
   }

   public int getFrameIndex() {
      return this.frameIndex;
   }
}
