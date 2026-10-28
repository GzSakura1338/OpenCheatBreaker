package net.minecraft.world.gen.structure;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureMineshaftPieces {
    public static WeightedRandomChestContent[] mineshaftChestContents = new WeightedRandomChestContent[]{
        new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
        new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5),
        new WeightedRandomChestContent(Items.redstone, 0, 4, 9, 5),
        new WeightedRandomChestContent(Items.dye, 4, 4, 9, 5),
        new WeightedRandomChestContent(Items.diamond, 0, 1, 2, 3),
        new WeightedRandomChestContent(Items.coal, 0, 3, 8, 10),
        new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15),
        new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 1),
        new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.rail), 0, 4, 8, 1),
        new WeightedRandomChestContent(Items.melon_seeds, 0, 2, 4, 10),
        new WeightedRandomChestContent(Items.pumpkin_seeds, 0, 2, 4, 10),
        new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3),
        new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1)
    };

    public static void registerStructurePieces() {
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Corridor.class, "MSCorridor");
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Cross.class, "MSCrossing");
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Room.class, "MSRoom");
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Stairs.class, "MSStairs");
    }

    public static StructureComponent getRandomComponent(List list, Random random, int n, int n2, int n3, int n4, int n5) {
        int nextInt = random.nextInt(100);
        if (nextInt >= 80) {
            StructureBoundingBox findValidPlacement = StructureMineshaftPieces.Cross.findValidPlacement(list, random, n, n2, n3, n4);
            if (findValidPlacement != null) {
                return new StructureMineshaftPieces.Cross(n5, random, findValidPlacement, n4);
            }
        } else if (nextInt >= 70) {
            StructureBoundingBox findValidPlacement = StructureMineshaftPieces.Stairs.findValidPlacement(list, random, n, n2, n3, n4);
            if (findValidPlacement != null) {
                return new StructureMineshaftPieces.Stairs(n5, random, findValidPlacement, n4);
            }
        } else {
            StructureBoundingBox findValidPlacement = StructureMineshaftPieces.Corridor.findValidPlacement(list, random, n, n2, n3, n4);
            if (findValidPlacement != null) {
                return new StructureMineshaftPieces.Corridor(n5, random, findValidPlacement, n4);
            }
        }

        return null;
    }

    public static StructureComponent getNextMineShaftComponent(StructureComponent structureComponent, List list, Random random, int n, int n2, int n3, int n4, int n5) {
        if (n5 > 8) {
            return null;
        }

        if (Math.abs(n - structureComponent.getBoundingBox().minX) <= 80 && Math.abs(n3 - structureComponent.getBoundingBox().minZ) <= 80) {
            StructureComponent randomComponent = getRandomComponent(list, random, n, n2, n3, n4, n5 + 1);
            if (randomComponent != null) {
                list.add(randomComponent);
                randomComponent.buildComponent(structureComponent, list, random);
            }

            return randomComponent;
        } else {
            return null;
        }
    }

    public static class Corridor extends StructureComponent {
        public boolean hasRails;
        public boolean hasSpiders;
        public boolean spawnerPlaced;
        public int sectionCount;

        public Corridor() {
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            nbttagCompound.setBoolean("hr", this.hasRails);
            nbttagCompound.setBoolean("sc", this.hasSpiders);
            nbttagCompound.setBoolean("hps", this.spawnerPlaced);
            nbttagCompound.setInteger("Num", this.sectionCount);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            this.hasRails = nbttagCompound.getBoolean("hr");
            this.hasSpiders = nbttagCompound.getBoolean("sc");
            this.spawnerPlaced = nbttagCompound.getBoolean("hps");
            this.sectionCount = nbttagCompound.getInteger("Num");
        }

        public Corridor(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
            this.hasRails = random.nextInt(3) == 0;
            this.hasSpiders = !this.hasRails && random.nextInt(23) == 0;
            if (this.coordBaseMode != 2 && this.coordBaseMode != 0) {
                this.sectionCount = structureBoundingBox.getXSize() / 5;
            } else {
                this.sectionCount = structureBoundingBox.getZSize() / 5;
            }
        }

        public static StructureBoundingBox findValidPlacement(List list, Random random, int n, int n2, int n3, int n4) {
            StructureBoundingBox structureBoundingBox = new StructureBoundingBox(n, n2, n3, n, n2 + 2, n3);

            int n5;
            for (n5 = random.nextInt(3) + 2; n5 > 0; n5--) {
                int n6 = n5 * 5;
                switch (n4) {
                    case 0:
                        structureBoundingBox.maxX = n + 2;
                        structureBoundingBox.maxZ = n3 + (n6 - 1);
                        break;
                    case 1:
                        structureBoundingBox.minX = n - (n6 - 1);
                        structureBoundingBox.maxZ = n3 + 2;
                        break;
                    case 2:
                        structureBoundingBox.maxX = n + 2;
                        structureBoundingBox.minZ = n3 - (n6 - 1);
                        break;
                    case 3:
                        structureBoundingBox.maxX = n + (n6 - 1);
                        structureBoundingBox.maxZ = n3 + 2;
                }

                if (StructureComponent.findIntersecting(list, structureBoundingBox) == null) {
                    break;
                }
            }

            return n5 > 0 ? structureBoundingBox : null;
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            int componentType = this.getComponentType();
            int nextInt = random.nextInt(4);
            switch (this.coordBaseMode) {
                case 0:
                    if (nextInt <= 1) {
                        StructureMineshaftPieces.getNextMineShaftComponent(
                            structureComponent, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, this.coordBaseMode, componentType
                        );
                    } else if (nextInt == 2) {
                        StructureMineshaftPieces.getNextMineShaftComponent(
                            structureComponent, list, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ - 3, 1, componentType
                        );
                    } else {
                        StructureMineshaftPieces.getNextMineShaftComponent(
                            structureComponent, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ - 3, 3, componentType
                        );
                    }
                    break;
                case 1:
                    if (nextInt <= 1) {
                        StructureMineshaftPieces.getNextMineShaftComponent(
                            structureComponent, list, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, this.coordBaseMode, componentType
                        );
                    } else if (nextInt == 2) {
                        StructureMineshaftPieces.getNextMineShaftComponent(
                            structureComponent, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, 2, componentType
                        );
                    } else {
                        StructureMineshaftPieces.getNextMineShaftComponent(
                            structureComponent, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, 0, componentType
                        );
                    }
                    break;
                case 2:
                    if (nextInt <= 1) {
                        StructureMineshaftPieces.getNextMineShaftComponent(
                            structureComponent, list, random, this.boundingBox.minX, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, this.coordBaseMode, componentType
                        );
                    } else if (nextInt == 2) {
                        StructureMineshaftPieces.getNextMineShaftComponent(
                            structureComponent, list, random, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, 1, componentType
                        );
                    } else {
                        StructureMineshaftPieces.getNextMineShaftComponent(
                            structureComponent, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, 3, componentType
                        );
                    }
                    break;
                case 3:
                    if (nextInt <= 1) {
                        StructureMineshaftPieces.getNextMineShaftComponent(
                            structureComponent, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ, this.coordBaseMode, componentType
                        );
                    } else if (nextInt == 2) {
                        StructureMineshaftPieces.getNextMineShaftComponent(
                            structureComponent, list, random, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.minZ - 1, 2, componentType
                        );
                    } else {
                        StructureMineshaftPieces.getNextMineShaftComponent(
                            structureComponent, list, random, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + random.nextInt(3), this.boundingBox.maxZ + 1, 0, componentType
                        );
                    }
            }

            if (componentType < 8) {
                if (this.coordBaseMode != 2 && this.coordBaseMode != 0) {
                    for (int i = this.boundingBox.minX + 3; i + 3 <= this.boundingBox.maxX; i += 5) {
                        int nextInt2 = random.nextInt(5);
                        if (nextInt2 == 0) {
                            StructureMineshaftPieces.getNextMineShaftComponent(structureComponent, list, random, i, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, componentType + 1);
                        } else if (nextInt2 == 1) {
                            StructureMineshaftPieces.getNextMineShaftComponent(structureComponent, list, random, i, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, componentType + 1);
                        }
                    }
                } else {
                    for (int i = this.boundingBox.minZ + 3; i + 3 <= this.boundingBox.maxZ; i += 5) {
                        int nextInt2 = random.nextInt(5);
                        if (nextInt2 == 0) {
                            StructureMineshaftPieces.getNextMineShaftComponent(structureComponent, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, i, 1, componentType + 1);
                        } else if (nextInt2 == 1) {
                            StructureMineshaftPieces.getNextMineShaftComponent(structureComponent, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, i, 3, componentType + 1);
                        }
                    }
                }
            }
        }

        @Override
        public boolean generateStructureChestContents(
            World world, StructureBoundingBox structureBoundingBox, Random random, int n, int n2, int n3, WeightedRandomChestContent[] weightedRandomChestContents, int n4
        ) {
            int xWithOffset = this.getXWithOffset(n, n3);
            int yWithOffset = this.getYWithOffset(n2);
            int zWithOffset = this.getZWithOffset(n, n3);
            if (structureBoundingBox.isVecInside(xWithOffset, yWithOffset, zWithOffset) && world.getBlock(xWithOffset, yWithOffset, zWithOffset).getMaterial() == Material.air) {
                int n5 = random.nextBoolean() ? 1 : 0;
                world.setBlock(xWithOffset, yWithOffset, zWithOffset, Blocks.rail, this.getMetadataWithOffset(Blocks.rail, n5), 2);
                EntityMinecartChest entityMinecartChest = new EntityMinecartChest(world, xWithOffset + 0.5F, yWithOffset + 0.5F, zWithOffset + 0.5F);
                WeightedRandomChestContent.generateChestContents(random, weightedRandomChestContents, entityMinecartChest, n4);
                world.spawnEntityInWorld(entityMinecartChest);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.isLiquidInStructureBoundingBox(world, structureBoundingBox)) {
                return false;
            }

            boolean flag = false;
            boolean flag2 = true;
            boolean flag3 = false;
            boolean flag4 = true;
            int n = this.sectionCount * 5 - 1;
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 0, 2, 1, n, Blocks.air, Blocks.air, false);
            this.randomlyFillWithBlocks(world, structureBoundingBox, random, 0.8F, 0, 2, 0, 2, 2, n, Blocks.air, Blocks.air, false);
            if (this.hasSpiders) {
                this.randomlyFillWithBlocks(world, structureBoundingBox, random, 0.6F, 0, 0, 0, 2, 1, n, Blocks.web, Blocks.air, false);
            }

            for (int i = 0; i < this.sectionCount; i++) {
                int n2 = 2 + i * 5;
                this.fillWithBlocks(world, structureBoundingBox, 0, 0, n2, 0, 1, n2, Blocks.fence, Blocks.air, false);
                this.fillWithBlocks(world, structureBoundingBox, 2, 0, n2, 2, 1, n2, Blocks.fence, Blocks.air, false);
                if (random.nextInt(4) == 0) {
                    this.fillWithBlocks(world, structureBoundingBox, 0, 2, n2, 0, 2, n2, Blocks.planks, Blocks.air, false);
                    this.fillWithBlocks(world, structureBoundingBox, 2, 2, n2, 2, 2, n2, Blocks.planks, Blocks.air, false);
                } else {
                    this.fillWithBlocks(world, structureBoundingBox, 0, 2, n2, 2, 2, n2, Blocks.planks, Blocks.air, false);
                }

                this.handleWorld(world, structureBoundingBox, random, 0.1F, 0, 2, n2 - 1, Blocks.web, 0);
                this.handleWorld(world, structureBoundingBox, random, 0.1F, 2, 2, n2 - 1, Blocks.web, 0);
                this.handleWorld(world, structureBoundingBox, random, 0.1F, 0, 2, n2 + 1, Blocks.web, 0);
                this.handleWorld(world, structureBoundingBox, random, 0.1F, 2, 2, n2 + 1, Blocks.web, 0);
                this.handleWorld(world, structureBoundingBox, random, 0.05F, 0, 2, n2 - 2, Blocks.web, 0);
                this.handleWorld(world, structureBoundingBox, random, 0.05F, 2, 2, n2 - 2, Blocks.web, 0);
                this.handleWorld(world, structureBoundingBox, random, 0.05F, 0, 2, n2 + 2, Blocks.web, 0);
                this.handleWorld(world, structureBoundingBox, random, 0.05F, 2, 2, n2 + 2, Blocks.web, 0);
                this.handleWorld(world, structureBoundingBox, random, 0.05F, 1, 2, n2 - 1, Blocks.torch, 0);
                this.handleWorld(world, structureBoundingBox, random, 0.05F, 1, 2, n2 + 1, Blocks.torch, 0);
                if (random.nextInt(100) == 0) {
                    this.generateStructureChestContents(
                        world,
                        structureBoundingBox,
                        random,
                        2,
                        0,
                        n2 - 1,
                        WeightedRandomChestContent.union(StructureMineshaftPieces.mineshaftChestContents, Items.enchanted_book.getLootTable(random)),
                        3 + random.nextInt(4)
                    );
                }

                if (random.nextInt(100) == 0) {
                    this.generateStructureChestContents(
                        world,
                        structureBoundingBox,
                        random,
                        0,
                        0,
                        n2 + 1,
                        WeightedRandomChestContent.union(StructureMineshaftPieces.mineshaftChestContents, Items.enchanted_book.getLootTable(random)),
                        3 + random.nextInt(4)
                    );
                }

                if (this.hasSpiders && !this.spawnerPlaced) {
                    int yWithOffset = this.getYWithOffset(0);
                    int n3 = n2 - 1 + random.nextInt(3);
                    int xWithOffset = this.getXWithOffset(1, n3);
                    n3 = this.getZWithOffset(1, n3);
                    if (structureBoundingBox.isVecInside(xWithOffset, yWithOffset, n3)) {
                        this.spawnerPlaced = true;
                        world.setBlock(xWithOffset, yWithOffset, n3, Blocks.mob_spawner, 0, 2);
                        TileEntityMobSpawner tileEntity = (TileEntityMobSpawner)world.getTileEntity(xWithOffset, yWithOffset, n3);
                        if (tileEntity != null) {
                            tileEntity.getLogic().setEntityName("CaveSpider");
                        }
                    }
                }
            }

            for (int i = 0; i <= 2; i++) {
                for (int j = 0; j <= n; j++) {
                    byte b = -1;
                    Block blockAtCurrentPosition = this.getBlockAtCurrentPosition(world, i, b, j, structureBoundingBox);
                    if (blockAtCurrentPosition.getMaterial() == Material.air) {
                        byte b2 = -1;
                        this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, i, b2, j, structureBoundingBox);
                    }
                }
            }

            if (this.hasRails) {
                for (int i = 0; i <= n; i++) {
                    Block blockAtCurrentPosition = this.getBlockAtCurrentPosition(world, 1, -1, i, structureBoundingBox);
                    if (blockAtCurrentPosition.getMaterial() != Material.air && blockAtCurrentPosition.isFullBlock()) {
                        this.handleWorld(world, structureBoundingBox, random, 0.7F, 1, 0, i, Blocks.rail, this.getMetadataWithOffset(Blocks.rail, 0));
                    }
                }
            }

            return true;
        }
    }

    public static class Cross extends StructureComponent {
        public int corridorDirection;
        public boolean isMultipleFloors;

        public Cross() {
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            nbttagCompound.setBoolean("tf", this.isMultipleFloors);
            nbttagCompound.setInteger("D", this.corridorDirection);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            this.isMultipleFloors = nbttagCompound.getBoolean("tf");
            this.corridorDirection = nbttagCompound.getInteger("D");
        }

        public Cross(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.corridorDirection = n2;
            this.boundingBox = structureBoundingBox;
            this.isMultipleFloors = structureBoundingBox.getYSize() > 3;
        }

        public static StructureBoundingBox findValidPlacement(List list, Random random, int n, int n2, int n3, int n4) {
            StructureBoundingBox structureBoundingBox = new StructureBoundingBox(n, n2, n3, n, n2 + 2, n3);
            if (random.nextInt(4) == 0) {
                structureBoundingBox.maxY += 4;
            }

            switch (n4) {
                case 0:
                    structureBoundingBox.minX = n - 1;
                    structureBoundingBox.maxX = n + 3;
                    structureBoundingBox.maxZ = n3 + 4;
                    break;
                case 1:
                    structureBoundingBox.minX = n - 4;
                    structureBoundingBox.minZ = n3 - 1;
                    structureBoundingBox.maxZ = n3 + 3;
                    break;
                case 2:
                    structureBoundingBox.minX = n - 1;
                    structureBoundingBox.maxX = n + 3;
                    structureBoundingBox.minZ = n3 - 4;
                    break;
                case 3:
                    structureBoundingBox.maxX = n + 4;
                    structureBoundingBox.minZ = n3 - 1;
                    structureBoundingBox.maxZ = n3 + 3;
            }

            return StructureComponent.findIntersecting(list, structureBoundingBox) != null ? null : structureBoundingBox;
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            int componentType = this.getComponentType();
            switch (this.corridorDirection) {
                case 0:
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, componentType
                    );
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, 1, componentType
                    );
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, 3, componentType
                    );
                    break;
                case 1:
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, componentType
                    );
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, componentType
                    );
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, 1, componentType
                    );
                    break;
                case 2:
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, componentType
                    );
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, 1, componentType
                    );
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, 3, componentType
                    );
                    break;
                case 3:
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, componentType
                    );
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, componentType
                    );
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, 3, componentType
                    );
            }

            if (this.isMultipleFloors) {
                if (random.nextBoolean()) {
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, 2, componentType
                    );
                }

                if (random.nextBoolean()) {
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, 1, componentType
                    );
                }

                if (random.nextBoolean()) {
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, 3, componentType
                    );
                }

                if (random.nextBoolean()) {
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, 0, componentType
                    );
                }
            }
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.isLiquidInStructureBoundingBox(world, structureBoundingBox)) {
                return false;
            }

            if (this.isMultipleFloors) {
                this.fillWithBlocks(
                    world,
                    structureBoundingBox,
                    this.boundingBox.minX + 1,
                    this.boundingBox.minY,
                    this.boundingBox.minZ,
                    this.boundingBox.maxX - 1,
                    this.boundingBox.minY + 3 - 1,
                    this.boundingBox.maxZ,
                    Blocks.air,
                    Blocks.air,
                    false
                );
                this.fillWithBlocks(
                    world,
                    structureBoundingBox,
                    this.boundingBox.minX,
                    this.boundingBox.minY,
                    this.boundingBox.minZ + 1,
                    this.boundingBox.maxX,
                    this.boundingBox.minY + 3 - 1,
                    this.boundingBox.maxZ - 1,
                    Blocks.air,
                    Blocks.air,
                    false
                );
                this.fillWithBlocks(
                    world,
                    structureBoundingBox,
                    this.boundingBox.minX + 1,
                    this.boundingBox.maxY - 2,
                    this.boundingBox.minZ,
                    this.boundingBox.maxX - 1,
                    this.boundingBox.maxY,
                    this.boundingBox.maxZ,
                    Blocks.air,
                    Blocks.air,
                    false
                );
                this.fillWithBlocks(
                    world,
                    structureBoundingBox,
                    this.boundingBox.minX,
                    this.boundingBox.maxY - 2,
                    this.boundingBox.minZ + 1,
                    this.boundingBox.maxX,
                    this.boundingBox.maxY,
                    this.boundingBox.maxZ - 1,
                    Blocks.air,
                    Blocks.air,
                    false
                );
                this.fillWithBlocks(
                    world,
                    structureBoundingBox,
                    this.boundingBox.minX + 1,
                    this.boundingBox.minY + 3,
                    this.boundingBox.minZ + 1,
                    this.boundingBox.maxX - 1,
                    this.boundingBox.minY + 3,
                    this.boundingBox.maxZ - 1,
                    Blocks.air,
                    Blocks.air,
                    false
                );
            } else {
                this.fillWithBlocks(
                    world,
                    structureBoundingBox,
                    this.boundingBox.minX + 1,
                    this.boundingBox.minY,
                    this.boundingBox.minZ,
                    this.boundingBox.maxX - 1,
                    this.boundingBox.maxY,
                    this.boundingBox.maxZ,
                    Blocks.air,
                    Blocks.air,
                    false
                );
                this.fillWithBlocks(
                    world,
                    structureBoundingBox,
                    this.boundingBox.minX,
                    this.boundingBox.minY,
                    this.boundingBox.minZ + 1,
                    this.boundingBox.maxX,
                    this.boundingBox.maxY,
                    this.boundingBox.maxZ - 1,
                    Blocks.air,
                    Blocks.air,
                    false
                );
            }

            this.fillWithBlocks(
                world,
                structureBoundingBox,
                this.boundingBox.minX + 1,
                this.boundingBox.minY,
                this.boundingBox.minZ + 1,
                this.boundingBox.minX + 1,
                this.boundingBox.maxY,
                this.boundingBox.minZ + 1,
                Blocks.planks,
                Blocks.air,
                false
            );
            this.fillWithBlocks(
                world,
                structureBoundingBox,
                this.boundingBox.minX + 1,
                this.boundingBox.minY,
                this.boundingBox.maxZ - 1,
                this.boundingBox.minX + 1,
                this.boundingBox.maxY,
                this.boundingBox.maxZ - 1,
                Blocks.planks,
                Blocks.air,
                false
            );
            this.fillWithBlocks(
                world,
                structureBoundingBox,
                this.boundingBox.maxX - 1,
                this.boundingBox.minY,
                this.boundingBox.minZ + 1,
                this.boundingBox.maxX - 1,
                this.boundingBox.maxY,
                this.boundingBox.minZ + 1,
                Blocks.planks,
                Blocks.air,
                false
            );
            this.fillWithBlocks(
                world,
                structureBoundingBox,
                this.boundingBox.maxX - 1,
                this.boundingBox.minY,
                this.boundingBox.maxZ - 1,
                this.boundingBox.maxX - 1,
                this.boundingBox.maxY,
                this.boundingBox.maxZ - 1,
                Blocks.planks,
                Blocks.air,
                false
            );

            for (int minX = this.boundingBox.minX; minX <= this.boundingBox.maxX; minX++) {
                for (int minZ = this.boundingBox.minZ; minZ <= this.boundingBox.maxZ; minZ++) {
                    if (this.getBlockAtCurrentPosition(world, minX, this.boundingBox.minY - 1, minZ, structureBoundingBox).getMaterial() == Material.air) {
                        this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, minX, this.boundingBox.minY - 1, minZ, structureBoundingBox);
                    }
                }
            }

            return true;
        }
    }

    public static class Room extends StructureComponent {
        public List roomsLinkedToTheRoom = new LinkedList();

        public Room() {
        }

        public Room(int n, Random random, int n2, int n3) {
            super(n);
            this.boundingBox = new StructureBoundingBox(n2, 50, n3, n2 + 7 + random.nextInt(6), 54 + random.nextInt(6), n3 + 7 + random.nextInt(6));
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            int componentType = this.getComponentType();
            int n = this.boundingBox.getYSize() - 3 - 1;
            if (n <= 0) {
                n = 1;
            }

            int n2 = 0;

            while (n2 < this.boundingBox.getXSize()) {
                n2 += random.nextInt(this.boundingBox.getXSize());
                if (n2 + 3 > this.boundingBox.getXSize()) {
                    break;
                }

                StructureComponent nextMineShaftComponent = StructureMineshaftPieces.getNextMineShaftComponent(
                    structureComponent, list, random, this.boundingBox.minX + n2, this.boundingBox.minY + random.nextInt(n) + 1, this.boundingBox.minZ - 1, 2, componentType
                );
                if (nextMineShaftComponent != null) {
                    StructureBoundingBox boundingBox = nextMineShaftComponent.getBoundingBox();
                    this.roomsLinkedToTheRoom
                        .add(new StructureBoundingBox(boundingBox.minX, boundingBox.minY, this.boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, this.boundingBox.minZ + 1));
                }

                n2 += 4;
            }

            n2 = 0;

            while (n2 < this.boundingBox.getXSize()) {
                n2 += random.nextInt(this.boundingBox.getXSize());
                if (n2 + 3 > this.boundingBox.getXSize()) {
                    break;
                }

                StructureComponent nextMineShaftComponent = StructureMineshaftPieces.getNextMineShaftComponent(
                    structureComponent, list, random, this.boundingBox.minX + n2, this.boundingBox.minY + random.nextInt(n) + 1, this.boundingBox.maxZ + 1, 0, componentType
                );
                if (nextMineShaftComponent != null) {
                    StructureBoundingBox boundingBox = nextMineShaftComponent.getBoundingBox();
                    this.roomsLinkedToTheRoom
                        .add(new StructureBoundingBox(boundingBox.minX, boundingBox.minY, this.boundingBox.maxZ - 1, boundingBox.maxX, boundingBox.maxY, this.boundingBox.maxZ));
                }

                n2 += 4;
            }

            n2 = 0;

            while (n2 < this.boundingBox.getZSize()) {
                n2 += random.nextInt(this.boundingBox.getZSize());
                if (n2 + 3 > this.boundingBox.getZSize()) {
                    break;
                }

                StructureComponent nextMineShaftComponent = StructureMineshaftPieces.getNextMineShaftComponent(
                    structureComponent, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + random.nextInt(n) + 1, this.boundingBox.minZ + n2, 1, componentType
                );
                if (nextMineShaftComponent != null) {
                    StructureBoundingBox boundingBox = nextMineShaftComponent.getBoundingBox();
                    this.roomsLinkedToTheRoom
                        .add(new StructureBoundingBox(this.boundingBox.minX, boundingBox.minY, boundingBox.minZ, this.boundingBox.minX + 1, boundingBox.maxY, boundingBox.maxZ));
                }

                n2 += 4;
            }

            n2 = 0;

            while (n2 < this.boundingBox.getZSize()) {
                n2 += random.nextInt(this.boundingBox.getZSize());
                if (n2 + 3 > this.boundingBox.getZSize()) {
                    break;
                }

                StructureComponent nextMineShaftComponent = StructureMineshaftPieces.getNextMineShaftComponent(
                    structureComponent, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + random.nextInt(n) + 1, this.boundingBox.minZ + n2, 3, componentType
                );
                if (nextMineShaftComponent != null) {
                    StructureBoundingBox boundingBox = nextMineShaftComponent.getBoundingBox();
                    this.roomsLinkedToTheRoom
                        .add(new StructureBoundingBox(this.boundingBox.maxX - 1, boundingBox.minY, boundingBox.minZ, this.boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ));
                }

                n2 += 4;
            }
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.isLiquidInStructureBoundingBox(world, structureBoundingBox)) {
                return false;
            }

            this.fillWithBlocks(
                world,
                structureBoundingBox,
                this.boundingBox.minX,
                this.boundingBox.minY,
                this.boundingBox.minZ,
                this.boundingBox.maxX,
                this.boundingBox.minY,
                this.boundingBox.maxZ,
                Blocks.dirt,
                Blocks.air,
                true
            );
            this.fillWithBlocks(
                world,
                structureBoundingBox,
                this.boundingBox.minX,
                this.boundingBox.minY + 1,
                this.boundingBox.minZ,
                this.boundingBox.maxX,
                Math.min(this.boundingBox.minY + 3, this.boundingBox.maxY),
                this.boundingBox.maxZ,
                Blocks.air,
                Blocks.air,
                false
            );

            for (StructureBoundingBox structureBoundingBox2 : (Iterable<StructureBoundingBox>)this.roomsLinkedToTheRoom) {
                this.fillWithBlocks(world, structureBoundingBox, structureBoundingBox2.minX, structureBoundingBox2.maxY - 2, structureBoundingBox2.minZ, structureBoundingBox2.maxX, structureBoundingBox2.maxY, structureBoundingBox2.maxZ, Blocks.air, Blocks.air, false);
            }

            this.handleWorld(
                world,
                structureBoundingBox,
                this.boundingBox.minX,
                this.boundingBox.minY + 4,
                this.boundingBox.minZ,
                this.boundingBox.maxX,
                this.boundingBox.maxY,
                this.boundingBox.maxZ,
                Blocks.air,
                false
            );
            return true;
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
            NBTTagList nbttagList = new NBTTagList();

            for (StructureBoundingBox structureBoundingBox : (Iterable<StructureBoundingBox>)this.roomsLinkedToTheRoom) {
                nbttagList.appendTag(structureBoundingBox.toNbt());
            }

            nbttagCompound.setTag("Entrances", nbttagList);
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
            NBTTagList tagList = nbttagCompound.getTagList("Entrances", 11);

            for (int i = 0; i < tagList.tagCount(); i++) {
                this.roomsLinkedToTheRoom.add(new StructureBoundingBox(tagList.getIntArrayAt(i)));
            }
        }
    }

    public static class Stairs extends StructureComponent {
        public Stairs() {
        }

        public Stairs(int n, Random random, StructureBoundingBox structureBoundingBox, int n2) {
            super(n);
            this.coordBaseMode = n2;
            this.boundingBox = structureBoundingBox;
        }

        @Override
        public void writeStructureToNBT(NBTTagCompound nbttagCompound) {
        }

        @Override
        public void readStructureFromNBT(NBTTagCompound nbttagCompound) {
        }

        public static StructureBoundingBox findValidPlacement(List list, Random random, int n, int n2, int n3, int n4) {
            StructureBoundingBox structureBoundingBox = new StructureBoundingBox(n, n2 - 5, n3, n, n2 + 2, n3);
            switch (n4) {
                case 0:
                    structureBoundingBox.maxX = n + 2;
                    structureBoundingBox.maxZ = n3 + 8;
                    break;
                case 1:
                    structureBoundingBox.minX = n - 8;
                    structureBoundingBox.maxZ = n3 + 2;
                    break;
                case 2:
                    structureBoundingBox.maxX = n + 2;
                    structureBoundingBox.minZ = n3 - 8;
                    break;
                case 3:
                    structureBoundingBox.maxX = n + 8;
                    structureBoundingBox.maxZ = n3 + 2;
            }

            return StructureComponent.findIntersecting(list, structureBoundingBox) != null ? null : structureBoundingBox;
        }

        @Override
        public void buildComponent(StructureComponent structureComponent, List list, Random random) {
            int componentType = this.getComponentType();
            switch (this.coordBaseMode) {
                case 0:
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, componentType
                    );
                    break;
                case 1:
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, 1, componentType
                    );
                    break;
                case 2:
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, componentType
                    );
                    break;
                case 3:
                    StructureMineshaftPieces.getNextMineShaftComponent(
                        structureComponent, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, 3, componentType
                    );
            }
        }

        @Override
        public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
            if (this.isLiquidInStructureBoundingBox(world, structureBoundingBox)) {
                return false;
            }

            this.fillWithBlocks(world, structureBoundingBox, 0, 5, 0, 2, 7, 1, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, structureBoundingBox, 0, 0, 7, 2, 2, 8, Blocks.air, Blocks.air, false);

            for (int i = 0; i < 5; i++) {
                this.fillWithBlocks(world, structureBoundingBox, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, Blocks.air, Blocks.air, false);
            }

            return true;
        }
    }
}
