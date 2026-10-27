package net.minecraft.util;

public class MouseFilter {
   public float actualSum;
   public float smoothedSum;
   public float movementLatency;

   public float smooth(float f, float f2) {
      this.actualSum += f;
      f = (this.actualSum - this.smoothedSum) * f2;
      this.movementLatency = this.movementLatency + (f - this.movementLatency) * 0.5F;
      if (f > 0.0F && f > this.movementLatency || f < 0.0F && f < this.movementLatency) {
         f = this.movementLatency;
      }

      this.smoothedSum += f;
      return f;
   }

   public void reset() {
      this.actualSum = 0.0F;
      this.smoothedSum = 0.0F;
      this.movementLatency = 0.0F;
   }
}
