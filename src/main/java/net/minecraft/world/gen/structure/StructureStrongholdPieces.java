package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneSlab.EnumType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.util.EnumFacing.Plane;
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
   public static List<StructureStrongholdPieces.PieceWeight> structurePieceList;
   public static Class<? extends StructureStrongholdPieces.Stronghold> strongComponentType;
   public static int totalWeight;
   public static StructureStrongholdPieces.Stones strongholdStones = new StructureStrongholdPieces.Stones();

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
      structurePieceList = Lists.newArrayList();

      for (StructureStrongholdPieces.PieceWeight pieceWeight : pieceWeightArray) {
         pieceWeight.instancesSpawned = 0;
         structurePieceList.add(pieceWeight);
      }

      strongComponentType = null;
   }

   public static boolean canAddStructurePieces() {
      boolean flag = false;
      totalWeight = 0;

      for (StructureStrongholdPieces.PieceWeight pieceWeight : structurePieceList) {
         if (pieceWeight.instancesLimit > 0 && pieceWeight.instancesSpawned < pieceWeight.instancesLimit) {
            flag = true;
         }

         totalWeight = totalWeight + pieceWeight.pieceWeight;
      }

      return flag;
   }

   public static StructureStrongholdPieces.Stronghold createPiece(
      Class<? extends StructureStrongholdPieces.Stronghold> clazz,
      List<StructureComponent> list,
      Random random,
      int n,
      int n2,
      int n3,
      EnumFacing enumFacing,
      int n4
   ) {
      StructureStrongholdPieces.Stronghold stronghold = null;
      if (clazz == StructureStrongholdPieces.Straight.class) {
         stronghold = StructureStrongholdPieces.Straight.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (clazz == StructureStrongholdPieces.Prison.class) {
         stronghold = StructureStrongholdPieces.Prison.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (clazz == StructureStrongholdPieces.LeftTurn.class) {
         stronghold = StructureStrongholdPieces.LeftTurn.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (clazz == StructureStrongholdPieces.RightTurn.class) {
         stronghold = StructureStrongholdPieces.RightTurn.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (clazz == StructureStrongholdPieces.RoomCrossing.class) {
         stronghold = StructureStrongholdPieces.RoomCrossing.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (clazz == StructureStrongholdPieces.StairsStraight.class) {
         stronghold = StructureStrongholdPieces.StairsStraight.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (clazz == StructureStrongholdPieces.Stairs.class) {
         stronghold = StructureStrongholdPieces.Stairs.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (clazz == StructureStrongholdPieces.Crossing.class) {
         stronghold = StructureStrongholdPieces.Crossing.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (clazz == StructureStrongholdPieces.ChestCorridor.class) {
         stronghold = StructureStrongholdPieces.ChestCorridor.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (clazz == StructureStrongholdPieces.Library.class) {
         stronghold = StructureStrongholdPieces.Library.create(list, random, n, n2, n3, enumFacing, n4);
      } else if (clazz == StructureStrongholdPieces.PortalRoom.class) {
         stronghold = StructureStrongholdPieces.PortalRoom.create(list, random, n, n2, n3, enumFacing, n4);
      }

      return stronghold;
   }

   public static StructureStrongholdPieces.Stronghold pickPiece(
      StructureStrongholdPieces.Stairs2 stairs2, List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
   ) {
      if (!canAddStructurePieces()) {
         return null;
      }

      if (strongComponentType != null) {
         StructureStrongholdPieces.Stronghold createPiece2 = createPiece(strongComponentType, list, random, n, n2, n3, enumFacing, n4);
         strongComponentType = null;
         if (createPiece2 != null) {
            return createPiece2;
         }
      }

      int n5 = 0;

      while (n5 < 5) {
         n5++;
         int nextInt = random.nextInt(totalWeight);

         for (StructureStrongholdPieces.PieceWeight pieceWeight : structurePieceList) {
            nextInt -= pieceWeight.pieceWeight;
            if (nextInt < 0) {
               if (!pieceWeight.canSpawnMoreStructuresOfType(n4) || pieceWeight == stairs2.strongholdPieceWeight) {
                  break;
               }

               StructureStrongholdPieces.Stronghold createPiece2 = createPiece(pieceWeight.pieceClass, list, random, n, n2, n3, enumFacing, n4);
               if (createPiece2 != null) {
                  pieceWeight.instancesSpawned++;
                  stairs2.strongholdPieceWeight = pieceWeight;
                  if (!pieceWeight.canSpawnMoreStructures()) {
                     structurePieceList.remove(pieceWeight);
                  }

                  return createPiece2;
               }
            }
         }
      }

      StructureBoundingBox structureBoundingBox = StructureStrongholdPieces.Corridor.create(list, random, n, n2, n3, enumFacing);
      return structureBoundingBox != null && structureBoundingBox.minY > 1 ? new StructureStrongholdPieces.Corridor(n4, random, structureBoundingBox, enumFacing) : null;
   }

   public static StructureComponent generate(
      StructureStrongholdPieces.Stairs2 stairs2, List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
   ) {
      if (n4 > 50) {
         return null;
      }

      if (Math.abs(n - stairs2.getBoundingBox().minX) <= 112 && Math.abs(n3 - stairs2.getBoundingBox().minZ) <= 112) {
         StructureStrongholdPieces.Stronghold pickPiece2 = pickPiece(stairs2, list, random, n, n2, n3, enumFacing, n4 + 1);
         if (pickPiece2 != null) {
            list.add(pickPiece2);
            stairs2.pieces.add(pickPiece2);
         }

         return pickPiece2;
      } else {
         return null;
      }
   }

   public static class ChestCorridor extends StructureStrongholdPieces.Stronghold {
      public static List<WeightedRandomChestContent> strongholdChestContents = Lists.newArrayList(
         new WeightedRandomChestContent[]{
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
         }
      );
      public boolean hasMadeChest;

      public ChestCorridor() {
      }

      public ChestCorridor(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.entryDoor = this.getRandomDoor(random);
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Chest", this.hasMadeChest);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.hasMadeChest = tagCompound.getBoolean("Chest");
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
      }

      public static StructureStrongholdPieces.ChestCorridor create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -1, 0, 5, 5, 7, enumFacing);
         return canStrongholdGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureStrongholdPieces.ChestCorridor(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 4, 6, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 1, 0);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 2, 3, 1, 4, Blocks.stonebrick.getDefaultState(), Blocks.stonebrick.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.SMOOTHBRICK.getMetadata()), 3, 1, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.SMOOTHBRICK.getMetadata()), 3, 1, 5, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.SMOOTHBRICK.getMetadata()), 3, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.SMOOTHBRICK.getMetadata()), 3, 2, 4, structureBoundingBoxIn);

         for (int i = 2; i <= 4; i++) {
            this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.SMOOTHBRICK.getMetadata()), 2, 1, i, structureBoundingBoxIn);
         }

         if (!this.hasMadeChest
            && structureBoundingBoxIn.isVecInside(new BlockPos(this.getXWithOffset(3, 3), this.getYWithOffset(2), this.getZWithOffset(3, 3)))) {
            this.hasMadeChest = true;
            this.generateChestContents(
               worldIn,
               structureBoundingBoxIn,
               randomIn,
               3,
               2,
               3,
               WeightedRandomChestContent.combineLootTables(strongholdChestContents, Items.enchanted_book.getRandom(randomIn)),
               2 + randomIn.nextInt(2)
            );
         }

         return true;
      }
   }

   public static class Corridor extends StructureStrongholdPieces.Stronghold {
      public int length;

      public Corridor() {
      }

      public Corridor(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
         this.length = enumFacing != EnumFacing.NORTH && enumFacing != EnumFacing.SOUTH ? structureBoundingBox.getXSize() : structureBoundingBox.getZSize();
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setInteger("Steps", this.length);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.length = tagCompound.getInteger("Steps");
      }

      public static StructureBoundingBox create(List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing) {
         byte b = 3;
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -1, 0, 5, 5, 4, enumFacing);
         StructureComponent findIntersecting = StructureComponent.findIntersecting(list, componentToAddBoundingBox);
         if (findIntersecting == null) {
            return null;
         }

         if (findIntersecting.getBoundingBox().minY == componentToAddBoundingBox.minY) {
            for (int i = 3; i >= 1; i--) {
               componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -1, 0, 5, 5, i - 1, enumFacing);
               if (!findIntersecting.getBoundingBox().intersectsWith(componentToAddBoundingBox)) {
                  return StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -1, 0, 5, 5, i, enumFacing);
               }
            }
         }

         return null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         for (int i = 0; i < this.length; i++) {
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 0, 0, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 0, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 2, 0, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 0, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 4, 0, i, structureBoundingBoxIn);

            for (int j = 1; j <= 3; j++) {
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 0, j, i, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.air.getDefaultState(), 1, j, i, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.air.getDefaultState(), 2, j, i, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.air.getDefaultState(), 3, j, i, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 4, j, i, structureBoundingBoxIn);
            }

            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 0, 4, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 4, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 2, 4, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 4, i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 4, 4, i, structureBoundingBoxIn);
         }

         return true;
      }
   }

   public static class Crossing extends StructureStrongholdPieces.Stronghold {
      public boolean lowerLeftExists;
      public boolean upperLeftExists;
      public boolean lowerRightExists;
      public boolean upperRightExists;

      public Crossing() {
      }

      public Crossing(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.entryDoor = this.getRandomDoor(random);
         this.boundingBox = structureBoundingBox;
         this.lowerLeftExists = random.nextBoolean();
         this.upperLeftExists = random.nextBoolean();
         this.lowerRightExists = random.nextBoolean();
         this.upperRightExists = random.nextInt(3) > 0;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("leftLow", this.lowerLeftExists);
         tagCompound.setBoolean("leftHigh", this.upperLeftExists);
         tagCompound.setBoolean("rightLow", this.lowerRightExists);
         tagCompound.setBoolean("rightHigh", this.upperRightExists);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.lowerLeftExists = tagCompound.getBoolean("leftLow");
         this.upperLeftExists = tagCompound.getBoolean("leftHigh");
         this.lowerRightExists = tagCompound.getBoolean("rightLow");
         this.upperRightExists = tagCompound.getBoolean("rightHigh");
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         int n = 3;
         int n2 = 5;
         if (this.coordBaseMode == EnumFacing.WEST || this.coordBaseMode == EnumFacing.NORTH) {
            n = 8 - n;
            n2 = 8 - n2;
         }

         this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 5, 1);
         if (this.lowerLeftExists) {
            this.getNextComponentX((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, n, 1);
         }

         if (this.upperLeftExists) {
            this.getNextComponentX((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, n2, 7);
         }

         if (this.lowerRightExists) {
            this.getNextComponentZ((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, n, 1);
         }

         if (this.upperRightExists) {
            this.getNextComponentZ((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, n2, 7);
         }
      }

      public static StructureStrongholdPieces.Crossing create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -4, -3, 0, 10, 9, 11, enumFacing);
         return canStrongholdGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureStrongholdPieces.Crossing(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 9, 8, 10, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 4, 3, 0);
         if (this.lowerLeftExists) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 3, 1, 0, 5, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         if (this.lowerRightExists) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 3, 1, 9, 5, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         if (this.upperLeftExists) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 7, 0, 7, 9, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         if (this.upperRightExists) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 5, 7, 9, 7, 9, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 10, 7, 3, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 2, 1, 8, 2, 6, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 5, 4, 4, 9, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 8, 1, 5, 8, 4, 9, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 4, 7, 3, 4, 9, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 3, 5, 3, 3, 6, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 3, 4, 3, 3, 4, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 6, 3, 4, 6, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 5, 1, 7, 7, 1, 8, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 9, 7, 1, 9, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 7, 7, 2, 7, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 5, 7, 4, 5, 9, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 5, 7, 8, 5, 9, Blocks.stone_slab.getDefaultState(), Blocks.stone_slab.getDefaultState(), false);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 5, 5, 7, 7, 5, 9, Blocks.double_stone_slab.getDefaultState(), Blocks.double_stone_slab.getDefaultState(), false
         );
         this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 6, 5, 6, structureBoundingBoxIn);
         return true;
      }
   }

   public static class LeftTurn extends StructureStrongholdPieces.Stronghold {
      public LeftTurn() {
      }

      public LeftTurn(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.entryDoor = this.getRandomDoor(random);
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.EAST) {
            this.getNextComponentZ((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
         } else {
            this.getNextComponentX((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
         }
      }

      public static StructureStrongholdPieces.LeftTurn create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -1, 0, 5, 5, 5, enumFacing);
         return canStrongholdGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureStrongholdPieces.LeftTurn(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 4, 4, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 1, 0);
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.EAST) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 3, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         } else {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         return true;
      }
   }

   public static class Library extends StructureStrongholdPieces.Stronghold {
      public static List<WeightedRandomChestContent> strongholdLibraryChestContents = Lists.newArrayList(
         new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Items.book, 0, 1, 3, 20),
            new WeightedRandomChestContent(Items.paper, 0, 2, 7, 20),
            new WeightedRandomChestContent(Items.map, 0, 1, 1, 1),
            new WeightedRandomChestContent(Items.compass, 0, 1, 1, 1)
         }
      );
      public boolean isLargeRoom;

      public Library() {
      }

      public Library(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.entryDoor = this.getRandomDoor(random);
         this.boundingBox = structureBoundingBox;
         this.isLargeRoom = structureBoundingBox.getYSize() > 6;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Tall", this.isLargeRoom);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.isLargeRoom = tagCompound.getBoolean("Tall");
      }

      public static StructureStrongholdPieces.Library create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -4, -1, 0, 14, 11, 15, enumFacing);
         if (!canStrongholdGoDeeper(componentToAddBoundingBox) || StructureComponent.findIntersecting(list, componentToAddBoundingBox) != null) {
            componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -4, -1, 0, 14, 6, 15, enumFacing);
            if (!canStrongholdGoDeeper(componentToAddBoundingBox) || StructureComponent.findIntersecting(list, componentToAddBoundingBox) != null) {
               return null;
            }
         }

         return new StructureStrongholdPieces.Library(n4, random, componentToAddBoundingBox, enumFacing);
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         int n = 11;
         if (!this.isLargeRoom) {
            n = 6;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 13, n - 1, 14, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 4, 1, 0);
         this.fillWithOutlineUnderSeaLevel(
            worldIn, structureBoundingBoxIn, randomIn, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.web.getDefaultState(), Blocks.web.getDefaultState(), false
         );
         boolean flag = true;
         byte b = 12;

         for (int i = 1; i <= 13; i++) {
            if ((i - 1) % 4 == 0) {
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 1, 1, i, 1, 4, i, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 12, 1, i, 12, 4, i, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false
               );
               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 2, 3, i, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 11, 3, i, structureBoundingBoxIn);
               if (this.isLargeRoom) {
                  this.fillWithBlocks(
                     worldIn, structureBoundingBoxIn, 1, 6, i, 1, 9, i, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false
                  );
                  this.fillWithBlocks(
                     worldIn, structureBoundingBoxIn, 12, 6, i, 12, 9, i, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false
                  );
               }
            } else {
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 1, 1, i, 1, 4, i, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
               );
               this.fillWithBlocks(
                  worldIn, structureBoundingBoxIn, 12, 1, i, 12, 4, i, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
               );
               if (this.isLargeRoom) {
                  this.fillWithBlocks(
                     worldIn, structureBoundingBoxIn, 1, 6, i, 1, 9, i, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
                  );
                  this.fillWithBlocks(
                     worldIn, structureBoundingBoxIn, 12, 6, i, 12, 9, i, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
                  );
               }
            }
         }

         for (int i = 3; i < 12; i += 2) {
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 3, 1, i, 4, 3, i, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 6, 1, i, 7, 3, i, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 9, 1, i, 10, 3, i, Blocks.bookshelf.getDefaultState(), Blocks.bookshelf.getDefaultState(), false
            );
         }

         if (this.isLargeRoom) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 3, 5, 13, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 5, 1, 12, 5, 13, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 5, 1, 9, 5, 2, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 5, 12, 9, 5, 13, Blocks.planks.getDefaultState(), Blocks.planks.getDefaultState(), false);
            this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 9, 5, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 5, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 9, 5, 10, structureBoundingBoxIn);
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 3, 6, 2, 3, 6, 12, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 10, 6, 2, 10, 6, 10, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 4, 6, 2, 9, 6, 2, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 4, 6, 12, 8, 6, 12, Blocks.oak_fence.getDefaultState(), Blocks.oak_fence.getDefaultState(), false
            );
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 9, 6, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 8, 6, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), 9, 6, 10, structureBoundingBoxIn);
            int metadataWithOffset = this.getMetadataWithOffset(Blocks.ladder, 3);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 10, 1, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 10, 2, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 10, 3, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 10, 4, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 10, 5, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 10, 6, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.ladder.getStateFromMeta(metadataWithOffset), 10, 7, 13, structureBoundingBoxIn);
            byte b2 = 7;
            byte b3 = 7;
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2 - 1, 9, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2, 9, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2 - 1, 8, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2, 8, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2 - 1, 7, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2, 7, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2 - 2, 7, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2 + 1, 7, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2 - 1, 7, b3 - 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2 - 1, 7, b3 + 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2, 7, b3 - 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.oak_fence.getDefaultState(), b2, 7, b3 + 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.torch.getDefaultState(), b2 - 2, 8, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.torch.getDefaultState(), b2 + 1, 8, b3, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.torch.getDefaultState(), b2 - 1, 8, b3 - 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.torch.getDefaultState(), b2 - 1, 8, b3 + 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.torch.getDefaultState(), b2, 8, b3 - 1, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.torch.getDefaultState(), b2, 8, b3 + 1, structureBoundingBoxIn);
         }

         this.generateChestContents(
            worldIn,
            structureBoundingBoxIn,
            randomIn,
            3,
            3,
            5,
            WeightedRandomChestContent.combineLootTables(strongholdLibraryChestContents, Items.enchanted_book.getRandom(randomIn, 1, 5, 2)),
            1 + randomIn.nextInt(4)
         );
         if (this.isLargeRoom) {
            this.setBlockState(worldIn, Blocks.air.getDefaultState(), 12, 9, 1, structureBoundingBoxIn);
            this.generateChestContents(
               worldIn,
               structureBoundingBoxIn,
               randomIn,
               12,
               8,
               1,
               WeightedRandomChestContent.combineLootTables(strongholdLibraryChestContents, Items.enchanted_book.getRandom(randomIn, 1, 5, 2)),
               1 + randomIn.nextInt(4)
            );
         }

         return true;
      }
   }

   static class PieceWeight {
      public Class<? extends StructureStrongholdPieces.Stronghold> pieceClass;
      public int pieceWeight;
      public int instancesSpawned;
      public int instancesLimit;

      public PieceWeight(Class<? extends StructureStrongholdPieces.Stronghold> clazz, int n, int n2) {
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

      public PortalRoom(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Mob", this.hasSpawner);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.hasSpawner = tagCompound.getBoolean("Mob");
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         if (componentIn != null) {
            ((StructureStrongholdPieces.Stairs2)componentIn).strongholdPortalRoom = this;
         }
      }

      public static StructureStrongholdPieces.PortalRoom create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -4, -1, 0, 11, 8, 16, enumFacing);
         return canStrongholdGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureStrongholdPieces.PortalRoom(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 10, 7, 15, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, StructureStrongholdPieces.Stronghold.Door.GRATES, 4, 1, 0);
         int n = 6;
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, n, 1, 1, n, 14, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 9, n, 1, 9, n, 14, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, n, 1, 8, n, 2, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 2, n, 14, 8, n, 14, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 2, 1, 4, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 8, 1, 1, 9, 1, 4, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 1, 1, 1, 1, 1, 3, Blocks.flowing_lava.getDefaultState(), Blocks.flowing_lava.getDefaultState(), false
         );
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 9, 1, 1, 9, 1, 3, Blocks.flowing_lava.getDefaultState(), Blocks.flowing_lava.getDefaultState(), false
         );
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 3, 1, 8, 7, 1, 12, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(
            worldIn, structureBoundingBoxIn, 4, 1, 9, 6, 1, 11, Blocks.flowing_lava.getDefaultState(), Blocks.flowing_lava.getDefaultState(), false
         );

         for (int i = 3; i < 14; i += 2) {
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 0, 3, i, 0, 4, i, Blocks.iron_bars.getDefaultState(), Blocks.iron_bars.getDefaultState(), false
            );
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, 10, 3, i, 10, 4, i, Blocks.iron_bars.getDefaultState(), Blocks.iron_bars.getDefaultState(), false
            );
         }

         for (int i = 2; i < 9; i += 2) {
            this.fillWithBlocks(
               worldIn, structureBoundingBoxIn, i, 3, 15, i, 4, 15, Blocks.iron_bars.getDefaultState(), Blocks.iron_bars.getDefaultState(), false
            );
         }

         int metadataWithOffset = this.getMetadataWithOffset(Blocks.stone_brick_stairs, 3);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 5, 6, 1, 7, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 2, 6, 6, 2, 7, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 3, 7, 6, 3, 7, false, randomIn, StructureStrongholdPieces.strongholdStones);

         for (int i = 4; i <= 6; i++) {
            this.setBlockState(worldIn, Blocks.stone_brick_stairs.getStateFromMeta(metadataWithOffset), i, 1, 4, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stone_brick_stairs.getStateFromMeta(metadataWithOffset), i, 2, 5, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stone_brick_stairs.getStateFromMeta(metadataWithOffset), i, 3, 6, structureBoundingBoxIn);
         }

         int horizontalIndex = EnumFacing.NORTH.getHorizontalIndex();
         int horizontalIndex2 = EnumFacing.SOUTH.getHorizontalIndex();
         int horizontalIndex3 = EnumFacing.EAST.getHorizontalIndex();
         int horizontalIndex4 = EnumFacing.WEST.getHorizontalIndex();
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case SOUTH:
                  horizontalIndex = EnumFacing.SOUTH.getHorizontalIndex();
                  horizontalIndex2 = EnumFacing.NORTH.getHorizontalIndex();
                  break;
               case WEST:
                  horizontalIndex = EnumFacing.WEST.getHorizontalIndex();
                  horizontalIndex2 = EnumFacing.EAST.getHorizontalIndex();
                  horizontalIndex3 = EnumFacing.SOUTH.getHorizontalIndex();
                  horizontalIndex4 = EnumFacing.NORTH.getHorizontalIndex();
                  break;
               case EAST:
                  horizontalIndex = EnumFacing.EAST.getHorizontalIndex();
                  horizontalIndex2 = EnumFacing.WEST.getHorizontalIndex();
                  horizontalIndex3 = EnumFacing.SOUTH.getHorizontalIndex();
                  horizontalIndex4 = EnumFacing.NORTH.getHorizontalIndex();
            }
         }

         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            4,
            3,
            8,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            5,
            3,
            8,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            6,
            3,
            8,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex2).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            4,
            3,
            12,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex2).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            5,
            3,
            12,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex2).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            6,
            3,
            12,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex3).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            3,
            3,
            9,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex3).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            3,
            3,
            10,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex3).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            3,
            3,
            11,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex4).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            7,
            3,
            9,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex4).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            7,
            3,
            10,
            structureBoundingBoxIn
         );
         this.setBlockState(
            worldIn,
            Blocks.end_portal_frame.getStateFromMeta(horizontalIndex4).withProperty(BlockEndPortalFrame.EYE, randomIn.nextFloat() > 0.9F),
            7,
            3,
            11,
            structureBoundingBoxIn
         );
         if (!this.hasSpawner) {
            n = this.getYWithOffset(3);
            BlockPos blockPos = new BlockPos(this.getXWithOffset(5, 6), n, this.getZWithOffset(5, 6));
            if (structureBoundingBoxIn.isVecInside(blockPos)) {
               this.hasSpawner = true;
               worldIn.setBlockState(blockPos, Blocks.mob_spawner.getDefaultState(), 2);
               TileEntity tileEntity = worldIn.getTileEntity(blockPos);
               if (tileEntity instanceof TileEntityMobSpawner) {
                  ((TileEntityMobSpawner)tileEntity).getSpawnerBaseLogic().setEntityName("Silverfish");
               }
            }
         }

         return true;
      }
   }

   public static class Prison extends StructureStrongholdPieces.Stronghold {
      public Prison() {
      }

      public Prison(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.entryDoor = this.getRandomDoor(random);
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
      }

      public static StructureStrongholdPieces.Prison create(List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -1, 0, 9, 5, 11, enumFacing);
         return canStrongholdGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureStrongholdPieces.Prison(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 8, 4, 10, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 1, 0);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 10, 3, 3, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 3, 1, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 3, 4, 3, 3, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 7, 4, 3, 7, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 4, 1, 9, 4, 3, 9, false, randomIn, StructureStrongholdPieces.strongholdStones);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 4, 4, 3, 6, Blocks.iron_bars.getDefaultState(), Blocks.iron_bars.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 1, 5, 7, 3, 5, Blocks.iron_bars.getDefaultState(), Blocks.iron_bars.getDefaultState(), false);
         this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), 4, 3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), 4, 3, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.iron_door.getStateFromMeta(this.getMetadataWithOffset(Blocks.iron_door, 3)), 4, 1, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.iron_door.getStateFromMeta(this.getMetadataWithOffset(Blocks.iron_door, 3) + 8), 4, 2, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.iron_door.getStateFromMeta(this.getMetadataWithOffset(Blocks.iron_door, 3)), 4, 1, 8, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.iron_door.getStateFromMeta(this.getMetadataWithOffset(Blocks.iron_door, 3) + 8), 4, 2, 8, structureBoundingBoxIn);
         return true;
      }
   }

   public static class RightTurn extends StructureStrongholdPieces.LeftTurn {
      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.EAST) {
            this.getNextComponentX((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
         } else {
            this.getNextComponentZ((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
         }
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 4, 4, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 1, 0);
         if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.EAST) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         } else {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 3, 3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         return true;
      }
   }

   public static class RoomCrossing extends StructureStrongholdPieces.Stronghold {
      public static List<WeightedRandomChestContent> strongholdRoomCrossingChestContents = Lists.newArrayList(
         new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
            new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5),
            new WeightedRandomChestContent(Items.redstone, 0, 4, 9, 5),
            new WeightedRandomChestContent(Items.coal, 0, 3, 8, 10),
            new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15),
            new WeightedRandomChestContent(Items.apple, 0, 1, 3, 15),
            new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 1)
         }
      );
      public int roomType;

      public RoomCrossing() {
      }

      public RoomCrossing(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.entryDoor = this.getRandomDoor(random);
         this.boundingBox = structureBoundingBox;
         this.roomType = random.nextInt(5);
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setInteger("Type", this.roomType);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.roomType = tagCompound.getInteger("Type");
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 4, 1);
         this.getNextComponentX((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 4);
         this.getNextComponentZ((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 4);
      }

      public static StructureStrongholdPieces.RoomCrossing create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -4, -1, 0, 11, 7, 11, enumFacing);
         return canStrongholdGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureStrongholdPieces.RoomCrossing(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 10, 6, 10, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 4, 1, 0);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 10, 6, 3, 10, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 4, 0, 3, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         this.fillWithBlocks(worldIn, structureBoundingBoxIn, 10, 1, 4, 10, 3, 6, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         switch (this.roomType) {
            case 0:
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 5, 1, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 5, 2, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 5, 3, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 4, 3, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 6, 3, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 5, 3, 4, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 5, 3, 6, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 4, 1, 4, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 4, 1, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 4, 1, 6, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 6, 1, 4, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 6, 1, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 6, 1, 6, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 5, 1, 4, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stone_slab.getDefaultState(), 5, 1, 6, structureBoundingBoxIn);
               break;
            case 1:
               for (int i = 0; i < 5; i++) {
                  this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 1, 3 + i, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 7, 1, 3 + i, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3 + i, 1, 3, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3 + i, 1, 7, structureBoundingBoxIn);
               }

               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 5, 1, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 5, 2, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 5, 3, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.flowing_water.getDefaultState(), 5, 4, 5, structureBoundingBoxIn);
               break;
            case 2:
               for (int i = 1; i <= 9; i++) {
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 1, 3, i, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 9, 3, i, structureBoundingBoxIn);
               }

               for (int i = 1; i <= 9; i++) {
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), i, 3, 1, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), i, 3, 9, structureBoundingBoxIn);
               }

               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 5, 1, 4, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 5, 1, 6, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 5, 3, 4, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 5, 3, 6, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, 1, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 6, 1, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, 3, 5, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 6, 3, 5, structureBoundingBoxIn);

               for (int i = 1; i <= 3; i++) {
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, i, 4, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 6, i, 4, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 4, i, 6, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.cobblestone.getDefaultState(), 6, i, 6, structureBoundingBoxIn);
               }

               this.setBlockState(worldIn, Blocks.torch.getDefaultState(), 5, 3, 5, structureBoundingBoxIn);

               for (int i = 2; i <= 8; i++) {
                  this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 2, 3, i, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 3, 3, i, structureBoundingBoxIn);
                  if (i <= 3 || i >= 7) {
                     this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 4, 3, i, structureBoundingBoxIn);
                     this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 5, 3, i, structureBoundingBoxIn);
                     this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 6, 3, i, structureBoundingBoxIn);
                  }

                  this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 7, 3, i, structureBoundingBoxIn);
                  this.setBlockState(worldIn, Blocks.planks.getDefaultState(), 8, 3, i, structureBoundingBoxIn);
               }

               this.setBlockState(
                  worldIn,
                  Blocks.ladder.getStateFromMeta(this.getMetadataWithOffset(Blocks.ladder, EnumFacing.WEST.getIndex())),
                  9,
                  1,
                  3,
                  structureBoundingBoxIn
               );
               this.setBlockState(
                  worldIn,
                  Blocks.ladder.getStateFromMeta(this.getMetadataWithOffset(Blocks.ladder, EnumFacing.WEST.getIndex())),
                  9,
                  2,
                  3,
                  structureBoundingBoxIn
               );
               this.setBlockState(
                  worldIn,
                  Blocks.ladder.getStateFromMeta(this.getMetadataWithOffset(Blocks.ladder, EnumFacing.WEST.getIndex())),
                  9,
                  3,
                  3,
                  structureBoundingBoxIn
               );
               this.generateChestContents(
                  worldIn,
                  structureBoundingBoxIn,
                  randomIn,
                  3,
                  4,
                  8,
                  WeightedRandomChestContent.combineLootTables(strongholdRoomCrossingChestContents, Items.enchanted_book.getRandom(randomIn)),
                  1 + randomIn.nextInt(4)
               );
         }

         return true;
      }
   }

   public static class Stairs extends StructureStrongholdPieces.Stronghold {
      public boolean isStructureStart;

      public Stairs() {
      }

      public Stairs(int n, Random random, int n2, int n3) {
         super(n);
         this.isStructureStart = true;
         this.coordBaseMode = Plane.HORIZONTAL.random(random);
         this.entryDoor = StructureStrongholdPieces.Stronghold.Door.OPENING;
         switch (this.coordBaseMode) {
            case SOUTH:
            case NORTH:
               this.boundingBox = new StructureBoundingBox(n2, 64, n3, n2 + 5 - 1, 74, n3 + 5 - 1);
               break;
            default:
               this.boundingBox = new StructureBoundingBox(n2, 64, n3, n2 + 5 - 1, 74, n3 + 5 - 1);
         }
      }

      public Stairs(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.isStructureStart = false;
         this.coordBaseMode = enumFacing;
         this.entryDoor = this.getRandomDoor(random);
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Source", this.isStructureStart);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.isStructureStart = tagCompound.getBoolean("Source");
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         if (this.isStructureStart) {
            StructureStrongholdPieces.strongComponentType = StructureStrongholdPieces.Crossing.class;
         }

         this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
      }

      public static StructureStrongholdPieces.Stairs create(List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -7, 0, 5, 11, 5, enumFacing);
         return canStrongholdGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureStrongholdPieces.Stairs(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 10, 4, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 7, 0);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 4);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 2, 6, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 5, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.STONE.getMetadata()), 1, 6, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 5, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.STONE.getMetadata()), 1, 5, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 2, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 3, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.STONE.getMetadata()), 3, 4, 3, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 3, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.STONE.getMetadata()), 3, 3, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 2, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 1, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.STONE.getMetadata()), 1, 2, 1, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 1, 2, structureBoundingBoxIn);
         this.setBlockState(worldIn, Blocks.stone_slab.getStateFromMeta(EnumType.STONE.getMetadata()), 1, 1, 3, structureBoundingBoxIn);
         return true;
      }
   }

   public static class Stairs2 extends StructureStrongholdPieces.Stairs {
      public StructureStrongholdPieces.PieceWeight strongholdPieceWeight;
      public StructureStrongholdPieces.PortalRoom strongholdPortalRoom;
      public List<StructureComponent> pieces = Lists.newArrayList();

      public Stairs2() {
      }

      public Stairs2(int n, Random random, int n2, int n3) {
         super(0, random, n2, n3);
      }

      @Override
      public BlockPos getBoundingBoxCenter() {
         return this.strongholdPortalRoom != null ? this.strongholdPortalRoom.getBoundingBoxCenter() : super.getBoundingBoxCenter();
      }
   }

   public static class StairsStraight extends StructureStrongholdPieces.Stronghold {
      public StairsStraight() {
      }

      public StairsStraight(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.entryDoor = this.getRandomDoor(random);
         this.boundingBox = structureBoundingBox;
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
      }

      public static StructureStrongholdPieces.StairsStraight create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -7, 0, 5, 11, 8, enumFacing);
         return canStrongholdGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureStrongholdPieces.StairsStraight(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 10, 7, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 7, 0);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 7);
         int metadataWithOffset = this.getMetadataWithOffset(Blocks.stone_stairs, 2);

         for (int i = 0; i < 6; i++) {
            this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset), 1, 6 - i, 1 + i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset), 2, 6 - i, 1 + i, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.stone_stairs.getStateFromMeta(metadataWithOffset), 3, 6 - i, 1 + i, structureBoundingBoxIn);
            if (i < 5) {
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 1, 5 - i, 1 + i, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 2, 5 - i, 1 + i, structureBoundingBoxIn);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), 3, 5 - i, 1 + i, structureBoundingBoxIn);
            }
         }

         return true;
      }
   }

   static class Stones extends BlockSelector {
      public Stones() {
      }

      @Override
      public void selectBlocks(Random rand, int x, int y, int z, boolean flag) {
         if (flag) {
            float nextFloat = rand.nextFloat();
            if (nextFloat < 0.2F) {
               this.blockstate = Blocks.stonebrick.getStateFromMeta(BlockStoneBrick.CRACKED_META);
            } else if (nextFloat < 0.5F) {
               this.blockstate = Blocks.stonebrick.getStateFromMeta(BlockStoneBrick.MOSSY_META);
            } else if (nextFloat < 0.55F) {
               this.blockstate = Blocks.monster_egg.getStateFromMeta(net.minecraft.block.BlockSilverfish.EnumType.STONEBRICK.getMetadata());
            } else {
               this.blockstate = Blocks.stonebrick.getDefaultState();
            }
         } else {
            this.blockstate = Blocks.air.getDefaultState();
         }
      }
   }

   public static class Straight extends StructureStrongholdPieces.Stronghold {
      public boolean expandsX;
      public boolean expandsZ;

      public Straight() {
      }

      public Straight(int n, Random random, StructureBoundingBox structureBoundingBox, EnumFacing enumFacing) {
         super(n);
         this.coordBaseMode = enumFacing;
         this.entryDoor = this.getRandomDoor(random);
         this.boundingBox = structureBoundingBox;
         this.expandsX = random.nextInt(2) == 0;
         this.expandsZ = random.nextInt(2) == 0;
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setBoolean("Left", this.expandsX);
         tagCompound.setBoolean("Right", this.expandsZ);
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         super.readStructureFromNBT(tagCompound);
         this.expandsX = tagCompound.getBoolean("Left");
         this.expandsZ = tagCompound.getBoolean("Right");
      }

      @Override
      public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
         this.getNextComponentNormal((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 1);
         if (this.expandsX) {
            this.getNextComponentX((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 2);
         }

         if (this.expandsZ) {
            this.getNextComponentZ((StructureStrongholdPieces.Stairs2)componentIn, listIn, rand, 1, 2);
         }
      }

      public static StructureStrongholdPieces.Straight create(
         List<StructureComponent> list, Random random, int n, int n2, int n3, EnumFacing enumFacing, int n4
      ) {
         StructureBoundingBox componentToAddBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(n, n2, n3, -1, -1, 0, 5, 5, 7, enumFacing);
         return canStrongholdGoDeeper(componentToAddBoundingBox) && StructureComponent.findIntersecting(list, componentToAddBoundingBox) == null
            ? new StructureStrongholdPieces.Straight(n4, random, componentToAddBoundingBox, enumFacing)
            : null;
      }

      @Override
      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn)) {
            return false;
         }

         this.fillWithRandomizedBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 4, 6, true, randomIn, StructureStrongholdPieces.strongholdStones);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, this.entryDoor, 1, 1, 0);
         this.placeDoor(worldIn, randomIn, structureBoundingBoxIn, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
         this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 1, 2, 1, Blocks.torch.getDefaultState());
         this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 3, 2, 1, Blocks.torch.getDefaultState());
         this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 1, 2, 5, Blocks.torch.getDefaultState());
         this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 3, 2, 5, Blocks.torch.getDefaultState());
         if (this.expandsX) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 2, 0, 3, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         if (this.expandsZ) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 2, 4, 3, 4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
         }

         return true;
      }
   }

   abstract static class Stronghold extends StructureComponent {
      public StructureStrongholdPieces.Stronghold.Door entryDoor = StructureStrongholdPieces.Stronghold.Door.OPENING;

      public Stronghold() {
      }

      public Stronghold(int n) {
         super(n);
      }

      @Override
      public void writeStructureToNBT(NBTTagCompound tagCompound) {
         tagCompound.setString("EntryDoor", this.entryDoor.name());
      }

      @Override
      public void readStructureFromNBT(NBTTagCompound tagCompound) {
         this.entryDoor = StructureStrongholdPieces.Stronghold.Door.valueOf(tagCompound.getString("EntryDoor"));
      }

      public void placeDoor(World worldIn, Random random, StructureBoundingBox structureBoundingBox, StructureStrongholdPieces.Stronghold.Door door, int n, int n2, int n3) {
         switch (door) {
            case OPENING:
            default:
               this.fillWithBlocks(
                  worldIn, structureBoundingBox, n, n2, n3, n + 3 - 1, n2 + 3 - 1, n3, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false
               );
               break;
            case WOOD_DOOR:
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), n, n2, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), n, n2 + 1, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), n, n2 + 2, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), n + 1, n2 + 2, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), n + 2, n2 + 2, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), n + 2, n2 + 1, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), n + 2, n2, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.oak_door.getDefaultState(), n + 1, n2, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.oak_door.getStateFromMeta(8), n + 1, n2 + 1, n3, structureBoundingBox);
               break;
            case GRATES:
               this.setBlockState(worldIn, Blocks.air.getDefaultState(), n + 1, n2, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.air.getDefaultState(), n + 1, n2 + 1, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), n, n2, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), n, n2 + 1, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), n, n2 + 2, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), n + 1, n2 + 2, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), n + 2, n2 + 2, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), n + 2, n2 + 1, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.iron_bars.getDefaultState(), n + 2, n2, n3, structureBoundingBox);
               break;
            case IRON_DOOR:
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), n, n2, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), n, n2 + 1, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), n, n2 + 2, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), n + 1, n2 + 2, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), n + 2, n2 + 2, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), n + 2, n2 + 1, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.stonebrick.getDefaultState(), n + 2, n2, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.iron_door.getDefaultState(), n + 1, n2, n3, structureBoundingBox);
               this.setBlockState(worldIn, Blocks.iron_door.getStateFromMeta(8), n + 1, n2 + 1, n3, structureBoundingBox);
               this.setBlockState(
                  worldIn, Blocks.stone_button.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_button, 4)), n + 2, n2 + 1, n3 + 1, structureBoundingBox
               );
               this.setBlockState(
                  worldIn, Blocks.stone_button.getStateFromMeta(this.getMetadataWithOffset(Blocks.stone_button, 3)), n + 2, n2 + 1, n3 - 1, structureBoundingBox
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

      public StructureComponent getNextComponentNormal(StructureStrongholdPieces.Stairs2 stairs2, List<StructureComponent> list, Random random, int n, int n2) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case SOUTH:
                  return StructureStrongholdPieces.generate(
                     stairs2,
                     list,
                     random,
                     this.boundingBox.minX + n,
                     this.boundingBox.minY + n2,
                     this.boundingBox.maxZ + 1,
                     this.coordBaseMode,
                     this.getComponentType()
                  );
               case WEST:
                  return StructureStrongholdPieces.generate(
                     stairs2,
                     list,
                     random,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + n2,
                     this.boundingBox.minZ + n,
                     this.coordBaseMode,
                     this.getComponentType()
                  );
               case EAST:
                  return StructureStrongholdPieces.generate(
                     stairs2,
                     list,
                     random,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + n2,
                     this.boundingBox.minZ + n,
                     this.coordBaseMode,
                     this.getComponentType()
                  );
               case NORTH:
                  return StructureStrongholdPieces.generate(
                     stairs2,
                     list,
                     random,
                     this.boundingBox.minX + n,
                     this.boundingBox.minY + n2,
                     this.boundingBox.minZ - 1,
                     this.coordBaseMode,
                     this.getComponentType()
                  );
            }
         }

         return null;
      }

      public StructureComponent getNextComponentX(StructureStrongholdPieces.Stairs2 stairs2, List<StructureComponent> list, Random random, int n, int n2) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case SOUTH:
                  return StructureStrongholdPieces.generate(
                     stairs2,
                     list,
                     random,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + n,
                     this.boundingBox.minZ + n2,
                     EnumFacing.WEST,
                     this.getComponentType()
                  );
               case WEST:
                  return StructureStrongholdPieces.generate(
                     stairs2,
                     list,
                     random,
                     this.boundingBox.minX + n2,
                     this.boundingBox.minY + n,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType()
                  );
               case EAST:
                  return StructureStrongholdPieces.generate(
                     stairs2,
                     list,
                     random,
                     this.boundingBox.minX + n2,
                     this.boundingBox.minY + n,
                     this.boundingBox.minZ - 1,
                     EnumFacing.NORTH,
                     this.getComponentType()
                  );
               case NORTH:
                  return StructureStrongholdPieces.generate(
                     stairs2,
                     list,
                     random,
                     this.boundingBox.minX - 1,
                     this.boundingBox.minY + n,
                     this.boundingBox.minZ + n2,
                     EnumFacing.WEST,
                     this.getComponentType()
                  );
            }
         }

         return null;
      }

      public StructureComponent getNextComponentZ(StructureStrongholdPieces.Stairs2 stairs2, List<StructureComponent> list, Random random, int n, int n2) {
         if (this.coordBaseMode != null) {
            switch (this.coordBaseMode) {
               case SOUTH:
                  return StructureStrongholdPieces.generate(
                     stairs2,
                     list,
                     random,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + n,
                     this.boundingBox.minZ + n2,
                     EnumFacing.EAST,
                     this.getComponentType()
                  );
               case WEST:
                  return StructureStrongholdPieces.generate(
                     stairs2,
                     list,
                     random,
                     this.boundingBox.minX + n2,
                     this.boundingBox.minY + n,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType()
                  );
               case EAST:
                  return StructureStrongholdPieces.generate(
                     stairs2,
                     list,
                     random,
                     this.boundingBox.minX + n2,
                     this.boundingBox.minY + n,
                     this.boundingBox.maxZ + 1,
                     EnumFacing.SOUTH,
                     this.getComponentType()
                  );
               case NORTH:
                  return StructureStrongholdPieces.generate(
                     stairs2,
                     list,
                     random,
                     this.boundingBox.maxX + 1,
                     this.boundingBox.minY + n,
                     this.boundingBox.minZ + n2,
                     EnumFacing.EAST,
                     this.getComponentType()
                  );
            }
         }

         return null;
      }

      public static boolean canStrongholdGoDeeper(StructureBoundingBox structureBoundingBox) {
         return structureBoundingBox != null && structureBoundingBox.minY > 10;
      }

      public enum Door {
         OPENING,
         WOOD_DOOR,
         GRATES,
         IRON_DOOR;
      }
   }
}
