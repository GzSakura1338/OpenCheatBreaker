package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumFacing.Plane;

public abstract class BlockDirectional extends Block {
   public static PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);

   public BlockDirectional(Material materialIn) {
      super(materialIn);
   }

   public BlockDirectional(Material material, MapColor mapColor) {
      super(material, mapColor);
   }
}
