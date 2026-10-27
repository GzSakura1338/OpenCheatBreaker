package net.optifine.texture;

public enum TextureType {
   TEXTURE_1D(3552),
   TEXTURE_2D(3553),
   TEXTURE_3D(32879),
   TEXTURE_RECTANGLE(34037);

   public int intValue;

   TextureType(int n) {
      this.intValue = n;
   }

   public int getId() {
      return this.intValue;
   }
}
