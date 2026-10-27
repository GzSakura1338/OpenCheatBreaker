package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public abstract class BlockLeaves extends BlockLeavesBase {
   public static PropertyBool DECAYABLE = PropertyBool.create("decayable");
   public static PropertyBool CHECK_DECAY = PropertyBool.create("check_decay");
   public int[] surroundings;
   public int iconIndex;
   public boolean isTransparent;

   public BlockLeaves() {
      super(Material.leaves, false);
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabDecorations);
      this.setHardness(0.2F);
      this.setLightOpacity(1);
      this.setStepSound(soundTypeGrass);
   }

   @Override
   public int getBlockColor() {
      return ColorizerFoliage.getFoliageColor(0.5, 1.0);
   }

   @Override
   public int getRenderColor(IBlockState state) {
      return ColorizerFoliage.getFoliageColorBasic();
   }

   @Override
   public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
      return BiomeColorHelper.getFoliageColorAtPos(worldIn, pos);
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      byte b = 1;
      int n = b + 1;
      int n2 = pos.getX();
      int n3 = pos.getY();
      int n4 = pos.getZ();
      if (worldIn.isAreaLoaded(new BlockPos(n2 - n, n3 - n, n4 - n), new BlockPos(n2 + n, n3 + n, n4 + n))) {
         for (int i = -b; i <= b; i++) {
            for (int j = -b; j <= b; j++) {
               for (int k = -b; k <= b; k++) {
                  BlockPos blockPos = pos.add(i, j, k);
                  IBlockState blockState = worldIn.getBlockState(blockPos);
                  if (blockState.getBlock().getMaterial() == Material.leaves && !blockState.getValue(CHECK_DECAY)) {
                     worldIn.setBlockState(blockPos, blockState.withProperty(CHECK_DECAY, true), 4);
                  }
               }
            }
         }
      }
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote && state.getValue(CHECK_DECAY) && state.getValue(DECAYABLE)) {
         byte b = 4;
         int n = b + 1;
         int n2 = pos.getX();
         int n3 = pos.getY();
         int n4 = pos.getZ();
         byte b2 = 32;
         int n5 = b2 * b2;
         int n6 = b2 / 2;
         if (this.surroundings == null) {
            this.surroundings = new int[b2 * b2 * b2];
         }

         if (worldIn.isAreaLoaded(new BlockPos(n2 - n, n3 - n, n4 - n), new BlockPos(n2 + n, n3 + n, n4 + n))) {
            MutableBlockPos mutableBlockPos = new MutableBlockPos();

            for (int i = -b; i <= b; i++) {
               for (int j = -b; j <= b; j++) {
                  for (int k = -b; k <= b; k++) {
                     Block block = worldIn.getBlockState(mutableBlockPos.set(n2 + i, n3 + j, n4 + k)).getBlock();
                     if (block != Blocks.log && block != Blocks.log2) {
                        if (block.getMaterial() == Material.leaves) {
                           this.surroundings[(i + n6) * n5 + (j + n6) * b2 + k + n6] = -2;
                        } else {
                           this.surroundings[(i + n6) * n5 + (j + n6) * b2 + k + n6] = -1;
                        }
                     } else {
                        this.surroundings[(i + n6) * n5 + (j + n6) * b2 + k + n6] = 0;
                     }
                  }
               }
            }

            for (int i = 1; i <= 4; i++) {
               for (int j = -b; j <= b; j++) {
                  for (int k = -b; k <= b; k++) {
                     for (int m = -b; m <= b; m++) {
                        if (this.surroundings[(j + n6) * n5 + (k + n6) * b2 + m + n6] == i - 1) {
                           if (this.surroundings[(j + n6 - 1) * n5 + (k + n6) * b2 + m + n6] == -2) {
                              this.surroundings[(j + n6 - 1) * n5 + (k + n6) * b2 + m + n6] = i;
                           }

                           if (this.surroundings[(j + n6 + 1) * n5 + (k + n6) * b2 + m + n6] == -2) {
                              this.surroundings[(j + n6 + 1) * n5 + (k + n6) * b2 + m + n6] = i;
                           }

                           if (this.surroundings[(j + n6) * n5 + (k + n6 - 1) * b2 + m + n6] == -2) {
                              this.surroundings[(j + n6) * n5 + (k + n6 - 1) * b2 + m + n6] = i;
                           }

                           if (this.surroundings[(j + n6) * n5 + (k + n6 + 1) * b2 + m + n6] == -2) {
                              this.surroundings[(j + n6) * n5 + (k + n6 + 1) * b2 + m + n6] = i;
                           }

                           if (this.surroundings[(j + n6) * n5 + (k + n6) * b2 + (m + n6 - 1)] == -2) {
                              this.surroundings[(j + n6) * n5 + (k + n6) * b2 + (m + n6 - 1)] = i;
                           }

                           if (this.surroundings[(j + n6) * n5 + (k + n6) * b2 + m + n6 + 1] == -2) {
                              this.surroundings[(j + n6) * n5 + (k + n6) * b2 + m + n6 + 1] = i;
                           }
                        }
                     }
                  }
               }
            }
         }

         int n7 = this.surroundings[n6 * n5 + n6 * b2 + n6];
         if (n7 >= 0) {
            worldIn.setBlockState(pos, state.withProperty(CHECK_DECAY, false), 4);
         } else {
            this.destroy(worldIn, pos);
         }
      }
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (worldIn.isRainingAt(pos.up()) && !World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && rand.nextInt(15) == 1) {
         double d = pos.getX() + rand.nextFloat();
         double d2 = pos.getY() - 0.05;
         double d3 = pos.getZ() + rand.nextFloat();
         worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d, d2, d3, 0.0, 0.0, 0.0);
      }
   }

   public void destroy(World worldIn, BlockPos pos) {
      this.dropBlockAsItem(worldIn, pos, worldIn.getBlockState(pos), 0);
      worldIn.setBlockToAir(pos);
   }

   @Override
   public int quantityDropped(Random random) {
      return random.nextInt(20) == 0 ? 1 : 0;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(Blocks.sapling);
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      if (!worldIn.isRemote) {
         int saplingDropChance = this.getSaplingDropChance(state);
         if (fortune > 0) {
            saplingDropChance -= 2 << fortune;
            if (saplingDropChance < 10) {
               saplingDropChance = 10;
            }
         }

         if (worldIn.rand.nextInt(saplingDropChance) == 0) {
            Item itemDropped = this.getItemDropped(state, worldIn.rand, fortune);
            spawnAsEntity(worldIn, pos, new ItemStack(itemDropped, 1, this.damageDropped(state)));
         }

         saplingDropChance = 200;
         if (fortune > 0) {
            saplingDropChance -= 10 << fortune;
            if (saplingDropChance < 40) {
               saplingDropChance = 40;
            }
         }

         this.dropApple(worldIn, pos, state, saplingDropChance);
      }
   }

   public void dropApple(World worldIn, BlockPos pos, IBlockState state, int chance) {
   }

   public int getSaplingDropChance(IBlockState state) {
      return 20;
   }

   @Override
   public boolean isOpaqueCube() {
      return !this.fancyGraphics;
   }

   public void setGraphicsLevel(boolean fancy) {
      this.isTransparent = fancy;
      this.fancyGraphics = fancy;
      this.iconIndex = fancy ? 0 : 1;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return this.isTransparent ? EnumWorldBlockLayer.CUTOUT_MIPPED : EnumWorldBlockLayer.SOLID;
   }

   @Override
   public boolean isVisuallyOpaque() {
      return false;
   }

   public abstract BlockPlanks.EnumType getWoodType(int n);
}
