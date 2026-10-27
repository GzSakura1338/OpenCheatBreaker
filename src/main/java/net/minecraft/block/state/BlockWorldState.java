package net.minecraft.block.state;

import com.google.common.base.Predicate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockWorldState {
   public World world;
   public BlockPos pos;
   public boolean forceLoad;
   public IBlockState state;
   public TileEntity tileEntity;
   public boolean tileEntityInitialized;

   public BlockWorldState(World worldIn, BlockPos posIn, boolean flag) {
      this.world = worldIn;
      this.pos = posIn;
      this.forceLoad = flag;
   }

   public IBlockState getBlockState() {
      if (this.state == null && (this.forceLoad || this.world.isBlockLoaded(this.pos))) {
         this.state = this.world.getBlockState(this.pos);
      }

      return this.state;
   }

   public TileEntity getTileEntity() {
      if (this.tileEntity == null && !this.tileEntityInitialized) {
         this.tileEntity = this.world.getTileEntity(this.pos);
         this.tileEntityInitialized = true;
      }

      return this.tileEntity;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public static Predicate<BlockWorldState> hasState(final Predicate<IBlockState> predicatesIn) {
      return new Predicate<BlockWorldState>() {
         public boolean apply(BlockWorldState blockWorldState) {
            return blockWorldState != null && predicatesIn.apply(blockWorldState.getBlockState());
         }
      };
   }
}
