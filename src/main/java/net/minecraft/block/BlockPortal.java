package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockPattern.PatternHelper;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortal extends BlockBreakable {
   public static PropertyEnum<Axis> AXIS = PropertyEnum.create("axis", Axis.class, Axis.X, Axis.Z);

   public BlockPortal() {
      super(Material.portal, false);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, Axis.X));
      this.setTickRandomly(true);
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      super.updateTick(worldIn, pos, state, rand);
      if (worldIn.provider.isSurfaceWorld()
         && worldIn.getGameRules().getBoolean("doMobSpawning")
         && rand.nextInt(2000) < worldIn.getDifficulty().getDifficultyId()) {
         int n = pos.getY();
         BlockPos pos2 = pos;

         while (!World.doesBlockHaveSolidTopSurface(worldIn, pos2) && pos2.getY() > 0) {
            pos2 = pos2.down();
         }

         if (n > 0 && !worldIn.getBlockState(pos2.up()).getBlock().isNormalCube()) {
            Entity spawnCreature = ItemMonsterPlacer.spawnCreature(worldIn, 57, pos2.getX() + 0.5, pos2.getY() + 1.1, pos2.getZ() + 0.5);
            if (spawnCreature != null) {
               spawnCreature.timeUntilPortal = spawnCreature.getPortalCooldown();
            }
         }
      }
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      Axis value = worldIn.getBlockState(pos).getValue(AXIS);
      float f = 0.125F;
      float f2 = 0.125F;
      if (value == Axis.X) {
         f = 0.5F;
      }

      if (value == Axis.Z) {
         f2 = 0.5F;
      }

      this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f2, 0.5F + f, 1.0F, 0.5F + f2);
   }

   public static int getMetaForAxis(Axis axis) {
      return axis == Axis.X ? 1 : (axis == Axis.Z ? 2 : 0);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   public boolean createPortalAt(World worldIn, BlockPos blockPos) {
      BlockPortal.Size size = new BlockPortal.Size(worldIn, blockPos, Axis.X);
      if (size.isValid() && size.foundPortalBlocks == 0) {
         size.createPortal();
         return true;
      } else {
         BlockPortal.Size size2 = new BlockPortal.Size(worldIn, blockPos, Axis.Z);
         if (size2.isValid() && size2.foundPortalBlocks == 0) {
            size2.createPortal();
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      Axis value = state.getValue(AXIS);
      if (value == Axis.X) {
         BlockPortal.Size size = new BlockPortal.Size(worldIn, pos, Axis.X);
         if (!size.isValid() || size.foundPortalBlocks < size.width * size.height) {
            worldIn.setBlockState(pos, Blocks.air.getDefaultState());
         }
      } else if (value == Axis.Z) {
         BlockPortal.Size size = new BlockPortal.Size(worldIn, pos, Axis.Z);
         if (!size.isValid() || size.foundPortalBlocks < size.width * size.height) {
            worldIn.setBlockState(pos, Blocks.air.getDefaultState());
         }
      }
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      Axis axis = null;
      IBlockState blockState = worldIn.getBlockState(pos);
      if (worldIn.getBlockState(pos).getBlock() == this) {
         axis = blockState.getValue(AXIS);
         if (axis == null) {
            return false;
         }

         if (axis == Axis.Z && side != EnumFacing.EAST && side != EnumFacing.WEST) {
            return false;
         }

         if (axis == Axis.X && side != EnumFacing.SOUTH && side != EnumFacing.NORTH) {
            return false;
         }
      }

      boolean flag = worldIn.getBlockState(pos.west()).getBlock() == this && worldIn.getBlockState(pos.west(2)).getBlock() != this;
      boolean flag2 = worldIn.getBlockState(pos.east()).getBlock() == this && worldIn.getBlockState(pos.east(2)).getBlock() != this;
      boolean flag3 = worldIn.getBlockState(pos.north()).getBlock() == this && worldIn.getBlockState(pos.north(2)).getBlock() != this;
      boolean flag4 = worldIn.getBlockState(pos.south()).getBlock() == this && worldIn.getBlockState(pos.south(2)).getBlock() != this;
      boolean flag5 = flag || flag2 || axis == Axis.X;
      boolean flag6 = flag3 || flag4 || axis == Axis.Z;
      return flag5 && side == EnumFacing.WEST
         ? true
         : (flag5 && side == EnumFacing.EAST ? true : (flag6 && side == EnumFacing.NORTH ? true : flag6 && side == EnumFacing.SOUTH));
   }

   @Override
   public int quantityDropped(Random random) {
      return 0;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.TRANSLUCENT;
   }

   @Override
   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      if (entityIn.ridingEntity == null && entityIn.riddenByEntity == null) {
         entityIn.setPortal(pos);
      }
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (rand.nextInt(100) == 0) {
         worldIn.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, "portal.portal", 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
      }

      for (int i = 0; i < 4; i++) {
         double d = pos.getX() + rand.nextFloat();
         double d2 = pos.getY() + rand.nextFloat();
         double d3 = pos.getZ() + rand.nextFloat();
         double d4 = (rand.nextFloat() - 0.5) * 0.5;
         double d5 = (rand.nextFloat() - 0.5) * 0.5;
         double d6 = (rand.nextFloat() - 0.5) * 0.5;
         int n = rand.nextInt(2) * 2 - 1;
         if (worldIn.getBlockState(pos.west()).getBlock() != this && worldIn.getBlockState(pos.east()).getBlock() != this) {
            d = pos.getX() + 0.5 + 0.25 * n;
            d4 = rand.nextFloat() * 2.0F * n;
         } else {
            d3 = pos.getZ() + 0.5 + 0.25 * n;
            d6 = rand.nextFloat() * 2.0F * n;
         }

         worldIn.spawnParticle(EnumParticleTypes.PORTAL, d, d2, d3, d4, d5, d6);
      }
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return null;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(AXIS, (meta & 3) == 2 ? Axis.Z : Axis.X);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return getMetaForAxis(state.getValue(AXIS));
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, AXIS);
   }

   public PatternHelper findPortal(World world, BlockPos blockPos) {
      Axis axis = Axis.Z;
      BlockPortal.Size size = new BlockPortal.Size(world, blockPos, Axis.X);
      LoadingCache createLoadingCache = BlockPattern.createLoadingCache(world, true);
      if (!size.isValid()) {
         axis = Axis.X;
         size = new BlockPortal.Size(world, blockPos, Axis.Z);
      }

      if (!size.isValid()) {
         return new PatternHelper(blockPos, EnumFacing.NORTH, EnumFacing.UP, createLoadingCache, 1, 1, 1);
      }

      int[] ints = new int[AxisDirection.values().length];
      EnumFacing rotateYCCW = size.direction1.rotateYCCW();
      BlockPos up = size.oppositeCorner.up(size.getHeight() - 1);

      for (AxisDirection axisDirection : AxisDirection.values()) {
         PatternHelper patternHelper = new PatternHelper(
            rotateYCCW.getAxisDirection() == axisDirection ? up : up.offset(size.direction1, size.getWidth() - 1),
            EnumFacing.getFacingFromAxis(axisDirection, axis),
            EnumFacing.UP,
            createLoadingCache,
            size.getWidth(),
            size.getHeight(),
            1
         );

         for (int i = 0; i < size.getWidth(); i++) {
            for (int j = 0; j < size.getHeight(); j++) {
               BlockWorldState translateOffset = patternHelper.translateOffset(i, j, 1);
               if (translateOffset.getBlockState() != null && translateOffset.getBlockState().getBlock().getMaterial() != Material.air) {
                  ints[axisDirection.ordinal()]++;
               }
            }
         }
      }

      AxisDirection axisDirection = AxisDirection.POSITIVE;

      for (AxisDirection axisDirection2 : AxisDirection.values()) {
         if (ints[axisDirection2.ordinal()] < ints[axisDirection.ordinal()]) {
            axisDirection = axisDirection2;
         }
      }

      return new PatternHelper(
         rotateYCCW.getAxisDirection() == axisDirection ? up : up.offset(size.direction1, size.getWidth() - 1),
         EnumFacing.getFacingFromAxis(axisDirection, axis),
         EnumFacing.UP,
         createLoadingCache,
         size.getWidth(),
         size.getHeight(),
         1
      );
   }

   public static class Size {
      public int foundPortalBlocks = 0;
      public World world;
      public Axis axis;
      public EnumFacing direction2;
      public EnumFacing direction1;
      public BlockPos oppositeCorner;
      public int width;
      public int height;

      public Size(World worldIn, BlockPos blockPos, Axis axis) {
         this.world = worldIn;
         this.axis = axis;
         if (axis == Axis.X) {
            this.direction2 = EnumFacing.EAST;
            this.direction1 = EnumFacing.WEST;
         } else {
            this.direction2 = EnumFacing.NORTH;
            this.direction1 = EnumFacing.SOUTH;
         }

         BlockPos blockPos2 = blockPos;

         while (blockPos.getY() > blockPos2.getY() - 21 && blockPos.getY() > 0 && this.canCreatePortal(worldIn.getBlockState(blockPos.down()).getBlock())) {
            blockPos = blockPos.down();
         }

         int n = this.distanceToPortalEdge(blockPos, this.direction2) - 1;
         if (n >= 0) {
            this.oppositeCorner = blockPos.offset(this.direction2, n);
            this.width = this.distanceToPortalEdge(this.oppositeCorner, this.direction1);
            if (this.width < 2 || this.width > 21) {
               this.oppositeCorner = null;
               this.width = 0;
            }
         }

         if (this.oppositeCorner != null) {
            this.height = this.findHeight();
         }
      }

      public int distanceToPortalEdge(BlockPos blockPos, EnumFacing enumFacing) {
         int n;
         for (n = 0; n < 22; n++) {
            BlockPos offset = blockPos.offset(enumFacing, n);
            if (!this.canCreatePortal(this.world.getBlockState(offset).getBlock()) || this.world.getBlockState(offset.down()).getBlock() != Blocks.obsidian) {
               break;
            }
         }

         Block block = this.world.getBlockState(blockPos.offset(enumFacing, n)).getBlock();
         return block == Blocks.obsidian ? n : 0;
      }

      public int getHeight() {
         return this.height;
      }

      public int getWidth() {
         return this.width;
      }

      public int findHeight() {
         label56:
         for (this.height = 0; this.height < 21; this.height++) {
            for (int i = 0; i < this.width; i++) {
               BlockPos up = this.oppositeCorner.offset(this.direction1, i).up(this.height);
               Block block = this.world.getBlockState(up).getBlock();
               if (!this.canCreatePortal(block)) {
                  break label56;
               }

               if (block == Blocks.portal) {
                  this.foundPortalBlocks++;
               }

               if (i == 0) {
                  block = this.world.getBlockState(up.offset(this.direction2)).getBlock();
                  if (block != Blocks.obsidian) {
                     break label56;
                  }
               } else if (i == this.width - 1) {
                  block = this.world.getBlockState(up.offset(this.direction1)).getBlock();
                  if (block != Blocks.obsidian) {
                     break label56;
                  }
               }
            }
         }

         for (int i = 0; i < this.width; i++) {
            if (this.world.getBlockState(this.oppositeCorner.offset(this.direction1, i).up(this.height)).getBlock() != Blocks.obsidian) {
               this.height = 0;
               break;
            }
         }

         if (this.height <= 21 && this.height >= 3) {
            return this.height;
         }

         this.oppositeCorner = null;
         this.width = 0;
         this.height = 0;
         return 0;
      }

      public boolean canCreatePortal(Block block) {
         return block.blockMaterial == Material.air || block == Blocks.fire || block == Blocks.portal;
      }

      public boolean isValid() {
         return this.oppositeCorner != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
      }

      public void createPortal() {
         for (int i = 0; i < this.width; i++) {
            BlockPos offset = this.oppositeCorner.offset(this.direction1, i);

            for (int j = 0; j < this.height; j++) {
               this.world.setBlockState(offset.up(j), Blocks.portal.getDefaultState().withProperty(BlockPortal.AXIS, this.axis), 2);
            }
         }
      }
   }
}
