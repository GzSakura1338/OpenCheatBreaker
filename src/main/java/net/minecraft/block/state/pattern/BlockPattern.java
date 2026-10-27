package net.minecraft.block.state.pattern;

import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;

public class BlockPattern {
   public Predicate<BlockWorldState>[][][] blockMatches;
   public int fingerLength;
   public int thumbLength;
   public int palmLength;

   public BlockPattern(Predicate<BlockWorldState>[][][] predicatesIn) {
      this.blockMatches = predicatesIn;
      this.fingerLength = predicatesIn.length;
      if (this.fingerLength > 0) {
         this.thumbLength = predicatesIn[0].length;
         if (this.thumbLength > 0) {
            this.palmLength = predicatesIn[0][0].length;
         } else {
            this.palmLength = 0;
         }
      } else {
         this.thumbLength = 0;
         this.palmLength = 0;
      }
   }

   public int getThumbLength() {
      return this.thumbLength;
   }

   public int getPalmLength() {
      return this.palmLength;
   }

   public BlockPattern.PatternHelper checkPatternAt(BlockPos pos, EnumFacing finger, EnumFacing thumb, LoadingCache<BlockPos, BlockWorldState> lcache) {
      for (int i = 0; i < this.palmLength; i++) {
         for (int j = 0; j < this.thumbLength; j++) {
            for (int k = 0; k < this.fingerLength; k++) {
               if (!this.blockMatches[k][j][i].apply(lcache.getUnchecked(translateOffset(pos, finger, thumb, i, j, k)))) {
                  return null;
               }
            }
         }
      }

      return new BlockPattern.PatternHelper(pos, finger, thumb, lcache, this.palmLength, this.thumbLength, this.fingerLength);
   }

   public BlockPattern.PatternHelper match(World worldIn, BlockPos pos) {
      LoadingCache createLoadingCache2 = createLoadingCache(worldIn, false);
      int max = Math.max(Math.max(this.palmLength, this.thumbLength), this.fingerLength);

      for (BlockPos blockPos : BlockPos.getAllInBox(pos, pos.add(max - 1, max - 1, max - 1))) {
         for (EnumFacing enumFacing : EnumFacing.values()) {
            for (EnumFacing enumFacing2 : EnumFacing.values()) {
               if (enumFacing2 != enumFacing && enumFacing2 != enumFacing.getOpposite()) {
                  BlockPattern.PatternHelper checkPatternAt = this.checkPatternAt(blockPos, enumFacing, enumFacing2, createLoadingCache2);
                  if (checkPatternAt != null) {
                     return checkPatternAt;
                  }
               }
            }
         }
      }

      return null;
   }

   public static LoadingCache<BlockPos, BlockWorldState> createLoadingCache(World world, boolean flag) {
      return CacheBuilder.newBuilder().build(new BlockPattern.CacheLoader(world, flag));
   }

   public static BlockPos translateOffset(BlockPos pos, EnumFacing finger, EnumFacing thumb, int palmOffset, int thumbOffset, int fingerOffset) {
      if (finger != thumb && finger != thumb.getOpposite()) {
         Vec3i vec3i = new Vec3i(finger.getFrontOffsetX(), finger.getFrontOffsetY(), finger.getFrontOffsetZ());
         Vec3i vec3i2 = new Vec3i(thumb.getFrontOffsetX(), thumb.getFrontOffsetY(), thumb.getFrontOffsetZ());
         Vec3i crossProduct = vec3i.crossProduct(vec3i2);
         return pos.add(
            vec3i2.getX() * -thumbOffset + crossProduct.getX() * palmOffset + vec3i.getX() * fingerOffset,
            vec3i2.getY() * -thumbOffset + crossProduct.getY() * palmOffset + vec3i.getY() * fingerOffset,
            vec3i2.getZ() * -thumbOffset + crossProduct.getZ() * palmOffset + vec3i.getZ() * fingerOffset
         );
      } else {
         throw new IllegalArgumentException("Invalid forwards & up combination");
      }
   }

   static class CacheLoader extends com.google.common.cache.CacheLoader<BlockPos, BlockWorldState> {
      public World world;
      public boolean forceLoad;

      public CacheLoader(World worldIn, boolean flag) {
         this.world = worldIn;
         this.forceLoad = flag;
      }

      public BlockWorldState load(BlockPos blockPos) throws Exception {
         return new BlockWorldState(this.world, blockPos, this.forceLoad);
      }
   }

   public static class PatternHelper {
      public BlockPos pos;
      public EnumFacing finger;
      public EnumFacing thumb;
      public LoadingCache<BlockPos, BlockWorldState> lcache;
      public int width;
      public int height;
      public int depth;

      public PatternHelper(
         BlockPos posIn, EnumFacing fingerIn, EnumFacing thumbIn, LoadingCache<BlockPos, BlockWorldState> lcacheIn, int n, int n2, int n3
      ) {
         this.pos = posIn;
         this.finger = fingerIn;
         this.thumb = thumbIn;
         this.lcache = lcacheIn;
         this.width = n;
         this.height = n2;
         this.depth = n3;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public EnumFacing getFinger() {
         return this.finger;
      }

      public EnumFacing getThumb() {
         return this.thumb;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public BlockWorldState translateOffset(int palmOffset, int thumbOffset, int fingerOffset) {
         return (BlockWorldState)this.lcache
            .getUnchecked(BlockPattern.translateOffset(this.pos, this.getFinger(), this.getThumb(), palmOffset, thumbOffset, fingerOffset));
      }

      @Override
      public String toString() {
         return MoreObjects.toStringHelper(this).add("up", this.thumb).add("forwards", this.finger).add("frontTopLeft", this.pos).toString();
      }
   }
}
