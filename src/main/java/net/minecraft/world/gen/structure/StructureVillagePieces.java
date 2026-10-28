package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

public class StructureVillagePieces {
    public static void registerVillagePieces() {
        MapGenStructureIO.registerStructureComponent(StructureVillagePieces.House1.class, "ViBH");
        MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Field1.class, "ViDF");
        MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Field2.class, "ViF");
        MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Torch.class, "ViL");
        MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Hall.class, "ViPH");
        MapGenStructureIO.registerStructureComponent(StructureVillagePieces.House4Garden.class, "ViSH");
        MapGenStructureIO.registerStructureComponent(StructureVillagePieces.WoodHut.class, "ViSmH");
        MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Church.class, "ViST");
        MapGenStructureIO.registerStructureComponent(StructureVillagePieces.House2.class, "ViS");
        MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Start.class, "ViStart");
        MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Path.class, "ViSR");
        MapGenStructureIO.registerStructureComponent(StructureVillagePieces.House3.class, "ViTRH");
        MapGenStructureIO.registerStructureComponent(StructureVillagePieces.Well.class, "ViW");
    }

    public static List getStructureVillageWeightedPieceList(Random random, int n) {
        ArrayList list = new ArrayList();
        list.add(
            new StructureVillagePieces.PieceWeight(StructureVillagePieces.House4Garden.class, 4, MathHelper.getRandomIntegerInRange(random, 2 + n, 4 + n * 2))
        );
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Church.class, 20, MathHelper.getRandomIntegerInRange(random, 0 + n, 1 + n)));
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House1.class, 20, MathHelper.getRandomIntegerInRange(random, 0 + n, 2 + n)));
        list.add(
            new StructureVillagePieces.PieceWeight(StructureVillagePieces.WoodHut.class, 3, MathHelper.getRandomIntegerInRange(random, 2 + n, 5 + n * 3))
        );
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Hall.class, 15, MathHelper.getRandomIntegerInRange(random, 0 + n, 2 + n)));
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Field1.class, 3, MathHelper.getRandomIntegerInRange(random, 1 + n, 4 + n)));
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Field2.class, 3, MathHelper.getRandomIntegerInRange(random, 2 + n, 4 + n * 2)));
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House2.class, 15, MathHelper.getRandomIntegerInRange(random, 0, 1 + n)));
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House3.class, 8, MathHelper.getRandomIntegerInRange(random, 0 + n, 3 + n * 2)));
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            if (((StructureVillagePieces.PieceWeight)iterator.next()).villagePiecesLimit == 0) {
                iterator.remove();
            }
        }

        return list;
    }

    public static int getTotalWeight(List list) {
        boolean flag = false;
        int n = 0;

        for (StructureVillagePieces.PieceWeight pieceWeight : (Iterable<StructureVillagePieces.PieceWeight>)list) {
            if (pieceWeight.villagePiecesLimit > 0 && pieceWeight.villagePiecesSpawned < pieceWeight.villagePiecesLimit) {
                flag = true;
            }

            n += pieceWeight.villagePieceWeight;
        }

        return flag ? n : -1;
    }

    public static StructureVillagePieces.Village findVillage(
        StructureVillagePieces.Start start, StructureVillagePieces.PieceWeight pieceWeight, List list, Random random, int n, int n2, int n3, int n4, int n5
    ) {
        Class villagePieceClass = pieceWeight.villagePieceClass;
        StructureVillagePieces.Village village = null;
        if (villagePieceClass == StructureVillagePieces.House4Garden.class) {
            village = StructureVillagePieces.House4Garden.createHouse4Garden(start, list, random, n, n2, n3, n4, n5);
        } else if (villagePieceClass == StructureVillagePieces.Church.class) {
            village = StructureVillagePieces.Church.createChurch(start, list, random, n, n2, n3, n4, n5);
        } else if (villagePieceClass == StructureVillagePieces.House1.class) {
            village = StructureVillagePieces.House1.createHouse1(start, list, random, n, n2, n3, n4, n5);
        } else if (villagePieceClass == StructureVillagePieces.WoodHut.class) {
            village = StructureVillagePieces.WoodHut.createWoodHut(start, list, random, n, n2, n3, n4, n5);
        } else if (villagePieceClass == StructureVillagePieces.Hall.class) {
            village = StructureVillagePieces.Hall.createHall(start, list, random, n, n2, n3, n4, n5);
        } else if (villagePieceClass == StructureVillagePieces.Field1.class) {
            village = StructureVillagePieces.Field1.createField1(start, list, random, n, n2, n3, n4, n5);
        } else if (villagePieceClass == StructureVillagePieces.Field2.class) {
            village = StructureVillagePieces.Field2.createField2(start, list, random, n, n2, n3, n4, n5);
        } else if (villagePieceClass == StructureVillagePieces.House2.class) {
            village = StructureVillagePieces.House2.createHouse2(start, list, random, n, n2, n3, n4, n5);
        } else if (villagePieceClass == StructureVillagePieces.House3.class) {
            village = StructureVillagePieces.House3.createHouse3(start, list, random, n, n2, n3, n4, n5);
        }

        return village;
    }

    public static StructureVillagePieces.Village getNextVillageComponent(
        StructureVillagePieces.Start start, List list, Random random, int n, int n2, int n3, int n4, int n5
    ) {
        int totalWeight = getTotalWeight(start.structureVillageWeightedPieceList);
        if (totalWeight <= 0) {
            return null;
        }

        int n6 = 0;

        while (n6 < 5) {
            n6++;
            int nextInt = random.nextInt(totalWeight);

            for (StructureVillagePieces.PieceWeight pieceWeight : (Iterable<StructureVillagePieces.PieceWeight>)start.structureVillageWeightedPieceList) {
                nextInt -= pieceWeight.villagePieceWeight;
                if (nextInt < 0) {
                    if (!pieceWeight.canSpawnMoreVillagePiecesOfType(n5) || pieceWeight == start.structVillagePieceWeight && start.structureVillageWeightedPieceList.size() > 1) {
                        break;
                    }

                    StructureVillagePieces.Village findVillage2 = findVillage(start, pieceWeight, list, random, n, n2, n3, n4, n5);
                    if (findVillage2 != null) {
                        pieceWeight.villagePiecesSpawned++;
                        start.structVillagePieceWeight = pieceWeight;
                        if (!pieceWeight.canSpawnMoreVillagePieces()) {
                            start.structureVillageWeightedPieceList.remove(pieceWeight);
                        }

                        return findVillage2;
                    }
                }
            }
        }

        StructureBoundingBox findStructureBoundingBox = StructureVillagePieces.Torch.findStructureBoundingBox(start, list, random, n, n2, n3, n4);
        return findStructureBoundingBox != null ? new StructureVillagePieces.Torch(start, n5, random, findStructureBoundingBox, n4) : null;
    }

    public static StructureComponent getNextVillageStructureComponent(
        StructureVillagePieces.Start start, List list, Random random, int n, int n2, int n3, int n4, int n5
    ) {
        if (n5 > 50) {
            return null;
        }

        if (Math.abs(n - start.getBoundingBox().minX) <= 112 && Math.abs(n3 - start.getBoundingBox().minZ) <= 112) {
            StructureVillagePieces.Village nextVillageComponent = getNextVillageComponent(start, list, random, n, n2, n3, n4, n5 + 1);
            if (nextVillageComponent != null) {
                int n6 = (nextVillageComponent.boundingBox.minX + nextVillageComponent.boundingBox.maxX) / 2;
                int n7 = (nextVillageComponent.boundingBox.minZ + nextVillageComponent.boundingBox.maxZ) / 2;
                int n8 = nextVillageComponent.boundingBox.maxX - nextVillageComponent.boundingBox.minX;
                int n9 = nextVillageComponent.boundingBox.maxZ - nextVillageComponent.boundingBox.minZ;
                int n10 = n8 > n9 ? n8 : n9;
                if (start.getWorldChunkManager().areBiomesViable(n6, n7, n10 / 2 + 4, MapGenVillage.villageSpawnBiomes)) {
                    list.add(nextVillageComponent);
                    start.list.add(nextVillageComponent);
                    return nextVillageComponent;
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public static StructureComponent getNextComponentVillagePath(
        StructureVillagePieces.Start start, List list, Random random, int n, int n2, int n3, int n4, int n5
    ) {
        if (n5 > 3 + start.terrainType) {
            return null;
        }

        if (Math.abs(n - start.getBoundingBox().minX) <= 112 && Math.abs(n3 - start.getBoundingBox().minZ) <= 112) {
            StructureBoundingBox findStructureBoundingBox = StructureVillagePieces.Path.findStructureBoundingBox(start, list, random, n, n2, n3, n4);
            if (findStructureBoundingBox != null && findStructureBoundingBox.minY > 10) {
                StructureVillagePieces.Path path = new StructureVillagePieces.Path(start, n5, random, findStructureBoundingBox, n4);
                int n6 = (path.boundingBox.minX + path.boundingBox.maxX) / 2;
                int n7 = (path.boundingBox.minZ + path.boundingBox.maxZ) / 2;
                int n8 = path.boundingBox.maxX - path.boundingBox.minX;
                int n9 = path.boundingBox.maxZ - path.boundingBox.minZ;
                int n10 = n8 > n9 ? n8 : n9;
                if (start.getWorldChunkManager().areBiomesViable(n6, n7, n10 / 2 + 4, MapGenVillage.villageSpawnBiomes)) {
                    list.add(path);
                    start.list2.add(path);
                    return path;
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public static class Church extends StructureVillagePieces.Village {
        public Church() {
        }

        public Church(StructureVillagePieces.Start start, int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(start, n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
        }

        public static StructureVillagePieces.Church createChurch(
            StructureVillagePieces.Start start, List list, Random random, int n, int n2, int n3, int n4, int n5
        ) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 5, 12, 9, n4);
            return canVillageGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureVillagePieces.Church(start, n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.intValue < 0) {
                this.intValue = this.getAverageGroundLevel(world, structureBoundingBox);
                if (this.intValue < 0) {
                    return true;
                }

                this.boundingBox.offset(0, this.intValue - this.boundingBox.maxY + 12 - 1, 0);
            }

            this.fillWithBlocks(world, structureBoundingBox, 1, 1, 1, 3, 3, 7, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 5, 1, 3, 9, 3, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 0, 0, 3, 0, 8, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 1, 0, 3, 10, 0, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 1, 1, 0, 10, 3, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 1, 1, 4, 10, 3, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 4, 0, 4, 7, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 0, 4, 4, 4, 7, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 1, 8, 3, 4, 8, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 5, 4, 3, 10, 4, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 5, 5, 3, 5, 7, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 9, 0, 4, 9, 4, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 4, 0, 4, 4, 4, Blocks.cobblestone, Blocks.cobblestone, false);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 0, 11, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 4, 11, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 2, 11, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 2, 11, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 1, 1, 6, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 1, 1, 7, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 2, 1, 7, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 3, 1, 6, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 3, 1, 7, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, this.getMetadataWithOffset(Blocks.stone_stairs, 3), 1, 1, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, this.getMetadataWithOffset(Blocks.stone_stairs, 3), 2, 1, 6, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, this.getMetadataWithOffset(Blocks.stone_stairs, 3), 3, 1, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, this.getMetadataWithOffset(Blocks.stone_stairs, 1), 1, 2, 7, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, this.getMetadataWithOffset(Blocks.stone_stairs, 0), 3, 2, 7, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 3, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 4, 2, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 4, 3, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 6, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 7, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 4, 6, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 4, 7, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 2, 6, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 2, 7, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 2, 6, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 2, 7, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 3, 6, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 4, 3, 6, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 2, 3, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 2, 4, 7, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 1, 4, 6, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 3, 4, 6, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 2, 4, 5, structureBoundingBox);
            int metadataWithOffset = this.getMetadataWithOffset(Blocks.ladder, 4);

            for (int i = 1; i <= 9; i++) {
                this.placeBlockAtCurrentPosition(world, Blocks.ladder, metadataWithOffset, 3, i, 3, structureBoundingBox);
            }

            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 2, 1, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 2, 2, 0, structureBoundingBox);
            this.placeDoorAtCurrentPosition(world, structureBoundingBox, random, 2, 1, 0, this.getMetadataWithOffset(Blocks.wooden_door, 1));
            if (this.getBlockAtCurrentPosition(world, 2, 0, -1, structureBoundingBox).getMaterial() == Material.air
                && this.getBlockAtCurrentPosition(world, 2, -1, -1, structureBoundingBox).getMaterial() != Material.air) {
                this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, this.getMetadataWithOffset(Blocks.stone_stairs, 3), 2, 0, -1, structureBoundingBox);
            }

            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 5; j++) {
                    this.clearCurrentPositionBlocksUpwards(world, j, 12, i, structureBoundingBox);
                    this.handleWorld(world, Blocks.cobblestone, 0, j, -1, i, structureBoundingBox);
                }
            }

            this.spawnVillagers(world, structureBoundingBox, 2, 1, 2, 1);
            return true;
        }

        @Override
        public int getVillagerType(int n) {
            return 2;
        }
    }

    public static class Field1 extends StructureVillagePieces.Village {
        public Block cropTypeA;
        public Block cropTypeB;
        public Block cropTypeC;
        public Block cropTypeD;

        public Field1() {
        }

        public Field1(StructureVillagePieces.Start start, int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(start, n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
            this.cropTypeA = this.nextInt(random);
            this.cropTypeB = this.nextInt(random);
            this.cropTypeC = this.nextInt(random);
            this.cropTypeD = this.nextInt(random);
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setInteger("CA", Block.blockRegistry.getIDForObject(this.cropTypeA));
            nbttagCompound.setInteger("CB", Block.blockRegistry.getIDForObject(this.cropTypeB));
            nbttagCompound.setInteger("CC", Block.blockRegistry.getIDForObject(this.cropTypeC));
            nbttagCompound.setInteger("CD", Block.blockRegistry.getIDForObject(this.cropTypeD));
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.cropTypeA = Block.getBlockById(nbttagCompound.getInteger("CA"));
            this.cropTypeB = Block.getBlockById(nbttagCompound.getInteger("CB"));
            this.cropTypeC = Block.getBlockById(nbttagCompound.getInteger("CC"));
            this.cropTypeD = Block.getBlockById(nbttagCompound.getInteger("CD"));
        }

        public Block nextInt(Random random) {
            switch (random.nextInt(5)) {
                case 0:
                    return Blocks.carrots;
                case 1:
                    return Blocks.potatoes;
                default:
                    return Blocks.wheat;
            }
        }

        public static StructureVillagePieces.Field1 createField1(
            StructureVillagePieces.Start start, List list, Random random, int n, int n2, int n3, int n4, int n5
        ) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 13, 4, 9, n4);
            return canVillageGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureVillagePieces.Field1(start, n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.intValue < 0) {
                this.intValue = this.getAverageGroundLevel(world, structureBoundingBox);
                if (this.intValue < 0) {
                    return true;
                }

                this.boundingBox.offset(0, this.intValue - this.boundingBox.maxY + 4 - 1, 0);
            }

            this.fillWithBlocks(world, structureBoundingBox, 0, 1, 0, 12, 4, 8, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 0, 1, 2, 0, 7, Blocks.farmland, Blocks.farmland, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 0, 1, 5, 0, 7, Blocks.farmland, Blocks.farmland, false);
            this.fillWithBlocks(world, structureBoundingBox, 7, 0, 1, 8, 0, 7, Blocks.farmland, Blocks.farmland, false);
            this.fillWithBlocks(world, structureBoundingBox, 10, 0, 1, 11, 0, 7, Blocks.farmland, Blocks.farmland, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 0, 0, 8, Blocks.log, Blocks.log, false);
            this.fillWithBlocks(world, structureBoundingBox, 6, 0, 0, 6, 0, 8, Blocks.log, Blocks.log, false);
            this.fillWithBlocks(world, structureBoundingBox, 12, 0, 0, 12, 0, 8, Blocks.log, Blocks.log, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 0, 0, 11, 0, 0, Blocks.log, Blocks.log, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 0, 8, 11, 0, 8, Blocks.log, Blocks.log, false);
            this.fillWithBlocks(world, structureBoundingBox, 3, 0, 1, 3, 0, 7, Blocks.water, Blocks.water, false);
            this.fillWithBlocks(world, structureBoundingBox, 9, 0, 1, 9, 0, 7, Blocks.water, Blocks.water, false);

            for (int i = 1; i <= 7; i++) {
                this.placeBlockAtCurrentPosition(world, this.cropTypeA, MathHelper.getRandomIntegerInRange(random, 2, 7), 1, 1, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, this.cropTypeA, MathHelper.getRandomIntegerInRange(random, 2, 7), 2, 1, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, this.cropTypeB, MathHelper.getRandomIntegerInRange(random, 2, 7), 4, 1, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, this.cropTypeB, MathHelper.getRandomIntegerInRange(random, 2, 7), 5, 1, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, this.cropTypeC, MathHelper.getRandomIntegerInRange(random, 2, 7), 7, 1, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, this.cropTypeC, MathHelper.getRandomIntegerInRange(random, 2, 7), 8, 1, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, this.cropTypeD, MathHelper.getRandomIntegerInRange(random, 2, 7), 10, 1, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, this.cropTypeD, MathHelper.getRandomIntegerInRange(random, 2, 7), 11, 1, i, structureBoundingBox);
            }

            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 13; j++) {
                    this.clearCurrentPositionBlocksUpwards(world, j, 4, i, structureBoundingBox);
                    this.handleWorld(world, Blocks.dirt, 0, j, -1, i, structureBoundingBox);
                }
            }

            return true;
        }
    }

    public static class Field2 extends StructureVillagePieces.Village {
        public Block cropTypeA;
        public Block cropTypeB;

        public Field2() {
        }

        public Field2(StructureVillagePieces.Start start, int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(start, n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
            this.cropTypeA = this.nextInt(random);
            this.cropTypeB = this.nextInt(random);
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setInteger("CA", Block.blockRegistry.getIDForObject(this.cropTypeA));
            nbttagCompound.setInteger("CB", Block.blockRegistry.getIDForObject(this.cropTypeB));
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.cropTypeA = Block.getBlockById(nbttagCompound.getInteger("CA"));
            this.cropTypeB = Block.getBlockById(nbttagCompound.getInteger("CB"));
        }

        public Block nextInt(Random random) {
            switch (random.nextInt(5)) {
                case 0:
                    return Blocks.carrots;
                case 1:
                    return Blocks.potatoes;
                default:
                    return Blocks.wheat;
            }
        }

        public static StructureVillagePieces.Field2 createField2(
            StructureVillagePieces.Start start, List list, Random random, int n, int n2, int n3, int n4, int n5
        ) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 7, 4, 9, n4);
            return canVillageGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureVillagePieces.Field2(start, n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.intValue < 0) {
                this.intValue = this.getAverageGroundLevel(world, structureBoundingBox);
                if (this.intValue < 0) {
                    return true;
                }

                this.boundingBox.offset(0, this.intValue - this.boundingBox.maxY + 4 - 1, 0);
            }

            this.fillWithBlocks(world, structureBoundingBox, 0, 1, 0, 6, 4, 8, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 0, 1, 2, 0, 7, Blocks.farmland, Blocks.farmland, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 0, 1, 5, 0, 7, Blocks.farmland, Blocks.farmland, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 0, 0, 8, Blocks.log, Blocks.log, false);
            this.fillWithBlocks(world, structureBoundingBox, 6, 0, 0, 6, 0, 8, Blocks.log, Blocks.log, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 0, 0, 5, 0, 0, Blocks.log, Blocks.log, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 0, 8, 5, 0, 8, Blocks.log, Blocks.log, false);
            this.fillWithBlocks(world, structureBoundingBox, 3, 0, 1, 3, 0, 7, Blocks.water, Blocks.water, false);

            for (int i = 1; i <= 7; i++) {
                this.placeBlockAtCurrentPosition(world, this.cropTypeA, MathHelper.getRandomIntegerInRange(random, 2, 7), 1, 1, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, this.cropTypeA, MathHelper.getRandomIntegerInRange(random, 2, 7), 2, 1, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, this.cropTypeB, MathHelper.getRandomIntegerInRange(random, 2, 7), 4, 1, i, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, this.cropTypeB, MathHelper.getRandomIntegerInRange(random, 2, 7), 5, 1, i, structureBoundingBox);
            }

            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 7; j++) {
                    this.clearCurrentPositionBlocksUpwards(world, j, 4, i, structureBoundingBox);
                    this.handleWorld(world, Blocks.dirt, 0, j, -1, i, structureBoundingBox);
                }
            }

            return true;
        }
    }

    public static class Hall extends StructureVillagePieces.Village {
        public Hall() {
        }

        public Hall(StructureVillagePieces.Start start, int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(start, n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
        }

        public static StructureVillagePieces.Hall createHall(
            StructureVillagePieces.Start start, List list, Random random, int n, int n2, int n3, int n4, int n5
        ) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 9, 7, 11, n4);
            return canVillageGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureVillagePieces.Hall(start, n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.intValue < 0) {
                this.intValue = this.getAverageGroundLevel(world, structureBoundingBox);
                if (this.intValue < 0) {
                    return true;
                }

                this.boundingBox.offset(0, this.intValue - this.boundingBox.maxY + 7 - 1, 0);
            }

            this.fillWithBlocks(world, structureBoundingBox, 1, 1, 1, 7, 4, 4, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 1, 6, 8, 4, 10, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 0, 6, 8, 0, 10, Blocks.dirt, Blocks.dirt, false);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 6, 0, 6, structureBoundingBox);
            this.fillWithBlocks(world, structureBoundingBox, 2, 1, 6, 2, 1, 10, Blocks.fence, Blocks.fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 8, 1, 6, 8, 1, 10, Blocks.fence, Blocks.fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 3, 1, 10, 7, 1, 10, Blocks.fence, Blocks.fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 0, 1, 7, 0, 4, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 0, 3, 5, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 8, 0, 0, 8, 3, 5, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 0, 0, 7, 1, 0, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 0, 5, 7, 1, 5, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 2, 0, 7, 3, 0, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 2, 5, 7, 3, 5, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 4, 1, 8, 4, 1, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 4, 4, 8, 4, 4, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 5, 2, 8, 5, 3, Blocks.planks, Blocks.planks, false);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 0, 4, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 0, 4, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 8, 4, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 8, 4, 3, structureBoundingBox);
            int metadataWithOffset = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
            int metadataWithOffset2 = this.getMetadataWithOffset(Blocks.oak_stairs, 2);

            for (int i = -1; i <= 2; i++) {
                for (int j = 0; j <= 8; j++) {
                    this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, metadataWithOffset, j, 4 + i, i, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, metadataWithOffset2, j, 4 + i, 5 - i, structureBoundingBox);
                }
            }

            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 0, 2, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 0, 2, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 8, 2, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 8, 2, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 8, 2, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 8, 2, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 2, 2, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 3, 2, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 5, 2, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 6, 2, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 2, 1, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.wooden_pressure_plate, 0, 2, 2, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 1, 1, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, this.getMetadataWithOffset(Blocks.oak_stairs, 3), 2, 1, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, this.getMetadataWithOffset(Blocks.oak_stairs, 1), 1, 1, 3, structureBoundingBox);
            this.fillWithBlocks(world, structureBoundingBox, 5, 0, 1, 7, 0, 3, Blocks.double_stone_slab, Blocks.double_stone_slab, false);
            this.placeBlockAtCurrentPosition(world, Blocks.double_stone_slab, 0, 6, 1, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.double_stone_slab, 0, 6, 1, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 2, 1, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 2, 2, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 2, 3, 1, structureBoundingBox);
            this.placeDoorAtCurrentPosition(world, structureBoundingBox, random, 2, 1, 0, this.getMetadataWithOffset(Blocks.wooden_door, 1));
            if (this.getBlockAtCurrentPosition(world, 2, 0, -1, structureBoundingBox).getMaterial() == Material.air
                && this.getBlockAtCurrentPosition(world, 2, -1, -1, structureBoundingBox).getMaterial() != Material.air) {
                this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, this.getMetadataWithOffset(Blocks.stone_stairs, 3), 2, 0, -1, structureBoundingBox);
            }

            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 6, 1, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 6, 2, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 6, 3, 4, structureBoundingBox);
            this.placeDoorAtCurrentPosition(world, structureBoundingBox, random, 6, 1, 5, this.getMetadataWithOffset(Blocks.wooden_door, 1));

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 9; j++) {
                    this.clearCurrentPositionBlocksUpwards(world, j, 7, i, structureBoundingBox);
                    this.handleWorld(world, Blocks.cobblestone, 0, j, -1, i, structureBoundingBox);
                }
            }

            this.spawnVillagers(world, structureBoundingBox, 4, 1, 2, 2);
            return true;
        }

        @Override
        public int getVillagerType(int n) {
            return n == 0 ? 4 : 0;
        }
    }

    public static class House1 extends StructureVillagePieces.Village {
        public House1() {
        }

        public House1(StructureVillagePieces.Start start, int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(start, n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
        }

        public static StructureVillagePieces.House1 createHouse1(
            StructureVillagePieces.Start start, List list, Random random, int n, int n2, int n3, int n4, int n5
        ) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 9, 9, 6, n4);
            return canVillageGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureVillagePieces.House1(start, n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.intValue < 0) {
                this.intValue = this.getAverageGroundLevel(world, structureBoundingBox);
                if (this.intValue < 0) {
                    return true;
                }

                this.boundingBox.offset(0, this.intValue - this.boundingBox.maxY + 9 - 1, 0);
            }

            this.fillWithBlocks(world, structureBoundingBox, 1, 1, 1, 7, 5, 4, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 8, 0, 5, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 5, 0, 8, 5, 5, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 6, 1, 8, 6, 4, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 7, 2, 8, 7, 3, Blocks.cobblestone, Blocks.cobblestone, false);
            int metadataWithOffset = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
            int metadataWithOffset2 = this.getMetadataWithOffset(Blocks.oak_stairs, 2);

            for (int i = -1; i <= 2; i++) {
                for (int j = 0; j <= 8; j++) {
                    this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, metadataWithOffset, j, 6 + i, i, structureBoundingBox);
                    this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, metadataWithOffset2, j, 6 + i, 5 - i, structureBoundingBox);
                }
            }

            this.fillWithBlocks(world, structureBoundingBox, 0, 1, 0, 0, 1, 5, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 1, 5, 8, 1, 5, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 8, 1, 0, 8, 1, 4, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 1, 0, 7, 1, 0, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 0, 0, 4, 0, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 5, 0, 4, 5, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 8, 2, 5, 8, 4, 5, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 8, 2, 0, 8, 4, 0, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 2, 1, 0, 4, 4, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 2, 5, 7, 4, 5, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 8, 2, 1, 8, 4, 4, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 2, 0, 7, 4, 0, Blocks.planks, Blocks.planks, false);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 4, 2, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 5, 2, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 6, 2, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 4, 3, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 5, 3, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 6, 3, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 3, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 3, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 8, 2, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 8, 2, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 8, 3, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 8, 3, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 2, 2, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 3, 2, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 5, 2, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 6, 2, 5, structureBoundingBox);
            this.fillWithBlocks(world, structureBoundingBox, 1, 4, 1, 7, 4, 1, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 4, 4, 7, 4, 4, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 3, 4, 7, 3, 4, Blocks.bookshelf, Blocks.bookshelf, false);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 7, 1, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, this.getMetadataWithOffset(Blocks.oak_stairs, 0), 7, 1, 3, structureBoundingBox);
            int metadataWithOffset3 = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
            this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, metadataWithOffset3, 6, 1, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, metadataWithOffset3, 5, 1, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, metadataWithOffset3, 4, 1, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, metadataWithOffset3, 3, 1, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 6, 1, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.wooden_pressure_plate, 0, 6, 2, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 4, 1, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.wooden_pressure_plate, 0, 4, 2, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.crafting_table, 0, 7, 1, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 1, 1, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 1, 2, 0, structureBoundingBox);
            this.placeDoorAtCurrentPosition(world, structureBoundingBox, random, 1, 1, 0, this.getMetadataWithOffset(Blocks.wooden_door, 1));
            if (this.getBlockAtCurrentPosition(world, 1, 0, -1, structureBoundingBox).getMaterial() == Material.air
                && this.getBlockAtCurrentPosition(world, 1, -1, -1, structureBoundingBox).getMaterial() != Material.air) {
                this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, this.getMetadataWithOffset(Blocks.stone_stairs, 3), 1, 0, -1, structureBoundingBox);
            }

            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 9; j++) {
                    this.clearCurrentPositionBlocksUpwards(world, j, 9, i, structureBoundingBox);
                    this.handleWorld(world, Blocks.cobblestone, 0, j, -1, i, structureBoundingBox);
                }
            }

            this.spawnVillagers(world, structureBoundingBox, 2, 1, 2, 1);
            return true;
        }

        @Override
        public int getVillagerType(int n) {
            return 1;
        }
    }

    public static class House2 extends StructureVillagePieces.Village {
        public static WeightedRandomChestContent[] villageBlacksmithChestContents = new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3),
            new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
            new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5),
            new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15),
            new WeightedRandomChestContent(Items.apple, 0, 1, 3, 15),
            new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_sword, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_chestplate, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_helmet, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_leggings, 0, 1, 1, 5),
            new WeightedRandomChestContent(Items.iron_boots, 0, 1, 1, 5),
            new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.obsidian), 0, 3, 7, 5),
            new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.sapling), 0, 3, 7, 5),
            new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3),
            new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)
        };
        public boolean hasMadeChest;

        public House2() {
        }

        public House2(StructureVillagePieces.Start start, int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(start, n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
        }

        public static StructureVillagePieces.House2 createHouse2(
            StructureVillagePieces.Start start, List list, Random random, int n, int n2, int n3, int n4, int n5
        ) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 10, 6, 7, n4);
            return canVillageGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureVillagePieces.House2(start, n5, random, componentToAddBoundingBox, n4)
                : null;
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
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.intValue < 0) {
                this.intValue = this.getAverageGroundLevel(world, structureBoundingBox);
                if (this.intValue < 0) {
                    return true;
                }

                this.boundingBox.offset(0, this.intValue - this.boundingBox.maxY + 6 - 1, 0);
            }

            this.fillWithBlocks(world, structureBoundingBox, 0, 1, 0, 9, 4, 6, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 9, 0, 6, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 4, 0, 9, 4, 6, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 5, 0, 9, 5, 6, Blocks.stone_slab, Blocks.stone_slab, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 5, 1, 8, 5, 5, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 1, 0, 2, 3, 0, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 1, 0, 0, 4, 0, Blocks.log, Blocks.log, false);
            this.fillWithBlocks(world, structureBoundingBox, 3, 1, 0, 3, 4, 0, Blocks.log, Blocks.log, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 1, 6, 0, 4, 6, Blocks.log, Blocks.log, false);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 3, 3, 1, structureBoundingBox);
            this.fillWithBlocks(world, structureBoundingBox, 3, 1, 2, 3, 3, 2, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 1, 3, 5, 3, 3, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 1, 1, 0, 3, 5, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 1, 6, 5, 3, 6, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 5, 1, 0, 5, 3, 0, Blocks.fence, Blocks.fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 9, 1, 0, 9, 3, 0, Blocks.fence, Blocks.fence, false);
            this.fillWithBlocks(world, structureBoundingBox, 6, 1, 4, 9, 4, 6, Blocks.cobblestone, Blocks.cobblestone, false);
            this.placeBlockAtCurrentPosition(world, Blocks.flowing_lava, 0, 7, 1, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.flowing_lava, 0, 8, 1, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, 9, 2, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.iron_bars, 0, 9, 2, 4, structureBoundingBox);
            this.fillWithBlocks(world, structureBoundingBox, 7, 2, 4, 8, 2, 5, Blocks.air, Blocks.air, false);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 6, 1, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.furnace, 0, 6, 2, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.furnace, 0, 6, 3, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.double_stone_slab, 0, 8, 1, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 2, 2, 6, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 4, 2, 6, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 2, 1, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.wooden_pressure_plate, 0, 2, 2, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 1, 1, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, this.getMetadataWithOffset(Blocks.oak_stairs, 3), 2, 1, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, this.getMetadataWithOffset(Blocks.oak_stairs, 1), 1, 1, 4, structureBoundingBox);
            if (!this.hasMadeChest) {
                int yWithOffset = this.getYWithOffset(1);
                int xWithOffset = this.getXWithOffset(5, 5);
                int zWithOffset = this.getZWithOffset(5, 5);
                if (structureBoundingBox.isVecInside(xWithOffset, yWithOffset, zWithOffset)) {
                    this.hasMadeChest = true;
                    this.generateStructureChestContents(world, structureBoundingBox, random, 5, 1, 5, villageBlacksmithChestContents, 3 + random.nextInt(6));
                }
            }

            for (int i = 6; i <= 8; i++) {
                if (this.getBlockAtCurrentPosition(world, i, 0, -1, structureBoundingBox).getMaterial() == Material.air
                    && this.getBlockAtCurrentPosition(world, i, -1, -1, structureBoundingBox).getMaterial() != Material.air) {
                    this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, this.getMetadataWithOffset(Blocks.stone_stairs, 3), i, 0, -1, structureBoundingBox);
                }
            }

            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 10; j++) {
                    this.clearCurrentPositionBlocksUpwards(world, j, 6, i, structureBoundingBox);
                    this.handleWorld(world, Blocks.cobblestone, 0, j, -1, i, structureBoundingBox);
                }
            }

            this.spawnVillagers(world, structureBoundingBox, 7, 1, 1, 1);
            return true;
        }

        @Override
        public int getVillagerType(int n) {
            return 3;
        }
    }

    public static class House3 extends StructureVillagePieces.Village {
        public House3() {
        }

        public House3(StructureVillagePieces.Start start, int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(start, n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
        }

        public static StructureVillagePieces.House3 createHouse3(
            StructureVillagePieces.Start start, List list, Random random, int n, int n2, int n3, int n4, int n5
        ) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 9, 7, 12, n4);
            return canVillageGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureVillagePieces.House3(start, n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.intValue < 0) {
                this.intValue = this.getAverageGroundLevel(world, structureBoundingBox);
                if (this.intValue < 0) {
                    return true;
                }

                this.boundingBox.offset(0, this.intValue - this.boundingBox.maxY + 7 - 1, 0);
            }

            this.fillWithBlocks(world, structureBoundingBox, 1, 1, 1, 7, 4, 4, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 1, 6, 8, 4, 10, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 0, 5, 8, 0, 10, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 0, 1, 7, 0, 4, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 0, 3, 5, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 8, 0, 0, 8, 3, 10, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 0, 0, 7, 2, 0, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 0, 5, 2, 1, 5, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 2, 0, 6, 2, 3, 10, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 3, 0, 10, 7, 3, 10, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 2, 0, 7, 3, 0, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 2, 5, 2, 3, 5, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 4, 1, 8, 4, 1, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 4, 4, 3, 4, 4, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 5, 2, 8, 5, 3, Blocks.planks, Blocks.planks, false);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 0, 4, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 0, 4, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 8, 4, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 8, 4, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 8, 4, 4, structureBoundingBox);
            int metadataWithOffset = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
            int metadataWithOffset2 = this.getMetadataWithOffset(Blocks.oak_stairs, 2);

            for (int i = -1; i <= 2; i++) {
                for (int j = 0; j <= 8; j++) {
                    this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, metadataWithOffset, j, 4 + i, i, structureBoundingBox);
                    if ((i > -1 || j <= 1) && (i > 0 || j <= 3) && (i > 1 || j <= 4 || j >= 6)) {
                        this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, metadataWithOffset2, j, 4 + i, 5 - i, structureBoundingBox);
                    }
                }
            }

            this.fillWithBlocks(world, structureBoundingBox, 3, 4, 5, 3, 4, 10, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 7, 4, 2, 7, 4, 10, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 5, 4, 4, 5, 10, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 6, 5, 4, 6, 5, 10, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 5, 6, 3, 5, 6, 10, Blocks.planks, Blocks.planks, false);
            int metadataWithOffset3 = this.getMetadataWithOffset(Blocks.oak_stairs, 0);

            for (int i = 4; i >= 1; i--) {
                this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, i, 2 + i, 7 - i, structureBoundingBox);

                for (int j = 8 - i; j <= 10; j++) {
                    this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, metadataWithOffset3, i, 2 + i, j, structureBoundingBox);
                }
            }

            int metadataWithOffset4 = this.getMetadataWithOffset(Blocks.oak_stairs, 1);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 6, 6, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 7, 5, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, metadataWithOffset4, 6, 6, 4, structureBoundingBox);

            for (int i = 6; i <= 8; i++) {
                for (int j = 5; j <= 10; j++) {
                    this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, metadataWithOffset4, i, 12 - i, j, structureBoundingBox);
                }
            }

            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 0, 2, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 0, 2, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 4, 2, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 5, 2, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 6, 2, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 8, 2, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 8, 2, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 8, 2, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 8, 2, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 8, 2, 5, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 8, 2, 6, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 8, 2, 7, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 8, 2, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 8, 2, 9, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 2, 2, 6, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 2, 2, 7, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 2, 2, 8, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 2, 2, 9, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 4, 4, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 5, 4, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 6, 4, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 5, 5, 10, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 2, 1, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 2, 2, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 2, 3, 1, structureBoundingBox);
            this.placeDoorAtCurrentPosition(world, structureBoundingBox, random, 2, 1, 0, this.getMetadataWithOffset(Blocks.wooden_door, 1));
            this.fillWithBlocks(world, structureBoundingBox, 1, 0, -1, 3, 2, -1, Blocks.air, Blocks.air, false);
            if (this.getBlockAtCurrentPosition(world, 2, 0, -1, structureBoundingBox).getMaterial() == Material.air
                && this.getBlockAtCurrentPosition(world, 2, -1, -1, structureBoundingBox).getMaterial() != Material.air) {
                this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, this.getMetadataWithOffset(Blocks.stone_stairs, 3), 2, 0, -1, structureBoundingBox);
            }

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 9; j++) {
                    this.clearCurrentPositionBlocksUpwards(world, j, 7, i, structureBoundingBox);
                    this.handleWorld(world, Blocks.cobblestone, 0, j, -1, i, structureBoundingBox);
                }
            }

            for (int i = 5; i < 11; i++) {
                for (int j = 2; j < 9; j++) {
                    this.clearCurrentPositionBlocksUpwards(world, j, 7, i, structureBoundingBox);
                    this.handleWorld(world, Blocks.cobblestone, 0, j, -1, i, structureBoundingBox);
                }
            }

            this.spawnVillagers(world, structureBoundingBox, 4, 1, 2, 2);
            return true;
        }
    }

    public static class House4Garden extends StructureVillagePieces.Village {
        public boolean isRoofAccessible;

        public House4Garden() {
        }

        public House4Garden(StructureVillagePieces.Start start, int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(start, n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
            this.isRoofAccessible = random.nextBoolean();
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setBoolean("Terrace", this.isRoofAccessible);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.isRoofAccessible = nbttagCompound.getBoolean("Terrace");
        }

        public static StructureVillagePieces.House4Garden createHouse4Garden(
            StructureVillagePieces.Start start, List list, Random random, int n, int n2, int n3, int n4, int n5
        ) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 5, 6, 5, n4);
            return StructureComponent.findIntersecting(list, componentToAddBoundingBox) != null ? null : new StructureVillagePieces.House4Garden(start, n5, random, componentToAddBoundingBox, n4);
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.intValue < 0) {
                this.intValue = this.getAverageGroundLevel(world, structureBoundingBox);
                if (this.intValue < 0) {
                    return true;
                }

                this.boundingBox.offset(0, this.intValue - this.boundingBox.maxY + 6 - 1, 0);
            }

            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 4, 0, 4, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 4, 0, 4, 4, 4, Blocks.log, Blocks.log, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 4, 1, 3, 4, 3, Blocks.planks, Blocks.planks, false);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 0, 1, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 0, 2, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 0, 3, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 4, 1, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 4, 2, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 4, 3, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 0, 1, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 0, 2, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 0, 3, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 4, 1, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 4, 2, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.cobblestone, 0, 4, 3, 4, structureBoundingBox);
            this.fillWithBlocks(world, structureBoundingBox, 0, 1, 1, 0, 3, 3, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 4, 1, 1, 4, 3, 3, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 1, 4, 3, 3, 4, Blocks.planks, Blocks.planks, false);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 2, 2, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 4, 2, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 1, 1, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 1, 2, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 1, 3, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 2, 3, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 3, 3, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 3, 2, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 3, 1, 0, structureBoundingBox);
            if (this.getBlockAtCurrentPosition(world, 2, 0, -1, structureBoundingBox).getMaterial() == Material.air
                && this.getBlockAtCurrentPosition(world, 2, -1, -1, structureBoundingBox).getMaterial() != Material.air) {
                this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, this.getMetadataWithOffset(Blocks.stone_stairs, 3), 2, 0, -1, structureBoundingBox);
            }

            this.fillWithBlocks(world, structureBoundingBox, 1, 1, 1, 3, 3, 3, Blocks.air, Blocks.air, false);
            if (this.isRoofAccessible) {
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 0, 5, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 1, 5, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 2, 5, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 3, 5, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 4, 5, 0, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 0, 5, 4, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 1, 5, 4, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 2, 5, 4, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 3, 5, 4, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 4, 5, 4, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 4, 5, 1, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 4, 5, 2, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 4, 5, 3, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 0, 5, 1, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 0, 5, 2, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 0, 5, 3, structureBoundingBox);
            }

            if (this.isRoofAccessible) {
                int metadataWithOffset = this.getMetadataWithOffset(Blocks.ladder, 3);
                this.placeBlockAtCurrentPosition(world, Blocks.ladder, metadataWithOffset, 3, 1, 3, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.ladder, metadataWithOffset, 3, 2, 3, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.ladder, metadataWithOffset, 3, 3, 3, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.ladder, metadataWithOffset, 3, 4, 3, structureBoundingBox);
            }

            this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 2, 3, 1, structureBoundingBox);

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    this.clearCurrentPositionBlocksUpwards(world, j, 6, i, structureBoundingBox);
                    this.handleWorld(world, Blocks.cobblestone, 0, j, -1, i, structureBoundingBox);
                }
            }

            this.spawnVillagers(world, structureBoundingBox, 1, 1, 2, 1);
            return true;
        }
    }

    public static class Path extends StructureVillagePieces.Road {
        public int averageGroundLevel;

        public Path() {
        }

        public Path(StructureVillagePieces.Start start, int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(start, n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
            this.averageGroundLevel = Math.max(structureBoundingBox.getXSize(), structureBoundingBox.getZSize());
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setInteger("Length", this.averageGroundLevel);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.averageGroundLevel = nbttagCompound.getInteger("Length");
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            boolean flag = false;

            for (int nextInt = random.nextInt(5); nextInt < this.averageGroundLevel - 8; nextInt += 2 + random.nextInt(5)) {
                StructureComponent nextComponentNN = this.getNextComponentNN((StructureVillagePieces.Start)structureComponent, list, random, 0, nextInt);
                if (nextComponentNN != null) {
                    nextInt += Math.max(nextComponentNN.boundingBox.getXSize(), nextComponentNN.boundingBox.getZSize());
                    flag = true;
                }
            }

            for (int nextInt = random.nextInt(5); nextInt < this.averageGroundLevel - 8; nextInt += 2 + random.nextInt(5)) {
                StructureComponent nextComponentPP = this.getNextComponentPP((StructureVillagePieces.Start)structureComponent, list, random, 0, nextInt);
                if (nextComponentPP != null) {
                    nextInt += Math.max(nextComponentPP.boundingBox.getXSize(), nextComponentPP.boundingBox.getZSize());
                    flag = true;
                }
            }

            if (flag && random.nextInt(3) > 0) {
                switch (this.coordBaseMode) {
                    case 0:
                        StructureVillagePieces.getNextComponentVillagePath(
                            (StructureVillagePieces.Start)structureComponent,
                            list,
                            random,
                            this.boundingBox.minX - 1,
                            this.boundingBox.minY,
                            this.boundingBox.maxZ - 2,
                            1,
                            this.getComponentType()
                        );
                        break;
                    case 1:
                        StructureVillagePieces.getNextComponentVillagePath(
                            (StructureVillagePieces.Start)structureComponent,
                            list,
                            random,
                            this.boundingBox.minX,
                            this.boundingBox.minY,
                            this.boundingBox.minZ - 1,
                            2,
                            this.getComponentType()
                        );
                        break;
                    case 2:
                        StructureVillagePieces.getNextComponentVillagePath(
                            (StructureVillagePieces.Start)structureComponent,
                            list,
                            random,
                            this.boundingBox.minX - 1,
                            this.boundingBox.minY,
                            this.boundingBox.minZ,
                            1,
                            this.getComponentType()
                        );
                        break;
                    case 3:
                        StructureVillagePieces.getNextComponentVillagePath(
                            (StructureVillagePieces.Start)structureComponent,
                            list,
                            random,
                            this.boundingBox.maxX - 2,
                            this.boundingBox.minY,
                            this.boundingBox.minZ - 1,
                            2,
                            this.getComponentType()
                        );
                }
            }

            if (flag && random.nextInt(3) > 0) {
                switch (this.coordBaseMode) {
                    case 0:
                        StructureVillagePieces.getNextComponentVillagePath(
                            (StructureVillagePieces.Start)structureComponent,
                            list,
                            random,
                            this.boundingBox.maxX + 1,
                            this.boundingBox.minY,
                            this.boundingBox.maxZ - 2,
                            3,
                            this.getComponentType()
                        );
                        break;
                    case 1:
                        StructureVillagePieces.getNextComponentVillagePath(
                            (StructureVillagePieces.Start)structureComponent,
                            list,
                            random,
                            this.boundingBox.minX,
                            this.boundingBox.minY,
                            this.boundingBox.maxZ + 1,
                            0,
                            this.getComponentType()
                        );
                        break;
                    case 2:
                        StructureVillagePieces.getNextComponentVillagePath(
                            (StructureVillagePieces.Start)structureComponent,
                            list,
                            random,
                            this.boundingBox.maxX + 1,
                            this.boundingBox.minY,
                            this.boundingBox.minZ,
                            3,
                            this.getComponentType()
                        );
                        break;
                    case 3:
                        StructureVillagePieces.getNextComponentVillagePath(
                            (StructureVillagePieces.Start)structureComponent,
                            list,
                            random,
                            this.boundingBox.maxX - 2,
                            this.boundingBox.minY,
                            this.boundingBox.maxZ + 1,
                            0,
                            this.getComponentType()
                        );
                }
            }
        }

        public static StructureBoundingBox findStructureBoundingBox(
            StructureVillagePieces.Start start, List list, Random random, int n, int n2, int n3, int n4
        ) {
            for (int i = 7 * MathHelper.getRandomIntegerInRange(random, 3, 5); i >= 7; i -= 7) {
                StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 3, 3, i, n4);
                if (StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null) {
                    return componentToAddBoundingBox;
                }
            }

            return null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            Block findBlock = this.findBlock(Blocks.gravel, 0);

            for (int minX = this.boundingBox.minX; minX <= this.boundingBox.maxX; minX++) {
                for (int minZ = this.boundingBox.minZ; minZ <= this.boundingBox.maxZ; minZ++) {
                    if (structureBoundingBox.isVecInside(minX, 64, minZ)) {
                        int n = world.getTopSolidOrLiquidBlock(minX, minZ) - 1;
                        world.setBlock(minX, n, minZ, findBlock, 0, 2);
                    }
                }
            }

            return true;
        }
    }

    public static class PieceWeight {
        public Class villagePieceClass;
        public int villagePieceWeight;
        public int villagePiecesSpawned;
        public int villagePiecesLimit;

        public PieceWeight(Class clazz, int n, int n2) {
            this.villagePieceClass = clazz;
            this.villagePieceWeight = n;
            this.villagePiecesLimit = n2;
        }

        public boolean canSpawnMoreVillagePiecesOfType(int n) {
            return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
        }

        public boolean canSpawnMoreVillagePieces() {
            return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
        }
    }

    public abstract static class Road extends StructureVillagePieces.Village {
        public Road() {
        }

        public Road(StructureVillagePieces.Start start, int n) {
            super(start, n);
        }
    }

    public static class Start extends StructureVillagePieces.Well {
        public List list = new ArrayList();
        public List list2 = new ArrayList();
        public WorldChunkManager worldChunkMngr;
        public StructureVillagePieces.PieceWeight structVillagePieceWeight;
        public List structureVillageWeightedPieceList;
        public int terrainType;
        public boolean inDesert;

        public Start() {
        }

        public Start(WorldChunkManager worldChunkManager, int n, Random random, int n2, int n3, List list, int n4) {
            super(null, 0, random, n2, n3);
            this.worldChunkMngr = worldChunkManager;
            this.structureVillageWeightedPieceList = list;
            this.terrainType = n4;
            BiomeGenBase biomeGenAt = worldChunkManager.getBiomeGenAt(n2, n3);
            this.inDesert = biomeGenAt == BiomeGenBase.desert || biomeGenAt == BiomeGenBase.desertHills;
        }

        public WorldChunkManager getWorldChunkManager() {
            return this.worldChunkMngr;
        }
    }

    public static class Torch extends StructureVillagePieces.Village {
        public Torch() {
        }

        public Torch(StructureVillagePieces.Start start, int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(start, n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
        }

        public static StructureBoundingBox findStructureBoundingBox(
            StructureVillagePieces.Start start, List list, Random random, int n, int n2, int n3, int n4
        ) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 3, 4, 2, n4);
            return StructureComponent.findIntersecting(list, componentToAddBoundingBox) != null ? null : componentToAddBoundingBox;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.intValue < 0) {
                this.intValue = this.getAverageGroundLevel(world, structureBoundingBox);
                if (this.intValue < 0) {
                    return true;
                }

                this.boundingBox.offset(0, this.intValue - this.boundingBox.maxY + 4 - 1, 0);
            }

            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 2, 3, 1, Blocks.air, Blocks.air, false);
            this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 1, 0, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 1, 1, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 1, 2, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.wool, 15, 1, 3, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 0, 3, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 1, 3, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 2, 3, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.torch, 0, 1, 3, -1, structureBoundingBox);
            return true;
        }
    }

    public abstract static class Village extends StructureComponent {
        public int intValue = -1;
        public int villagersSpawned;
        public boolean flag;

        public Village() {
        }

        public Village(StructureVillagePieces.Start start, int n) {
            super(n);
            if (start != null) {
                this.flag = start.inDesert;
            }
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            nbttagCompound.setInteger("HPos", this.intValue);
            nbttagCompound.setInteger("VCount", this.villagersSpawned);
            nbttagCompound.setBoolean("Desert", this.flag);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            this.intValue = nbttagCompound.getInteger("HPos");
            this.villagersSpawned = nbttagCompound.getInteger("VCount");
            this.flag = nbttagCompound.getBoolean("Desert");
        }

        public StructureComponent getNextComponentNN(StructureVillagePieces.Start start, List list, Random random, int n, int n2) {
            switch (this.coordBaseMode) {
                case 0:
                    return StructureVillagePieces.getNextVillageStructureComponent(
                        start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + n, this.boundingBox.minZ + n2, 1, this.getComponentType()
                    );
                case 1:
                    return StructureVillagePieces.getNextVillageStructureComponent(
                        start, list, random, this.boundingBox.minX + n2, this.boundingBox.minY + n, this.boundingBox.minZ - 1, 2, this.getComponentType()
                    );
                case 2:
                    return StructureVillagePieces.getNextVillageStructureComponent(
                        start, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + n, this.boundingBox.minZ + n2, 1, this.getComponentType()
                    );
                case 3:
                    return StructureVillagePieces.getNextVillageStructureComponent(
                        start, list, random, this.boundingBox.minX + n2, this.boundingBox.minY + n, this.boundingBox.minZ - 1, 2, this.getComponentType()
                    );
                default:
                    return null;
            }
        }

        public StructureComponent getNextComponentPP(StructureVillagePieces.Start start, List list, Random random, int n, int n2) {
            switch (this.coordBaseMode) {
                case 0:
                    return StructureVillagePieces.getNextVillageStructureComponent(
                        start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + n, this.boundingBox.minZ + n2, 3, this.getComponentType()
                    );
                case 1:
                    return StructureVillagePieces.getNextVillageStructureComponent(
                        start, list, random, this.boundingBox.minX + n2, this.boundingBox.minY + n, this.boundingBox.maxZ + 1, 0, this.getComponentType()
                    );
                case 2:
                    return StructureVillagePieces.getNextVillageStructureComponent(
                        start, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + n, this.boundingBox.minZ + n2, 3, this.getComponentType()
                    );
                case 3:
                    return StructureVillagePieces.getNextVillageStructureComponent(
                        start, list, random, this.boundingBox.minX + n2, this.boundingBox.minY + n, this.boundingBox.maxZ + 1, 0, this.getComponentType()
                    );
                default:
                    return null;
            }
        }

        public int getAverageGroundLevel(World world, StructureBoundingBox structureBoundingBox) {
            int n = 0;
            int n2 = 0;

            for (int minZ = this.boundingBox.minZ; minZ <= this.boundingBox.maxZ; minZ++) {
                for (int minX = this.boundingBox.minX; minX <= this.boundingBox.maxX; minX++) {
                    if (structureBoundingBox.isVecInside(minX, 64, minZ)) {
                        n += Math.max(world.getTopSolidOrLiquidBlock(minX, minZ), world.provider.getAverageGroundLevel());
                        n2++;
                    }
                }
            }

            return n2 == 0 ? -1 : n / n2;
        }

        public static boolean canVillageGoDeeper(StructureBoundingBox structureBoundingBox) {
            return structureBoundingBox != null && structureBoundingBox.minY > 10;
        }

        public void spawnVillagers(World world, StructureBoundingBox structureBoundingBox, int n, int n2, int n3, int n4) {
            if (this.villagersSpawned < n4) {
                for (int villagersSpawned = this.villagersSpawned; villagersSpawned < n4; villagersSpawned++) {
                    int xWithOffset = this.getXWithOffset(n + villagersSpawned, n3);
                    int yWithOffset = this.getYWithOffset(n2);
                    int zWithOffset = this.getZWithOffset(n + villagersSpawned, n3);
                    if (!structureBoundingBox.isVecInside(xWithOffset, yWithOffset, zWithOffset)) {
                        break;
                    }

                    this.villagersSpawned++;
                    EntityVillager entityVillager = new EntityVillager(world, this.getVillagerType(villagersSpawned));
                    entityVillager.setLocationAndAngles(xWithOffset + 0.5, yWithOffset, zWithOffset + 0.5, 0.0F, 0.0F);
                    world.spawnEntityInWorld(entityVillager);
                }
            }
        }

        public int getVillagerType(int n) {
            return 0;
        }

        public Block findBlock(Block block, int n) {
            if (this.flag) {
                if (block == Blocks.log || block == Blocks.log2) {
                    return Blocks.sandstone;
                }

                if (block == Blocks.cobblestone) {
                    return Blocks.sandstone;
                }

                if (block == Blocks.planks) {
                    return Blocks.sandstone;
                }

                if (block == Blocks.oak_stairs) {
                    return Blocks.sandstone_stairs;
                }

                if (block == Blocks.stone_stairs) {
                    return Blocks.sandstone_stairs;
                }

                if (block == Blocks.gravel) {
                    return Blocks.sandstone;
                }
            }

            return block;
        }

        public int computeInt(Block block, int n) {
            if (this.flag) {
                if (block == Blocks.log || block == Blocks.log2) {
                    return 0;
                }

                if (block == Blocks.cobblestone) {
                    return 0;
                }

                if (block == Blocks.planks) {
                    return 2;
                }
            }

            return n;
        }

        @Override
        public void placeBlockAtCurrentPosition(World world, Block block, int n, int n2, int n3, int n4, StructureBoundingBox structureBoundingBox) {
            Block findBlock = this.findBlock(block, n);
            int computeInt = this.computeInt(block, n);
            super.placeBlockAtCurrentPosition(world, findBlock, computeInt, n2, n3, n4, structureBoundingBox);
        }

        @Override
        public void fillWithBlocks(
            World world, StructureBoundingBox structureBoundingBox, int n, int n2, int n3, int n4, int n5, int n6, Block block, Block block2, boolean flag
        ) {
            Block findBlock = this.findBlock(block, 0);
            int computeInt = this.computeInt(block, 0);
            Block findBlock2 = this.findBlock(block2, 0);
            int computeInt2 = this.computeInt(block2, 0);
            super.fillWithMetadataBlocks(world, structureBoundingBox, n, n2, n3, n4, n5, n6, findBlock, computeInt, findBlock2, computeInt2, flag);
        }

        @Override
        public void handleWorld(World world, Block block, int n, int n2, int n3, int n4, StructureBoundingBox structureBoundingBox) {
            Block findBlock = this.findBlock(block, n);
            int computeInt = this.computeInt(block, n);
            super.handleWorld(world, findBlock, computeInt, n2, n3, n4, structureBoundingBox);
        }
    }

    public static class Well extends StructureVillagePieces.Village {
        public Well() {
        }

        public Well(StructureVillagePieces.Start start, int n, Random random, int n2, int n3) {
            super(start, n);
            this.coordBaseMode = random.nextInt(4);
            switch (this.coordBaseMode) {
                case 0:
                case 2:
                    this.boundingBox = new StructureBoundingBox(n2, 64, n3, n2 + 6 - 1, 78, n3 + 6 - 1);
                    break;
                default:
                    this.boundingBox = new StructureBoundingBox(n2, 64, n3, n2 + 6 - 1, 78, n3 + 6 - 1);
            }
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            StructureVillagePieces.getNextComponentVillagePath(
                (StructureVillagePieces.Start)structureComponent,
                list,
                random,
                this.boundingBox.minX - 1,
                this.boundingBox.maxY - 4,
                this.boundingBox.minZ + 1,
                1,
                this.getComponentType()
            );
            StructureVillagePieces.getNextComponentVillagePath(
                (StructureVillagePieces.Start)structureComponent,
                list,
                random,
                this.boundingBox.maxX + 1,
                this.boundingBox.maxY - 4,
                this.boundingBox.minZ + 1,
                3,
                this.getComponentType()
            );
            StructureVillagePieces.getNextComponentVillagePath(
                (StructureVillagePieces.Start)structureComponent,
                list,
                random,
                this.boundingBox.minX + 1,
                this.boundingBox.maxY - 4,
                this.boundingBox.minZ - 1,
                2,
                this.getComponentType()
            );
            StructureVillagePieces.getNextComponentVillagePath(
                (StructureVillagePieces.Start)structureComponent,
                list,
                random,
                this.boundingBox.minX + 1,
                this.boundingBox.maxY - 4,
                this.boundingBox.maxZ + 1,
                0,
                this.getComponentType()
            );
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.intValue < 0) {
                this.intValue = this.getAverageGroundLevel(world, structureBoundingBox);
                if (this.intValue < 0) {
                    return true;
                }

                this.boundingBox.offset(0, this.intValue - this.boundingBox.maxY + 3, 0);
            }

            this.fillWithBlocks(world, structureBoundingBox, 1, 0, 1, 4, 12, 4, Blocks.cobblestone, Blocks.flowing_water, false);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 2, 12, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 3, 12, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 2, 12, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 3, 12, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 1, 13, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 1, 14, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 4, 13, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 4, 14, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 1, 13, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 1, 14, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 4, 13, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, 4, 14, 4, structureBoundingBox);
            this.fillWithBlocks(world, structureBoundingBox, 1, 15, 1, 4, 15, 4, Blocks.cobblestone, Blocks.cobblestone, false);

            for (int i = 0; i <= 5; i++) {
                for (int j = 0; j <= 5; j++) {
                    if (j == 0 || j == 5 || i == 0 || i == 5) {
                        this.placeBlockAtCurrentPosition(world, Blocks.gravel, 0, j, 11, i, structureBoundingBox);
                        this.clearCurrentPositionBlocksUpwards(world, j, 12, i, structureBoundingBox);
                    }
                }
            }

            return true;
        }
    }

    public static class WoodHut extends StructureVillagePieces.Village {
        public boolean isTallHouse;
        public int tablePosition;

        public WoodHut() {
        }

        public WoodHut(StructureVillagePieces.Start start, int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(start, n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
            this.isTallHouse = random.nextBoolean();
            this.tablePosition = random.nextInt(3);
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            super.writeStructureToNBT(nbttagCompound);
            nbttagCompound.setInteger("T", this.tablePosition);
            nbttagCompound.setBoolean("C", this.isTallHouse);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            super.readStructureFromNBT(nbttagCompound);
            this.tablePosition = nbttagCompound.getInteger("T");
            this.isTallHouse = nbttagCompound.getBoolean("C");
        }

        public static StructureVillagePieces.WoodHut createWoodHut(
            StructureVillagePieces.Start start, List list, Random random, int n, int n2, int n3, int n4, int n5
        ) {
            StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, 0, 0, 0, 4, 6, 5, n4);
            return canVillageGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
                ? new StructureVillagePieces.WoodHut(start, n5, random, componentToAddBoundingBox, n4)
                : null;
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.intValue < 0) {
                this.intValue = this.getAverageGroundLevel(world, structureBoundingBox);
                if (this.intValue < 0) {
                    return true;
                }

                this.boundingBox.offset(0, this.intValue - this.boundingBox.maxY + 6 - 1, 0);
            }

            this.fillWithBlocks(world, structureBoundingBox, 1, 1, 1, 3, 5, 4, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 3, 0, 4, Blocks.cobblestone, Blocks.cobblestone, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 0, 1, 2, 0, 3, Blocks.dirt, Blocks.dirt, false);
            if (this.isTallHouse) {
                this.fillWithBlocks(world, structureBoundingBox, 1, 4, 1, 2, 4, 3, Blocks.log, Blocks.log, false);
            } else {
                this.fillWithBlocks(world, structureBoundingBox, 1, 5, 1, 2, 5, 3, Blocks.log, Blocks.log, false);
            }

            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 1, 4, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 2, 4, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 1, 4, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 2, 4, 4, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 0, 4, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 0, 4, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 0, 4, 3, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 3, 4, 1, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 3, 4, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.log, 0, 3, 4, 3, structureBoundingBox);
            this.fillWithBlocks(world, structureBoundingBox, 0, 1, 0, 0, 3, 0, Blocks.log, Blocks.log, false);
            this.fillWithBlocks(world, structureBoundingBox, 3, 1, 0, 3, 3, 0, Blocks.log, Blocks.log, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 1, 4, 0, 3, 4, Blocks.log, Blocks.log, false);
            this.fillWithBlocks(world, structureBoundingBox, 3, 1, 4, 3, 3, 4, Blocks.log, Blocks.log, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 1, 1, 0, 3, 3, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 3, 1, 1, 3, 3, 3, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 1, 0, 2, 3, 0, Blocks.planks, Blocks.planks, false);
            this.fillWithBlocks(world, structureBoundingBox, 1, 1, 4, 2, 3, 4, Blocks.planks, Blocks.planks, false);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 2, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 3, 2, 2, structureBoundingBox);
            if (this.tablePosition > 0) {
                this.placeBlockAtCurrentPosition(world, Blocks.fence, 0, this.tablePosition, 1, 3, structureBoundingBox);
                this.placeBlockAtCurrentPosition(world, Blocks.wooden_pressure_plate, 0, this.tablePosition, 2, 3, structureBoundingBox);
            }

            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 1, 1, 0, structureBoundingBox);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 1, 2, 0, structureBoundingBox);
            this.placeDoorAtCurrentPosition(world, structureBoundingBox, random, 1, 1, 0, this.getMetadataWithOffset(Blocks.wooden_door, 1));
            if (this.getBlockAtCurrentPosition(world, 1, 0, -1, structureBoundingBox).getMaterial() == Material.air
                && this.getBlockAtCurrentPosition(world, 1, -1, -1, structureBoundingBox).getMaterial() != Material.air) {
                this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, this.getMetadataWithOffset(Blocks.stone_stairs, 3), 1, 0, -1, structureBoundingBox);
            }

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 4; j++) {
                    this.clearCurrentPositionBlocksUpwards(world, j, 6, i, structureBoundingBox);
                    this.handleWorld(world, Blocks.cobblestone, 0, j, -1, i, structureBoundingBox);
                }
            }

            this.spawnVillagers(world, structureBoundingBox, 1, 1, 2, 1);
            return true;
        }
    }
}
