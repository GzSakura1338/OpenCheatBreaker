package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class WorldGenDungeons extends WorldGenerator {
    public static WeightedRandomChestContent[] weightedRandomChestContentArray = new WeightedRandomChestContent[]{
        new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 10),
        new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 4, 10),
        new WeightedRandomChestContent(Items.bread, 0, 1, 1, 10),
        new WeightedRandomChestContent(Items.wheat, 0, 1, 4, 10),
        new WeightedRandomChestContent(Items.gunpowder, 0, 1, 4, 10),
        new WeightedRandomChestContent(Items.string, 0, 1, 4, 10),
        new WeightedRandomChestContent(Items.bucket, 0, 1, 1, 10),
        new WeightedRandomChestContent(Items.golden_apple, 0, 1, 1, 1),
        new WeightedRandomChestContent(Items.redstone, 0, 1, 4, 10),
        new WeightedRandomChestContent(Items.record_13, 0, 1, 1, 10),
        new WeightedRandomChestContent(Items.record_cat, 0, 1, 1, 10),
        new WeightedRandomChestContent(Items.name_tag, 0, 1, 1, 10),
        new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 2),
        new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 5),
        new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)
    };

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        byte b = 3;
        int n4 = random.nextInt(2) + 2;
        int n5 = random.nextInt(2) + 2;
        int n6 = 0;

        for (int i = n - n4 - 1; i <= n + n4 + 1; i++) {
            for (int j = n2 - 1; j <= n2 + b + 1; j++) {
                for (int k = n3 - n5 - 1; k <= n3 + n5 + 1; k++) {
                    Material material = world.getBlock(i, j, k).getMaterial();
                    if (j == n2 - 1 && !material.isSolid()) {
                        return false;
                    }

                    if (j == n2 + b + 1 && !material.isSolid()) {
                        return false;
                    }

                    if ((i == n - n4 - 1 || i == n + n4 + 1 || k == n3 - n5 - 1 || k == n3 + n5 + 1)
                        && j == n2
                        && world.isAirBlock(i, j, k)
                        && world.isAirBlock(i, j + 1, k)) {
                        n6++;
                    }
                }
            }
        }

        if (n6 >= 1 && n6 <= 5) {
            for (int i = n - n4 - 1; i <= n + n4 + 1; i++) {
                for (int j = n2 + b; j >= n2 - 1; j--) {
                    for (int k = n3 - n5 - 1; k <= n3 + n5 + 1; k++) {
                        if (i != n - n4 - 1
                            && j != n2 - 1
                            && k != n3 - n5 - 1
                            && i != n + n4 + 1
                            && j != n2 + b + 1
                            && k != n3 + n5 + 1) {
                            world.setBlockToAir(i, j, k);
                        } else if (j >= 0 && !world.getBlock(i, j - 1, k).getMaterial().isSolid()) {
                            world.setBlockToAir(i, j, k);
                        } else if (world.getBlock(i, j, k).getMaterial().isSolid()) {
                            if (j == n2 - 1 && random.nextInt(4) != 0) {
                                world.setBlock(i, j, k, Blocks.mossy_cobblestone, 0, 2);
                            } else {
                                world.setBlock(i, j, k, Blocks.cobblestone, 0, 2);
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 3; j++) {
                    int n7 = n + random.nextInt(n4 * 2 + 1) - n4;
                    int n8 = n3 + random.nextInt(n5 * 2 + 1) - n5;
                    if (world.isAirBlock(n7, n2, n8)) {
                        int n9 = 0;
                        if (world.getBlock(n7 - 1, n2, n8).getMaterial().isSolid()) {
                            n9++;
                        }

                        if (world.getBlock(n7 + 1, n2, n8).getMaterial().isSolid()) {
                            n9++;
                        }

                        if (world.getBlock(n7, n2, n8 - 1).getMaterial().isSolid()) {
                            n9++;
                        }

                        if (world.getBlock(n7, n2, n8 + 1).getMaterial().isSolid()) {
                            n9++;
                        }

                        if (n9 == 1) {
                            world.setBlock(n7, n2, n8, Blocks.chest, 0, 2);
                            WeightedRandomChestContent[] union = WeightedRandomChestContent.union(
                                weightedRandomChestContentArray, Items.enchanted_book.getLootTable(random)
                            );
                            TileEntityChest tileEntity = (TileEntityChest)world.getTileEntity(n7, n2, n8);
                            if (tileEntity != null) {
                                WeightedRandomChestContent.generateChestContents(random, union, tileEntity, 8);
                            }
                            break;
                        }
                    }
                }
            }

            world.setBlock(n, n2, n3, Blocks.mob_spawner, 0, 2);
            TileEntityMobSpawner tileEntity = (TileEntityMobSpawner)world.getTileEntity(n, n2, n3);
            if (tileEntity != null) {
                tileEntity.getLogic().setEntityName(this.pickMobSpawner(random));
            } else {
                System.err.println("Failed to fetch mob spawner entity at (" + n + ", " + n2 + ", " + n3 + ")");
            }

            return true;
        } else {
            return false;
        }
    }

    public String pickMobSpawner(Random random) {
        int nextInt = random.nextInt(4);
        return nextInt == 0 ? "Skeleton" : (nextInt == 1 ? "Zombie" : (nextInt == 2 ? "Zombie" : (nextInt == 3 ? "Spider" : "")));
    }
}
