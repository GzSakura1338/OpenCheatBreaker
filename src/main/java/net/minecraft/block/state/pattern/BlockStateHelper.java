package net.minecraft.block.state.pattern;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;

public class BlockStateHelper implements Predicate<IBlockState> {
   public Map<IProperty, Predicate> propertyPredicates = Maps.newHashMap();
   public BlockState blockstate;

   public BlockStateHelper(BlockState blockStateIn) {
      this.blockstate = blockStateIn;
   }

   public static BlockStateHelper forBlock(Block blockIn) {
      return new BlockStateHelper(blockIn.getBlockState());
   }

   public boolean apply(IBlockState blockState) {
      if (blockState != null && blockState.getBlock().equals(this.blockstate.getBlock())) {
         for (Entry entry : this.propertyPredicates.entrySet()) {
            Comparable value = blockState.getValue((IProperty)entry.getKey());
            if (!((Predicate)entry.getValue()).apply(value)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public <V extends Comparable<V>> BlockStateHelper where(IProperty<V> property, Predicate<? extends V> is) {
      if (!this.blockstate.getProperties().contains(property)) {
         throw new IllegalArgumentException(this.blockstate + " cannot support property " + property);
      }

      this.propertyPredicates.put(property, is);
      return this;
   }
}
