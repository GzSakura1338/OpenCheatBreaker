package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing.Axis;

public abstract class BlockRotatedPillar extends Block {
   public static PropertyEnum<Axis> AXIS = PropertyEnum.create("axis", Axis.class);

   public BlockRotatedPillar(Material materialIn) {
      super(materialIn, materialIn.getMaterialMapColor());
   }

   public BlockRotatedPillar(Material material, MapColor mapColor) {
      super(material, mapColor);
   }
}
