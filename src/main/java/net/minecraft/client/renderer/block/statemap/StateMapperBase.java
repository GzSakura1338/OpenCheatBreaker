package net.minecraft.client.renderer.block.statemap;

import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

public abstract class StateMapperBase implements IStateMapper {
   public Map<IBlockState, ModelResourceLocation> mapStateModelLocations = Maps.newLinkedHashMap();

   public String getPropertyString(Map<IProperty, Comparable> map) {
      StringBuilder sb = new StringBuilder();

      for (Entry entry : map.entrySet()) {
         if (sb.length() != 0) {
            sb.append(",");
         }

         IProperty key = (IProperty)entry.getKey();
         Comparable value = (Comparable)entry.getValue();
         sb.append(key.getName());
         sb.append("=");
         sb.append(key.getName(value));
      }

      if (sb.length() == 0) {
         sb.append("normal");
      }

      return sb.toString();
   }

   @Override
   public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn) {
      UnmodifiableIterator unmodifiableIterator = blockIn.getBlockState().getValidStates().iterator();

      while (unmodifiableIterator.hasNext()) {
         IBlockState blockState = (IBlockState)unmodifiableIterator.next();
         this.mapStateModelLocations.put(blockState, this.getModelResourceLocation(blockState));
      }

      return this.mapStateModelLocations;
   }

   public abstract ModelResourceLocation getModelResourceLocation(IBlockState blockState);
}
