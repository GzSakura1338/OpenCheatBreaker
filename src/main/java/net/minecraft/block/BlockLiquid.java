package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public abstract class BlockLiquid extends Block {
   public static PropertyInteger LEVEL = PropertyInteger.create("level", 0, 15);

   public BlockLiquid(Material materialIn) {
      super(materialIn);
      this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0));
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      this.setTickRandomly(true);
   }

   @Override
   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return this.blockMaterial != Material.lava;
   }

   @Override
   public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
      return this.blockMaterial == Material.water ? BiomeColorHelper.getWaterColorAtPos(worldIn, pos) : 16777215;
   }

   public static float getLiquidHeightPercent(int meta) {
      if (meta >= 8) {
         meta = 0;
      }

      return (meta + 1) / 9.0F;
   }

   public int getLevel(IBlockAccess worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos).getBlock().getMaterial() == this.blockMaterial ? worldIn.getBlockState(pos).getValue(LEVEL) : -1;
   }

   public int getEffectiveFlowDecay(IBlockAccess worldIn, BlockPos pos) {
      int level = this.getLevel(worldIn, pos);
      return level >= 8 ? 0 : level;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
      return hitIfLiquid && state.getValue(LEVEL) == 0;
   }

   @Override
   public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      Material material = worldIn.getBlockState(pos).getBlock().getMaterial();
      return material == this.blockMaterial ? false : (side == EnumFacing.UP ? true : (material == Material.ice ? false : super.isBlockSolid(worldIn, pos, side)));
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      return worldIn.getBlockState(pos).getBlock().getMaterial() == this.blockMaterial
         ? false
         : (side == EnumFacing.UP ? true : super.shouldSideBeRendered(worldIn, pos, side));
   }

   public boolean shouldRenderSides(IBlockAccess blockAccess, BlockPos pos) {
      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            IBlockState blockState = blockAccess.getBlockState(pos.add(i, 0, j));
            Block block = blockState.getBlock();
            Material material = block.getMaterial();
            if (material != this.blockMaterial && !block.isFullBlock()) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   @Override
   public int getRenderType() {
      return 1;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return null;
   }

   @Override
   public int quantityDropped(Random random) {
      return 0;
   }

   public Vec3 getFlowVector(IBlockAccess worldIn, BlockPos pos) {
      Vec3 vec3 = new Vec3(0.0, 0.0, 0.0);
      int effectiveFlowDecay = this.getEffectiveFlowDecay(worldIn, pos);

      for (EnumFacing enumFacing : Plane.HORIZONTAL) {
         BlockPos offset = pos.offset(enumFacing);
         int effectiveFlowDecay2 = this.getEffectiveFlowDecay(worldIn, offset);
         if (effectiveFlowDecay2 < 0) {
            if (!worldIn.getBlockState(offset).getBlock().getMaterial().blocksMovement()) {
               effectiveFlowDecay2 = this.getEffectiveFlowDecay(worldIn, offset.down());
               if (effectiveFlowDecay2 >= 0) {
                  int n = effectiveFlowDecay2 - (effectiveFlowDecay - 8);
                  vec3 = vec3.addVector((offset.getX() - pos.getX()) * n, (offset.getY() - pos.getY()) * n, (offset.getZ() - pos.getZ()) * n);
               }
            }
         } else if (effectiveFlowDecay2 >= 0) {
            int n = effectiveFlowDecay2 - effectiveFlowDecay;
            vec3 = vec3.addVector((offset.getX() - pos.getX()) * n, (offset.getY() - pos.getY()) * n, (offset.getZ() - pos.getZ()) * n);
         }
      }

      if (worldIn.getBlockState(pos).getValue(LEVEL) >= 8) {
         for (EnumFacing enumFacing : Plane.HORIZONTAL) {
            BlockPos offset = pos.offset(enumFacing);
            if (this.isBlockSolid(worldIn, offset, enumFacing) || this.isBlockSolid(worldIn, offset.up(), enumFacing)) {
               vec3 = vec3.normalize().addVector(0.0, -6.0, 0.0);
               break;
            }
         }
      }

      return vec3.normalize();
   }

   @Override
   public Vec3 modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3 motion) {
      return motion.add(this.getFlowVector(worldIn, pos));
   }

   @Override
   public int tickRate(World worldIn) {
      return this.blockMaterial == Material.water ? 5 : (this.blockMaterial == Material.lava ? (worldIn.provider.getHasNoSky() ? 10 : 30) : 0);
   }

   @Override
   public int getMixedBrightnessForBlock(IBlockAccess worldIn, BlockPos pos) {
      int combinedLight = worldIn.getCombinedLight(pos, 0);
      int combinedLight2 = worldIn.getCombinedLight(pos.up(), 0);
      int n = combinedLight & 0xFF;
      int n2 = combinedLight2 & 0xFF;
      int n3 = combinedLight >> 16 & 0xFF;
      int n4 = combinedLight2 >> 16 & 0xFF;
      return (n > n2 ? n : n2) | (n3 > n4 ? n3 : n4) << 16;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return this.blockMaterial == Material.water ? EnumWorldBlockLayer.TRANSLUCENT : EnumWorldBlockLayer.SOLID;
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      double d = pos.getX();
      double d2 = pos.getY();
      double d3 = pos.getZ();
      if (this.blockMaterial == Material.water) {
         int value = state.getValue(LEVEL);
         if (value > 0 && value < 8) {
            if (rand.nextInt(64) == 0) {
               worldIn.playSound(d + 0.5, d2 + 0.5, d3 + 0.5, "liquid.water", rand.nextFloat() * 0.25F + 0.75F, rand.nextFloat() * 1.0F + 0.5F, false);
            }
         } else if (rand.nextInt(10) == 0) {
            worldIn.spawnParticle(EnumParticleTypes.SUSPENDED, d + rand.nextFloat(), d2 + rand.nextFloat(), d3 + rand.nextFloat(), 0.0, 0.0, 0.0);
         }
      }

      if (this.blockMaterial == Material.lava
         && worldIn.getBlockState(pos.up()).getBlock().getMaterial() == Material.air
         && !worldIn.getBlockState(pos.up()).getBlock().isOpaqueCube()) {
         if (rand.nextInt(100) == 0) {
            double d4 = d + rand.nextFloat();
            double d5 = d2 + this.maxY;
            double d6 = d3 + rand.nextFloat();
            worldIn.spawnParticle(EnumParticleTypes.LAVA, d4, d5, d6, 0.0, 0.0, 0.0);
            worldIn.playSound(d4, d5, d6, "liquid.lavapop", 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
         }

         if (rand.nextInt(200) == 0) {
            worldIn.playSound(d, d2, d3, "liquid.lava", 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false);
         }
      }

      if (rand.nextInt(10) == 0 && World.doesBlockHaveSolidTopSurface(worldIn, pos.down())) {
         Material material = worldIn.getBlockState(pos.down(2)).getBlock().getMaterial();
         if (!material.blocksMovement() && !material.isLiquid()) {
            double d4 = d + rand.nextFloat();
            double d5 = d2 - 1.05;
            double d6 = d3 + rand.nextFloat();
            if (this.blockMaterial == Material.water) {
               worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d4, d5, d6, 0.0, 0.0, 0.0);
            } else {
               worldIn.spawnParticle(EnumParticleTypes.DRIP_LAVA, d4, d5, d6, 0.0, 0.0, 0.0);
            }
         }
      }
   }

   public static double getFlowDirection(IBlockAccess worldIn, BlockPos pos, Material materialIn) {
      Vec3 flowVector = getFlowingBlock(materialIn).getFlowVector(worldIn, pos);
      return flowVector.xCoord == 0.0 && flowVector.zCoord == 0.0 ? -1000.0 : MathHelper.atan2(flowVector.zCoord, flowVector.xCoord) - (Math.PI / 2);
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.checkForMixing(worldIn, pos, state);
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      this.checkForMixing(worldIn, pos, state);
   }

   public boolean checkForMixing(World worldIn, BlockPos pos, IBlockState state) {
      if (this.blockMaterial == Material.lava) {
         boolean flag = false;

         for (EnumFacing enumFacing : EnumFacing.values()) {
            if (enumFacing != EnumFacing.DOWN && worldIn.getBlockState(pos.offset(enumFacing)).getBlock().getMaterial() == Material.water) {
               flag = true;
               break;
            }
         }

         if (flag) {
            Integer value = state.getValue(LEVEL);
            if (value == 0) {
               worldIn.setBlockState(pos, Blocks.obsidian.getDefaultState());
               this.triggerMixEffects(worldIn, pos);
               return true;
            }

            if (value <= 4) {
               worldIn.setBlockState(pos, Blocks.cobblestone.getDefaultState());
               this.triggerMixEffects(worldIn, pos);
               return true;
            }
         }
      }

      return false;
   }

   public void triggerMixEffects(World worldIn, BlockPos pos) {
      double d = pos.getX();
      double d2 = pos.getY();
      double d3 = pos.getZ();
      worldIn.playSoundEffect(d + 0.5, d2 + 0.5, d3 + 0.5, "random.fizz", 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

      for (int i = 0; i < 8; i++) {
         worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d + Math.random(), d2 + 1.2, d3 + Math.random(), 0.0, 0.0, 0.0);
      }
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(LEVEL, meta);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(LEVEL);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, LEVEL);
   }

   public static BlockDynamicLiquid getFlowingBlock(Material materialIn) {
      if (materialIn == Material.water) {
         return Blocks.flowing_water;
      } else if (materialIn == Material.lava) {
         return Blocks.flowing_lava;
      } else {
         throw new IllegalArgumentException("Invalid material");
      }
   }

   public static BlockStaticLiquid getStaticBlock(Material materialIn) {
      if (materialIn == Material.water) {
         return Blocks.water;
      } else if (materialIn == Material.lava) {
         return Blocks.lava;
      } else {
         throw new IllegalArgumentException("Invalid material");
      }
   }
}
