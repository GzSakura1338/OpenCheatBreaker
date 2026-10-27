package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.ResourceLocation;

public abstract class BlockStateBase implements IBlockState {
   public static Joiner COMMA_JOINER = Joiner.on(',');
   public static Function<Entry<IProperty, Comparable>, String> MAP_ENTRY_TO_STRING = new Function<Entry<IProperty, Comparable>, String>() {
      public String apply(Entry<IProperty, Comparable> entry) {
         if (entry == null) {
            return "<NULL>";
         }

         IProperty key = (IProperty)entry.getKey();
         return key.getName() + "=" + key.getName((Comparable)entry.getValue());
      }
   };
   public int blockId = -1;
   public int blockStateId = -1;
   public int metadata = -1;
   public ResourceLocation blockLocation = null;

   public int getBlockId() {
      if (this.blockId < 0) {
         this.blockId = Block.getIdFromBlock(this.getBlock());
      }

      return this.blockId;
   }

   public int getBlockStateId() {
      if (this.blockStateId < 0) {
         this.blockStateId = Block.getStateId(this);
      }

      return this.blockStateId;
   }

   public int getMetadata() {
      if (this.metadata < 0) {
         this.metadata = this.getBlock().getMetaFromState(this);
      }

      return this.metadata;
   }

   public ResourceLocation getBlockLocation() {
      if (this.blockLocation == null) {
         this.blockLocation = Block.blockRegistry.getNameForObject(this.getBlock());
      }

      return this.blockLocation;
   }

   public ImmutableTable<IProperty<?>, Comparable<?>, IBlockState> getPropertyValueTable() {
      return null;
   }

   @Override
   public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
      return this.withProperty(property, cyclePropertyValue(property.getAllowedValues(), this.getValue(property)));
   }

   public static <T> T cyclePropertyValue(Collection<T> values, T currentValue) {
      Iterator iterator = values.iterator();

      while (iterator.hasNext()) {
         if (iterator.next().equals(currentValue)) {
            if (iterator.hasNext()) {
               return (T)iterator.next();
            }

            return (T)values.iterator().next();
         }
      }

      return (T)iterator.next();
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(Block.blockRegistry.getNameForObject(this.getBlock()));
      if (!this.getProperties().isEmpty()) {
         sb.append("[");
         COMMA_JOINER.appendTo(sb, Iterables.transform(this.getProperties().entrySet(), MAP_ENTRY_TO_STRING));
         sb.append("]");
      }

      return sb.toString();
   }
}
