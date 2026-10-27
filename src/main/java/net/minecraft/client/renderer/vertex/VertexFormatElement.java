package net.minecraft.client.renderer.vertex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VertexFormatElement {
   public static Logger LOGGER = LogManager.getLogger();
   public VertexFormatElement.EnumUsage usage;
   public VertexFormatElement.EnumType type;
   public int index;
   public int elementCount;

   public VertexFormatElement(int indexIn, VertexFormatElement.EnumType typeIn, VertexFormatElement.EnumUsage usageIn, int count) {
      if (!this.isValidType(indexIn, usageIn)) {
         LOGGER.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
         this.usage = VertexFormatElement.EnumUsage.UV;
      } else {
         this.usage = usageIn;
      }

      this.type = typeIn;
      this.index = indexIn;
      this.elementCount = count;
   }

   public boolean isValidType(int n, VertexFormatElement.EnumUsage enumUsage) {
      return n == 0 || enumUsage == VertexFormatElement.EnumUsage.UV;
   }

   public VertexFormatElement.EnumType getType() {
      return this.type;
   }

   public VertexFormatElement.EnumUsage getUsage() {
      return this.usage;
   }

   public int getElementCount() {
      return this.elementCount;
   }

   public int getIndex() {
      return this.index;
   }

   @Override
   public String toString() {
      return this.elementCount + "," + this.usage.getDisplayName() + "," + this.type.getDisplayName();
   }

   public int getSize() {
      return this.type.getSize() * this.elementCount;
   }

   public boolean isPositionElement() {
      return this.usage == VertexFormatElement.EnumUsage.POSITION;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         VertexFormatElement vertexFormatElement = (VertexFormatElement)obj;
         return this.elementCount != vertexFormatElement.elementCount
            ? false
            : (this.index != vertexFormatElement.index ? false : (this.type != vertexFormatElement.type ? false : this.usage == vertexFormatElement.usage));
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int n = this.type.hashCode();
      n = 31 * n + this.usage.hashCode();
      n = 31 * n + this.index;
      return 31 * n + this.elementCount;
   }

   public enum EnumType {
      FLOAT(4, "Float", 5126),
      UBYTE(1, "Unsigned Byte", 5121),
      BYTE(1, "Byte", 5120),
      USHORT(2, "Unsigned Short", 5123),
      SHORT(2, "Short", 5122),
      UINT(4, "Unsigned Int", 5125),
      INT(4, "Int", 5124);

      public int size;
      public String displayName;
      public int glConstant;

      EnumType(int sizeIn, String displayNameIn, int glConstantIn) {
         this.size = sizeIn;
         this.displayName = displayNameIn;
         this.glConstant = glConstantIn;
      }

      public int getSize() {
         return this.size;
      }

      public String getDisplayName() {
         return this.displayName;
      }

      public int getGlConstant() {
         return this.glConstant;
      }
   }

   public enum EnumUsage {
      POSITION("Position"),
      NORMAL("Normal"),
      COLOR("Vertex Color"),
      UV("UV"),
      MATRIX("Bone Matrix"),
      BLEND_WEIGHT("Blend Weight"),
      PADDING("Padding");

      public String displayName;

      EnumUsage(String displayNameIn) {
         this.displayName = displayNameIn;
      }

      public String getDisplayName() {
         return this.displayName;
      }
   }
}
