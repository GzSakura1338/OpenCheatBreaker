package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockPattern.PatternHelper;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockPumpkin extends BlockDirectional {
   public BlockPattern snowmanBasePattern;
   public BlockPattern snowmanPattern;
   public BlockPattern golemBasePattern;
   public BlockPattern golemPattern;
   public static Predicate<IBlockState> IS_GOLEM_HEAD_PREDICATE = new Predicate<IBlockState>() {
      public boolean apply(IBlockState blockState) {
         return blockState != null && (blockState.getBlock() == Blocks.pumpkin || blockState.getBlock() == Blocks.lit_pumpkin);
      }
   };

   public BlockPumpkin() {
      super(Material.gourd, MapColor.adobeColor);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      super.onBlockAdded(worldIn, pos, state);
      this.trySpawnGolem(worldIn, pos);
   }

   public boolean canDispenserPlace(World worldIn, BlockPos pos) {
      return this.getSnowmanBasePattern().match(worldIn, pos) != null || this.getGolemBasePattern().match(worldIn, pos) != null;
   }

   public void trySpawnGolem(World worldIn, BlockPos pos) {
      PatternHelper patternHelper;
      if ((patternHelper = this.getSnowmanPattern().match(worldIn, pos)) != null) {
         for (int i = 0; i < this.getSnowmanPattern().getThumbLength(); i++) {
            BlockWorldState translateOffset = patternHelper.translateOffset(0, i, 0);
            worldIn.setBlockState(translateOffset.getPos(), Blocks.air.getDefaultState(), 2);
         }

         EntitySnowman entitySnowman = new EntitySnowman(worldIn);
         BlockPos pos2 = patternHelper.translateOffset(0, 2, 0).getPos();
         entitySnowman.setLocationAndAngles(pos2.getX() + 0.5, pos2.getY() + 0.05, pos2.getZ() + 0.5, 0.0F, 0.0F);
         worldIn.spawnEntityInWorld(entitySnowman);

         for (int i = 0; i < 120; i++) {
            worldIn.spawnParticle(
               EnumParticleTypes.SNOW_SHOVEL,
               pos2.getX() + worldIn.rand.nextDouble(),
               pos2.getY() + worldIn.rand.nextDouble() * 2.5,
               pos2.getZ() + worldIn.rand.nextDouble(),
               0.0,
               0.0,
               0.0
            );
         }

         for (int i = 0; i < this.getSnowmanPattern().getThumbLength(); i++) {
            BlockWorldState translateOffset = patternHelper.translateOffset(0, i, 0);
            worldIn.notifyNeighborsRespectDebug(translateOffset.getPos(), Blocks.air);
         }
      } else if ((patternHelper = this.getGolemPattern().match(worldIn, pos)) != null) {
         for (int i = 0; i < this.getGolemPattern().getPalmLength(); i++) {
            for (int j = 0; j < this.getGolemPattern().getThumbLength(); j++) {
               worldIn.setBlockState(patternHelper.translateOffset(i, j, 0).getPos(), Blocks.air.getDefaultState(), 2);
            }
         }

         BlockPos pos2 = patternHelper.translateOffset(1, 2, 0).getPos();
         EntityIronGolem entityIronGolem = new EntityIronGolem(worldIn);
         entityIronGolem.setPlayerCreated(true);
         entityIronGolem.setLocationAndAngles(pos2.getX() + 0.5, pos2.getY() + 0.05, pos2.getZ() + 0.5, 0.0F, 0.0F);
         worldIn.spawnEntityInWorld(entityIronGolem);

         for (int i = 0; i < 120; i++) {
            worldIn.spawnParticle(
               EnumParticleTypes.SNOWBALL,
               pos2.getX() + worldIn.rand.nextDouble(),
               pos2.getY() + worldIn.rand.nextDouble() * 3.9,
               pos2.getZ() + worldIn.rand.nextDouble(),
               0.0,
               0.0,
               0.0
            );
         }

         for (int i = 0; i < this.getGolemPattern().getPalmLength(); i++) {
            for (int j = 0; j < this.getGolemPattern().getThumbLength(); j++) {
               BlockWorldState translateOffset = patternHelper.translateOffset(i, j, 0);
               worldIn.notifyNeighborsRespectDebug(translateOffset.getPos(), Blocks.air);
            }
         }
      }
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos).getBlock().blockMaterial.isReplaceable() && World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(FACING).getHorizontalIndex();
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING);
   }

   public BlockPattern getSnowmanBasePattern() {
      if (this.snowmanBasePattern == null) {
         this.snowmanBasePattern = FactoryBlockPattern.start()
            .aisle(" ", "#", "#")
            .where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.snow)))
            .build();
      }

      return this.snowmanBasePattern;
   }

   public BlockPattern getSnowmanPattern() {
      if (this.snowmanPattern == null) {
         this.snowmanPattern = FactoryBlockPattern.start()
            .aisle("^", "#", "#")
            .where('^', BlockWorldState.hasState(IS_GOLEM_HEAD_PREDICATE))
            .where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.snow)))
            .build();
      }

      return this.snowmanPattern;
   }

   public BlockPattern getGolemBasePattern() {
      if (this.golemBasePattern == null) {
         this.golemBasePattern = FactoryBlockPattern.start()
            .aisle("~ ~", "###", "~#~")
            .where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.iron_block)))
            .where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air)))
            .build();
      }

      return this.golemBasePattern;
   }

   public BlockPattern getGolemPattern() {
      if (this.golemPattern == null) {
         this.golemPattern = FactoryBlockPattern.start()
            .aisle("~^~", "###", "~#~")
            .where('^', BlockWorldState.hasState(IS_GOLEM_HEAD_PREDICATE))
            .where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.iron_block)))
            .where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air)))
            .build();
      }

      return this.golemPattern;
   }
}
