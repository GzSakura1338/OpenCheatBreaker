package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenForest;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;

public class BlockSapling extends BlockBush implements IGrowable {
   public static PropertyEnum<BlockPlanks.EnumType> TYPE = PropertyEnum.create("type", BlockPlanks.EnumType.class);
   public static PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);

   public BlockSapling() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockPlanks.EnumType.OAK).withProperty(STAGE, 0));
      float f = 0.4F;
      this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal(this.getUnlocalizedName() + "." + BlockPlanks.EnumType.OAK.getUnlocalizedName() + ".name");
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote) {
         super.updateTick(worldIn, pos, state, rand);
         if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
            this.grow(worldIn, pos, state, rand);
         }
      }
   }

   public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (state.getValue(STAGE) == 0) {
         worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4);
      } else {
         this.generateTree(worldIn, pos, state, rand);
      }
   }

   public void generateTree(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      WorldGenAbstractTree worldGenAbstractTree = rand.nextInt(10) == 0 ? new WorldGenBigTree(true) : new WorldGenTrees(true);
      int n = 0;
      int n2 = 0;
      boolean flag = false;
      switch (state.getValue(TYPE)) {
         case SPRUCE:
            label68:
            for (n = 0; n >= -1; n--) {
               for (n2 = 0; n2 >= -1; n2--) {
                  if (this.canSpawnGiantTree(worldIn, pos, n, n2, BlockPlanks.EnumType.SPRUCE)) {
                     worldGenAbstractTree = new WorldGenMegaPineTree(false, rand.nextBoolean());
                     flag = true;
                     break label68;
                  }
               }
            }

            if (!flag) {
               n2 = 0;
               n = 0;
               worldGenAbstractTree = new WorldGenTaiga2(true);
            }
            break;
         case BIRCH:
            worldGenAbstractTree = new WorldGenForest(true, false);
            break;
         case JUNGLE:
            IBlockState withProperty = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
            IBlockState withProperty2 = Blocks.leaves
               .getDefaultState()
               .withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE)
               .withProperty(BlockLeaves.CHECK_DECAY, false);

            label82:
            for (n = 0; n >= -1; n--) {
               for (n2 = 0; n2 >= -1; n2--) {
                  if (this.canSpawnGiantTree(worldIn, pos, n, n2, BlockPlanks.EnumType.JUNGLE)) {
                     worldGenAbstractTree = new WorldGenMegaJungle(true, 10, 20, withProperty, withProperty2);
                     flag = true;
                     break label82;
                  }
               }
            }

            if (!flag) {
               n2 = 0;
               n = 0;
               worldGenAbstractTree = new WorldGenTrees(true, 4 + rand.nextInt(7), withProperty, withProperty2, false);
            }
            break;
         case ACACIA:
            worldGenAbstractTree = new WorldGenSavannaTree(true);
            break;
         case DARK_OAK:
            label96:
            for (n = 0; n >= -1; n--) {
               for (n2 = 0; n2 >= -1; n2--) {
                  if (this.canSpawnGiantTree(worldIn, pos, n, n2, BlockPlanks.EnumType.DARK_OAK)) {
                     worldGenAbstractTree = new WorldGenCanopyTree(true);
                     flag = true;
                     break label96;
                  }
               }
            }

            if (!flag) {
               return;
            }
         case OAK:
      }

      IBlockState defaultState = Blocks.air.getDefaultState();
      if (flag) {
         worldIn.setBlockState(pos.add(n, 0, n2), defaultState, 4);
         worldIn.setBlockState(pos.add(n + 1, 0, n2), defaultState, 4);
         worldIn.setBlockState(pos.add(n, 0, n2 + 1), defaultState, 4);
         worldIn.setBlockState(pos.add(n + 1, 0, n2 + 1), defaultState, 4);
      } else {
         worldIn.setBlockState(pos, defaultState, 4);
      }

      if (!worldGenAbstractTree.generate(worldIn, rand, pos.add(n, 0, n2))) {
         if (flag) {
            worldIn.setBlockState(pos.add(n, 0, n2), state, 4);
            worldIn.setBlockState(pos.add(n + 1, 0, n2), state, 4);
            worldIn.setBlockState(pos.add(n, 0, n2 + 1), state, 4);
            worldIn.setBlockState(pos.add(n + 1, 0, n2 + 1), state, 4);
         } else {
            worldIn.setBlockState(pos, state, 4);
         }
      }
   }

   public boolean canSpawnGiantTree(World world, BlockPos blockPos, int n, int n2, BlockPlanks.EnumType enumType) {
      return this.isTypeAt(world, blockPos.add(n, 0, n2), enumType)
         && this.isTypeAt(world, blockPos.add(n + 1, 0, n2), enumType)
         && this.isTypeAt(world, blockPos.add(n, 0, n2 + 1), enumType)
         && this.isTypeAt(world, blockPos.add(n + 1, 0, n2 + 1), enumType);
   }

   public boolean isTypeAt(World worldIn, BlockPos pos, BlockPlanks.EnumType type) {
      IBlockState blockState = worldIn.getBlockState(pos);
      return blockState.getBlock() == this && blockState.getValue(TYPE) == type;
   }

   @Override
   public int damageDropped(IBlockState state) {
      return state.getValue(TYPE).getMetadata();
   }

   @Override
   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      for (BlockPlanks.EnumType enumType : BlockPlanks.EnumType.values()) {
         list.add(new ItemStack(itemIn, 1, enumType.getMetadata()));
      }
   }

   @Override
   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return true;
   }

   @Override
   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return worldIn.rand.nextFloat() < 0.45;
   }

   @Override
   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      this.grow(worldIn, pos, state, rand);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(TYPE, BlockPlanks.EnumType.byMetadata(meta & 7)).withProperty(STAGE, (meta & 8) >> 3);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int n = 0;
      n |= state.getValue(TYPE).getMetadata();
      return n | state.getValue(STAGE) << 3;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, TYPE, STAGE);
   }
}
