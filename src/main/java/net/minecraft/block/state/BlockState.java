package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.Cartesian;
import net.minecraft.util.MapPopulator;

public class BlockState {
   public static Joiner COMMA_JOINER = Joiner.on(", ");
   public static Function<IProperty, String> GET_NAME_FUNC = new Function<IProperty, String>() {
      public String apply(IProperty property) {
         return property == null ? "<NULL>" : property.getName();
      }
   };
   public Block block;
   public ImmutableList<IProperty> properties;
   public ImmutableList<IBlockState> validStates;

   public BlockState(Block blockIn, IProperty... properties) {
      this.block = blockIn;
      Arrays.sort(properties, new Comparator<IProperty>() {
         public int compare(IProperty property, IProperty property2) {
            return property.getName().compareTo(property2.getName());
         }
      });
      this.properties = ImmutableList.copyOf(properties);
      LinkedHashMap newLinkedHashMap = Maps.newLinkedHashMap();
      ArrayList newArrayList = Lists.newArrayList();

      for (List list : Cartesian.cartesianProduct(this.getAllowedValues())) {
         Map createMap = MapPopulator.createMap(this.properties, list);
         BlockState.StateImplementation stateImplementation = new BlockState.StateImplementation(blockIn, ImmutableMap.copyOf(createMap));
         newLinkedHashMap.put(createMap, stateImplementation);
         newArrayList.add(stateImplementation);
      }

      for (BlockState.StateImplementation stateImplementation : (Iterable<BlockState.StateImplementation>)newArrayList) {
         stateImplementation.buildPropertyValueTable(newLinkedHashMap);
      }

      this.validStates = ImmutableList.copyOf(newArrayList);
   }

   public ImmutableList<IBlockState> getValidStates() {
      return this.validStates;
   }

   public List<Iterable<Comparable>> getAllowedValues() {
      ArrayList newArrayList = Lists.newArrayList();

      for (int i = 0; i < this.properties.size(); i++) {
         newArrayList.add(((IProperty)this.properties.get(i)).getAllowedValues());
      }

      return newArrayList;
   }

   public IBlockState getBaseState() {
      return (IBlockState)this.validStates.get(0);
   }

   public Block getBlock() {
      return this.block;
   }

   public Collection<IProperty> getProperties() {
      return this.properties;
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this)
         .add("block", Block.blockRegistry.getNameForObject(this.block))
         .add("properties", Iterables.transform(this.properties, GET_NAME_FUNC))
         .toString();
   }

   static class StateImplementation extends BlockStateBase {
      public Block block;
      public ImmutableMap<IProperty, Comparable> properties;
      public ImmutableTable<IProperty, Comparable, IBlockState> propertyValueTable;

      public StateImplementation(Block blockIn, ImmutableMap<IProperty, Comparable> propertiesIn) {
         this.block = blockIn;
         this.properties = propertiesIn;
      }

      @Override
      public Collection<IProperty> getPropertyNames() {
         return Collections.unmodifiableCollection(this.properties.keySet());
      }

      @Override
      public <T extends Comparable<T>> T getValue(IProperty<T> property) {
         if (!this.properties.containsKey(property)) {
            throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.block.getBlockState());
         } else {
            return (T)property.getValueClass().cast(this.properties.get(property));
         }
      }

      @Override
      public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
         if (!this.properties.containsKey(property)) {
            throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.block.getBlockState());
         } else if (!property.getAllowedValues().contains(value)) {
            throw new IllegalArgumentException(
               "Cannot set property "
                  + property
                  + " to "
                  + value
                  + " on block "
                  + Block.blockRegistry.getNameForObject(this.block)
                  + ", it is not an allowed value"
            );
         } else {
            return this.properties.get(property) == value ? this : (IBlockState)this.propertyValueTable.get(property, value);
         }
      }

      @Override
      public ImmutableMap<IProperty, Comparable> getProperties() {
         return this.properties;
      }

      @Override
      public Block getBlock() {
         return this.block;
      }

      @Override
      public boolean equals(Object obj) {
         return this == obj;
      }

      @Override
      public int hashCode() {
         return this.properties.hashCode();
      }

      public void buildPropertyValueTable(Map<Map<IProperty, Comparable>, BlockState.StateImplementation> map) {
         if (this.propertyValueTable != null) {
            throw new IllegalStateException();
         }

         HashBasedTable hashBasedTable = HashBasedTable.create();
         UnmodifiableIterator unmodifiableIterator = this.properties.keySet().iterator();

         while (unmodifiableIterator.hasNext()) {
            IProperty property = (IProperty)unmodifiableIterator.next();

            for (Comparable comparable : (Iterable<Comparable>)property.getAllowedValues()) {
               if (comparable != this.properties.get(property)) {
                  hashBasedTable.put(property, comparable, map.get(this.getPropertiesWithValue(property, comparable)));
               }
            }
         }

         this.propertyValueTable = ImmutableTable.copyOf(hashBasedTable);
      }

      public Map<IProperty, Comparable> getPropertiesWithValue(IProperty property, Comparable value) {
         HashMap newHashMap = Maps.newHashMap(this.properties);
         newHashMap.put(property, value);
         return newHashMap;
      }
   }
}
