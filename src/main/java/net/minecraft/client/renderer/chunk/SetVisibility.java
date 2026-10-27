package net.minecraft.client.renderer.chunk;

import java.util.Set;
import net.minecraft.util.EnumFacing;

public class SetVisibility {
   public static int COUNT_FACES = EnumFacing.values().length;
   public long bits;

   public void setManyVisible(Set<EnumFacing> set) {
      for (EnumFacing enumFacing : set) {
         for (EnumFacing enumFacing2 : set) {
            this.setVisible(enumFacing, enumFacing2, true);
         }
      }
   }

   public void setVisible(EnumFacing facing, EnumFacing facing2, boolean flag) {
      this.setBit(facing.ordinal() + facing2.ordinal() * COUNT_FACES, flag);
      this.setBit(facing2.ordinal() + facing.ordinal() * COUNT_FACES, flag);
   }

   public void setAllVisible(boolean visible) {
      if (visible) {
         this.bits = -1L;
      } else {
         this.bits = 0L;
      }
   }

   public boolean isVisible(EnumFacing facing, EnumFacing facing2) {
      return this.getBit(facing.ordinal() + facing2.ordinal() * COUNT_FACES);
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(' ');

      for (EnumFacing enumFacing : EnumFacing.values()) {
         sb.append(' ').append(enumFacing.toString().toUpperCase().charAt(0));
      }

      sb.append('\n');

      for (EnumFacing enumFacing : EnumFacing.values()) {
         sb.append(enumFacing.toString().toUpperCase().charAt(0));

         for (EnumFacing enumFacing2 : EnumFacing.values()) {
            if (enumFacing == enumFacing2) {
               sb.append("  ");
            } else {
               boolean isVisible = this.isVisible(enumFacing, enumFacing2);
               sb.append(' ').append((char)(isVisible ? 'Y' : 'n'));
            }
         }

         sb.append('\n');
      }

      return sb.toString();
   }

   public boolean getBit(int n) {
      return (this.bits & 1 << n) != 0L;
   }

   public void setBit(int n, boolean flag) {
      if (flag) {
         this.setBit(n);
      } else {
         this.clearBit(n);
      }
   }

   public void setBit(int n) {
      this.bits |= 1 << n;
   }

   public void clearBit(int n) {
      this.bits &= ~(1 << n);
   }
}
