package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockNewLog extends BlockLog {
   public static PropertyEnum<BlockPlanks.EnumType> VARIANT = PropertyEnum.create(
      "variant", BlockPlanks.EnumType.class, new Predicate<BlockPlanks.EnumType>() {
         public boolean apply(BlockPlanks.EnumType enumType) {
            return enumType.getMetadata() >= 4;
         }
      }
   );

   public BlockNewLog() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockPlanks.EnumType.ACACIA).withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
   }

   @Override
   public MapColor getMapColor(IBlockState state) {
      BlockPlanks.EnumType value = state.getValue(VARIANT);
      switch (state.getValue(LOG_AXIS)) {
         case X:
         case Z:
         case NONE:
         default:
            switch (value) {
               case ACACIA:
               default:
                  return MapColor.stoneColor;
               case DARK_OAK:
                  return BlockPlanks.EnumType.DARK_OAK.getMapColor();
            }
         case Y:
            return value.getMapColor();
      }
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.ACACIA.getMetadata() - 4));
      list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.DARK_OAK.getMetadata() - 4));
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      IBlockState withProperty = this.getDefaultState().withProperty(VARIANT, BlockPlanks.EnumType.byMetadata((meta & 3) + 4));
      switch (meta & 12) {
         case 0:
            withProperty = withProperty.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
            break;
         case 4:
            withProperty = withProperty.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
            break;
         case 8:
            withProperty = withProperty.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
            break;
         default:
            withProperty = withProperty.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
      }

      return withProperty;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int n = 0;
      n |= state.getValue(VARIANT).getMetadata() - 4;
      switch (state.getValue(LOG_AXIS)) {
         case X:
            n |= 4;
            break;
         case Z:
            n |= 8;
            break;
         case NONE:
            n |= 12;
      }

      return n;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, VARIANT, LOG_AXIS);
   }

   @Override
   public ItemStack createStackedBlock(IBlockState state) {
      return new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(VARIANT).getMetadata() - 4);
   }

   @Override
   public int damageDropped(IBlockState state) {
      return state.getValue(VARIANT).getMetadata() - 4;
   }
}
