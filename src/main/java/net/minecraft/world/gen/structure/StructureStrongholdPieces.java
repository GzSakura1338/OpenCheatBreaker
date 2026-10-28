package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureComponent.BlockSelector;

public class StructureStrongholdPieces {
    public static StructureStrongholdPieces.PieceWeight[] pieceWeightArray = new StructureStrongholdPieces.PieceWeight[]{
        new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Straight.class, 40, 0),
        new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Prison.class, 5, 5),
        new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.LeftTurn.class, 20, 0),
        new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.RightTurn.class, 20, 0),
        new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.RoomCrossing.class, 10, 6),
        new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.StairsStraight.class, 5, 5),
        new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Stairs.class, 5, 5),
        new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Crossing.class, 5, 4),
        new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.ChestCorridor.class, 5, 4),
        new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Library.class, 10, 2) {
            @Override
            public boolean canSpawnMoreStructuresOfType(int n) {
                return super.canSpawnMoreStructuresOfType(n) && n > 4;
            }
        },
        new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.PortalRoom.class, 20, 1) {
            @Override
            public boolean canSpawnMoreStructuresOfType(int n) {
                return super.canSpawnMoreStructuresOfType(n) && n > 5;
            }
        }
    };
    public static List structurePieceList;
    public static Class strongComponentType;
    public static int totalWeight;
    public static StructureStrongholdPieces.Stones strongholdStones = new StructureStrongholdPieces.Stones(null);

    public static void registerStrongholdPieces() {
        MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.ChestCorridor.class, "SHCC");
        MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Corridor.class, "SHFC");
        MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Crossing.class, "SH5C");
        MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.LeftTurn.class, "SHLT");
        MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Library.class, "SHLi");
        MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.PortalRoom.class, "SHPR");
        MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Prison.class, "SHPH");
        MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.RightTurn.class, "SHRT");
        MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.RoomCrossing.class, "SHRC");
        MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Stairs.class, "SHSD");
        MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Stairs2.class, "SHStart");
        MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.Straight.class, "SHS");
        MapGenStructureIO.registerStructureComponent(StructureStrongholdPieces.StairsStraight.class, "SHSSD");
    }

    public static void prepareStructurePieces() {
        structurePieceList = new ArrayList();

        for (StructureStrongholdPieces.PieceWeight pieceWeight : pieceWeightArray) {
            pieceWeight.instancesSpawned = 0;
            structurePieceList.add(pieceWeight);
        }

        strongComponentType = null;
    }

    public static boolean canAddStructurePieces() {
        boolean flag = false;
        totalWeight = 0;

        for (StructureStrongholdPieces.PieceWeight pieceWeight : (Iterable<StructureStrongholdPieces.PieceWeight>)structurePieceList) {
            if (pieceWeight.instancesLimit > 0 && pieceWeight.instancesSpawned < pieceWeight.instancesLimit) {
                flag = true;
            }

            totalWeight = totalWeight + pieceWeight.pieceWeight;
        }

        return flag;
    }

    public static StructureStrongholdPieces.Stronghold getStrongholdComponentFromWeightedPiece(
        Class clazz, List list, Random random, int n, int n2, int n3, int n4, int n5
    ) {
        StructureStrongholdPieces.Stronghold stronghold = null;
        if (clazz == StructureStrongholdPieces.Straight.class) {
            stronghold = StructureStrongholdPieces.Straight.findValidPlacement(list, random, n, n2, n3, n4, n5);
        } else if (clazz == StructureStrongholdPieces.Prison.class) {
            stronghold = StructureStrongholdPieces.Prison.findValidPlacement(list, random, n, n2, n3, n4, n5);
        } else if (clazz == StructureStrongholdPieces.LeftTurn.class) {
            stronghold = StructureStrongholdPieces.LeftTurn.findValidPlacement(list, random, n, n2, n3, n4, n5);
        } else if (clazz == StructureStrongholdPieces.RightTurn.class) {
            stronghold = StructureStrongholdPieces.RightTurn.findValidPlacement(list, random, n, n2, n3, n4, n5);
        } else if (clazz == StructureStrongholdPieces.RoomCrossing.class) {
            stronghold = StructureStrongholdPieces.RoomCrossing.findValidPlacement(list, random, n, n2, n3, n4, n5);
        } else if (clazz == StructureStrongholdPieces.StairsStraight.class) {
            stronghold = StructureStrongholdPieces.StairsStraight.findValidPlacement(list, random, n, n2, n3, n4, n5);
        } else if (clazz == StructureStrongholdPieces.Stairs.class) {
            stronghold = StructureStrongholdPieces.Stairs.getStrongholdStairsComponent(list, random, n, n2, n3, n4, n5);
        } else if (clazz == StructureStrongholdPieces.Crossing.class) {
            stronghold = StructureStrongholdPieces.Crossing.findValidPlacement(list, random, n, n2, n3, n4, n5);
        } else if (clazz == StructureStrongholdPieces.ChestCorridor.class) {
            stronghold = StructureStrongholdPieces.ChestCorridor.findValidPlacement(list, random, n, n2, n3, n4, n5);
        } else if (clazz == StructureStrongholdPieces.Library.class) {
            stronghold = StructureStrongholdPieces.Library.findValidPlacement(list, random, n, n2, n3, n4, n5);
        } else if (clazz == StructureStrongholdPieces.PortalRoom.class) {
            stronghold = StructureStrongholdPieces.PortalRoom.findValidPlacement(list, random, n, n2, n3, n4, n5);
        }

        return stronghold;
    }

    public static StructureStrongholdPieces.Stronghold getNextComponent(
        StructureStrongholdPieces.Stairs2 stairs2, List list, Random random, int n, int n2, int n3, int n4, int n5
    ) {
        if (!canAddStructurePieces()) {
            return null;
        }

        if (strongComponentType != null) {
            StructureStrongholdPieces.Stronghold stronghold = getStrongholdComponentFromWeightedPiece(strongComponentType, list, random, n, n2, n3, n4, n5);
            strongComponentType = null;
            if (stronghold != null) {
                return stronghold;
            }
        }

        int n6 = 0;

        while (n6 < 5) {
            n6++;
            int nextInt = random.nextInt(totalWeight);

            for (StructureStrongholdPieces.PieceWeight pieceWeight : (Iterable<StructureStrongholdPieces.PieceWeight>)structurePieceList) {
                nextInt -= pieceWeight.pieceWeight;
                if (nextInt < 0) {
                    if (!pieceWeight.canSpawnMoreStructuresOfType(n5) || pieceWeight == stairs2.strongholdPieceWeight) {
                        break;
                    }

                    StructureStrongholdPieces.Stronghold stronghold = getStrongholdComponentFromWeightedPiece(pieceWeight.pieceClass, list, random, n, n2, n3, n4, n5);
                    if (stronghold != null) {
                        pieceWeight.instancesSpawned++;
                        stairs2.strongholdPieceWeight = pieceWeight;
                        if (!pieceWeight.canSpawnMoreStructures()) {
                            structurePieceList.remove(pieceWeight);
                        }

                        return stronghold;
                    }
                }
            }
        }

        StructureBoundingBox findStructureBoundingBox = StructureStrongholdPieces.Corridor.findStructureBoundingBox(list, random, n, n2, n3, n4);
        return findStructureBoundingBox != null && findStructureBoundingBox.minY > 1 ? new StructureStrongholdPieces.Corridor(n5, random, findStructureBoundingBox, n4) : null;
    }

    public static StructureComponent getNextValidComponent(
        StructureStrongholdPieces.Stairs2 stairs2, List list, Random random, int n, int n2, int n3, int n4, int n5
    ) {
        if (n5 > 50) {
            return null;
        }

        if (Math.abs(n - stairs2.getBoundingBox().minX) <= 112 && Math.abs(n3 - stairs2.getBoundingBox().minZ) <= 112) {
            StructureStrongholdPieces.Stronghold nextComponent = getNextComponent(stairs2, list, random, n, n2, n3, n4, n5 + 1);
            if (nextComponent != null) {
                list.add(nextComponent);
                stairs2.list.add(nextComponent);
            }

            return nextComponent;
        } else {
            return null;
        }
    }

    public static class ChestCorridor extends StructureStrongholdPieces.Stronghold {
        public static WeightedRandomChestContent[] strongholdChestContents = new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Items.ender_pearl, 0, 1, 1, 10),
            new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3),
            new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
            new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5),
            new WeightedRandomChestContent(Items.redstone, 0, 4, 9, 5),
            new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15),
            new WeightedRandomChestContent(Items.apple, 0, 1, 3, 15),
            new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_sword, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_chestplate, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_helmet, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_leggings, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_boots, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.golden_apple, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)
        };
        public boolean hasMadeChest;

        public ChestCorridor() {
        }

        public ChestCorridor(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.door = this.getRandomDoor(random);
            this.boundingBox = structureBoundingBox;
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setBoolean("Chest", this.hasMadeChest);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.hasMadeChest = nbttagCompound.getBoolean("Chest");
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)structureComponent, list, random, 1, 1);
        }

        public static StructureStrongholdPieces.ChestCorridor findValidPlacement(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -1, 0, 5, 5, 7, n4);
            return canStrongholdGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureStrongholdPieces.ChestCorridor(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.isLiquidInStructureBoundingBox(world, structureBoundingBox)) {
                return false;
            }

            this.fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 4, 4, 6, true, random, StructureStrongholdPieces.strongholdStones);
            this.placeDoor(world, random, structureBoundingBox, this.door, 1, 1, 0);
            this.placeDoor(world, random, structureBoundingBox, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
            this.fillWithBlocks(world, structureBoundingBox, 3, 1, 2, 3, 1, 4, Blocks.stonebrick, Blocks.stonebrick, false);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 3, 1, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 3, 1, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 3, 2, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 3, 2, 4, structureBoundingBox);

            for (int i = 2; i <= 4; i++) {
                this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 5, 2, 1, i, structureBoundingBox);
            }

            if (!this.hasMadeChest) {
                int yWithOffset = this.getYWithOffset(2);
                int xWithOffset = this.getXWithOffset(3, 3);
                int zWithOffset = this.getZWithOffset(3, 3);
                if (structureBoundingBox.isVecInside(xWithOffset, yWithOffset, zWithOffset)) {
                    this.hasMadeChest = true;
                    this.generateStructureChestContents(
                        world,
                        structureBoundingBox,
                        random,
                        3,
                        2,
                        3,
                        WeightedRandomChestContent.union(strongholdChestContents, Items.enchanted_book.getLootTable(random)),
                        2 + random.nextInt(2)
                    );
                }
            }

            return true;
        }
    }

    public static class Corridor extends StructureStrongholdPieces.Stronghold {
        public int intValue;

        public Corridor() {
        }

        public Corridor(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
            this.intValue = n2 != 2 && n2 != 0 ? structureBoundingBox.getXSize() : structureBoundingBox.getZSize();
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setInteger("Steps", this.intValue);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.intValue = nbttagCompound.getInteger("Steps");
        }

        public static StructureBoundingBox findStructureBoundingBox(List list, Random random, int n, int n2, int n3, int n4) {
            boolean flag = true;
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -1, 0, 5, 5, 4, n4);
            StructureComponent findIntersecting = StructureComponent.findIntersecting(list, componentToAddBoundingBox);
            if (findIntersecting == null) {
                return null;
            }

            if (findIntersecting.getBoundingBox().minY == componentToAddBoundingBox.minY) {
                for (int i = 3; i >= 1; i--) {
                    componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -1, 0, 5, 5, i - 1, n4);
                    if (!findIntersecting.getBoundingBox().intersectsWith(componentToAddBoundingBox)) {
                        return StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -1, 0, 5, 5, i, n4);
                    }
                }
            }

            return null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.isLiquidInStructureBoundingBox(world, structureBoundingBox)) {
                return false;
            }

            for (int i = 0; i < this.intValue; i++) {
                this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 0, 0, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 1, 0, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 2, 0, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 3, 0, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 4, 0, i, structureBoundingBox);

                for (int j = 1; j <= 3; j++) {
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 0, j, i, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 1, j, i, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 2, j, i, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 3, j, i, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 4, j, i, structureBoundingBox);
                }

                this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 0, 4, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 1, 4, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 2, 4, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 3, 4, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 4, 4, i, structureBoundingBox);
            }

            return true;
        }
    }

    public static class Crossing extends StructureStrongholdPieces.Stronghold {
        public boolean flag;
        public boolean flag2;
        public boolean flag3;
        public boolean flag4;

        public Crossing() {
        }

        public Crossing(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.door = this.getRandomDoor(random);
            this.boundingBox = structureBoundingBox;
            this.flag = random.nextBoolean();
            this.flag2 = random.nextBoolean();
            this.flag3 = random.nextBoolean();
            this.flag4 = random.nextInt(3) > 0;
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setBoolean("leftLow", this.flag);
            nbttagCompound.setBoolean("leftHigh", this.flag2);
            nbttagCompound.setBoolean("rightLow", this.flag3);
            nbttagCompound.setBoolean("rightHigh", this.flag4);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.flag = nbttagCompound.getBoolean("leftLow");
            this.flag2 = nbttagCompound.getBoolean("leftHigh");
            this.flag3 = nbttagCompound.getBoolean("rightLow");
            this.flag4 = nbttagCompound.getBoolean("rightHigh");
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            int n = 3;
            int n2 = 5;
            if (this.coordBaseMode == 1 || this.coordBaseMode == 2) {
                n = 8 - n;
                n2 = 8 - n2;
            }

            this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)structureComponent, list, random, 5, 1);
            if (this.flag) {
                this.getNextComponentX((StructureStrongholdPieces.Stairs2)structureComponent, list, random, n, 1);
            }

            if (this.flag2) {
                this.getNextComponentX((StructureStrongholdPieces.Stairs2)structureComponent, list, random, n2, 7);
            }

            if (this.flag3) {
                this.getNextComponentZ((StructureStrongholdPieces.Stairs2)structureComponent, list, random, n, 1);
            }

            if (this.flag4) {
                this.getNextComponentZ((StructureStrongholdPieces.Stairs2)structureComponent, list, random, n2, 7);
            }
        }

        public static StructureStrongholdPieces.Crossing findValidPlacement(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -4, -3, 0, 10, 9, 11, n4);
            return canStrongholdGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureStrongholdPieces.Crossing(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.isLiquidInStructureBoundingBox(world, structureBoundingBox)) {
                return false;
            }

            this.fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 9, 8, 10, true, random, StructureStrongholdPieces.strongholdStones);
            this.placeDoor(world, random, structureBoundingBox, this.door, 4, 3, 0);
            if (this.flag) {
                this.fillWithBlocks(world, structureBoundingBox, 0, 3, 1, 0, 5, 3, Blocks.air, Blocks.air, false);
            }

            if (this.flag3) {
                this.fillWithBlocks(world, structureBoundingBox, 9, 3, 1, 9, 5, 3, Blocks.air, Blocks.air, false);
            }

            if (this.flag2) {
                this.fillWithBlocks(world, structureBoundingBox, 0, 5, 7, 0, 7, 9, Blocks.air, Blocks.air, false);
            }

            if (this.flag4) {
                this.fillWithBlocks(world, structureBoundingBox, 9, 5, 7, 9, 7, 9, Blocks.air, Blocks.air, false);
            }

            this.fillWithBlocks(world, structureBoundingBox, 5, 1, 10, 7, 3, 10, Blocks.air, Blocks.air, false);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 1, 2, 1, 8, 2, 6, false, random, StructureStrongholdPieces.strongholdStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 4, 1, 5, 4, 4, 9, false, random, StructureStrongholdPieces.strongholdStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 8, 1, 5, 8, 4, 9, false, random, StructureStrongholdPieces.strongholdStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 1, 4, 7, 3, 4, 9, false, random, StructureStrongholdPieces.strongholdStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 1, 3, 5, 3, 3, 6, false, random, StructureStrongholdPieces.strongholdStones);
            this.fillWithBlocks(world, structureBoundingBox, 1, 3, 4, 3, 3, 4, Blocks.stone_slab, Blocks.stone_slab, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 4, 6, 3, 4, 6, Blocks.stone_slab, Blocks.stone_slab, false);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 5, 1, 7, 7, 1, 8, false, random, StructureStrongholdPieces.strongholdStones);
            this.fillWithBlocks(world, structureBoundingBox, 5, 1, 9, 7, 1, 9, Blocks.stone_slab, Blocks.stone_slab, false);
            this.fillWithBlocks(world, structureBoundingBox, 5, 2, 7, 7, 2, 7, Blocks.stone_slab, Blocks.stone_slab, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 5, 7, 4, 5, 9, Blocks.stone_slab, Blocks.stone_slab, false);
            this.fillWithBlocks(world, structureBoundingBox, 8, 5, 7, 8, 5, 9, Blocks.stone_slab, Blocks.stone_slab, false);
            this.fillWithBlocks(world, structureBoundingBox, 5, 5, 7, 7, 5, 9, Blocks.double_stone_slab, Blocks.double_stone_slab, false);
            this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 6, 5, 6, structureBoundingBox);
            return true;
        }
    }

    public static class LeftTurn extends StructureStrongholdPieces.Stronghold {
        public LeftTurn() {
        }

        public LeftTurn(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.door = this.getRandomDoor(random);
            this.boundingBox = structureBoundingBox;
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            if (this.coordBaseMode != 2 && this.coordBaseMode != 3) {
                this.getNextComponentZ((StructureStrongholdPieces.Stairs2)structureComponent, list, random, 1, 1);
            } else {
                this.getNextComponentX((StructureStrongholdPieces.Stairs2)structureComponent, list, random, 1, 1);
            }
        }

        public static StructureStrongholdPieces.LeftTurn findValidPlacement(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -1, 0, 5, 5, 5, n4);
            return canStrongholdGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureStrongholdPieces.LeftTurn(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.isLiquidInStructureBoundingBox(world, structureBoundingBox)) {
                return false;
            }

            this.fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 4, 4, 4, true, random, StructureStrongholdPieces.strongholdStones);
            this.placeDoor(world, random, structureBoundingBox, this.door, 1, 1, 0);
            if (this.coordBaseMode != 2 && this.coordBaseMode != 3) {
                this.fillWithBlocks(world, structureBoundingBox, 4, 1, 1, 4, 3, 3, Blocks.air, Blocks.air, false);
            } else {
                this.fillWithBlocks(world, structureBoundingBox, 0, 1, 1, 0, 3, 3, Blocks.air, Blocks.air, false);
            }

            return true;
        }
    }

    public static class Library extends StructureStrongholdPieces.Stronghold {
        public static WeightedRandomChestContent[] strongholdLibraryChestContents = new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Items.book, 0, 1, 3, 20),
            new WeightedRandomChestContent(Items.paper, 0, 2, 7, 20),
            new WeightedRandomChestContent(Items.map, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.compass, 0, 1, 1, 1)
        };
        public boolean isLargeRoom;

        public Library() {
        }

        public Library(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.door = this.getRandomDoor(random);
            this.boundingBox = structureBoundingBox;
            this.isLargeRoom = structureBoundingBox.getYSize() > 6;
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setBoolean("Tall", this.isLargeRoom);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.isLargeRoom = nbttagCompound.getBoolean("Tall");
        }

        public static StructureStrongholdPieces.Library findValidPlacement(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -4, -1, 0, 14, 11, 15, n4);
            if (!canStrongholdGoDeeper(componentToAddBoundingBox) || StructureComponent.findIntersecting(list, componentToAddBoundingBox) != null) {
                componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -4, -1, 0, 14, 6, 15, n4);
                if (!canStrongholdGoDeeper(componentToAddBoundingBox) || StructureComponent.findIntersecting(list, componentToAddBoundingBox) != null) {
                    return null;
                }
            }

            return new StructureStrongholdPieces.Library(n5, random, componentToAddBoundingBox, n4);
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.isLiquidInStructureBoundingBox(world, structureBoundingBox)) {
                return false;
            }

            int n = 11;
            if (!this.isLargeRoom) {
                n = 6;
            }

            this.fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 13, n - 1, 14, true, random, StructureStrongholdPieces.strongholdStones);
            this.placeDoor(world, random, structureBoundingBox, this.door, 4, 1, 0);
            this.randomlyFillWithBlocks(world, structureBoundingBox, random, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.web, Blocks.web, false);
            boolean flag = true;
            boolean flag2 = true;

            for (int i = 1; i <= 13; i++) {
                if ((i - 1) % 4 == 0) {
                    this.fillWithBlocks(world, structureBoundingBox, 1, 1, i, 1, 4, i, Blocks.planks, Blocks.planks, false);
                    this.fillWithBlocks(world, structureBoundingBox, 12, 1, i, 12, 4, i, Blocks.planks, Blocks.planks, false);
                    this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 2, 3, i, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 11, 3, i, structureBoundingBox);
                    if (this.isLargeRoom) {
                        this.fillWithBlocks(world, structureBoundingBox, 1, 6, i, 1, 9, i, Blocks.planks, Blocks.planks, false);
                        this.fillWithBlocks(world, structureBoundingBox, 12, 6, i, 12, 9, i, Blocks.planks, Blocks.planks, false);
                    }
                } else {
                    this.fillWithBlocks(world, structureBoundingBox, 1, 1, i, 1, 4, i, Blocks.bookshelf, Blocks.bookshelf, false);
                    this.fillWithBlocks(world, structureBoundingBox, 12, 1, i, 12, 4, i, Blocks.bookshelf, Blocks.bookshelf, false);
                    if (this.isLargeRoom) {
                        this.fillWithBlocks(world, structureBoundingBox, 1, 6, i, 1, 9, i, Blocks.bookshelf, Blocks.bookshelf, false);
                        this.fillWithBlocks(world, structureBoundingBox, 12, 6, i, 12, 9, i, Blocks.bookshelf, Blocks.bookshelf, false);
                    }
                }
            }

            for (int i = 3; i < 12; i += 2) {
                this.fillWithBlocks(world, structureBoundingBox, 3, 1, i, 4, 3, i, Blocks.bookshelf, Blocks.bookshelf, false);
                this.fillWithBlocks(world, structureBoundingBox, 6, 1, i, 7, 3, i, Blocks.bookshelf, Blocks.bookshelf, false);
                this.fillWithBlocks(world, structureBoundingBox, 9, 1, i, 10, 3, i, Blocks.bookshelf, Blocks.bookshelf, false);
            }

            if (this.isLargeRoom) {
                this.fillWithBlocks(world, structureBoundingBox, 1, 5, 1, 3, 5, 13, Blocks.planks, Blocks.planks, false);
                this.fillWithBlocks(world, structureBoundingBox, 10, 5, 1, 12, 5, 13, Blocks.planks, Blocks.planks, false);
                this.fillWithBlocks(world, structureBoundingBox, 4, 5, 1, 9, 5, 2, Blocks.planks, Blocks.planks, false);
                this.fillWithBlocks(world, structureBoundingBox, 4, 5, 12, 9, 5, 13, Blocks.planks, Blocks.planks, false);
                this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 9, 5, 11, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 8, 5, 11, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 9, 5, 10, structureBoundingBox);
                this.fillWithBlocks(world, structureBoundingBox, 3, 6, 2, 3, 6, 12, Blocks.fence, Blocks.fence, false);
                this.fillWithBlocks(world, structureBoundingBox, 10, 6, 2, 10, 6, 10, Blocks.fence, Blocks.fence, false);
                this.fillWithBlocks(world, structureBoundingBox, 4, 6, 2, 9, 6, 2, Blocks.fence, Blocks.fence, false);
                this.fillWithBlocks(world, structureBoundingBox, 4, 6, 12, 8, 6, 12, Blocks.fence, Blocks.fence, false);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 9, 6, 11, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 8, 6, 11, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 9, 6, 10, structureBoundingBox);
                int metadataWithOffset = this.getMetadataWithOffset(Blocks.ladder, 3);
                this.placeBlockAtCurrentPosition(world, Blocks.ladder, metadataWithOffset, 10, 1, 13, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.ladder, metadataWithOffset, 10, 2, 13, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.ladder, metadataWithOffset, 10, 3, 13, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.ladder, metadataWithOffset, 10, 4, 13, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.ladder, metadataWithOffset, 10, 5, 13, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.ladder, metadataWithOffset, 10, 6, 13, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.ladder, metadataWithOffset, 10, 7, 13, structureBoundingBox);
                byte b = 7;
                byte b2 = 7;
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, b - 1, 9, b2, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, b, 9, b2, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, b - 1, 8, b2, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, b, 8, b2, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, b - 1, 7, b2, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, b, 7, b2, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, b - 2, 7, b2, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, b + 1, 7, b2, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, b - 1, 7, b2 - 1, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, b - 1, 7, b2 + 1, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, b, 7, b2 - 1, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, b, 7, b2 + 1, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, b - 2, 8, b2, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, b + 1, 8, b2, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, b - 1, 8, b2 - 1, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, b - 1, 8, b2 + 1, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, b, 8, b2 - 1, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, b, 8, b2 + 1, structureBoundingBox);
            }

            this.generateStructureChestContents(
                world,
                structureBoundingBox,
                random,
                3,
                3,
                5,
                WeightedRandomChestContent.union(strongholdLibraryChestContents, Items.enchanted_book.getLootTable(random, 1, 5, 2)),
                1 + random.nextInt(4)
            );
            if (this.isLargeRoom) {
                this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 12, 9, 1, structureBoundingBox);
                this.generateStructureChestContents(
                    world,
                    structureBoundingBox,
                    random,
                    12,
                    8,
                    1,
                    WeightedRandomChestContent.union(strongholdLibraryChestContents, Items.enchanted_book.getLootTable(random, 1, 5, 2)),
                    1 + random.nextInt(4)
                );
            }

            return true;
        }
    }

    public static class PieceWeight {
        public Class pieceClass;
        public int pieceWeight;
        public int instancesSpawned;
        public int instancesLimit;

        public PieceWeight(Class clazz, int n, int n2) {
            this.pieceClass = clazz;
            this.pieceWeight = n;
            this.instancesLimit = n2;
        }

        public boolean canSpawnMoreStructuresOfType(int n) {
            return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
        }

        public boolean canSpawnMoreStructures() {
            return this.instancesLimit == 0 || this.instancesSpawned < this.instancesLimit;
        }
    }

    public static class PortalRoom extends StructureStrongholdPieces.Stronghold {
        public boolean hasSpawner;

        public PortalRoom() {
        }

        public PortalRoom(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setBoolean("Mob", this.hasSpawner);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.hasSpawner = nbttagCompound.getBoolean("Mob");
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            if (structureComponent != null) {
                ((StructureStrongholdPieces.Stairs2)structureComponent).strongholdPortalRoom = this;
            }
        }

        public static StructureStrongholdPieces.PortalRoom findValidPlacement(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -4, -1, 0, 11, 8, 16, n4);
            return canStrongholdGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureStrongholdPieces.PortalRoom(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 10, 7, 15, false, random, StructureStrongholdPieces.strongholdStones);
            this.placeDoor(world, random, structureBoundingBox, StructureStrongholdPieces.Stronghold.Door.GRATES, 4, 1, 0);
            byte b = 6;
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 1, b, 1, 1, b, 14, false, random, StructureStrongholdPieces.strongholdStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 9, b, 1, 9, b, 14, false, random, StructureStrongholdPieces.strongholdStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 2, b, 1, 8, b, 2, false, random, StructureStrongholdPieces.strongholdStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 2, b, 14, 8, b, 14, false, random, StructureStrongholdPieces.strongholdStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 1, 1, 1, 2, 1, 4, false, random, StructureStrongholdPieces.strongholdStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 8, 1, 1, 9, 1, 4, false, random, StructureStrongholdPieces.strongholdStones);
            this.fillWithBlocks(world, structureBoundingBox, 1, 1, 1, 1, 1, 3, Blocks.flowing_lava, Blocks.flowing_lava, false);
            this.fillWithBlocks(world, structureBoundingBox, 9, 1, 1, 9, 1, 3, Blocks.flowing_lava, Blocks.flowing_lava, false);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 3, 1, 8, 7, 1, 12, false, random, StructureStrongholdPieces.strongholdStones);
            this.fillWithBlocks(world, structureBoundingBox, 4, 1, 9, 6, 1, 11, Blocks.flowing_lava, Blocks.flowing_lava, false);

            for (int i = 3; i < 14; i += 2) {
                this.fillWithBlocks(world, structureBoundingBox, 0, 3, i, 0, 4, i, Blocks.iron_bars, Blocks.iron_bars, false);
                this.fillWithBlocks(world, structureBoundingBox, 10, 3, i, 10, 4, i, Blocks.iron_bars, Blocks.iron_bars, false);
            }

            for (int i = 2; i < 9; i += 2) {
                this.fillWithBlocks(world, structureBoundingBox, i, 3, 15, i, 4, 15, Blocks.iron_bars, Blocks.iron_bars, false);
            }

            int metadataWithOffset = this.getMetadataWithOffset(Blocks.stone_brick_stairs, 3);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 4, 1, 5, 6, 1, 7, false, random, StructureStrongholdPieces.strongholdStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 4, 2, 6, 6, 2, 7, false, random, StructureStrongholdPieces.strongholdStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 4, 3, 7, 6, 3, 7, false, random, StructureStrongholdPieces.strongholdStones);

            for (int i = 4; i <= 6; i++) {
                this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, metadataWithOffset, i, 1, 4, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, metadataWithOffset, i, 2, 5, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.stone_brick_stairs, metadataWithOffset, i, 3, 6, structureBoundingBox);
            }

            int n = 2;
            int n2 = 0;
            int n3 = 3;
            int n4 = 1;
            switch (this.coordBaseMode) {
                case 0:
                    n = 0;
                    n2 = 2;
                    break;
                case 1:
                    n = 1;
                    n2 = 3;
                    n3 = 0;
                    n4 = 2;
                case 2:
                default:
                    break;
                case 3:
                    n = 3;
                    n2 = 1;
                    n3 = 0;
                    n4 = 2;
            }

            this.placeBlockAtCurrentPosition(world, Blocks.end_portal_frame, n + (random.nextFloat() > 0.9F ? 4 : 0), 4, 3, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.end_portal_frame, n + (random.nextFloat() > 0.9F ? 4 : 0), 5, 3, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.end_portal_frame, n + (random.nextFloat() > 0.9F ? 4 : 0), 6, 3, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.end_portal_frame, n2 + (random.nextFloat() > 0.9F ? 4 : 0), 4, 3, 12, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.end_portal_frame, n2 + (random.nextFloat() > 0.9F ? 4 : 0), 5, 3, 12, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.end_portal_frame, n2 + (random.nextFloat() > 0.9F ? 4 : 0), 6, 3, 12, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.end_portal_frame, n3 + (random.nextFloat() > 0.9F ? 4 : 0), 3, 3, 9, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.end_portal_frame, n3 + (random.nextFloat() > 0.9F ? 4 : 0), 3, 3, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.end_portal_frame, n3 + (random.nextFloat() > 0.9F ? 4 : 0), 3, 3, 11, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.end_portal_frame, n4 + (random.nextFloat() > 0.9F ? 4 : 0), 7, 3, 9, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.end_portal_frame, n4 + (random.nextFloat() > 0.9F ? 4 : 0), 7, 3, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.end_portal_frame, n4 + (random.nextFloat() > 0.9F ? 4 : 0), 7, 3, 11, structureBoundingBox);
            if (!this.hasSpawner) {
                int yWithOffset = this.getYWithOffset(3);
                int xWithOffset = this.getXWithOffset(5, 6);
                int zWithOffset = this.getZWithOffset(5, 6);
                if (structureBoundingBox.isVecInside(xWithOffset, yWithOffset, zWithOffset)) {
                    this.hasSpawner = true;
                    world.setBlock(xWithOffset, yWithOffset, zWithOffset, Blocks.mob_spawner, 0, 2);
                    TileEntityMobSpawner tileEntity = (TileEntityMobSpawner)world.getTileEntity(xWithOffset, yWithOffset, zWithOffset);
                    if (tileEntity != null) {
                        tileEntity.getLogic().setEntityName("Silverfish");
                    }
                }
            }

            return true;
        }
    }

    public static class Prison extends StructureStrongholdPieces.Stronghold {
        public Prison() {
        }

        public Prison(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.door = this.getRandomDoor(random);
            this.boundingBox = structureBoundingBox;
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)structureComponent, list, random, 1, 1);
        }

        public static StructureStrongholdPieces.Prison findValidPlacement(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -1, 0, 9, 5, 11, n4);
            return canStrongholdGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureStrongholdPieces.Prison(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.isLiquidInStructureBoundingBox(world, structureBoundingBox)) {
                return false;
            }

            this.fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 8, 4, 10, true, random, StructureStrongholdPieces.strongholdStones);
            this.placeDoor(world, random, structureBoundingBox, this.door, 1, 1, 0);
            this.fillWithBlocks(world, structureBoundingBox, 1, 1, 10, 3, 3, 10, Blocks.air, Blocks.air, false);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 4, 1, 1, 4, 3, 1, false, random, StructureStrongholdPieces.strongholdStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 4, 1, 3, 4, 3, 3, false, random, StructureStrongholdPieces.strongholdStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 4, 1, 7, 4, 3, 7, false, random, StructureStrongholdPieces.strongholdStones);
            this.fillWithRandomizedBlocks(world, structureBoundingBox, 4, 1, 9, 4, 3, 9, false, random, StructureStrongholdPieces.strongholdStones);
            this.fillWithBlocks(world, structureBoundingBox, 4, 1, 4, 4, 3, 6, Blocks.iron_bars, Blocks.iron_bars, false);
            this.fillWithBlocks(world, structureBoundingBox, 5, 1, 5, 7, 3, 5, Blocks.iron_bars, Blocks.iron_bars, false);
            this.placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, 4, 3, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, 4, 3, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.iron_door, this.getMetadataWithOffset(Blocks.iron_door, 3), 4, 1, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.iron_door, this.getMetadataWithOffset(Blocks.iron_door, 3) + 8, 4, 2, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.iron_door, this.getMetadataWithOffset(Blocks.iron_door, 3), 4, 1, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.iron_door, this.getMetadataWithOffset(Blocks.iron_door, 3) + 8, 4, 2, 8, structureBoundingBox);
            return true;
        }
    }

    public static class RightTurn extends StructureStrongholdPieces.LeftTurn {
        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            if (this.coordBaseMode != 2 && this.coordBaseMode != 3) {
                this.getNextComponentX((StructureStrongholdPieces.Stairs2)structureComponent, list, random, 1, 1);
            } else {
                this.getNextComponentZ((StructureStrongholdPieces.Stairs2)structureComponent, list, random, 1, 1);
            }
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.isLiquidInStructureBoundingBox(world, structureBoundingBox)) {
                return false;
            }

            this.fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 4, 4, 4, true, random, StructureStrongholdPieces.strongholdStones);
            this.placeDoor(world, random, structureBoundingBox, this.door, 1, 1, 0);
            if (this.coordBaseMode != 2 && this.coordBaseMode != 3) {
                this.fillWithBlocks(world, structureBoundingBox, 0, 1, 1, 0, 3, 3, Blocks.air, Blocks.air, false);
            } else {
                this.fillWithBlocks(world, structureBoundingBox, 4, 1, 1, 4, 3, 3, Blocks.air, Blocks.air, false);
            }

            return true;
        }
    }

    public static class RoomCrossing extends StructureStrongholdPieces.Stronghold {
        public static WeightedRandomChestContent[] strongholdRoomCrossingChestContents = new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
            new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5),
            new WeightedRandomChestContent(Items.redstone, 0, 4, 9, 5),
            new WeightedRandomChestContent(Items.coal, 0, 3, 8, 10),
            new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15),
            new WeightedRandomChestContent(Items.apple, 0, 1, 3, 15),
            new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 1)
        };
        public int roomType;

        public RoomCrossing() {
        }

        public RoomCrossing(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.door = this.getRandomDoor(random);
            this.boundingBox = structureBoundingBox;
            this.roomType = random.nextInt(5);
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setInteger("Type", this.roomType);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.roomType = nbttagCompound.getInteger("Type");
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)structureComponent, list, random, 4, 1);
            this.getNextComponentX((StructureStrongholdPieces.Stairs2)structureComponent, list, random, 1, 4);
            this.getNextComponentZ((StructureStrongholdPieces.Stairs2)structureComponent, list, random, 1, 4);
        }

        public static StructureStrongholdPieces.RoomCrossing findValidPlacement(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -4, -1, 0, 11, 7, 11, n4);
            return canStrongholdGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureStrongholdPieces.RoomCrossing(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.isLiquidInStructureBoundingBox(world, structureBoundingBox)) {
                return false;
            }

            this.fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 10, 6, 10, true, random, StructureStrongholdPieces.strongholdStones);
            this.placeDoor(world, random, structureBoundingBox, this.door, 4, 1, 0);
            this.fillWithBlocks(world, structureBoundingBox, 4, 1, 10, 6, 3, 10, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 1, 4, 0, 3, 6, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 10, 1, 4, 10, 3, 6, Blocks.air, Blocks.air, false);
            switch (this.roomType) {
                case 0:
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 5, 1, 5, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 5, 2, 5, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 5, 3, 5, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 4, 3, 5, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 6, 3, 5, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 5, 3, 4, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 5, 3, 6, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 4, 1, 4, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 4, 1, 5, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 4, 1, 6, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 6, 1, 4, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 6, 1, 5, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 6, 1, 6, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 5, 1, 4, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 5, 1, 6, structureBoundingBox);
                    break;
                case 1:
                    for (int i = 0; i < 5; i++) {
                        this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 3, 1, 3 + i, structureBoundingBox);
                        this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 7, 1, 3 + i, structureBoundingBox);
                        this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 3 + i, 1, 3, structureBoundingBox);
                        this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 3 + i, 1, 7, structureBoundingBox);
                    }

                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 5, 1, 5, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 5, 2, 5, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 5, 3, 5, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.flowing_water, 0, 5, 4, 5, structureBoundingBox);
                    break;
                case 2:
                    for (int i = 1; i <= 9; i++) {
                        this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 1, 3, i, structureBoundingBox);
                        this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 9, 3, i, structureBoundingBox);
                    }

                    for (int i = 1; i <= 9; i++) {
                        this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, i, 3, 1, structureBoundingBox);
                        this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, i, 3, 9, structureBoundingBox);
                    }

                    this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 5, 1, 4, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 5, 1, 6, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 5, 3, 4, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 5, 3, 6, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 4, 1, 5, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 6, 1, 5, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 4, 3, 5, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 6, 3, 5, structureBoundingBox);

                    for (int i = 1; i <= 3; i++) {
                        this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 4, i, 4, structureBoundingBox);
                        this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 6, i, 4, structureBoundingBox);
                        this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 4, i, 6, structureBoundingBox);
                        this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 6, i, 6, structureBoundingBox);
                    }

                    this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 5, 3, 5, structureBoundingBox);

                    for (int i = 2; i <= 8; i++) {
                        this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 2, 3, i, structureBoundingBox);
                        this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 3, 3, i, structureBoundingBox);
                        if (i <= 3 || i >= 7) {
                            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 4, 3, i, structureBoundingBox);
                            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 5, 3, i, structureBoundingBox);
                            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 6, 3, i, structureBoundingBox);
                        }

                        this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 7, 3, i, structureBoundingBox);
                        this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 8, 3, i, structureBoundingBox);
                    }

                    this.placeBlockAtCurrentPosition(world, Blocks.ladder, this.getMetadataWithOffset(Blocks.ladder, 4), 9, 1, 3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.ladder, this.getMetadataWithOffset(Blocks.ladder, 4), 9, 2, 3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.ladder, this.getMetadataWithOffset(Blocks.ladder, 4), 9, 3, 3, structureBoundingBox);
                    this.generateStructureChestContents(
                        world,
                        structureBoundingBox,
                        random,
                        3,
                        4,
                        8,
                        WeightedRandomChestContent.union(strongholdRoomCrossingChestContents, Items.enchanted_book.getLootTable(random)),
                        1 + random.nextInt(4)
                    );
            }

            return true;
        }
    }

    public static class Stairs extends StructureStrongholdPieces.Stronghold {
        public boolean flag;

        public Stairs() {
        }

        public Stairs(int n, Random random, int n2, int n3) {
            super(n);
            this.flag = true;
            this.coordBaseMode = random.nextInt(4);
            this.door = StructureStrongholdPieces.Stronghold.Door.OPENING;
            switch (this.coordBaseMode) {
                case 0:
                case 2:
                    this.boundingBox = new StructureBoundingBox(n2, 64, n3, n2 + 5 - 1, 74, n3 + 5 - 1);
                    break;
                default:
                    this.boundingBox = new StructureBoundingBox(n2, 64, n3, n2 + 5 - 1, 74, n3 + 5 - 1);
            }
        }

        public Stairs(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.flag = false;
            this.coordBaseMode = n2;
            this.door = this.getRandomDoor(random);
            this.boundingBox = structureBoundingBox;
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setBoolean("Source", this.flag);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.flag = nbttagCompound.getBoolean("Source");
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            if (this.flag) {
                StructureStrongholdPieces.strongComponentType = StructureStrongholdPieces.Crossing.class;
            }

            this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)structureComponent, list, random, 1, 1);
        }

        public static StructureStrongholdPieces.Stairs getStrongholdStairsComponent(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -7, 0, 5, 11, 5, n4);
            return canStrongholdGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureStrongholdPieces.Stairs(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.isLiquidInStructureBoundingBox(world, structureBoundingBox)) {
                return false;
            }

            this.fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 4, 10, 4, true, random, StructureStrongholdPieces.strongholdStones);
            this.placeDoor(world, random, structureBoundingBox, this.door, 1, 7, 0);
            this.placeDoor(world, random, structureBoundingBox, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 4);
            this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 2, 6, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 1, 5, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 1, 6, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 1, 5, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 1, 4, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 1, 5, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 2, 4, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 3, 3, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 3, 4, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 3, 3, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 3, 2, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 3, 3, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 2, 2, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 1, 1, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 1, 2, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 1, 1, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, 1, 1, 3, structureBoundingBox);
            return true;
        }
    }

    public static class Stairs2 extends StructureStrongholdPieces.Stairs {
        public StructureStrongholdPieces.PieceWeight strongholdPieceWeight;
        public StructureStrongholdPieces.PortalRoom strongholdPortalRoom;
        public List list = new ArrayList();

        public Stairs2() {
        }

        public Stairs2(int n, Random random, int n2, int n3) {
            super(0, random, n2, n3);
        }

        @Override
        public ChunkPosition createChunkPosition() {
            return this.strongholdPortalRoom != null ? this.strongholdPortalRoom.createChunkPosition() : super.createChunkPosition();
        }
    }

    public static class StairsStraight extends StructureStrongholdPieces.Stronghold {
        public StairsStraight() {
        }

        public StairsStraight(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.door = this.getRandomDoor(random);
            this.boundingBox = structureBoundingBox;
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)structureComponent, list, random, 1, 1);
        }

        public static StructureStrongholdPieces.StairsStraight findValidPlacement(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -7, 0, 5, 11, 8, n4);
            return canStrongholdGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureStrongholdPieces.StairsStraight(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.isLiquidInStructureBoundingBox(world, structureBoundingBox)) {
                return false;
            }

            this.fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 4, 10, 7, true, random, StructureStrongholdPieces.strongholdStones);
            this.placeDoor(world, random, structureBoundingBox, this.door, 1, 7, 0);
            this.placeDoor(world, random, structureBoundingBox, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 7);
            int metadataWithOffset = this.getMetadataWithOffset(Blocks.stone_stairs, 2);

            for (int i = 0; i < 6; i++) {
                this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset, 1, 6 - i, 1 + i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset, 2, 6 - i, 1 + i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, metadataWithOffset, 3, 6 - i, 1 + i, structureBoundingBox);
                if (i < 5) {
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 1, 5 - i, 1 + i, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 2, 5 - i, 1 + i, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 3, 5 - i, 1 + i, structureBoundingBox);
                }
            }

            return true;
        }
    }

    public static class Stones extends BlockSelector {
        public Stones() {
        }

        @Override
        public void selectBlocks(Random random, int n, int n2, int n3, boolean flag) {
            if (flag) {
                this.block = Blocks.stonebrick;
                float nextFloat = random.nextFloat();
                if (nextFloat < 0.2F) {
                    this.selectedBlockMetaData = 2;
                } else if (nextFloat < 0.5F) {
                    this.selectedBlockMetaData = 1;
                } else if (nextFloat < 0.55F) {
                    this.block = Blocks.monster_egg;
                    this.selectedBlockMetaData = 2;
                } else {
                    this.selectedBlockMetaData = 0;
                }
            } else {
                this.block = Blocks.air;
                this.selectedBlockMetaData = 0;
            }
        }

        public Stones(Object obj) {
            this();
        }
    }

    public static class Straight extends StructureStrongholdPieces.Stronghold {
        public boolean expandsX;
        public boolean expandsZ;

        public Straight() {
        }

        public Straight(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.door = this.getRandomDoor(random);
            this.boundingBox = structureBoundingBox;
            this.expandsX = random.nextInt(2) == 0;
            this.expandsZ = random.nextInt(2) == 0;
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setBoolean("Left", this.expandsX);
            nbttagCompound.setBoolean("Right", this.expandsZ);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.expandsX = nbttagCompound.getBoolean("Left");
            this.expandsZ = nbttagCompound.getBoolean("Right");
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)structureComponent, list, random, 1, 1);
            if (this.expandsX) {
                this.getNextComponentX((StructureStrongholdPieces.Stairs2)structureComponent, list, random, 1, 2);
            }

            if (this.expandsZ) {
                this.getNextComponentZ((StructureStrongholdPieces.Stairs2)structureComponent, list, random, 1, 2);
            }
        }

        public static StructureStrongholdPieces.Straight findValidPlacement(List list, Random random, int n, int n2, int n3, int n4, int n5) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -1, 0, 5, 5, 7, n4);
            return canStrongholdGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureStrongholdPieces.Straight(n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.isLiquidInStructureBoundingBox(world, structureBoundingBox)) {
                return false;
            }

            this.fillWithRandomizedBlocks(world, structureBoundingBox, 0, 0, 0, 4, 4, 6, true, random, StructureStrongholdPieces.strongholdStones);
            this.placeDoor(world, random, structureBoundingBox, this.door, 1, 1, 0);
            this.placeDoor(world, random, structureBoundingBox, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
            this.handleWorld(world, structureBoundingBox, random, 0.1F, 1, 2, 1, Blocks.torch, 0);
            this.handleWorld(world, structureBoundingBox, random, 0.1F, 3, 2, 1, Blocks.torch, 0);
            this.handleWorld(world, structureBoundingBox, random, 0.1F, 1, 2, 5, Blocks.torch, 0);
            this.handleWorld(world, structureBoundingBox, random, 0.1F, 3, 2, 5, Blocks.torch, 0);
            if (this.expandsX) {
                this.fillWithBlocks(world, structureBoundingBox, 0, 1, 2, 0, 3, 4, Blocks.air, Blocks.air, false);
            }

            if (this.expandsZ) {
                this.fillWithBlocks(world, structureBoundingBox, 4, 1, 2, 4, 3, 4, Blocks.air, Blocks.air, false);
            }

            return true;
        }
    }

    public abstract static class Stronghold extends StructureComponent {
        public StructureStrongholdPieces.Stronghold.Door door = StructureStrongholdPieces.Stronghold.Door.OPENING;

        public Stronghold() {
        }

        public Stronghold(int n) {
            super(n);
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            nbttagCompound.setString("EntryDoor", this.door.name());
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            this.door = StructureStrongholdPieces.Stronghold.Door.valueOf(nbttagCompound.getString("EntryDoor"));
        }

        public void placeDoor(World world, Random random, StructureBoundingBox structureBoundingBox, StructureStrongholdPieces.Stronghold.Door door, int n, int n2, int n3) {
            switch (door) {
                case OPENING:
                default:
                    this.fillWithBlocks(world, structureBoundingBox, n, n2, n3, n + 3 - 1, n2 + 3 - 1, n3, Blocks.air, Blocks.air, false);
                    break;
                case WOOD_DOOR:
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, n, n2, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, n, n2 + 1, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, n, n2 + 2, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, n + 1, n2 + 2, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, n + 2, n2 + 2, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, n + 2, n2 + 1, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, n + 2, n2, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.wooden_door, 0, n + 1, n2, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.wooden_door, 8, n + 1, n2 + 1, n3, structureBoundingBox);
                    break;
                case GRATES:
                    this.placeBlockAtCurrentPosition(world, Blocks.air, 0, n + 1, n2, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.air, 0, n + 1, n2 + 1, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, n, n2, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, n, n2 + 1, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, n, n2 + 2, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, n + 1, n2 + 2, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, n + 2, n2 + 2, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, n + 2, n2 + 1, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, n + 2, n2, n3, structureBoundingBox);
                    break;
                case IRON_DOOR:
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, n, n2, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, n, n2 + 1, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, n, n2 + 2, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, n + 1, n2 + 2, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, n + 2, n2 + 2, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, n + 2, n2 + 1, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, n + 2, n2, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.iron_door, 0, n + 1, n2, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.iron_door, 8, n + 1, n2 + 1, n3, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(
                        world, Blocks.stone_button, this.getMetadataWithOffset(Blocks.stone_button, 4), n + 2, n2 + 1, n3 + 1, structureBoundingBox
                    );
                    this.placeBlockAtCurrentPosition(
                        world, Blocks.stone_button, this.getMetadataWithOffset(Blocks.stone_button, 3), n + 2, n2 + 1, n3 - 1, structureBoundingBox
                    );
            }
        }

        public StructureStrongholdPieces.Stronghold.Door getRandomDoor(Random random) {
            int nextInt = random.nextInt(5);
            switch (nextInt) {
                case 0:
                case 1:
                default:
                    return StructureStrongholdPieces.Stronghold.Door.OPENING;
                case 2:
                    return StructureStrongholdPieces.Stronghold.Door.WOOD_DOOR;
                case 3:
                    return StructureStrongholdPieces.Stronghold.Door.GRATES;
                case 4:
                    return StructureStrongholdPieces.Stronghold.Door.IRON_DOOR;
            }
        }

        public StructureComponent getNextComponentNormal(StructureStrongholdPieces.Stairs2 stairs2, List list, Random random, int n, int n2) {
            switch (this.coordBaseMode) {
                case 0:
                    return StructureStrongholdPieces.getNextValidComponent(
                        stairs2,
                        list,
                        random,
                        this.boundingBox.minX + n,
                        this.boundingBox.minY + n2,
                        this.boundingBox.maxZ + 1,
                        this.coordBaseMode,
                        this.getComponentType()
                    );
                case 1:
                    return StructureStrongholdPieces.getNextValidComponent(
                        stairs2,
                        list,
                        random,
                        this.boundingBox.minX - 1,
                        this.boundingBox.minY + n2,
                        this.boundingBox.minZ + n,
                        this.coordBaseMode,
                        this.getComponentType()
                    );
                case 2:
                    return StructureStrongholdPieces.getNextValidComponent(
                        stairs2,
                        list,
                        random,
                        this.boundingBox.minX + n,
                        this.boundingBox.minY + n2,
                        this.boundingBox.minZ - 1,
                        this.coordBaseMode,
                        this.getComponentType()
                    );
                case 3:
                    return StructureStrongholdPieces.getNextValidComponent(
                        stairs2,
                        list,
                        random,
                        this.boundingBox.maxX + 1,
                        this.boundingBox.minY + n2,
                        this.boundingBox.minZ + n,
                        this.coordBaseMode,
                        this.getComponentType()
                    );
                default:
                    return null;
            }
        }

        public StructureComponent getNextComponentX(StructureStrongholdPieces.Stairs2 stairs2, List list, Random random, int n, int n2) {
            switch (this.coordBaseMode) {
                case 0:
                    return StructureStrongholdPieces.getNextValidComponent(
                        stairs2, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + n, this.boundingBox.minZ + n2, 1, this.getComponentType()
                    );
                case 1:
                    return StructureStrongholdPieces.getNextValidComponent(
                        stairs2, list, random, this.boundingBox.minX + n2, this.boundingBox.minY + n, this.boundingBox.minZ - 1, 2, this.getComponentType()
                    );
                case 2:
                    return StructureStrongholdPieces.getNextValidComponent(
                        stairs2, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + n, this.boundingBox.minZ + n2, 1, this.getComponentType()
                    );
                case 3:
                    return StructureStrongholdPieces.getNextValidComponent(
                        stairs2, list, random, this.boundingBox.minX + n2, this.boundingBox.minY + n, this.boundingBox.minZ - 1, 2, this.getComponentType()
                    );
                default:
                    return null;
            }
        }

        public StructureComponent getNextComponentZ(StructureStrongholdPieces.Stairs2 stairs2, List list, Random random, int n, int n2) {
            switch (this.coordBaseMode) {
                case 0:
                    return StructureStrongholdPieces.getNextValidComponent(
                        stairs2, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + n, this.boundingBox.minZ + n2, 3, this.getComponentType()
                    );
                case 1:
                    return StructureStrongholdPieces.getNextValidComponent(
                        stairs2, list, random, this.boundingBox.minX + n2, this.boundingBox.minY + n, this.boundingBox.maxZ + 1, 0, this.getComponentType()
                    );
                case 2:
                    return StructureStrongholdPieces.getNextValidComponent(
                        stairs2, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + n, this.boundingBox.minZ + n2, 3, this.getComponentType()
                    );
                case 3:
                    return StructureStrongholdPieces.getNextValidComponent(
                        stairs2, list, random, this.boundingBox.minX + n2, this.boundingBox.minY + n, this.boundingBox.maxZ + 1, 0, this.getComponentType()
                    );
                default:
                    return null;
            }
        }

        public static boolean canStrongholdGoDeeper(StructureBoundingBox structureBoundingBox) {
            return structureBoundingBox != null && structureBoundingBox.minY > 10;
        }

        public enum Door {
            OPENING("OPENING", 0),
            WOOD_DOOR("WOOD_DOOR", 1),
            GRATES("GRATES", 2),
            IRON_DOOR("IRON_DOOR", 3);

            public static StructureStrongholdPieces.Stronghold.Door[] doorArray = new StructureStrongholdPieces.Stronghold.Door[]{
                OPENING, WOOD_DOOR, GRATES, IRON_DOOR
            };

            Door(String str, int n) {
            }
        }
    }
}
