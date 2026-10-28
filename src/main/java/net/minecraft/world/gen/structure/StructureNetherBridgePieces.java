package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureNetherBridgePieces {
    public static StructureNetherBridgePieces.PieceWeight[] primaryComponents = new StructureNetherBridgePieces.PieceWeight[]{
        new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Straight.class, 30, 0, true),
        new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Crossing3.class, 10, 4),
        new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Crossing.class, 10, 4),
        new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Stairs.class, 10, 3),
        new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Throne.class, 5, 2),
        new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Entrance.class, 5, 1)
    };
    public static StructureNetherBridgePieces.PieceWeight[] secondaryComponents = new StructureNetherBridgePieces.PieceWeight[]{
        new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor5.class, 25, 0, true),
        new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Crossing2.class, 15, 5),
        new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor2.class, 5, 10),
        new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor.class, 5, 10),
        new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor3.class, 10, 3, true),
        new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor4.class, 7, 2),
        new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.NetherStalkRoom.class, 5, 2)
    };

    public static void registerNetherFortressPieces() {
        MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Crossing3.class, "NeBCr");
        MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.End.class, "NeBEF");
        MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Straight.class, "NeBS");
        MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Corridor3.class, "NeCCS");
        MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Corridor4.class, "NeCTB");
        MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Entrance.class, "NeCE");
        MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Crossing2.class, "NeSCSC");
        MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Corridor.class, "NeSCLT");
        MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Corridor5.class, "NeSC");
        MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Corridor2.class, "NeSCRT");
        MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.NetherStalkRoom.class, "NeCSR");
        MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Throne.class, "NeMT");
        MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Crossing.class, "NeRC");
        MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Stairs.class, "NeSR");
        MapGenStructureIO.registerStructureComponent(StructureNetherBridgePieces.Start.class, "NeStart");
    }

    public static StructureNetherBridgePieces.Piece createNextComponentRandom(
        StructureNetherBridgePieces.PieceWeight pieceWeight, List list, Random random, int n, int n2, int n3, int n4, int n5
    ) {
        Class weightClass = pieceWeight.weightClass;
        StructureNetherBridgePieces.Piece piece = null;
        if (weightClass == StructureNetherBridgePieces.Straight.class) {
            piece = StructureNetherBridgePieces.Straight.createValidComponent(list, random, n, n2, n3, n4, n5);
        } else if (weightClass == StructureNetherBridgePieces.Crossing3.class) {
            piece = StructureNetherBridgePieces.Crossing3.createValidComponent(list, random, n, n2, n3, n4, n5);
        } else if (weightClass == StructureNetherBridgePieces.Crossing.class) {
            piece = StructureNetherBridgePieces.Crossing.createValidComponent(list, random, n, n2, n3, n4, n5);
        } else if (weightClass == StructureNetherBridgePieces.Stairs.class) {
            piece = StructureNetherBridgePieces.Stairs.createValidComponent(list, random, n, n2, n3, n4, n5);
        } else if (weightClass == StructureNetherBridgePieces.Throne.class) {
            piece = StructureNetherBridgePieces.Throne.createValidComponent(list, random, n, n2, n3, n4, n5);
        } else if (weightClass == StructureNetherBridgePieces.Entrance.class) {
            piece = StructureNetherBridgePieces.Entrance.createValidComponent(list, random, n, n2, n3, n4, n5);
        } else if (weightClass == StructureNetherBridgePieces.Corridor5.class) {
            piece = StructureNetherBridgePieces.Corridor5.createValidComponent(list, random, n, n2, n3, n4, n5);
        } else if (weightClass == StructureNetherBridgePieces.Corridor2.class) {
            piece = StructureNetherBridgePieces.Corridor2.createValidComponent(list, random, n, n2, n3, n4, n5);
        } else if (weightClass == StructureNetherBridgePieces.Corridor.class) {
            piece = StructureNetherBridgePieces.Corridor.createValidComponent(list, random, n, n2, n3, n4, n5);
        } else if (weightClass == StructureNetherBridgePieces.Corridor3.class) {
            piece = StructureNetherBridgePieces.Corridor3.createValidComponent(list, random, n, n2, n3, n4, n5);
        } else if (weightClass == StructureNetherBridgePieces.Corridor4.class) {
            piece = StructureNetherBridgePieces.Corridor4.createValidComponent(list, random, n, n2, n3, n4, n5);
        } else if (weightClass == StructureNetherBridgePieces.Crossing2.class) {
            piece = StructureNetherBridgePieces.Crossing2.createValidComponent(list, random, n, n2, n3, n4, n5);
        } else if (weightClass == StructureNetherBridgePieces.NetherStalkRoom.class) {
            piece = StructureNetherBridgePieces.NetherStalkRoom.createValidComponent(list, random, n, n2, n3, n4, n5);
        }

        return piece;
    }

    public static class Corridor extends StructureNetherBridgePieces.Piece {
        public boolean flag;

        public Corridor() {
        }

        public Corridor(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
            this.flag = random.nextInt(3) == 0;
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.flag = nbttagCompound.getBoolean("Chest");
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setBoolean("Chest", this.flag);
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            this.getNextComponentX((StructureNetherBridgePieces.Start)structureComponent, list, random, 0, 1, true);
        }

        public static StructureNetherBridgePieces.Corridor createValidComponent(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, 0, 0, 5, 7, 5, n4);
            return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureNetherBridgePieces.Corridor(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 4, 1, 4, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 0, 4, 5, 4, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 2, 0, 4, 5, 4, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 3, 1, 4, 4, 1, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 3, 3, 4, 4, 3, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 0, 0, 5, 0, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 4, 3, 5, 4, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 3, 4, 1, 4, 4, Blocks.nether_brick_fence, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 3, 3, 4, 3, 4, 4, Blocks.nether_brick_fence, Blocks.nether_brick, false);
            if (this.flag) {
                int yWithOffset = this.getYWithOffset(2);
                int xWithOffset = this.getXWithOffset(3, 3);
                int zWithOffset = this.getZWithOffset(3, 3);
                if (structureBoundingBox.isVecInside(xWithOffset, yWithOffset, zWithOffset)) {
                    this.flag = false;
                    this.generateStructureChestContents(world, structureBoundingBox, random, 3, 2, 3, weightedRandomChestContentArray, 2 + random.nextInt(4));
                }
            }

            this.fillWithBlocks(world, structureBoundingBox, 0, 6, 0, 4, 6, 4, Blocks.nether_brick, Blocks.nether_brick, false);

            for (int i = 0; i <= 4; i++) {
                for (int j = 0; j <= 4; j++) {
                    this.handleWorld(world, Blocks.nether_brick, 0, i, -1, j, structureBoundingBox);
                }
            }

            return true;
        }
    }

    public static class Corridor2 extends StructureNetherBridgePieces.Piece {
        public boolean flag;

        public Corridor2() {
        }

        public Corridor2(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
            this.flag = random.nextInt(3) == 0;
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.flag = nbttagCompound.getBoolean("Chest");
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setBoolean("Chest", this.flag);
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            this.getNextComponentZ((StructureNetherBridgePieces.Start)structureComponent, list, random, 0, 1, true);
        }

        public static StructureNetherBridgePieces.Corridor2 createValidComponent(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, 0, 0, 5, 7, 5, n4);
            return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureNetherBridgePieces.Corridor2(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 4, 1, 4, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 0, 4, 5, 4, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 0, 0, 5, 4, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 3, 1, 0, 4, 1, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 3, 3, 0, 4, 3, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 2, 0, 4, 5, 0, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 2, 4, 4, 5, 4, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 3, 4, 1, 4, 4, Blocks.nether_brick_fence, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 3, 3, 4, 3, 4, 4, Blocks.nether_brick_fence, Blocks.nether_brick, false);
            if (this.flag) {
                int yWithOffset = this.getYWithOffset(2);
                int xWithOffset = this.getXWithOffset(1, 3);
                int zWithOffset = this.getZWithOffset(1, 3);
                if (structureBoundingBox.isVecInside(xWithOffset, yWithOffset, zWithOffset)) {
                    this.flag = false;
                    this.generateStructureChestContents(world, structureBoundingBox, random, 1, 2, 3, weightedRandomChestContentArray, 2 + random.nextInt(4));
                }
            }

            this.fillWithBlocks(world, structureBoundingBox, 0, 6, 0, 4, 6, 4, Blocks.nether_brick, Blocks.nether_brick, false);

            for (int i = 0; i <= 4; i++) {
                for (int j = 0; j <= 4; j++) {
                    this.handleWorld(world, Blocks.nether_brick, 0, i, -1, j, structureBoundingBox);
                }
            }

            return true;
        }
    }

    public static class Corridor3 extends StructureNetherBridgePieces.Piece {
        public Corridor3() {
        }

        public Corridor3(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            this.getNextComponentNormal((StructureNetherBridgePieces.Start)structureComponent, list, random, 1, 0, true);
        }

        public static StructureNetherBridgePieces.Corridor3 createValidComponent(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -7, 0, 5, 14, 10, n4);
            return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureNetherBridgePieces.Corridor3(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            int metadataWithOffset = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 2);

            for (int i = 0; i <= 9; i++) {
                int max = Math.max(1, 7 - i);
                int min = Math.min(Math.max(max + 5, 14 - i), 13);
                int n = i;
                this.fillWithBlocks(world, structureBoundingBox, 0, 0, i, 4, max, i, Blocks.nether_brick, Blocks.nether_brick, false);
                this.fillWithBlocks(world, structureBoundingBox, 1, max + 1, i, 3, min - 1, i, Blocks.air, Blocks.air, false);
                if (i <= 6) {
                    this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, metadataWithOffset, 1, max + 1, i, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, metadataWithOffset, 2, max + 1, i, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, metadataWithOffset, 3, max + 1, i, structureBoundingBox);
                }

                this.fillWithBlocks(world, structureBoundingBox, 0, min, i, 4, min, i, Blocks.nether_brick, Blocks.nether_brick, false);
                this.fillWithBlocks(world, structureBoundingBox, 0, max + 1, i, 0, min - 1, i, Blocks.nether_brick, Blocks.nether_brick, false);
                this.fillWithBlocks(world, structureBoundingBox, 4, max + 1, i, 4, min - 1, i, Blocks.nether_brick, Blocks.nether_brick, false);
                if ((i & 1) == 0) {
                    this.fillWithBlocks(world, structureBoundingBox, 0, max + 2, i, 0, max + 3, i, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                    this.fillWithBlocks(world, structureBoundingBox, 4, max + 2, i, 4, max + 3, i, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                }

                for (int j = 0; j <= 4; j++) {
                    this.handleWorld(world, Blocks.nether_brick, 0, j, -1, n, structureBoundingBox);
                }
            }

            return true;
        }
    }

    public static class Corridor4 extends StructureNetherBridgePieces.Piece {
        public Corridor4() {
        }

        public Corridor4(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            byte b = 1;
            if (this.coordBaseMode == 1 || this.coordBaseMode == 2) {
                b = 5;
            }

            this.getNextComponentX((StructureNetherBridgePieces.Start)structureComponent, list, random, 0, b, random.nextInt(8) > 0);
            this.getNextComponentZ((StructureNetherBridgePieces.Start)structureComponent, list, random, 0, b, random.nextInt(8) > 0);
        }

        public static StructureNetherBridgePieces.Corridor4 createValidComponent(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -3, 0, 0, 9, 7, 9, n4);
            return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureNetherBridgePieces.Corridor4(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 8, 1, 8, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 0, 8, 5, 8, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 6, 0, 8, 6, 5, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 0, 2, 5, 0, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 6, 2, 0, 8, 5, 0, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 3, 0, 1, 4, 0, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 7, 3, 0, 7, 4, 0, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 4, 8, 2, 8, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 1, 4, 2, 2, 4, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 6, 1, 4, 7, 2, 4, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 3, 8, 8, 3, 8, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 3, 6, 0, 3, 7, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 8, 3, 6, 8, 3, 7, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 3, 4, 0, 5, 5, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 8, 3, 4, 8, 5, 5, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 3, 5, 2, 5, 5, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 6, 3, 5, 7, 5, 5, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 4, 5, 1, 5, 5, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 7, 4, 5, 7, 5, 5, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);

            for (int i = 0; i <= 5; i++) {
                for (int j = 0; j <= 8; j++) {
                    this.handleWorld(world, Blocks.nether_brick, 0, j, -1, i, structureBoundingBox);
                }
            }

            return true;
        }
    }

    public static class Corridor5 extends StructureNetherBridgePieces.Piece {
        public Corridor5() {
        }

        public Corridor5(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            this.getNextComponentNormal((StructureNetherBridgePieces.Start)structureComponent, list, random, 1, 0, true);
        }

        public static StructureNetherBridgePieces.Corridor5 createValidComponent(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, 0, 0, 5, 7, 5, n4);
            return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureNetherBridgePieces.Corridor5(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 4, 1, 4, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 0, 4, 5, 4, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 0, 0, 5, 4, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 2, 0, 4, 5, 4, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 3, 1, 0, 4, 1, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 3, 3, 0, 4, 3, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 3, 1, 4, 4, 1, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 3, 3, 4, 4, 3, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 6, 0, 4, 6, 4, Blocks.nether_brick, Blocks.nether_brick, false);

            for (int i = 0; i <= 4; i++) {
                for (int j = 0; j <= 4; j++) {
                    this.handleWorld(world, Blocks.nether_brick, 0, i, -1, j, structureBoundingBox);
                }
            }

            return true;
        }
    }

    public static class Crossing extends StructureNetherBridgePieces.Piece {
        public Crossing() {
        }

        public Crossing(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            this.getNextComponentNormal((StructureNetherBridgePieces.Start)structureComponent, list, random, 2, 0, false);
            this.getNextComponentX((StructureNetherBridgePieces.Start)structureComponent, list, random, 0, 2, false);
            this.getNextComponentZ((StructureNetherBridgePieces.Start)structureComponent, list, random, 0, 2, false);
        }

        public static StructureNetherBridgePieces.Crossing createValidComponent(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -2, 0, 0, 7, 9, 7, n4);
            return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureNetherBridgePieces.Crossing(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 6, 1, 6, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 0, 6, 7, 6, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 0, 1, 6, 0, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 6, 1, 6, 6, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 5, 2, 0, 6, 6, 0, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 5, 2, 6, 6, 6, 6, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 0, 0, 6, 1, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 5, 0, 6, 6, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 6, 2, 0, 6, 6, 1, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 6, 2, 5, 6, 6, 6, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 6, 0, 4, 6, 0, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 5, 0, 4, 5, 0, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 6, 6, 4, 6, 6, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 5, 6, 4, 5, 6, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 6, 2, 0, 6, 4, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 5, 2, 0, 5, 4, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 6, 6, 2, 6, 6, 4, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 6, 5, 2, 6, 5, 4, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);

            for (int i = 0; i <= 6; i++) {
                for (int j = 0; j <= 6; j++) {
                    this.handleWorld(world, Blocks.nether_brick, 0, i, -1, j, structureBoundingBox);
                }
            }

            return true;
        }
    }

    public static class Crossing2 extends StructureNetherBridgePieces.Piece {
        public Crossing2() {
        }

        public Crossing2(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            this.getNextComponentNormal((StructureNetherBridgePieces.Start)structureComponent, list, random, 1, 0, true);
            this.getNextComponentX((StructureNetherBridgePieces.Start)structureComponent, list, random, 0, 1, true);
            this.getNextComponentZ((StructureNetherBridgePieces.Start)structureComponent, list, random, 0, 1, true);
        }

        public static StructureNetherBridgePieces.Crossing2 createValidComponent(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, 0, 0, 5, 7, 5, n4);
            return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureNetherBridgePieces.Crossing2(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 4, 1, 4, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 0, 4, 5, 4, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 0, 0, 5, 0, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 2, 0, 4, 5, 0, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 4, 0, 5, 4, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 2, 4, 4, 5, 4, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 6, 0, 4, 6, 4, Blocks.nether_brick, Blocks.nether_brick, false);

            for (int i = 0; i <= 4; i++) {
                for (int j = 0; j <= 4; j++) {
                    this.handleWorld(world, Blocks.nether_brick, 0, i, -1, j, structureBoundingBox);
                }
            }

            return true;
        }
    }

    public static class Crossing3 extends StructureNetherBridgePieces.Piece {
        public Crossing3() {
        }

        public Crossing3(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
        }

        public Crossing3(Random random, int n, int n2) {
            super(0);
            this.coordBaseMode = random.nextInt(4);
            switch (this.coordBaseMode) {
                case 0:
                case 2:
                    this.boundingBox = new StructureBoundingBox(n, 64, n2, n + 19 - 1, 73, n2 + 19 - 1);
                    break;
                default:
                    this.boundingBox = new StructureBoundingBox(n, 64, n2, n + 19 - 1, 73, n2 + 19 - 1);
            }
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            this.getNextComponentNormal((StructureNetherBridgePieces.Start)structureComponent, list, random, 8, 3, false);
            this.getNextComponentX((StructureNetherBridgePieces.Start)structureComponent, list, random, 3, 8, false);
            this.getNextComponentZ((StructureNetherBridgePieces.Start)structureComponent, list, random, 3, 8, false);
        }

        public static StructureNetherBridgePieces.Crossing3 createValidComponent(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -8, -3, 0, 19, 10, 19, n4);
            return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureNetherBridgePieces.Crossing3(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            this.fillWithBlocks(world, structureBoundingBox, 7, 3, 0, 11, 4, 18, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 3, 7, 18, 4, 11, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 8, 5, 0, 10, 7, 18, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 5, 8, 18, 7, 10, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 7, 5, 0, 7, 5, 7, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 7, 5, 11, 7, 5, 18, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 11, 5, 0, 11, 5, 7, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 11, 5, 11, 11, 5, 18, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 5, 7, 7, 5, 7, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 11, 5, 7, 18, 5, 7, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 5, 11, 7, 5, 11, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 11, 5, 11, 18, 5, 11, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 7, 2, 0, 11, 2, 5, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 7, 2, 13, 11, 2, 18, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 7, 0, 0, 11, 1, 3, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 7, 0, 15, 11, 1, 18, Blocks.nether_brick, Blocks.nether_brick, false);

            for (int i = 7; i <= 11; i++) {
                for (int j = 0; j <= 2; j++) {
                    this.handleWorld(world, Blocks.nether_brick, 0, i, -1, j, structureBoundingBox);
                    this.handleWorld(world, Blocks.nether_brick, 0, i, -1, 18 - j, structureBoundingBox);
                }
            }

            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 7, 5, 2, 11, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 13, 2, 7, 18, 2, 11, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 7, 3, 1, 11, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 15, 0, 7, 18, 1, 11, Blocks.nether_brick, Blocks.nether_brick, false);

            for (int i = 0; i <= 2; i++) {
                for (int j = 7; j <= 11; j++) {
                    this.handleWorld(world, Blocks.nether_brick, 0, i, -1, j, structureBoundingBox);
                    this.handleWorld(world, Blocks.nether_brick, 0, 18 - i, -1, j, structureBoundingBox);
                }
            }

            return true;
        }
    }

    public static class End extends StructureNetherBridgePieces.Piece {
        public int fillSeed;

        public End() {
        }

        public End(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
            this.fillSeed = random.nextInt();
        }

        public static StructureNetherBridgePieces.End createEnd(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -3, 0, 5, 10, 8, n4);
            return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureNetherBridgePieces.End(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.fillSeed = nbttagCompound.getInteger("Seed");
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setInteger("Seed", this.fillSeed);
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            Random random2 = new Random(this.fillSeed);

            for (int i = 0; i <= 4; i++) {
                for (int j = 3; j <= 4; j++) {
                    int nextInt = random2.nextInt(8);
                    this.fillWithBlocks(world, structureBoundingBox, i, j, 0, i, j, nextInt, Blocks.nether_brick, Blocks.nether_brick, false);
                }
            }

            int nextInt = random2.nextInt(8);
            this.fillWithBlocks(world, structureBoundingBox, 0, 5, 0, 0, 5, nextInt, Blocks.nether_brick, Blocks.nether_brick, false);
            nextInt = random2.nextInt(8);
            this.fillWithBlocks(world, structureBoundingBox, 4, 5, 0, 4, 5, nextInt, Blocks.nether_brick, Blocks.nether_brick, false);

            for (int i = 0; i <= 4; i++) {
                int nextInt2 = random2.nextInt(5);
                this.fillWithBlocks(world, structureBoundingBox, i, 2, 0, i, 2, nextInt2, Blocks.nether_brick, Blocks.nether_brick, false);
            }

            for (int i = 0; i <= 4; i++) {
                for (int j = 0; j <= 1; j++) {
                    int nextInt2 = random2.nextInt(3);
                    this.fillWithBlocks(world, structureBoundingBox, i, j, 0, i, j, nextInt2, Blocks.nether_brick, Blocks.nether_brick, false);
                }
            }

            return true;
        }
    }

    public static class Entrance extends StructureNetherBridgePieces.Piece {
        public Entrance() {
        }

        public Entrance(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            this.getNextComponentNormal((StructureNetherBridgePieces.Start)structureComponent, list, random, 5, 3, true);
        }

        public static StructureNetherBridgePieces.Entrance createValidComponent(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -5, -3, 0, 13, 14, 13, n4);
            return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureNetherBridgePieces.Entrance(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            this.fillWithBlocks(world, structureBoundingBox, 0, 3, 0, 12, 4, 12, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 5, 0, 12, 13, 12, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 5, 0, 1, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 11, 5, 0, 12, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 5, 11, 4, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 8, 5, 11, 10, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 5, 9, 11, 7, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 5, 0, 4, 12, 1, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 8, 5, 0, 10, 12, 1, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 5, 9, 0, 7, 12, 1, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 11, 2, 10, 12, 10, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 5, 8, 0, 7, 8, 0, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);

            for (int i = 1; i <= 11; i += 2) {
                this.fillWithBlocks(world, structureBoundingBox, i, 10, 0, i, 11, 0, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.fillWithBlocks(world, structureBoundingBox, i, 10, 12, i, 11, 12, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.fillWithBlocks(world, structureBoundingBox, 0, 10, i, 0, 11, i, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.fillWithBlocks(world, structureBoundingBox, 12, 10, i, 12, 11, i, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.placeBlockAtCurrentPosition(world, Blocks.nether_brick, 0, i, 13, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.nether_brick, 0, i, 13, 12, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.nether_brick, 0, 0, 13, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.nether_brick, 0, 12, 13, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, i + 1, 13, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, i + 1, 13, 12, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 0, 13, i + 1, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 12, 13, i + 1, structureBoundingBox);
            }

            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 0, 13, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 0, 13, 12, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 0, 13, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 12, 13, 0, structureBoundingBox);

            for (int i = 3; i <= 9; i += 2) {
                this.fillWithBlocks(world, structureBoundingBox, 1, 7, i, 1, 8, i, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.fillWithBlocks(world, structureBoundingBox, 11, 7, i, 11, 8, i, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            }

            this.fillWithBlocks(world, structureBoundingBox, 4, 2, 0, 8, 2, 12, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 4, 12, 2, 8, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 0, 0, 8, 1, 3, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 0, 9, 8, 1, 12, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 4, 3, 1, 8, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 9, 0, 4, 12, 1, 8, Blocks.nether_brick, Blocks.nether_brick, false);

            for (int i = 4; i <= 8; i++) {
                for (int j = 0; j <= 2; j++) {
                    this.handleWorld(world, Blocks.nether_brick, 0, i, -1, j, structureBoundingBox);
                    this.handleWorld(world, Blocks.nether_brick, 0, i, -1, 12 - j, structureBoundingBox);
                }
            }

            for (int i = 0; i <= 2; i++) {
                for (int j = 4; j <= 8; j++) {
                    this.handleWorld(world, Blocks.nether_brick, 0, i, -1, j, structureBoundingBox);
                    this.handleWorld(world, Blocks.nether_brick, 0, 12 - i, -1, j, structureBoundingBox);
                }
            }

            this.fillWithBlocks(world, structureBoundingBox, 5, 5, 5, 7, 5, 7, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 6, 1, 6, 6, 4, 6, Blocks.air, Blocks.air, false);
            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick, 0, 6, 0, 6, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.flowing_lava, 0, 6, 5, 6, structureBoundingBox);
            int xWithOffset = this.getXWithOffset(6, 6);
            int yWithOffset = this.getYWithOffset(5);
            int zWithOffset = this.getZWithOffset(6, 6);
            if (structureBoundingBox.isVecInside(xWithOffset, yWithOffset, zWithOffset)) {
                world.scheduledUpdatesAreImmediate = true;
                Blocks.flowing_lava.updateTick(world, xWithOffset, yWithOffset, zWithOffset, random);
                world.scheduledUpdatesAreImmediate = false;
            }

            return true;
        }
    }

    public static class NetherStalkRoom extends StructureNetherBridgePieces.Piece {
        public NetherStalkRoom() {
        }

        public NetherStalkRoom(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            this.getNextComponentNormal((StructureNetherBridgePieces.Start)structureComponent, list, random, 5, 3, true);
            this.getNextComponentNormal((StructureNetherBridgePieces.Start)structureComponent, list, random, 5, 11, true);
        }

        public static StructureNetherBridgePieces.NetherStalkRoom createValidComponent(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -5, -3, 0, 13, 14, 13, n4);
            return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureNetherBridgePieces.NetherStalkRoom(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            this.fillWithBlocks(world, structureBoundingBox, 0, 3, 0, 12, 4, 12, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 5, 0, 12, 13, 12, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 5, 0, 1, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 11, 5, 0, 12, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 5, 11, 4, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 8, 5, 11, 10, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 5, 9, 11, 7, 12, 12, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 5, 0, 4, 12, 1, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 8, 5, 0, 10, 12, 1, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 5, 9, 0, 7, 12, 1, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 11, 2, 10, 12, 10, Blocks.nether_brick, Blocks.nether_brick, false);

            for (int i = 1; i <= 11; i += 2) {
                this.fillWithBlocks(world, structureBoundingBox, i, 10, 0, i, 11, 0, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.fillWithBlocks(world, structureBoundingBox, i, 10, 12, i, 11, 12, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.fillWithBlocks(world, structureBoundingBox, 0, 10, i, 0, 11, i, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.fillWithBlocks(world, structureBoundingBox, 12, 10, i, 12, 11, i, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.placeBlockAtCurrentPosition(world, Blocks.nether_brick, 0, i, 13, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.nether_brick, 0, i, 13, 12, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.nether_brick, 0, 0, 13, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.nether_brick, 0, 12, 13, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, i + 1, 13, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, i + 1, 13, 12, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 0, 13, i + 1, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 12, 13, i + 1, structureBoundingBox);
            }

            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 0, 13, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 0, 13, 12, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 0, 13, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 12, 13, 0, structureBoundingBox);

            for (int i = 3; i <= 9; i += 2) {
                this.fillWithBlocks(world, structureBoundingBox, 1, 7, i, 1, 8, i, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
                this.fillWithBlocks(world, structureBoundingBox, 11, 7, i, 11, 8, i, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            }

            int metadataWithOffset = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 3);

            for (int i = 0; i <= 6; i++) {
                int n = i + 4;

                for (int j = 5; j <= 7; j++) {
                    this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, metadataWithOffset, j, 5 + i, n, structureBoundingBox);
                }

                if (n >= 5 && n <= 8) {
                    this.fillWithBlocks(world, structureBoundingBox, 5, 5, n, 7, i + 4, n, Blocks.nether_brick, Blocks.nether_brick, false);
                } else if (n >= 9 && n <= 10) {
                    this.fillWithBlocks(world, structureBoundingBox, 5, 8, n, 7, i + 4, n, Blocks.nether_brick, Blocks.nether_brick, false);
                }

                if (i >= 1) {
                    this.fillWithBlocks(world, structureBoundingBox, 5, 6 + i, n, 7, 9 + i, n, Blocks.air, Blocks.air, false);
                }
            }

            for (int i = 5; i <= 7; i++) {
                this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, metadataWithOffset, i, 12, 11, structureBoundingBox);
            }

            this.fillWithBlocks(world, structureBoundingBox, 5, 6, 7, 5, 7, 7, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 7, 6, 7, 7, 7, 7, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 5, 13, 12, 7, 13, 12, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 5, 2, 3, 5, 3, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 5, 9, 3, 5, 10, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 5, 4, 2, 5, 8, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 9, 5, 2, 10, 5, 3, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 9, 5, 9, 10, 5, 10, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 10, 5, 4, 10, 5, 8, Blocks.nether_brick, Blocks.nether_brick, false);
            int metadataWithOffset2 = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 0);
            int metadataWithOffset3 = this.getMetadataWithOffset(Blocks.nether_brick_stairs, 1);
            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, metadataWithOffset3, 4, 5, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, metadataWithOffset3, 4, 5, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, metadataWithOffset3, 4, 5, 9, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, metadataWithOffset3, 4, 5, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, metadataWithOffset2, 8, 5, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, metadataWithOffset2, 8, 5, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, metadataWithOffset2, 8, 5, 9, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_stairs, metadataWithOffset2, 8, 5, 10, structureBoundingBox);
            this.fillWithBlocks(world, structureBoundingBox, 3, 4, 4, 4, 4, 8, Blocks.soul_sand, Blocks.soul_sand, false);
            this.fillWithBlocks(world, structureBoundingBox, 8, 4, 4, 9, 4, 8, Blocks.soul_sand, Blocks.soul_sand, false);
            this.fillWithBlocks(world, structureBoundingBox, 3, 5, 4, 4, 5, 8, Blocks.nether_wart, Blocks.nether_wart, false);
            this.fillWithBlocks(world, structureBoundingBox, 8, 5, 4, 9, 5, 8, Blocks.nether_wart, Blocks.nether_wart, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 2, 0, 8, 2, 12, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 4, 12, 2, 8, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 0, 0, 8, 1, 3, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 0, 9, 8, 1, 12, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 4, 3, 1, 8, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 9, 0, 4, 12, 1, 8, Blocks.nether_brick, Blocks.nether_brick, false);

            for (int i = 4; i <= 8; i++) {
                for (int j = 0; j <= 2; j++) {
                    this.handleWorld(world, Blocks.nether_brick, 0, i, -1, j, structureBoundingBox);
                    this.handleWorld(world, Blocks.nether_brick, 0, i, -1, 12 - j, structureBoundingBox);
                }
            }

            for (int i = 0; i <= 2; i++) {
                for (int j = 4; j <= 8; j++) {
                    this.handleWorld(world, Blocks.nether_brick, 0, i, -1, j, structureBoundingBox);
                    this.handleWorld(world, Blocks.nether_brick, 0, 12 - i, -1, j, structureBoundingBox);
                }
            }

            return true;
        }
    }

    public abstract static class Piece extends StructureComponent {
        public static WeightedRandomChestContent[] weightedRandomChestContentArray = new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 5),
            new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 5),
            new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 15),
            new WeightedRandomChestContent(Items.golden_sword, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.golden_chestplate, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.flint_and_steel, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.nether_wart, 0, 3, 7, 5),
            new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 10),
            new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 8),
            new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 3)
        };

        public Piece() {
        }

        public Piece(int n) {
            super(n);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
        }

        public int getTotalWeight(List list) {
            boolean flag = false;
            int n = 0;

            for (StructureNetherBridgePieces.PieceWeight pieceWeight : (Iterable<StructureNetherBridgePieces.PieceWeight>)list) {
                if (pieceWeight.intValue3 > 0 && pieceWeight.intValue2 < pieceWeight.intValue3) {
                    flag = true;
                }

                n += pieceWeight.intValue;
            }

            return flag ? n : -1;
        }

        public StructureNetherBridgePieces.Piece getNextComponent(
            StructureNetherBridgePieces.Start start, List list, List list2, Random random, int n, int n2, int n3, int n4, int n5
        ) {
            int totalWeight = this.getTotalWeight(list);
            boolean flag = totalWeight > 0 && n5 <= 30;
            int n6 = 0;

            while (n6 < 5 && flag) {
                n6++;
                int nextInt = random.nextInt(totalWeight);

                for (StructureNetherBridgePieces.PieceWeight pieceWeight : (Iterable<StructureNetherBridgePieces.PieceWeight>)list) {
                    nextInt -= pieceWeight.intValue;
                    if (nextInt < 0) {
                        if (!pieceWeight.check(n5) || pieceWeight == start.theNetherBridgePieceWeight && !pieceWeight.flag) {
                            break;
                        }

                        StructureNetherBridgePieces.Piece createNextComponentRandom = StructureNetherBridgePieces.createNextComponentRandom(
                            pieceWeight, list2, random, n, n2, n3, n4, n5
                        );
                        if (createNextComponentRandom != null) {
                            pieceWeight.intValue2++;
                            start.theNetherBridgePieceWeight = pieceWeight;
                            if (!pieceWeight.check()) {
                                list.remove(pieceWeight);
                            }

                            return createNextComponentRandom;
                        }
                    }
                }
            }

            return StructureNetherBridgePieces.End.createEnd(list2, random, n, n2, n3, n4, n5);
        }

        public StructureComponent getNextComponent(
            StructureNetherBridgePieces.Start start, List list, Random random, int n, int n2, int n3, int n4, int n5, boolean flag
        ) {
            if (Math.abs(n - start.getBoundingBox().minX) <= 112 && Math.abs(n3 - start.getBoundingBox().minZ) <= 112) {
                List primaryWeights = start.primaryWeights;
                if (flag) {
                    primaryWeights = start.secondaryWeights;
                }

                StructureNetherBridgePieces.Piece nextComponent = this.getNextComponent(start, primaryWeights, list, random, n, n2, n3, n4, n5 + 1);
                if (nextComponent != null) {
                    list.add(nextComponent);
                    start.list.add(nextComponent);
                }

                return nextComponent;
            } else {
                return StructureNetherBridgePieces.End.createEnd(list, random, n, n2, n3, n4, n5);
            }
        }

        public StructureComponent getNextComponentNormal(StructureNetherBridgePieces.Start start, List list, Random random, int n, int n2, boolean flag) {
            switch (this.coordBaseMode) {
                case 0:
                    return this.getNextComponent(
                        start,
                        list,
                        random,
                        this.boundingBox.minX + n,
                        this.boundingBox.minY + n2,
                        this.boundingBox.maxZ + 1,
                        this.coordBaseMode,
                        this.getComponentType(),
                        flag
                    );
                case 1:
                    return this.getNextComponent(
                        start,
                        list,
                        random,
                        this.boundingBox.minX - 1,
                        this.boundingBox.minY + n2,
                        this.boundingBox.minZ + n,
                        this.coordBaseMode,
                        this.getComponentType(),
                        flag
                    );
                case 2:
                    return this.getNextComponent(
                        start,
                        list,
                        random,
                        this.boundingBox.minX + n,
                        this.boundingBox.minY + n2,
                        this.boundingBox.minZ - 1,
                        this.coordBaseMode,
                        this.getComponentType(),
                        flag
                    );
                case 3:
                    return this.getNextComponent(
                        start,
                        list,
                        random,
                        this.boundingBox.maxX + 1,
                        this.boundingBox.minY + n2,
                        this.boundingBox.minZ + n,
                        this.coordBaseMode,
                        this.getComponentType(),
                        flag
                    );
                default:
                    return null;
            }
        }

        public StructureComponent getNextComponentX(StructureNetherBridgePieces.Start start, List list, Random random, int n, int n2, boolean flag) {
            switch (this.coordBaseMode) {
                case 0:
                    return this.getNextComponent(
                        start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + n, this.boundingBox.minZ + n2, 1, this.getComponentType(), flag
                    );
                case 1:
                    return this.getNextComponent(
                        start, list, random, this.boundingBox.minX + n2, this.boundingBox.minY + n, this.boundingBox.minZ - 1, 2, this.getComponentType(), flag
                    );
                case 2:
                    return this.getNextComponent(
                        start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + n, this.boundingBox.minZ + n2, 1, this.getComponentType(), flag
                    );
                case 3:
                    return this.getNextComponent(
                        start, list, random, this.boundingBox.minX + n2, this.boundingBox.minY + n, this.boundingBox.minZ - 1, 2, this.getComponentType(), flag
                    );
                default:
                    return null;
            }
        }

        public StructureComponent getNextComponentZ(StructureNetherBridgePieces.Start start, List list, Random random, int n, int n2, boolean flag) {
            switch (this.coordBaseMode) {
                case 0:
                    return this.getNextComponent(
                        start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + n, this.boundingBox.minZ + n2, 3, this.getComponentType(), flag
                    );
                case 1:
                    return this.getNextComponent(
                        start, list, random, this.boundingBox.minX + n2, this.boundingBox.minY + n, this.boundingBox.maxZ + 1, 0, this.getComponentType(), flag
                    );
                case 2:
                    return this.getNextComponent(
                        start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + n, this.boundingBox.minZ + n2, 3, this.getComponentType(), flag
                    );
                case 3:
                    return this.getNextComponent(
                        start, list, random, this.boundingBox.minX + n2, this.boundingBox.minY + n, this.boundingBox.maxZ + 1, 0, this.getComponentType(), flag
                    );
                default:
                    return null;
            }
        }

        public static boolean isAboveGround(StructureBoundingBox structureBoundingBox) {
            return structureBoundingBox != null && structureBoundingBox.minY > 10;
        }
    }

    public static class PieceWeight {
        public Class weightClass;
        public int intValue;
        public int intValue2;
        public int intValue3;
        public boolean flag;

        public PieceWeight(Class clazz, int n, int n2, boolean flag) {
            this.weightClass = clazz;
            this.intValue = n;
            this.intValue3 = n2;
            this.flag = flag;
        }

        public PieceWeight(Class clazz, int n, int n2) {
            this(clazz, n, n2, false);
        }

        public boolean check(int n) {
            return this.intValue3 == 0 || this.intValue2 < this.intValue3;
        }

        public boolean check() {
            return this.intValue3 == 0 || this.intValue2 < this.intValue3;
        }
    }

    public static class Stairs extends StructureNetherBridgePieces.Piece {
        public Stairs() {
        }

        public Stairs(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            this.getNextComponentZ((StructureNetherBridgePieces.Start)structureComponent, list, random, 6, 2, false);
        }

        public static StructureNetherBridgePieces.Stairs createValidComponent(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -2, 0, 0, 7, 11, 7, n4);
            return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureNetherBridgePieces.Stairs(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 6, 1, 6, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 0, 6, 10, 6, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 0, 1, 8, 0, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 5, 2, 0, 6, 8, 0, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 1, 0, 8, 6, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 6, 2, 1, 6, 8, 6, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 2, 6, 5, 8, 6, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 3, 2, 0, 5, 4, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 6, 3, 2, 6, 5, 2, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 6, 3, 4, 6, 5, 4, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick, 0, 5, 2, 5, structureBoundingBox);
            this.fillWithBlocks(world, structureBoundingBox, 4, 2, 5, 4, 3, 5, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 3, 2, 5, 3, 4, 5, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 2, 5, 2, 5, 5, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 2, 5, 1, 6, 5, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 7, 1, 5, 7, 4, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 6, 8, 2, 6, 8, 4, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 6, 0, 4, 8, 0, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 5, 0, 4, 5, 0, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);

            for (int i = 0; i <= 6; i++) {
                for (int j = 0; j <= 6; j++) {
                    this.handleWorld(world, Blocks.nether_brick, 0, i, -1, j, structureBoundingBox);
                }
            }

            return true;
        }
    }

    public static class Start extends StructureNetherBridgePieces.Crossing3 {
        public StructureNetherBridgePieces.PieceWeight theNetherBridgePieceWeight;
        public ArrayList list = new ArrayList();
        public List primaryWeights;
        public List secondaryWeights;

        public Start() {
        }

        public Start(Random random, int n, int n2) {
            super(random, n, n2);
            this.primaryWeights = new ArrayList();

            for (StructureNetherBridgePieces.PieceWeight pieceWeight : StructureNetherBridgePieces.primaryComponents) {
                pieceWeight.intValue2 = 0;
                this.primaryWeights.add(pieceWeight);
            }

            this.secondaryWeights = new ArrayList();

            for (StructureNetherBridgePieces.PieceWeight pieceWeight : StructureNetherBridgePieces.secondaryComponents) {
                pieceWeight.intValue2 = 0;
                this.secondaryWeights.add(pieceWeight);
            }
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
        }
    }

    public static class Straight extends StructureNetherBridgePieces.Piece {
        public Straight() {
        }

        public Straight(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            this.getNextComponentNormal((StructureNetherBridgePieces.Start)structureComponent, list, random, 1, 3, false);
        }

        public static StructureNetherBridgePieces.Straight createValidComponent(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -3, 0, 5, 10, 19, n4);
            return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureNetherBridgePieces.Straight(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            this.fillWithBlocks(world, structureBoundingBox, 0, 3, 0, 4, 4, 18, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 5, 0, 3, 7, 18, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 5, 0, 0, 5, 18, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 5, 0, 4, 5, 18, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 0, 4, 2, 5, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 13, 4, 2, 18, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 4, 1, 3, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 15, 4, 1, 18, Blocks.nether_brick, Blocks.nether_brick, false);

            for (int i = 0; i <= 4; i++) {
                for (int j = 0; j <= 2; j++) {
                    this.handleWorld(world, Blocks.nether_brick, 0, i, -1, j, structureBoundingBox);
                    this.handleWorld(world, Blocks.nether_brick, 0, i, -1, 18 - j, structureBoundingBox);
                }
            }

            this.fillWithBlocks(world, structureBoundingBox, 0, 1, 1, 0, 4, 1, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 3, 4, 0, 4, 4, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 3, 14, 0, 4, 14, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 1, 17, 0, 4, 17, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 1, 1, 4, 4, 1, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 3, 4, 4, 4, 4, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 3, 14, 4, 4, 14, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 1, 17, 4, 4, 17, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            return true;
        }
    }

    public static class Throne extends StructureNetherBridgePieces.Piece {
        public boolean hasSpawner;

        public Throne() {
        }

        public Throne(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.hasSpawner = nbttagCompound.getBoolean("Mob");
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setBoolean("Mob", this.hasSpawner);
        }

        public static StructureNetherBridgePieces.Throne createValidComponent(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -2, 0, 0, 7, 8, 9, n4);
            return isAboveGround(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureNetherBridgePieces.Throne(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 0, 6, 7, 7, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 0, 0, 5, 1, 7, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 2, 1, 5, 2, 7, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 3, 2, 5, 3, 7, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 4, 3, 5, 4, 7, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 2, 0, 1, 4, 2, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 5, 2, 0, 5, 4, 2, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 5, 2, 1, 5, 3, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 5, 5, 2, 5, 5, 3, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 5, 3, 0, 5, 8, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 6, 5, 3, 6, 5, 8, Blocks.nether_brick, Blocks.nether_brick, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 5, 8, 5, 5, 8, Blocks.nether_brick, Blocks.nether_brick, false);
            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 1, 6, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.nether_brick_fence, 0, 5, 6, 3, structureBoundingBox);
            this.fillWithBlocks(world, structureBoundingBox, 0, 6, 3, 0, 6, 8, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 6, 6, 3, 6, 6, 8, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 6, 8, 5, 7, 8, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 8, 8, 4, 8, 8, Blocks.nether_brick_fence, Blocks.nether_brick_fence, false);
            if (!this.hasSpawner) {
                int yWithOffset = this.getYWithOffset(5);
                int xWithOffset = this.getXWithOffset(3, 5);
                int zWithOffset = this.getZWithOffset(3, 5);
                if (structureBoundingBox.isVecInside(xWithOffset, yWithOffset, zWithOffset)) {
                    this.hasSpawner = true;
                    world.setBlock(xWithOffset, yWithOffset, zWithOffset, Blocks.mob_spawner, 0, 2);
                    TileEntityMobSpawner tileEntity = (TileEntityMobSpawner)world.getTileEntity(xWithOffset, yWithOffset, zWithOffset);
                    if (tileEntity != null) {
                        tileEntity.getLogic().setEntityName("Blaze");
                    }
                }
            }

            for (int i = 0; i <= 6; i++) {
                for (int j = 0; j <= 6; j++) {
                    this.handleWorld(world, Blocks.nether_brick, 0, i, -1, j, structureBoundingBox);
                }
            }

            return true;
        }
    }
}
