package net.optifine.shaders;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;

public class SVertexFormat {
   public static final int vertexSizeBlock = 14;
   public static final int offsetMidTexCoord = 8;
   public static final int offsetTangent = 10;
   public static final int offsetEntity = 12;
   public static VertexFormat defVertexFormatTextured = makeDefVertexFormatTextured();

   public static VertexFormat makeDefVertexFormatBlock() {
      VertexFormat vertexFormat = new VertexFormat();
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.POSITION, 3));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.UBYTE, EnumUsage.COLOR, 4));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.UV, 2));
      vertexFormat.addElement(new VertexFormatElement(1, EnumType.SHORT, EnumUsage.UV, 2));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.BYTE, EnumUsage.NORMAL, 3));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.BYTE, EnumUsage.PADDING, 1));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.PADDING, 2));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.SHORT, EnumUsage.PADDING, 4));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.SHORT, EnumUsage.PADDING, 4));
      return vertexFormat;
   }

   public static VertexFormat makeDefVertexFormatItem() {
      VertexFormat vertexFormat = new VertexFormat();
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.POSITION, 3));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.UBYTE, EnumUsage.COLOR, 4));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.UV, 2));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.SHORT, EnumUsage.PADDING, 2));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.BYTE, EnumUsage.NORMAL, 3));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.BYTE, EnumUsage.PADDING, 1));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.PADDING, 2));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.SHORT, EnumUsage.PADDING, 4));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.SHORT, EnumUsage.PADDING, 4));
      return vertexFormat;
   }

   public static VertexFormat makeDefVertexFormatTextured() {
      VertexFormat vertexFormat = new VertexFormat();
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.POSITION, 3));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.UBYTE, EnumUsage.PADDING, 4));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.UV, 2));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.SHORT, EnumUsage.PADDING, 2));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.BYTE, EnumUsage.NORMAL, 3));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.BYTE, EnumUsage.PADDING, 1));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.PADDING, 2));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.SHORT, EnumUsage.PADDING, 4));
      vertexFormat.addElement(new VertexFormatElement(0, EnumType.SHORT, EnumUsage.PADDING, 4));
      return vertexFormat;
   }

   public static void setDefBakedFormat(VertexFormat vertexFormat) {
      if (vertexFormat != null) {
         vertexFormat.clear();
         vertexFormat.addElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.POSITION, 3));
         vertexFormat.addElement(new VertexFormatElement(0, EnumType.UBYTE, EnumUsage.COLOR, 4));
         vertexFormat.addElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.UV, 2));
         vertexFormat.addElement(new VertexFormatElement(0, EnumType.SHORT, EnumUsage.PADDING, 2));
         vertexFormat.addElement(new VertexFormatElement(0, EnumType.BYTE, EnumUsage.NORMAL, 3));
         vertexFormat.addElement(new VertexFormatElement(0, EnumType.BYTE, EnumUsage.PADDING, 1));
         vertexFormat.addElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.PADDING, 2));
         vertexFormat.addElement(new VertexFormatElement(0, EnumType.SHORT, EnumUsage.PADDING, 4));
         vertexFormat.addElement(new VertexFormatElement(0, EnumType.SHORT, EnumUsage.PADDING, 4));
      }
   }

   public static VertexFormat duplicate(VertexFormat vertexFormat) {
      if (vertexFormat == null) {
         return null;
      }

      VertexFormat vertexFormat2 = new VertexFormat();
      copy(vertexFormat, vertexFormat2);
      return vertexFormat2;
   }

   public static void copy(VertexFormat vertexFormat, VertexFormat vertexFormat2) {
      if (vertexFormat != null && vertexFormat2 != null) {
         vertexFormat2.clear();

         for (int i = 0; i < vertexFormat.getElementCount(); i++) {
            vertexFormat2.addElement(vertexFormat.getElement(i));
         }
      }
   }
}
