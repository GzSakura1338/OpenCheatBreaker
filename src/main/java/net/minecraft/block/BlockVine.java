package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockVine extends Block {
   public static PropertyBool UP = PropertyBool.create("up");
   public static PropertyBool NORTH = PropertyBool.create("north");
   public static PropertyBool EAST = PropertyBool.create("east");
   public static PropertyBool SOUTH = PropertyBool.create("south");
   public static PropertyBool WEST = PropertyBool.create("west");
   public static PropertyBool[] ALL_FACES = new PropertyBool[]{UP, NORTH, SOUTH, WEST, EAST};

   public BlockVine() {
      super(Material.vine);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(UP, false)
            .withProperty(NORTH, false)
            .withProperty(EAST, false)
            .withProperty(SOUTH, false)
            .withProperty(WEST, false)
      );
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state.withProperty(UP, worldIn.getBlockState(pos.up()).getBlock().isBlockNormalCube());
   }

   @Override
   public void setBlockBoundsForItemRender() {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
   public boolean isReplaceable(World worldIn, BlockPos pos) {
      return true;
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      float f = 0.0625F;
      float f2 = 1.0F;
      float f3 = 1.0F;
      float f4 = 1.0F;
      float f5 = 0.0F;
      float f6 = 0.0F;
      float f7 = 0.0F;
      boolean flag = false;
      if (worldIn.getBlockState(pos).getValue(WEST)) {
         f5 = Math.max(f5, 0.0625F);
         f2 = 0.0F;
         f3 = 0.0F;
         f6 = 1.0F;
         f4 = 0.0F;
         f7 = 1.0F;
         flag = true;
      }

      if (worldIn.getBlockState(pos).getValue(EAST)) {
         f2 = Math.min(f2, 0.9375F);
         f5 = 1.0F;
         f3 = 0.0F;
         f6 = 1.0F;
         f4 = 0.0F;
         f7 = 1.0F;
         flag = true;
      }

      if (worldIn.getBlockState(pos).getValue(NORTH)) {
         f7 = Math.max(f7, 0.0625F);
         f4 = 0.0F;
         f2 = 0.0F;
         f5 = 1.0F;
         f3 = 0.0F;
         f6 = 1.0F;
         flag = true;
      }

      if (worldIn.getBlockState(pos).getValue(SOUTH)) {
         f4 = Math.min(f4, 0.9375F);
         f7 = 1.0F;
         f2 = 0.0F;
         f5 = 1.0F;
         f3 = 0.0F;
         f6 = 1.0F;
         flag = true;
      }

      if (!flag && this.canPlaceOn(worldIn.getBlockState(pos.up()).getBlock())) {
         f3 = Math.min(f3, 0.9375F);
         f6 = 1.0F;
         f2 = 0.0F;
         f5 = 1.0F;
         f4 = 0.0F;
         f7 = 1.0F;
      }

      this.setBlockBounds(f2, f3, f4, f5, f6, f7);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   @Override
   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
      switch (side) {
         case UP:
            return this.canPlaceOn(worldIn.getBlockState(pos.up()).getBlock());
         case NORTH:
         case SOUTH:
         case EAST:
         case WEST:
            return this.canPlaceOn(worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock());
         default:
            return false;
      }
   }

   public boolean canPlaceOn(Block blockIn) {
      return blockIn.isFullCube() && blockIn.blockMaterial.blocksMovement();
   }

   public boolean recheckGrownSides(World worldIn, BlockPos pos, IBlockState state) {
      IBlockState state2 = state;

      for (EnumFacing enumFacing : Plane.HORIZONTAL) {
         PropertyBool propertyFor = getPropertyFor(enumFacing);
         if (state.getValue(propertyFor) && !this.canPlaceOn(worldIn.getBlockState(pos.offset(enumFacing)).getBlock())) {
            IBlockState blockState = worldIn.getBlockState(pos.up());
            if (blockState.getBlock() != this || !blockState.getValue(propertyFor)) {
               state = state.withProperty(propertyFor, false);
            }
         }
      }

      if (getNumGrownFaces(state) == 0) {
         return false;
      }

      if (state2 != state) {
         worldIn.setBlockState(pos, state, 2);
      }

      return true;
   }

   @Override
   public int getBlockColor() {
      return ColorizerFoliage.getFoliageColorBasic();
   }

   @Override
   public int getRenderColor(IBlockState state) {
      return ColorizerFoliage.getFoliageColorBasic();
   }

   @Override
   public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
      return worldIn.getBiomeGenForCoords(pos).getFoliageColorAtPos(pos);
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!worldIn.isRemote && !this.recheckGrownSides(worldIn, pos, state)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote && worldIn.rand.nextInt(4) == 0) {
         byte b = 4;
         int n = 5;
         boolean flag = false;

         label182:
         for (int i = -b; i <= b; i++) {
            for (int j = -b; j <= b; j++) {
               for (int k = -1; k <= 1; k++) {
                  if (worldIn.getBlockState(pos.add(i, k, j)).getBlock() == this) {
                     if (--n <= 0) {
                        flag = true;
                        break label182;
                     }
                  }
               }
            }
         }

         EnumFacing random = EnumFacing.random(rand);
         BlockPos up = pos.up();
         if (random == EnumFacing.UP && pos.getY() < 255 && worldIn.isAirBlock(up)) {
            if (!flag) {
               IBlockState state2 = state;

               for (EnumFacing enumFacing : Plane.HORIZONTAL) {
                  if (rand.nextBoolean() || !this.canPlaceOn(worldIn.getBlockState(up.offset(enumFacing)).getBlock())) {
                     state2 = state2.withProperty(getPropertyFor(enumFacing), false);
                  }
               }

               if (state2.getValue(NORTH) || state2.getValue(EAST) || state2.getValue(SOUTH) || state2.getValue(WEST)) {
                  worldIn.setBlockState(up, state2, 2);
               }
            }
         } else if (!random.getAxis().isHorizontal() || state.getValue(getPropertyFor(random))) {
            if (pos.getY() > 1) {
               BlockPos down = pos.down();
               IBlockState blockState = worldIn.getBlockState(down);
               Block block = blockState.getBlock();
               if (block.blockMaterial == Material.air) {
                  IBlockState state2 = state;

                  for (EnumFacing enumFacing : Plane.HORIZONTAL) {
                     if (rand.nextBoolean()) {
                        state2 = state2.withProperty(getPropertyFor(enumFacing), false);
                     }
                  }

                  if (state2.getValue(NORTH) || state2.getValue(EAST) || state2.getValue(SOUTH) || state2.getValue(WEST)) {
                     worldIn.setBlockState(down, state2, 2);
                  }
               } else if (block == this) {
                  IBlockState blockState2 = blockState;

                  for (EnumFacing enumFacing : Plane.HORIZONTAL) {
                     PropertyBool propertyFor = getPropertyFor(enumFacing);
                     if (rand.nextBoolean() && state.getValue(propertyFor)) {
                        blockState2 = blockState2.withProperty(propertyFor, true);
                     }
                  }

                  if (blockState2.getValue(NORTH) || blockState2.getValue(EAST) || blockState2.getValue(SOUTH) || blockState2.getValue(WEST)) {
                     worldIn.setBlockState(down, blockState2, 2);
                  }
               }
            }
         } else if (!flag) {
            BlockPos offset = pos.offset(random);
            Block block = worldIn.getBlockState(offset).getBlock();
            if (block.blockMaterial == Material.air) {
               EnumFacing rotateY = random.rotateY();
               EnumFacing rotateYCCW = random.rotateYCCW();
               boolean value = state.getValue(getPropertyFor(rotateY));
               boolean value2 = state.getValue(getPropertyFor(rotateYCCW));
               BlockPos offset2 = offset.offset(rotateY);
               BlockPos offset3 = offset.offset(rotateYCCW);
               if (value && this.canPlaceOn(worldIn.getBlockState(offset2).getBlock())) {
                  worldIn.setBlockState(offset, this.getDefaultState().withProperty(getPropertyFor(rotateY), true), 2);
               } else if (value2 && this.canPlaceOn(worldIn.getBlockState(offset3).getBlock())) {
                  worldIn.setBlockState(offset, this.getDefaultState().withProperty(getPropertyFor(rotateYCCW), true), 2);
               } else if (value && worldIn.isAirBlock(offset2) && this.canPlaceOn(worldIn.getBlockState(pos.offset(rotateY)).getBlock())) {
                  worldIn.setBlockState(offset2, this.getDefaultState().withProperty(getPropertyFor(random.getOpposite()), true), 2);
               } else if (value2 && worldIn.isAirBlock(offset3) && this.canPlaceOn(worldIn.getBlockState(pos.offset(rotateYCCW)).getBlock())) {
                  worldIn.setBlockState(offset3, this.getDefaultState().withProperty(getPropertyFor(random.getOpposite()), true), 2);
               } else if (this.canPlaceOn(worldIn.getBlockState(offset.up()).getBlock())) {
                  worldIn.setBlockState(offset, this.getDefaultState(), 2);
               }
            } else if (block.blockMaterial.isOpaque() && block.isFullCube()) {
               worldIn.setBlockState(pos, state.withProperty(getPropertyFor(random), true), 2);
            }
         }
      }
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      IBlockState withProperty = this.getDefaultState()
         .withProperty(UP, false)
         .withProperty(NORTH, false)
         .withProperty(EAST, false)
         .withProperty(SOUTH, false)
         .withProperty(WEST, false);
      return facing.getAxis().isHorizontal() ? withProperty.withProperty(getPropertyFor(facing.getOpposite()), true) : withProperty;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return null;
   }

   @Override
   public int quantityDropped(Random random) {
      return 0;
   }

   @Override
   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
      if (!worldIn.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears) {
         player.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
         spawnAsEntity(worldIn, pos, new ItemStack(Blocks.vine, 1, 0));
      } else {
         super.harvestBlock(worldIn, player, pos, state, te);
      }
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState()
         .withProperty(SOUTH, (meta & 1) > 0)
         .withProperty(WEST, (meta & 2) > 0)
         .withProperty(NORTH, (meta & 4) > 0)
         .withProperty(EAST, (meta & 8) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int n = 0;
      if (state.getValue(SOUTH)) {
         n |= 1;
      }

      if (state.getValue(WEST)) {
         n |= 2;
      }

      if (state.getValue(NORTH)) {
         n |= 4;
      }

      if (state.getValue(EAST)) {
         n |= 8;
      }

      return n;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, UP, NORTH, EAST, SOUTH, WEST);
   }

   public static PropertyBool getPropertyFor(EnumFacing side) {
      switch (side) {
         case UP:
            return UP;
         case NORTH:
            return NORTH;
         case SOUTH:
            return SOUTH;
         case EAST:
            return EAST;
         case WEST:
            return WEST;
         default:
            throw new IllegalArgumentException(side + " is an invalid choice");
      }
   }

   public static int getNumGrownFaces(IBlockState state) {
      int n = 0;

      for (PropertyBool propertyBool : ALL_FACES) {
         if (state.getValue(propertyBool)) {
            n++;
         }
      }

      return n;
   }
}
