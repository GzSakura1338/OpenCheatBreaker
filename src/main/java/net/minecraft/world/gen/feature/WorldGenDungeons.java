package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenDungeons extends WorldGenerator {
   public static Logger LOGGER = LogManager.getLogger();
   public static String[] SPAWNERTYPES = new String[]{"Skeleton", "Zombie", "Zombie", "Spider"};
   public static List<WeightedRandomChestContent> CHESTCONTENT = Lists.newArrayList(
      new WeightedRandomChestContent[]{
         new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 10),
         new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 4, 10),
         new WeightedRandomChestContent(Items.bread, 0, 1, 1, 10),
         new WeightedRandomChestContent(Items.wheat, 0, 1, 4, 10),
         new WeightedRandomChestContent(Items.gunpowder, 0, 1, 4, 10),
         new WeightedRandomChestContent(Items.string, 0, 1, 4, 10),
         new WeightedRandomChestContent(Items.bucket, 0, 1, 1, 10),
         new WeightedRandomChestContent(Items.golden_apple, 0, 1, 1, 1),
         new WeightedRandomChestContent(Items.redstone, 0, 1, 4, 10),
         new WeightedRandomChestContent(Items.record_13, 0, 1, 1, 4),
         new WeightedRandomChestContent(Items.record_cat, 0, 1, 1, 4),
         new WeightedRandomChestContent(Items.name_tag, 0, 1, 1, 10),
         new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 2),
         new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 5),
         new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)
      }
   );

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      byte b = 3;
      int n = rand.nextInt(2) + 2;
      int n2 = -n - 1;
      int n3 = n + 1;
      byte b2 = -1;
      byte b3 = 4;
      int n4 = rand.nextInt(2) + 2;
      int n5 = -n4 - 1;
      int n6 = n4 + 1;
      int n7 = 0;

      for (int i = n2; i <= n3; i++) {
         for (int j = -1; j <= 4; j++) {
            for (int k = n5; k <= n6; k++) {
               BlockPos blockPos = position.add(i, j, k);
               Material material = worldIn.getBlockState(blockPos).getBlock().getMaterial();
               boolean isSolid = material.isSolid();
               if (j == -1 && !isSolid) {
                  return false;
               }

               if (j == 4 && !isSolid) {
                  return false;
               }

               if ((i == n2 || i == n3 || k == n5 || k == n6)
                  && j == 0
                  && worldIn.isAirBlock(blockPos)
                  && worldIn.isAirBlock(blockPos.up())) {
                  n7++;
               }
            }
         }
      }

      if (n7 >= 1 && n7 <= 5) {
         for (int i = n2; i <= n3; i++) {
            for (int j = 3; j >= -1; j--) {
               for (int k = n5; k <= n6; k++) {
                  BlockPos blockPos = position.add(i, j, k);
                  if (i != n2 && j != -1 && k != n5 && i != n3 && j != 4 && k != n6) {
                     if (worldIn.getBlockState(blockPos).getBlock() != Blocks.chest) {
                        worldIn.setBlockToAir(blockPos);
                     }
                  } else if (blockPos.getY() >= 0 && !worldIn.getBlockState(blockPos.down()).getBlock().getMaterial().isSolid()) {
                     worldIn.setBlockToAir(blockPos);
                  } else if (worldIn.getBlockState(blockPos).getBlock().getMaterial().isSolid() && worldIn.getBlockState(blockPos).getBlock() != Blocks.chest) {
                     if (j == -1 && rand.nextInt(4) != 0) {
                        worldIn.setBlockState(blockPos, Blocks.mossy_cobblestone.getDefaultState(), 2);
                     } else {
                        worldIn.setBlockState(blockPos, Blocks.cobblestone.getDefaultState(), 2);
                     }
                  }
               }
            }
         }

         for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
               int n8 = position.getX() + rand.nextInt(n * 2 + 1) - n;
               int n9 = position.getY();
               int n10 = position.getZ() + rand.nextInt(n4 * 2 + 1) - n4;
               BlockPos blockPos = new BlockPos(n8, n9, n10);
               if (worldIn.isAirBlock(blockPos)) {
                  int n11 = 0;

                  for (EnumFacing enumFacing : Plane.HORIZONTAL) {
                     if (worldIn.getBlockState(blockPos.offset(enumFacing)).getBlock().getMaterial().isSolid()) {
                        n11++;
                     }
                  }

                  if (n11 == 1) {
                     worldIn.setBlockState(blockPos, Blocks.chest.correctFacing(worldIn, blockPos, Blocks.chest.getDefaultState()), 2);
                     List combineLootTables = WeightedRandomChestContent.combineLootTables(CHESTCONTENT, Items.enchanted_book.getRandom(rand));
                     TileEntity tileEntity = worldIn.getTileEntity(blockPos);
                     if (tileEntity instanceof TileEntityChest) {
                        WeightedRandomChestContent.generateChestContents(rand, combineLootTables, (TileEntityChest)tileEntity, 8);
                     }
                     break;
                  }
               }
            }
         }

         worldIn.setBlockState(position, Blocks.mob_spawner.getDefaultState(), 2);
         TileEntity tileEntity = worldIn.getTileEntity(position);
         if (tileEntity instanceof TileEntityMobSpawner) {
            ((TileEntityMobSpawner)tileEntity).getSpawnerBaseLogic().setEntityName(this.pickMobSpawner(rand));
         } else {
            LOGGER.error("Failed to fetch mob spawner entity at (" + position.getX() + ", " + position.getY() + ", " + position.getZ() + ")");
         }

         return true;
      } else {
         return false;
      }
   }

   public String pickMobSpawner(Random random) {
      return SPAWNERTYPES[random.nextInt(SPAWNERTYPES.length)];
   }
}
