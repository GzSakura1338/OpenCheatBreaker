package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockPressurePlate extends BlockBasePressurePlate {
   public static PropertyBool POWERED = PropertyBool.create("powered");
   public BlockPressurePlate.Sensitivity sensitivity;

   public BlockPressurePlate(Material materialIn, BlockPressurePlate.Sensitivity sensitivityIn) {
      super(materialIn);
      this.setDefaultState(this.blockState.getBaseState().withProperty(POWERED, false));
      this.sensitivity = sensitivityIn;
   }

   @Override
   public int getRedstoneStrength(IBlockState state) {
      return state.getValue(POWERED) ? 15 : 0;
   }

   @Override
   public IBlockState setRedstoneStrength(IBlockState state, int strength) {
      return state.withProperty(POWERED, strength > 0);
   }

   @Override
   public int computeRedstoneStrength(World worldIn, BlockPos pos) {
      AxisAlignedBB sensitiveAABB = this.getSensitiveAABB(pos);
      List list;
      switch (this.sensitivity) {
         case EVERYTHING:
            list = worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, sensitiveAABB);
            break;
         case MOBS:
            list = worldIn.getEntitiesWithinAABB(EntityLivingBase.class, sensitiveAABB);
            break;
         default:
            return 0;
      }

      if (!list.isEmpty()) {
         for (Entity entity : (Iterable<Entity>)list) {
            if (!entity.doesEntityNotTriggerPressurePlate()) {
               return 15;
            }
         }
      }

      return 0;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(POWERED, meta == 1);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(POWERED) ? 1 : 0;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, POWERED);
   }

   public enum Sensitivity {
      EVERYTHING,
      MOBS;
   }
}
