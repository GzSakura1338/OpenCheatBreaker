package net.minecraft.util;

public class Vec4b {
   public byte typeId;
   public byte x;
   public byte y;
   public byte rot;

   public Vec4b(byte b, byte b2, byte b3, byte b4) {
      this.typeId = b;
      this.x = b2;
      this.y = b3;
      this.rot = b4;
   }

   public Vec4b(Vec4b vec4b) {
      this.typeId = vec4b.typeId;
      this.x = vec4b.x;
      this.y = vec4b.y;
      this.rot = vec4b.rot;
   }

   public byte getTypeId() {
      return this.typeId;
   }

   public byte getX() {
      return this.x;
   }

   public byte getY() {
      return this.y;
   }

   public byte getRotation() {
      return this.rot;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof Vec4b)) {
         return false;
      }

      Vec4b vec4b = (Vec4b)obj;
      return this.typeId != vec4b.typeId ? false : (this.rot != vec4b.rot ? false : (this.x != vec4b.x ? false : this.y == vec4b.y));
   }

   @Override
   public int hashCode() {
      int typeId = this.typeId;
      typeId = 31 * typeId + this.x;
      typeId = 31 * typeId + this.y;
      return 31 * typeId + this.rot;
   }
}
