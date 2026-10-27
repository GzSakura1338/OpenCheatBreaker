package net.minecraft.client.renderer.block.statemap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class StateMap extends StateMapperBase {
   public IProperty<?> name;
   public String suffix;
   public List<IProperty<?>> ignored;

   public StateMap(IProperty<?> name, String suffix, List<IProperty<?>> ignored) {
      this.name = name;
      this.suffix = suffix;
      this.ignored = ignored;
   }

   @Override
   public ModelResourceLocation getModelResourceLocation(IBlockState state) {
      LinkedHashMap newLinkedHashMap = Maps.newLinkedHashMap(state.getProperties());
      String str;
      if (this.name == null) {
         str = Block.blockRegistry.getNameForObject(state.getBlock()).toString();
      } else {
         str = ((IProperty)this.name).getName((Comparable)newLinkedHashMap.remove(this.name));
      }

      if (this.suffix != null) {
         str = str + this.suffix;
      }

      for (IProperty property : this.ignored) {
         newLinkedHashMap.remove(property);
      }

      return new ModelResourceLocation(str, this.getPropertyString(newLinkedHashMap));
   }

   public static class Builder {
      public IProperty<?> name;
      public String suffix;
      public List<IProperty<?>> ignored = Lists.newArrayList();

      public StateMap.Builder withName(IProperty<?> builderPropertyIn) {
         this.name = builderPropertyIn;
         return this;
      }

      public StateMap.Builder withSuffix(String builderSuffixIn) {
         this.suffix = builderSuffixIn;
         return this;
      }

      public StateMap.Builder ignore(IProperty<?>... properties) {
         Collections.addAll(this.ignored, properties);
         return this;
      }

      public StateMap build() {
         return new StateMap(this.name, this.suffix, this.ignored);
      }
   }
}
