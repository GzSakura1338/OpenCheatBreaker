package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.block.BlockLever;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Direction;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureComponent.BlockSelector;

public class ComponentScatteredFeaturePieces {
    public static void registerScatteredFeaturePieces() {
        MapGenStructureIO.registerStructureComponent(ComponentScatteredFeaturePieces.DesertPyramid.class, "TeDP");
        MapGenStructureIO.registerStructureComponent(ComponentScatteredFeaturePieces.JunglePyramid.class, "TeJP");
        MapGenStructureIO.registerStructureComponent(ComponentScatteredFeaturePieces.SwampHut.class, "TeSH");
    }

    public static class DesertPyramid extends ComponentScatteredFeaturePieces.Feature {
        public boolean[] booleanArray = new boolean[4];
        public static WeightedRandomChestContent[] itemsToGenerateInTemple = new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3),
            new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
            new WeightedRandomChestContent(Items.gold_ingot, 0, 2, 7, 15),
            new WeightedRandomChestContent(Items.emerald, 0, 1, 3, 2),
            new WeightedRandomChestContent(Items.bone, 0, 4, 6, 20),
            new WeightedRandomChestContent(Items.rotten_flesh, 0, 3, 7, 16),
            new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3),
            new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)
        };

        public DesertPyramid() {
        }

        public DesertPyramid(Random random, int n, int n2) {
            super(random, n, 64, n2, 21, 15, 21);
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setBoolean("hasPlacedChest0", this.booleanArray[0]);
            nbttagCompound.setBoolean("hasPlacedChest1", this.booleanArray[1]);
            nbttagCompound.setBoolean("hasPlacedChest2", this.booleanArray[2]);
            nbttagCompound.setBoolean("hasPlacedChest3", this.booleanArray[3]);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.booleanArray[0] = nbttagCompound.getBoolean("hasPlacedChest0");
            this.booleanArray[1] = nbttagCompound.getBoolean("hasPlacedChest1");
            this.booleanArray[2] = nbttagCompound.getBoolean("hasPlacedChest2");
            this.booleanArray[3] = nbttagCompound.getBoolean("hasPlacedChest3");
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            this.fillWithBlocks(world, structureBoundingBox, 0, -4, 0, this.scatteredFeatureSizeX - 1, 0, this.scatteredFeatureSizeZ - 1, Blocks.sandstone, Blocks.sandstone, false);

            for (int i = 1; i <= 9; i++) {
                this.fillWithBlocks(
                    world,
                    structureBoundingBox,
                    i,
                    i,
                    i,
                    this.scatteredFeatureSizeX - 1 - i,
                    i,
                    this.scatteredFeatureSizeZ - 1 - i,
                    Blocks.sandstone,
                    Blocks.sandstone,
                    false
                );
                this.fillWithBlocks(
                    world,
                    structureBoundingBox,
                    i + 1,
                    i,
                    i + 1,
                    this.scatteredFeatureSizeX - 2 - i,
                    i,
                    this.scatteredFeatureSizeZ - 2 - i,
                    Blocks.air,
                    Blocks.air,
                    false
                );
            }

            for (int i = 0; i < this.scatteredFeatureSizeX; i++) {
                for (int j = 0; j < this.scatteredFeatureSizeZ; j++) {
                    byte b = -5;
                    this.handleWorld(world, Blocks.sandstone, 0, i, b, j, structureBoundingBox);
                }
            }

            int metadataWithOffset = this.getMetadataWithOffset(Blocks.sandstone_stairs, 3);
            int metadataWithOffset2 = this.getMetadataWithOffset(Blocks.sandstone_stairs, 2);
            int metadataWithOffset3 = this.getMetadataWithOffset(Blocks.sandstone_stairs, 0);
            int metadataWithOffset4 = this.getMetadataWithOffset(Blocks.sandstone_stairs, 1);
            byte b = 1;
            byte b2 = 11;
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 4, 9, 4, Blocks.sandstone, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 10, 1, 3, 10, 3, Blocks.sandstone, Blocks.sandstone, false);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone_stairs, metadataWithOffset, 2, 10, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone_stairs, metadataWithOffset2, 2, 10, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone_stairs, metadataWithOffset3, 0, 10, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone_stairs, metadataWithOffset4, 4, 10, 2, structureBoundingBox);
            this.fillWithBlocks(world, structureBoundingBox, this.scatteredFeatureSizeX - 5, 0, 0, this.scatteredFeatureSizeX - 1, 9, 4, Blocks.sandstone, Blocks.air, false);
            this.fillWithBlocks(
                world, structureBoundingBox, this.scatteredFeatureSizeX - 4, 10, 1, this.scatteredFeatureSizeX - 2, 10, 3, Blocks.sandstone, Blocks.sandstone, false
            );
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone_stairs, metadataWithOffset, this.scatteredFeatureSizeX - 3, 10, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone_stairs, metadataWithOffset2, this.scatteredFeatureSizeX - 3, 10, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone_stairs, metadataWithOffset3, this.scatteredFeatureSizeX - 5, 10, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone_stairs, metadataWithOffset4, this.scatteredFeatureSizeX - 1, 10, 2, structureBoundingBox);
            this.fillWithBlocks(world, structureBoundingBox, 8, 0, 0, 12, 4, 4, Blocks.sandstone, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 9, 1, 0, 11, 3, 4, Blocks.air, Blocks.air, false);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, 9, 1, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, 9, 2, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, 9, 3, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, 10, 3, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, 11, 3, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, 11, 2, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, 11, 1, 1, structureBoundingBox);
            this.fillWithBlocks(world, structureBoundingBox, 4, 1, 1, 8, 3, 3, Blocks.sandstone, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 1, 2, 8, 2, 2, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 12, 1, 1, 16, 3, 3, Blocks.sandstone, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 12, 1, 2, 16, 2, 2, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 5, 4, 5, this.scatteredFeatureSizeX - 6, 4, this.scatteredFeatureSizeZ - 6, Blocks.sandstone, Blocks.sandstone, false);
            this.fillWithBlocks(world, structureBoundingBox, 9, 4, 9, 11, 4, 11, Blocks.air, Blocks.air, false);
            this.fillWithMetadataBlocks(world, structureBoundingBox, 8, 1, 8, 8, 3, 8, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
            this.fillWithMetadataBlocks(world, structureBoundingBox, 12, 1, 8, 12, 3, 8, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
            this.fillWithMetadataBlocks(world, structureBoundingBox, 8, 1, 12, 8, 3, 12, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
            this.fillWithMetadataBlocks(world, structureBoundingBox, 12, 1, 12, 12, 3, 12, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 1, 5, 4, 4, 11, Blocks.sandstone, Blocks.sandstone, false);
            this.fillWithBlocks(world, structureBoundingBox, this.scatteredFeatureSizeX - 5, 1, 5, this.scatteredFeatureSizeX - 2, 4, 11, Blocks.sandstone, Blocks.sandstone, false);
            this.fillWithBlocks(world, structureBoundingBox, 6, 7, 9, 6, 7, 11, Blocks.sandstone, Blocks.sandstone, false);
            this.fillWithBlocks(world, structureBoundingBox, this.scatteredFeatureSizeX - 7, 7, 9, this.scatteredFeatureSizeX - 7, 7, 11, Blocks.sandstone, Blocks.sandstone, false);
            this.fillWithMetadataBlocks(world, structureBoundingBox, 5, 5, 9, 5, 7, 11, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
            this.fillWithMetadataBlocks(
                world, structureBoundingBox, this.scatteredFeatureSizeX - 6, 5, 9, this.scatteredFeatureSizeX - 6, 7, 11, Blocks.sandstone, 2, Blocks.sandstone, 2, false
            );
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 5, 5, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 5, 6, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 6, 6, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, this.scatteredFeatureSizeX - 6, 5, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, this.scatteredFeatureSizeX - 6, 6, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, this.scatteredFeatureSizeX - 7, 6, 10, structureBoundingBox);
            this.fillWithBlocks(world, structureBoundingBox, 2, 4, 4, 2, 6, 4, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, this.scatteredFeatureSizeX - 3, 4, 4, this.scatteredFeatureSizeX - 3, 6, 4, Blocks.air, Blocks.air, false);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone_stairs, metadataWithOffset, 2, 4, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone_stairs, metadataWithOffset, 2, 3, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone_stairs, metadataWithOffset, this.scatteredFeatureSizeX - 3, 4, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone_stairs, metadataWithOffset, this.scatteredFeatureSizeX - 3, 3, 4, structureBoundingBox);
            this.fillWithBlocks(world, structureBoundingBox, 1, 1, 3, 2, 2, 3, Blocks.sandstone, Blocks.sandstone, false);
            this.fillWithBlocks(world, structureBoundingBox, this.scatteredFeatureSizeX - 3, 1, 3, this.scatteredFeatureSizeX - 2, 2, 3, Blocks.sandstone, Blocks.sandstone, false);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone_stairs, 0, 1, 1, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone_stairs, 0, this.scatteredFeatureSizeX - 2, 1, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 1, 1, 2, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 1, this.scatteredFeatureSizeX - 2, 2, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone_stairs, metadataWithOffset4, 2, 1, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone_stairs, metadataWithOffset3, this.scatteredFeatureSizeX - 3, 1, 2, structureBoundingBox);
            this.fillWithBlocks(world, structureBoundingBox, 4, 3, 5, 4, 3, 18, Blocks.sandstone, Blocks.sandstone, false);
            this.fillWithBlocks(world, structureBoundingBox, this.scatteredFeatureSizeX - 5, 3, 5, this.scatteredFeatureSizeX - 5, 3, 17, Blocks.sandstone, Blocks.sandstone, false);
            this.fillWithBlocks(world, structureBoundingBox, 3, 1, 5, 4, 2, 16, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, this.scatteredFeatureSizeX - 6, 1, 5, this.scatteredFeatureSizeX - 5, 2, 16, Blocks.air, Blocks.air, false);

            for (int i = 5; i <= 17; i += 2) {
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, 4, 1, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 1, 4, 2, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, this.scatteredFeatureSizeX - 5, 1, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 1, this.scatteredFeatureSizeX - 5, 2, i, structureBoundingBox);
            }

            this.placeBlockAtCurrentPosition(world, Blocks.wool, b, 10, 0, 7, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.wool, b, 10, 0, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.wool, b, 9, 0, 9, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.wool, b, 11, 0, 9, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.wool, b, 8, 0, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.wool, b, 12, 0, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.wool, b, 7, 0, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.wool, b, 13, 0, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.wool, b, 9, 0, 11, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.wool, b, 11, 0, 11, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.wool, b, 10, 0, 12, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.wool, b, 10, 0, 13, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.wool, b2, 10, 0, 10, structureBoundingBox);

            for (int i = 0; i <= this.scatteredFeatureSizeX - 1; i += this.scatteredFeatureSizeX - 1) {
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, i, 2, 1, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i, 2, 2, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, i, 2, 3, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, i, 3, 1, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i, 3, 2, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, i, 3, 3, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i, 4, 1, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 1, i, 4, 2, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i, 4, 3, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, i, 5, 1, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i, 5, 2, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, i, 5, 3, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i, 6, 1, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 1, i, 6, 2, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i, 6, 3, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i, 7, 1, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i, 7, 2, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i, 7, 3, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, i, 8, 1, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, i, 8, 2, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, i, 8, 3, structureBoundingBox);
            }

            for (int i = 2; i <= this.scatteredFeatureSizeX - 3; i += this.scatteredFeatureSizeX - 3 - 2) {
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, i - 1, 2, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i, 2, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, i + 1, 2, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, i - 1, 3, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i, 3, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, i + 1, 3, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i - 1, 4, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 1, i, 4, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i + 1, 4, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, i - 1, 5, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i, 5, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, i + 1, 5, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i - 1, 6, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 1, i, 6, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i + 1, 6, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i - 1, 7, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i, 7, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wool, b, i + 1, 7, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, i - 1, 8, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, i, 8, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, i + 1, 8, 0, structureBoundingBox);
            }

            this.fillWithMetadataBlocks(world, structureBoundingBox, 8, 4, 0, 12, 6, 0, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 8, 6, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 12, 6, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.wool, b, 9, 5, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 1, 10, 5, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.wool, b, 11, 5, 0, structureBoundingBox);
            this.fillWithMetadataBlocks(world, structureBoundingBox, 8, -14, 8, 12, -11, 12, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
            this.fillWithMetadataBlocks(world, structureBoundingBox, 8, -10, 8, 12, -10, 12, Blocks.sandstone, 1, Blocks.sandstone, 1, false);
            this.fillWithMetadataBlocks(world, structureBoundingBox, 8, -9, 8, 12, -9, 12, Blocks.sandstone, 2, Blocks.sandstone, 2, false);
            this.fillWithBlocks(world, structureBoundingBox, 8, -8, 8, 12, -1, 12, Blocks.sandstone, Blocks.sandstone, false);
            this.fillWithBlocks(world, structureBoundingBox, 9, -11, 9, 11, -1, 11, Blocks.air, Blocks.air, false);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_pressure_plate, 0, 10, -11, 10, structureBoundingBox);
            this.fillWithBlocks(world, structureBoundingBox, 9, -13, 9, 11, -13, 11, Blocks.tnt, Blocks.air, false);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 8, -11, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 8, -10, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 1, 7, -10, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, 7, -11, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 12, -11, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 12, -10, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 1, 13, -10, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, 13, -11, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 10, -11, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 10, -10, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 1, 10, -10, 7, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, 10, -11, 7, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 10, -11, 12, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 10, -10, 12, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 1, 10, -10, 13, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sandstone, 2, 10, -11, 13, structureBoundingBox);

            for (int i = 0; i < 4; i++) {
                if (!this.booleanArray[i]) {
                    int n = Direction.offsetX[i] * 2;
                    int n2 = Direction.offsetZ[i] * 2;
                    this.booleanArray[i] = this.generateStructureChestContents(
                        world,
                        structureBoundingBox,
                        random,
                        10 + n,
                        -11,
                        10 + n2,
                        WeightedRandomChestContent.union(itemsToGenerateInTemple, Items.enchanted_book.getLootTable(random)),
                        2 + random.nextInt(5)
                    );
                }
            }

            return true;
        }
    }

    public abstract static class Feature extends StructureComponent {
        public int intValue = -1;
        public int scatteredFeatureSizeX;
        public int scatteredFeatureSizeY;
        public int scatteredFeatureSizeZ;

        public Feature() {
        }

        public Feature(Random random, int n, int n2, int n3, int n4, int n5, int n6) {
            super(0);
            this.scatteredFeatureSizeX = n4;
            this.scatteredFeatureSizeY = n5;
            this.scatteredFeatureSizeZ = n6;
            this.coordBaseMode = random.nextInt(4);
            switch (this.coordBaseMode) {
                case 0:
                case 2:
                    this.boundingBox = new StructureBoundingBox(n, n2, n3, n + n4 - 1, n2 + n5 - 1, n3 + n6 - 1);
                    break;
                default:
                    this.boundingBox = new StructureBoundingBox(n, n2, n3, n + n6 - 1, n2 + n5 - 1, n3 + n4 - 1);
            }
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            nbttagCompound.setInteger("Width", this.scatteredFeatureSizeX);
            nbttagCompound.setInteger("Height", this.scatteredFeatureSizeY);
            nbttagCompound.setInteger("Depth", this.scatteredFeatureSizeZ);
            nbttagCompound.setInteger("HPos", this.intValue);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            this.scatteredFeatureSizeX = nbttagCompound.getInteger("Width");
            this.scatteredFeatureSizeY = nbttagCompound.getInteger("Height");
            this.scatteredFeatureSizeZ = nbttagCompound.getInteger("Depth");
            this.intValue = nbttagCompound.getInteger("HPos");
        }

        public boolean checkWorld(World world, StructureBoundingBox structureBoundingBox, int n) {
            if (this.intValue >= 0) {
                return true;
            }

            int n2 = 0;
            int n3 = 0;

            for (int minZ = this.boundingBox.minZ; minZ <= this.boundingBox.maxZ; minZ++) {
                for (int minX = this.boundingBox.minX; minX <= this.boundingBox.maxX; minX++) {
                    if (structureBoundingBox.isVecInside(minX, 64, minZ)) {
                        n2 += Math.max(world.getTopSolidOrLiquidBlock(minX, minZ), world.provider.getAverageGroundLevel());
                        n3++;
                    }
                }
            }

            if (n3 == 0) {
                return false;
            }

            this.intValue = n2 / n3;
            this.boundingBox.offset(0, this.intValue - this.boundingBox.minY + n, 0);
            return true;
        }
    }

    public static class JunglePyramid extends ComponentScatteredFeaturePieces.Feature {
        public boolean flag;
        public boolean flag2;
        public boolean flag3;
        public boolean flag4;
        public static WeightedRandomChestContent[] junglePyramidsChestContents = new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3),
            new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
            new WeightedRandomChestContent(Items.gold_ingot, 0, 2, 7, 15),
            new WeightedRandomChestContent(Items.emerald, 0, 1, 3, 2),
            new WeightedRandomChestContent(Items.bone, 0, 4, 6, 20),
            new WeightedRandomChestContent(Items.rotten_flesh, 0, 3, 7, 16),
            new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3),
            new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)
        };
        public static WeightedRandomChestContent[] junglePyramidsDispenserContents = new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Items.arrow, 0, 2, 7, 30)
        };
        public static ComponentScatteredFeaturePieces.JunglePyramid.Stones junglePyramidsRandomScatteredStones = new ComponentScatteredFeaturePieces.JunglePyramid.Stones(
            null
        );

        public JunglePyramid() {
        }

        public JunglePyramid(Random random, int n, int n2) {
            super(random, n, 64, n2, 12, 10, 15);
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setBoolean("placedMainChest", this.flag);
            nbttagCompound.setBoolean("placedHiddenChest", this.flag2);
            nbttagCompound.setBoolean("placedTrap1", this.flag3);
            nbttagCompound.setBoolean("placedTrap2", this.flag4);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.flag = nbttagCompound.getBoolean("placedMainChest");
            this.flag2 = nbttagCompound.getBoolean("placedHiddenChest");
            this.flag3 = nbttagCompound.getBoolean("placedTrap1");
            this.flag4 = nbttagCompound.getBoolean("placedTrap2");
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (!this.checkWorld(world, structureBoundingBox, 0)) {
                return false;
            }

            int metadataWithOffset = this.getMetadataWithOffset(Blocks.stone_stairs, 3);
            int metadataWithOffset2 = this.getMetadataWithOffset(Blocks.stone_stairs, 2);
            int metadataWithOffset3 = this.getMetadataWithOffset(Blocks.stone_stairs, 0);
            int metadataWithOffset4 = this.getMetadataWithOffset(Blocks.stone_stairs, 1);
            this.fillWithRandomizedBlocks(
                world, structureBoundingBox, 0, -4, 0, this.scatteredFeatureSizeX - 1, 0, this.scatteredFeatureSizeZ - 1, false, random, junglePyramidsRandomScatteredStones
            );
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 2, 1, 2, 9, 2, 2, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 2, 1, 12, 9, 2, 12, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 2, 1, 3, 2, 2, 11, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 9, 1, 3, 9, 2, 11, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 1, 3, 1, 10, 6, 1, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 1, 3, 13, 10, 6, 13, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 1, 3, 2, 1, 6, 12, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 10, 3, 2, 10, 6, 12, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 2, 3, 2, 9, 3, 12, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 2, 6, 2, 9, 6, 12, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 3, 7, 3, 8, 7, 11, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 4, 8, 4, 7, 8, 10, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithAir(world, structureBoundingBox, 3, 1, 3, 8, 2, 11);
            this.fillWithAir(world, structureBoundingBox, 4, 3, 6, 7, 3, 9);
            this.fillWithAir(world, structureBoundingBox, 2, 4, 2, 9, 5, 12);
            this.fillWithAir(world, structureBoundingBox, 4, 6, 5, 7, 6, 9);
            this.fillWithAir(world, structureBoundingBox, 5, 7, 6, 6, 7, 8);
            this.fillWithAir(world, structureBoundingBox, 5, 1, 2, 6, 2, 2);
            this.fillWithAir(world, structureBoundingBox, 5, 2, 12, 6, 2, 12);
            this.fillWithAir(world, structureBoundingBox, 5, 5, 1, 6, 5, 1);
            this.fillWithAir(world, structureBoundingBox, 5, 5, 13, 6, 5, 13);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 1, 5, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 10, 5, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 1, 5, 9, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 10, 5, 9, structureBoundingBox);

            for (int i = 0; i <= 14; i += 14) {
                this.fillWithRandomizedBlocks(world, structureBoundingBox, 2, 4, i, 2, 5, i, false, random, junglePyramidsRandomScatteredStones);
                this.fillWithRandomizedBlocks(world, structureBoundingBox, 4, 4, i, 4, 5, i, false, random, junglePyramidsRandomScatteredStones);
                this.fillWithRandomizedBlocks(world, structureBoundingBox, 7, 4, i, 7, 5, i, false, random, junglePyramidsRandomScatteredStones);
                this.fillWithRandomizedBlocks(world, structureBoundingBox, 9, 4, i, 9, 5, i, false, random, junglePyramidsRandomScatteredStones);
            }

            this.fillWithRandomizedBlocks(world, structureBoundingBox, 5, 6, 0, 6, 6, 0, false, random, junglePyramidsRandomScatteredStones);

            for (int i = 0; i <= 11; i += 11) {
                for (int j = 2; j <= 12; j += 2) {
                    this.fillWithRandomizedBlocks(world, structureBoundingBox, i, 4, j, i, 5, j, false, random, junglePyramidsRandomScatteredStones);
                }

                this.fillWithRandomizedBlocks(world, structureBoundingBox, i, 6, 5, i, 6, 5, false, random, junglePyramidsRandomScatteredStones);
                this.fillWithRandomizedBlocks(world, structureBoundingBox, i, 6, 9, i, 6, 9, false, random, junglePyramidsRandomScatteredStones);
            }

            this.fillWithRandomizedBlocks(world, structureBoundingBox, 2, 7, 2, 2, 9, 2, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 9, 7, 2, 9, 9, 2, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 2, 7, 12, 2, 9, 12, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 9, 7, 12, 9, 9, 12, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 4, 9, 4, 4, 9, 4, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 7, 9, 4, 7, 9, 4, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 4, 9, 10, 4, 9, 10, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 7, 9, 10, 7, 9, 10, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 5, 9, 7, 6, 9, 7, false, random, junglePyramidsRandomScatteredStones);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset, 5, 9, 6, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset, 6, 9, 6, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset2, 5, 9, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset2, 6, 9, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset, 4, 0, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset, 5, 0, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset, 6, 0, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset, 7, 0, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset, 4, 1, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset, 4, 2, 9, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset, 4, 3, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset, 7, 1, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset, 7, 2, 9, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset, 7, 3, 10, structureBoundingBox);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 4, 1, 9, 4, 1, 9, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 7, 1, 9, 7, 1, 9, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 4, 1, 10, 7, 2, 10, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 5, 4, 5, 6, 4, 5, false, random, junglePyramidsRandomScatteredStones);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset3, 4, 4, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset4, 7, 4, 5, structureBoundingBox);

            for (int i = 0; i < 4; i++) {
                this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset2, 5, 0 - i, 6 + i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset2, 6, 0 - i, 6 + i, structureBoundingBox);
                this.fillWithAir(world, structureBoundingBox, 5, 0 - i, 7 + i, 6, 0 - i, 9 + i);
            }

            this.fillWithAir(world, structureBoundingBox, 1, -3, 12, 10, -1, 13);
            this.fillWithAir(world, structureBoundingBox, 1, -3, 1, 3, -1, 13);
            this.fillWithAir(world, structureBoundingBox, 1, -3, 1, 9, -1, 5);

            for (int i = 1; i <= 13; i += 2) {
                this.fillWithRandomizedBlocks(world, structureBoundingBox, 1, -3, i, 1, -2, i, false, random, junglePyramidsRandomScatteredStones);
            }

            for (int i = 2; i <= 12; i += 2) {
                this.fillWithRandomizedBlocks(world, structureBoundingBox, 1, -1, i, 3, -1, i, false, random, junglePyramidsRandomScatteredStones);
            }

            this.fillWithRandomizedBlocks(world, structureBoundingBox, 2, -2, 1, 5, -2, 1, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 7, -2, 1, 9, -2, 1, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 6, -3, 1, 6, -3, 1, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 6, -1, 1, 6, -1, 1, false, random, junglePyramidsRandomScatteredStones);
            this.placeBlockAtCurrentPosition(world, Blocks.tripwire_hook, this.getMetadataWithOffset(Blocks.tripwire_hook, 3) | 4, 1, -3, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.tripwire_hook, this.getMetadataWithOffset(Blocks.tripwire_hook, 1) | 4, 4, -3, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.tripwire, 4, 2, -3, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.tripwire, 4, 3, -3, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, 5, -3, 7, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, 5, -3, 6, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, 5, -3, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, 5, -3, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, 5, -3, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, 5, -3, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, 5, -3, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, 4, -3, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.mossy_cobblestone, 0, 3, -3, 1, structureBoundingBox);
            if (!this.flag3) {
                this.flag3 = this.generateStructureDispenserContents(world, structureBoundingBox, random, 3, -2, 1, 2, junglePyramidsDispenserContents, 2);
            }

            this.placeBlockAtCurrentPosition(world, Blocks.vine, 15, 3, -2, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.tripwire_hook, this.getMetadataWithOffset(Blocks.tripwire_hook, 2) | 4, 7, -3, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.tripwire_hook, this.getMetadataWithOffset(Blocks.tripwire_hook, 0) | 4, 7, -3, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.tripwire, 4, 7, -3, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.tripwire, 4, 7, -3, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.tripwire, 4, 7, -3, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, 8, -3, 6, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, 9, -3, 6, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, 9, -3, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.mossy_cobblestone, 0, 9, -3, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, 9, -2, 4, structureBoundingBox);
            if (!this.flag4) {
                this.flag4 = this.generateStructureDispenserContents(world, structureBoundingBox, random, 9, -2, 3, 4, junglePyramidsDispenserContents, 2);
            }

            this.placeBlockAtCurrentPosition(world, Blocks.vine, 15, 8, -1, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.vine, 15, 8, -2, 3, structureBoundingBox);
            if (!this.flag) {
                this.flag = this.generateStructureChestContents(
                    world,
                    structureBoundingBox,
                    random,
                    8,
                    -3,
                    3,
                    WeightedRandomChestContent.union(junglePyramidsChestContents, Items.enchanted_book.getLootTable(random)),
                    2 + random.nextInt(5)
                );
            }

            this.placeBlockAtCurrentPosition(world, Blocks.mossy_cobblestone, 0, 9, -3, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.mossy_cobblestone, 0, 8, -3, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.mossy_cobblestone, 0, 4, -3, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.mossy_cobblestone, 0, 5, -2, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.mossy_cobblestone, 0, 5, -1, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.mossy_cobblestone, 0, 6, -3, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.mossy_cobblestone, 0, 7, -2, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.mossy_cobblestone, 0, 7, -1, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.mossy_cobblestone, 0, 8, -3, 5, structureBoundingBox);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 9, -1, 1, 9, -1, 5, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithAir(world, structureBoundingBox, 8, -3, 8, 10, -1, 10);
            this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 3, 8, -2, 11, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 3, 9, -2, 11, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 3, 10, -2, 11, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.lever, BlockLever.invertMetadata(this.getMetadataWithOffset(Blocks.lever, 2)), 8, -2, 12, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.lever, BlockLever.invertMetadata(this.getMetadataWithOffset(Blocks.lever, 2)), 9, -2, 12, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.lever, BlockLever.invertMetadata(this.getMetadataWithOffset(Blocks.lever, 2)), 10, -2, 12, structureBoundingBox);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 8, -3, 8, 8, -3, 10, false, random, junglePyramidsRandomScatteredStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 10, -3, 8, 10, -3, 10, false, random, junglePyramidsRandomScatteredStones);
            this.placeBlockAtCurrentPosition(world, Blocks.mossy_cobblestone, 0, 10, -2, 9, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, 8, -2, 9, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, 8, -2, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, 10, -1, 9, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sticky_piston, 1, 9, -2, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sticky_piston, this.getMetadataWithOffset(Blocks.sticky_piston, 4), 10, -2, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.sticky_piston, this.getMetadataWithOffset(Blocks.sticky_piston, 4), 10, -1, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.unpowered_repeater, this.getMetadataWithOffset(Blocks.unpowered_repeater, 2), 10, -2, 10, structureBoundingBox);
            if (!this.flag2) {
                this.flag2 = this.generateStructureChestContents(
                    world,
                    structureBoundingBox,
                    random,
                    9,
                    -3,
                    10,
                    WeightedRandomChestContent.union(junglePyramidsChestContents, Items.enchanted_book.getLootTable(random)),
                    2 + random.nextInt(5)
                );
            }

            return true;
        }

        public static class Stones extends BlockSelector {
            public Stones() {
            }

            @Override
            public void selectBlocks(Random random, int n, int n2, int n3, boolean flag) {
                if (random.nextFloat() < 0.4F) {
                    this.block = Blocks.cobblestone;
                } else {
                    this.block = Blocks.mossy_cobblestone;
                }
            }

            public Stones(Object obj) {
                this();
            }
        }
    }

    public static class SwampHut extends ComponentScatteredFeaturePieces.Feature {
        public boolean hasWitch;

        public SwampHut() {
        }

        public SwampHut(Random random, int n, int n2) {
            super(random, n, 64, n2, 7, 5, 9);
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setBoolean("Witch", this.hasWitch);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.hasWitch = nbttagCompound.getBoolean("Witch");
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (!this.checkWorld(world, structureBoundingBox, 0)) {
                return false;
            }

            this.fillWithMetadataBlocks(world, structureBoundingBox, 1, 1, 1, 5, 1, 7, Blocks.planks, 1, Blocks.planks, 1, false);
            this.fillWithMetadataBlocks(world, structureBoundingBox, 1, 4, 2, 5, 4, 7, Blocks.planks, 1, Blocks.planks, 1, false);
            this.fillWithMetadataBlocks(world, structureBoundingBox, 2, 1, 0, 4, 1, 0, Blocks.planks, 1, Blocks.planks, 1, false);
            this.fillWithMetadataBlocks(world, structureBoundingBox, 2, 2, 2, 3, 3, 2, Blocks.planks, 1, Blocks.planks, 1, false);
            this.fillWithMetadataBlocks(world, structureBoundingBox, 1, 2, 3, 1, 3, 6, Blocks.planks, 1, Blocks.planks, 1, false);
            this.fillWithMetadataBlocks(world, structureBoundingBox, 5, 2, 3, 5, 3, 6, Blocks.planks, 1, Blocks.planks, 1, false);
            this.fillWithMetadataBlocks(world, structureBoundingBox, 2, 2, 7, 4, 3, 7, Blocks.planks, 1, Blocks.planks, 1, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 0, 2, 1, 3, 2, Blocks.log, Blocks.log, false);
            this.fillWithBlocks(world, structureBoundingBox, 5, 0, 2, 5, 3, 2, Blocks.log, Blocks.log, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 0, 7, 1, 3, 7, Blocks.log, Blocks.log, false);
            this.fillWithBlocks(world, structureBoundingBox, 5, 0, 7, 5, 3, 7, Blocks.log, Blocks.log, false);
            this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 2, 3, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 3, 3, 7, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 1, 3, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 5, 3, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 5, 3, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.flower_pot, 7, 1, 3, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.crafting_table, 0, 3, 2, 6, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cauldron, 0, 4, 2, 6, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 1, 2, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 5, 2, 1, structureBoundingBox);
            int metadataWithOffset = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
            int metadataWithOffset2 = this.getMetadataWithOffset(Blocks.oak_stairs, 1);
            int metadataWithOffset3 = this.getMetadataWithOffset(Blocks.oak_stairs, 0);
            int metadataWithOffset4 = this.getMetadataWithOffset(Blocks.oak_stairs, 2);
            this.fillWithMetadataBlocks(world, structureBoundingBox, 0, 4, 1, 6, 4, 1, Blocks.spruce_stairs, metadataWithOffset, Blocks.spruce_stairs, metadataWithOffset, false);
            this.fillWithMetadataBlocks(world, structureBoundingBox, 0, 4, 2, 0, 4, 7, Blocks.spruce_stairs, metadataWithOffset3, Blocks.spruce_stairs, metadataWithOffset3, false);
            this.fillWithMetadataBlocks(world, structureBoundingBox, 6, 4, 2, 6, 4, 7, Blocks.spruce_stairs, metadataWithOffset2, Blocks.spruce_stairs, metadataWithOffset2, false);
            this.fillWithMetadataBlocks(world, structureBoundingBox, 0, 4, 8, 6, 4, 8, Blocks.spruce_stairs, metadataWithOffset4, Blocks.spruce_stairs, metadataWithOffset4, false);

            for (int i = 2; i <= 7; i += 5) {
                for (int j = 1; j <= 5; j += 4) {
                    this.handleWorld(world, Blocks.log, 0, j, -1, i, structureBoundingBox);
                }
            }

            if (!this.hasWitch) {
                int xWithOffset = this.getXWithOffset(2, 5);
                int yWithOffset = this.getYWithOffset(2);
                int zWithOffset = this.getZWithOffset(2, 5);
                if (structureBoundingBox.isVecInside(xWithOffset, yWithOffset, zWithOffset)) {
                    this.hasWitch = true;
                    EntityWitch entityWitch = new EntityWitch(world);
                    entityWitch.setLocationAndAngles(xWithOffset + 0.5, yWithOffset, zWithOffset + 0.5, 0.0F, 0.0F);
                    entityWitch.onSpawnWithEgg(null);
                    world.spawnEntityInWorld(entityWitch);
                }
            }

            return true;
        }
    }
}
