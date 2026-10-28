package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class WorldGeneratorBonusChest extends WorldGenerator {
    public WeightedRandomChestContent[] theBonusChestGenerator;
    public int itemsToGenerateInBonusChest;

    public WorldGeneratorBonusChest(WeightedRandomChestContent[] weightedRandomChestContents, int n) {
        this.theBonusChestGenerator = weightedRandomChestContents;
        this.itemsToGenerateInBonusChest = n;
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        Block block;
        while (((block = world.getBlock(n, n2, n3)).getMaterial() == Material.air || block.getMaterial() == Material.leaves) && n2 > 1) {
            n2--;
        }

        if (n2 < 1) {
            return false;
        }

        n2++;

        for (int i = 0; i < 4; i++) {
            int n4 = n + random.nextInt(4) - random.nextInt(4);
            int n5 = n2 + random.nextInt(3) - random.nextInt(3);
            int n6 = n3 + random.nextInt(4) - random.nextInt(4);
            if (world.isAirBlock(n4, n5, n6) && World.doesBlockHaveSolidTopSurface(world, n4, n5 - 1, n6)) {
                world.setBlock(n4, n5, n6, Blocks.chest, 0, 2);
                TileEntityChest tileEntity = (TileEntityChest)world.getTileEntity(n4, n5, n6);
                if (tileEntity != null && tileEntity != null) {
                    WeightedRandomChestContent.generateChestContents(random, this.theBonusChestGenerator, tileEntity, this.itemsToGenerateInBonusChest);
                }

                if (world.isAirBlock(n4 - 1, n5, n6) && World.doesBlockHaveSolidTopSurface(world, n4 - 1, n5 - 1, n6)) {
                    world.setBlock(n4 - 1, n5, n6, Blocks.torch, 0, 2);
                }

                if (world.isAirBlock(n4 + 1, n5, n6) && World.doesBlockHaveSolidTopSurface(world, n4 - 1, n5 - 1, n6)) {
                    world.setBlock(n4 + 1, n5, n6, Blocks.torch, 0, 2);
                }

                if (world.isAirBlock(n4, n5, n6 - 1) && World.doesBlockHaveSolidTopSurface(world, n4 - 1, n5 - 1, n6)) {
                    world.setBlock(n4, n5, n6 - 1, Blocks.torch, 0, 2);
                }

                if (world.isAirBlock(n4, n5, n6 + 1) && World.doesBlockHaveSolidTopSurface(world, n4 - 1, n5 - 1, n6)) {
                    world.setBlock(n4, n5, n6 + 1, Blocks.torch, 0, 2);
                }

                return true;
            }
        }

        return false;
    }
}
