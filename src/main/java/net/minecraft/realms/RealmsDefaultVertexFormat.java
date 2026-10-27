package net.minecraft.realms;

import com.cheatbreaker.client.render.VertexElementWrapper;
import com.cheatbreaker.client.render.VertexFormatWrapper;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;

public class RealmsDefaultVertexFormat {
   public static VertexFormatWrapper BLOCK = new VertexFormatWrapper(new VertexFormat());
   public static VertexFormatWrapper BLOCK_NORMALS = new VertexFormatWrapper(new VertexFormat());
   public static VertexFormatWrapper ENTITY = new VertexFormatWrapper(new VertexFormat());
   public static VertexFormatWrapper PARTICLE = new VertexFormatWrapper(new VertexFormat());
   public static VertexFormatWrapper POSITION = new VertexFormatWrapper(new VertexFormat());
   public static VertexFormatWrapper POSITION_COLOR = new VertexFormatWrapper(new VertexFormat());
   public static VertexFormatWrapper POSITION_TEX = new VertexFormatWrapper(new VertexFormat());
   public static VertexFormatWrapper POSITION_NORMAL = new VertexFormatWrapper(new VertexFormat());
   public static VertexFormatWrapper POSITION_TEX_COLOR = new VertexFormatWrapper(new VertexFormat());
   public static VertexFormatWrapper POSITION_TEX_NORMAL = new VertexFormatWrapper(new VertexFormat());
   public static VertexFormatWrapper POSITION_TEX2_COLOR = new VertexFormatWrapper(new VertexFormat());
   public static VertexFormatWrapper POSITION_TEX_COLOR_NORMAL = new VertexFormatWrapper(new VertexFormat());
   public static VertexElementWrapper ELEMENT_POSITION = new VertexElementWrapper(
      new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.POSITION, 3)
   );
   public static VertexElementWrapper ELEMENT_COLOR = new VertexElementWrapper(
      new VertexFormatElement(0, EnumType.UBYTE, EnumUsage.COLOR, 4)
   );
   public static VertexElementWrapper ELEMENT_UV0 = new VertexElementWrapper(
      new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.UV, 2)
   );
   public static VertexElementWrapper ELEMENT_UV1 = new VertexElementWrapper(
      new VertexFormatElement(1, EnumType.SHORT, EnumUsage.UV, 2)
   );
   public static VertexElementWrapper ELEMENT_NORMAL = new VertexElementWrapper(
      new VertexFormatElement(0, EnumType.BYTE, EnumUsage.NORMAL, 3)
   );
   public static VertexElementWrapper ELEMENT_PADDING = new VertexElementWrapper(
      new VertexFormatElement(0, EnumType.BYTE, EnumUsage.PADDING, 1)
   );

   static {
      BLOCK.addElement(ELEMENT_POSITION);
      BLOCK.addElement(ELEMENT_COLOR);
      BLOCK.addElement(ELEMENT_UV0);
      BLOCK.addElement(ELEMENT_UV1);
      BLOCK_NORMALS.addElement(ELEMENT_POSITION);
      BLOCK_NORMALS.addElement(ELEMENT_COLOR);
      BLOCK_NORMALS.addElement(ELEMENT_UV0);
      BLOCK_NORMALS.addElement(ELEMENT_NORMAL);
      BLOCK_NORMALS.addElement(ELEMENT_PADDING);
      ENTITY.addElement(ELEMENT_POSITION);
      ENTITY.addElement(ELEMENT_UV0);
      ENTITY.addElement(ELEMENT_NORMAL);
      ENTITY.addElement(ELEMENT_PADDING);
      PARTICLE.addElement(ELEMENT_POSITION);
      PARTICLE.addElement(ELEMENT_UV0);
      PARTICLE.addElement(ELEMENT_COLOR);
      PARTICLE.addElement(ELEMENT_UV1);
      POSITION.addElement(ELEMENT_POSITION);
      POSITION_COLOR.addElement(ELEMENT_POSITION);
      POSITION_COLOR.addElement(ELEMENT_COLOR);
      POSITION_TEX.addElement(ELEMENT_POSITION);
      POSITION_TEX.addElement(ELEMENT_UV0);
      POSITION_NORMAL.addElement(ELEMENT_POSITION);
      POSITION_NORMAL.addElement(ELEMENT_NORMAL);
      POSITION_NORMAL.addElement(ELEMENT_PADDING);
      POSITION_TEX_COLOR.addElement(ELEMENT_POSITION);
      POSITION_TEX_COLOR.addElement(ELEMENT_UV0);
      POSITION_TEX_COLOR.addElement(ELEMENT_COLOR);
      POSITION_TEX_NORMAL.addElement(ELEMENT_POSITION);
      POSITION_TEX_NORMAL.addElement(ELEMENT_UV0);
      POSITION_TEX_NORMAL.addElement(ELEMENT_NORMAL);
      POSITION_TEX_NORMAL.addElement(ELEMENT_PADDING);
      POSITION_TEX2_COLOR.addElement(ELEMENT_POSITION);
      POSITION_TEX2_COLOR.addElement(ELEMENT_UV0);
      POSITION_TEX2_COLOR.addElement(ELEMENT_UV1);
      POSITION_TEX2_COLOR.addElement(ELEMENT_COLOR);
      POSITION_TEX_COLOR_NORMAL.addElement(ELEMENT_POSITION);
      POSITION_TEX_COLOR_NORMAL.addElement(ELEMENT_UV0);
      POSITION_TEX_COLOR_NORMAL.addElement(ELEMENT_COLOR);
      POSITION_TEX_COLOR_NORMAL.addElement(ELEMENT_NORMAL);
      POSITION_TEX_COLOR_NORMAL.addElement(ELEMENT_PADDING);
   }
}
