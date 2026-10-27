package net.optifine.shaders;

import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType;

public class SVertexAttrib {
   public int index;
   public int count;
   public EnumType type;
   public int offset;

   public SVertexAttrib(int n, int n2, EnumType enumType) {
      this.index = n;
      this.count = n2;
      this.type = enumType;
   }
}
