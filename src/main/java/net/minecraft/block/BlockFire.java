package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;

public class BlockFire extends Block {
   public static PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
   public static PropertyBool FLIP = PropertyBool.create("flip");
   public static PropertyBool ALT = PropertyBool.create("alt");
   public static PropertyBool NORTH = PropertyBool.create("north");
   public static PropertyBool EAST = PropertyBool.create("east");
   public static PropertyBool SOUTH = PropertyBool.create("south");
   public static PropertyBool WEST = PropertyBool.create("west");
   public static PropertyInteger UPPER = PropertyInteger.create("upper", 0, 2);
   public Map<Block, Integer> encouragements = Maps.newIdentityHashMap();
   public Map<Block, Integer> flammabilities = Maps.newIdentityHashMap();

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      int n = pos.getX();
      int n2 = pos.getY();
      int n3 = pos.getZ();
      if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && !Blocks.fire.canCatchFire(worldIn, pos.down())) {
         boolean flag = (n + n2 + n3 & 1) == 1;
         boolean flag2 = (n / 2 + n2 / 2 + n3 / 2 & 1) == 1;
         int n4 = 0;
         if (this.canCatchFire(worldIn, pos.up())) {
            n4 = (flag ? 1 : 2);
         }

         return state.withProperty(NORTH, this.canCatchFire(worldIn, pos.north()))
            .withProperty(EAST, this.canCatchFire(worldIn, pos.east()))
            .withProperty(SOUTH, this.canCatchFire(worldIn, pos.south()))
            .withProperty(WEST, this.canCatchFire(worldIn, pos.west()))
            .withProperty(UPPER, Integer.valueOf(n4))
            .withProperty(FLIP, flag2)
            .withProperty(ALT, flag);
      } else {
         return this.getDefaultState();
      }
   }

   public BlockFire() {
      super(Material.fire);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(AGE, 0)
            .withProperty(FLIP, false)
            .withProperty(ALT, false)
            .withProperty(NORTH, false)
            .withProperty(EAST, false)
            .withProperty(SOUTH, false)
            .withProperty(WEST, false)
            .withProperty(UPPER, 0)
      );
      this.setTickRandomly(true);
   }

   public static void init() {
      Blocks.fire.setFireInfo(Blocks.planks, 5, 20);
      Blocks.fire.setFireInfo(Blocks.double_wooden_slab, 5, 20);
      Blocks.fire.setFireInfo(Blocks.wooden_slab, 5, 20);
      Blocks.fire.setFireInfo(Blocks.oak_fence_gate, 5, 20);
      Blocks.fire.setFireInfo(Blocks.spruce_fence_gate, 5, 20);
      Blocks.fire.setFireInfo(Blocks.birch_fence_gate, 5, 20);
      Blocks.fire.setFireInfo(Blocks.jungle_fence_gate, 5, 20);
      Blocks.fire.setFireInfo(Blocks.dark_oak_fence_gate, 5, 20);
      Blocks.fire.setFireInfo(Blocks.acacia_fence_gate, 5, 20);
      Blocks.fire.setFireInfo(Blocks.oak_fence, 5, 20);
      Blocks.fire.setFireInfo(Blocks.spruce_fence, 5, 20);
      Blocks.fire.setFireInfo(Blocks.birch_fence, 5, 20);
      Blocks.fire.setFireInfo(Blocks.jungle_fence, 5, 20);
      Blocks.fire.setFireInfo(Blocks.dark_oak_fence, 5, 20);
      Blocks.fire.setFireInfo(Blocks.acacia_fence, 5, 20);
      Blocks.fire.setFireInfo(Blocks.oak_stairs, 5, 20);
      Blocks.fire.setFireInfo(Blocks.birch_stairs, 5, 20);
      Blocks.fire.setFireInfo(Blocks.spruce_stairs, 5, 20);
      Blocks.fire.setFireInfo(Blocks.jungle_stairs, 5, 20);
      Blocks.fire.setFireInfo(Blocks.log, 5, 5);
      Blocks.fire.setFireInfo(Blocks.log2, 5, 5);
      Blocks.fire.setFireInfo(Blocks.leaves, 30, 60);
      Blocks.fire.setFireInfo(Blocks.leaves2, 30, 60);
      Blocks.fire.setFireInfo(Blocks.bookshelf, 30, 20);
      Blocks.fire.setFireInfo(Blocks.tnt, 15, 100);
      Blocks.fire.setFireInfo(Blocks.tallgrass, 60, 100);
      Blocks.fire.setFireInfo(Blocks.double_plant, 60, 100);
      Blocks.fire.setFireInfo(Blocks.yellow_flower, 60, 100);
      Blocks.fire.setFireInfo(Blocks.red_flower, 60, 100);
      Blocks.fire.setFireInfo(Blocks.deadbush, 60, 100);
      Blocks.fire.setFireInfo(Blocks.wool, 30, 60);
      Blocks.fire.setFireInfo(Blocks.vine, 15, 100);
      Blocks.fire.setFireInfo(Blocks.coal_block, 5, 5);
      Blocks.fire.setFireInfo(Blocks.hay_block, 60, 20);
      Blocks.fire.setFireInfo(Blocks.carpet, 60, 20);
   }

   public void setFireInfo(Block blockIn, int encouragement, int flammability) {
      this.encouragements.put(blockIn, encouragement);
      this.flammabilities.put(blockIn, flammability);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public int quantityDropped(Random random) {
      return 0;
   }

   @Override
   public int tickRate(World worldIn) {
      return 30;
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (worldIn.getGameRules().getBoolean("doFireTick")) {
         if (!this.canPlaceBlockAt(worldIn, pos)) {
            worldIn.setBlockToAir(pos);
         }

         Block block = worldIn.getBlockState(pos.down()).getBlock();
         boolean flag = block == Blocks.netherrack;
         if (worldIn.provider instanceof WorldProviderEnd && block == Blocks.bedrock) {
            flag = true;
         }

         if (!flag && worldIn.isRaining() && this.canDie(worldIn, pos)) {
            worldIn.setBlockToAir(pos);
         } else {
            int value = state.getValue(AGE);
            if (value < 15) {
               state = state.withProperty(AGE, value + rand.nextInt(3) / 2);
               worldIn.setBlockState(pos, state, 4);
            }

            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn) + rand.nextInt(10));
            if (!flag) {
               if (!this.canNeighborCatchFire(worldIn, pos)) {
                  if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) || value > 3) {
                     worldIn.setBlockToAir(pos);
                  }

                  return;
               }

               if (!this.canCatchFire(worldIn, pos.down()) && value == 15 && rand.nextInt(4) == 0) {
                  worldIn.setBlockToAir(pos);
                  return;
               }
            }

            boolean isBlockinHighHumidity = worldIn.isBlockinHighHumidity(pos);
            byte b = 0;
            if (isBlockinHighHumidity) {
               b = -50;
            }

            this.catchOnFire(worldIn, pos.east(), 300 + b, rand, value);
            this.catchOnFire(worldIn, pos.west(), 300 + b, rand, value);
            this.catchOnFire(worldIn, pos.down(), 250 + b, rand, value);
            this.catchOnFire(worldIn, pos.up(), 250 + b, rand, value);
            this.catchOnFire(worldIn, pos.north(), 300 + b, rand, value);
            this.catchOnFire(worldIn, pos.south(), 300 + b, rand, value);

            for (int i = -1; i <= 1; i++) {
               for (int j = -1; j <= 1; j++) {
                  for (int k = -1; k <= 4; k++) {
                     if (i != 0 || k != 0 || j != 0) {
                        int n = 100;
                        if (k > 1) {
                           n += (k - 1) * 100;
                        }

                        BlockPos blockPos = pos.add(i, k, j);
                        int neighborEncouragement = this.getNeighborEncouragement(worldIn, blockPos);
                        if (neighborEncouragement > 0) {
                           int n2 = (neighborEncouragement + 40 + worldIn.getDifficulty().getDifficultyId() * 7) / (value + 30);
                           if (isBlockinHighHumidity) {
                              n2 /= 2;
                           }

                           if (n2 > 0 && rand.nextInt(n) <= n2 && (!worldIn.isRaining() || !this.canDie(worldIn, blockPos))) {
                              int n3 = value + rand.nextInt(5) / 4;
                              if (n3 > 15) {
                                 n3 = 15;
                              }

                              worldIn.setBlockState(blockPos, state.withProperty(AGE, n3), 3);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public boolean canDie(World worldIn, BlockPos pos) {
      return worldIn.isRainingAt(pos)
         || worldIn.isRainingAt(pos.west())
         || worldIn.isRainingAt(pos.east())
         || worldIn.isRainingAt(pos.north())
         || worldIn.isRainingAt(pos.south());
   }

   @Override
   public boolean requiresUpdates() {
      return false;
   }

   public int getFlammability(Block blockIn) {
      Integer integer = this.flammabilities.get(blockIn);
      return integer == null ? 0 : integer;
   }

   public int getEncouragement(Block blockIn) {
      Integer integer = this.encouragements.get(blockIn);
      return integer == null ? 0 : integer;
   }

   public void catchOnFire(World worldIn, BlockPos pos, int chance, Random random, int age) {
      int flammability = this.getFlammability(worldIn.getBlockState(pos).getBlock());
      if (random.nextInt(chance) < flammability) {
         IBlockState blockState = worldIn.getBlockState(pos);
         if (random.nextInt(age + 10) < 5 && !worldIn.isRainingAt(pos)) {
            int n = age + random.nextInt(5) / 4;
            if (n > 15) {
               n = 15;
            }

            worldIn.setBlockState(pos, this.getDefaultState().withProperty(AGE, n), 3);
         } else {
            worldIn.setBlockToAir(pos);
         }

         if (blockState.getBlock() == Blocks.tnt) {
            Blocks.tnt.onBlockDestroyedByPlayer(worldIn, pos, blockState.withProperty(BlockTNT.EXPLODE, true));
         }
      }
   }

   public boolean canNeighborCatchFire(World worldIn, BlockPos pos) {
      for (EnumFacing enumFacing : EnumFacing.values()) {
         if (this.canCatchFire(worldIn, pos.offset(enumFacing))) {
            return true;
         }
      }

      return false;
   }

   public int getNeighborEncouragement(World worldIn, BlockPos pos) {
      if (!worldIn.isAirBlock(pos)) {
         return 0;
      }

      int n = 0;

      for (EnumFacing enumFacing : EnumFacing.values()) {
         n = Math.max(this.getEncouragement(worldIn.getBlockState(pos.offset(enumFacing)).getBlock()), n);
      }

      return n;
   }

   @Override
   public boolean isCollidable() {
      return false;
   }

   public boolean canCatchFire(IBlockAccess worldIn, BlockPos pos) {
      return this.getEncouragement(worldIn.getBlockState(pos).getBlock()) > 0;
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) || this.canNeighborCatchFire(worldIn, pos);
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && !this.canNeighborCatchFire(worldIn, pos)) {
         worldIn.setBlockToAir(pos);
      }
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      if (worldIn.provider.getDimensionId() > 0 || !Blocks.portal.createPortalAt(worldIn, pos)) {
         if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && !this.canNeighborCatchFire(worldIn, pos)) {
            worldIn.setBlockToAir(pos);
         } else {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn) + worldIn.rand.nextInt(10));
         }
      }
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (rand.nextInt(24) == 0) {
         worldIn.playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, "fire.fire", 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
      }

      if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && !Blocks.fire.canCatchFire(worldIn, pos.down())) {
         if (Blocks.fire.canCatchFire(worldIn, pos.west())) {
            for (int i = 0; i < 2; i++) {
               double d = pos.getX() + rand.nextDouble() * 0.1F;
               double d2 = pos.getY() + rand.nextDouble();
               double d3 = pos.getZ() + rand.nextDouble();
               worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d, d2, d3, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.fire.canCatchFire(worldIn, pos.east())) {
            for (int i = 0; i < 2; i++) {
               double d = pos.getX() + 1 - rand.nextDouble() * 0.1F;
               double d2 = pos.getY() + rand.nextDouble();
               double d3 = pos.getZ() + rand.nextDouble();
               worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d, d2, d3, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.fire.canCatchFire(worldIn, pos.north())) {
            for (int i = 0; i < 2; i++) {
               double d = pos.getX() + rand.nextDouble();
               double d2 = pos.getY() + rand.nextDouble();
               double d3 = pos.getZ() + rand.nextDouble() * 0.1F;
               worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d, d2, d3, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.fire.canCatchFire(worldIn, pos.south())) {
            for (int i = 0; i < 2; i++) {
               double d = pos.getX() + rand.nextDouble();
               double d2 = pos.getY() + rand.nextDouble();
               double d3 = pos.getZ() + 1 - rand.nextDouble() * 0.1F;
               worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d, d2, d3, 0.0, 0.0, 0.0);
            }
         }

         if (Blocks.fire.canCatchFire(worldIn, pos.up())) {
            for (int i = 0; i < 2; i++) {
               double d = pos.getX() + rand.nextDouble();
               double d2 = pos.getY() + 1 - rand.nextDouble() * 0.1F;
               double d3 = pos.getZ() + rand.nextDouble();
               worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d, d2, d3, 0.0, 0.0, 0.0);
            }
         }
      } else {
         for (int i = 0; i < 3; i++) {
            double d = pos.getX() + rand.nextDouble();
            double d2 = pos.getY() + rand.nextDouble() * 0.5 + 0.5;
            double d3 = pos.getZ() + rand.nextDouble();
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d, d2, d3, 0.0, 0.0, 0.0);
         }
      }
   }

   @Override
   public MapColor getMapColor(IBlockState state) {
      return MapColor.tntColor;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(AGE, meta);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(AGE);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, AGE, NORTH, EAST, SOUTH, WEST, UPPER, FLIP, ALT);
   }
}
