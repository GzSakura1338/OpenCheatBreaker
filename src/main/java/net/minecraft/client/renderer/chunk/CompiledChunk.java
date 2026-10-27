package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.List;
import net.minecraft.client.renderer.WorldRenderer.State;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;

public class CompiledChunk {
   public static CompiledChunk DUMMY = new CompiledChunk() {
      @Override
      public void setLayerUsed(EnumWorldBlockLayer layer) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void setLayerStarted(EnumWorldBlockLayer layer) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean isVisible(EnumFacing facing, EnumFacing facing2) {
         return false;
      }

      @Override
      public void setAnimatedSprites(EnumWorldBlockLayer enumWorldBlockLayer, BitSet bitSet) {
         throw new UnsupportedOperationException();
      }
   };
   public boolean[] layersUsed = new boolean[RenderChunk.ENUM_WORLD_BLOCK_LAYERS.length];
   public boolean[] layersStarted = new boolean[RenderChunk.ENUM_WORLD_BLOCK_LAYERS.length];
   public boolean empty = true;
   public List<TileEntity> tileEntities = Lists.newArrayList();
   public SetVisibility setVisibility = new SetVisibility();
   public State state;
   public BitSet[] animatedSprites = new BitSet[RenderChunk.ENUM_WORLD_BLOCK_LAYERS.length];

   public boolean isEmpty() {
      return this.empty;
   }

   public void setLayerUsed(EnumWorldBlockLayer layer) {
      this.empty = false;
      this.layersUsed[layer.ordinal()] = true;
   }

   public boolean isLayerEmpty(EnumWorldBlockLayer layer) {
      return !this.layersUsed[layer.ordinal()];
   }

   public void setLayerStarted(EnumWorldBlockLayer layer) {
      this.layersStarted[layer.ordinal()] = true;
   }

   public boolean isLayerStarted(EnumWorldBlockLayer layer) {
      return this.layersStarted[layer.ordinal()];
   }

   public List<TileEntity> getTileEntities() {
      return this.tileEntities;
   }

   public void addTileEntity(TileEntity tileEntityIn) {
      this.tileEntities.add(tileEntityIn);
   }

   public boolean isVisible(EnumFacing facing, EnumFacing facing2) {
      return this.setVisibility.isVisible(facing, facing2);
   }

   public void setVisibility(SetVisibility visibility) {
      this.setVisibility = visibility;
   }

   public State getState() {
      return this.state;
   }

   public void setState(State stateIn) {
      this.state = stateIn;
   }

   public BitSet getAnimatedSprites(EnumWorldBlockLayer enumWorldBlockLayer) {
      return this.animatedSprites[enumWorldBlockLayer.ordinal()];
   }

   public void setAnimatedSprites(EnumWorldBlockLayer enumWorldBlockLayer, BitSet bitSet) {
      this.animatedSprites[enumWorldBlockLayer.ordinal()] = bitSet;
   }
}
